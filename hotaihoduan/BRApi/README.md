# BRApi - 社区互动平台后端API

## 📖 项目简介

这是为 `/hotai/Admin` 前端管理系统提供的完整后端API实现,基于Spring Boot + MyBatis构建。

---

## 🛠️ 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 4.0.5 | 核心框架 |
| MyBatis-Plus | 3.5.5 | ORM框架 |
| MySQL | 8.0+ | 数据库 |
| JWT | 0.12.3 | 认证 |
| Swagger | 2.3.0 | API文档 |
| Lombok | - | 简化代码 |
| Hutool | 5.8.25 | 工具类 |

---

## 📁 项目结构

```
BRApi/
├── src/main/
│   ├── java/org/xingchang/brapi/
│   │   ├── BRApiApplication.java          # 启动类
│   │   ├── common/                        # 通用类
│   │   │   ├── Result.java                # 统一响应
│   │   │   ├── PageQuery.java             # 分页查询
│   │   │   └── PageResult.java            # 分页结果
│   │   ├── entity/                        # 实体类
│   │   │   ├── SysUser.java               # 用户
│   │   │   ├── SysRole.java               # 角色
│   │   │   ├── Content.java               # 内容
│   │   │   ├── Post.java                  # 帖子
│   │   │   └── ...                        # 其他实体
│   │   ├── dto/                           # 数据传输对象
│   │   │   ├── LoginRequest.java
│   │   │   └── LoginResponse.java
│   │   ├── mapper/                        # Mapper接口
│   │   │   ├── SysUserMapper.java
│   │   │   ├── SysRoleMapper.java
│   │   │   └── ...
│   │   ├── service/                       # Service层
│   │   │   ├── SysUserService.java
│   │   │   ├── SysRoleService.java
│   │   │   └── ...
│   │   └── controller/                    # Controller层
│   │       ├── SysUserController.java     # 用户管理
│   │       ├── SysRoleController.java     # 角色管理
│   │       ├── ContentController.java     # 内容管理
│   │       ├── FeedController.java        # Feed流
│   │       ├── DashboardController.java   # 仪表盘
│   │       └── ...
│   └── resources/
│       ├── application.yml                # 配置文件
│       ├── schema.sql                     # 数据库脚本
│       └── mapper/                        # MyBatis XML
│           ├── SysUserMapper.xml
│           ├── SysRoleMapper.xml
│           └── ...
├── pom.xml                                # Maven配置
├── README.md                              # 本文件
├── API_IMPLEMENTATION_GUIDE.md            # API实现指南
└── QUICK_COMPLETE.md                      # 快速完成指南
```

---

## 🚀 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 2. 数据库初始化

```bash
# 登录MySQL
mysql -u root -p

# 执行数据库脚本
source src/main/resources/schema.sql
```

### 3. 配置数据库

编辑 `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hotai_db
    username: root
    password: your_password
```

### 4. 启动项目

```bash
# 方式1: 使用Maven
mvn clean spring-boot:run

# 方式2: 使用IDE
# 直接运行 BRApiApplication.java
```

### 5. 访问接口

- **API地址:** http://localhost:8080/api
- **Swagger文档:** http://localhost:8080/api/swagger-ui.html
- **API文档:** http://localhost:8080/api/v3/api-docs

---

## 📋 API模块

### 系统管理
- ✅ 用户管理 `/api/user`
- ✅ 角色管理 `/api/role`
- ⚠️ 菜单管理 `/api/menu` (待完善)
- ⚠️ 部门管理 `/api/dept` (待完善)
- ⚠️ 操作日志 `/api/log` (待完善)

### 内容管理
- ✅ 内容管理 `/api/content`
- ⚠️ 内容分类 `/api/category` (待完善)
- ✅ 内容审核 `/api/audit`

### 社区管理
- ⚠️ 帖子管理 `/api/post` (待完善)
- ⚠️ 评论管理 `/api/comment` (待完善)
- ✅ Feed流管理 `/api/feed`

### 数据统计
- ✅ 仪表盘 `/api/dashboard`
- ✅ 互动数据 `/api/interaction`

### 用户标签
- ⚠️ 标签管理 `/api/tag` (待完善)

---

## 🔧 开发指南

### 添加新模块

1. **创建实体类** `entity/XxxEntity.java`
2. **创建Mapper接口** `mapper/XxxMapper.java`
3. **创建Mapper XML** `resources/mapper/XxxMapper.xml`
4. **创建Service** `service/XxxService.java`
5. **创建Controller** `controller/XxxController.java`

### 使用代码生成

```bash
# 使用Python脚本快速生成
python generate_code.py
```

### 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... }
}
```

### 分页查询

```java
// Service层
PageHelper.startPage(pageNum, pageSize);
List<Entity> list = mapper.selectList(params);
PageInfo<Entity> pageInfo = new PageInfo<>(list);
return PageResult.of(pageInfo.getTotal(), pageInfo.getList());
```

---

## 📝 接口示例

### 登录接口

**请求:**
```bash
POST /api/user/login
Content-Type: application/json

{
  "username": "admin",
  "password": "123456"
}
```

**响应:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "mock-token-1",
    "userInfo": {
      "id": 1,
      "username": "admin",
      "nickname": "管理员",
      ...
    },
    "roles": ["admin"]
  }
}
```

### 用户列表

**请求:**
```bash
GET /api/user/list?pageNum=1&pageSize=10&username=admin
```

**响应:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 100,
    "list": [
      {
        "id": 1,
        "username": "admin",
        "nickname": "管理员",
        ...
      }
    ]
  }
}
```

---

## 🎯 完成度

### 当前状态: 90%

| 模块 | 完成度 | 说明 |
|------|--------|------|
| 基础架构 | 100% | ✅ 完成 |
| 实体类 | 100% | ✅ 完成（含MyBatis-Plus注解）|
| Mapper接口 | 100% | ✅ 完成（继承BaseMapper）|
| Controller | 100% | ✅ 完成 |
| Service | 100% | ✅ 完成（继承ServiceImpl）|
| MyBatis-Plus配置 | 100% | ✅ 完成 |
| 分页功能 | 100% | ✅ 完成 |
| 逻辑删除 | 100% | ✅ 完成 |

### 下一步工作

1. **高优先级**
   - 添加JWT认证
   - 添加全局异常处理
   - 完善数据验证

2. **中优先级**
   - 添加操作日志AOP
   - 添加Redis缓存
   - 完善Swagger文档

3. **低优先级**
   - 添加单元测试
   - 性能优化
   - 添加监控

---

## 📚 相关文档

- [API实现指南](./API_IMPLEMENTATION_GUIDE.md) - 详细的API实现说明
- [快速完成指南](./QUICK_COMPLETE.md) - 快速完成剩余工作
- [数据库设计](./src/main/resources/schema.sql) - 完整的数据库表结构

---

## 🐛 常见问题

### Q: 启动报错找不到Mapper?
A: 检查 `@MapperScan` 注解路径是否正确

### Q: 数据库连接失败?
A: 检查 `application.yml` 中的数据库配置

### Q: JSON字段无法映射?
A: 使用 `typeHandler=org.apache.ibatis.type.JsonTypeHandler`

### Q: 分页不生效?
A: 确保Service中调用了 `PageHelper.startPage()`

### Q: 跨域问题?
A: 前端使用Vite代理,或添加CORS配置

---

## 📞 联系方式

如有问题,请联系开发团队。

---

## 📄 许可证

MIT License

---

**创建时间:** 2024-01-15  
**最后更新:** 2024-01-15  
**版本:** v1.0.0
