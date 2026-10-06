-- =============================================
-- 个人博客系统数据库初始化脚本
-- 数据库: MySQL 8.0+
-- 字符集: utf8mb4
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS blog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE blog;

-- ===================== 1. 用户表 =====================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `email` VARCHAR(100) COMMENT '邮箱',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0禁用 1正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0未删 1已删',
    INDEX `idx_username` (`username`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ===================== 2. 角色表 =====================
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `role_key` VARCHAR(50) NOT NULL UNIQUE COMMENT '角色标识(ADMIN/EDITOR/USER)',
    `description` VARCHAR(255) COMMENT '角色描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ===================== 3. 用户角色关联表 =====================
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    INDEX `idx_role` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ===================== 4. 分类表 =====================
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `slug` VARCHAR(50) NOT NULL UNIQUE COMMENT '分类标识(用于URL)',
    `description` VARCHAR(255) COMMENT '分类描述',
    `sort_order` INT DEFAULT 0 COMMENT '排序(越大越靠前)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- ===================== 5. 文章表 =====================
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文章ID',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `slug` VARCHAR(200) UNIQUE COMMENT 'URL别名',
    `summary` VARCHAR(500) COMMENT '摘要',
    `content` LONGTEXT NOT NULL COMMENT '内容(Markdown)',
    `cover_image` VARCHAR(255) COMMENT '封面图URL',
    `category_id` BIGINT COMMENT '分类ID',
    `view_count` BIGINT DEFAULT 0 COMMENT '浏览量',
    `like_count` INT DEFAULT 0 COMMENT '点赞数',
    `comment_count` INT DEFAULT 0 COMMENT '评论数',
    `is_top` TINYINT DEFAULT 0 COMMENT '是否置顶：0否 1是',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0草稿 1发布 2回收站',
    `visibility` TINYINT DEFAULT 0 COMMENT '可见性：0公开 1私密',
    `seo_keywords` VARCHAR(255) COMMENT 'SEO关键词',
    `seo_description` VARCHAR(500) COMMENT 'SEO描述',
    `author_id` BIGINT COMMENT '作者ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `publish_time` DATETIME COMMENT '发布时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX `idx_category` (`category_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_visibility` (`visibility`),
    INDEX `idx_create_time` (`create_time`),
    INDEX `idx_is_top` (`is_top`),
    FULLTEXT INDEX `ft_title_content` (`title`, `content`) WITH PARSER ngram
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章表';

-- ===================== 6. 标签表 =====================
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '标签ID',
    `name` VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
    `slug` VARCHAR(50) UNIQUE COMMENT '标签标识',
    `color` VARCHAR(20) COMMENT '标签颜色',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- ===================== 7. 文章标签关联表 =====================
DROP TABLE IF EXISTS `article_tag`;
CREATE TABLE `article_tag` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `article_id` BIGINT NOT NULL COMMENT '文章ID',
    `tag_id` BIGINT NOT NULL COMMENT '标签ID',
    UNIQUE KEY `uk_article_tag` (`article_id`, `tag_id`),
    INDEX `idx_tag` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章标签关联表';

-- ===================== 8. 评论表 =====================
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    `article_id` BIGINT NOT NULL COMMENT '文章ID',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父评论ID(0为顶级评论)',
    `reply_to_id` BIGINT COMMENT '回复的评论ID',
    `nickname` VARCHAR(50) NOT NULL COMMENT '昵称',
    `email` VARCHAR(100) COMMENT '邮箱(可选)',
    `website` VARCHAR(255) COMMENT '网站(可选)',
    `content` TEXT NOT NULL COMMENT '评论内容',
    `ip` VARCHAR(50) COMMENT 'IP地址',
    `user_agent` VARCHAR(500) COMMENT '浏览器信息',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0待审核 1已通过 2已拒绝',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_article` (`article_id`),
    INDEX `idx_parent` (`parent_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- ===================== 9. 网站配置表 =====================
DROP TABLE IF EXISTS `site_config`;
CREATE TABLE `site_config` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `config_key` VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    `config_value` TEXT COMMENT '配置值',
    `description` VARCHAR(255) COMMENT '配置描述',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网站配置表';

-- ===================== 10. 访问日志表 =====================
DROP TABLE IF EXISTS `visit_log`;
CREATE TABLE `visit_log` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `ip` VARCHAR(50) COMMENT 'IP地址',
    `url` VARCHAR(500) COMMENT '访问URL',
    `article_id` BIGINT COMMENT '文章ID(如果访问文章页)',
    `user_agent` VARCHAR(500) COMMENT '浏览器信息',
    `referer` VARCHAR(500) COMMENT '来源页',
    `visit_date` DATE COMMENT '访问日期',
    `visit_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
    INDEX `idx_date` (`visit_date`),
    INDEX `idx_article` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='访问日志表';

-- ===================== 11. 操作日志表 =====================
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT COMMENT '操作用户ID',
    `username` VARCHAR(50) COMMENT '操作用户名',
    `operation` VARCHAR(100) COMMENT '操作描述',
    `method` VARCHAR(200) COMMENT '请求方法',
    `params` TEXT COMMENT '请求参数',
    `ip` VARCHAR(50) COMMENT 'IP地址',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_user` (`user_id`),
    INDEX `idx_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- ===================== 初始化数据 =====================

-- 初始化角色
INSERT INTO `sys_role` (`role_name`, `role_key`, `description`) VALUES
('管理员', 'ADMIN', '系统管理员，拥有所有权限'),
('编辑', 'EDITOR', '内容编辑，只能管理文章和评论'),
('普通用户', 'USER', '前台注册用户，只能查看公开文章');

-- 初始化管理员账号 (密码: admin123, BCrypt加密)
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `email`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '管理员', 'admin@blog.com', 1);

-- 关联管理员角色
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1);

-- 初始化分类
INSERT INTO `category` (`name`, `slug`, `description`, `sort_order`) VALUES
('技术分享', 'tech', '技术相关的分享文章', 100),
('日常总结', 'daily', '日常生活总结', 90),
('学习笔记', 'notes', '框架、语言、课程的学习记录', 80),
('随笔/想法', 'thoughts', '自由写作、心情、观点类', 70),
('项目记录', 'projects', '项目经验、BUG解决方案', 60),
('读书笔记', 'reading', '读书心得与笔记', 50),
('工具收藏', 'tools', '开发工具、效率工具、资源分享', 40);

-- 初始化保留标签
INSERT INTO `tag` (`name`, `slug`, `color`) VALUES
('随心记', 'moments', '#6366f1');

-- 初始化网站配置
INSERT INTO `site_config` (`config_key`, `config_value`, `description`) VALUES
('site_title', 'Capybara', '网站标题'),
('site_subtitle', '记录生活，分享技术', '网站副标题'),
('site_description', '一个简约的个人博客', '网站描述'),
('site_keywords', '博客,技术,分享', 'SEO关键词'),
('site_logo', '', '网站Logo URL'),
('site_favicon', '', '网站Favicon URL'),
('site_icp', '', '备案号'),
('social_github', '', 'GitHub链接'),
('social_email', '', '联系邮箱'),
('social_wechat', '', '微信二维码图片URL'),
('comment_audit', 'true', '评论是否需要审核'),
('theme_mode', 'light', '默认主题模式');
