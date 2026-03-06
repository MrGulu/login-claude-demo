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
      width="500px"
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
              size="large"
            />
          </el-form-item>
          <el-form-item label="角色标识" prop="roleKey" class="form-col-small">
            <el-input
              v-model="roleForm.roleKey"
              placeholder="请输入角色标识，如：admin"
              size="large"
            />
          </el-form-item>
        </div>

        <div class="form-row">
          <el-form-item label="状态" prop="status" class="form-col-small">
            <el-radio-group v-model="roleForm.status" class="status-radio" size="large">
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
              size="large"
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
          <el-button @click="dialogVisible = false" size="large" class="cancel-btn">取消</el-button>
          <el-button
            type="primary"
            @click="handleSubmit"
            :loading="submitLoading"
            size="large"
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
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;600;700&family=Inter:wght@300;400;500;600&display=swap');

.role-management {
  min-height: 100vh;
  padding: 24px 32px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  animation: fadeIn 0.6s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 页面标题 */
.page-header {
  margin-bottom: 20px;
  animation: slideDown 0.8s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.page-title {
  font-family: 'Playfair Display', serif;
  font-size: 32px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0 0 4px 0;
  letter-spacing: -0.5px;
  background: linear-gradient(135deg, #1a1a1a 0%, #4a4a4a 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-subtitle {
  font-family: 'Inter', sans-serif;
  font-size: 13px;
  font-weight: 400;
  color: #6c757d;
  margin: 0;
  letter-spacing: 0.3px;
}

/* 搜索区域 */
.search-section {
  background: #ffffff;
  border-radius: 12px;
  padding: 16px 24px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  animation: slideUp 0.8s ease-out 0.1s both;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.search-form {
  margin: 0;
  display: flex;
  align-items: center;
  line-height: 1;
}

.search-form :deep(.el-form-item) {
  margin-bottom: 0 !important;
  margin-right: 16px;
}

.search-form :deep(.el-form-item:last-child) {
  margin-right: 0;
}

.search-input,
.search-select {
  width: 160px;
}

.search-input :deep(.el-input__wrapper),
.search-select :deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 0 0 1px #e0e0e0 inset;
  transition: all 0.3s ease;
}

.search-input :deep(.el-input__wrapper:hover),
.search-select :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c0c0c0 inset;
}

.search-input :deep(.el-input__wrapper.is-focus),
.search-select :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px #dc2626 inset;
}

.search-btn {
  border-radius: 10px;
  padding: 12px 24px;
  font-weight: 500;
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  border: none;
  transition: all 0.3s ease;
}

.search-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(220, 38, 38, 0.3);
}

.reset-btn {
  border-radius: 10px;
  padding: 12px 24px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.reset-btn:hover {
  transform: translateY(-2px);
}

/* 操作栏 */
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #ffffff;
  border-radius: 12px;
  padding: 16px 24px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  animation: slideUp 0.8s ease-out 0.2s both;
}

.add-btn {
  border-radius: 10px;
  padding: 12px 28px;
  font-weight: 500;
  font-size: 15px;
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  border: none;
  transition: all 0.3s ease;
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(220, 38, 38, 0.3);
}

.toolbar-info {
  font-family: 'Inter', sans-serif;
  font-size: 14px;
  color: #6c757d;
}

.count {
  font-weight: 600;
  color: #dc2626;
  font-size: 18px;
  margin: 0 4px;
}

/* 表格容器 */
.table-container {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  animation: slideUp 0.8s ease-out 0.3s both;
}

/* 表格样式 */
.role-table {
  font-family: 'Inter', sans-serif;
}

.role-table :deep(.table-header) {
  background: #f8f9fa;
}

.role-table :deep(.table-header th) {
  font-weight: 600;
  font-size: 13px;
  color: #495057;
  letter-spacing: 0.5px;
  text-transform: uppercase;
  padding: 12px 12px;
  border-bottom: 2px solid #e9ecef;
}

.role-table :deep(.table-row) {
  transition: all 0.3s ease;
}

.role-table :deep(.table-row:hover) {
  background: #f8f9fa;
  transform: scale(1.01);
}

.role-table :deep(.el-table__cell) {
  padding: 12px 12px;
  border-bottom: 1px solid #f1f3f5;
}

.role-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.role-icon {
  color: #dc2626;
  font-size: 16px;
}

.status-tag {
  font-weight: 500;
  padding: 4px 12px;
  border-radius: 6px;
}

.action-buttons {
  display: flex;
  justify-content: center;
  align-items: center;
}

