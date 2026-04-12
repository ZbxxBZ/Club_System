# 学生端社团申报 API 规范（交付后端）

## 1. 文档信息
- 模块：学生端社团申报
- 版本：v1.1
- 更新时间：2026-03-12
- 适用系统：学校社团管理系统

## 2. 业务目标
支持学生提交“新建社团申请”，流程分两步：
1. 上传申报材料文件（章程文件 + 指导教师证明文件）
2. 提交申报信息并落库（club + club_apply）

## 3. 强约束（必须遵守）
1. 学生“提交新建社团申请”动作，禁止写入 audit_log。
2. audit_log 仅用于审批类审计动作（APPROVE / REJECT / RETURN）。
3. 提交申请时需保证事务一致性：club 与 club_apply 同事务提交。

## 4. 鉴权与权限
- 鉴权：Authorization: Bearer <accessToken>
- 角色：仅 STUDENT 可调用提交申报接口
- 未登录返回 1002，无权限返回 1003

## 5. 统一响应结构
```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

## 6. 接口总览
1. POST /api/files/upload
2. POST /api/clubs/apply
3. GET /api/clubs/apply/mine

## 7. 接口一：上传章程文件

### 7.1 请求
- Method: POST
- URL: /api/files/upload
- Content-Type: multipart/form-data

FormData 参数：
- file（必填）：章程文件
- bizType（必填）：固定值 CLUB_APPLY_CHARTER

文件规格约定：
- 指导教师证明文件不限规格（文件类型、大小不限，由存储层统一兜底）

同一上传接口也用于指导教师证明文件上传：
- file（必填）：指导教师证明文件
- bizType（必填）：固定值 CLUB_APPLY_INSTRUCTOR_PROOF

### 7.2 成功响应示例
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "url": "https://oss.example.com/club-apply/charter/20260311/abc.pdf"
  }
}
```

### 7.3 失败响应示例
```json
{
  "code": 1001,
  "message": "文件为空",
  "data": null
}
```

## 8. 接口二：提交新建社团申请

### 8.1 请求
- Method: POST
- URL: /api/clubs/apply
- Content-Type: application/json

Request Body 示例：
```json
{
  "clubName": "晨曦音乐社",
  "category": "文化类",
  "purpose": "丰富校园文化活动",
  "instructorName": "李老师",
  "charterUrl": "https://oss.example.com/club-apply/charter/20260311/abc.pdf",
  "instructorProofUrl": "https://oss.example.com/club-apply/instructor-proof/20260312/proof.zip",
  "remark": "首批发起人20人，具备固定排练场地。"
}
```

### 8.2 字段约束
1. clubName
- 必填
- 长度建议：1~100
- 不可与现有社团重名（建议唯一校验）

2. category
- 必填
- 长度建议：1~30

3. purpose
- 可选
- 长度建议：<= 500

4. instructorName
- 必填
- 对应 club_apply.instructor_name
- 长度建议：1~50

5. charterUrl
- 必填
- 对应 club_apply.charter_url
- 需为可访问文件地址

6. remark
- 可选
- 对应 club_apply.remark
- 最大 1000 字

7. instructorProofUrl
- 必填
- 指导教师证明文件地址
- 建议落库字段：club_apply.instructor_proof_url（如当前库无此字段，可先落扩展表或 JSON 扩展字段）

### 8.3 落库规则（事务内）
1. 创建 club 草稿记录（待审批状态）
2. 创建 club_apply 记录：
- club_id = 新建 club.id
- initiator_user_id = 当前登录用户ID
- charter_url = charterUrl
- instructor_name = instructorName
- instructor_proof_url = instructorProofUrl（或后端映射到扩展字段）
- apply_status = 1（待初审）
- current_step = MATERIAL_REVIEW（或后端定义值）
- remark = remark

### 8.4 成功响应示例
```json
{
  "code": 0,
  "message": "提交成功",
  "data": {
    "applyId": 50001,
    "clubId": 30001,
    "applyStatus": 1,
    "currentStep": "MATERIAL_REVIEW"
  }
}
```

### 8.5 失败响应示例
```json
{
  "code": 1006,
  "message": "社团名称已存在",
  "data": null
}
```

## 9. 状态与流程约定
- club_apply.apply_status 初始值：1（待初审）
- 申请状态码定义：
  - 1：待初审
  - 2：答辩中
  - 3：公示中
  - 4：通过
  - 5：驳回

## 10. 接口三：查询学生本人申报记录

### 10.1 请求
- Method: GET
- URL: /api/clubs/apply/mine

Query 参数：
- pageNum（可选）：默认 1
- pageSize（可选）：默认 10

### 10.2 成功响应示例
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 283357707223041,
        "clubId": 283357707223040,
        "clubName": "test",
        "applyStatus": 1,
        "createdAt": "2026-03-12T00:25:27.74"
      }
    ],
    "total": 1,
    "pageNum": 1,
    "pageSize": 10
  }
}
```

### 10.3 记录字段说明
- id：申报记录ID
- clubId：社团ID
- clubName：社团名称
- applyStatus：申报状态码（1/2/3/4/5）
- createdAt：申报创建时间

## 11. 审计与日志规范
1. 提交申请（学生动作）
- 禁止写 audit_log
- 可写独立业务日志（例如 club_apply_submit_log）

2. 审批动作（管理员动作）
- 可写 audit_log
- biz_type 建议：CLUB_APPLY
- op_type 建议：APPROVE / REJECT / RETURN

## 12. 错误码建议
- 1001 参数错误
- 1002 未登录或 token 失效
- 1003 无权限
- 1005 资源不存在
- 1006 业务校验失败（重名、文件缺失等）
- 1099 系统异常

## 13. 联调验收清单
1. 章程文件与指导教师证明文件均可上传并返回可访问 url
2. 提交申请后 club 与 club_apply 均成功落库
3. club_apply.apply_status 初始值正确
4. 学生提交申请动作不写 audit_log
5. 学生端可查询本人申报记录并显示状态
6. 管理员审批动作写 audit_log（仅审批动作）
