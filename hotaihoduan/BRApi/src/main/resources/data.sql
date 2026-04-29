USE hotai_db;

-- 清空已有数据（避免重复）
TRUNCATE TABLE sys_user;
TRUNCATE TABLE sys_role;
TRUNCATE TABLE sys_menu;
TRUNCATE TABLE sys_dept;
TRUNCATE TABLE sys_log;
TRUNCATE TABLE content;
TRUNCATE TABLE content_category;
-- post 表已合并到 content，不再需要
TRUNCATE TABLE comment;
TRUNCATE TABLE feed_config;
TRUNCATE TABLE feed_content;
TRUNCATE TABLE interaction_like;
TRUNCATE TABLE interaction_collect;
TRUNCATE TABLE interaction_share;
TRUNCATE TABLE tag;
TRUNCATE TABLE user_tag;

-- ===================== 1. 部门表 =====================
INSERT INTO sys_dept (parent_id, dept_name, leader, phone, email, sort, status) VALUES
                                                                                    (0, '总公司', '张建国', '13800000000', 'ceo@hotai.com', 0, 1),
                                                                                    (1, '技术研发部', '李明', '13800000001', 'tech@hotai.com', 1, 1),
                                                                                    (1, '产品运营部', '王芳', '13800000002', 'ops@hotai.com', 2, 1),
                                                                                    (1, '市场销售部', '赵强', '13800000003', 'market@hotai.com', 3, 1),
                                                                                    (1, '人力资源部', '刘敏', '13800000004', 'hr@hotai.com', 4, 1),
                                                                                    (2, '后端开发组', '陈亮', '13800000005', 'backend@hotai.com', 0, 1),
                                                                                    (2, '前端开发组', '周杰', '13800000006', 'frontend@hotai.com', 1, 1);

-- ===================== 2. 角色表（无重复admin） =====================
INSERT INTO sys_role (role_name, role_key, sort, status, remark, menu_ids, data_scope) VALUES
                                                                                           ('超级管理员', 'admin', 1, 1, '系统最高权限', '[1,2,3,4,5,6,7,8,9,10]', 1),
                                                                                           ('普通管理员', 'manager', 2, 1, '部门管理权限', '[1,2,3,4,5]', 3),
                                                                                           ('内容创作者', 'editor', 3, 1, '内容发布管理', '[6,7,8]', 5),
                                                                                           ('普通用户', 'user', 4, 1, '基础浏览权限', '[9,10]', 5);

-- ===================== 3. 菜单表 =====================
INSERT INTO sys_menu (parent_id, menu_name, menu_type, icon, path, component, perms, sort, status, visible) VALUES
                                                                                                                (0, '系统管理', 'M', 'system', '/system', 'Layout', NULL, 0, 1, 1),
                                                                                                                (1, '用户管理', 'C', 'user', '/system/user', 'system/user/index', 'system:user:list', 0, 1, 1),
                                                                                                                (1, '角色管理', 'C', 'role', '/system/role', 'system/role/index', 'system:role:list', 1, 1, 1),
                                                                                                                (1, '菜单管理', 'C', 'menu', '/system/menu', 'system/menu/index', 'system:menu:list', 2, 1, 1),
                                                                                                                (1, '部门管理', 'C', 'dept', '/system/dept', 'system/dept/index', 'system:dept:list', 3, 1, 1),
                                                                                                                (0, '内容管理', 'M', 'content', '/content', 'Layout', NULL, 1, 1, 1),
                                                                                                                (5, '内容列表', 'C', 'article', '/content/list', 'content/list/index', 'content:list:list', 0, 1, 1),
                                                                                                                (5, '分类管理', 'C', 'category', '/content/category', 'content/category/index', 'content:category:list', 1, 1, 1),
                                                                                                                (0, '互动管理', 'M', 'interaction', '/interaction', 'Layout', NULL, 2, 1, 1),
                                                                                                                (8, '评论管理', 'C', 'comment', '/interaction/comment', 'interaction/comment/index', 'interaction:comment:list', 0, 1, 1),
                                                                                                                (8, '点赞收藏', 'C', 'like', '/interaction/like', 'interaction/like/index', 'interaction:like:list', 1, 1, 1);

