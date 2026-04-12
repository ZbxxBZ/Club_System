# 定时任务“启动/关闭”后端联动增量需求说明

## 1. 需求背景
当前学校管理员页中有两个任务按钮：
- 执行毕业生自动退社
- 执行社团注销账号冻结

现有逻辑是前端本地切换颜色状态（灰/绿）。

新增要求：
- 任务处于关闭状态时，按钮为灰色；点击后按钮变绿色，并调用后端“启动任务”命令。
- 任务处于启动状态时，按钮默认为绿色；点击后按钮变灰色，并调用后端“关闭任务”命令。
- 前端按钮颜色仅在后端返回成功后切换。

## 2. 任务定义
建议后端使用稳定的任务编码（taskCode）：
- `GRADUATION_EXIT_CLUB`
- `CLUB_CANCEL_FREEZE_ACCOUNT`

建议任务状态（taskStatus）：
- `STOPPED`（关闭，灰色）
- `STARTED`（启动，绿色）
- `RUNNING`（执行中，绿色）
- `FAILED`（执行失败，灰色）

## 3. 接口设计（推荐统一接口）

### 3.1 任务启动/关闭统一操作接口
- `POST /api/school-admin/scheduled-tasks/{taskCode}/command`

Request Body：
```json
{
  "action": "START",
  "reason": "学校管理员手动触发"
}
```
或
```json
{
  "action": "STOP",
  "reason": "学校管理员手动关闭"
}
```

`action` 枚举：
- `START`
- `STOP`

Response：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "taskCode": "GRADUATION_EXIT_CLUB",
    "action": "START",
    "taskStatus": "STARTED",
    "jobId": "job-20260311-0001",
    "processedCount": 86,
    "updatedAt": "2026-03-11T16:20:35+08:00"
  }
}
```

### 3.2 查询任务状态接口（页面初始化时使用）
- `GET /api/school-admin/scheduled-tasks/status?taskCodes=GRADUATION_EXIT_CLUB,CLUB_CANCEL_FREEZE_ACCOUNT`

Response：
```json
{
  "code": 0,
  "message": "success",
  "data": [
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
}
```

## 4. 备选方案（分离接口）
若后端不采用统一接口，可使用：
- 开始：`POST /api/school-admin/scheduled-tasks/{taskCode}/start`
- 关闭：`POST /api/school-admin/scheduled-tasks/{taskCode}/stop`
- 查询：`GET /api/school-admin/scheduled-tasks/status`

前端优先支持统一接口，分离接口可在网关层做兼容转发。

## 5. 前端联调约定

### 5.1 按钮行为
- 当前为灰色（`STOPPED/FAILED`）时点击：发 `START`，成功后变绿色。
- 当前为绿色（`STARTED/RUNNING`）时点击：发 `STOP`，成功后变灰色。
- 仅当后端返回 `code=0` 时更新颜色状态。

页面初始化时颜色映射：
- `STOPPED/FAILED` -> 灰色
- `STARTED/RUNNING` -> 绿色

### 5.2 页面初始化
- 页面加载时调用状态查询接口，避免刷新后状态丢失。

### 5.3 失败处理
- 若后端失败：保持原状态不变，提示失败原因。

## 6. 并发与幂等要求
- 同一 `taskCode + action` 在短时间重复请求应幂等。
- 若任务已在运行，重复 `START` 返回可读提示（如“任务执行中”）。
- 若任务未启动却收到 `STOP`，返回明确状态（如“任务已关闭”）。

## 7. 权限与审计要求
- 仅 `SCHOOL_ADMIN` 可操作。
- 操作留痕必须记录：
  - `operatorId`
  - `operatorRole`
  - `taskCode`
  - `action`
  - `beforeStatus`
  - `afterStatus`
  - `reason`
  - `ip`
  - `ua`
  - `createdAt`

重要限制：
- 定时任务启停属于非审批类功能，**禁止写入 `audit_log`**。
- 如需留痕，请写入独立任务操作日志表（例如 `task_op_log`）或平台统一运维日志系统。

## 8. 错误码建议
- `2001` 任务编码不存在
- `2002` 任务正在执行中，禁止重复开始
- `2003` 当前状态不可关闭
- `2004` 无权限
- `2099` 任务执行异常

## 9. 与当前前端代码对应（已按本方案实现）
- 页面：`src/views/dashboard/school-admin/SchoolAdminDashboardView.vue`
- API 封装：`src/api/user-permission.js`
  - `getScheduledTaskStatusApi(taskCodes)`
  - `commandScheduledTaskApi(taskCode, { action, reason })`
- 主文档同步：`docs/user-permission-module-api.md` 的 `8.9`、`8.10`

前端任务按钮与 taskCode 对应：
- 执行毕业生自动退社 -> `GRADUATION_EXIT_CLUB`
- 执行社团注销账号冻结 -> `CLUB_CANCEL_FREEZE_ACCOUNT`

前端行为说明：
- 页面加载即请求状态接口，刷新后颜色状态保持与后端一致。
- 灰色按钮点击发送 `START`，成功后转绿色。
- 绿色按钮点击发送 `STOP`，成功后转灰色。
