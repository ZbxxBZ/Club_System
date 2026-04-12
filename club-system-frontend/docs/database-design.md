# 学校社团管理系统数据库设计（MySQL，简化表名版）

## 1. 设计说明
本版将原有表名统一简化，示例：`sys_user` -> `user`。  
为避免与 MySQL 关键字冲突（如 `user`、`role`），建表 SQL 中统一使用反引号。

---

## 2. 表名简化映射

| 原表名 | 新表名 |
|---|---|
| `sys_user` | `user` |
| `sys_role` | `role` |
| `sys_user_role` | `user_role` |
| `club_info` | `club` |
| `club_apply` | `club_apply` |
| `club_annual_review` | `club_review` |
| `club_cancel_apply` | `club_cancel` |
| `club_position` | `club_position` |
| `club_member` | `club_member` |
| `act_event` | `event` |
| `act_signup` | `event_signup` |
| `act_checkin` | `event_checkin` |
| `act_summary` | `event_summary` |
| `fin_budget` | `budget` |
| `fin_income` | `income` |
| `fin_expense` | `expense` |
| `fin_ledger` | `ledger` |
| `flow_instance` | `flow` |
| `flow_task` | `flow_task` |
| `audit_log` | `audit_log` |

---

## 3. 核心设计约定

- 主键统一 `BIGINT`
- 时间字段统一 `DATETIME(3)`
- 软删除字段统一 `is_deleted TINYINT(1)`
- 审计字段统一：`created_at`、`updated_at`、`created_by`、`updated_by`
- 字符集：`utf8mb4`
- 存储引擎：`InnoDB`

---

## 4. 每个表的业务说明

### 4.1 用户与权限

- `user`：系统用户主表，存储账号、密码哈希、状态、基础身份信息。
- `role`：角色字典表，定义 `STUDENT`、`CLUB_ADMIN`、`SCHOOL_ADMIN` 等角色。
- `user_role`：用户与角色关系表，支持一人多角色；社团管理员通过 `club_id` 绑定管理范围。

### 4.2 社团生命周期与组织

- `club`：社团主数据，包含社团名称、分类、指导老师、招新时间窗口、招新状态（OPEN/CLOSED）、社团状态。
- `club_apply`：社团成立申请表，记录章程地址、指导教师姓名、指导教师证明文件地址、当前审批步骤、申请状态。
- `club_review`：社团年审表，记录年度报告、经费报告、评分与审核状态。
- `club_cancel`：社团注销申请表，记录注销类型、清算材料、注销流转状态。
- `club_position`：社团组织架构岗位定义（会长/部长/干事等），支持树形层级。
- `club_member`：社团成员关系表，记录成员岗位、入社/退社时间、贡献分。
- `club_join_apply`：学生入社申请表，记录申请人、个人简介、入社理由、审批状态、驳回原因及审批人信息。

### 4.3 活动管理

- `event`：活动主表，记录活动时间地点、人数上限、审核与报名状态。
- `event_signup`：活动报名表，记录用户报名关系；通过唯一索引防止重复报名。
- `event_checkin`：活动签到表，记录签到方式（校园码/人脸）、签到时间与设备。
- `event_summary`：活动总结表，记录活动复盘、反馈评分、附件材料。

### 4.4 经费管理

- `budget`：年度预算表，记录社团年度预算总额、已使用金额、预算状态。
- `income`：经费收入表，记录收入来源（拨款/赞助/会费）、金额与凭证。
- `expense`：经费支出申请表，记录支出明细、审批层级、审批状态与驳回原因。
- `ledger`：资金台账表，记录每笔收入/支出后的余额变化，作为审计依据。

### 4.5 审批与审计

- `flow`：流程实例表，记录业务审批流程当前节点与整体状态。
- `flow_task`：流程任务表，记录每个审批节点的处理人、处理结果与意见。
- `audit_log`：审计日志表，记录关键操作前后数据、操作人、时间、请求来源。

---

## 5. 全量建表语句（对应简化后表名）

