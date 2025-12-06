-- ====================================
-- 情绪记录与心理趋势可视化系统 - 数据库设计
-- ====================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS mood_tracker CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE mood_tracker;

-- ====================================
-- 用户表
-- ====================================
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    `email` VARCHAR(100) COMMENT '邮箱',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `gender` TINYINT COMMENT '性别：0-未知，1-男，2-女',
    `birthday` DATE COMMENT '生日',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `role` TINYINT NOT NULL DEFAULT 0 COMMENT '角色：0-普通用户，1-管理员',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ====================================
-- 情绪类型表
-- ====================================
CREATE TABLE `mood_type` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(20) NOT NULL COMMENT '情绪名称',
    `icon` VARCHAR(50) NOT NULL COMMENT '图标（emoji或图标名称）',
    `color` VARCHAR(20) NOT NULL COMMENT '颜色代码',
    `category` VARCHAR(20) COMMENT '分类：positive-积极，negative-消极，neutral-中性',
    `description` VARCHAR(200) COMMENT '描述',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `is_system` TINYINT NOT NULL DEFAULT 0 COMMENT '是否系统预设：0-否，1-是',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='情绪类型表';

-- ====================================
-- 情绪记录表
-- ====================================
CREATE TABLE `mood_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `mood_type_id` BIGINT NOT NULL COMMENT '情绪类型ID',
    `intensity` TINYINT NOT NULL COMMENT '情绪强度：1-10',
    `trigger_event` VARCHAR(500) COMMENT '触发事件描述',
    `thoughts` TEXT COMMENT '当时的想法',
    `location` VARCHAR(100) COMMENT '地点',
    `weather` VARCHAR(20) COMMENT '天气',
    `tags` VARCHAR(200) COMMENT '标签（逗号分隔）',
    `images` TEXT COMMENT '图片URL（JSON数组）',
    `is_private` TINYINT NOT NULL DEFAULT 1 COMMENT '是否私密：0-公开，1-私密',
    `record_date` DATE NOT NULL COMMENT '记录日期',
    `record_time` TIME NOT NULL COMMENT '记录时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_record_date` (`record_date`),
    KEY `idx_mood_type_id` (`mood_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='情绪记录表';

-- ====================================
-- 标签表
-- ====================================
CREATE TABLE `tag` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(30) NOT NULL COMMENT '标签名称',
    `category` VARCHAR(20) COMMENT '分类：work-工作，family-家庭，health-健康，social-社交，other-其他',
    `color` VARCHAR(20) COMMENT '颜色',
    `user_id` BIGINT COMMENT '用户ID（NULL表示系统标签）',
    `usage_count` INT NOT NULL DEFAULT 0 COMMENT '使用次数',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- ====================================
-- 心情分享墙表（匿名）
-- ====================================
CREATE TABLE `mood_share` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID（匿名显示）',
    `mood_type_id` BIGINT NOT NULL COMMENT '情绪类型ID',
    `content` VARCHAR(500) NOT NULL COMMENT '分享内容',
    `anonymous_name` VARCHAR(20) COMMENT '匿名昵称',
    `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    `comment_count` INT NOT NULL DEFAULT 0 COMMENT '评论数',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_created_at` (`created_at`),
    KEY `idx_mood_type_id` (`mood_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='心情分享墙表';

-- ====================================
-- 分享墙点赞表
-- ====================================
CREATE TABLE `mood_share_like` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `share_id` BIGINT NOT NULL COMMENT '分享ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_share_user` (`share_id`, `user_id`),
    KEY `idx_share_id` (`share_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分享墙点赞表';

-- ====================================
-- 分享墙评论表
-- ====================================
CREATE TABLE `mood_share_comment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `share_id` BIGINT NOT NULL COMMENT '分享ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `content` VARCHAR(300) NOT NULL COMMENT '评论内容',
    `anonymous_name` VARCHAR(20) COMMENT '匿名昵称',
    `parent_id` BIGINT COMMENT '父评论ID（用于回复）',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_share_id` (`share_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分享墙评论表';

-- ====================================
-- 用户设置表
-- ====================================
CREATE TABLE `user_settings` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `reminder_enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '是否开启提醒：0-否，1-是',
    `reminder_time` TIME COMMENT '提醒时间',
    `theme` VARCHAR(20) DEFAULT 'light' COMMENT '主题：light-浅色，dark-深色',
    `privacy_level` TINYINT NOT NULL DEFAULT 1 COMMENT '隐私级别：1-完全私密，2-好友可见，3-公开',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户设置表';

-- ====================================
-- 心理评估表
-- ====================================
CREATE TABLE `assessment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `assessment_type` VARCHAR(50) NOT NULL COMMENT '评估类型：PHQ9-抑郁，GAD7-焦虑，PSS-压力',
    `score` INT NOT NULL COMMENT '得分',
    `level` VARCHAR(20) COMMENT '等级：low-低，medium-中，high-高',
    `result` TEXT COMMENT '评估结果（JSON）',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='心理评估表';

-- ====================================
-- 初始化数据 - 情绪类型
-- ====================================
INSERT INTO `mood_type` (`name`, `icon`, `color`, `category`, `description`, `sort_order`, `is_system`) VALUES
('开心', '😊', '#FFD93D', 'positive', '感到快乐、愉悦', 1, 1),
('兴奋', '🤩', '#FF6B6B', 'positive', '非常激动、充满活力', 2, 1),
('平静', '😌', '#95E1D3', 'neutral', '内心平和、放松', 3, 1),
('感恩', '🙏', '#F38181', 'positive', '心怀感激', 4, 1),
('自信', '😎', '#6C5CE7', 'positive', '充满信心', 5, 1),
('难过', '😢', '#A8DADC', 'negative', '感到悲伤、失落', 6, 1),
('焦虑', '😰', '#F1C40F', 'negative', '紧张不安、担忧', 7, 1),
('愤怒', '😡', '#E74C3C', 'negative', '生气、愤怒', 8, 1),
('疲惫', '😫', '#95A5A6', 'negative', '身心俱疲', 9, 1),
('孤独', '😔', '#6C7A89', 'negative', '感到孤单、寂寞', 10, 1),
('困惑', '😕', '#BDC3C7', 'neutral', '迷茫、不确定', 11, 1),
('无聊', '😐', '#D3D3D3', 'neutral', '百无聊赖', 12, 1);

-- ====================================
-- 初始化数据 - 系统标签
-- ====================================
INSERT INTO `tag` (`name`, `category`, `color`, `user_id`) VALUES
('工作', 'work', '#3498db', NULL),
('学习', 'work', '#2ecc71', NULL),
('家庭', 'family', '#e74c3c', NULL),
('朋友', 'social', '#f39c12', NULL),
('恋爱', 'social', '#e91e63', NULL),
('运动', 'health', '#27ae60', NULL),
('睡眠', 'health', '#9b59b6', NULL),
('饮食', 'health', '#16a085', NULL),
('娱乐', 'other', '#f1c40f', NULL),
('旅行', 'other', '#1abc9c', NULL);

-- ====================================
-- 初始化数据 - 默认管理员账户
-- 密码: admin123 (BCrypt加密)
-- ====================================
INSERT INTO `user` (`username`, `password`, `email`, `nickname`, `status`, `role`) VALUES
('admin', '$2a$10$C9mtedWeiDOujYGJ/IFC.uS0YuVRWyFqSxG/IWJ2TDiDmzUIOsLTu', 'admin@mood.com', '系统管理员', 1, 1);
