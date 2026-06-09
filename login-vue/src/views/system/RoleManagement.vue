<template>
  <div class="role-management">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">角色管理</h1>
      <p class="page-subtitle">管理系统角色与权限分配</p>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item>
          <el-input
            v-model="searchForm.roleName"
            placeholder="搜索角色名称"
            clearable
            prefix-icon="Search"
            class="search-input"
          />
        </el-form-item>
        <el-form-item>
          <el-select
            v-model="searchForm.status"
            placeholder="状态筛选"
            clearable
            class="search-select"
          >
            <el-option label="全部状态" :value="null" />
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" class="search-btn">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset" class="reset-btn">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 操作栏 -->
    <div class="toolbar">
      <el-button v-permission="'system:role:add'" type="primary" :icon="Plus" @click="handleAdd" class="add-btn">
        新增角色
      </el-button>
      <div class="toolbar-info">
        共 <span class="count">{{ pagination.total }}</span> 条记录
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <el-table
        :data="tableData"
        v-loading="loading"
        class="role-table"
        header-row-class-name="table-header"
        row-class-name="table-row"
      >
        <el-table-column prop="roleName" label="角色名称" width="160">
          <template #default="{ row }">
            <div class="role-cell">
              <el-icon class="role-icon"><UserFilled /></el-icon>
              <span>{{ row.roleName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="roleKey" label="角色标识" width="160" />
        <el-table-column label="系统角色" width="120" align="center">
          <template #default="{ row }">
            <el-tag
              v-if="row.isSystem === 1"
              type="danger"
              effect="light"
              class="status-tag"
            >
              系统角色
            </el-tag>
            <el-tag
              v-else
              type="info"
              effect="light"
              class="status-tag"
            >
              普通角色
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag
              v-if="row.status === 1"
              type="success"
              effect="light"
              class="status-tag"
            >
              正常
            </el-tag>
            <el-tag
              v-else
              type="danger"
              effect="light"
              class="status-tag"
            >
              禁用
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" align="center" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="80" fixed="right" align="center">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-dropdown trigger="click" @command="(cmd) => handleDropdownCommand(cmd, row)" placement="bottom-end">
                <el-button size="small" class="action-menu-btn" circle>
                  <el-icon class="action-icon"><Operation /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu class="action-dropdown">
                    <el-dropdown-item
                      v-if="hasPermission('system:role:edit')"
                      :disabled="row.isSystem === 1"
                      command="edit"
                      class="dropdown-item-custom"
                    >
                      <el-icon class="item-icon primary-icon"><Edit /></el-icon>
                      <span class="item-text">编辑角色</span>
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-if="hasPermission('system:role:assign')"
                      command="assign"
                      class="dropdown-item-custom"
                    >
                      <el-icon class="item-icon warning-icon"><Setting /></el-icon>
                      <span class="item-text">分配权限</span>
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-if="hasPermission('system:role:delete')"
                      :disabled="row.isSystem === 1"
                      command="delete"
                      divided
                      class="dropdown-item-custom danger-item"
                    >
                      <el-icon class="item-icon danger-icon"><Delete /></el-icon>
                      <span class="item-text">删除角色</span>
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页器 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          class="custom-pagination"
          :prev-text="'上一页'"
          :next-text="'下一页'"
        />
      </div>
    </div>

    <!-- 新增/编辑角色对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="650px"
      @close="handleDialogClose"
      class="role-dialog"
      :close-on-click-modal="false"
    >
      <el-form
        ref="roleFormRef"
        :model="roleForm"
        :rules="roleRules"
        class="role-form"
        label-position="top"
      >
        <div class="form-row">
          <el-form-item label="角色名称" prop="roleName" class="form-col-small">
            <el-input
              v-model="roleForm.roleName"
              placeholder="请输入角色名称"
            />
          </el-form-item>
          <el-form-item label="角色标识" prop="roleKey" class="form-col-small">
            <el-input
              v-model="roleForm.roleKey"
              placeholder="请输入角色标识，如：admin"
            />
          </el-form-item>
        </div>

        <div class="form-row">
          <el-form-item label="状态" prop="status" class="form-col-small">
            <el-radio-group v-model="roleForm.status" class="status-radio">
              <el-radio :label="1" border>正常</el-radio>
              <el-radio :label="0" border>禁用</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="排序" prop="sort" class="form-col-small">
            <el-input-number
              v-model="roleForm.sort"
              :min="0"
              class="sort-input"
              controls-position="right"
            />
          </el-form-item>
        </div>

        <div class="form-row">
          <el-form-item label="备注" prop="remark" class="form-col-full">
            <el-input
              v-model="roleForm.remark"
              type="textarea"
              :rows="3"
              placeholder="请输入备注信息（选填）"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false" class="cancel-btn">取消</el-button>
          <el-button
            type="primary"
            @click="handleSubmit"
            :loading="submitLoading"
            class="submit-btn"
          >
            {{ editingRoleId ? '保存修改' : '立即创建' }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 分配菜单权限对话框 -->
    <el-dialog
      v-model="menuDialogVisible"
      title="分配菜单权限"
      width="600px"
      @close="handleMenuDialogClose"
      class="menu-dialog"
    >
      <div class="menu-tree-container">
        <el-tree
          ref="menuTreeRef"
          :data="menuTreeData"
          :props="{ label: 'menuName', children: 'children' }"
          node-key="id"
          show-checkbox
          default-expand-all
          :check-strictly="false"
          class="permission-tree"
        />
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleExpandAll" class="tree-btn">展开/折叠</el-button>
          <el-button @click="handleCheckAll" class="tree-btn">全选/取消</el-button>
          <el-button @click="menuDialogVisible = false" class="cancel-btn">取消</el-button>
          <el-button
            type="primary"
            @click="handleMenuSubmit"
            :loading="menuSubmitting"
            class="submit-btn"
          >
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, UserFilled, Edit, Delete, Setting, Operation } from '@element-plus/icons-vue'
import {
  getRoleList,
  createRole,
  updateRole,
  deleteRole,
  getRoleMenus,
  assignMenus
} from '@/api/role'
import { getMenuTree } from '@/api/menu'

// 搜索表单
const searchForm = reactive({
  roleName: '',
  roleKey: '',
  status: null
})

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = computed(() => editingRoleId.value ? '编辑角色' : '新增角色')
const editingRoleId = ref(null)
const submitLoading = ref(false)

// 表单
const roleFormRef = ref(null)
const roleForm = reactive({
  roleName: '',
  roleKey: '',
  status: 1,
  sort: 0,
  remark: ''
})

// 表单验证规则
const roleRules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在2-50个字符', trigger: 'blur' }
  ],
  roleKey: [
    { required: true, message: '请输入角色标识', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在2-50个字符', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 菜单权限对话框
const menuDialogVisible = ref(false)
const menuTreeRef = ref(null)
const menuTreeData = ref([])
const currentRoleId = ref(null)
const menuSubmitting = ref(false)
const isExpanded = ref(true)
const isCheckedAll = ref(false)

// 权限检查函数
const hasPermission = (permission) => {
  const userPerms = JSON.parse(localStorage.getItem('userPerms') || '[]')
  return userPerms.includes(permission)
}

// 加载角色列表
const loadRoleList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      roleName: searchForm.roleName || undefined,
      roleKey: searchForm.roleKey || undefined,
      status: searchForm.status
    }
    const response = await getRoleList(params)
    console.log('角色列表响应:', response)
    console.log('response.data:', response.data)
    console.log('response.data.records:', response.data?.records)

    if (response.code === 200) {
      tableData.value = response.data.records
      pagination.total = parseInt(response.data.total)
      console.log('tableData:', tableData.value)
      console.log('pagination.total:', pagination.total)
    } else {
      ElMessage.error(response.message || '加载角色列表失败')
    }
  } catch (error) {
    console.error('加载角色列表失败:', error)
    ElMessage.error('加载角色列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadRoleList()
}

// 重置
const handleReset = () => {
  searchForm.roleName = ''
  searchForm.roleKey = ''
  searchForm.status = null
  pagination.page = 1
  loadRoleList()
}

// 分页
const handleSizeChange = () => {
  loadRoleList()
}

const handleCurrentChange = () => {
  loadRoleList()
}

// 新增
const handleAdd = () => {
  editingRoleId.value = null
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  editingRoleId.value = row.id
  Object.assign(roleForm, {
    roleName: row.roleName,
    roleKey: row.roleKey,
    status: row.status,
    sort: row.sort,
    remark: row.remark
  })
  dialogVisible.value = true
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除角色"${row.roleName}"吗？删除后将无法恢复。`,
    '删除确认',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
      draggable: true
    }
  ).then(async () => {
    try {
      const response = await deleteRole(row.id)
      if (response.code === 200) {
        ElMessage.success('删除成功')
        loadRoleList()
      } else {
        ElMessage.error(response.message || '删除失败')
      }
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error(error.response?.data?.message || '删除失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!roleFormRef.value) return

  try {
    await roleFormRef.value.validate()
    submitLoading.value = true

    const data = { ...roleForm }
    let response
    if (editingRoleId.value) {
      response = await updateRole(editingRoleId.value, data)
    } else {
      response = await createRole(data)
    }

    if (response.code === 200) {
      ElMessage.success(editingRoleId.value ? '更新成功' : '创建成功')
      dialogVisible.value = false
      loadRoleList()
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
    if (error.response) {
      ElMessage.error(error.response.data.message || '操作失败')
    }
  } finally {
    submitLoading.value = false
  }
}

// 关闭对话框
const handleDialogClose = () => {
  roleFormRef.value?.resetFields()
  Object.assign(roleForm, {
    roleName: '',
    roleKey: '',
    status: 1,
    sort: 0,
    remark: ''
  })
}

// 分配菜单权限
const handleAssignMenu = async (row) => {
  console.log('分配权限 - row:', row)
  console.log('分配权限 - row.id:', row.id)
  currentRoleId.value = row.id
  menuDialogVisible.value = true

  // 加载菜单树（防抖，首次加载后缓存）
  if (menuTreeData.value.length === 0) {
    try {
      const response = await getMenuTree()
      if (response.code === 200) {
        menuTreeData.value = response.data
      }
    } catch (error) {
      ElMessage.error('加载菜单树失败')
      return
    }
  }


  // 加载角色已有的菜单权限
  try {
    const response = await getRoleMenus(row.id)
    if (response.code === 200) {
      // 等待树渲染完成后设置选中
      setTimeout(() => {
        // 过滤出叶子节点（没有子节点的节点）
        const leafKeys = response.data.filter(id => {
          const node = menuTreeRef.value?.getNode(id)
          return node && (!node.childNodes || node.childNodes.length === 0)
        })
        menuTreeRef.value?.setCheckedKeys(leafKeys, false)
      }, 100)
    }
  } catch (error) {
    ElMessage.error('加载角色权限失败')
  }











}

// 提交菜单权限
const handleMenuSubmit = async () => {
  try {
    menuSubmitting.value = true
    // 获取选中的节点（包括半选节点）
    const checkedKeys = menuTreeRef.value.getCheckedKeys()
    const halfCheckedKeys = menuTreeRef.value.getHalfCheckedKeys()
    const menuIds = [...checkedKeys, ...halfCheckedKeys]

    const response = await assignMenus(currentRoleId.value, { menuIds })
    if (response.code === 200) {
      ElMessage.success('分配权限成功')
      menuDialogVisible.value = false
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '分配权限失败')
  } finally {
    menuSubmitting.value = false
  }
}

// 关闭菜单对话框
const handleMenuDialogClose = () => {
  menuTreeRef.value?.setCheckedKeys([], false)
}

// 展开/折叠
const handleExpandAll = () => {
  isExpanded.value = !isExpanded.value
  const nodes = menuTreeRef.value?.store?.nodesMap
  if (nodes) {
    Object.values(nodes).forEach(node => {
      node.expanded = isExpanded.value
    })
  }
}

// 全选/取消
const handleCheckAll = () => {
  isCheckedAll.value = !isCheckedAll.value
  if (isCheckedAll.value) {
    const allKeys = []
    const collectKeys = (nodes) => {
      nodes.forEach(node => {
        allKeys.push(node.id)
        if (node.children && node.children.length > 0) {
          collectKeys(node.children)
        }
      })
    }
    collectKeys(menuTreeData.value)
    menuTreeRef.value?.setCheckedKeys(allKeys, false)
  } else {
    menuTreeRef.value?.setCheckedKeys([], false)
  }
}

// 处理下拉菜单命令
const handleDropdownCommand = (command, row) => {
  if (command === 'edit') {
    handleEdit(row)
  } else if (command === 'assign') {
    handleAssignMenu(row)
  } else if (command === 'delete') {
    handleDelete(row)
  }
}

onMounted(() => {
  loadRoleList()
})
</script>

<style scoped>
/* 导入优雅字体 */
@import url('https://fonts.googleapis.com/css2?family=Fira+Code:wght@400;500;600;700&family=Fira+Sans:wght@300;400;500;600;700&display=swap');

.role-management {
  min-height: 100vh;
  padding: 24px 32px;
  background: linear-gradient(135deg, #FAF5FF 0%, #F3E8FF 100%);
  animation: fadeIn 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  font-family: 'Fira Sans', sans-serif;
  color: #4C1D95;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 页面标题 */
.page-header {
  margin-bottom: 24px;
}
.page-title {
  font-family: 'Fira Code', monospace;
  font-size: 28px;
  font-weight: 700;
  color: #4C1D95;
  margin: 0 0 8px 0;
  letter-spacing: -0.5px;
}
.page-subtitle {
  font-size: 14px;
  color: #7C3AED;
  opacity: 0.8;
  margin: 0;
}

/* 搜索区域 */
.search-section, .toolbar, .table-container {
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(124, 58, 237, 0.05);
  border: 1px solid rgba(124, 58, 237, 0.1);
  margin-bottom: 16px;
  transition: all 0.3s ease;
}

.search-section {
  padding: 16px 24px;
}

.search-form {
  margin: 0;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.search-form :deep(.el-form-item) {
  margin: 0 !important;
}

.search-input,
.search-select {
  width: 180px;
}

.search-input :deep(.el-input__wrapper),
.search-select :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px #E5E7EB inset;
  transition: all 0.2s ease;
  background: #F9FAFB;
}

.search-input :deep(.el-input__wrapper:hover),
.search-select :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #C4B5FD inset;
  background: #ffffff;
}

.search-input :deep(.el-input__wrapper.is-focus),
.search-select :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px #7C3AED inset;
  background: #ffffff;
}

.el-button {
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  border-radius: 8px;
}
.el-button:active {
  transform: scale(0.96);
}

.search-btn, .add-btn, .submit-btn {
  background: linear-gradient(135deg, #7C3AED 0%, #6D28D9 100%) !important;
  border: none !important;
  font-family: 'Fira Sans', sans-serif;
  font-weight: 500;
  color: white !important;
}

.search-btn:hover, .add-btn:hover, .submit-btn:hover {
  box-shadow: 0 4px 12px rgba(124, 58, 237, 0.3) !important;
  transform: translateY(-1px);
}

.reset-btn {
  border: 1px solid #D1D5DB;
  color: #4B5563;
  background: #ffffff;
}

.reset-btn:hover {
  border-color: #7C3AED;
  color: #7C3AED;
  background: #F3E8FF;
  transform: translateY(-1px);
}

/* 操作栏 */
.toolbar {
  padding: 16px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.toolbar-info {
  font-size: 14px;
  color: #6B7280;
}

.count {
  font-family: 'Fira Code', monospace;
  font-weight: 600;
  color: #7C3AED;
  font-size: 16px;
  margin: 0 4px;
}

/* 表格容器 */
.table-container {
  padding: 24px;
  overflow: hidden;
}

/* 表格样式 */
.role-table {
  font-family: 'Fira Sans', sans-serif;
  --el-table-border-color: #F3F4F6;
  --el-table-header-bg-color: #F3E8FF;
  --el-table-row-hover-bg-color: #FAF5FF;
}

.role-table :deep(.table-header th) {
  font-family: 'Fira Code', monospace;
  font-weight: 600;
  color: #4C1D95;
  background-color: var(--el-table-header-bg-color);
  padding: 12px;
  border-bottom: 2px solid #E9D5FF;
}

.role-table :deep(.table-row) {
  transition: all 0.2s ease;
}

.role-table :deep(.table-row:hover) {
  transform: translateX(2px);
}

.role-table :deep(.el-table__cell) {
  padding: 14px 12px;
  border-bottom: 1px solid #F3F4F6;
}

.role-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}

.role-icon {
  color: #7C3AED;
}

/* 状态标签通透徽章化 */
.status-tag {
  border-radius: 6px;
  font-weight: 600;
  font-size: 11px;
  padding: 2px 8px;
  border: 1px solid transparent;
}
.status-tag.el-tag--success {
  background-color: #ECFDF5 !important;
  color: #059669 !important;
  border-color: #A7F3D0 !important;
}
.status-tag.el-tag--danger {
  background-color: #FAF5FF !important;
  color: #7C3AED !important;
  border-color: #FCA5A5 !important;
}

.action-buttons {
  display: flex;
  justify-content: center;
}

.action-menu-btn {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: #F3E8FF;
  border: 1px solid transparent;
  color: #7C3AED;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.action-menu-btn:hover {
  background: #7C3AED;
  color: white;
  box-shadow: 0 4px 12px rgba(124, 58, 237, 0.2);
  transform: translateY(-2px);
}

.action-icon {
  font-size: 16px;
  color: inherit;
  transition: all 0.2s ease;
}

/* 下拉菜单样式 */
.action-dropdown {
  border-radius: 12px;
  border: 1px solid #E5E7EB;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  padding: 8px;
}

.dropdown-item-custom {
  border-radius: 6px;
  margin-bottom: 2px;
  transition: all 0.2s ease;
  font-family: 'Fira Sans', sans-serif;
  color: #374151;
  padding: 8px 16px;
}

.dropdown-item-custom:hover {
  background: #F3E8FF;
  color: #7C3AED;
  padding-left: 18px;
}

.dropdown-item-custom.danger-item:hover {
  background: #FAF5FF;
  color: #7C3AED;
}

.item-icon {
  margin-right: 8px;
  transition: transform 0.2s ease;
}

.dropdown-item-custom:hover .item-icon {
  transform: scale(1.1);
}

/* 分页器 */
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.custom-pagination :deep(.el-pager li) {
  border-radius: 6px;
  font-family: 'Fira Code', monospace;
  transition: all 0.2s ease;
}

.custom-pagination :deep(.el-pager li.is-active) {
  background: #7C3AED;
  color: white;
}

.custom-pagination :deep(.el-pager li:hover:not(.is-active)) {
  color: #7C3AED;
}

/* 对话框 */
:deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  background: #ffffff;
  box-shadow: 0 20px 25px -5px rgba(124, 58, 237, 0.08), 0 10px 10px -5px rgba(124, 58, 237, 0.04);
  border: 1px solid rgba(124, 58, 237, 0.08);
}

.role-dialog :deep(.el-dialog__header),
.menu-dialog :deep(.el-dialog__header) {
  background: #ffffff;
  margin: 0;
  padding: 24px 28px 20px 28px;
  border-bottom: 1px solid rgba(124, 58, 237, 0.06);
}

.role-dialog :deep(.el-dialog__title),
.menu-dialog :deep(.el-dialog__title) {
  font-family: 'Fira Sans', sans-serif;
  color: #1F2937;
  font-weight: 600;
  font-size: 18px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.role-dialog :deep(.el-dialog__title::before),
.menu-dialog :deep(.el-dialog__title::before) {
  content: "";
  display: inline-block;
  width: 4px;
  height: 16px;
  background: linear-gradient(135deg, #7C3AED 0%, #6D28D9 100%);
  border-radius: 2px;
}

.role-dialog :deep(.el-dialog__headerbtn),
.menu-dialog :deep(.el-dialog__headerbtn) {
  top: 20px;
  right: 20px;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #F3F4F6;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  border: none;
}

.role-dialog :deep(.el-dialog__headerbtn:hover),
.menu-dialog :deep(.el-dialog__headerbtn:hover) {
  background: #F3E8FF;
}

.role-dialog :deep(.el-dialog__headerbtn .el-dialog__close),
.menu-dialog :deep(.el-dialog__headerbtn .el-dialog__close) {
  color: #9CA3AF;
  transition: all 0.2s ease;
}

.role-dialog :deep(.el-dialog__headerbtn:hover .el-dialog__close),
.menu-dialog :deep(.el-dialog__headerbtn:hover .el-dialog__close) {
  color: #7C3AED;
}

.role-dialog :deep(.el-dialog__body),
.menu-dialog :deep(.el-dialog__body) {
  padding: 24px 28px;
  background: #ffffff;
}

/* 表单行布局 */
.form-row {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.form-row:last-child {
  margin-bottom: 0;
}

.form-col {
  flex: 1;
  margin-bottom: 0 !important;
}

.form-col-small {
  flex: 0 0 280px;
  margin-bottom: 0 !important;
}

.form-col-full {
  flex: 1;
  margin-bottom: 0 !important;
}

.role-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: #4B5563;
  font-size: 13px;
  padding-bottom: 6px !important;
}

.role-form :deep(.el-form-item__content) {
  line-height: normal;
}

.role-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px #E5E7EB inset !important;
  background: #F9FAFB;
  transition: all 0.2s ease;
}

.role-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #C4B5FD inset !important;
  background: #ffffff;
}

.role-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(124, 58, 237, 0.2) inset, 0 0 0 3px rgba(124, 58, 237, 0.15) !important;
  background: #ffffff;
}

.role-form :deep(.el-input__inner) {
  font-size: 14px;
  color: #111827;
}

.role-form :deep(.el-textarea__inner) {
  border-radius: 8px;
  border: 1px solid #E5E7EB !important;
  background: #F9FAFB;
  transition: all 0.2s ease;
  resize: none;
}

.role-form :deep(.el-textarea__inner:hover) {
  border-color: #C4B5FD !important;
  background: #ffffff;
}

.role-form :deep(.el-textarea__inner:focus) {
  border-color: #7C3AED !important;
  box-shadow: 0 0 0 3px rgba(124, 58, 237, 0.15) !important;
  background: #ffffff;
}

/* 排序输入框 */
.sort-input {
  width: 100%;
}

.status-radio {
  display: flex;
  gap: 12px;
  width: 100%;
}

.status-radio :deep(.el-radio.is-bordered) {
  border-radius: 8px;
  border: 1px solid #E5E7EB !important;
  background: #F9FAFB;
  transition: all 0.2s ease;
  margin: 0 !important;
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 40px;
}

.status-radio :deep(.el-radio.is-bordered:hover) {
  border-color: #C4B5FD !important;
  background: #FDFEFF;
}

.status-radio :deep(.el-radio.is-bordered.is-checked) {
  border-color: #7C3AED !important;
  background: #F5F3FF;
  box-shadow: 0 2px 8px rgba(124, 58, 237, 0.08);
}

.status-radio :deep(.el-radio__input.is-checked .el-radio__inner) {
  border-color: #7C3AED !important;
  background: #7C3AED !important;
}

.status-radio :deep(.el-radio__input.is-checked + .el-radio__label) {
  color: #7C3AED !important;
  font-weight: 500;
}

.dialog-footer {
  padding: 16px 28px;
  border-top: 1px solid rgba(124, 58, 237, 0.06);
  margin: 24px -28px -24px -28px;
  background: #F9FAFB;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.cancel-btn,
.tree-btn {
  border-radius: 8px;
  border: 1px solid #E5E7EB !important;
  color: #4B5563 !important;
  background: #ffffff !important;
  font-weight: 500;
  transition: all 0.2s ease;
}

.cancel-btn:hover,
.tree-btn:hover {
  border-color: #C4B5FD !important;
  color: #7C3AED !important;
  background: #F5F3FF !important;
}

/* 菜单树容器 */
.menu-tree-container {
  max-height: 400px;
  overflow-y: auto;
  padding: 16px;
  background: #F9FAFB;
  border: 1px solid #E5E7EB;
  border-radius: 10px;
}

.menu-tree-container::-webkit-scrollbar {
  width: 6px;
}

.menu-tree-container::-webkit-scrollbar-thumb {
  background: #C4B5FD;
  border-radius: 3px;
}

.permission-tree {
  background: transparent;
}

.permission-tree :deep(.el-tree-node__content) {
  padding: 8px;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.permission-tree :deep(.el-tree-node__content:hover) {
  background: #F3E8FF;
  color: #7C3AED;
}

.permission-tree :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #7C3AED !important;
  border-color: #7C3AED !important;
}

/* 响应式 */
@media (max-width: 768px) {
  .role-management {
    padding: 16px;
  }

  .search-section,
  .toolbar,
  .table-container {
    padding: 16px;
  }

  .search-input,
  .search-select {
    width: 100%;
  }

  .search-form :deep(.el-form-item) {
    margin-right: 0;
    margin-bottom: 12px;
  }

  .form-row {
    flex-direction: column;
    gap: 0;
  }

  .form-col {
    margin-bottom: 20px !important;
  }
}
</style>
