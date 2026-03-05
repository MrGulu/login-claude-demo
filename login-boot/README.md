# Login Boot - 用户登录系统后端

基于 Spring Boot 的用户登录认证系统后端服务，提供完整的用户管理、身份认证、密码重置等功能。

## 技术栈

### 核心框架
- **Spring Boot** 2.7.2 - 应用框架
- **Spring Web** - RESTful API
- **Spring Validation** - 参数校验

### 数据层
- **MyBatis-Plus** 3.5.5 - ORM框架，简化数据库操作
- **SQLite** 3.43.0.0 - 轻量级嵌入式数据库

### 安全认证
- **JWT** (java-jwt 3.19.2) - 无状态身份认证
- **BCrypt** - 密码加密

### 工具库
- **Hutool** 5.8.25 - Java工具类库
- **Fastjson** 1.2.83 - JSON处理
- **Lombok** - 简化Java代码

### 测试
- **JUnit 5** - 单元测试框架
- **Mockito** - Mock测试框架
- **Spring Boot Test** - 集成测试

## 项目架构

```
login-boot
├── src/main/java/com/demo/login
│   ├── common                    # 公共模块
│   │   ├── enums                # 枚举类（结果码等）
│   │   ├── exception            # 异常处理
│   │   ├── result               # 统一响应结果
│   │   └── utils                # 工具类
│   ├── config                   # 配置类
│   │   ├── CorsConfig          # 跨域配置
│   │   ├── JacksonConfig       # JSON配置
│   │   ├── MyBatisPlusConfig   # MyBatis-Plus配置
│   │   ├── RedisConfig         # Redis配置
│   │   └── WebMvcConfig        # Web MVC配置
│   ├── controller              # 控制器层
│   │   ├── AuthController      # 认证控制器
│   │   ├── CaptchaController   # 验证码控制器
│   │   ├── PasswordResetController  # 密码重置控制器
│   │   ├── UserManagementController # 用户管理控制器
│   │   └── UserProfileController    # 用户资料控制器
│   ├── dto                     # 数据传输对象
│   ├── entity                  # 实体类
│   │   ├── User               # 用户实体
│   │   ├── LoginLog           # 登录日志
│   │   └── PasswordResetLog   # 密码重置日志
│   ├── interceptor            # 拦截器
│   │   └── JwtInterceptor     # JWT拦截器
│   ├── mapper                 # 数据访问层
│   ├── service                # 业务逻辑层
│   │   ├── IAuthService       # 认证服务接口
│   │   ├── ICaptchaService    # 验证码服务接口
│   │   ├── IEmailService      # 邮件服务接口
│   │   ├── ISmsService        # 短信服务接口
│   │   ├── IPasswordResetService  # 密码重置服务接口
│   │   ├── IUserManagementService # 用户管理服务接口
│   │   ├── IUserProfileService    # 用户资料服务接口
│   │   └── impl               # 服务实现类
│   ├── util                   # 工具类
│   │   └── DatabaseInitializer # 数据库初始化
│   └── vo                     # 视图对象
├── src/main/resources
│   ├── application.yml        # 主配置文件
│   ├── application-dev.yml    # 开发环境配置
│   └── db/schema.sql          # 数据库表结构
└── src/test/java              # 测试代码
```

## 核心功能

### 1. 用户认证
- 用户登录（用户名/邮箱/手机号）
- 图形验证码验证
- JWT Token生成与验证
- 登录日志记录

### 2. 用户管理
- 用户注册
- 用户信息查询（分页）
- 用户信息更新
- 用户状态管理（启用/禁用）
- 用户删除

### 3. 密码管理
- 密码重置（邮箱/短信验证码）
- 密码修改
- 旧密码验证
- 密码重置日志记录

### 4. 用户资料
- 查看个人资料
- 更新个人资料
- 修改密码

### 5. 验证码服务
- 图形验证码生成
- 邮箱验证码发送
- 短信验证码发送
- 验证码校验

