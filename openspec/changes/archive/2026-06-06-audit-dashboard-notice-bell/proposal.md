## Why

目前系统虽在后台默默记录了登录日志，但管理员缺乏直观的可视化数据看板进行访问审计和趋势分析。同时，新上线的公告系统对普通用户缺乏实时的顶栏消息提醒和已读/未读状态管理，导致系统通知无法获得即时反馈，用户体验较为孤立。

## What Changes

*   **首页图表大盘与登录日志审计**：
    *   在首页（`HomeView`）集成 ECharts，绘制最近 7 天的系统访问量折线图及浏览器分布图。
    *   新增“系统审计”子菜单（与用户管理同级），支持管理员分页模糊搜索和导出登录日志。
*   **顶栏消息铃铛与已读未读管理**：
    *   在右上角顶栏新增带红色 Badge 未读数的气泡铃铛。点击铃铛滑出通知抽屉，展示最新公告，并支持“一键已读”与“单条标为已读”。
    *   针对从未阅读过的公告，用户登录系统时弹窗强提醒。
    *   数据库中建立 `sys_notice_read` 已读状态表，通过接口支持多用户维度的已读/未读状态计算。

## Capabilities

### New Capabilities
- `login-audit-dashboard`: 登录日志的查询、审计管理，以及首页的数据分析图表展示。
- `notice-notification-hub`: 顶栏消息铃铛交互、滑出抽屉式通知列表、用户已读未读标记。

### Modified Capabilities
- `notice-management`: 补充在获取公告列表时加入针对当前登录用户的已读/未读标识字段返回。

## Impact

*   **数据库**:
    *   读取现有的 `sys_login_log` 日志表进行查询。
    *   新增 `sys_notice_read` 用户-公告已读关系表。
*   **后端 API**:
    *   提供 `/api/admin/login-logs` 登录日志分页接口。
    *   提供 `/api/admin/login-logs/statistics` 首页图表数据接口。
    *   提供 `/api/admin/notices/unread-count` 获取未读数接口。
    *   提供 `/api/admin/notices/read/{id}` 标为已读接口。
    *   提供 `/api/admin/notices/read-all` 全部标为已读接口。
*   **前端路由与界面**:
    *   更新 `src/router/index.js` 添加登录日志路由。
    *   更新 `Header.vue` 引入铃铛图标及未读数显示，加入 Drawer 抽屉。
    *   更新 `HomeView.vue` 引入 ECharts 并绘制动态图表。
