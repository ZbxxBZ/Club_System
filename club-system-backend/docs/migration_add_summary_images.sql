-- 数据库迁移脚本：为活动总结表添加图片字段
-- 日期：2026-03-23
-- 说明：支持活动总结上传多张图片

-- 添加 summary_images 字段，用于存储图片URL数组（JSON格式）
ALTER TABLE `event_summary`
ADD COLUMN `summary_images` TEXT NULL COMMENT '总结图片URL数组(JSON格式)'
AFTER `summary_text`;

-- 验证字段是否添加成功
SELECT
    COLUMN_NAME,
    COLUMN_TYPE,
    IS_NULLABLE,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'club_system'
  AND TABLE_NAME = 'event_summary'
ORDER BY ORDINAL_POSITION;
