# 社区互动平台 - 后台管理系统

基于 Vue3 + Element Plus + Vuex + Vue Router + Mock.js 的社区互动平台后台管理系统

## 技术特性

### 核心技术
- **前端框架**: Vue 3.5+ (Composition API)
- **UI组件库**: Element Plus 2.13+ (完整图标库)
- **状态管理**: Vuex 4.1+ (模块化Store)
- **路由管理**: Vue Router 5.0+ (动态路由、路由守卫)
- **HTTP客户端**: Axios 1.14+ (请求/响应拦截器)
- **Mock数据**: Mock.js 1.1+ (模块化Mock)
- **构建工具**: Vite 8.0+ (快速构建、HMR)

### 技术亮点
- **模块化架构**: API、Mock、Store、Router完全模块化
- **统一请求封装**: Axios拦截器统一处理Token、错误提示
- **路由守卫**: 基于Token的身份认证和权限控制
- **动态路由**: 支持根据角色权限动态生成菜单
- **响应式布局**: 适配不同屏幕尺寸
- **Element Plus图标**: 注册所有@element-plus/icons-vue图标
- **代理配置**: Vite开发服务器代理后端API
- **环境变量**: 区分开发和生产环境配置
- **LocalStorage持久化**: Token和用户信息本地存储

## 项目结构

```
hotai/Admin/
├── public/                 # 静态资源
│   ├── favicon.svg        # 网站图标
│   └── icons.svg          # SVG图标集
├── src/
│   ├── api/               # API接口模块（14个模块）
│   │   ├── index.js       # API统一导出
│   │   ├── request.js     # Axios封装（请求/响应拦截器）
│   │   ├── user.js        # 用户API（登录、用户信息、用户管理）
│   │   ├── role.js        # 角色API（角色管理、权限分配）
│   │   ├── menu.js        # 菜单API（菜单管理）
│   │   ├── dept.js        # 部门API（部门管理）
│   │   ├── log.js         # 日志API（操作日志）
│   │   ├── content.js     # 内容API（内容管理）
│   │   ├── audit.js       # 审核API（内容审核）
│   │   ├── category.js    # 分类API（分类管理）
│   │   ├── post.js        # 帖子API（帖子管理）
│   │   ├── comment.js     # 评论API（评论管理）
│   │   ├── feed.js        # Feed流API（Feed管理）
│   │   ├── interaction.js # 互动API（点赞、收藏、分享）
│   │   ├── tag.js         # 标签API（用户标签）
│   │   └── dashboard.js   # 仪表盘API（统计数据）
│   ├── assets/            # 资源文件
│   │   ├── hero.png       # 首页图片
│   │   ├── vite.svg       # Vite图标
│   │   └── vue.svg        # Vue图标
│   ├── layout/            # 布局组件
│   │   ├── index.vue      # 主布局容器
│   │   └── components/    # 布局子组件
│   │       ├── Sidebar.vue    # 侧边栏导航
│   │       ├── SidebarItem.vue # 侧边栏菜单项
│   │       ├── Navbar.vue     # 顶部导航栏
│   │       └── AppMain.vue    # 主内容区域
│   ├── mock/              # Mock数据（模块化）
│   │   ├── index.js       # Mock入口配置
│   │   └── modules/       # Mock数据模块（14个）
│   │       ├── user.js        # 用户Mock数据
│   │       ├── role.js        # 角色Mock数据
│   │       ├── menu.js        # 菜单Mock数据
│   │       ├── dept.js        # 部门Mock数据
│   │       ├── log.js         # 日志Mock数据
│   │       ├── content.js     # 内容Mock数据
│   │       ├── audit.js       # 审核Mock数据
│   │       ├── category.js    # 分类Mock数据
│   │       ├── post.js        # 帖子Mock数据
│   │       ├── comment.js     # 评论Mock数据
│   │       ├── feed.js        # Feed流Mock数据
│   │       ├── interaction.js # 互动Mock数据
│   │       ├── tag.js         # 标签Mock数据
│   │       └── dashboard.js   # 仪表盘Mock数据
│   ├── router/            # 路由配置
│   │   └── index.js       # 路由定义和守卫
│   ├── store/             # Vuex状态管理
│   │   ├── index.js       # Store入口
│   │   └── modules/       # Store模块
│   │       ├── user.js        # 用户状态（Token、用户信息、角色）
│   │       ├── app.js         # 应用状态（侧边栏、设备类型）
│   │       └── permission.js  # 权限状态（动态路由）
│   ├── styles/            # 全局样式
│   │   └── index.css      # 全局CSS样式
│   ├── utils/             # 工具函数
│   │   └── logger.js      # 日志工具
│   ├── views/             # 页面组件（7大模块）
│   │   ├── dashboard/     # 首页仪表盘
│   │   │   └── index.vue      # 数据统计、趋势图表
│   │   ├── login/         # 登录模块
│   │   │   └── index.vue      # 登录页面
│   │   ├── error/         # 错误页面
│   │   │   └── 404.vue        # 404页面
│   │   ├── system/        # 系统管理（5个子模块）
│   │   │   ├── user/          # 用户管理
│   │   │   │   └── index.vue
│   │   │   ├── role/          # 角色管理
│   │   │   │   └── index.vue
│   │   │   ├── menu/          # 菜单管理
│   │   │   │   └── index.vue
│   │   │   ├── dept/          # 部门管理
│   │   │   │   └── index.vue
│   │   │   └── log/           # 操作日志
│   │   │       └── index.vue
│   │   ├── content/       # 内容管理（3个子模块）
│   │   │   ├── list/          # 内容列表
│   │   │   │   └── index.vue
│   │   │   ├── audit/         # 内容审核
│   │   │   │   └── index.vue
│   │   │   └── category/      # 分类管理
│   │   │       └── index.vue
│   │   ├── community/     # 社区管理（3个子模块）
│   │   │   ├── post/          # 帖子管理
│   │   │   │   └── index.vue
│   │   │   ├── comment/       # 评论管理
│   │   │   │   └── index.vue
│   │   │   └── feed/          # Feed流管理
│   │   │       └── index.vue
│   │   ├── interaction/   # 互动数据（3个子模块）
│   │   │   ├── like/          # 点赞管理
│   │   │   │   └── index.vue
│   │   │   ├── collect/       # 收藏管理
│   │   │   │   └── index.vue
│   │   │   └── share/         # 分享管理
│   │   │       └── index.vue
│   │   └── user/          # 用户模块
│   │       └── tag/           # 用户标签
│   │           └── index.vue
│   ├── App.vue            # 根组件
│   ├── main.js            # 入口文件（注册插件、图标）
│   └── style.css          # 基础样式
├── .env.development       # 开发环境配置
├── .env.production        # 生产环境配置
├── .gitignore             # Git忽略配置
├── index.html             # HTML入口
├── package.json           # 项目依赖
├── vite.config.js         # Vite配置（别名、代理）
└── README.md              # 项目文档

```

