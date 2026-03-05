# Login Vue - 用户登录系统前端

基于 Vue 3 + Vite + Element Plus 的现代化用户登录认证系统前端应用，采用玻璃态射主义设计风格。

## 技术栈

### 核心框架
- **Vue 3.4.21** - 渐进式JavaScript框架
- **Vite 5.1.6** - 下一代前端构建工具
- **Vue Router 4.2.5** - 官方路由管理器

### UI组件库
- **Element Plus 2.6.0** - 基于Vue 3的组件库
- **unplugin-vue-components** - 组件自动导入
- **unplugin-auto-import** - API自动导入

### HTTP客户端
- **Axios 1.13.6** - Promise based HTTP client

### 开发工具
- **@vitejs/plugin-vue** - Vite的Vue插件
- **Element Plus Resolver** - Element Plus组件解析器

## 项目结构

```
login-vue
├── public/                    # 静态资源
├── src/
│   ├── api/                  # API接口
│   │   ├── auth.js          # 认证接口
│   │   ├── captcha.js       # 验证码接口
│   │   ├── password.js      # 密码管理接口
│   │   ├── user.js          # 用户资料接口
│   │   └── userManagement.js # 用户管理接口
│   ├── assets/              # 资源文件
│   │   └── styles/          # 样式文件
│   │       ├── main.css     # 全局样式
│   │       ├── element-theme.css  # Element主题
│   │       └── message-override.css # 消息样式覆盖
│   ├── components/          # 公共组件
│   │   ├── Background.vue   # 背景组件
│   │   ├── BrandSection.vue # 品牌区域
│   │   ├── ErrorToast.vue   # 错误提示
│   │   ├── ForgotPasswordDialog.vue # 忘记密码对话框
│   │   ├── Header.vue       # 头部组件
│   │   ├── LoginForm.vue    # 登录表单
│   │   ├── Sidebar.vue      # 侧边栏
│   │   ├── SocialLogin.vue  # 社交登录
│   │   ├── SuccessToast.vue # 成功提示
│   │   └── dashboard/       # 仪表盘组件
│   │       ├── ActivityFeed.vue    # 活动动态
│   │       ├── QuickActions.vue    # 快捷操作
│   │       ├── StatsCard.vue       # 统计卡片
│   │       └── WelcomeBanner.vue   # 欢迎横幅
│   ├── composables/         # 组合式函数
│   │   └── useUserInfo.js   # 用户信息hook
│   ├── layouts/             # 布局组件
│   │   └── DashboardLayout.vue # 仪表盘布局
│   ├── router/              # 路由配置
│   │   └── index.js         # 路由定义
│   ├── utils/               # 工具函数
│   │   └── toast.js         # 提示工具
│   ├── views/               # 页面视图
│   │   ├── HomeView.vue     # 首页
│   │   ├── LoginView.vue    # 登录页
│   │   ├── ProfileView.vue  # 个人资料页
│   │   └── system/          # 系统管理
│   │       └── UserManagement.vue # 用户管理页
│   ├── App.vue              # 根组件
│   └── main.js              # 入口文件
├── index.html               # HTML模板
├── package.json             # 项目配置
└── vite.config.js           # Vite配置
```

## 核心功能

### 1. 用户认证
- 用户登录（支持用户名/邮箱/手机号）
- 图形验证码验证
- 记住登录状态
- 自动登出（Token过期）
- 登录状态持久化

### 2. 密码管理
- 忘记密码
- 邮箱/短信验证码重置
- 修改密码
- 密码强度验证

### 3. 用户资料
- 查看个人信息
- 编辑个人资料
- 头像上传
- 修改密码

### 4. 用户管理（管理员）
- 用户列表查询
- 用户信息编辑
- 用户状态管理
- 用户删除
- 创建新用户

### 5. 仪表盘
- 欢迎横幅
- 统计卡片
- 快捷操作
- 活动动态

## 设计特点

### 1. 玻璃态射效果（Glassmorphism）
- 毛玻璃背景（backdrop-filter: blur）
- 半透明容器
- 光晕边框
- 柔和阴影

### 2. 对角线分割布局
- 左侧品牌展示区（55%宽度，对角线裁切）
- 右侧登录表单（480px宽度，悬浮卡片）
- 响应式适配

