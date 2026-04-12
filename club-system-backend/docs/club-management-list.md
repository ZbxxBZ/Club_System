# 社团管理列表后端 API 文档

## 1. 文档目标
本文档用于后端实现“学校管理员-社团管理列表”相关接口，支持以下能力：
- 查询社团基础信息（带筛选）
- 单个社团状态切换（启用/冻结）
- 前端多选后的批量状态切换（由前端循环调用单体接口）

## 2. 鉴权与权限要求
- 鉴权方式：Authorization: Bearer <token>
- 角色要求：仅 SCHOOL_ADMIN 可调用状态修改接口
- 数据范围：学校管理员可管理全校社团

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

## 4. 接口一：社团列表查询

### 4.1 请求
- 方法：GET
- 路径：/api/clubs/public
- Query 参数：
  - pageNum: number，可选，默认 1
  - pageSize: number，可选，默认 50
  - keyword: string，可选，按社团名称模糊匹配
  - category: string，可选，按分类精确匹配

### 4.2 成功响应示例
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 20001,
        "clubName": "音乐社",
        "category": "文化类",
        "instructorName": "李老师",
        "status": "ACTIVE"
      },
      {
        "id": 20002,
        "clubName": "篮球社",
        "category": "体育类",
        "instructorName": "王老师",
        "status": "FROZEN"
      }
    ],
    "total": 2,
    "pageNum": 1,
    "pageSize": 50
  }
}
```

### 4.3 字段说明
- id: 社团主键
- clubName: 社团名称
- category: 社团分类
- instructorName: 指导教师姓名
- status: 社团状态，枚举见第 6 节

## 5. 接口二：更新社团状态（单体）

### 5.1 请求
- 方法：PATCH
- 路径：/api/school-admin/clubs/{clubId}/status

Path 参数：
- clubId: number，必填，社团ID

Body：
```json
{
  "status": "FROZEN",
  "reason": "学校管理员批量冻结社团"
}
```

Body 字段：
- status: 必填，ACTIVE 或 FROZEN
- reason: 可选，操作原因

### 5.2 成功响应示例
```json
{
  "code": 0,
  "message": "社团状态更新成功",
  "data": {
    "clubId": 20001,
    "status": "FROZEN",
    "updatedAt": "2026-03-11T18:30:00+08:00"
  }
}
```

### 5.3 失败响应示例
```json
{
  "code": 1005,
  "message": "社团不存在",
  "data": null
}
```

## 6. 状态枚举约定

后端返回建议：
- ACTIVE: 启用
- FROZEN: 冻结

兼容建议（如历史系统存在）：
- NORMAL / ENABLED -> ACTIVE
- DISABLED / LOCKED -> FROZEN

## 7. 批量能力实现约定
- 当前前端通过多选后循环调用 PATCH /school-admin/clubs/{clubId}/status 实现批量。
- 后端需保证单体接口幂等：
  - 已是 ACTIVE 再次设为 ACTIVE，返回成功或“无需变更”
  - 已是 FROZEN 再次设为 FROZEN，返回成功或“无需变更”

可选增强：
- 后续可新增批量接口：POST /api/school-admin/clubs/status/batch

## 8. 错误码建议
- 1001: 参数错误
- 1002: 未登录或 token 失效
- 1003: 无权限（非 SCHOOL_ADMIN）
- 1005: 社团不存在
- 1006: 状态值不合法
- 1099: 系统异常

## 9. 日志与审计约束
- 社团状态启用/冻结属于非审批类操作。
- 禁止写入 audit_log。
- 建议写入独立操作日志表（例如 club_status_op_log）或统一操作日志系统。

## 10. 联调验收清单
- 列表接口支持 keyword、category 查询
- 列表记录包含 status 字段
- 状态接口支持 ACTIVE/FROZEN 双向切换
- 重复切换同一状态具备幂等行为
- 权限校验正确（仅 SCHOOL_ADMIN 可调用）
