# 用户与权限管理模块 API 文档

## 1. 目标与范围
本接口文档用于支持前端“用户与权限管理模块”联调，覆盖三类角色：
- 学生：浏览社团、加入社团、报名活动
- 社团管理员：维护本社团信息、成员、经费
- 学校管理员：审批社团事项、管理用户状态、统计监管

本文档接口已与前端代码对应：
- `src/api/auth.js`
- `src/api/user-permission.js`
- `src/views/dashboard/student/StudentDashboardView.vue`
- `src/views/dashboard/club-admin/ClubAdminDashboardView.vue`
- `src/views/dashboard/school-admin/SchoolAdminDashboardView.vue`

## 2. 鉴权与安全约定
- 协议：`HTTPS`
- Token：`Authorization: Bearer <accessToken>`
- 统一身份认证：支持校园 SSO 票据换取系统 Token
- 权限模型：`RBAC`（角色） + `Scope`（数据范围，例如“仅本社团”）
- 用户状态控制：`ACTIVE/FROZEN/GRADUATED/CANCELED`
- 所有学校管理员关键操作需记录操作留痕，但**非审批类功能禁止写入 `audit_log`**（遵循 `docs/database-design.md` 第8节）

## 3. 统一返回格式
```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

- `code = 0`：成功
- `code != 0`：业务失败

常见错误码建议：
- `1001` 参数错误
- `1002` 未登录或 token 失效
- `1003` 无权限
- `1004` 账号状态不可用（冻结/毕业/注销）
- `1005` 资源不存在
- `1006` 数据范围越权（例如社团管理员操作其他社团）
- `1099` 系统异常

## 4. 角色与权限码

### 4.1 角色码
- `STUDENT`
- `CLUB_ADMIN`
- `SCHOOL_ADMIN`

### 4.2 权限码
- `student:club:browse`
- `student:club:join`
- `student:event:signup`
- `club:info:manage`
- `club:event:manage`
- `club:finance:manage`
- `school:club:approval`
- `school:finance:audit`
- `school:data:stats`
- `school:user-status:manage`

## 5. 认证与会话接口

### 5.1 账号密码登录
- `POST /api/auth/login`

Request:
```json
{
  "username": "20230001",
  "password": "123456"
}
```

Response `data`:
```json
{
  "token": "jwt-token",
  "refreshToken": "refresh-token",
  "userId": 1001,
  "username": "20230001",
  "realName": "张三",
  "permissionType": "STUDENT",
  "status": "ACTIVE",
  "clubId": null
}
```

### 5.2 校园统一身份认证登录（SSO）
- `POST /api/auth/sso/login`

Request:
```json
{
  "ticket": "sso-ticket-from-cas"
}
```

Response：同 `5.1`

### 5.3 获取当前登录用户信息
- `GET /api/auth/me`

Response `data`:
```json
{
  "userId": 1001,
  "username": "20230001",
  "realName": "张三",
  "permissionType": "STUDENT",
  "status": "ACTIVE",
  "clubId": null,
  "permissions": ["student:club:browse", "student:club:join", "student:event:signup"]
}
```

### 5.4 刷新 Token
- `POST /api/auth/refresh-token`

### 5.5 退出登录
- `POST /api/auth/logout`

## 6. 学生端接口

### 6.1 社团公开列表
- `GET /api/clubs/public?pageNum=1&pageSize=8&keyword=&category=`

Response `data`:
```json
{
  "records": [
    {
      "id": 1,
      "clubName": "音乐社",
      "category": "文化类",
      "instructorName": "李老师"
    }
  ],
  "total": 100
}
```

### 6.2 申请加入社团
- `POST /api/clubs/{clubId}/join`

### 6.3 可报名活动列表
- `GET /api/events/open?pageNum=1&pageSize=8`

Response `data.records` 字段建议包含：
- `id`
- `title`
- `location`
- `limitCount`
- `remainingSlots`

### 6.4 报名活动
- `POST /api/events/{eventId}/signup`

高并发要求：
- 后端必须使用缓存原子扣减或分布式锁避免超卖
- 超额返回明确错误信息（如“名额已满”）

## 7. 社团管理员接口（仅本社团）

### 7.1 获取我的社团信息
- `GET /api/club-admin/me/club`

Response `data` 建议包含：
```json
{
  "clubName": "音乐社",
  "category": "文化类",
  "instructorName": "李老师",
  "introduction": "面向全校同学开展音乐排练与交流活动。",
  "purpose": "丰富校园文化生活"
}
```

### 7.2 更新我的社团信息
- `PUT /api/club-admin/me/club`

Request:
```json
{
  "clubName": "音乐社",
  "category": "文化类",
  "introduction": "面向全校同学开展音乐排练与交流活动。",
  "purpose": "丰富校园文化生活",
  "instructorName": "李老师"
}
```

字段说明：
- `introduction`：社团简介，建议长度 `<= 300`

### 7.3 获取我的社团成员
- `GET /api/club-admin/me/members?pageNum=1&pageSize=10`

### 7.4 调整成员职位
- `PATCH /api/club-admin/me/members/{memberId}/role`

Request:
```json
{
  "positionName": "部长"
}
```

### 7.5 移除成员
- `DELETE /api/club-admin/me/members/{memberId}`

### 7.6 获取本社团经费记录
- `GET /api/club-admin/me/finances?pageNum=1&pageSize=10`

Response `records` 建议包含：
- `id`
- `type` 或 `typeName`
- `amount`
- `status`

### 7.7 申请注销本社团
- `POST /api/club-admin/me/club/cancel`

Request:
```json
{
  "applyType": 1,
  "applyReason": "连续两学期无活动，申请注销",
  "assetSettlementUrl": "https://oss.example.com/club-cancel/asset-settlement/20260312/settlement.pdf"
}
```

Response:
```json
{
  "id": 90001,
  "clubId": 283357707223040,
  "applyType": 1,
  "applyReason": "连续两学期无活动，申请注销",
  "assetSettlementUrl": "https://oss.example.com/club-cancel/asset-settlement/20260312/settlement.pdf",
  "cancelStatus": 1,
  "createdAt": "2026-03-12T16:20:35+08:00"
}
```

字段说明：
- `applyType`：申请类型，`1` 主动注销，`2` 强制注销（社团管理员发起固定传 `1`）。
- `cancelStatus`：注销审批状态。
  - `1` 待公示
  - `2` 待经费结清
  - `3` 待资产移交
  - `4` 已完成
  - `5` 驳回

### 7.8 查询我的社团注销申请进度
- `GET /api/club-admin/me/club/cancel-applies?pageNum=1&pageSize=20`

Response:
```json
{
  "records": [
    {
      "id": 90001,
      "clubId": 283357707223040,
      "clubName": "晨曦音乐社",
      "applyType": 1,
      "applyReason": "连续两学期无活动，申请注销",
      "assetSettlementUrl": "https://oss.example.com/club-cancel/asset-settlement/20260312/settlement.pdf",
      "cancelStatus": 2,
      "createdAt": "2026-03-12T16:20:35+08:00"
    }
  ],
  "total": 1,
  "pageNum": 1,
  "pageSize": 20
}
```

后端处理建议：
- 成功提交后将 `club.status` 更新为 `4`（待注销），进入学校管理员审核流程。
- 提交前校验：未完成经费申请为 `0`、余额已清算、资产移交已登记。
- 若已存在“待审核”注销申请，接口应幂等返回现有申请，避免重复创建。

日志限制：
- 社团管理员发起注销申请属于非审批操作，**禁止写入 `audit_log`**。

## 8. 学校管理员接口

### 8.1 用户列表
- `GET /api/school-admin/users?pageNum=1&pageSize=10&roleCode=&status=&keyword=`

Query 参数：
- `pageNum`：页码，默认 `1`
- `pageSize`：每页条数，默认 `10`
- `keyword`：关键词（姓名或账号模糊匹配）
- `roleCode`：角色筛选
  - `STUDENT`
  - `CLUB_ADMIN`
  - `SCHOOL_ADMIN`
- `status`：状态筛选
  - `ACTIVE`
  - `FROZEN`
  - `GRADUATED`
  - `CANCELED`

Response:
```json
{
  "records": [
    {
      "id": 2001,
      "username": "20230001",
      "realName": "张三",
      "roleCode": "STUDENT",
      "status": "ACTIVE"
    }
  ],
  "total": 1,
  "pageNum": 1,
  "pageSize": 10
}
```

### 8.2 调整用户状态
- `PATCH /api/school-admin/users/{userId}/status`

Request（禁用账号）:
```json
{
  "status": "FROZEN",
  "reason": "违规操作"
}
```

Request（取消禁用，恢复账号）:
```json
{
  "status": "ACTIVE",
  "reason": "学校管理员手动取消禁用"
}
```

前端按钮约定：
- 当前状态非 `FROZEN`：按钮灰色，文案“禁用账号”，点击后发送 `status=FROZEN`。
- 当前状态为 `FROZEN`：按钮绿色，文案“取消禁用账号”，点击后发送 `status=ACTIVE`。

日志限制：
- 用户状态切换属于非审批类功能，**禁止写入 `audit_log`**（遵循 `docs/database-design.md` 第8节）。

### 8.3 调整用户角色
- `PATCH /api/school-admin/users/{userId}/role`

Request（设为社团管理员）:
```json
{
  "roleCode": "CLUB_ADMIN"
}
```

Request（取消社团管理员，恢复学生）:
```json
{
  "roleCode": "STUDENT"
}
```

前端按钮约定：
- 当前角色非 `CLUB_ADMIN`：按钮灰色，文案“设为社团管理员”，点击后发送 `roleCode=CLUB_ADMIN`。
- 当前角色为 `CLUB_ADMIN`：按钮绿色，文案“取消社团管理员”，点击后发送 `roleCode=STUDENT`。

### 8.4 社团管理列表
- `GET /api/school-admin/clubs/manage?pageNum=1&pageSize=10&keyword=&category=`

Response:
```json
{
  "records": [
    {
      "clubId": 283357707223040,
      "clubName": "晨曦音乐社",
      "category": "文化类",
      "status": 2,
      "applyStatus": 4,
      "initiatorRealName": "张三",
      "instructorName": "李老师",
      "canEdit": true,
      "canDelete": false,
      "editDisabledReason": "",
      "deleteDisabledReason": "仅驳回待审核或已注销社团允许删除"
    }
  ],
  "total": 1,
  "pageNum": 1,
  "pageSize": 10
}
```

状态定义：
- `status`（club.status）：
  - `1` 待审核
  - `2` 正常
  - `3` 限期整改/冻结
  - `4` 待注销
  - `5` 已注销
- `applyStatus`（club_apply.apply_status）：
  - `1` 待初审
  - `2` 答辩中
  - `3` 公示中
  - `4` 通过
  - `5` 驳回

前端按钮控制建议（后端返回为准）：
- `canEdit`: 是否允许修改
- `canDelete`: 是否允许删除
- `editDisabledReason/deleteDisabledReason`: 禁用原因文案

后端判定逻辑（简版，建议直接实现）：
- 修改允许（`canEdit=true`）：
  - `status != 5` 且 `applyStatus` 不在 `[1,2,3]`
- 修改禁止（`canEdit=false`）：
  - `status = 5`（已注销）
  - 或 `applyStatus` 在 `[1,2,3]`（审批进行中）

- 删除允许（`canDelete=true`）：
  - （`applyStatus = 5` 且 `status = 1`）
  - 或 `status = 5`
- 删除禁止（`canDelete=false`）：
  - 其他状态组合一律禁止

推荐伪代码：
```text
canEdit = (status != 5) && (applyStatus not in [1,2,3])
canDelete = ((applyStatus == 5 && status == 1) || status == 5)
```

说明：
- 前端只消费 `canEdit/canDelete` 与禁用原因，不自行硬编码复杂规则。
- 后端在修改/删除接口必须二次校验上述规则，避免绕过前端直接调用。

### 8.4.1 社团详情
- `GET /api/school-admin/clubs/{clubId}/manage-detail`

Response:
```json
{
  "clubId": 283357707223040,
  "clubName": "晨曦音乐社",
  "category": "文化类",
  "status": 2,
  "applyStatus": 4,
  "initiatorRealName": "张三",
  "instructorName": "李老师",
  "purpose": "丰富校园文化活动",
  "charterUrl": "https://oss.example.com/club-apply/charter/20260311/abc.pdf",
  "instructorProofUrl": "https://oss.example.com/club-apply/instructor-proof/20260312/proof.zip",
  "createdAt": "2026-03-12T00:25:27.74"
}
```

说明：
- 该接口不返回申请阶段备注（`remark`），社团管理详情页无需展示该字段。

### 8.4.2 修改社团
- `PATCH /api/school-admin/clubs/{clubId}`

Request:
```json
{
  "clubName": "晨曦音乐社",
  "category": "文化类",
  "purpose": "丰富校园文化活动",
  "instructorName": "李老师"
}
```

说明：
- 社团管理“修改社团信息”仅修改社团基础信息，不修改申请阶段备注（`remark`）。

建议规则：
- 审批流进行中（`applyStatus` 为 1/2/3）禁止修改
- 已注销（`status=5`）禁止修改

### 8.4.3 删除社团
- `DELETE /api/school-admin/clubs/{clubId}`

建议规则：
- 仅允许删除“驳回且待审核”或“已注销”记录
- 后端需进行二次校验，禁止前端绕过

### 8.4.4 冻结/解冻社团状态
- `PATCH /api/school-admin/clubs/{clubId}/status`

Request（冻结）：
```json
{
  "status": "FROZEN",
  "reason": "学校管理员手动冻结社团"
}
```

Request（解冻）：
```json
{
  "status": "ACTIVE",
  "reason": "学校管理员手动解冻社团"
}
```

状态映射建议：
- `FROZEN` -> `club.status = 3`（限期整改/冻结）
- `ACTIVE` -> `club.status = 2`（正常）

前端按钮约定：
- 当 `club.status = 2` 时展示“冻结”
- 当 `club.status = 3` 时展示“解冻”
- 仅 `status` 在 `2/3` 时允许切换，其他状态按钮禁用并提示原因

日志限制：
- 社团冻结/解冻属于非审批类功能，**禁止写入 `audit_log`**。

### 8.5 社团注销审核

### 8.5.1 社团注销审核列表
- `GET /api/school-admin/approvals/club-cancels?pageNum=1&pageSize=10&keyword=&cancelStatus=`

Response:
```json
{
  "records": [
    {
      "id": 90001,
      "clubId": 283357707223040,
      "clubName": "晨曦音乐社",
      "applyType": 1,
      "applyReason": "连续两学期无活动，申请注销",
      "assetSettlementUrl": "https://oss.example.com/club-cancel/asset-settlement/20260312/settlement.pdf",
      "cancelStatus": 1,
      "initiatorRealName": "张三",
      "createdAt": "2026-03-12T16:20:35+08:00"
    }
  ],
  "total": 1,
  "pageNum": 1,
  "pageSize": 10
}
```

### 8.5.2 社团注销审核详情
- `GET /api/school-admin/approvals/club-cancels/{cancelId}`

Response:
```json
{
  "id": 90001,
  "clubId": 283357707223040,
  "clubName": "晨曦音乐社",
  "applyType": 1,
  "applyReason": "连续两学期无活动，申请注销",
  "assetSettlementUrl": "https://oss.example.com/club-cancel/asset-settlement/20260312/settlement.pdf",
  "cancelStatus": 2,
  "initiatorRealName": "张三",
  "createdAt": "2026-03-12T16:20:35+08:00",
  "updatedAt": "2026-03-13T09:05:10+08:00"
}
```

### 8.5.3 推进/驳回社团注销审核
- `PATCH /api/school-admin/approvals/club-cancels/{cancelId}/status`

Request（推进下一流程示例）：
```json
{
  "cancelStatus": 2,
  "opinion": "学校管理员推进社团注销审核流程"
}
```

Request（驳回示例）：
```json
{
  "cancelStatus": 5,
  "opinion": "学校管理员驳回社团注销申请"
}
```

状态流转规则：
- 正常推进：`1 -> 2 -> 3 -> 4`
- 任一未完成状态可驳回为 `5`
- `4`（已完成）与 `5`（驳回）为终态，不可继续推进

### 8.6 触发毕业生自动退社
- `POST /api/school-admin/users/graduation/exit-clubs`

Request:
```json
{
  "mode": "AUTO"
}
```

Response:
```json
{
  "processedCount": 86
}
```

### 8.7 触发社团注销后账号冻结
- `POST /api/school-admin/clubs/{clubId}/freeze-accounts`

说明：
- `clubId` 可为具体社团 ID
- 批处理可约定 `clubId=batch`，后端根据 `source` 自动筛选待冻结范围

Request（批处理场景）：
```json
{
  "source": "CANCELED_CLUBS"
}
```

Response:
```json
{
  "frozenCount": 23
}
```

### 8.8 统计总览
- `GET /api/school-admin/statistics/overview`

Response:
```json
{
  "activeClubCount": 98,
  "totalClubCount": 120,
  "activeUserCount": 13045,
  "totalUserCount": 15420,
  "pendingApprovalCount": 17,
  "suspiciousExpenseCount": 6
}
```

字段说明：
- `activeClubCount`: 正常社团数
- `totalClubCount`: 全部社团数
- `activeUserCount`: 活跃用户数（仅非冻结用户）
- `totalUserCount`: 全部用户数（冻结 + 非冻结）

### 8.9 定时任务状态查询（用于按钮刷新后状态保持）
- `GET /api/school-admin/scheduled-tasks/status?taskCodes=GRADUATION_EXIT_CLUB,CLUB_CANCEL_FREEZE_ACCOUNT`

Response:
```json
[
  {
    "taskCode": "GRADUATION_EXIT_CLUB",
    "taskStatus": "STOPPED",
    "updatedAt": "2026-03-11T09:00:00+08:00"
  },
  {
    "taskCode": "CLUB_CANCEL_FREEZE_ACCOUNT",
    "taskStatus": "STARTED",
    "updatedAt": "2026-03-10T18:10:20+08:00"
  }
]
```

`taskStatus` 建议值：
- `STOPPED`（按钮灰色）
- `STARTED` / `RUNNING`（按钮绿色）

### 8.10 定时任务启动/关闭命令
- `POST /api/school-admin/scheduled-tasks/{taskCode}/command`

`taskCode` 建议：
- `GRADUATION_EXIT_CLUB`
- `CLUB_CANCEL_FREEZE_ACCOUNT`

Request（启动）：
```json
{
  "action": "START",
  "reason": "学校管理员手动启动"
}
```

Request（关闭）：
```json
{
  "action": "STOP",
  "reason": "学校管理员手动关闭"
}
```

Response:
```json
{
  "taskCode": "GRADUATION_EXIT_CLUB",
  "action": "START",
  "taskStatus": "STARTED",
  "updatedAt": "2026-03-11T16:20:35+08:00"
}
```

### 8.11 社团申报详情查询（审批列表“社团详情”按钮）
- `GET /api/school-admin/approvals/clubs/{approvalId}`

Response:
```json
{
  "id": 283357707223041,
  "clubId": 283357707223040,
  "clubName": "晨曦音乐社",
  "category": "文化类",
  "applyStatus": 1,
  "initiatorRealName": "张三",
  "instructorName": "李老师",
  "purpose": "丰富校园文化活动",
  "remark": "首批发起人20人",
  "charterUrl": "https://oss.example.com/club-apply/charter/20260311/abc.pdf",
  "instructorProofUrl": "https://oss.example.com/club-apply/instructor-proof/20260312/proof.zip",
  "createdAt": "2026-03-12T00:25:27.74"
}
```

字段说明：
- `initiatorRealName`: 申报人真实姓名
- `instructorName`: 指导教师姓名
- `charterUrl`: 章程文件下载地址（学校管理员可下载）
- `instructorProofUrl`: 指导教师证明文件下载地址（学校管理员可下载）

## 9. 后端实现要求（必须）
- 所有管理员接口需要二次鉴权：角色校验 + 数据范围校验
- 社团管理员接口必须限制在 `club-admin/me` 作用域
- 用户状态控制必须在网关或业务层统一拦截：`ACTIVE` 之外状态拒绝访问受保护资源
- “毕业自动退社”“社团注销冻结账号”需支持定时任务与手动触发两种方式
- 关键操作必须记录操作字段：`operatorId/operatorRole/targetId/before/after/ip/ua/createdAt`
- 说明：
非审批类操作（如用户状态切换、定时任务启停、页面操作日志）不得写入 `audit_log`，应写入独立业务日志表或扩展日志体系。
- 对外返回禁止泄露敏感字段（如密码哈希、内部风控评分）

## 10. 前后端联调清单
- 登录成功后 `permissionType` 与前端角色映射一致
- `/auth/me` 返回 `status`，用于前端账号状态拦截
- 所有列表接口至少支持 `pageNum/pageSize`
- 业务错误通过 `code/message` 返回可读信息
- 涉及并发扣减的报名接口需要压测验证无超卖