## 功能模块详解

### 1. 首页仪表盘 (Dashboard)
- **统计概览**: 用户总数、内容总数、帖子总数、评论总数等核心指标
- **数据趋势**: 用户增长趋势图表、内容发布趋势图表
- **最近内容**: 展示最新发布的内容列表
- **待审核提醒**: 显示待审核内容数量，快速跳转审核页面
- **热门内容**: 展示当前热门内容排行

### 2. 系统管理 (System)

#### 2.1 用户管理
- **用户列表**: 分页展示所有用户，支持搜索和筛选
- **新增用户**: 创建新用户账号，设置基本信息
- **编辑用户**: 修改用户信息（用户名、邮箱、手机号等）
- **删除用户**: 删除指定用户
- **状态管理**: 启用/禁用用户账号
- **重置密码**: 管理员重置用户密码
- **角色分配**: 为用户分配角色和权限

#### 2.2 角色管理
- **角色列表**: 展示所有角色及其权限范围
- **角色详情**: 查看角色的详细信息和权限配置
- **新增角色**: 创建新角色，定义角色名称和描述
- **编辑角色**: 修改角色信息
- **删除角色**: 删除不需要的角色
- **权限分配**: 为角色分配菜单权限和操作权限
- **状态管理**: 启用/禁用角色

#### 2.3 菜单管理
- **菜单树**: 树形结构展示所有菜单和子菜单
- **新增菜单**: 添加新的菜单项或按钮权限
- **编辑菜单**: 修改菜单名称、图标、路由等信息
- **删除菜单**: 删除菜单项
- **动态路由**: 支持根据权限动态生成路由

#### 2.4 部门管理
- **部门树**: 树形结构展示组织架构
- **新增部门**: 创建新部门
- **编辑部门**: 修改部门信息
- **删除部门**: 删除部门节点

