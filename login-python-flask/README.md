# Login Vue - Python Flask 后端

本项目是基于现有 `login-boot` (Spring Boot 版本) 1:1 重构的 Python Flask 后端系统。旨在提供与 Java 版本完全一致的接口和业务逻辑，可无缝对接原有的 `login-vue` 前端。

## 技术栈
- 框架：**Flask 2.x**
- ORM：**Flask-SQLAlchemy**
- 密码学：**bcrypt** (兼容原版 Spring Boot 的 `$2a$` 哈希规则)
- 身份验证：**PyJWT** (基于 JWT)
- 数据流校验：与原有 Spring Boot VO/DTO 和 Result JSON 结构保持一致

## 环境与安装

推荐使用 `Python 3.8+`。

1. **创建并进入虚拟环境（可选）**
   ```bash
   python -m venv venv
   # Windows
   .\venv\Scripts\activate
   # macOS/Linux
   source venv/bin/activate
   ```

2. **安装依赖**
   ```bash
   pip install -r requirements.txt
   ```

3. **数据库初始化**
   本项目底层使用 SQLite。首次启动入口 `app.py` 时，会自动读取 `./data/schema.sql` 并生成 `../data/login.db` 库。这与原有 Spring Boot 行为一致。

## 运行项目

使用以下命令启动服务：
```bash
python app.py
```
服务默认运行在 **`http://localhost:8080`**。

## 前端对接指南
如要使用 `login-vue` 此版本后端联调，只需将 `login-vue` 项目目录的配置文件例如 `vite.config.js` 中的代理 target 端口更改为 `8080`：
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

## 功能完成度 (1:1 对齐)
- [x] JWT 认证 (`/api/auth/*`)
- [x] 数学计算验证码生成 (`/api/captcha/generate`) 
- [x] 动态路由与树形菜单列表 (`/api/menus/*`)
- [x] 用户个人中心 (资料/头像/修改密码) (`/api/user/*`)
- [x] 用户系统管理 (CRUD / 修改状态 / 角色分配 / 岗位分配)
- [x] 角色及菜单权限绑定设置
- [x] 岗位字典管理