-- ===================== 4. 用户表 =====================
-- 注意：所有用户的初始密码都是 "123456"，已使用BCrypt加密
-- BCrypt加密后的密码: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKpzOWGK
INSERT INTO sys_user (username, password, nickname, avatar, email, phone, gender, dept_id, dept_name, role_ids, role_names, status, last_login_time, last_login_ip) VALUES
                                                                                                                                                                        ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKpzOWGK', '超级管理员', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'admin@hotai.com', '13800138000', 1, 1, '总公司', '[1]', '超级管理员', 1, '2025-01-01 10:00:00', '127.0.0.1'),
                                                                                                                                                                        ('tech01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKpzOWGK', '后端开发', 'https://cube.elemecdn.com/1/22/1234567890abcdef1234567890abcdefpng.png', 'tech01@hotai.com', '13800138001', 1, 6, '后端开发组', '[2,3]', '普通管理员,内容创作者', 1, '2025-01-01 11:00:00', '192.168.1.101'),
                                                                                                                                                                        ('front01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKpzOWGK', '前端开发', 'https://cube.elemecdn.com/2/33/1234567890abcdef1234567890abcdefpng.png', 'front01@hotai.com', '13800138002', 2, 7, '前端开发组', '[3]', '内容创作者', 1, '2025-01-01 12:00:00', '192.168.1.102'),
                                                                                                                                                                        ('ops01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKpzOWGK', '运营主管', 'https://cube.elemecdn.com/3/44/1234567890abcdef1234567890abcdefpng.png', 'ops01@hotai.com', '13800138003', 2, 3, '产品运营部', '[2]', '普通管理员', 1, '2025-01-01 13:00:00', '192.168.1.103'),
                                                                                                                                                                        ('user01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKpzOWGK', '普通用户1', 'https://cube.elemecdn.com/4/55/1234567890abcdef1234567890abcdefpng.png', 'user01@hotai.com', '13800138004', 0, 4, '市场销售部', '[4]', '普通用户', 1, '2025-01-01 14:00:00', '192.168.1.104'),
                                                                                                                                                                        ('user02', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKpzOWGK', '普通用户2', 'https://cube.elemecdn.com/5/66/1234567890abcdef1234567890abcdefpng.png', 'user02@hotai.com', '13800138005', 1, 5, '人力资源部', '[4]', '普通用户', 1, '2025-01-01 15:00:00', '192.168.1.105');

-- ===================== 5. 内容分类表 =====================
INSERT INTO content_category (category_name, category_key, icon, sort, content_count, description, status) VALUES
                                                                                                               ('科技资讯', 'tech', 'https://cube.elemecdn.com/icon/tech.png', 1, 5, '最新科技动态、技术分享', 1),
                                                                                                               ('生活百科', 'life', 'https://cube.elemecdn.com/icon/life.png', 2, 4, '生活技巧、日常知识', 1),
                                                                                                               ('娱乐八卦', 'entertainment', 'https://cube.elemecdn.com/icon/ent.png', 3, 3, '明星动态、娱乐新闻', 1),
                                                                                                               ('体育竞技', 'sports', 'https://cube.elemecdn.com/icon/sport.png', 4, 2, '体育赛事、运动健身', 1),
                                                                                                               ('职场经验', 'career', 'https://cube.elemecdn.com/icon/job.png', 5, 1, '职场技巧、职业发展', 1);

-- ===================== 6. 标签表 =====================
INSERT INTO tag (tag_name, tag_key, icon, sort, user_count, description, status) VALUES
                                                                                     ('Java', 'java', 'https://cube.elemecdn.com/tag/java.png', 1, 10, '后端Java开发', 1),
                                                                                     ('Python', 'python', 'https://cube.elemecdn.com/tag/python.png', 2, 8, 'Python编程', 1),
                                                                                     ('Vue', 'vue', 'https://cube.elemecdn.com/tag/vue.png', 3, 12, '前端Vue框架', 1),
                                                                                     ('React', 'react', 'https://cube.elemecdn.com/tag/react.png', 4, 6, '前端React框架', 1),
                                                                                     ('美食', 'food', 'https://cube.elemecdn.com/tag/food.png', 5, 15, '美食分享', 1),
                                                                                     ('旅行', 'travel', 'https://cube.elemecdn.com/tag/travel.png', 6, 11, '旅行攻略', 1);

-- ===================== 7. 用户标签关联表 =====================
INSERT INTO user_tag (user_id, tag_id) VALUES
                                           (2, 1), (2, 2),
                                           (3, 3), (3, 4),
                                           (5, 5), (5, 6),
                                           (6, 5), (6, 6);

-- ===================== 8. 内容表（统一：包含原content和post数据） =====================
-- 管理员发布的内容（原content表数据）
INSERT INTO content (title, content, summary, cover_image, content_type, content_source, category_id, category_name, author_id, author_name, author_avatar, views, likes, comments, shares, collects, status, is_top, is_hot, is_recommend, tags, publish_time) VALUES
('Java 并发编程实战详解', 'Java并发编程是后端开发必备技能...', '深入讲解Java多线程、锁机制、线程池', 'https://cube.elemecdn.com/content/java1.jpg', 'article', 'admin', 1, '科技资讯', 2, '后端开发', 'https://cube.elemecdn.com/1/22/1234567890abcdef1234567890abcdefpng.png', 1200, 89, 23, 15, 32, 'published', 1, 1, 1, 'Java,并发编程,后端', '2025-01-01 09:00:00'),
('Vue3 最新特性总结', 'Vue3组合式API、Teleport、Suspense...', 'Vue3核心新特性与实战技巧', 'https://cube.elemecdn.com/content/vue1.jpg', 'article', 'admin', 1, '科技资讯', 3, '前端开发', 'https://cube.elemecdn.com/2/33/1234567890abcdef1234567890abcdefpng.png', 980, 67, 18, 9, 25, 'published', 0, 1, 1, 'Vue3,前端,框架', '2025-01-01 10:30:00'),
('家庭厨房必备的10个小技巧', '厨房清洁、食材保存、烹饪技巧...', '让你的厨房生活更便捷', 'https://cube.elemecdn.com/content/food1.jpg', 'article', 'admin', 2, '生活百科', 5, '普通用户1', 'https://cube.elemecdn.com/4/55/1234567890abcdef1234567890abcdefpng.png', 1500, 120, 45, 30, 68, 'published', 0, 0, 1, '美食,厨房,生活', '2025-01-01 14:00:00'),
('2025国内热门旅游地推荐', '云南、新疆、川藏线风景绝美...', '适合自驾游的宝藏旅行地', 'https://cube.elemecdn.com/content/travel1.jpg', 'article', 'admin', 2, '生活百科', 6, '普通用户2', 'https://cube.elemecdn.com/5/66/1234567890abcdef1234567890abcdefpng.png', 2100, 156, 52, 41, 89, 'published', 0, 1, 1, '旅行,自驾游,攻略', '2025-01-01 16:00:00');

-- 用户发布的帖子（原post表数据）
INSERT INTO content (title, content, content_type, content_source, images, video_url, link_url, author_id, author_name, author_avatar, views, likes, comments, shares, collects, is_top, is_essence, is_hot, status, publish_time) VALUES
('求助：Java项目启动报错如何解决？', '项目使用SpringBoot，启动时报错端口占用...', 'text', 'user', '[]', '', '', 2, '后端开发', 'https://cube.elemecdn.com/1/22/1234567890abcdef1234567890abcdefpng.png', 320, 12, 8, 2, 5, 0, 0, 0, 'published', '2025-01-02 09:00:00'),
('分享我的前端开发桌面', '分享常用的IDE、插件、壁纸...', 'image', 'user', '["https://cube.elemecdn.com/post/desk1.jpg","https://cube.elemecdn.com/post/desk2.jpg"]', '', '', 3, '前端开发', 'https://cube.elemecdn.com/2/33/1234567890abcdef1234567890abcdefpng.png', 890, 45, 23, 11, 18, 0, 1, 1, 'published', '2025-01-02 10:00:00'),
('周末美食vlog', '自制火锅全过程记录', 'video', 'user', '[]', 'https://cube.elemecdn.com/video/food.mp4', '', 5, '普通用户1', 'https://cube.elemecdn.com/4/55/1234567890abcdef1234567890abcdefpng.png', 1200, 78, 34, 22, 41, 1, 0, 1, 'published', '2025-01-02 11:00:00');

-- ===================== 9. 评论表（content_type统一为'content'） =====================
INSERT INTO comment (content_type, content_id, content_title, parent_id, root_id, user_id, user_name, user_avatar, content, likes, reply_count, status, is_top) VALUES
('content', 1, 'Java 并发编程实战详解', 0, 0, 3, '前端开发', 'https://cube.elemecdn.com/2/33/1234567890abcdef1234567890abcdefpng.png', '写得非常详细，受益匪浅', 10, 2, 1, 1),
('content', 1, 'Java 并发编程实战详解', 1, 1, 2, '后端开发', 'https://cube.elemecdn.com/1/22/1234567890abcdef1234567890abcdefpng.png', '感谢支持，会持续更新', 5, 0, 1, 0),
('content', 6, '分享我的前端开发桌面', 0, 0, 6, '普通用户2', 'https://cube.elemecdn.com/5/66/1234567890abcdef1234567890abcdefpng.png', '桌面太酷了，求壁纸链接', 8, 0, 1, 0);

-- ===================== 10. 操作日志表 =====================
INSERT INTO sys_log (user_id, username, operation, method, params, time, ip, status) VALUES
                                                                                         (1, 'admin', '用户登录', 'com.hotai.controller.SysUserController.login', '{"username":"admin","password":"******"}', 120, '127.0.0.1', 1),
                                                                                         (2, 'tech01', '查询用户列表', 'com.hotai.controller.SysUserController.list', '{"pageNum":1,"pageSize":10}', 45, '192.168.1.101', 1),
                                                                                         (3, 'front01', '发布内容', 'com.hotai.controller.ContentController.save', '{"title":"Vue3 最新特性总结"}', 200, '192.168.1.102', 1);

-- ===================== 11. Feed流配置表 =====================
INSERT INTO feed_config (config_key, config_value, config_desc) VALUES
                                                                    ('feed_refresh_time', '5', 'Feed流刷新间隔（分钟）'),
                                                                    ('feed_page_size', '20', 'Feed流每页显示数量'),
                                                                    ('hot_score_weight', '0.6', '热门内容权重系数');

-- ===================== 12. Feed流内容表（content_type统一为'content'） =====================
INSERT INTO feed_content (content_type, content_id, score) VALUES
('content', 1, 98.5),
('content', 2, 92.3),
('content', 6, 87.6),
('content', 4, 85.2);

-- ===================== 13. 点赞表（content_type统一为'content'） =====================
INSERT INTO interaction_like (user_id, content_type, content_id) VALUES
(3, 'content', 1),
(5, 'content', 2),
(6, 'content', 6),
(2, 'content', 7);

-- ===================== 14. 收藏表（content_type统一为'content'） =====================
INSERT INTO interaction_collect (user_id, content_type, content_id) VALUES
(3, 'content', 1),
(5, 'content', 4),
(2, 'content', 6);

-- ===================== 15. 分享表（content_type统一为'content'） =====================
INSERT INTO interaction_share (user_id, content_type, content_id, share_platform) VALUES
(2, 'content', 1, '微信'),
(3, 'content', 2, '朋友圈'),
(5, 'content', 7, '抖音');