#### 2.5 操作日志
- **日志列表**: 记录所有用户操作行为
- **日志查询**: 按时间、用户、操作类型筛选
- **日志详情**: 查看操作的详细信息
- **日志删除**: 删除指定日志记录
- **日志清空**: 批量清空历史日志
- **日志导出**: 导出日志数据为文件

### 3. 内容管理 (Content)

#### 3.1 内容列表
- **内容展示**: 分页展示所有UGC（用户生成内容）
- **搜索筛选**: 按标题、作者、分类、状态等条件搜索
- **内容详情**: 查看内容的完整信息
- **新增内容**: 管理员手动创建内容
- **编辑内容**: 修改内容信息
- **删除内容**: 单个删除或批量删除
- **置顶管理**: 设置/取消内容置顶
- **推荐管理**: 设置/取消内容推荐

#### 3.2 内容审核
- **待审核列表**: 展示所有待审核的内容
- **审核操作**: 通过/拒绝审核，填写审核意见
- **批量审核**: 批量通过或拒绝多条内容
- **审核统计**: 统计审核数量、通过率等数据
- **审核记录**: 查看历史审核记录

#### 3.3 分类管理
- **分类列表**: 展示所有内容分类
- **分类详情**: 查看分类下的内容统计
- **新增分类**: 创建新的内容分类
- **编辑分类**: 修改分类名称和描述
- **删除分类**: 删除不需要的分类
- **状态管理**: 启用/禁用分类

### 4. 社区管理 (Community)

#### 4.1 帖子管理
- **帖子列表**: 分页展示所有社区帖子
- **帖子详情**: 查看帖子完整内容和互动数据
- **新增帖子**: 管理员发布帖子
- **编辑帖子**: 修改帖子内容
- **删除帖子**: 单个删除或批量删除
- **置顶管理**: 设置/取消帖子置顶
- **精华管理**: 设置/取消精华帖

#### 4.2 评论管理
- **评论列表**: 展示所有评论，支持按帖子筛选
- **评论详情**: 查看评论的详细信息
- **删除评论**: 单个删除或批量删除违规评论
- **批量审核**: 批量通过评论审核
- **置顶管理**: 设置/取消评论置顶
- **状态管理**: 修改评论状态（正常/隐藏/删除）

#### 4.3 Feed流管理
- **Feed配置**: 配置推荐算法参数
- **更新配置**: 修改Feed流推送规则
- **Feed统计**: 查看Feed流的数据统计
- **内容列表**: 展示Feed流中的内容
- **推送管理**: 手动推送内容到Feed流
- **移除内容**: 从Feed流中移除指定内容

### 5. 互动数据 (Interaction)

#### 5.1 点赞管理
- **点赞列表**: 展示所有点赞记录
- **数据筛选**: 按用户、内容、时间筛选
- **互动统计**: 统计点赞数量和趋势

#### 5.2 收藏管理
- **收藏列表**: 展示所有收藏记录
- **数据筛选**: 按用户、内容、时间筛选
- **收藏统计**: 统计收藏数量和热门内容

#### 5.3 分享管理
- **分享列表**: 展示所有分享记录
- **数据筛选**: 按用户、内容、时间筛选
- **分享统计**: 统计分享数量和传播效果

#### 5.4 互动分析
- **互动统计**: 综合统计点赞、收藏、分享数据
- **用户行为**: 分析用户互动行为模式
- **内容排行**: 展示互动量最高的内容排行榜

### 6. 用户标签 (User Tag)

#### 6.1 标签管理
- **标签列表**: 展示所有用户标签
- **标签详情**: 查看标签的使用统计
- **新增标签**: 创建新的用户标签
- **编辑标签**: 修改标签名称和描述
- **删除标签**: 删除不需要的标签

#### 6.2 用户标签
- **用户标签列表**: 查看用户的标签分配情况
- **分配标签**: 为用户添加标签
- **移除标签**: 移除用户的标签
- **批量分配**: 批量为多个用户分配标签
- **标签统计**: 统计各标签的用户数量
- **标签推荐**: 基于用户行为推荐合适的标签
- **兴趣分析**: 分析用户的兴趣偏好

### 7. 用户认证

#### 7.1 登录系统
- **用户登录**: 账号密码登录
- **Token认证**: 基于JWT的身份认证
- **自动登录**: 记住登录状态
- **登出功能**: 安全退出登录

