-- 为前台注册用户补充普通用户角色。
INSERT INTO `sys_role` (`role_name`, `role_key`, `description`)
SELECT '普通用户', 'USER', '前台注册用户，只能查看公开文章'
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_role` WHERE `role_key` = 'USER'
);