## 快速开始

### 环境要求
- JDK 1.8+
- Maven 3.6+

### 安装步骤

1. **克隆项目**
```bash
git clone <repository-url>
cd login-boot
```

2. **配置文件**

编辑 `src/main/resources/application-dev.yml` 配置数据库和其他参数：

```yaml
server:
  port: 8080

spring:
  datasource:
    driver-class-name: org.sqlite.JDBC
    url: jdbc:sqlite:./data/login.db

jwt:
  secret: your-secret-key
  expire: 7200
```

3. **构建项目**
```bash
mvn clean install
```

4. **运行项目**
```bash
mvn spring-boot:run
```

或者直接运行主类：
```bash
java -jar target/login-boot-1.0.0.jar
```

5. **访问应用**
```
http://localhost:8080
```

### 数据库初始化

项目启动时会自动执行 `src/main/resources/db/schema.sql` 创建数据库表结构。

默认管理员账号：
- 用户名：`admin`
- 密码：`admin123`

## API 接口文档

### 认证接口

#### 用户登录
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123",
  "captchaKey": "uuid",
  "captchaCode": "1234"
}
```

#### 用户登出
```http
POST /api/auth/logout
Authorization: Bearer <token>
```

### 用户管理接口

#### 创建用户
```http
POST /api/users
Authorization: Bearer <token>
Content-Type: application/json

{
  "username": "newuser",
  "password": "password123",
  "email": "user@example.com",
  "phone": "13800138000",
  "realName": "张三"
}
```

#### 查询用户列表
```http
GET /api/users?page=1&size=10&keyword=admin
Authorization: Bearer <token>
```

#### 更新用户信息
```http
PUT /api/users/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "email": "newemail@example.com",
  "phone": "13900139000",
  "realName": "李四"
}
```

#### 更新用户状态
```http
PUT /api/users/{id}/status
Authorization: Bearer <token>
Content-Type: application/json

{
  "status": 1
}
```

#### 删除用户
```http
DELETE /api/users/{id}
Authorization: Bearer <token>
```

### 密码管理接口

#### 发送验证码
```http
POST /api/password/send-code
Content-Type: application/json

{
  "type": "email",
  "target": "user@example.com"
}
```

#### 验证验证码
```http
POST /api/password/verify-code
Content-Type: application/json

{
  "type": "email",
  "target": "user@example.com",
  "code": "123456"
}
```

#### 重置密码
```http
POST /api/password/reset
Content-Type: application/json

{
  "type": "email",
  "target": "user@example.com",
  "code": "123456",
  "newPassword": "newpassword123"
}
```

### 用户资料接口

#### 获取个人资料
```http
GET /api/profile
Authorization: Bearer <token>
```

#### 更新个人资料
```http
PUT /api/profile
Authorization: Bearer <token>
Content-Type: application/json

{
  "email": "newemail@example.com",
  "phone": "13900139000",
  "realName": "王五"
}
```

#### 修改密码
```http
PUT /api/profile/password
Authorization: Bearer <token>
Content-Type: application/json

{
  "oldPassword": "oldpassword123",
  "newPassword": "newpassword123"
}
```

### 验证码接口

#### 获取图形验证码
```http
GET /api/captcha
```

响应：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "key": "uuid",
    "image": "base64-encoded-image"
  }
}
```

## 统一响应格式

所有接口返回统一的JSON格式：

### 成功响应
```json
{
  "code": 200,
  "message": "success",
  "data": {
    // 响应数据
  }
}
```

### 错误响应
```json
{
  "code": 400,
  "message": "错误信息",
  "data": null
}
```

### 响应码说明
- `200` - 成功
- `400` - 请求参数错误
- `401` - 未授权
- `403` - 无权限
- `404` - 资源不存在
- `500` - 服务器内部错误

## 安全特性

