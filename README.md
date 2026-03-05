# Login System - 用户登录认证系统

一个完整的前后端分离的用户登录认证系统，采用现代化技术栈构建，提供用户管理、身份认证、密码重置等完整功能。

## 项目简介

本项目是一个企业级的用户登录认证系统，包含前端和后端两个独立的子项目：

- **login-boot**: Spring Boot后端服务，提供RESTful API
- **login-vue**: Vue 3前端应用，提供现代化的用户界面

## 技术栈

### 后端 (login-boot)
- Spring Boot 2.7.2
- MyBatis-Plus 3.5.5
- SQLite 3.43.0.0
- JWT 3.19.2
- Hutool 5.8.25
- Java 1.8

### 前端 (login-vue)
- Vue 3.4.21
- Vite 5.1.6
- Element Plus 2.6.0
- Vue Router 4.2.5
- Axios 1.13.6

## 项目结构

```
login-claude-demo/
├── login-boot/              # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/       # Java源代码
│   │   │   └── resources/  # 配置文件
│   │   └── test/           # 测试代码
│   ├── pom.xml             # Maven配置
│   └── README.md           # 后端文档
├── login-vue/              # 前端项目
│   ├── src/
│   │   ├── api/           # API接口
│   │   ├── assets/        # 静态资源
│   │   ├── components/    # 组件
│   │   ├── views/         # 页面
│   │   └── router/        # 路由
│   ├── package.json       # npm配置
│   └── README.md          # 前端文档
└── README.md              # 项目总览（本文件）
```

## 核心功能

### 用户认证
- ✅ 用户登录（用户名/邮箱/手机号）
- ✅ 图形验证码验证
- ✅ JWT Token认证
- ✅ 登录日志记录
- ✅ 自动登出

### 用户管理
- ✅ 用户注册
- ✅ 用户列表查询（分页）
- ✅ 用户信息编辑
- ✅ 用户状态管理
- ✅ 用户删除

### 密码管理
- ✅ 忘记密码
- ✅ 邮箱/短信验证码重置
- ✅ 修改密码
- ✅ 密码强度验证
- ✅ 密码重置日志

### 用户资料
- ✅ 查看个人信息
- ✅ 编辑个人资料
- ✅ 修改密码

### 系统管理
- ✅ 用户管理界面
- ✅ 仪表盘统计
- ✅ 活动动态

## 快速开始

### 环境要求

#### 后端
- JDK 1.8+
- Maven 3.6+

#### 前端
- Node.js 16.0+
- npm 7.0+ 或 yarn 1.22+

### 安装步骤

#### 1. 克隆项目

```bash
git clone <repository-url>
cd login-claude-demo
```

#### 2. 启动后端服务

```bash
cd login-boot

# 构建项目
mvn clean install

# 运行项目
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动。

#### 3. 启动前端应用

打开新的终端窗口：

```bash
cd login-vue

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端应用将在 `http://localhost:5173` 启动。

#### 4. 访问应用

打开浏览器访问：`http://localhost:5173`

默认管理员账号：
- 用户名：`admin`
- 密码：`admin123`

## 系统架构

### 架构图

```
┌─────────────────┐
│   浏览器客户端    │
└────────┬────────┘
         │ HTTP/HTTPS
         ↓
┌─────────────────┐
│   Vue 3 前端     │
│  (Port: 5173)   │
│  - Vue Router   │
│  - Element Plus │
│  - Axios        │
└────────┬────────┘
         │ REST API
         ↓
┌─────────────────┐
│ Spring Boot后端 │
│  (Port: 8080)   │
│  - JWT认证      │
│  - MyBatis-Plus │
└────────┬────────┘
         │ JDBC
         ↓
┌─────────────────┐
│  SQLite数据库   │
│  (./data/)      │
└─────────────────┘
```

### 技术架构

#### 后端架构
```
Controller层 (接口控制)
    ↓
Service层 (业务逻辑)
    ↓
Mapper层 (数据访问)
    ↓
Database (SQLite)
```

#### 前端架构
```
Views (页面视图)
    ↓
Components (组件)
    ↓
API (接口调用)
    ↓
Backend API
```

## API接口

### 认证接口
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出

### 用户管理接口
- `GET /api/users` - 查询用户列表
- `POST /api/users` - 创建用户
- `PUT /api/users/{id}` - 更新用户
- `DELETE /api/users/{id}` - 删除用户
- `PUT /api/users/{id}/status` - 更新用户状态

### 密码管理接口
- `POST /api/password/send-code` - 发送验证码
- `POST /api/password/verify-code` - 验证验证码
- `POST /api/password/reset` - 重置密码

