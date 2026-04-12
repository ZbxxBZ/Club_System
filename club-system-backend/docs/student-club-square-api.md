# 学生端社团广场 API 文档

## 1. 目标
用于学生端社团广场展示，支持：
- 仅展示审批通过的社团
- 基础信息展示（成立时间、指导教师、宗旨）
- 招新动态展示（招新时间、人数上限、报名入口）
- 关键词搜索（如：音乐、科技）
- 分类筛选（文化类、体育类、学术类）

## 2. 鉴权与权限
- 鉴权方式：Authorization: Bearer <token>
- 角色要求：STUDENT（建议）
- 若允许未登录浏览，可放开为公开接口，但需脱敏输出

## 3. 统一返回结构
```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

## 4. 接口定义

### 4.1 查询社团广场列表
- 方法：GET
- 路径：/api/clubs/public

Query 参数：
- pageNum: number，可选，默认 1
- pageSize: number，可选，默认 12
- keyword: string，可选，按社团名称模糊匹配
- category: string，可选，按分类精确匹配（文化类/体育类/学术类）
- isRecruiting: boolean，可选，按"当前是否在招新窗口"筛选（true=招新中，false=未招新）
- onlyApproved: boolean，可选，建议默认 true
- applyStatus: number，可选，建议固定 4（通过）
- status: string，可选，建议固定 ACTIVE（可对外展示）

推荐前端调用参数：
```json
{
  "pageNum": 1,
  "pageSize": 12,
  "keyword": "音乐",
  "category": "文化类",
  "isRecruiting": true,
  "onlyApproved": true,
  "applyStatus": 4,
  "status": "ACTIVE"
}
```

## 5. "仅展示审批通过社团"判定规则
后端建议至少满足以下任一方案，并在实现中固定：

方案 A（推荐，语义最清晰）：
- club_apply.apply_status = 4（通过）
- 且 club.status 为可展示状态（如 ACTIVE / 正常）

方案 B（简化）：
- 仅按 club.status = ACTIVE / 正常
- 并确保未审批通过的社团不会进入 ACTIVE

说明：
- 前端默认带 onlyApproved=true、applyStatus=4、status=ACTIVE。
- 前端可按需追加 isRecruiting=true/false 做索引筛选。
- 后端可将 onlyApproved=true 作为强约束并忽略无效组合，避免误展示未通过社团。

## 6. 成功响应示例
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 20001,
        "clubName": "晨曦音乐社",
        "category": "文化类",
        "instructorName": "李老师",
        "purpose": "丰富校园文化活动",
        "establishedDate": "2024-09-01",
        "recruitStartAt": "2026-03-20T09:00:00+08:00",
        "recruitEndAt": "2026-04-05T18:00:00+08:00",
        "recruitLimit": 120,
        "recruitStatus": "OPEN",
        "joinEntryEnabled": true,
        "isRecruiting": true,
        "status": "ACTIVE"
      }
    ],
    "total": 1,
    "pageNum": 1,
    "pageSize": 12
  }
}
```

## 7. 字段说明（前端展示）
- id: 社团ID
- clubName: 社团名称
- category: 分类
- instructorName: 指导教师
- purpose: 社团宗旨
- establishedDate: 成立时间
- recruitStartAt: 招新开始时间
- recruitEndAt: 招新结束时间
- recruitLimit: 招新人数上限
- recruitStatus: 招新开关状态（OPEN/CLOSED）
- joinEntryEnabled: 报名入口是否可点击（由后端按统一规则计算并返回）
- isRecruiting: 是否处于招新中（推荐后端返回，避免前端重复计算）
- status: 社团展示状态

前端按钮联动规则：
- joinEntryEnabled=true 时，按钮蓝色，可点击，文案"申请加入"。
- joinEntryEnabled=false 且 isRecruiting=false 时，按钮灰色，不可点击，文案"当前未招新"。
- joinEntryEnabled=false 且 isRecruiting=true 时，按钮灰色，不可点击，文案"已招满"。
- 后端需返回 isRecruiting 与 joinEntryEnabled，前端无需再自行推导。

## 8. 数据库字段映射建议
- club.id -> id
- club.club_name -> clubName
- club.category -> category
- club.instructor_name -> instructorName
- club.purpose -> purpose
- club.established_date -> establishedDate
- club.recruit_start_at -> recruitStartAt
- club.recruit_end_at -> recruitEndAt
- club.recruit_limit -> recruitLimit
- club.recruit_status -> recruitStatus
- club.status -> status
- club_apply.apply_status -> 审批通过判定（仅筛选条件，不必前端展示）

isRecruiting 计算建议：
- isRecruiting = (recruitStatus == OPEN) AND now in [recruitStartAt, recruitEndAt]
- joinEntryEnabled = isRecruiting AND 未达到 recruitLimit

## 9. 错误码建议
- 1001: 参数错误
- 1002: 未登录或 token 失效
- 1003: 无权限
- 1005: 资源不存在
- 1099: 系统异常

## 10. 索引与性能建议
- 已有索引：club.idx_status_category(status, category)
- 建议补充：
  - club(club_name) 前缀索引用于 keyword 模糊搜索（按实际 SQL 设计）
  - club_apply(club_id, apply_status) 用于审批状态关联过滤
  - club(recruit_status, recruit_start_at, recruit_end_at) 用于 isRecruiting 查询范围过滤

如需高并发按 isRecruiting 精确筛选，可选方案：
- 增加冗余字段 club.is_recruiting（0/1）并建立索引，配合定时任务刷新。
- 或在缓存层维护"招新中社团ID集合"加速过滤。

## 11. 联调验收清单
1. 未通过审批社团不出现在学生端社团广场
2. keyword 搜索有效（如音乐、科技）
3. category 筛选有效（文化类/体育类/学术类）
4. isRecruiting 筛选有效（招新中/未招新）
5. 基础信息字段完整返回
6. 招新时间与人数上限字段完整返回
7. 空结果返回 records=[] 且 total=0
8. 招新中社团按钮为蓝色可点击；未招新社团按钮为灰色不可点击

## 12. 审计说明
- 社团广场查询属于读操作，不写 audit_log。
- 保持当前规则：audit_log 仅用于审批动作（APPROVE/REJECT/RETURN）。
