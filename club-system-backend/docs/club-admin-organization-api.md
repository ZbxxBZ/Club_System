# 社团管理员组织架构与成员维护 API 文档

## 1. 文档目标
用于后端实现社团管理员端"组织架构维护"能力，支持：
- 岗位层级维护（新增 / 编辑 / 删除）
- 成员维护（添加 / 移除 / 调整职位）
- 与前端社团管理员页面直接联调

对应前端页面：
- src/views/dashboard/club-admin/ClubAdminDashboardView.vue

对应前端接口封装：
- src/api/user-permission.js

## 2. 鉴权与权限
- 鉴权方式：Authorization: Bearer <token>
- 角色要求：仅 CLUB_ADMIN
- 数据范围：仅当前管理员绑定社团（user_role.club_id）

## 3. 统一返回结构
```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

约定：
- code = 0 表示成功
- code != 0 表示业务失败

## 4. 接口总览
1. GET /api/club-admin/me/positions
2. POST /api/club-admin/me/positions
3. PATCH /api/club-admin/me/positions/{positionId}
4. DELETE /api/club-admin/me/positions/{positionId}
5. GET /api/club-admin/me/members
6. POST /api/club-admin/me/members
7. PATCH /api/club-admin/me/members/{memberId}/role
8. DELETE /api/club-admin/me/members/{memberId}

---

## 5. 岗位层级维护（club_position）

### 5.1 查询岗位列表
- 方法：GET
- 路径：/api/club-admin/me/positions

Query 参数：
- pageNum: number，可选，默认 1
- pageSize: number，可选，默认 200

成功响应示例：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 91001,
        "clubId": 20001,
        "positionName": "社团管理员",
        "parentPositionId": null,
        "levelNo": 1,
        "sortNo": 1,
        "createdAt": "2026-03-17T10:00:00+08:00"
      },
      {
        "id": 91002,
        "clubId": 20001,
        "positionName": "部长",
        "parentPositionId": 91001,
        "levelNo": 2,
        "sortNo": 10,
        "createdAt": "2026-03-17T10:01:00+08:00"
      },
      {
        "id": 91003,
        "clubId": 20001,
        "positionName": "干事",
        "parentPositionId": 91002,
        "levelNo": 3,
        "sortNo": 20,
        "createdAt": "2026-03-17T10:02:00+08:00"
      }
    ],
    "total": 3,
    "pageNum": 1,
    "pageSize": 200
  }
}
```

### 5.2 新增岗位
- 方法：POST
- 路径：/api/club-admin/me/positions

请求体示例：
```json
{
  "positionName": "部长",
  "parentPositionId": 91001,
  "levelNo": 2
}
```

字段约束：
- positionName：必填，1~50
- parentPositionId：可空，空表示顶级岗位
- levelNo：必填，正整数；建议后端按 parent 自动计算并覆盖传入值
- sortNo：由后端自动生成（同父级下建议按 max(sort_no)+10 分配），前端不手动输入

成功响应示例：
```json
{
  "code": 0,
  "message": "岗位创建成功",
  "data": {
    "id": 91002,
    "clubId": 20001,
    "positionName": "部长",
    "parentPositionId": 91001,
    "levelNo": 2,
    "sortNo": 10
  }
}
```

### 5.3 编辑岗位
- 方法：PATCH
- 路径：/api/club-admin/me/positions/{positionId}

请求体示例：
```json
{
  "positionName": "执行部长",
  "parentPositionId": 91001,
  "levelNo": 2
}
```

说明：
- 允许改名、改父级。
- 若改父级，需校验不能形成循环引用。
- 本接口默认不要求前端传 sortNo；如未传，后端保持原排序或按规则重排。

### 5.4 删除岗位
- 方法：DELETE
- 路径：/api/club-admin/me/positions/{positionId}

删除前校验：
1. 有子岗位时禁止删除
2. 被成员占用时禁止删除（club_member.position_id 存在引用）

成功响应示例：
```json
{
  "code": 0,
  "message": "岗位删除成功",
  "data": {
    "positionId": 91003
  }
}
```

---

## 6. 成员维护（club_member）

### 6.1 查询成员列表
- 方法：GET
- 路径：/api/club-admin/me/members

Query 参数：
- pageNum: number，可选，默认 1
- pageSize: number，可选，默认 10

成功响应示例：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 82001,
        "clubId": 20001,
        "userId": 10001,
        "username": "20230001",
        "realName": "张三",
        "positionId": 91002,
        "positionName": "部长",
        "memberStatus": 1,
        "joinAt": "2026-03-01T09:00:00+08:00"
      }
    ],
    "total": 1,
    "pageNum": 1,
    "pageSize": 10
  }
}
```

### 6.2 添加成员
- 方法：POST
- 路径：/api/club-admin/me/members

请求体示例：
```json
{
  "userId": 10003,
  "positionId": 91003,
  "positionName": "干事"
}
```

约束：
- userId 必填
- positionId 可选
- joinAt 建议由后端自动写入当前时间（club_member.join_at 为 NOT NULL）
- 同一社团同一用户受 `uk_club_user(club_id, user_id)` 约束，不可直接重复插入
- 若历史记录已存在且成员已退出，建议"复用原记录重新入社"而非新增记录

### 6.3 调整成员职位
- 方法：PATCH
- 路径：/api/club-admin/me/members/{memberId}/role

请求体示例：
```json
{
  "positionId": 91002,
  "positionName": "部长"
}
```

清空职位示例：
```json
{
  "positionId": null,
  "positionName": ""
}
```

### 6.4 移除成员
- 方法：DELETE
- 路径：/api/club-admin/me/members/{memberId}

处理建议：
- 逻辑移除：member_status = 2，quit_at = 当前时间
- 保留历史数据，不建议物理删除

---

## 7. 数据库映射

岗位表（club_position）：
- id -> positionId
- club_id -> clubId
- position_name -> positionName
- parent_position_id -> parentPositionId
- level_no -> levelNo
- sort_no -> sortNo

成员表（club_member）：
- id -> memberId
- club_id -> clubId
- user_id -> userId
- position_id -> positionId
- member_status -> memberStatus
- join_at -> joinAt
- quit_at -> quitAt

---

## 8. 关键业务规则
1. 岗位树必须无环
2. 删除岗位前必须无子岗位、无成员引用
3. 成员职位变更时 position_id 与 position_name 需一致
4. 仅允许操作当前管理员所属社团数据
5. 本模块与社团招新字段（如 club.recruit_status）解耦，不应在这些接口中读写招新状态

---

## 9. 错误码建议
- 1001：参数错误
- 1002：未登录或 token 失效
- 1003：无权限或越权
- 1005：资源不存在
- 1006：业务校验失败（岗位有下级、岗位被占用、重复成员等）
- 1099：系统异常

---

## 10. 审计与日志约束
- 组织架构维护、成员维护属于非审批类操作。
- 禁止写入 audit_log。
- 建议写入独立操作日志表（如 club_position_op_log、club_member_op_log）或统一操作日志系统。

---

## 11. 联调验收清单
1. 岗位可新增、编辑、删除
2. 岗位层级与排序可正确返回
3. 删除岗位时子岗位/成员占用拦截正确
4. 成员可添加、移除、调整职位
5. 成员职位调整后列表可立即反映
6. 仅 CLUB_ADMIN 且仅本社团可操作
7. 本模块操作不写 audit_log
