# 学校管理员社团审批列表 API 文档

## 1. 文档信息
- 模块: 学校管理员-社团审批
- 版本: v1.0
- 更新时间: 2026-03-12
- 适用端: 后端 API / 前端联调

## 2. 业务说明
本模块将学校管理员原"社团管理列表"调整为"社团审批列表"，核心能力:
1. 查询社团审批记录列表
2. 推进审批流程状态
3. 驳回申请

审批状态定义:
- 1: 待初审
- 2: 答辩中
- 3: 公示中
- 4: 通过
- 5: 驳回

状态流转规则:
- 下一流程: 1 -> 2 -> 3 -> 4
- 驳回: 任意非终态可直接 -> 5
- 终态: 4(通过)、5(驳回) 不可再推进

## 3. 鉴权
- Header: Authorization: Bearer <accessToken>
- 角色: SCHOOL_ADMIN

## 4. 统一响应结构
```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

## 5. 接口一: 查询社团审批列表
- Method: GET
- URL: /api/school-admin/approvals/clubs

### Query 参数
- pageNum: number, 可选, 默认 1
- pageSize: number, 可选, 默认 10
- keyword: string, 可选, 社团名称关键词
- applyStatus: number, 可选, 审批状态(1/2/3/4/5)

### 成功响应示例
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [
      {
        "clubId": 283357707223040,
        "clubName": "test",
        "applyStatus": 1,
        "initiatorRealName": "张三",
        "createdAt": "2026-03-12T00:25:27.74"
      }
    ],
    "total": 1,
    "pageNum": 1,
    "pageSize": 10
  }
}
```

### 字段说明
- clubId: 社团ID
- clubName: 社团名称
- applyStatus: 审批状态码(1/2/3/4/5)
- initiatorRealName: 申请人真实姓名
- createdAt: 申请创建时间

## 6. 接口二: 更新审批状态(推进/驳回)
- Method: PATCH
- URL: /api/school-admin/approvals/clubs/{approvalId}/status
- Content-Type: application/json

### Path 参数
- approvalId: number, 必填, 审批记录ID

### Request Body
- applyStatus: number, 必填, 目标状态(2/3/4/5)
- opinion: string, 可选, 审批意见(建议 <= 500)

### 请求示例-推进到答辩中
```json
{
  "applyStatus": 2,
  "opinion": "学校管理员推进审批流程"
}
```

### 请求示例-驳回
```json
{
  "applyStatus": 5,
  "opinion": "学校管理员驳回申请"
}
```

### 成功响应示例
```json
{
  "code": 0,
  "message": "状态更新成功",
  "data": {
    "id": 283357707223041,
    "applyStatus": 2,
    "currentStep": "DEFENSE_REVIEW"
  }
}
```

### 失败响应示例
```json
{
  "code": 1006,
  "message": "当前状态不允许更新到目标状态",
  "data": null
}
```

## 7. 后端约束建议
1. 只允许合法流转: 1->2->3->4, 或 ->5
2. 终态(4/5)禁止继续推进
3. 状态更新需记录审批日志(审批动作允许写 audit_log)
4. 参数校验失败返回 1001, 业务规则冲突返回 1006

## 8. 前端对接说明
- 列表查询调用: GET /api/school-admin/approvals/clubs
- 进入下一流程按钮:
  - 当前 1 -> 传 2
  - 当前 2 -> 传 3
  - 当前 3 -> 传 4
- 驳回按钮: 传 applyStatus = 5
