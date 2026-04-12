# 社团管理员招新动态设置 API 文档

## 1. 目标
用于社团管理员维护本社团招新动态，支持：
- 设置招新时间窗口（开始/结束）
- 设置招新人数上限
- 设置报名入口状态（开启/关闭）
- 学生端社团广场展示最新招新信息

## 2. 鉴权与权限
- 鉴权方式：Authorization: Bearer <token>
- 角色要求：CLUB_ADMIN
- 数据范围：仅可操作当前管理员绑定社团（user_role.club_id）

## 3. 统一返回结构
```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

## 4. 接口定义

### 4.1 查询当前社团招新配置
- 方法：GET
- 路径：/api/club-admin/me/club/recruit-config

成功响应示例：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "clubId": 20001,
    "recruitStartAt": "2026-03-20T09:00:00+08:00",
    "recruitEndAt": "2026-04-05T18:00:00+08:00",
    "recruitLimit": 120,
    "recruitStatus": "OPEN",
    "updatedAt": "2026-03-17T10:20:00+08:00"
  }
}
```

### 4.2 更新当前社团招新配置
- 方法：PUT
- 路径：/api/club-admin/me/club/recruit-config
- Content-Type：application/json

请求体示例：
```json
{
  "recruitStartAt": "2026-03-20T09:00:00+08:00",
  "recruitEndAt": "2026-04-05T18:00:00+08:00",
  "recruitLimit": 120,
  "recruitStatus": "OPEN"
}
```

字段约束：
- recruitStartAt: 必填，开始时间
- recruitEndAt: 必填，结束时间，必须晚于开始时间
- recruitLimit: 必填，正整数，建议 1~10000
- recruitStatus: 必填，枚举 OPEN/CLOSED

成功响应示例：
```json
{
  "code": 0,
  "message": "招新动态设置成功",
  "data": {
    "clubId": 20001,
    "recruitStartAt": "2026-03-20T09:00:00+08:00",
    "recruitEndAt": "2026-04-05T18:00:00+08:00",
    "recruitLimit": 120,
    "recruitStatus": "OPEN",
    "updatedAt": "2026-03-17T10:25:00+08:00"
  }
}
```

## 5. 状态语义约定
- OPEN: 招新入口开启
- CLOSED: 招新入口关闭

学生端展示建议：
- 若 recruitStatus = CLOSED，报名入口按钮禁用
- 若当前时间不在 [recruitStartAt, recruitEndAt]，报名入口按钮禁用

## 6. 数据库字段映射
- club.id -> clubId
- club.recruit_start_at -> recruitStartAt
- club.recruit_end_at -> recruitEndAt
- club.recruit_limit -> recruitLimit
- club.recruit_status -> recruitStatus（OPEN/CLOSED）

索引建议：
- idx_recruit_status_time(recruit_status, recruit_start_at, recruit_end_at)

## 7. 业务校验规则
1. 结束时间必须晚于开始时间
2. 人数上限必须为正整数
3. 社团被禁用/注销时，不允许设置 OPEN（建议返回业务错误）
4. 非本社团管理员不可更新

## 8. 与学生端广场联动
学生端查询接口 `/api/clubs/public` 需要返回：
- recruitStatus
- isRecruiting
- recruitStartAt
- recruitEndAt
- recruitLimit
- joinEntryEnabled

后端统一计算规则：
- isRecruiting = (recruitStatus == OPEN) AND now in [recruitStartAt, recruitEndAt]
- joinEntryEnabled = isRecruiting AND 未达到 recruitLimit

## 9. 错误码建议
- 1001: 参数错误（时间区间不合法/人数上限不合法）
- 1002: 未登录或 token 失效
- 1003: 无权限（非 CLUB_ADMIN 或越权）
- 1005: 社团不存在
- 1006: 业务校验失败（社团状态不允许开启招新等）
- 1099: 系统异常

## 10. 审计与日志约束
- 招新配置属于非审批类配置操作。
- 禁止写入 audit_log。
- 建议写入独立操作日志（如 club_recruit_op_log）或统一操作日志系统。

## 11. 联调验收清单
1. CLUB_ADMIN 可查询并更新本社团招新配置
2. 非本社团管理员无法修改
3. 时间区间非法时返回明确错误
4. 学生端社团广场可看到更新后的招新时间与人数上限
5. 招新入口状态与时间窗口联动正确
6. 本功能不写 audit_log
