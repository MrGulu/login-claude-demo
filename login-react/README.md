# Login React - React 版本登录系统

这是 login-vue 的 React 版本复刻，实现了完整的前后端分离登录管理系统。

## 技术栈

| 类别 | 技术选型 |
|------|---------|
| 框架 | React 18.3.1 + Vite 5.1.6 |
| 语言 | TypeScript 5.3.3 |
| 路由 | React Router v6.22.0 |
| UI 库 | Ant Design 5.12.0 |
| HTTP | Axios 1.6.7 |
| 状态管理 | Context API + Hooks |
| 样式 | Sass + CSS-in-JS |

## 功能特性

### 认证模块
- 用户登录（验证码支持）
- 记住密码功能
- 密码找回
- 登出功能

### 用户管理
- 用户列表展示
- 新增/编辑用户
- 删除用户
- 用户状态切换
- 角色分配
- 岗位分配

### 角色管理
- 角色列表展示
- 新增/编辑角色
- 删除角色
- 权限树展示

### 岗位管理
- 岗位列表展示
- 新增/编辑岗位
- 删除岗位
- 岗位状态切换

### 个人资料
- 用户信息展示
- 个人资料编辑
- 头像上传
- 密码修改

### 仪表盘
- 欢迎横幅
- 统计卡片
- 快捷操作
- 活动动态

## 项目结构

```
login-react/
├── public/
├── src/
│   ├── api/              # API 接口定义
│   │   ├── index.ts       # Axios 实例和拦截器
│   │   ├── auth.ts        # 认证接口
│   │   ├── captcha.ts      # 验证码接口
│       ├── password.ts     # 密码管理接口
│   │   ├── user.ts        # 用户资料接口
│   │   ├── userManagement.ts # 用户管理接口
│   │   ├── role.ts        # 角色管理接口
│   │   ├── position.ts     # 岗位管理接口
│   │   └── menu.ts        # 菜单接口
│   ├── assets/styles/    # 全局样式
│   │   ├── main.scss       # CSS 变量和重置样式
│   │   └── animations.scss # 关键帧动画
│   ├── components/        # 公共组件
│   │   ├── Background.tsx
│   │   ├── BrandSection.tsx
│   │   ├── LoginForm.tsx
│   │   ├── SocialLogin.tsx
│   │   ├── ForgotPasswordDialog.tsx
│   │   ├── Header.tsx
│   │   └── Sidebar.tsx
│   ├── contexts/          # 状态管理
│   │   ├── AuthContext.tsx
│   │   └── UserContext.tsx
│   ├── hooks/            # 自定义 Hooks
│   │   ├── useAuth.ts
│   │   ├── useUserInfo.ts
│   │   └── usePermission.ts
│   ├── layouts/          # 布局组件
│   │   └── DashboardLayout.tsx
│   ├── pages/            # 页面组件
│   │   ├── LoginPage.tsx
│   │   ├── HomePage.tsx
│   │   ├── ProfilePage.tsx
│   │   └── system/
│   │       ├── UserManagementPage.tsx
│   │       ├── RoleManagementPage.tsx
│   │       └── PositionManagementPage.tsx
│   ├── router/           # 路由配置
│   │   └── index.tsx
│   ├── types/            # 类型定义
│   │   ├── api.ts
│   │   └── user.ts
│   ├── App.tsx           # 根组件
│   └── main.tsx          # 入口文件
├── index.html
├── package.json
├── tsconfig.json
├── tsconfig.node.json
├── tsconfig.app.json
└── vite.config.ts
```

## 安装和启动

### 环境要求
- Node.js >= 18.0.0
- npm >= 9.0.0

### 安装依赖
```bash
cd login-react
npm install
```

### 启动开发服务器
```bash
cd login-react
npm run dev
```
访问 http://localhost:5174

### 构建生产版本
```bash
npm run build
```

## API 接口说明

后端服务运行在 `http://localhost:8080`，所有 API 请求通过 `/api` 前缀代理。

### 认证接口
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出
- `GET /api/auth/userInfo` - 获取用户信息
- `POST /api/auth/refreshToken` - 刷新 Token

### 验证码接口
- `GET /api/captcha` - 获取验证码图片

### 密码管理接口
- `POST /api/password/sendCode` - 发送验证码
- `POST /api/password/verify` - 验证验证码
- `POST /api/password/reset` - 重置密码

### 用户管理接口
- `GET /api/user/list` - 获取用户列表
- `GET /api/user/detail/{id}` - 获取用户详情
- `POST /api/user/create` - 创建用户
- `PUT /api/user/update/{id}` - 更新用户
- `DELETE /api/user/delete/{id}` - 删除用户
- `PUT /api/user/status/{id}` - 更新用户状态
- `GET /api/user/roles/{id}` - 获取用户角色
- `POST /api/user/assignRoles` - 分配角色
- `GET /api/user/positions/{id}` - 获取用户岗位
- `POST /api/user/assignPositions` - 分配岗位

### 角色管理接口
- `GET /api/role/list` - 获取角色列表
- `GET /api/role/detail/{id}` - 获取角色详情
- `POST /api/role/create` - 创建角色
- `PUT /api/role/update/{id}` - 更新角色
- `DELETE /api/role/delete/{id}` - 删除角色
- `GET /api/role/menus` - 获取菜单树
- `POST /api/role/assignMenus` - 分配菜单

### 岗位管理接口
- `GET /api/position/list` - 获取岗位列表
- `GET /api/position/detail/{id}` - 获取岗位详情
- `POST /api/position/create` - 创建岗位
- `PUT /api/position/update/{id}` - 更新岗位
- `DELETE /api/position/delete/{id}` - 删除岗位
- `PUT /api/position/status/{id}` - 更新岗位状态

## 开发指南

### 路径别名
使用 `@` 作为 `src/` 的别名：
```typescript
import { login } from '@/api/auth'
import MyComponent from '@/components/MyComponent'
```

### 状态管理
使用 Context API 管理全局状态：
```typescript
import { useAuth } from '@/hooks/useAuth'
import { useUserInfo } from '@/hooks/useUserInfo'

const { token, updateToken, clearToken } = useAuth()
const { userInfo, setUserInfo } = useUserInfo()
```

### 样式
- 使用 Sass 编写样式
- CSS 变量定义在 `main.scss` 中
- 组件内联样式使用 CSS-in-JS

### 代码规范
- 使用 TypeScript 类型
- 函数组件 + Hooks 模式
- 遵循 React 最佳实践

## 部署说明

### 生产环境配置
1. 修改 `vite.config.ts` 中的 API 代理地址
2. 运行 `npm run build`
3. 部署 `dist/` 目录到静态服务器

### 环境变量
创建 `.env` 文件配置环境变量：
```env
VITE_API_BASE_URL=http://your-api-server.com
```

## 常见问题

### Q: 路径别名不生效？
A: 确保 `vite.config.ts` 和 `tsconfig.app.json` 中正确配置了路径别名。

### Q: API 请求失败？
A: 检查后端服务是否启动（端口 8080），确认代理配置正确。

### Q: 样式不生效？
A: 清除浏览器缓存（Ctrl+Shift+R），确保样式文件正确导入。

### Q: 如何调试？
A: 使用浏览器开发者工具查看网络请求和组件渲染，检查控制台错误。

## 许可证

MIT License

## 联系

如有问题或建议，请提交 Issue 或 Pull Request。
