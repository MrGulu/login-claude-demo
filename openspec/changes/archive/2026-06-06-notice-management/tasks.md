## 1. 数据库与菜单初始化

- [x] 1.1 创建 `sys_notice` 公告物理表
- [x] 1.2 在 `sys_menu` 表中插入“公告管理”菜单及按钮级操作权限数据
- [x] 1.3 为 `root` 角色分配对应的新菜单和按钮权限 (即写入 `sys_role_menu`)

## 2. 后端服务开发

- [x] 2.1 创建 `Notice` 实体类并声明 SQLite 表关联
- [x] 2.2 编写 `NoticeMapper`、`INoticeService` 及 `NoticeServiceImpl` 核心业务逻辑
- [x] 2.3 编写 `NoticeController` 提供 CRUD RESTful 接口
- [x] 2.4 在接口上添加 `@RequirePermission` 权限注解，拦截写操作

## 3. 前端页面与路由开发

- [x] 3.1 增加公告管理路由配置
- [x] 3.2 在侧边栏组件中映射 `Notification` 菜单图标
- [x] 3.3 创建前端接口调用文件 `src/api/notice.js`
- [x] 3.4 编写 `views/system/NoticeManagement.vue` 界面，实现数据展示与增删改弹框
- [x] 3.5 使用 `v-permission` 限制写操作按钮，确保普通用户仅有只读权限

## 4. 服务重启与功能验证

- [x] 4.1 清除后端菜单缓存，重启全栈服务
- [x] 4.2 使用 `admin` (root 角色) 登录系统，验证新增、编辑和删除公告
- [x] 4.3 新建一个普通用户并分配普通角色，验证其登录后能看到公告，但无增删改操作权限
