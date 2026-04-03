# Login Vue - Python Django 后端

本项目是基于现有 `login-boot` (Spring Boot 版本) 1:1 重构的 Python Django 经典 MVC Web 后端系统。为了完全替代原来 Spring 版提供的所有 JSON 序列化功能，在此应用中使用了原生 JSON 处理与免外键（`managed=False`） 的 ORM 实现，无缝对接原有的 `login-vue` 前端。

## 技术栈
- 框架：**Django 4.2+** 
- REST 支持库：**Django REST framework (DRF)** / 基于基础 JSON views
- 安全与认证框架：**bcrypt**, 自定义 `PyJWT`，完全仿照 Spring Boot Interceptor 开发了自定义 `@login_required`
- CORS 解决模块：**django-cors-headers**

## 目录及安装指南

推荐使用 `Python 3.9+`。本工程未启用任何额外数据库系统或 Redis，直接读取并生成 SQLite 数据。

1. **装载虚拟依赖环境**
   ```bash
   python -m venv venv
   # Windows 启用环境
   .\venv\Scripts\activate
   # Linux 环境下
   source venv/bin/activate
   ```

2. **下载相关库包并构建项目**
   ```bash
   pip install -r requirements.txt
   ```

3. **数据库连接绑定**
   Django 并未使用 `python manage.py migrate` 传统形式创建此库，在原 Spring 逻辑实现中我们已在此目录下创建了一组直接映射自 `../data/schema.sql` 的 `../data/login.db` 并在 manage.py 的 `main()` 中加入了自动检查并执行初始化脚本逻辑。

## 启动调试

执行 Django 开发服务：
```bash
python manage.py runserver 0.0.0.0:8080
```
启动参数中 **0.0.0.0:8080** 确保在其他机器网络上的所有地址绑定；本服务占用端口 **`http://localhost:8080`**.

## 映射对接前端项目
如要搭配 `login-vue` 这个 Vue / Vite/ Vue CLI 模板项目时，您只需更新代理服务网关的配置接口（一般为 `vite.config.js`），保证发送至 `/api/*` 的链接目标改变：
```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

> [!WARNING]
> 由于所有 Python 后端 (Flask/FastAPI/Django) 现在都统一配置为 8080 端口，请确保一次只启动其中一个服务，以避免端口冲突。

## 功能完成项总结
- [x] JWT 认证：生成 / 注册鉴权接口 (`/api/auth/*`)
- [x] 数学验证码生成器，带自动去噪音模块：不含 Redis 等扩展依赖，基于本地基于内存缓存模拟 (`/api/captcha/generate`) 
- [x] 动态路由与树形菜单列表的组合拉取功能 (`/api/menus/*`)
- [x] 管理控制台全部增删查改配置，基于 `sys_user`, `sys_role`, `sys_position`, `sys_menu` 等的复杂关系操作绑定与展示。