### 用户资料接口
- `GET /api/profile` - 获取个人资料
- `PUT /api/profile` - 更新个人资料
- `PUT /api/profile/password` - 修改密码

### 验证码接口
- `GET /api/captcha` - 获取图形验证码

详细的API文档请参考：
- [后端API文档](./login-boot/README.md#api-接口文档)

## 数据库设计

### 主要数据表

#### users (用户表)
- id - 用户ID
- username - 用户名
- password - 密码（加密）
- email - 邮箱
- phone - 手机号
- real_name - 真实姓名
- status - 状态（0禁用/1启用）
- create_time - 创建时间
- update_time - 更新时间

#### login_log (登录日志表)
- id - 日志ID
- user_id - 用户ID
- login_time - 登录时间
- ip_address - IP地址
- device - 设备信息
- status - 登录状态

#### password_reset_log (密码重置日志表)
- id - 日志ID
- user_id - 用户ID
- reset_time - 重置时间
- reset_type - 重置方式（邮箱/短信）
- ip_address - IP地址

## 安全特性

### 密码安全
- ✅ BCrypt加密存储
- ✅ 密码强度验证
- ✅ 密码重置验证码

### 身份认证
- ✅ JWT Token无状态认证
- ✅ Token过期自动刷新
- ✅ 请求拦截器验证

### 接口安全
- ✅ CORS跨域配置
- ✅ 参数校验
- ✅ 全局异常处理
- ✅ SQL注入防护

### 日志审计
- ✅ 登录日志记录
- ✅ 密码重置日志
- ✅ 操作日志追踪

## 开发指南

### 后端开发

详细的后端开发指南请参考：[login-boot/README.md](./login-boot/README.md)

主要内容：
- 项目结构说明
- 添加新接口
- 异常处理
- 日志记录
- 测试编写

### 前端开发

详细的前端开发指南请参考：[login-vue/README.md](./login-vue/README.md)

主要内容：
- 项目结构说明
- 路由配置
- API调用
- 组件开发
- 样式定制

## 部署指南

### 后端部署

1. **打包项目**
```bash
cd login-boot
mvn clean package -DskipTests
```

2. **运行jar包**
```bash
java -jar target/login-boot-1.0.0.jar
```

3. **后台运行**
```bash
nohup java -jar target/login-boot-1.0.0.jar > app.log 2>&1 &
```

### 前端部署

1. **构建项目**
```bash
cd login-vue
npm run build
```

2. **部署到Nginx**
```nginx
server {
    listen 80;
    server_name example.com;
    root /path/to/dist;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### Docker部署

#### 后端Dockerfile
```dockerfile
FROM openjdk:8-jdk-alpine
WORKDIR /app
COPY target/login-boot-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### 前端Dockerfile
```dockerfile
FROM node:16-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

#### Docker Compose
```yaml
version: '3.8'
services:
  backend:
    build: ./login-boot
    ports:
      - "8080:8080"
    volumes:
      - ./data:/app/data

  frontend:
    build: ./login-vue
    ports:
      - "80:80"
    depends_on:
      - backend
```

## 测试

### 后端测试
```bash
cd login-boot
mvn test
```

### 前端测试
```bash
cd login-vue
npm run test
```

## 常见问题

### 1. 后端启动失败
- 检查JDK版本是否为1.8+
- 检查端口8080是否被占用
- 检查数据库文件路径是否有写权限

### 2. 前端无法连接后端
- 检查后端服务是否启动
- 检查vite.config.js中的代理配置
- 检查浏览器控制台的网络请求

### 3. 登录失败
- 检查用户名密码是否正确
- 检查验证码是否正确
- 查看后端日志排查问题

### 4. Token过期
- Token默认有效期为2小时
- 过期后会自动跳转到登录页
- 可在后端配置文件中修改过期时间

## 版本历史

### v1.0.0 (2024-03-05)
- ✅ 初始版本发布
- ✅ 实现用户认证功能
- ✅ 实现用户管理功能
- ✅ 实现密码管理功能
- ✅ 实现用户资料功能
- ✅ 实现仪表盘功能
- ✅ 前后端完整集成

## 贡献指南

欢迎提交Issue和Pull Request！

### 提交规范
```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 重构
test: 测试相关
chore: 构建/工具链相关
```

## 许可证

MIT License

## 联系方式

如有问题或建议，请提交Issue或Pull Request。

---

**注意**: 本项目仅供学习和演示使用，生产环境部署前请进行充分的安全评估和性能测试。
