# TongRDS（Redis）缓存策略设计文档

## 一、技术选型

| 项目 | 说明 |
|------|------|
| 缓存中间件 | TongRDS V2.2（100% 兼容 Redis 协议） |
| 客户端 | Spring Boot Starter Data Redis（Lettuce） |
| 序列化 | Key: StringRedisSerializer / Value: GenericJackson2JsonRedisSerializer |
| 降级策略 | 所有读操作 try-catch，Redis 不可用时自动回退到 MySQL 查询 |

---

## 二、Redis Key 设计总览

| 用途 | Key 格式 | 数据类型 | TTL | 说明 |
|------|---------|---------|-----|------|
| 用户会话信息 | `session:{userId}` | String（JSON） | 24 小时 | 缓存登录态，减少每次请求查库 |
| 活动报名配额 | `event:quota:{eventId}` | String（int） | 24 小时 | DECR 原子操作防超卖 |
| 活动剩余名额 | `event:slots:{eventId}` | String（int） | 10 秒 | 展示用，短 TTL 保证近实时 |
| 报名防重锁 | `event:lock:{eventId}:{userId}` | String | 5 秒 | SETNX 防止用户重复提交 |
| 社团每日浏览量 | `club:views:YYYYMMDD` | ZSET | 8 天 | member=clubId, score=浏览次数 |
| 热门社团 TOP20 | `club:hot:top20` | String（JSON） | 1 小时 | 缓存计算结果，避免重复聚合 |
| 经费审批阈值 | `config:approval:expense-threshold` | String | 无 TTL | 可动态配置，替代硬编码 |
| 社团审批阶段 | `config:approval:club-stages` | String（JSON） | 无 TTL | 可动态配置审批流程节点 |

---

## 三、缓存策略详细设计

### 3.1 用户会话信息缓存（Cache-Aside 模式）

**目标**：每个 HTTP 请求经过 AuthInterceptor 时，原本都要查库获取用户会话信息（角色、状态等），缓存后减少约 90% 的数据库查询。

**缓存流程**：

```
请求进入 AuthInterceptor
    ↓
从 Redis 读取 session:{userId}
    ↓ 命中 → 直接使用缓存数据
    ↓ 未命中 → 查询 MySQL → 写入 Redis（TTL 24h）
    ↓
校验用户状态、设置 ThreadLocal
```

**失效策略（主动淘汰）**：

- 修改用户状态（冻结 / 解冻） → `evictSessionInfo(userId)`
- 修改用户角色 → `evictSessionInfo(userId)`
- 社团审批通过（授予管理员角色） → `evictSessionInfo(userId)`
- 社团注销（降级为学生） → `evictSessionInfo(userId)`

**涉及文件**：

- `config/AuthInterceptor.java` — 读取缓存
- `serviceImpl/SchoolAdminPermissionServiceImpl.java` — 失效淘汰

---

### 3.2 活动报名防超卖（Redis 原子操作）

**目标**：解决高并发场景下"先查后写"的竞态条件，确保限额 N 人的活动不会出现 N+1 人报名成功。

**原理**：利用 Redis `DECR` 命令的原子性，单线程执行扣减，天然避免并发冲突。

**报名流程**：

```
用户点击报名
    ↓
SETNX event:lock:{eventId}:{userId} （防重复提交，5s 过期）
    ↓ 获取失败 → 返回"请勿重复提交"
    ↓ 获取成功 ↓
校验活动状态、时间窗口、会员限制、重复报名
    ↓
DECR event:quota:{eventId}
    ↓ 结果 >= 0 → 名额扣减成功，继续 DB 写入
    ↓ 结果 < 0  → INCR 回滚，返回"名额已满"
    ↓
DB 写入报名记录
    ↓ 成功 → 删除 event:slots 展示缓存
    ↓ 失败（如唯一键冲突） → INCR 回滚配额
    ↓
finally: 释放防重锁
```

**取消报名流程**：

```
DB 更新报名状态为"已取消"
    ↓
INCR event:quota:{eventId} （归还名额）
    ↓
DELETE event:slots:{eventId} （清除展示缓存）
```

