# 账号禁用/取消禁用 API 需求说明

## 1. 功能目标
学校管理员在用户治理列表中对账号执行双向切换：
- 未禁用账号：点击“禁用账号”
- 已禁用账号：点击“取消禁用账号”

前端交互约定：
- 灰色按钮：`禁用账号`（发送禁用请求）
- 绿色按钮：`取消禁用账号`（发送取消禁用请求）

## 2. 接口定义

### 2.1 调整用户状态
- `PATCH /api/school-admin/users/{userId}/status`

Request（禁用）：
```json
{
  "status": "FROZEN",
  "reason": "学校管理员手动禁用"
}
```

Request（取消禁用）：
```json
{
  "status": "ACTIVE",
  "reason": "学校管理员手动取消禁用"
}
```

## 3. 返回示例

成功（禁用）：
```json
{
  "code": 0,
  "message": "账号已禁用",
  "data": {
    "userId": 1001,
    "status": "FROZEN"
  }
}
```

成功（取消禁用）：
```json
{
  "code": 0,
  "message": "已取消账号禁用",
  "data": {
    "userId": 1001,
    "status": "ACTIVE"
  }
}
```

## 4. 列表接口字段要求
- `GET /api/school-admin/users`
- 每条记录必须返回可识别状态字段（至少一个）：
  - `status`
  - `userStatus`
  - `accountStatus`
  - `state`

前端状态映射：
- `ACTIVE/ENABLED/NORMAL/1/正常` -> 未禁用（灰色按钮）
- `FROZEN/DISABLED/LOCKED/2/禁用/冻结` -> 已禁用（绿色按钮）

## 5. 权限与幂等
- 仅 `SCHOOL_ADMIN` 可调用。
- 重复禁用或重复取消禁用应幂等处理，返回可读提示。

## 6. 留痕要求（重要）
- 用户状态切换属于非审批类功能，**禁止写入 `audit_log`**。
- 如需留痕，请写入独立日志表（如 `user_status_op_log`）或统一操作日志系统。