#### 7.2 权限控制
- **路由守卫**: 基于Token的路由访问控制
- **角色权限**: 根据角色动态显示菜单
- **按钮权限**: 细粒度的操作权限控制（规划中）

### 8. 错误处理
- **404页面**: 访问不存在的页面时显示
- **错误提示**: 统一的错误消息提示
- **异常处理**: 全局异常捕获和处理

## 快速开始

### 环境要求
- Node.js 16+
- npm 或 yarn

### 安装依赖

```bash
npm install
```

### 开发运行

```bash
npm run dev
```

访问地址: http://localhost:3000

默认登录账号: 
- 用户名: admin
- 密码: 任意密码（Mock环境）

### 生产构建

```bash
npm run build
```

构建产物位于 `dist/` 目录

### 预览构建

```bash
npm run preview
```

## Mock数据说明

### Mock模块化管理

项目采用完全模块化的Mock数据管理，所有Mock数据位于 `src/mock/modules/` 目录下。

**Mock模块列表（14个）:**
- user.js - 用户数据
- role.js - 角色数据
- menu.js - 菜单数据
- dept.js - 部门数据
- log.js - 日志数据
- content.js - 内容数据
- audit.js - 审核数据
- category.js - 分类数据
- post.js - 帖子数据
- comment.js - 评论数据
- feed.js - Feed流数据
- interaction.js - 互动数据
- tag.js - 标签数据
- dashboard.js - 仪表盘数据

### Mock环境切换

**开发环境（默认启用Mock）:**
- Mock.js自动拦截API请求
- 无需后端服务即可运行
- 适合前端独立开发

**生产环境（使用真实API）:**

1. 注释掉 `src/main.js` 中的Mock引入:
```javascript
// import './mock' // 注释这行
```

2. 配置 `.env.production` 中的API地址:
```
VITE_API_BASE_URL=https://your-api-domain.com/api
```

3. 确保后端API接口已部署并可访问

### Mock数据特点
- 真实的数据结构和字段
- 支持分页、搜索、筛选
- 模拟网络延迟
- 完整的CRUD操作响应

## 路由配置

### 路由结构（7大模块）

| 路由路径 | 组件 | 功能说明 | 图标 |
|---------|------|----------|------|
| `/login` | Login | 登录页面 | - |
| `/` | Layout | 主布局容器 | - |
| `/dashboard` | Dashboard | 首页仪表盘 | HomeFilled |
| `/system` | Layout | 系统管理模块 | Setting |
| `/system/user` | User | 用户管理 | User |
| `/system/role` | Role | 角色管理 | UserFilled |
| `/system/menu` | Menu | 菜单管理 | Menu |
| `/system/dept` | Dept | 部门管理 | OfficeBuilding |
| `/system/log` | Log | 操作日志 | Document |
| `/content` | Layout | 内容管理模块 | Document |
| `/content/list` | ContentList | 内容列表 | List |
| `/content/audit` | ContentAudit | 内容审核 | Check |
| `/content/category` | ContentCategory | 分类管理 | Folder |
| `/community` | Layout | 社区管理模块 | ChatDotRound |
| `/community/post` | Post | 帖子管理 | ChatLineRound |
| `/community/comment` | Comment | 评论管理 | ChatSquare |
| `/community/feed` | Feed | Feed流管理 | DataLine |
| `/interaction` | Layout | 互动数据模块 | DataAnalysis |
| `/interaction/like` | Like | 点赞管理 | CaretTop |
| `/interaction/collect` | Collect | 收藏管理 | Star |
| `/interaction/share` | Share | 分享管理 | Share |
| `/user` | Layout | 用户模块 | User |
| `/user/tag` | UserTag | 用户标签 | CollectionTag |
| `/404` | NotFound | 404错误页 | - |

### 路由守卫

```javascript
router.beforeEach((to, from, next) => {
  const token = store.state.user.token
  
  // 需要认证的页面，未登录跳转到登录页
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } 
  // 已登录访问登录页，跳转到首页
  else if (to.path === '/login' && token) {
    next('/')
  } 
  else {
    next()
  }
})
```

### 路由特性
- **懒加载**: 所有页面组件使用动态import
- **嵌套路由**: 使用Layout作为父路由容器
- **路由元信息**: title、icon、requiresAuth
- **404处理**: 未匹配路由自动跳转404页面

## 状态管理 (Vuex)

