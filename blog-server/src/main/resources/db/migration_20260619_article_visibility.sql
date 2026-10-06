-- 为已有数据库补充文章可见性字段。
-- visibility: 0公开，1私密
SET @column_exists := (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'article'
      AND column_name = 'visibility'
);

SET @add_column_sql := IF(
    @column_exists = 0,
    'ALTER TABLE `article` ADD COLUMN `visibility` TINYINT DEFAULT 0 COMMENT ''可见性：0公开 1私密'' AFTER `status`',
    'SELECT 1'
);
PREPARE add_column_stmt FROM @add_column_sql;
EXECUTE add_column_stmt;
DEALLOCATE PREPARE add_column_stmt;

SET @index_exists := (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'article'
      AND index_name = 'idx_visibility'
);

SET @add_index_sql := IF(
    @index_exists = 0,
    'ALTER TABLE `article` ADD INDEX `idx_visibility` (`visibility`)',
    'SELECT 1'
);
PREPARE add_index_stmt FROM @add_index_sql;
EXECUTE add_index_stmt;
DEALLOCATE PREPARE add_index_stmt;
