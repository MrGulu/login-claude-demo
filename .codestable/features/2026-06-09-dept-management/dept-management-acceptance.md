# 部门管理与用户部门关联 验收报告

> 阶段：阶段 3（验收闭环）  
> 验收日期：2026-06-09  
> 关联方案 doc：[dept-management-design.md](file:///c:/workspace/login-claude-demo/.codestable/features/2026-06-09-dept-management/dept-management-design.md)  

---

## 1. 接口契约核对

对照方案第 2.1 节名词层逐一核查：

**接口示例逐项核对**：
- [x] 1. 获取部门列表（`GET /api/admin/departments`）：为了契合前端拦截器的规范，API 加了 `/admin` 统一前缀。实测该接口返回了所有扁平的部门，供前端构建树状表格。
- [x] 2. 创建部门（`POST /api/admin/departments`）：入参包含 `parentId`, `deptName`, `sort`, `status`, `remark`，创建成功返回生成的主键 ID。
- [x] 3. 删除部门（`DELETE /api/admin/departments/{id}`）：当存在子部门或有关联用户时，接口抛出 400 Bad Request 和对应的中文拦截信息，与示例设计完全一致。

**名词层"现状 → 变化"核对**：
- [x] 实体 `Department`：对应 `sys_department` 表，属性包括层级关系、基本字段及 MyBatis-Plus 逻辑删除与自动审计字段。符合名词设计。
- [x] 实体 `User` & 视图 `UserVO`：成功增加了 `deptId` 及 `deptName` 属性。

**流程图核对**：
- [x] 流程图中各节点（如在 `createUser`/`updateUser` 中注入并查验 `DepartmentMapper` 以校验部门合法性及启用状态）已在 `UserManagementServiceImpl.java` 中实现并进行了单元测试覆盖。

---

## 2. 行为与决策核对

对照方案第 1 节 + 第 2.2 节：

**需求摘要逐项验证**：
- [x] 部门管理 CRUD 自主可维护。 -> 实测正常
- [x] 部门停用时，在用户分配该部门的下拉框中置灰。 -> 实测正常，前端利用 `<el-tree-select>` 的 `disabled` 属性完美控制
- [x] 用户列表显示“所属部门”。 -> 实测正常，用户表格已新加所属部门一列。

**明确不做逐项核对**：
- [x] **数据权限过滤**：代码中仅用于基本关联展示，未加入任何针对部门的数据过滤隔离 SQL。
- [x] **单选模式限制**：前端编辑弹框中，部门下拉树没有开启 `multiple`，为严格的单选模式。

**关键决策落地**：
- [x] **决策 1：菜单位置与层级**：部门管理以系统管理的二级菜单形式并列放置，并在 `schema.sql` 中合理指定其 `sort = 4` 排在岗位管理下方，同时顺延更新了公告和日志菜单的 sort。
- [x] **决策 2：用户与部门多对一关系**：直接在 `sys_user` 插入 `dept_id`。
- [x] **决策 3：级联删除校验**：删除逻辑中阻断包含子部门及包含用户的部门。

**编排层现状 → 变化核对**：
- [x] 在新建或编辑用户前，在后台执行对 `deptId` 状态的检查，若停用或不存在则跑出 400 并提示“指定的部门不存在或已被禁用”。

**流程级约束核对**：
- [x] 树形循环引用防御：在 `DepartmentServiceImpl` 的 `updateDepartment` 方法中调用 `isChildDepartment` 进行检查，若父级指向自己或子孙部门，则抛出异常，测试已覆盖该场景。

**挂载点反向核对（可卸载性）**：
- [x] 挂载点 M1：`data/schema.sql` 执行成功。
- [x] 挂载点 M2：`router/index.js` 注册路由成功。
- [x] 挂载点 M3：`api/dept.js` 封装成功。
- [x] 挂载点 M4：`DeptManagement.vue` 和 `UserManagement.vue` 的界面实现成功。
- [x] **反向核查**：经 Grep，本功能代码没有任何清单外的外部依赖挂接，拔除沙盘推演可完全干净拔除。

---

## 3. 验收场景核对

### 场景 1：部门列表与树形展现
- 证据来源：自动化浏览器子代理截图 `dept_list.png`
- 结果：通过。列表以展开的树形表格形式正确渲染。
![部门列表树形展示](file:///C:/Users/twlma/.gemini/antigravity-ide/brain/0f743c8c-5e90-45d3-9ab6-bc3d0e1a0b8f/dept_list_1780988665045.png)

### 场景 2：存在子部门删除拦截
- 证据来源：浏览器子代理执行删除确认弹窗截图 `delete_dept_failed_with_children.png`
- 结果：通过。在对“总公司”执行删除时，页面成功捕获并拦截提示“存在子部门，无法删除”。
![存在子部门删除拦截](file:///C:/Users/twlma/.gemini/antigravity-ide/brain/0f743c8c-5e90-45d3-9ab6-bc3d0e1a0b8f/delete_dept_failed_with_children_1780988926611.png)

### 场景 3：用户列表部门名称回显
- 证据来源：浏览器子代理截图 `user_list_with_dept.png`
- 结果：通过。列表显示所属部门列，admin 回显总公司，user 回显前端组。
![用户列表部门回显](file:///C:/Users/twlma/.gemini/antigravity-ide/brain/0f743c8c-5e90-45d3-9ab6-bc3d0e1a0b8f/user_list_with_dept_1780988944078.png)

---

## 4. 术语一致性
- **部门 (Department)**：在实体类 `Department`、表名 `sys_department`、Controller 路由 `/api/admin/departments` 中完全一致。
- **关联 (deptId)**：在 `sys_user` 及 `sys_user` 的字段、DTO 参数、VO 字段中均使用相同的 `deptId` / `dept_id` / `deptName` 命名体系，无命名冲突。

---

## 5. 架构归并
- [x] 架构总览 [ARCHITECTURE.md](file:///c:/workspace/login-claude-demo/.codestable/architecture/ARCHITECTURE.md)：已在“2. 核心概念”、“3. 子系统索引”和“5. 已知约束”中补充了部门实体与防循环引用/级联删除的架构纪律说明。

---

## 6. requirement 回写
- [x] 由于设计文档中 `requirement` 字段为空但新增了用户可感功能，已触发 `cs-req backfill` 在 [.codestable/requirements/dept-management.md](file:///c:/workspace/login-claude-demo/.codestable/requirements/dept-management.md) 建立了需求能力愿景归档，并将状态记为 `current`。

---

## 7. roadmap 回写
- [x] 非 roadmap 起头，跳过。

---

## 8. attention.md 候选盘点
- [x] 本 feature 未暴露需要补入 `attention.md` 的内容（在重置并删除 login.db 重启后端时能顺利加载 schema 并生成库，无隐藏环境坑）。

---

## 9. 遗留
- **实现阶段“顺手发现”列表**：
  - 顺手发现：在 `UserManagementServiceTest.java` 中，原测试类漏掉了 `UserRoleMapper`, `RoleMapper`, `UserPositionMapper`, `PositionMapper` 的 Mock 注入定义。由于此前某些变更使 delete 等方法开始校验角色，导致在单测独立运行时原有的 delete 用例会发生 NullPointerException。
  - 处理结果：已在本次变更中一并修复，对这些依赖的 Mapper 补齐了 Mock 声明，所有测试已绿灯通过。
