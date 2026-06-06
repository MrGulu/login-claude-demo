## Context

目前，系统具备完整的用户、角色、岗位以及动态菜单权限管理，支持超级管理员角色 `root`。我们需要在此基础上新增公告管理模块，要求：
1. 具备 `root` 角色的用户能进行公告的管理（增删改查）。
2. 其他用户（如绑定了普通角色的用户）对公告仅拥有只读查看权限。
3. 菜单与“用户管理”菜单同级（即在“系统管理”目录下并列）。

## Goals / Non-Goals

**Goals:**
*   在数据库中新增 `sys_notice` 表。
*   后端提供公告管理与查询的分页 RESTful 接口，并使用 `@RequirePermission` 校验角色权限。
*   在数据库 `sys_menu` 表中注入“公告管理”菜单及按钮级权限（查询、新增、编辑、删除），与用户管理同级。
*   前端新增“公告管理”页面（组件路径为 `system/NoticeManagement.vue`），并在路由中配置。
*   前端通过 `v-permission` 或自定义权限逻辑控制敏感按钮（新增、编辑、删除）的显隐，实现“普通用户仅可查阅”。

**Non-Goals:**
*   不提供富文本编辑，直接使用多行输入框（TextArea）。
*   不支持已读/未读状态的记录，公告默认为全局所有人均可读。
*   不支持公告的发布范围限制。

## Decisions

### 1. 数据库设计

新增 `sys_notice` 表：
```sql
CREATE TABLE IF NOT EXISTS sys_notice (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    author VARCHAR(50) NOT NULL,
    status INTEGER DEFAULT 1,
    deleted INTEGER DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    create_by VARCHAR(50),
    update_by VARCHAR(50),
    remark VARCHAR(500)
);
```

### 2. 后端接口设计

提供如下 RESTful API：
*   `GET /api/admin/notices` (查询公告列表，带分页)
    *   所需权限: `system:notice:query`
*   `POST /api/admin/notices` (创建公告)
    *   所需权限: `system:notice:add`
*   `PUT /api/admin/notices/{id}` (更新公告)
    *   所需权限: `system:notice:edit`
*   `DELETE /api/admin/notices/{id}` (删除公告)
    *   所需权限: `system:notice:delete`

### 3. 数据表初始化 (SQL 数据)

为了使菜单和按钮权限生效，在 `sys_menu` 表中新增以下数据：
```sql
-- 公告管理菜单
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, sort, perms)
VALUES (23, 3, '公告管理', 'C', '/system/notices', 'system/NoticeManagement', 'Notification', 4, 'system:notice:view');

-- 按钮级权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, icon, sort, perms)
VALUES 
(24, 23, '查询公告', 'F', NULL, 1, 'system:notice:query'),
(25, 23, '新增公告', 'F', NULL, 2, 'system:notice:add'),
(26, 23, '编辑公告', 'F', NULL, 3, 'system:notice:edit'),
(27, 23, '删除公告', 'F', NULL, 4, 'system:notice:delete');
```

同时，为了保证 root 角色自动获得新权限：
```sql
-- 将新菜单和权限分配给 root 角色 (role_id = 1)
INSERT OR IGNORE INTO sys_role_menu (role_id, menu_id) VALUES (1, 23);
INSERT OR IGNORE INTO sys_role_menu (role_id, menu_id) VALUES (1, 24);
INSERT OR IGNORE INTO sys_role_menu (role_id, menu_id) VALUES (1, 25);
INSERT OR IGNORE INTO sys_role_menu (role_id, menu_id) VALUES (1, 26);
INSERT OR IGNORE INTO sys_role_menu (role_id, menu_id) VALUES (1, 27);
```

### 4. 前端组件与路由设计

*   **路由配置**:
    在 `src/router/index.js` 的 `DashboardLayout` 子路由中添加：
    ```javascript
    {
      path: '/system/notices',
      name: 'NoticeManagement',
      component: () => import('../views/system/NoticeManagement.vue'),
      meta: { requiresAuth: true }
    }
    ```
*   **侧边栏图标映射**:
    在 `Sidebar.vue` 中导入 `Notification` 并添加到 `iconMap` 里，以便侧边栏正确展示图标。
*   **新页面开发**:
    在 `src/views/system/NoticeManagement.vue` 开发列表展示及增删改，对写按钮使用 `v-permission="'system:notice:add'"` 等进行包围。

## Risks / Trade-offs

*   **[Risk] SQLite 主键自动递增及缓存不同步**
    *   **Mitigation**: 新建或修改菜单后，需要调用 `menuService.clearCache()` 以清除菜单的 JVM 级别双重锁缓存。
*   **[Risk] 非 root 角色通过接口绕过前端验证进行写操作**
    *   **Mitigation**: 在后端 Controller 使用 `@RequirePermission` 强权限防线拦截。非 root 角色的用户没有被赋予 `add`、`edit`、`delete` 权限时，其发起的写操作请求均会被 `PermissionInterceptor` 拦截。
