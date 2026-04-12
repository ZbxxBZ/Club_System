-- 学校社团管理系统（MySQL 8.x）建表脚本
-- 执行前请先创建数据库并切换：
-- CREATE DATABASE club_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
-- USE club_system;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '1筹建 2正常 3限期整改 4待注销 5已注销',
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
  `instructor_name` VARCHAR(50),
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
  `event_status` TINYINT NOT NULL COMMENT '1草稿 2待审核 3报名中 4已结束 5驳回',
  `safety_plan_url` VARCHAR(255),
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
  `summary_text` TEXT,
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

SET FOREIGN_KEY_CHECKS = 1;
