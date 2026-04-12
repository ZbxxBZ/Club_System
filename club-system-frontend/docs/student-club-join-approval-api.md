# 学生入社申请与社团管理员审批 API 文档

## 1. 目标
支持完整流程：
- 学生提交入社申请
- 社团管理员审批（通过/驳回）
- 状态流转：待批准 -> 已加入 或 待批准 -> 已驳回

对应前端页面：
- 学生端：src/views/dashboard/student/StudentDashboardView.vue
- 社团管理员端：src/views/dashboard/club-admin/ClubAdminDashboardView.vue

## 2. 鉴权与权限
- 学生接口：仅 STUDENT
- 社团管理员接口：仅 CLUB_ADMIN 且仅本社团数据
- 鉴权：Authorization: Bearer <token>

## 3. 统一返回结构
```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

## 4. 状态定义
- PENDING（1）：待批准
- JOINED（2）：已加入
- REJECTED（3）：已驳回

## 5. 接口清单
1. GET /api/clubs/public
2. POST /api/clubs/{clubId}/join
3. GET /api/clubs/join/mine
4. GET /api/club-admin/me/join-applies
5. POST /api/club-admin/me/join-applies/{applyId}/decision

---

## 6. 学生端接口

### 6.1 社团广场列表（含加入态）
- 方法：GET
- 路径：/api/clubs/public

返回字段要求（每条社团记录）：
- id
- clubName
- recruitStatus
- recruitStartAt
- recruitEndAt
- recruitLimit
- isRecruiting
- joinStatus
- joinEntryEnabled

计算规则：
- isRecruiting = (recruitStatus == OPEN) AND now in [recruitStartAt, recruitEndAt]
- joinEntryEnabled = isRecruiting AND 未达到 recruitLimit AND joinStatus != JOINED AND joinStatus != PENDING

### 6.2 提交入社申请
- 方法：POST
- 路径：/api/clubs/{clubId}/join

请求体：
```json
{
  "applyReason": "希望参与社团活动"
}
```
说明：applyReason 可选。

业务校验：
1. 社团存在且可见
2. 社团处于可招新状态
3. 当前时间在招新窗口
4. 未达到 recruitLimit
5. 当前用户不是 JOINED/PENDING

名额占用规则（重要）：
- 待批准申请（PENDING）即占用名额。
- 若“当前在籍人数 + 待批准人数 + 本次申请”超过或等于 recruitLimit，则拒绝后续申请。
- 后端返回：`code=1006, message="club recruit quota is full"`。

成功响应示例：
```json
{
  "code": 0,
  "message": "加社申请已提交",
  "data": {
    "applyId": 70001,
    "clubId": 20001,
    "joinStatus": "PENDING",
    "createdAt": "2026-03-17T18:20:00+08:00"
  }
}
```

### 6.3 查询我的入社申请记录
- 方法：GET
- 路径：/api/clubs/join/mine

Query 参数：
- pageNum: number，可选，默认 1
- pageSize: number，可选，默认 20

返回字段：
- id
- clubId
- clubName
- joinStatus
- applyReason
- rejectReason
- createdAt
- reviewedAt

---

## 7. 社团管理员端接口

### 7.1 查询本社团入社申请队列
- 方法：GET
- 路径：/api/club-admin/me/join-applies

Query 参数：
- pageNum: number，可选，默认 1
- pageSize: number，可选，默认 50
- status: string，可选（PENDING/JOINED/REJECTED），默认建议 PENDING

返回字段：
- id
- clubId
- userId
- username
- realName
- joinStatus
- applyReason
- rejectReason
- createdAt
- reviewedAt

### 7.2 审批入社申请
- 方法：POST
- 路径：/api/club-admin/me/join-applies/{applyId}/decision

请求体示例（通过）：
```json
{
  "decision": "APPROVE"
}
```

请求体示例（驳回）：
```json
{
  "decision": "REJECT",
  "rejectReason": "本轮招新名额已满"
}
```

业务规则：
1. 仅允许审批 PENDING 申请
2. decision=APPROVE：
   - join_status 更新为 JOINED
   - 同事务写入/更新 club_member（member_status=1, join_at=当前时间）
3. decision=REJECT：
   - join_status 更新为 REJECTED
   - 写 reject_reason 与 reviewed_at
4. 幂等保护：已审批记录再次审批返回明确提示

成功响应示例：
```json
{
  "code": 0,
  "message": "审批成功",
  "data": {
    "applyId": 70001,
    "joinStatus": "JOINED",
    "reviewedAt": "2026-03-17T18:35:00+08:00"
  }
}
```

---

## 8. 数据库表映射
主表：club_join_apply
- id
- club_id
- user_id
- apply_reason
- join_status（1待批准/2已加入/3已驳回）
- review_user_id
- reviewed_at
- reject_reason
- created_at
- updated_at
- created_by
- updated_by
- is_deleted

关联表：club_member
- 审批通过时入社关系生效（member_status=1）

---

## 9. 错误码建议
- 1001 参数错误
- 1002 未登录或 token 失效
- 1003 无权限
- 1005 资源不存在
- 1006 业务校验失败（未招新/已招满/重复申请/重复审批）
- 1099 系统异常

1006 典型响应示例（名额已满）：
```json
{
  "code": 1006,
  "message": "club recruit quota is full",
  "data": null
}
```

## 10. 审计说明
- 学生提交入社申请属于非审批提交动作，不写 audit_log。
- 社团管理员审批（APPROVE/REJECT）属于审批动作，可按审批审计规范记录。