### 3. 动态背景
- 3个浮动渐变光球
- 移动网格图案
- 鼠标视差效果
- 平滑动画过渡

### 4. 交互动画
- 页面加载时的渐入动画
- 输入框聚焦时的缩放和阴影效果
- 按钮悬停时的光效扫过动画
- 鼠标移动时的视差效果

### 5. 红金配色主题
- 主色：#dc2626（红色）
- 强调色：#f59e0b（金色）
- 喜庆氛围设计

## 快速开始

### 环境要求
- Node.js 16.0+
- npm 7.0+ 或 yarn 1.22+

### 安装步骤

1. **克隆项目**
```bash
git clone <repository-url>
cd login-vue
```

2. **安装依赖**
```bash
npm install
# 或
yarn install
```

3. **配置后端API地址**

编辑 `vite.config.js` 中的代理配置：

```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',  // 后端API地址
      changeOrigin: true
    }
  }
}
```

4. **启动开发服务器**
```bash
npm run dev
# 或
yarn dev
```

5. **访问应用**
```
http://localhost:5173
```

默认登录账号：
- 用户名：`admin`
- 密码：`admin123`

### 构建生产版本

```bash
npm run build
# 或
yarn build
```

构建产物位于 `dist/` 目录。

### 预览生产构建

```bash
npm run preview
# 或
yarn preview
```

## 开发指南

### 路由配置

路由定义在 `src/router/index.js`：

```javascript
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue')
  },
  {
    path: '/',
    component: DashboardLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/HomeView.vue')
      }
    ]
  }
]
```

### API调用

所有API接口封装在 `src/api/` 目录：

```javascript
// src/api/auth.js
import axios from 'axios'

export const login = (data) => {
  return axios.post('/api/auth/login', data)
}

export const logout = () => {
  return axios.post('/api/auth/logout')
}
```

使用示例：

```javascript
import { login } from '@/api/auth'

const handleLogin = async () => {
  try {
    const response = await login({
      username: 'admin',
      password: 'admin123',
      captchaKey: 'uuid',
      captchaCode: '1234'
    })
    console.log(response.data)
  } catch (error) {
    console.error(error)
  }
}
```

### 组件自动导入

项目配置了组件自动导入，Element Plus组件无需手动导入：

```vue
<template>
  <!-- 直接使用，无需import -->
  <el-button type="primary">按钮</el-button>
  <el-input v-model="value" />
</template>
```

### 状态管理

使用组合式API管理状态，例如 `src/composables/useUserInfo.js`：

```javascript
import { ref } from 'vue'

const userInfo = ref(null)

export function useUserInfo() {
  const setUserInfo = (info) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  const getUserInfo = () => {
    if (!userInfo.value) {
      const stored = localStorage.getItem('userInfo')
      if (stored) {
        userInfo.value = JSON.parse(stored)
      }
    }
    return userInfo.value
  }

  return {
    userInfo,
    setUserInfo,
    getUserInfo
  }
}
```

### 路由守卫

在 `src/router/index.js` 中配置路由守卫：

```javascript
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/')
  } else {
    next()
  }
})
```

### 样式定制

#### 全局样式
在 `src/assets/styles/main.css` 中定义全局样式和CSS变量：

```css
:root {
  --color-primary: #dc2626;
  --color-accent: #f59e0b;
  --color-bg: #0f172a;
  --color-surface: rgba(255, 255, 255, 0.05);
}
```

#### Element Plus主题
在 `src/assets/styles/element-theme.css` 中自定义Element Plus主题。

#### 组件样式
使用scoped样式避免污染：

```vue
<style scoped>
.my-component {
  color: #333;
}
</style>
```

### 添加新页面

1. **创建视图组件**（`src/views/`）
```vue
<!-- src/views/NewPage.vue -->
<template>
  <div class="new-page">
    <h1>新页面</h1>
  </div>
</template>

<script setup>
// 页面逻辑
</script>

<style scoped>
.new-page {
  padding: 20px;
}
</style>
```

2. **添加路由**（`src/router/index.js`）
```javascript
{
  path: '/new-page',
  name: 'NewPage',
  component: () => import('@/views/NewPage.vue'),
  meta: { requiresAuth: true }
}
```