### Store模块

#### 1. user模块 (用户状态)
```javascript
state: {
  token: '',        // 用户Token（持久化到localStorage）
  userInfo: {},     // 用户信息（持久化到localStorage）
  roles: []         // 用户角色
}

actions:
  - login()         // 用户登录，保存Token和用户信息
  - logout()        // 用户登出，清除所有状态
  - getUserInfo()   // 获取用户信息
```

#### 2. app模块 (应用状态)
```javascript
state: {
  sidebar: {},      // 侧边栏状态（展开/收起）
  device: '',       // 设备类型（desktop/mobile）
  size: ''          // 组件尺寸（large/default/small）
}
```

#### 3. permission模块 (权限状态)
```javascript
state: {
  routes: [],       // 所有路由
  addRoutes: []     // 动态添加的路由
}

actions:
  - generateRoutes() // 根据角色生成可访问路由
```

### 数据持久化
- Token和userInfo存储在localStorage
- 页面刷新后自动恢复登录状态

## API接口说明

### API模块列表（14个模块）

| 模块 | 文件 | 功能说明 |
|------|------|----------|
| 用户管理 | user.js | 登录、获取用户信息、用户CRUD、重置密码、状态管理 |
| 角色管理 | role.js | 角色CRUD、权限分配、状态管理 |
| 菜单管理 | menu.js | 菜单CRUD、树形结构 |
| 部门管理 | dept.js | 部门CRUD、树形结构 |
| 操作日志 | log.js | 日志查询、删除、清空、导出 |
| 内容管理 | content.js | 内容CRUD、批量删除、置顶、推荐 |
| 内容审核 | audit.js | 审核列表、审核操作、批量审核、统计 |
| 分类管理 | category.js | 分类CRUD、状态管理 |
| 帖子管理 | post.js | 帖子CRUD、批量删除、置顶、精华 |
| 评论管理 | comment.js | 评论CRUD、批量操作、置顶、状态管理 |
| Feed流 | feed.js | Feed配置、统计、内容推送、移除 |
| 互动数据 | interaction.js | 点赞/收藏/分享列表、统计、排行 |
| 用户标签 | tag.js | 标签CRUD、用户标签分配、统计、推荐、兴趣分析 |
| 仪表盘 | dashboard.js | 统计数据、趋势图表、热门内容、待审核 |

### 请求拦截器功能
- 自动添加Authorization Token
- 统一错误处理和消息提示
- Token过期自动登出
- 请求超时设置（15秒）