**配额初始化时机**：

- 学校管理员审批活动通过（状态变为 3-报名中）时：`SET event:quota:{eventId} = limitCount - currentSignups`
- 报名时若 Redis 中无配额 key：从 DB 计算当前已报名人数后初始化

**Redis 不可用降级**：

- 捕获异常后回退到原有的 `countActiveSignups` DB 查询方式
- 接受极端故障下的微量超卖风险，保证系统可用性优先

**涉及文件**：

- `serviceImpl/StudentPermissionServiceImpl.java` — signupEvent / cancelEventSignup
- `serviceImpl/SchoolAdminPermissionServiceImpl.java` — decisionEventApproval（初始化配额）

---

### 3.3 活动剩余名额展示缓存

**目标**：活动列表页展示"剩余 XX 名额"，高频访问场景下避免每次请求都执行 `COUNT` 查询。

**策略**：

- 首次请求从 DB 计算：`remaining = limitCount - countActiveSignups`
- 写入 Redis，**TTL 10 秒**
- 10 秒内的后续请求直接读缓存
- 报名/取消报名成功后主动删除缓存，下次请求重新计算

**为什么是 10 秒**：

- 太短（1-2s）：缓存命中率低，效果不明显
- 太长（60s+）：用户看到的名额与实际偏差过大
- 10 秒是展示精度与性能的平衡点

**注意**：此缓存仅用于展示，实际报名是否成功由 `event:quota` 的原子 DECR 决定。

**涉及文件**：

- `serviceImpl/StudentPermissionServiceImpl.java` — listOpenEvents

---

### 3.4 热门社团 TOP20 缓存

**目标**：展示近 7 天浏览量最高的 20 个社团，减少复杂聚合查询。

**浏览量追踪**：

```
用户访问社团详情页 → POST /api/clubs/{clubId}/view
    ↓
ZINCRBY club:views:20260411 1 {clubId}
（按天分 ZSET，每个 ZSET TTL 8 天自动过期）
```

**TOP20 计算流程**：

```
GET /api/clubs/hot
    ↓
查缓存 club:hot:top20
    ↓ 命中 → 直接返回
    ↓ 未命中 ↓
ZUNIONSTORE 合并最近 7 天的 ZSET
    ↓
ZREVRANGE 0 19 取 TOP20 的 clubId
    ↓
批量查询 DB 获取社团详细信息
    ↓
缓存结果到 club:hot:top20（TTL 1 小时）
    ↓
返回
```

**为什么按天分 ZSET**：

- 自然支持 7 天滑动窗口，无需手动清理历史数据
- 每个 ZSET 设置 8 天 TTL，Redis 自动回收
- `ZUNIONSTORE` 合并多天数据，计算简单高效

**涉及文件**：

- `controller/StudentController.java` — trackClubView / listHotClubs 端点
- `serviceImpl/StudentPermissionServiceImpl.java` — 业务实现

---

### 3.5 审批流程配置缓存

**目标**：将硬编码的审批参数（经费阈值 500 元、3 阶段审批节点）改为 Redis 存储的可配置项，支持运行时动态调整，无需重启服务。

#### 3.5.1 经费审批阈值

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `config:approval:expense-threshold` | `500` | 低于此金额自动通过，高于此金额需学校审批 |

**读取逻辑**：

```java
private BigDecimal getAutoApproveThreshold() {
    String value = cacheService.getConfig("config:approval:expense-threshold");
    return value != null ? new BigDecimal(value) : new BigDecimal("500");
}
```

#### 3.5.2 社团审批阶段

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `config:approval:club-stages` | `[{"from":1,"to":2},{"from":2,"to":3},{"from":3,"to":4}]` | 材料审核→答辩审核→公示审核→通过 |

**管理端 API**：

```
GET  /api/school-admin/config/expense-threshold       查看经费阈值
PUT  /api/school-admin/config/expense-threshold       修改经费阈值
GET  /api/school-admin/config/club-approval-stages    查看审批阶段
PUT  /api/school-admin/config/club-approval-stages    修改审批阶段
```