### 密码安全
- 使用 BCrypt 算法加密存储密码
- 密码强度验证（长度、复杂度）
- 密码重置需要验证码验证

### 身份认证
- JWT Token 无状态认证
- Token 过期时间配置（默认2小时）
- 请求拦截器自动验证Token

### 接口安全
- CORS 跨域配置
- 参数校验（@Valid注解）
- 全局异常处理
- SQL注入防护（MyBatis-Plus参数化查询）

### 日志审计
- 登录日志记录（时间、IP、设备）
- 密码重置日志记录
- 操作日志追踪

## 配置说明

### JWT配置
```yaml
jwt:
  secret: your-secret-key    # JWT密钥
  expire: 7200               # 过期时间（秒）
  header: Authorization      # Token请求头
```

### 数据库配置
```yaml
spring:
  datasource:
    driver-class-name: org.sqlite.JDBC
    url: jdbc:sqlite:./data/login.db
```

### MyBatis-Plus配置
```yaml
mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  type-aliases-package: com.demo.login.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: assign_id
      logic-delete-field: deleted
```

## 测试

### 运行单元测试
```bash
mvn test
```

### 运行指定测试类
```bash
mvn test -Dtest=AuthServiceTest
```

### 测试覆盖率
```bash
mvn clean test jacoco:report
```

## 开发指南

### 添加新接口

1. **创建DTO**（`dto`包）
```java
@Data
public class NewFeatureDTO {
    @NotBlank(message = "字段不能为空")
    private String field;
}
```

2. **创建Service接口**（`service`包）
```java
public interface INewFeatureService {
    Result<Void> doSomething(NewFeatureDTO dto);
}
```

3. **实现Service**（`service.impl`包）
```java
@Service
public class NewFeatureServiceImpl implements INewFeatureService {
    @Override
    public Result<Void> doSomething(NewFeatureDTO dto) {
        // 业务逻辑
        return Result.success();
    }
}
```

4. **创建Controller**（`controller`包）
```java
@RestController
@RequestMapping("/api/feature")
public class NewFeatureController {
    @Autowired
    private INewFeatureService service;

    @PostMapping
    public Result<Void> create(@Valid @RequestBody NewFeatureDTO dto) {
        return service.doSomething(dto);
    }
}
```

### 异常处理

使用自定义业务异常：
```java
throw new BusinessException(ResultCode.PARAM_ERROR);
```

全局异常处理器会自动捕获并返回统一格式。

### 日志记录

使用Lombok的`@Slf4j`注解：
```java
@Slf4j
@Service
public class SomeService {
    public void doSomething() {
        log.info("执行某操作");
        log.error("发生错误", exception);
    }
}
```

## 部署

### 打包
```bash
mvn clean package -DskipTests
```

生成的jar包位于 `target/login-boot-1.0.0.jar`

### 运行
```bash
java -jar target/login-boot-1.0.0.jar
```

### 指定配置文件
```bash
java -jar target/login-boot-1.0.0.jar --spring.profiles.active=prod
```

### 后台运行
```bash
nohup java -jar target/login-boot-1.0.0.jar > app.log 2>&1 &
```

## 常见问题

### 1. 数据库连接失败
检查 `application.yml` 中的数据库配置是否正确，确保数据库文件路径存在。

### 2. JWT Token验证失败
- 检查Token是否过期
- 检查请求头格式：`Authorization: Bearer <token>`
- 检查JWT密钥配置是否一致

### 3. 跨域问题
已配置CORS，如仍有问题，检查 `CorsConfig` 配置。

### 4. 端口被占用
修改 `application.yml` 中的 `server.port` 配置。

## 版本历史

### v1.0.0 (2024-03-05)
- 初始版本发布
- 实现用户认证功能
- 实现用户管理功能
- 实现密码管理功能
- 实现验证码功能

## 许可证

MIT License

## 联系方式

如有问题或建议，请提交Issue或Pull Request。
