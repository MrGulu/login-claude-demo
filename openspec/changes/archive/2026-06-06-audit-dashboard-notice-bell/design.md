## Context

系统在当前拥有 RBAC 的架构上运作。现需要在该体系中并行引入两大功能：
1. **方案一（数据大盘与系统审计）**：引入 ECharts 库在首页展示登录频次折线图和浏览器占比饼图，并在后台展示 `sys_login_log` 记录。
2. **方案三（消息通知铃铛与已读未读管理）**：在右上角顶栏提供消息铃铛和 Badge，点击滑出未读消息抽屉，支持标为已读，且在用户登录时若有最新未读公告则进行强弹窗提醒。

## Goals / Non-Goals

**Goals:**
*   在 SQLite 数据库中创建 `sys_notice_read` 用户-公告已读关系表。
*   在 `sys_menu` 中注入“登录日志”菜单（层级与用户管理并列），并将权限分配给 root 角色。
*   后端提供登录日志的分页列表 API，以及用于首页渲染的 7 天登录统计、浏览器分布统计 API。
*   后端为公告模块提供未读统计接口，以及单条标为已读、一键已读接口。
*   前端在 package.json 中引入 `echarts` 依赖。
*   前端重构 `HomeView.vue` 引入折线图与饼图；重构 `Header.vue` 引入铃铛与滑出抽屉；新建 `views/system/LoginLogManagement.vue` 提供审计查询。

**Non-Goals:**
*   公告不按角色或部门进行分群已读统计，只以全局正常态公告作全用户维度的未读排查。
*   登录日志接口不进行跨省 IP 地理位置的第三方库高精度反查，直接保存请求包附带的 IP。

## Decisions

### 1. 数据库建表

```sql
-- 1. 创建用户公告已读表
CREATE TABLE IF NOT EXISTS sys_notice_read (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    notice_id INTEGER NOT NULL,
    read_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX IF NOT EXISTS idx_user_notice_read ON sys_notice_read(user_id, notice_id);

-- 2. 插入登录日志菜单 (ID = 28)
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, sort, perms)
VALUES (28, 3, '登录日志', 'C', '/system/login-logs', 'system/LoginLogManagement', 'Document', 5, 'system:login-log:view');

-- 3. 登录日志按钮权限 (ID = 29)
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, icon, sort, perms)
VALUES (29, 28, '查询日志', 'F', NULL, 1, 'system:login-log:query');

-- 4. 关联 root 角色
INSERT OR IGNORE INTO sys_role_menu (role_id, menu_id) VALUES (1, 28);
INSERT OR IGNORE INTO sys_role_menu (role_id, menu_id) VALUES (1, 29);
```

### 2. 接口设计契约

#### A. 登录日志审计与统计 (方案一)
*   `GET /api/admin/login-logs` (分页获取登录日志):
    *   出参: `Result<Page<LoginLog>>`
    *   权限: `system:login-log:query`
*   `GET /api/admin/login-logs/statistics` (首页 ECharts 统计):
    *   出参: 包含 7 天折线图数据（日期与频次）和浏览器占比的统计数据结构。
    *   权限: 免鉴权，所有已登录用户在首页渲染需要该数据。

#### B. 已读未读通知管理 (方案三)
*   `GET /api/admin/notices/unread-count` (当前登录用户的未读公告数):
    *   出参: `Result<Integer>`
*   `POST /api/admin/notices/read/{id}` (将单条公告标为已读):
    *   出参: `Result<Void>`
*   `POST /api/admin/notices/read-all` (一键已读全部):
    *   出参: `Result<Void>`

---

## 子代理开发拆分方案 (Subagent Execution Plan)

考虑到方案一和方案三具有较强的独立性，我们采用 **Subagent-Driven Development** 思想分发给两个模拟的子代理协同：

```
                              ┌────────────────────────┐
                              │  主代理 (Antigravity)  │
                              └────────────────────────┘
                                   │              │
                    派发子代理 A ───┘              └─── 派发子代理 B
                    (看板与日志模块)                    (消息中心模块)
                    ┌──────────────┐              ┌──────────────┐
                    │  子代理 A    │              │  子代理 B    │
                    └──────────────┘              └──────────────┘
                     - 登录日志后端                  - 已读表结构与SQL
                     - 登录日志Controller            - 未读数/已读API后端
                     - 安装 ECharts                 - 顶栏铃铛+Badge
                     - 首页大盘图表化                - 滑出 Drawer 抽屉
                     - 审计日志前端管理页             - 首屏公告弹窗提醒
```

在执行期间，两组任务会在 tasks 中单独划开，并独立验证和提交。