**涉及文件**：

- `serviceImpl/ClubAdminPermissionServiceImpl.java` — 读取经费阈值
- `serviceImpl/SchoolAdminPermissionServiceImpl.java` — 读取审批阶段 + 配置管理
- `controller/SchoolAdminController.java` — 配置管理端点

---

## 四、容灾与降级

| 场景 | 降级策略 |
|------|---------|
| Redis 连接超时 | CacheService 所有读方法返回 null，业务层回退到 MySQL 查询 |
| Redis 宕机 | 系统自动降级为纯 DB 模式，功能不受影响，仅性能下降 |
| 报名配额 Redis 不可用 | 回退到 `countActiveSignups` DB 查询，接受微量超卖风险 |
| 缓存数据不一致 | 会话缓存通过主动淘汰保证一致性；名额缓存 10s 自动过期 |

**实现方式**：所有 CacheService 读操作统一 try-catch，异常时 log.warn 并返回 null。

---

## 五、连接池配置

```properties
spring.data.redis.host=127.0.0.1
spring.data.redis.port=6379
spring.data.redis.password=
spring.data.redis.database=0
spring.data.redis.timeout=3000ms
spring.data.redis.lettuce.pool.max-active=16
spring.data.redis.lettuce.pool.max-idle=8
spring.data.redis.lettuce.pool.min-idle=2
```

| 参数 | 值 | 说明 |
|------|-----|------|
| max-active | 16 | 最大并发连接数，满足中等并发场景 |
| max-idle | 8 | 空闲时保留的连接数，减少连接创建开销 |
| min-idle | 2 | 最少保持 2 个连接，避免冷启动延迟 |
| timeout | 3000ms | 连接超时 3 秒，超时后降级到 DB |

---

## 六、预期性能提升

| 场景 | 优化前 | 优化后 | 提升 |
|------|--------|--------|------|
| 每次请求认证（AuthInterceptor） | 查 MySQL ~5ms | 查 Redis ~0.5ms | ~10x |
| 社团列表页加载 | 复杂 SQL ~200ms | Redis 缓存 ~5ms | ~40x |
| 活动报名名额查询 | COUNT 查询 ~10ms/次 | 10s 缓存 ~0.5ms | ~20x |
| 高并发报名（100人限额） | 可能 101+ 人成功 | 精确 100 人 | 数据一致 |
| 审批页面加载（阈值/阶段） | 硬编码，改需重启 | Redis 读取 ~0.5ms | 动态配置 |

---

## 七、文件变更清单

### 新建文件

| 文件 | 说明 |
|------|------|
| `config/RedisConfig.java` | RedisTemplate Bean 配置（Jackson 序列化 + JavaTimeModule） |
| `service/CacheService.java` | 统一缓存操作封装（会话、配额、浏览量、配置等） |

### 修改文件

| 文件 | 改动 |
|------|------|
| `pom.xml` | 新增 spring-boot-starter-data-redis 依赖 |
| `application.properties` | 新增 spring.data.redis.* 连接配置 |
| `config/AuthInterceptor.java` | 会话信息优先从 Redis 读取，未命中再查 DB |
| `controller/StudentController.java` | 新增浏览量追踪、热门社团查询端点 |
| `controller/SchoolAdminController.java` | 新增审批配置管理端点 |
| `service/StudentPermissionService.java` | 接口新增 trackClubView、listHotClubs |
| `service/SchoolAdminPermissionService.java` | 接口新增配置管理方法 |
| `serviceImpl/StudentPermissionServiceImpl.java` | 防超卖报名、名额缓存、热门社团 |
| `serviceImpl/SchoolAdminPermissionServiceImpl.java` | 会话失效、配额初始化、可配置审批 |
| `serviceImpl/ClubAdminPermissionServiceImpl.java` | 经费阈值从 Redis 动态读取 |
| `mapper/UserPermissionMapper.java` + XML | 新增 findEventLimitCount、listClubsByIds |