```sql
-- =============================================
-- 1) 用户与权限
-- =============================================

CREATE TABLE `user` (
  `id` BIGINT PRIMARY KEY,
  `username` VARCHAR(50) NOT NULL,
  `password_hash` VARCHAR(255) NOT NULL,
  `real_name` VARCHAR(50) NOT NULL,
  `phone` VARCHAR(20),
  `email` VARCHAR(100),
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '1启用 2冻结 3注销',
  `student_no` VARCHAR(30),
  `staff_no` VARCHAR(30),
  `graduate_at` DATETIME NULL,
  `last_login_at` DATETIME NULL,
  `created_at` DATETIME(3) NOT NULL,
  `updated_at` DATETIME(3) NOT NULL,
  `created_by` BIGINT,
  `updated_by` BIGINT,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_status` (`status`),
  KEY `idx_student_no` (`student_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `role` (
  `id` BIGINT PRIMARY KEY,
  `role_code` VARCHAR(30) NOT NULL COMMENT 'STUDENT/CLUB_ADMIN/SCHOOL_ADMIN',
  `role_name` VARCHAR(50) NOT NULL,
  `status` TINYINT NOT NULL DEFAULT 1,
  `created_at` DATETIME(3) NOT NULL,
  `updated_at` DATETIME(3) NOT NULL,
  `created_by` BIGINT,
  `updated_by` BIGINT,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `user_role` (
  `id` BIGINT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `role_id` BIGINT NOT NULL,
  `club_id` BIGINT NULL COMMENT '社团管理员需绑定社团',
  `status` TINYINT NOT NULL DEFAULT 1,
  `created_at` DATETIME(3) NOT NULL,
  `updated_at` DATETIME(3) NOT NULL,
  `created_by` BIGINT,
  `updated_by` BIGINT,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  UNIQUE KEY `uk_user_role_club` (`user_id`, `role_id`, `club_id`),
  KEY `idx_role` (`role_id`),
  KEY `idx_club` (`club_id`),
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- 2) 社团生命周期与组织
-- =============================================

CREATE TABLE `club` (
  `id` BIGINT PRIMARY KEY,
  `club_code` VARCHAR(32) NOT NULL,
  `club_name` VARCHAR(100) NOT NULL,
  `category` VARCHAR(30) NOT NULL,
  `purpose` VARCHAR(500),
  `description` TEXT,
  `instructor_name` VARCHAR(50),
  `established_date` DATE,
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '1筹建 2正常 3限期整改/冻结 4待注销 5已注销',
  `recruit_start_at` DATETIME NULL,
  `recruit_end_at` DATETIME NULL,
  `recruit_limit` INT NULL,
  `recruit_status` VARCHAR(10) NOT NULL DEFAULT 'CLOSED' COMMENT 'OPEN/CLOSED',
  `created_at` DATETIME(3) NOT NULL,
  `updated_at` DATETIME(3) NOT NULL,
  `created_by` BIGINT,
  `updated_by` BIGINT,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  UNIQUE KEY `uk_club_code` (`club_code`),
  UNIQUE KEY `uk_club_name` (`club_name`),
  KEY `idx_status_category` (`status`, `category`),
  KEY `idx_recruit_status_time` (`recruit_status`, `recruit_start_at`, `recruit_end_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `club_apply` (
  `id` BIGINT PRIMARY KEY,
  `club_id` BIGINT NOT NULL,
  `initiator_user_id` BIGINT NOT NULL,
  `charter_url` VARCHAR(255),
  `instructor_name` VARCHAR(50) NULL COMMENT '指导教师姓名',
  `instructor_proof_url` VARCHAR(255) NULL COMMENT '指导教师证明文件对象存储地址',
  `apply_status` TINYINT NOT NULL COMMENT '1待初审 2答辩中 3公示中 4通过 5驳回',
  `current_step` VARCHAR(30),
  `remark` VARCHAR(1000),
  `created_at` DATETIME(3) NOT NULL,
  `updated_at` DATETIME(3) NOT NULL,
  `created_by` BIGINT,
  `updated_by` BIGINT,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  KEY `idx_club_status` (`club_id`, `apply_status`),
  CONSTRAINT `fk_club_apply_club` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `club_review` (
  `id` BIGINT PRIMARY KEY,
  `club_id` BIGINT NOT NULL,
  `review_year` INT NOT NULL,
  `report_url` VARCHAR(255),
  `finance_report_url` VARCHAR(255),
  `review_status` TINYINT NOT NULL COMMENT '1待提交 2待审核 3通过 4驳回 5整改中',
  `score` DECIMAL(5,2),
  `reject_reason` VARCHAR(500),
  `created_at` DATETIME(3) NOT NULL,
  `updated_at` DATETIME(3) NOT NULL,
  `created_by` BIGINT,
  `updated_by` BIGINT,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  UNIQUE KEY `uk_club_year` (`club_id`, `review_year`),
  KEY `idx_review_status` (`review_status`),
  CONSTRAINT `fk_club_review_club` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `club_cancel` (
  `id` BIGINT PRIMARY KEY,
  `club_id` BIGINT NOT NULL,
  `apply_type` TINYINT NOT NULL COMMENT '1主动 2强制',
  `apply_reason` VARCHAR(500),
  `asset_settlement_url` VARCHAR(255),
  `cancel_status` TINYINT NOT NULL COMMENT '1待公示 2待经费结清 3待资产移交 4已完成 5驳回',
  `created_at` DATETIME(3) NOT NULL,
  `updated_at` DATETIME(3) NOT NULL,
  `created_by` BIGINT,
  `updated_by` BIGINT,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  KEY `idx_club_cancel_status` (`club_id`, `cancel_status`),
  CONSTRAINT `fk_club_cancel_club` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `club_position` (
  `id` BIGINT PRIMARY KEY,
  `club_id` BIGINT NOT NULL,
  `position_name` VARCHAR(50) NOT NULL,
  `parent_position_id` BIGINT NULL,
  `level_no` INT NOT NULL,
  `sort_no` INT NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  `updated_at` DATETIME(3) NOT NULL,
  `created_by` BIGINT,
  `updated_by` BIGINT,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  KEY `idx_club_level` (`club_id`, `level_no`),
  CONSTRAINT `fk_club_position_club` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `club_member` (
  `id` BIGINT PRIMARY KEY,
  `club_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `position_id` BIGINT NULL,
  `join_at` DATETIME NOT NULL,
  `quit_at` DATETIME NULL,
  `member_status` TINYINT NOT NULL DEFAULT 1 COMMENT '1在籍 2已退出',
  `contribution_score` DECIMAL(10,2) NOT NULL DEFAULT 0,
  `created_at` DATETIME(3) NOT NULL,
  `updated_at` DATETIME(3) NOT NULL,
  `created_by` BIGINT,
  `updated_by` BIGINT,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  UNIQUE KEY `uk_club_user` (`club_id`, `user_id`),
  KEY `idx_member_status` (`member_status`),
  CONSTRAINT `fk_club_member_club` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`),
  CONSTRAINT `fk_club_member_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `club_join_apply` (
  `id` BIGINT PRIMARY KEY,
  `club_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `apply_reason` VARCHAR(500) NULL,
  `self_intro` VARCHAR(500) NULL COMMENT '个人简介',
  `join_status` TINYINT NOT NULL DEFAULT 1 COMMENT '1待批准 2已加入 3已驳回',
  `review_user_id` BIGINT NULL,
  `reviewed_at` DATETIME NULL,
  `reject_reason` VARCHAR(500) NULL,
  `created_at` DATETIME(3) NOT NULL,
  `updated_at` DATETIME(3) NOT NULL,
  `created_by` BIGINT,
  `updated_by` BIGINT,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  KEY `idx_join_user_status` (`user_id`, `join_status`),
  KEY `idx_join_club_status` (`club_id`, `join_status`),
  CONSTRAINT `fk_club_join_apply_club` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`),
  CONSTRAINT `fk_club_join_apply_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- 3) 活动管理
-- =============================================

CREATE TABLE `event` (
  `id` BIGINT PRIMARY KEY,
  `club_id` BIGINT NOT NULL,
  `title` VARCHAR(200) NOT NULL,
  `content` TEXT,
  `location` VARCHAR(200),
  `start_at` DATETIME NOT NULL,
  `end_at` DATETIME NOT NULL,
  `signup_start_at` DATETIME,
  `signup_end_at` DATETIME,
  `limit_count` INT NOT NULL DEFAULT 0,
  `only_member` TINYINT(1) NOT NULL DEFAULT 0,
  `event_status` TINYINT NOT NULL COMMENT '1草稿 2待审核 3报名中 4已结束 5驳回 6进行中',
  `safety_plan_url` VARCHAR(255),
  `checkin_code` VARCHAR(20) NULL COMMENT '签到码',
  `reject_reason` VARCHAR(500) NULL COMMENT '驳回原因',
  `created_at` DATETIME(3) NOT NULL,
  `updated_at` DATETIME(3) NOT NULL,
  `created_by` BIGINT,
  `updated_by` BIGINT,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  KEY `idx_club_time` (`club_id`, `start_at`),
  KEY `idx_event_status` (`event_status`),
  CONSTRAINT `fk_event_club` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `event_signup` (
  `id` BIGINT PRIMARY KEY,
  `event_id` BIGINT NOT NULL,
  `club_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `signup_status` TINYINT NOT NULL DEFAULT 1 COMMENT '1已报名 2取消',
  `signup_at` DATETIME NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  `updated_at` DATETIME(3) NOT NULL,
  `created_by` BIGINT,
  `updated_by` BIGINT,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  UNIQUE KEY `uk_event_user` (`event_id`, `user_id`),
  KEY `idx_event_signup` (`event_id`, `signup_status`),
  CONSTRAINT `fk_event_signup_event` FOREIGN KEY (`event_id`) REFERENCES `event` (`id`),
  CONSTRAINT `fk_event_signup_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `event_checkin` (
  `id` BIGINT PRIMARY KEY,
  `event_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `checkin_type` TINYINT NOT NULL COMMENT '1校园码 2人脸',
  `checkin_at` DATETIME NOT NULL,
  `device_no` VARCHAR(64),
  `created_at` DATETIME(3) NOT NULL,
  `updated_at` DATETIME(3) NOT NULL,
  `created_by` BIGINT,
  `updated_by` BIGINT,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  KEY `idx_event_checkin` (`event_id`, `checkin_at`),
  CONSTRAINT `fk_event_checkin_event` FOREIGN KEY (`event_id`) REFERENCES `event` (`id`),
  CONSTRAINT `fk_event_checkin_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `event_summary` (
  `id` BIGINT PRIMARY KEY,
  `event_id` BIGINT NOT NULL,
  `summary_text` TEXT COMMENT '总结文字内容',
  `summary_images` TEXT COMMENT '总结图片URL数组(JSON格式)',
  `feedback_score` DECIMAL(4,2),
  `issue_reflection` TEXT,
  `attachment_url` VARCHAR(255),
  `created_at` DATETIME(3) NOT NULL,
  `updated_at` DATETIME(3) NOT NULL,
  `created_by` BIGINT,
  `updated_by` BIGINT,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  UNIQUE KEY `uk_event_summary` (`event_id`),
  CONSTRAINT `fk_event_summary_event` FOREIGN KEY (`event_id`) REFERENCES `event` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- 4) 经费管理
-- =============================================

CREATE TABLE `budget` (
  `id` BIGINT PRIMARY KEY,
  `club_id` BIGINT NOT NULL,
  `budget_year` INT NOT NULL,
  `budget_total` DECIMAL(14,2) NOT NULL DEFAULT 0,
  `used_total` DECIMAL(14,2) NOT NULL DEFAULT 0,
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '1草稿 2生效 3调整中',
  `created_at` DATETIME(3) NOT NULL,
  `updated_at` DATETIME(3) NOT NULL,
  `created_by` BIGINT,
  `updated_by` BIGINT,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  UNIQUE KEY `uk_budget_club_year` (`club_id`, `budget_year`),
  CONSTRAINT `fk_budget_club` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `income` (
  `id` BIGINT PRIMARY KEY,
  `club_id` BIGINT NOT NULL,
  `income_type` TINYINT NOT NULL COMMENT '1学校拨款 2赞助 3会费',
  `amount` DECIMAL(14,2) NOT NULL,
  `occur_at` DATETIME NOT NULL,
  `proof_url` VARCHAR(255),
  `source_desc` VARCHAR(255),
  `created_at` DATETIME(3) NOT NULL,
  `updated_at` DATETIME(3) NOT NULL,
  `created_by` BIGINT,
  `updated_by` BIGINT,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  KEY `idx_income_club_time` (`club_id`, `occur_at`),
  CONSTRAINT `fk_income_club` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `expense` (
  `id` BIGINT PRIMARY KEY,
  `club_id` BIGINT NOT NULL,
  `event_id` BIGINT NULL,
  `budget_id` BIGINT,
  `category` VARCHAR(50),
  `amount` DECIMAL(14,2) NOT NULL,
  `payee_name` VARCHAR(100),
  `payee_account` VARCHAR(100),
  `invoice_url` VARCHAR(255),
  `expense_desc` VARCHAR(500),
  `approve_level` TINYINT NOT NULL COMMENT '1社团自审 2学校审核',
  `expense_status` TINYINT NOT NULL COMMENT '1待审 2通过 3驳回 4已支付',
  `reject_reason` VARCHAR(500),
  `created_at` DATETIME(3) NOT NULL,
  `updated_at` DATETIME(3) NOT NULL,
  `created_by` BIGINT,
  `updated_by` BIGINT,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  KEY `idx_expense_club_status` (`club_id`, `expense_status`),
  KEY `idx_expense_amount` (`amount`),
  CONSTRAINT `fk_expense_club` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`),
  CONSTRAINT `fk_expense_event` FOREIGN KEY (`event_id`) REFERENCES `event` (`id`),
  CONSTRAINT `fk_expense_budget` FOREIGN KEY (`budget_id`) REFERENCES `budget` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `ledger` (
  `id` BIGINT PRIMARY KEY,
  `club_id` BIGINT NOT NULL,
  `biz_type` TINYINT NOT NULL COMMENT '1收入 2支出',
  `biz_id` BIGINT NOT NULL,
  `change_amount` DECIMAL(14,2) NOT NULL,
  `balance_after` DECIMAL(14,2) NOT NULL,
  `occur_at` DATETIME NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  `updated_at` DATETIME(3) NOT NULL,
  `created_by` BIGINT,
  `updated_by` BIGINT,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  KEY `idx_ledger_club_time` (`club_id`, `occur_at`),
  CONSTRAINT `fk_ledger_club` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- 5) 审批流与审计
-- =============================================

CREATE TABLE `flow` (
  `id` BIGINT PRIMARY KEY,
  `biz_type` VARCHAR(30) NOT NULL COMMENT 'CLUB_APPLY/CLUB_REVIEW/EXPENSE_APPROVE',
  `biz_id` BIGINT NOT NULL,
  `process_key` VARCHAR(50) NOT NULL,
  `current_node` VARCHAR(50),
  `flow_status` TINYINT NOT NULL COMMENT '1进行中 2通过 3驳回 4终止',
  `start_user_id` BIGINT,
  `finished_at` DATETIME NULL,
  `created_at` DATETIME(3) NOT NULL,
  `updated_at` DATETIME(3) NOT NULL,
  `created_by` BIGINT,
  `updated_by` BIGINT,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  KEY `idx_flow_biz` (`biz_type`, `biz_id`),
  KEY `idx_flow_status` (`flow_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `flow_task` (
  `id` BIGINT PRIMARY KEY,
  `flow_id` BIGINT NOT NULL,
  `node_code` VARCHAR(50) NOT NULL,
  `assignee_user_id` BIGINT,
  `task_status` TINYINT NOT NULL COMMENT '1待处理 2已通过 3已驳回 4已转交',
  `opinion` VARCHAR(500),
  `handled_at` DATETIME NULL,
  `created_at` DATETIME(3) NOT NULL,
  `updated_at` DATETIME(3) NOT NULL,
  `created_by` BIGINT,
  `updated_by` BIGINT,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  KEY `idx_flow_task_status` (`flow_id`, `task_status`),
  CONSTRAINT `fk_flow_task_flow` FOREIGN KEY (`flow_id`) REFERENCES `flow` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `audit_log` (
  `id` BIGINT PRIMARY KEY,
  `biz_type` VARCHAR(30) NOT NULL,
  `biz_id` BIGINT NOT NULL,
  `op_type` VARCHAR(30) NOT NULL COMMENT 'CREATE/UPDATE/APPROVE/REJECT/PAY',
  `op_user_id` BIGINT,
  `op_user_role` VARCHAR(30),
  `op_at` DATETIME(3) NOT NULL,
  `request_id` VARCHAR(64),
  `ip` VARCHAR(45),
  `user_agent` VARCHAR(255),
  `before_json` JSON,
  `after_json` JSON,
  `remark` VARCHAR(500),
  KEY `idx_audit_biz` (`biz_type`, `biz_id`),
  KEY `idx_audit_user_time` (`op_user_id`, `op_at`),
  KEY `idx_audit_time` (`op_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## 6. 关键业务约束

1. 报名防超卖：
   - `event_signup` 唯一索引 `uk_event_user(event_id, user_id)` 防重复报名
   - 缓存（TongRDS）原子扣减 + DB 事务补偿

2. 经费一致性：
   - `expense` 审批通过与 `ledger` 入账同事务

3. 角色权限一致性：
   - `user_role` + 接口 RBAC 双校验

4. 注销前校验：
   - 未完成经费申请为0
   - 余额已清算
   - 资产移交已登记

---

## 7. MVP 首期落地表

- 认证权限：`user`、`role`、`user_role`
- 社团基础：`club`、`club_member`
- 活动核心：`event`、`event_signup`
- 经费核心：`income`、`expense`、`ledger`
- 审批审计：`flow`、`flow_task`、`audit_log`

以上表即可支撑登录鉴权、活动报名、经费审批与基础审计闭环。

---

## 8. 审计表使用备注（重要）

- `audit_log` 当前仅用于**社团审批业务审计**（如学校管理员对社团审批队列执行 APPROVE/REJECT/RETURN）。
- 非审批类功能（如用户状态切换、定时任务启停、页面操作日志）**禁止写入** `audit_log`，避免审计语义被污染。
- 若后续确需新增审计写入场景，必须先补充设计说明并在评审通过后再落库实现。
