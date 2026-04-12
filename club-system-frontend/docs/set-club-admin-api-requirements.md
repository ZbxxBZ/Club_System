# 设为社团管理员功能后端对接文档

## 1. 目标
用于对齐“学校管理员将用户设为社团管理员”的联调需求，解决前端点击后角色不更新、刷新后仍显示学生等问题。

前端对应页面：
- `src/views/dashboard/school-admin/SchoolAdminDashboardView.vue`

前端接口封装：
- `src/api/user-permission.js`
- `updateUserRoleApi(userId, payload)`

## 2. 接口定义

### 2.1 调整用户角色
- `PATCH /api/school-admin/users/{userId}/role`

Request Body：
```json
{
  "roleCode": "CLUB_ADMIN"
}
```

取消社团管理员 Request Body：
```json
{
  "roleCode": "STUDENT"
}
```

说明：
- `roleCode` 必须支持：`STUDENT`、`CLUB_ADMIN`、`SCHOOL_ADMIN`
- 本功能需要支持双向切换：
  - 设置社团管理员：`STUDENT -> CLUB_ADMIN`
  - 取消社团管理员：`CLUB_ADMIN -> STUDENT`

前端按钮交互约定：
- 用户当前非社团管理员：按钮灰色，文案“设为社团管理员”。
- 用户当前是社团管理员：按钮绿色，文案“取消社团管理员”。
- 点击后按目标角色发请求，成功后刷新用户列表。

## 3. 返回规范
统一返回：
```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

建议成功响应：
```json
{
  "code": 0,
  "message": "角色调整成功",
  "data": {
    "userId": 1001,
    "roleCode": "CLUB_ADMIN"
  }
}
```

取消成功响应示例：
```json
{
  "code": 0,
  "message": "已取消社团管理员",
  "data": {
    "userId": 1001,
    "roleCode": "STUDENT"
  }
}
```

建议失败响应：
```json
{
  "code": 1003,
  "message": "无权限操作",
  "data": null
}
```

## 4. 列表接口字段要求（关键）
角色设置后，前端刷新依赖用户列表接口确认结果。

### 4.1 用户列表接口
- `GET /api/school-admin/users?pageNum=1&pageSize=10`

每条记录至少返回下列角色字段之一：
- `roleCode`
- `permissionType`
- `role`
- `userRole`
- `roles`（数组）

建议优先返回：
```json
{
  "id": 1001,
  "username": "20230001",
  "realName": "张三",
  "roleCode": "CLUB_ADMIN",
  "status": "ACTIVE"
}
```

## 5. 后端落库要求

1. 原子更新
- 角色变更应在事务内完成，确保角色关系表与用户展示字段一致。

2. 单一事实来源
- 若角色存储于 `user_role` 关系表，列表查询必须正确 join 并返回最新角色。

3. 避免读写延迟
- 写主库后立刻查询若走从库可能出现延迟，导致前端刷新看到旧角色。

4. 缓存同步
- 若用户列表/用户详情有缓存，角色更新后必须失效或更新缓存。

## 6. 权限控制

仅 `SCHOOL_ADMIN` 可调用：
- 非学校管理员调用返回 `403` 或业务码无权限。

建议校验：
- 操作人是否具备 `school:user-status:manage` 或等价权限。

## 7. 业务约束建议

1. 幂等性
- 重复将用户设为 `CLUB_ADMIN` 时应返回成功或“无需变更”，不应报系统异常。

2. 目标用户合法性
- 目标用户不存在返回明确错误码。

3. 角色冲突处理
- 若用户可多角色，明确本接口是“覆盖主角色”还是“追加角色”。
- 建议本接口返回 `effectiveRoleCode` 告知前端最终生效角色。

## 8. 操作留痕要求
每次角色变更记录：
- `operatorId`
- `operatorRole`
- `targetUserId`
- `beforeRole`
- `afterRole`
- `reason`（可选）
- `ip`
- `ua`
- `createdAt`

重要限制：
- 角色变更属于非审批类功能，**禁止写入 `audit_log`**（遵循 `docs/database-design.md` 第8节）。
- 建议写入独立日志表（如 `user_role_op_log`）或统一操作日志系统。

## 9. 联调自检步骤

1. 调用角色变更接口
- `PATCH /school-admin/users/{userId}/role` with `roleCode=CLUB_ADMIN`
- 期望 `code=0`

2. 立即查询用户列表
- `GET /school-admin/users`
- 目标用户角色应返回 `CLUB_ADMIN`

3. 前端页面验证
- 点击“设为社团管理员”后提示成功
- 点“刷新列表”后，目标用户显示“社团管理员”

4. 若失败定位
- 接口成功但列表仍学生：重点排查数据库事务、查询 SQL、缓存失效、主从延迟。

5. 取消社团管理员回归
- 先将用户设为 `CLUB_ADMIN`
- 再次调用角色接口传 `roleCode=STUDENT`
- 列表应恢复显示“学生”，按钮恢复灰色“设为社团管理员”

## 10. 常见问题排查

1. 前端显示还是学生
- 后端列表未返回更新后的角色字段，或返回字段名不在前端识别范围。

2. 接口成功但刷新后回退
- 更新接口写成功，但列表接口读到旧数据（缓存/从库延迟）。

3. 接口直接失败
- 检查调用者是否为学校管理员，检查权限注解与网关鉴权。