### API基础路径
- 开发环境: `/api` (代理到 http://localhost:8080)
- 生产环境: 通过 `VITE_API_BASE_URL` 环境变量配置

## 项目配置

### Vite配置 (vite.config.js)

```javascript
{
  // 路径别名
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')  // @指向src目录
    }
  },
  
  // 开发服务器
  server: {
    port: 3000,              // 端口号
    open: true,              // 自动打开浏览器
    proxy: {
      '/api': {
        target: 'http://localhost:8080',  // 后端API地址
        changeOrigin: true                // 跨域
      }
    }
  }
}
```

### 环境变量

**.env.development (开发环境):**
```
VITE_API_BASE_URL=/api
```

**.env.production (生产环境):**
```
VITE_API_BASE_URL=https://your-api-domain.com/api
```

### 使用环境变量
```javascript
const baseURL = import.meta.env.VITE_API_BASE_URL
```

## 开发规范

### 目录规范
- **api/**: 按业务模块划分API文件，每个文件导出一个对象
- **views/**: 按功能模块划分页面，每个页面一个文件夹
- **mock/modules/**: Mock数据与API模块一一对应
- **store/modules/**: Store模块按功能划分

### 命名规范
- **文件名**: 小驼峰命名 (user.js, userTag.vue)
- **组件名**: 大驼峰命名 (UserTag, ContentList)
- **路由name**: 大驼峰命名 (User, ContentList)
- **API方法**: 小驼峰命名 (getUserList, addUser)

### 代码规范
- 使用ES6+语法
- 使用async/await处理异步
- 统一使用箭头函数
- API调用统一使用try-catch
- 组件使用Composition API（推荐）

### Git提交规范
- feat: 新功能
- fix: 修复bug
- docs: 文档更新
- style: 代码格式调整
- refactor: 重构
- test: 测试
- chore: 构建/工具变动

## 常见问题

### 1. 如何添加新的菜单页面？

**步骤：**
1. 在 `src/views/` 创建页面组件
2. 在 `src/router/index.js` 添加路由配置
3. 在 `src/api/` 创建对应的API模块
4. 在 `src/mock/modules/` 创建Mock数据（可选）

### 2. 如何对接真实后端API？

**步骤：**
1. 注释 `src/main.js` 中的 `import './mock'`
2. 修改 `.env.production` 中的 `VITE_API_BASE_URL`
3. 确保后端API返回格式符合前端约定：
```javascript
{
  code: 200,        // 状态码
  message: '成功',  // 提示信息
  data: {}          // 数据
}
```

### 3. Token过期如何处理？

系统已在 `src/api/request.js` 响应拦截器中处理：
- 后端返回 `code: 401` 时自动登出
- 清除本地Token和用户信息
- 刷新页面跳转到登录页

### 4. 如何实现按钮级权限控制？

**方案：**
1. 在路由meta中添加权限标识
2. 创建自定义指令 `v-permission`
3. 根据用户角色判断按钮显示/隐藏

### 5. 如何修改端口号？

修改 `vite.config.js` 中的 `server.port` 配置

### 6. 如何添加新的图标？

项目已注册所有Element Plus图标，直接使用：
```vue
<el-icon><User /></el-icon>
```

### 7. 如何调试Mock数据？

在浏览器控制台查看Network，Mock.js会拦截请求并返回模拟数据

## 功能统计

### 页面统计
- **总页面数**: 20个
- **系统管理**: 5个页面（用户、角色、菜单、部门、日志）
- **内容管理**: 3个页面（内容列表、审核、分类）
- **社区管理**: 3个页面（帖子、评论、Feed流）
- **互动数据**: 3个页面（点赞、收藏、分享）
- **其他页面**: 6个（首页、登录、404、用户标签等）

### API接口统计
- **API模块数**: 14个
- **接口总数**: 约80+个接口
- **请求方法**: GET、POST、PUT、DELETE

### 组件统计
- **布局组件**: 4个（Layout、Sidebar、Navbar、AppMain）
- **页面组件**: 20个
- **Element Plus组件**: 全量引入

### 代码统计
- **Vue文件**: 24个
- **JavaScript文件**: 30+个
- **Mock模块**: 14个
- **Store模块**: 3个

## 后续开发计划

### 功能增强
- [ ] 完善按钮级权限控制
- [ ] 添加数据导出功能（Excel）
- [ ] 集成图表组件（ECharts/Chart.js）
- [ ] 添加文件上传组件（图片、文档）
- [ ] 实现富文本编辑器（Quill/TinyMCE）
- [ ] 添加消息通知功能
- [ ] 实现实时数据推送（WebSocket）

### 用户体验
- [ ] 添加国际化支持（i18n）
- [ ] 优化移动端适配
- [ ] 添加主题切换功能（暗黑模式）
- [ ] 添加页面加载动画
- [ ] 优化表格性能（虚拟滚动）

### 技术优化
- [ ] 接入真实后端API
- [ ] 添加单元测试
- [ ] 添加E2E测试
- [ ] 优化打包体积
- [ ] 添加错误监控（Sentry）
- [ ] 性能监控和优化

### 安全增强
- [ ] 添加验证码功能
- [ ] 实现刷新Token机制
- [ ] 添加操作二次确认
- [ ] 敏感操作日志记录
- [ ] XSS和CSRF防护


## 相关文档

- [Vue 3 官方文档](https://cn.vuejs.org/)
- [Element Plus 官方文档](https://element-plus.org/zh-CN/)
- [Vite 官方文档](https://cn.vitejs.dev/)
- [Vue Router 官方文档](https://router.vuejs.org/zh/)
- [Vuex 官方文档](https://vuex.vuejs.org/zh/)
- [Axios 官方文档](https://axios-http.com/zh/)
- [Mock.js 官方文档](http://mockjs.com/)

## 技术支持

如有问题或建议，请联系开发团队或提交Issue。

## 更新日志

### v1.0.0 (2024)
- ✅ 完成基础框架搭建
- ✅ 实现7大功能模块
- ✅ 完成14个API模块
- ✅ 实现Mock数据模拟
- ✅ 完成路由守卫和权限控制
- ✅ 实现用户认证和状态管理

## License

MIT

---

**社区互动平台后台管理系统** - 基于Vue3的现代化管理平台
