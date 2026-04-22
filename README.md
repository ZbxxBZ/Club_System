# 校园社团数字化管理平台

基于 Spring Boot + Vue 3 的校园社团全生命周期管理系统，支持学生、社团管理员、学校管理员三种角色，覆盖社团申报审批、成员管理、活动管理、经费管理、数据统计等核心业务。

## 技术栈

### 后端
- Java 17 + Spring Boot 3.5
- MyBatis + MySQL 8.0
- JWT 认证 (JJWT)
- TongRDS / Redis 缓存
- 腾讯云 COS 文件存储

### 前端
- Vue 3 (Composition API) + Vite
- Pinia 状态管理
- Vue Router 路由守卫
- Element Plus UI
- Axios + ECharts

## 项目结构

```
├── club-system-backend/       # 后端 Spring Boot 项目
│   ├── src/main/java/         # Java 源码
│   │   ├── config/            # 拦截器、Redis、JWT、定时任务等配置
│   │   ├── controller/        # REST 控制器 (Auth/Student/ClubAdmin/SchoolAdmin)
│   │   ├── service/           # 业务接口
│   │   ├── serviceImpl/       # 业务实现
│   │   ├── mapper/            # MyBatis Mapper
│   │   ├── entity/            # 实体、请求DTO、响应DTO
│   │   └── util/              # JWT、权限、ID生成器等工具
│   ├── src/main/resources/
│   │   ├── mapper/            # MyBatis XML
│   │   └── application.properties
│   └── docs/                  # 后端文档
├── club-system-frontend/      # 前端 Vue 3 项目
│   ├── src/
│   │   ├── api/               # Axios 请求封装 (116个接口)
│   │   ├── stores/            # Pinia 状态管理
│   │   ├── router/            # 路由 + 导航守卫
│   │   ├── utils/             # Token、角色权限工具
│   │   └── views/             # 页面 (student/club-admin/school-admin)
│   └── docs/                  # 前端文档
└── docs/                      # 系统架构文档
```

## 功能模块

### 学生端
- 社团广场浏览、搜索、分类筛选
- 申请加入社团 / 申请创建新社团
- 活动报名、取消报名、签到
- 热门社团 TOP20（Redis 缓存）
- 个人信息管理

### 社团管理员端
- 社团信息维护、招新配置
- 组织架构管理（树形职位 + 成员）
- 入社申请审批
- 活动创建、签到管理、活动总结
- 经费管理（收入录入、支出申请、台账明细）
- 社团注销申请

### 学校管理员端
- 用户管理（状态冻结/解冻、角色变更）
- 社团审批（3 阶段流程：材料审核 → 答辩 → 公示）
- 社团注销审批（3 阶段流程）
- 活动审批（通过/驳回）
- 经费审批（>500元需学校审核，阈值可配置）
- 数据统计（社团/活动/财务，ECharts 图表）
- 定时任务管理（毕业生退社、账号冻结、活动状态推进）
- 系统配置（审批阈值、审批阶段动态调整）

## 缓存策略（TongRDS / Redis）

| 功能 | Key | TTL | 说明 |
|------|-----|-----|------|
| 用户会话 | `session:{userId}` | 24h | 减少每次请求查库 |
| 报名防超卖 | `event:quota:{eventId}` | 24h | DECR 原子操作保证名额精确 |
| 剩余名额 | `event:slots:{eventId}` | 10s | 活动列表展示用 |
| 防重复提交 | `event:lock:{eventId}:{userId}` | 5s | SETNX 分布式锁 |
| 社团浏览量 | `club:views:YYYYMMDD` | 8天 | ZSET 按天统计 |
| 热门社团 | `club:hot:top20` | 1h | 近7天 TOP20 |
| 审批配置 | `config:approval:*` | 永久 | 动态可调 |

## 快速开始

### 环境要求
- JDK 17+
- MySQL 8.0+
- Redis 7+ / TongRDS
- Node.js 18+

### 后端启动

```bash
cd club-system-backend

# 创建本地配置文件（填入真实密码和密钥）
cp src/main/resources/application-local.properties.example src/main/resources/application-local.properties

# 导入数据库
mysql -u root -p club_system < docs/test-data-insert.sql

# 启动
./mvnw spring-boot:run
```

`application-local.properties` 需配置：
```properties
spring.datasource.password=你的数据库密码
app.jwt.secret=你的JWT密钥
cos.secret-id=你的COS密钥ID
cos.secret-key=你的COS密钥
cos.bucket=你的COS桶名
cos.domain=你的COS域名
```

### 前端启动

```bash
cd club-system-frontend
npm install
npm run dev
```

访问 http://localhost:5173

### 测试账号

| 账号 | 角色 | 说明 |
|------|------|------|
| admin | 学校管理员 | 全局管理 |
| cadmin2 | 社团管理员 | 篮球社 |
| cadmin3 | 社团管理员 | 编程社 |
| student01 ~ student08 | 学生 | 分布在不同社团 |

## 文档

- [认证与权限模块](docs/01-认证与权限模块.md)
- [学生端模块](docs/02-学生端模块.md)
- [社团管理员模块](docs/03-社团管理员模块.md)
- [学校管理员模块](docs/04-学校管理员模块.md)
- [缓存与定时任务模块](docs/05-缓存与定时任务模块.md)
- [前端架构模块](docs/06-前端架构模块.md)
- [Redis 缓存策略](club-system-backend/docs/redis-cache-strategy.md)
- [测试数据说明](club-system-backend/docs/test-data-description.md)
- [数据库设计](club-system-frontend/docs/database-design.md)
