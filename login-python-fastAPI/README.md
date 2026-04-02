# Login Vue - Python FastAPI 后端

本项目是基于现有 `login-boot` (Spring Boot 版本) 1:1 重构的 Python FastAPI 高性能异步 / 同步 API 后端系统。旨在提供与 Java 版本完全一致的接口和业务逻辑，并利用 Pydantic 和 FastAPI 自动生成强类型的架构系统对接原有的 `login-vue` 前端。

## 技术栈
- 框架：**FastAPI** (\+ Uvicorn ASGI Server)
- 数据库驱动：**SQLAlchemy 2.0+**
- 模型验证：**Pydantic**
- 密码学：**passlib (Bcrypt)**
- 身份验证：**PyJWT** (基于 HTTPBearer)

## 环境与安装

推荐使用 `Python 3.9+`。

1. **环境初始化**
   ```bash
   python -m venv venv
   # Windows
   .\venv\Scripts\activate
   ```

2. **安装依赖**
   ```bash
   pip install -r requirements.txt
   ```

3. **初始化数据库**
   FastAPI 配置使用了 `startup_event` 事件，在第一次启动引擎时：
   - 如果不存在 SQLite 数据库。会自动读取 `./data/schema.sql` 结构文件创建名为 `login.db` 的数据库实体。

## 运行项目

执行 Uvicorn 命令以开发模式热加载运行：
```bash
uvicorn main:app --host 0.0.0.0 --port 8080 --reload
```
服务将在 **`http://localhost:8080`** 获取响应。
另外值得注意的是：FastAPI 提供免费对接的后端接口文档（虽然与现有 Vue项目无关，但便于调试）：可以通过访问 `http://localhost:8080/docs` (Swagger UI) 或者 `http://localhost:8080/redoc` 进行开发调试。

## 联合调试配置
在前端 `login-vue` 的 `vite.config.js` 或者 `vue.config.js` 文件中执行代理变更，将代理端口映射到 8080 即可使用此后台。
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

## 功能清单
项目内部的层级与实现方案已完美替换 Spring Controller 对应的 APIRouter，业务模型 Pydantic DTO，依赖注入 (`Depends(get_current_user)`) 取代了原始 Java Interceptors。
- [x] JWT 认证 (`/api/auth/*`)
- [x] 数学计算验证码生成 (`/api/captcha/generate`) 
- [x] 动态路由与树形菜单列表 (`/api/menus/*`)
- [x] 用户中心 (修改资料/密码修改) (`/api/user/*`)
- [x] 系统管理员模块管理操作 (CRUD / 岗位 / 角色授权分配等)  
