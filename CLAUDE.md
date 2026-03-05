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

### 前端 (login-vue)
- **框架**: Vue.js
- **构建工具**: Vite
- **包管理**: npm

## 开发规范

### 代码风格
- 后端遵循 Java 标准命名规范（驼峰命名）
- 前端遵循 Vue 官方风格指南
- 保持代码简洁，避免过度工程化

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

## 注意事项

1. 修改功能时通常需要同时修改前后端代码
2. 前端调用后端 API 时注意跨域配置
3. 敏感信息（密码、密钥等）不要提交到代码库
4. 代码编写遵循最小化原则，只实现必要的功能
