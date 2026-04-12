-- 删除测试用例：移除 user 表中的测试账号（MySQL 8.x）

DELETE FROM `user`
WHERE `username` = 'club_admin_test01';

SELECT
  `id`, `username`, `real_name`, `status`, `is_deleted`
FROM `user`
WHERE `username` = 'club_admin_test01';
