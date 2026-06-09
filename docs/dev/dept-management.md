---
doc_type: dev-guide
slug: dept-management
component: dept-management
status: current
summary: 部门管理与用户部门关联开发者指南，包含 API 接口参考、核心防环机制和前后端集成说明。
tags: [backend, frontend, system, department, api]
last_reviewed: 2026-06-09
---

# 部门管理与用户关联开发者指南

## 概述
本指南面向后端及前端集成开发者，用于指引如何快速上手、集成及维护系统的**部门管理与用户部门关联**模块。该模块提供树状组织架构（支持无限级子部门）的维护功能，并实现用户与所属部门的多对一关联。

## 前置依赖
在开发、测试或集成该功能前，需确认以下前置依赖已就绪：
- **数据库结构**：包含 `sys_department` 表，且 `sys_user` 表具有 `dept_id` 关联字段。详情参见 `data/schema.sql`。
- **用户模型支持**：后端实体类 `User` 须声明 `deptId`，`UserVO` 须具有 `deptId` 与 `deptName`。
- **路由及权限控制**：
  - 前端路由中已注册 `/system/departments` 页面。
  - 需要在数据库菜单表 `sys_menu` 中初始化以下权限标识：
    - `system:dept:query` （部门查询）
    - `system:dept:add` （部门新增）
    - `system:dept:edit` （部门编辑）
    - `system:dept:delete` （部门删除）

## 快速上手

### 1. 后端集成：获取部门树状列表接口
后端提供扁平的列表数据，由前端完成树状表格渲染或下拉树构造。
```java
// DepartmentController.java 示例
@GetMapping
@RequirePermission("system:dept:query")
public Result<List<Department>> getDepartmentList(DepartmentQueryDTO queryDTO) {
    List<Department> list = departmentService.getDepartmentList(queryDTO);
    return Result.success(list);
}
```

### 2. 前端集成：调用 API 并渲染
引入前端 API 定义，并在组件中发起调用：
```javascript
// 引入接口封装 (src/api/dept.js)
import { getDepartmentList } from '@/api/dept'

// 组件内加载方法
const fetchDeptList = async () => {
  try {
    const response = await getDepartmentList()
    if (response.code === 200) {
      // 获得扁平数组数据，Element Plus 表格可通过指定 row-key="id" 自动根据 parentId 渲染成树状结构
      deptData.value = response.data
    }
  } catch (error) {
    console.error('加载部门列表失败:', error)
  }
}
```

## 核心概念

### 1. 树状层级循环引用防护 (Anti-Loop Prevention)
为防止在修改部门父级时产生环状死锁（例如：部门 A 的父级设为部门 B，而部门 B 的父级又指向部门 A，或者指向其下的孙子部门），后端与前端共同落实了防环控制：
- **后端校验**：在 `IDepartmentService#updateDepartment` 事务中执行防环判定。如果拟更改的父部门 ID (`parentId`) 指向当前编辑部门自身，或者指向了当前编辑部门的子孙部门，直接抛出 `IllegalArgumentException` / 业务异常，拒绝更新。
- **前端置灰**：在编辑部门信息时，父部门下拉选择框（下拉树）会对当前编辑节点及其所有子孙节点进行 `disabled = true` 属性控制，阻止用户在界面上选中冲突节点。

### 2. 级联防御校验 (Cascading Rule Check)
系统设计遵循数据一致性纪律，严格防止出现脏关联数据：
- **子部门级联限制**：若部门存在下属子部门（`parentId` 为该部门 ID 的记录存在），该部门不可被删除。
- **用户级联限制**：若 `sys_user` 中仍有用户的 `dept_id` 指向该部门，该部门不可被删除。
- **状态联动限制**：当一个部门的状态被变更为 **“停用”** 时，不仅其自身在用户分配页面置灰，前端和后端也会在为用户分配部门时拦截该部门的选择（仅允许绑定启用状态的部门）。

## 接口参考

### 1. 获取部门列表
- **请求方法**：`GET`
- **路径**：`/api/admin/departments`
- **Query 参数** (`DepartmentQueryDTO`)：
  - `deptName` (String)：部门名称，支持模糊检索。
  - `status` (Integer)：部门状态，`0`-停用，`1`-正常。
- **响应体**：
  ```json
  {
    "code": 200,
    "message": "success",
    "data": [
      {
        "id": 1,
        "parentId": 0,
        "deptName": "总公司",
        "sort": 1,
        "status": 1,
        "remark": "集团总部",
        "createTime": "2026-06-09T08:00:00"
      }
    ]
  }
  ```

### 2. 创建部门
- **请求方法**：`POST`
- **路径**：`/api/admin/departments`
- **请求体** (`DepartmentDTO`)：
  ```json
  {
    "parentId": 1,
    "deptName": "研发中心",
    "sort": 2,
    "status": 1,
    "remark": "研发部门"
  }
  ```
- **响应体**：
  ```json
  {
    "code": 200,
    "message": "success",
    "data": 2  // 返回创建的部门主键 ID
  }
  ```

### 3. 修改部门
- **请求方法**：`PUT`
- **路径**：`/api/admin/departments/{id}`
- **请求体** (`DepartmentDTO`)：同创建部门结构。
- **响应体**：
  ```json
  {
    "code": 200,
    "message": "success",
    "data": null
  }
  ```

### 4. 删除部门
- **请求方法**：`DELETE`
- **路径**：`/api/admin/departments/{id}`
- **响应体（拦截失败示例）**：
  ```json
  {
    "code": 400,
    "message": "该部门下有绑定用户，无法删除",
    "data": null
  }
  ```

### 5. 快速修改部门状态
- **请求方法**：`PUT`
- **路径**：`/api/admin/departments/{id}/status`
- **请求体**：
  ```json
  {
    "status": 0
  }
  ```
- **响应体**：
  ```json
  {
    "code": 200,
    "message": "success",
    "data": null
  }
  ```

## 常见场景

### 1. 添加一个下级部门
下游应用需要添加新层级部门时，将 `parentId` 指定为对应父部门的 `id`，并将 `status` 设为 `1`。

### 2. 批量加载树形结构（前端常用）
直接调用 `GET /api/admin/departments` (不传 `status` 时默认返回所有部门)，然后在前端利用支持树形数组的数据工具进行转换，或者利用 `el-table` 的 `row-key` 属性自动在表格上展示树形级联。

### 3. 分配用户部门的后台合法性强校验
在创建用户或修改用户时，如果带上了 `deptId`，后端在 Service 内部必须查询该部门状态：
```java
Department dept = departmentMapper.selectById(deptId);
if (dept == null || dept.getStatus() == 0) {
    throw new BusinessException("指定的部门不存在或已被禁用");
}
```

## 已知限制与注意事项
- **单选限制**：目前系统仅支持用户关联单一部门。在前端实现和后端 DTO 中，`deptId` 均为单一数值而非数组，暂不支持多部门绑定。
- **层级排序**：部门列表默认按字段 `sort` 升序排列。在维护部门时，建议合理规划 `sort` 以达到理想的前端展示顺序。

## 相关文档
- **用户指南**：[user-guide/dept-management.md](../user/dept-management.md)
- **系统架构说明**：[.codestable/architecture/ARCHITECTURE.md](../../.codestable/architecture/ARCHITECTURE.md)
- **学习沉淀文档**：
  - [mockito-injectmocks-npe.md](../../.codestable/compound/2026-06-09-learning-mockito-injectmocks-npe.md)
  - [el-tree-select-antiloop.md](../../.codestable/compound/2026-06-09-learning-el-tree-select-antiloop.md)