3. **添加导航**（如需要）
```vue
<el-menu-item index="/new-page">
  <span>新页面</span>
</el-menu-item>
```

### 添加新API

1. **创建API文件**（`src/api/`）
```javascript
// src/api/newFeature.js
import axios from 'axios'

export const getList = (params) => {
  return axios.get('/api/feature/list', { params })
}

export const create = (data) => {
  return axios.post('/api/feature', data)
}

export const update = (id, data) => {
  return axios.put(`/api/feature/${id}`, data)
}

export const remove = (id) => {
  return axios.delete(`/api/feature/${id}`)
}
```

2. **在组件中使用**
```vue
<script setup>
import { getList, create } from '@/api/newFeature'

const fetchData = async () => {
  const response = await getList({ page: 1, size: 10 })
  console.log(response.data)
}
</script>
```

## 配置说明

### Vite配置

`vite.config.js` 主要配置项：

```javascript
export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      resolvers: [ElementPlusResolver()],
    }),
  ],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')  // 路径别名
    }
  },
  server: {
    port: 5173,           // 开发服务器端口
    open: true,           // 自动打开浏览器
    proxy: {              // API代理
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

### 环境变量

创建 `.env` 文件配置环境变量：

```env
# .env.development
VITE_API_BASE_URL=http://localhost:8080

# .env.production
VITE_API_BASE_URL=https://api.example.com
```

使用环境变量：

```javascript
const apiBaseUrl = import.meta.env.VITE_API_BASE_URL
```

## 部署

### 静态部署

1. **构建项目**
```bash
npm run build
```

2. **部署dist目录**

将 `dist/` 目录部署到静态服务器（Nginx、Apache等）。

### Nginx配置示例

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

创建 `Dockerfile`：

```dockerfile
FROM node:16-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

构建并运行：

```bash
docker build -t login-vue .
docker run -p 80:80 login-vue
```

## 浏览器支持

- Chrome >= 87
- Firefox >= 78
- Safari >= 14
- Edge >= 88

注意：需要支持 CSS backdrop-filter 属性的现代浏览器。

## 性能优化

### 代码分割
使用动态导入实现路由懒加载：

```javascript
{
  path: '/profile',
  component: () => import('@/views/ProfileView.vue')
}
```

### 组件按需导入
已配置 `unplugin-vue-components` 自动按需导入Element Plus组件。

### 构建优化
Vite默认进行了以下优化：
- Tree-shaking
- 代码压缩
- CSS提取
- 资源哈希

## 常见问题

### 1. 开发服务器启动失败
- 检查端口5173是否被占用
- 检查Node.js版本是否符合要求
- 删除 `node_modules` 重新安装依赖

### 2. API请求失败
- 检查后端服务是否启动
- 检查 `vite.config.js` 中的代理配置
- 检查浏览器控制台的网络请求

### 3. 组件样式不生效
- 检查是否正确导入样式文件
- 检查CSS选择器优先级
- 使用浏览器开发工具检查元素样式

### 4. 路由跳转404
- 检查路由配置是否正确
- 生产环境需要配置服务器重定向到index.html

### 5. Token过期处理
在axios拦截器中统一处理：

```javascript
axios.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      router.push('/login')
    }
    return Promise.reject(error)
  }
)
```

## 开发规范

### 命名规范
- 组件名：PascalCase（如 `UserProfile.vue`）
- 文件名：kebab-case（如 `user-profile.js`）
- 变量名：camelCase（如 `userName`）
- 常量名：UPPER_SNAKE_CASE（如 `API_BASE_URL`）

### 代码风格
- 使用2空格缩进
- 使用单引号
- 组件使用 `<script setup>` 语法
- 优先使用组合式API

### Git提交规范
```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 重构
test: 测试相关
chore: 构建/工具链相关
```

## 版本历史

### v1.0.0 (2024-03-05)
- 初始版本发布
- 实现用户登录功能
- 实现用户资料管理
- 实现密码重置功能
- 实现用户管理功能
- 实现仪表盘页面
- 玻璃态射主义设计风格

## 许可证

MIT License

## 联系方式

如有问题或建议，请提交Issue或Pull Request。