.action-menu-btn {
  width: 34px;
  height: 34px;
  padding: 0;
  border-radius: 10px;
  border: none;
  background: linear-gradient(135deg, #dc2626 0%, #f59e0b 100%);
  transition: all 0.3s ease;
  box-shadow: none;
}

.action-menu-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(220, 38, 38, 0.3);
}

.action-menu-btn:active {
  transform: translateY(0);
}

.action-icon {
  font-size: 17px;
  color: #ffffff;
  transition: all 0.3s ease;
}

/* 下拉菜单样式 */
.action-dropdown {
  min-width: 160px;
  padding: 8px 0;
  border-radius: 12px;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12), 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid #f1f3f5;
  background: #ffffff;
  animation: dropdownSlideIn 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes dropdownSlideIn {
  from {
    opacity: 0;
    transform: translateY(-8px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.dropdown-item-custom {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.dropdown-item-custom::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  width: 3px;
  height: 100%;
  background: transparent;
  transition: all 0.3s ease;
}

.dropdown-item-custom:hover {
  background: linear-gradient(90deg, rgba(59, 130, 246, 0.08) 0%, rgba(59, 130, 246, 0.02) 100%);
  color: #1f2937;
  padding-left: 20px;
}

.dropdown-item-custom:hover::before {
  background: #3b82f6;
}

.dropdown-item-custom.danger-item:hover {
  background: linear-gradient(90deg, rgba(239, 68, 68, 0.08) 0%, rgba(239, 68, 68, 0.02) 100%);
  color: #dc2626;
}

.dropdown-item-custom.danger-item:hover::before {
  background: #ef4444;
}

.dropdown-item-custom.is-disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.dropdown-item-custom.is-disabled:hover {
  background: transparent;
  padding-left: 16px;
}

.dropdown-item-custom.is-disabled:hover::before {
  background: transparent;
}

.item-icon {
  font-size: 16px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  flex-shrink: 0;
}

.primary-icon {
  color: #3b82f6;
}

.warning-icon {
  color: #f59e0b;
}

.danger-icon {
  color: #ef4444;
}

.dropdown-item-custom:hover .item-icon {
  transform: scale(1.15) translateX(2px);
}

.dropdown-item-custom.is-disabled:hover .item-icon {
  transform: none;
}

.item-text {
  font-family: 'Inter', sans-serif;
  letter-spacing: 0.01em;
}

/* 分割线样式 */
:deep(.el-dropdown-menu__item--divided) {
  margin-top: 8px;
  border-top: 1px solid #f1f3f5;
  padding-top: 18px;
}

/* 分页器 */
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.custom-pagination :deep(.el-pagination__total),
.custom-pagination :deep(.el-pagination__jump) {
  font-family: 'Inter', sans-serif;
  font-weight: 500;
}

.custom-pagination :deep(.el-pager li) {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.custom-pagination :deep(.el-pager li:hover) {
  transform: translateY(-2px);
}

.custom-pagination :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  color: #ffffff;
}

/* 对话框 */
.role-dialog :deep(.el-dialog),
.menu-dialog :deep(.el-dialog) {
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  overflow: hidden;
}

.role-dialog :deep(.el-dialog__header),
.menu-dialog :deep(.el-dialog__header) {
  padding: 24px 32px;
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  border-bottom: none;
}

.role-dialog :deep(.el-dialog__title),
.menu-dialog :deep(.el-dialog__title) {
  font-family: 'Playfair Display', serif;
  font-size: 22px;
  font-weight: 700;
  color: #ffffff;
  letter-spacing: 0.5px;
}

.role-dialog :deep(.el-dialog__headerbtn .el-dialog__close),
.menu-dialog :deep(.el-dialog__headerbtn .el-dialog__close) {
  color: #ffffff;
  font-size: 20px;
}

.role-dialog :deep(.el-dialog__headerbtn .el-dialog__close):hover,
.menu-dialog :deep(.el-dialog__headerbtn .el-dialog__close):hover {
  color: #fef2f2;
}

.role-dialog :deep(.el-dialog__body),
.menu-dialog :deep(.el-dialog__body) {
  padding: 32px;
  background: #fafafa;
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
  flex: 0 0 180px;
  margin-bottom: 0 !important;
}

.form-col-full {
  flex: 1;
  margin-bottom: 0 !important;
}

.role-form :deep(.el-form-item__label) {
  font-family: 'Inter', sans-serif;
  font-weight: 600;
  color: #1f2937;
  font-size: 14px;
  margin-bottom: 8px;
  padding: 0;
  line-height: 1.5;
}

.role-form :deep(.el-form-item__content) {
  line-height: normal;
}

.role-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px #d1d5db inset;
  transition: all 0.2s ease;
  background: #ffffff;
}

.role-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #9ca3af inset;
}

.role-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px #dc2626 inset;
}

