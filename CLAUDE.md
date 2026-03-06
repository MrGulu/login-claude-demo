# Login System - 全栈登录系统

这是一个前后端分离的登录系统项目，包含 Spring Boot 后端和 Vue 前端。

## 项目结构

```
login-claude-demo/
├── login-boot/          # Spring Boot 后端项目
│   ├── src/            # Java 源代码
│   ├── data/           # 数据文件
│   ├── pom.xml         # Maven 配置
│   └── README.md       # 后端文档
│
├── login-vue/          # Vue 前端项目
│   ├── src/            # Vue 源代码
│   ├── public/         # 静态资源
│   ├── package.json    # npm 配置
│   ├── vite.config.js  # Vite 构建配置
│   └── README.md       # 前端文档
│
└── CLAUDE.md          # 本文件
```

## 技术栈

### 后端 (login-boot)
- **框架**: Spring Boot
- **构建工具**: Maven
- **语言**: Java
- **数据库**: SQLite
- **ORM**: MyBatis Plus

### 前端 (login-vue)
- **框架**: Vue.js
- **构建工具**: Vite
- **包管理**: npm
- **UI 组件库**: Element Plus

## 服务管理

### 启动服务
1. 后端: `cd login-boot && mvn spring-boot:run`
2. 前端: `cd login-vue && npm run dev`

### 停止服务
- 删除 SQLite 数据库前务必先停止后端服务，避免文件锁定
- 使用 Ctrl+C 或通过 PID 终止进程

### 数据库重置
```bash
rm login-boot/data/login.db
# 然后重启后端以重新初始化数据库
```

## 开发规范

### 开发流程（重要）
**开始任何开发工作前，必须先阅读相关模块的 README.md：**
- 后端开发：先读 `login-boot/README.md`
- 前端开发：先读 `login-vue/README.md`
- 全栈功能：两个 README 都要读
- README 中包含 API 接口定义、架构说明、已有功能等关键信息
- **读取后必须简要总结关键信息，确认理解后再开始编码**

### 代码风格
- 后端遵循 Java 标准命名规范（驼峰命名）
- 前端遵循 Vue 官方风格指南
- 保持代码简洁，避免过度工程化

### 代码质量与验证
- 始终验证 Vue3 组合式 API 中的 async/await 使用
- 前端 API 响应：axios 拦截器已处理，直接访问 `response.code` 和 `response.data`，不是 `response.data.code`
- 提交认证相关代码前运行类型检查
- 彻底测试 ID 生成策略的变更（SQLite getGeneratedKeys 配合 MyBatis Plus）
- 重构后验证导入路径的正确性

### API 接口
- 前后端通过 RESTful API 通信
- 接口文档请参考各子项目的 README.md

### Git 提交
- 提交信息使用中文描述
- 格式：`[模块] 简短描述`
- 例如：`[后端] 添加用户登录接口`、`[前端] 优化登录页面样式`

## 常用命令

### 后端开发
```bash
cd login-boot
mvn spring-boot:run        # 启动后端服务
mvn clean package          # 打包
```

### 前端开发
```bash
cd login-vue
npm install                # 安装依赖
npm run dev                # 启动开发服务器
npm run build              # 构建生产版本
```

### UI 实现规则
- 当用户请求 modal/dialog/popup 时，首先明确具体行为：
  - 全屏覆盖页面（路由导航）
  - 模态对话框（停留在当前页面）
  - 弹出层覆盖（teleport 组件）
- 匹配现有主题色彩和毛玻璃设计风格
- 测试 CSS 变更时清除浏览器缓存（Ctrl+Shift+R）

## 后端开发

### 后端配置
- 使用 SQLite 配合 MyBatis Plus（不使用 Redis，无外部依赖）
- ID 生成：`@TableId(type = IdType.ASSIGN_ID)` - SQLite 不支持 getGeneratedKeys，必须使用雪花算法
- 验证码校验：仅使用内存存储
- Maven 必须在 PATH 中以支持 CLI 启动服务
- 修改实体类后必须重启后端：`taskkill //F //PID <pid> && mvn spring-boot:run`

## 注意事项

1. 修改功能时通常需要同时修改前后端代码
2. 前端调用后端 API 时注意跨域配置
3. 敏感信息（密码、密钥等）不要提交到代码库
4. 代码编写遵循最小化原则，只实现必要的功能
5. Claude Code hooks 必须在非拦截情况下输出原始数据（`process.stdout.write(data)`），否则工具调用失败
