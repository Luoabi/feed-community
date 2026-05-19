
# Hotai - 社区内容生态系统

![GitHub stars](https://img.shields.io/badge/stars-0-yellow)
![GitHub forks](https://img.shields.io/badge/forks-0-blue)
![License](https://img.shields.io/badge/license-MIT-green)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen)
![Vue](https://img.shields.io/badge/Vue-3.5.32-blue)

## 📋 项目简介

**Hotai** 是一个完整的社区内容生态系统，包含：
- 微信小程序端（用户浏览、发布内容）
- 后台管理系统（内容审核、Feed流管理、系统管理）
- Spring Boot 后端 API（个性化推荐算法、JWT认证、RBAC权限）

### 核心功能
- ✅ **UCG内容生产** - 支持文本、图片、视频、链接等多种内容类型
- ✅ **内容审核流程** - 完整的待审核→审核通过/拒绝流程
- ✅ **个性化推荐** - 基于用户兴趣画像的Feed流推荐算法
- ✅ **RBAC权限系统** - 用户、角色、菜单三级联动权限控制
- ✅ **前后端分离** - Vue + Spring Boot 现代化架构
- ✅ **JWT认证** - 安全的Token认证机制

---

## 🚀 项目结构

```
zwjBS/
├── hotai/                          # 后台管理系统
│   └── Admin/                      # Vue 3 + Element Plus
│       ├── src/
│       │   ├── api/                # API接口
│       │   ├── views/              # 页面组件
│       │   ├── router/             # 路由
│       │   └── store/              # 状态管理
│       └── package.json
│
├── hotaihoduan/                    # 后端 API
│   └── BRApi/                      # Spring Boot 3.4.1
│       ├── src/main/java/
│       │   └── org/xingchang/brapi/
│       │       ├── controller/     # 控制器
│       │       ├── service/        # 业务逻辑（含推荐算法）
│       │       ├── mapper/         # MyBatis-Plus
│       │       └── entity/         # 实体类
│       └── pom.xml
│
└── wechatApp/                      # 微信小程序
    ├── pages/                      # 小程序页面
    ├── api/                        # API接口
    └── app.js
```

---

## 🛠️ 技术栈

### 后台管理系统（Vue 3）
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.5.32 | 前端框架 |
| Vue Router | 5.0.4 | 路由管理 |
| Vuex | 4.1.0 | 状态管理 |
| Element Plus | 2.13.6 | UI组件库 |
| Axios | 1.14.0 | HTTP客户端 |
| Vite | 8.0.4 | 构建工具 |

### 后端 API（Spring Boot）
| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.4.1 | 后端框架 |
| MyBatis-Plus | 3.5.7 | ORM框架 |
| MySQL | 8.x | 数据库 |
| Swagger/OpenAPI | 2.3.0 | API文档 |
| JWT | 0.12.3 | Token认证 |
| Lombok | - | 简化代码 |
| Hutool | 5.8.25 | 工具类库 |

### 小程序端
- 微信小程序原生框架
- 支持真机调试

---

## ⚙️ 快速开始

### 1. 环境要求
- JDK 17+
- Node.js 18+
- MySQL 8.x
- Maven 3.8+

### 2. 数据库初始化

```sql
-- 创建数据库
CREATE DATABASE hotai_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 导入数据
USE hotai_db;
-- 执行 schema.sql 创建表结构
-- 执行 data.sql 导入测试数据
```

**测试账号：**
- 后台管理员：`admin` / `123456`
- 普通用户：`user01` / `123456`

### 3. 后端启动

```bash
cd hotaihoduan/BRApi
mvn clean install
mvn spring-boot:run
```

后端服务启动后访问：
- API地址：`http://localhost:8080/api`
- Swagger文档：`http://localhost:8080/api/swagger-ui.html`

### 4. 后台管理系统启动

```bash
cd hotai/Admin
npm install
npm run dev
```

访问地址：`http://localhost:5173`

### 5. 小程序端配置

1. 打开 `wechatApp/api/index.js`
2. 修改 `BASE_URL` 为你的后端地址（如果真机调试，改为局域网IP）

```javascript
const BASE_URL = 'http://localhost:8080/api' // 开发环境
// const BASE_URL = 'http://192.168.1.100:8080/api' // 真机调试
```

---

## 🎯 核心业务流程

### 1. 用户发布内容 → 待审核 → 管理员审核 → Feed流推荐

**小程序端：**
1. 用户登录小程序
2. 进入发布页面，填写标题、内容、选择分类和标签
3. 支持上传图片、视频
4. 提交后内容状态为`pending`（待审核）

**后台管理：**
1. 管理员登录后台
2. 进入「内容审核」页面
3. 查看待审核内容，支持单条/批量审核
4. 审核通过后内容状态变为`published`

**个性化推荐：**
1. 用户已登录 → 基于兴趣画像个性化推荐
2. 用户未登录 → 热门推荐（冷启动）
3. 推荐算法考虑：兴趣标签+内容热度+时间新鲜度+内容质量

---

## 🧠 个性化推荐算法

### 推荐分数（100分制）

| 权重 | 维度 | 说明 |
|------|------|------|
| 50% | 兴趣匹配分 | 用户Top5兴趣标签与内容标签匹配 |
| 30% | 内容热度分 | 浏览、点赞、评论、分享、收藏 |
| 15% | 时间新鲜度 | 24小时内满分，7天内线性衰减 |
| 5% | 内容质量分 | 精华、推荐、封面图等加分 |

### 算法流程

```java
1. 获取用户Top5兴趣标签
2. 获取候选内容（500条已发布内容）
3. 计算每条内容的个性化分数
4. 按分数降序排序
5. 过滤已浏览内容（去重）
6. 返回分页结果
```

---

## 📊 数据库设计

### 核心表结构

| 表名 | 说明 |
|------|------|
| `sys_user` | 系统用户（管理员） |
| `sys_role` | 角色表 |
| `sys_menu` | 菜单/权限表 |
| `content` | 内容表（统合文章+帖子） |
| `content_category` | 内容分类 |
| `comment` | 评论表 |
| `user_interest_profile` | 用户兴趣画像 |
| `user_like` | 点赞记录 |
| `user_collect` | 收藏记录 |
| `user_behavior_log` | 用户行为日志 |

---

## 🔒 权限管理

### RBAC三级联动

- **用户** → 分配**角色**
- **角色** → 分配**菜单/权限**
- **菜单** → 定义操作权限

### 预置角色

| 角色 | 权限 |
|------|------|
| 超级管理员 | 系统最高权限 |
| 普通管理员 | 部门管理权限 |
| 内容创作者 | 内容发布管理 |
| 普通用户 | 基础浏览权限 |

---

## 📚 API 文档

启动后端后访问 Swagger 文档：
```
http://localhost:8080/api/swagger-ui.html
```

### 主要接口

| 模块 | 接口 |
|------|------|
| 用户认证 | `/user/login` / `/user/register` |
| 内容管理 | `/content/list` / `/content/add` / `/content/approve` |
| Feed流 | `/feed/personalized` / `/feed/contentList` |
| 互动 | `/interaction/like` / `/interaction/collect` / `/comment` |
| 系统管理 | `/system/user` / `/system/role` / `/system/menu` |

---

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

### 开发规范
- 遵循 RESTful API 设计
- 后端使用 Lombok 简化代码
- 前端使用 Vue 3 Composition API
- Commit message 使用语义化格式

---

## 📄 许可证

本项目采用 [MIT License](LICENSE) 许可证。

---

## 💬 联系我们

如有问题或建议，欢迎提交 Issue！

---

**Star ⭐ 这个项目，如果对你有帮助的话！**