.role-form :deep(.el-input__inner) {
  font-size: 14px;
  color: #111827;
}

.role-form :deep(.el-input__inner::placeholder) {
  color: #9ca3af;
}

.role-form :deep(.el-textarea__inner) {
  border-radius: 8px;
  border: 1px solid #d1d5db;
  transition: all 0.2s ease;
  font-size: 14px;
  line-height: 1.6;
  background: #ffffff;
  resize: none;
}

.role-form :deep(.el-textarea__inner:hover) {
  border-color: #9ca3af;
}

.role-form :deep(.el-textarea__inner:focus) {
  border-color: #dc2626;
  box-shadow: 0 0 0 3px rgba(220, 38, 38, 0.1);
}

.role-form :deep(.el-textarea__inner::placeholder) {
  color: #9ca3af;
}

/* 排序输入框 */
.sort-input {
  width: 100%;
}

.sort-input :deep(.el-input__wrapper) {
  padding-right: 50px;
}

.sort-input :deep(.el-input-number__decrease),
.sort-input :deep(.el-input-number__increase) {
  width: 32px;
  background: #f3f4f6;
  border: none;
  transition: all 0.2s ease;
}

.sort-input :deep(.el-input-number__decrease):hover,
.sort-input :deep(.el-input-number__increase):hover {
  background: #dc2626;
  color: #ffffff;
}

.status-radio {
  display: flex;
  gap: 12px;
  width: 100%;
}

.status-radio :deep(.el-radio) {
  margin-right: 0;
  flex: 1;
}

.status-radio :deep(.el-radio.is-bordered) {
  border-radius: 8px;
  padding: 10px 20px;
  transition: all 0.2s ease;
  border: 2px solid #d1d5db;
  background: #ffffff;
  display: flex;
  justify-content: center;
  align-items: center;
}

.status-radio :deep(.el-radio.is-bordered:hover) {
  border-color: #9ca3af;
}

.status-radio :deep(.el-radio.is-bordered.is-checked) {
  border-color: #dc2626;
  background: #fef2f2;
}

.status-radio :deep(.el-radio__label) {
  font-weight: 600;
  font-size: 14px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 32px;
  background: #f3f4f6;
  border-top: 1px solid #e5e7eb;
}

.cancel-btn,
.tree-btn {
  border-radius: 8px;
  padding: 10px 24px;
  font-weight: 600;
  border: 2px solid #d1d5db;
  color: #6b7280;
  background: #ffffff;
}

.cancel-btn:hover,
.tree-btn:hover {
  border-color: #9ca3af;
  color: #374151;
}

.submit-btn {
  border-radius: 8px;
  padding: 10px 24px;
  font-weight: 600;
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  border: none;
  box-shadow: 0 2px 8px rgba(220, 38, 38, 0.3);
}

.submit-btn:hover {
  box-shadow: 0 4px 12px rgba(220, 38, 38, 0.4);
}

/* 菜单树容器 */
.menu-tree-container {
  max-height: 400px;
  overflow-y: auto;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 10px;
}

.menu-tree-container::-webkit-scrollbar {
  width: 6px;
}

.menu-tree-container::-webkit-scrollbar-thumb {
  background: #dc2626;
  border-radius: 3px;
}

.permission-tree :deep(.el-tree-node__content) {
  padding: 8px;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.permission-tree :deep(.el-tree-node__content:hover) {
  background: rgba(220, 38, 38, 0.08);
}

/* 响应式 */
@media (max-width: 768px) {
  .role-management {
    padding: 20px;
  }

  .page-title {
    font-size: 28px;
  }

  .search-section,
  .toolbar,
  .table-container {
    padding: 20px;
  }

  .search-input,
  .search-select {
    width: 100%;
  }

  .search-form :deep(.el-form-item) {
    margin-right: 0;
    margin-bottom: 12px;
  }

  /* 对话框响应式 */
  .role-dialog :deep(.el-dialog),
  .menu-dialog :deep(.el-dialog) {
    width: 95% !important;
    margin: 20px auto;
  }

  .form-row {
    flex-direction: column;
    gap: 0;
  }

  .form-col {
    margin-bottom: 20px !important;
  }

  .role-dialog :deep(.el-dialog__header),
  .role-dialog :deep(.el-dialog__body),
  .menu-dialog :deep(.el-dialog__header),
  .menu-dialog :deep(.el-dialog__body),
  .dialog-footer {
    padding-left: 20px;
    padding-right: 20px;
  }
}
</style>
