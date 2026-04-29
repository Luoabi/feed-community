-- 创建数据库
CREATE DATABASE IF NOT EXISTS hotai_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE hotai_db;

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(500) COMMENT '头像',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    gender TINYINT DEFAULT 0 COMMENT '性别：0未知 1男 2女',
    bio VARCHAR(500) COMMENT '个人简介',
    dept_id BIGINT COMMENT '部门ID',
    dept_name VARCHAR(100) COMMENT '部门名称',
    role_ids JSON COMMENT '角色ID列表', 
    role_names VARCHAR(500) COMMENT '角色名称',
    status TINYINT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0未删除 1已删除',
    INDEX idx_username (username),
    INDEX idx_dept_id (dept_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_key VARCHAR(50) NOT NULL UNIQUE COMMENT '角色标识',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    remark VARCHAR(500) COMMENT '备注',
    menu_ids JSON COMMENT '菜单ID列表',
    data_scope TINYINT DEFAULT 1 COMMENT '数据权限：1全部 2自定义 3本部门 4本部门及以下 5仅本人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0未删除 1已删除',
    INDEX idx_role_key (role_key),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 菜单表
CREATE TABLE IF NOT EXISTS sys_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '菜单ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID',
    menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    menu_type VARCHAR(10) NOT NULL COMMENT '菜单类型：M目录 C菜单 F按钮',
    icon VARCHAR(100) COMMENT '图标',
    path VARCHAR(200) COMMENT '路由地址',
    component VARCHAR(200) COMMENT '组件路径',
    perms VARCHAR(100) COMMENT '权限标识',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    visible TINYINT DEFAULT 1 COMMENT '可见：0隐藏 1显示',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0未删除 1已删除',
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 部门表
CREATE TABLE IF NOT EXISTS sys_dept (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '部门ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父部门ID',
    dept_name VARCHAR(50) NOT NULL COMMENT '部门名称',
    leader VARCHAR(50) COMMENT '负责人',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0未删除 1已删除',
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 操作日志表
CREATE TABLE sys_log (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
                         user_id BIGINT COMMENT '用户ID',
                         username VARCHAR(50) COMMENT '用户名',
                         module VARCHAR(50) COMMENT '模块名称',
                         operation_type VARCHAR(20) COMMENT '操作类型：create update delete query login logout',
                         description VARCHAR(500) COMMENT '操作描述',
                         method VARCHAR(10) COMMENT '请求方法：GET POST PUT DELETE',
                         url VARCHAR(500) COMMENT '请求URL',
                         params TEXT COMMENT '请求参数',
                         status TINYINT DEFAULT 1 COMMENT '状态：0失败 1成功',
                         error_msg TEXT COMMENT '错误信息',
                         duration BIGINT COMMENT '执行时长(毫秒)',
                         ip VARCHAR(50) COMMENT 'IP地址',
                         browser VARCHAR(50) COMMENT '浏览器',
                         os VARCHAR(50) COMMENT '操作系统',
                         create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         INDEX idx_user_id (user_id),
                         INDEX idx_module (module),
                         INDEX idx_operation_type (operation_type),
                         INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 内容表（统一：合并了原content和post表）
CREATE TABLE IF NOT EXISTS content (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '内容ID',
    
    -- 基础信息
    title VARCHAR(500) NOT NULL COMMENT '标题',
    content TEXT COMMENT '内容',
    summary VARCHAR(1000) COMMENT '摘要',
    
    -- 类型和来源
    content_type VARCHAR(20) DEFAULT 'text' COMMENT '内容类型：article/post/text/image/video/link',
    content_source VARCHAR(20) DEFAULT 'user' COMMENT '内容来源：admin/user',
    
    -- 分类信息
    category_id BIGINT COMMENT '分类ID',
    category_name VARCHAR(100) COMMENT '分类名称',
    
    -- 媒体资源
    cover_image VARCHAR(500) COMMENT '封面图',
    images JSON COMMENT '图片列表（JSON数组）',
    video_url VARCHAR(500) COMMENT '视频URL',
    link_url VARCHAR(500) COMMENT '链接URL',
    
    -- 作者信息
    author_id BIGINT COMMENT '作者ID',
    author_name VARCHAR(50) COMMENT '作者名称',
    author_avatar VARCHAR(500) COMMENT '作者头像',
    
    -- 互动数据
    views INT DEFAULT 0 COMMENT '浏览数',
    likes INT DEFAULT 0 COMMENT '点赞数',
    comments INT DEFAULT 0 COMMENT '评论数',
    shares INT DEFAULT 0 COMMENT '分享数',
    collects INT DEFAULT 0 COMMENT '收藏数',
    
    -- 状态标记
    status VARCHAR(20) DEFAULT 'draft' COMMENT '状态：draft/pending/published/rejected',
    is_top TINYINT DEFAULT 0 COMMENT '是否置顶：0否 1是',
    is_essence TINYINT DEFAULT 0 COMMENT '是否精华：0否 1是',
    is_hot TINYINT DEFAULT 0 COMMENT '是否热门：0否 1是',
    is_recommend TINYINT DEFAULT 0 COMMENT '是否推荐：0否 1是',
    
    -- 其他
    tags VARCHAR(500) COMMENT '标签（逗号分隔）',
    publish_time DATETIME COMMENT '发布时间',
    
    -- 通用字段
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0未删除 1已删除',
    
    INDEX idx_content_type (content_type),
    INDEX idx_content_source (content_source),
    INDEX idx_category_id (category_id),
    INDEX idx_author_id (author_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容表（统一）';

-- 内容分类表
CREATE TABLE IF NOT EXISTS content_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
    category_key VARCHAR(50) NOT NULL UNIQUE COMMENT '分类标识',
    icon VARCHAR(500) COMMENT '图标',
    sort INT DEFAULT 0 COMMENT '排序',
    content_count INT DEFAULT 0 COMMENT '内容数量',
    description VARCHAR(500) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0未删除 1已删除',
    INDEX idx_category_key (category_key),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容分类表';
-- 帖子表已合并到content表，此表已废弃
-- 使用 content_type='post' 和 content_source='user' 来标识帖子

-- 评论表（content_type统一为'content'，不再区分post和content）
CREATE TABLE IF NOT EXISTS `comment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `content_type` VARCHAR(20) NOT NULL DEFAULT 'content' COMMENT '内容类型(content/comment)',
  `content_id` BIGINT NOT NULL COMMENT '内容ID',
  `content_title` VARCHAR(255) COMMENT '内容标题',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父评论ID',
  `root_id` BIGINT DEFAULT 0 COMMENT '根评论ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `user_name` VARCHAR(50) NOT NULL COMMENT '用户名',
  `user_avatar` VARCHAR(255) COMMENT '用户头像',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `likes` INT DEFAULT 0 COMMENT '点赞数',
  `reply_count` INT DEFAULT 0 COMMENT '回复数',
  `status` TINYINT DEFAULT 1 COMMENT '状态(0-已删除 1-正常 2-待审核)',
  `is_top` TINYINT DEFAULT 0 COMMENT '是否置顶(0-否 1-是)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记(0-未删除 1-已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_content` (`content_type`, `content_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 用户点赞表（target_type统一为'content'或'comment'）
CREATE TABLE IF NOT EXISTS `user_like` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `user_name` VARCHAR(50) NOT NULL COMMENT '用户名',
  `user_avatar` VARCHAR(255) COMMENT '用户头像',
  `target_type` VARCHAR(20) NOT NULL COMMENT '目标类型(content/comment)',
  `target_id` BIGINT NOT NULL COMMENT '目标ID',
  `target_title` VARCHAR(255) COMMENT '目标标题',
  `target_author` VARCHAR(50) COMMENT '目标作者',
  `status` TINYINT DEFAULT 1 COMMENT '状态(0-已取消 1-有效)',
  `ip` VARCHAR(50) COMMENT 'IP地址',
  `device` VARCHAR(50) COMMENT '设备信息',
  `like_time` DATETIME NOT NULL COMMENT '点赞时间',
  `cancel_time` DATETIME COMMENT '取消时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记(0-未删除 1-已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_target` (`target_type`, `target_id`),
  KEY `idx_like_time` (`like_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户点赞表';

-- 用户收藏表（target_type统一为'content'）
CREATE TABLE IF NOT EXISTS `user_collect` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `user_name` VARCHAR(50) NOT NULL COMMENT '用户名',
  `user_avatar` VARCHAR(255) COMMENT '用户头像',
  `target_type` VARCHAR(20) NOT NULL DEFAULT 'content' COMMENT '目标类型(content)',
  `target_id` BIGINT NOT NULL COMMENT '目标ID',
  `target_title` VARCHAR(255) COMMENT '目标标题',
  `target_author` VARCHAR(50) COMMENT '目标作者',
  `folder_name` VARCHAR(50) DEFAULT '默认收藏夹' COMMENT '收藏夹名称',
  `status` TINYINT DEFAULT 1 COMMENT '状态(0-已取消 1-有效)',
  `ip` VARCHAR(50) COMMENT 'IP地址',
  `device` VARCHAR(50) COMMENT '设备信息',
  `collect_time` DATETIME NOT NULL COMMENT '收藏时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记(0-未删除 1-已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_target` (`target_type`, `target_id`),
  KEY `idx_collect_time` (`collect_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- 用户分享表
CREATE TABLE IF NOT EXISTS `user_share` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `user_name` VARCHAR(50) NOT NULL COMMENT '用户名',
  `user_avatar` VARCHAR(255) COMMENT '用户头像',
  `target_title` VARCHAR(255) COMMENT '分享内容标题',
  `platform` VARCHAR(20) NOT NULL COMMENT '分享平台(wechat/moments/qq/weibo/link)',
  `click_count` INT DEFAULT 0 COMMENT '点击次数',
  `conversion_count` INT DEFAULT 0 COMMENT '转化次数',
  `ip` VARCHAR(50) COMMENT 'IP地址',
  `device` VARCHAR(50) COMMENT '设备信息',
  `share_time` DATETIME NOT NULL COMMENT '分享时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记(0-未删除 1-已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_platform` (`platform`),
  KEY `idx_share_time` (`share_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户分享表';

-- Feed流配置表
CREATE TABLE IF NOT EXISTS feed_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '配置ID',
    config_key VARCHAR(50) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_desc VARCHAR(500) COMMENT '配置描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Feed流配置表';

-- Feed流内容表
CREATE TABLE IF NOT EXISTS feed_content (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    content_type VARCHAR(20) NOT NULL COMMENT '内容类型',
    content_id BIGINT NOT NULL COMMENT '内容ID',
    score DOUBLE DEFAULT 0 COMMENT '排序分数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_score (score DESC),
    INDEX idx_create_time (create_time DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Feed流内容表';

-- 点赞表
CREATE TABLE IF NOT EXISTS interaction_like (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    content_type VARCHAR(20) NOT NULL COMMENT '内容类型',
    content_id BIGINT NOT NULL COMMENT '内容ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_content (user_id, content_type, content_id),
    INDEX idx_content_type_id (content_type, content_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞表';

-- 收藏表
CREATE TABLE IF NOT EXISTS interaction_collect (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    content_type VARCHAR(20) NOT NULL COMMENT '内容类型',
    content_id BIGINT NOT NULL COMMENT '内容ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_content (user_id, content_type, content_id),
    INDEX idx_content_type_id (content_type, content_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 分享表
CREATE TABLE IF NOT EXISTS interaction_share (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    content_type VARCHAR(20) NOT NULL COMMENT '内容类型',
    content_id BIGINT NOT NULL COMMENT '内容ID',
    share_platform VARCHAR(50) COMMENT '分享平台',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_content_type_id (content_type, content_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分享表';

-- 标签表
CREATE TABLE IF NOT EXISTS tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '标签ID',
    tag_name VARCHAR(50) NOT NULL COMMENT '标签名称',
    tag_key VARCHAR(50) NOT NULL UNIQUE COMMENT '标签标识',
    icon VARCHAR(500) COMMENT '图标',
    sort INT DEFAULT 0 COMMENT '排序',
    user_count INT DEFAULT 0 COMMENT '使用用户数',
    description VARCHAR(500) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0未删除 1已删除',
    INDEX idx_tag_key (tag_key),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- 用户标签关联表
CREATE TABLE IF NOT EXISTS user_tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_tag (user_id, tag_id),
    INDEX idx_user_id (user_id),
    INDEX idx_tag_id (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户标签关联表';



-- ==================== 个性化推荐系统表 ====================

-- 用户行为日志表
CREATE TABLE IF NOT EXISTS user_behavior_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    content_id BIGINT NOT NULL COMMENT '内容ID',
    action_type VARCHAR(20) NOT NULL COMMENT '行为类型：view/click/like/collect/share/comment',
    duration INT DEFAULT 0 COMMENT '停留时长(秒)',
    source VARCHAR(50) COMMENT '来源：feed/search/profile/detail',
    ip VARCHAR(50) COMMENT 'IP地址',
    device VARCHAR(50) COMMENT '设备信息',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_content_id (content_id),
    INDEX idx_action_type (action_type),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为日志表';

-- 用户兴趣画像表
CREATE TABLE IF NOT EXISTS user_interest_profile (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    tag_name VARCHAR(50) NOT NULL COMMENT '标签名称',
    interest_score DOUBLE DEFAULT 0 COMMENT '兴趣分数(0-100)',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    like_count INT DEFAULT 0 COMMENT '点赞次数',
    collect_count INT DEFAULT 0 COMMENT '收藏次数',
    comment_count INT DEFAULT 0 COMMENT '评论次数',
    share_count INT DEFAULT 0 COMMENT '分享次数',
    last_active_time DATETIME COMMENT '最后活跃时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_tag (user_id, tag_id),
    INDEX idx_user_id (user_id),
    INDEX idx_tag_id (tag_id),
    INDEX idx_interest_score (interest_score DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户兴趣画像表';

-- 内容标签关联表
CREATE TABLE IF NOT EXISTS content_tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    content_id BIGINT NOT NULL COMMENT '内容ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    tag_name VARCHAR(50) NOT NULL COMMENT '标签名称',
    weight DOUBLE DEFAULT 1.0 COMMENT '标签权重(0-1)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_content_tag (content_id, tag_id),
    INDEX idx_content_id (content_id),
    INDEX idx_tag_id (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容标签关联表';

-- 用户个性化Feed流表
CREATE TABLE IF NOT EXISTS user_feed (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    content_id BIGINT NOT NULL COMMENT '内容ID',
    score DOUBLE DEFAULT 0 COMMENT '推荐分数',
    reason VARCHAR(200) COMMENT '推荐理由',
    position INT COMMENT '推荐位置',
    is_viewed TINYINT DEFAULT 0 COMMENT '是否已浏览：0否 1是',
    is_clicked TINYINT DEFAULT 0 COMMENT '是否已点击：0否 1是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    expire_time DATETIME COMMENT '过期时间',
    INDEX idx_user_id (user_id),
    INDEX idx_content_id (content_id),
    INDEX idx_score (score DESC),
    INDEX idx_create_time (create_time),
    INDEX idx_expire_time (expire_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户个性化Feed流表';

-- 推荐效果统计表
CREATE TABLE IF NOT EXISTS recommendation_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    stat_date DATE NOT NULL COMMENT '统计日期',
    algorithm VARCHAR(50) COMMENT '算法类型：hot/personalized/collaborative',
    total_recommend INT DEFAULT 0 COMMENT '推荐总数',
    total_view INT DEFAULT 0 COMMENT '浏览总数',
    total_click INT DEFAULT 0 COMMENT '点击总数',
    total_like INT DEFAULT 0 COMMENT '点赞总数',
    ctr DOUBLE DEFAULT 0 COMMENT '点击率',
    avg_duration DOUBLE DEFAULT 0 COMMENT '平均停留时长(秒)',
    coverage_rate DOUBLE DEFAULT 0 COMMENT '内容覆盖率',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_date_algorithm (stat_date, algorithm),
    INDEX idx_stat_date (stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推荐效果统计表';
