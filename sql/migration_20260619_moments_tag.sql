-- 初始化随心记保留标签
INSERT INTO `tag` (`name`, `slug`, `color`)
SELECT '随心记', 'moments', '#6366f1'
WHERE NOT EXISTS (
    SELECT 1 FROM `tag` WHERE `name` = '随心记'
);
