<template>
  <div class="dept-management">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">部门管理</h1>
      <p class="page-subtitle">管理系统组织架构与部门树</p>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item>
          <el-input
            v-model="searchForm.deptName"
            placeholder="搜索部门名称"
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
      <el-button v-permission="'system:dept:add'" type="primary" :icon="Plus" @click="handleAdd" class="add-btn">
        新增部门
      </el-button>
      <div class="toolbar-info">
        共 <span class="count">{{ totalCount }}</span> 个部门
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <el-table
        :data="tableData"
        v-loading="loading"
        row-key="id"
        default-expand-all
        class="dept-table"
        header-row-class-name="table-header"
        row-class-name="table-row"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
        <el-table-column prop="deptName" label="部门名称" min-width="200" />
        <el-table-column prop="sort" label="排序" width="100" align="center" />
        <el-table-column label="状态" width="120" align="center">
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
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
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
                      v-if="hasPermission('system:dept:edit')"
                      command="edit"
                      class="dropdown-item-custom"
                    >
                      <el-icon class="item-icon primary-icon"><Edit /></el-icon>
                      <span class="item-text">编辑部门</span>
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-if="hasPermission('system:dept:edit')"
                      command="status"
                      class="dropdown-item-custom"
                    >
                      <el-icon class="item-icon warning-icon"><Switch /></el-icon>
                      <span class="item-text">{{ row.status === 1 ? '禁用部门' : '启用部门' }}</span>
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-if="hasPermission('system:dept:delete')"
                      command="delete"
                      divided
                      class="dropdown-item-custom danger-item"
                    >
                      <el-icon class="item-icon danger-icon"><Delete /></el-icon>
                      <span class="item-text">删除部门</span>
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
      class="dept-dialog"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        class="dept-form"
        label-position="top"
      >
        <div class="form-row">
          <el-form-item label="父部门" prop="parentId" class="form-col-full">
            <el-tree-select
              v-model="formData.parentId"
              :data="deptOptions"
              check-strictly
              :render-after-expand="false"
              placeholder="请选择父部门（不选默认为一级部门）"
              value-key="id"
              :props="{ label: 'deptName', value: 'id', children: 'children', disabled: 'disabled' }"
              class="form-tree-select"
            />
          </el-form-item>
        </div>

        <div class="form-row">
          <el-form-item label="部门名称" prop="deptName" class="form-col-full">
            <el-input
              v-model="formData.deptName"
              placeholder="请输入部门名称（2-50字符）"
            />
          </el-form-item>
        </div>

        <div class="form-row">
          <el-form-item label="排序" prop="sort" class="form-col-half">
            <el-input-number
              v-model="formData.sort"
              :min="0"
              :max="999"
              placeholder="请输入排序号"
              class="sort-input"
            />
          </el-form-item>
          <el-form-item label="状态" prop="status" class="form-col-half">
            <el-radio-group v-model="formData.status" class="status-radio">
              <el-radio :label="1" border>正常</el-radio>
              <el-radio :label="0" border>禁用</el-radio>
            </el-radio-group>
          </el-form-item>
        </div>

        <div class="form-row">
          <el-form-item label="备注" prop="remark" class="form-col-full">
            <el-input
              v-model="formData.remark"
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
            {{ isEdit ? '保存修改' : '立即创建' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Edit, Delete, Switch, Operation } from '@element-plus/icons-vue'
import { getDepartmentList, createDepartment, updateDepartment, deleteDepartment, updateDepartmentStatus } from '@/api/dept'

// 搜索表单
const searchForm = reactive({
  deptName: '',
  status: null
})

// 表格数据
const rawData = ref([])
const tableData = ref([])
const totalCount = ref(0)
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = computed(() => isEdit.value ? '编辑部门' : '新增部门')
const isEdit = ref(false)
const submitLoading = ref(false)

// 表单
const formRef = ref()
const formData = reactive({
  id: null,
  parentId: 0,
  deptName: '',
  status: 1,
  sort: 0,
  remark: ''
})

// 表单验证规则
const rules = {
  deptName: [
    { required: true, message: '请输入部门名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在2-50个字符', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ],
  remark: [
    { max: 500, message: '备注长度不能超过500个字符', trigger: 'blur' }
  ]
}

// 权限检查函数
const hasPermission = (permission) => {
  const userPerms = JSON.parse(localStorage.getItem('userPerms') || '[]')
  return userPerms.includes(permission)
}

// 扁平列表转树形结构
const handleTree = (data, idKey = 'id', parentIdKey = 'parentId', childrenKey = 'children') => {
  const result = []
  if (!Array.isArray(data)) {
    return result
  }
  const map = {}
  data.forEach(item => {
    map[item[idKey]] = { ...item, [childrenKey]: [] }
  })
  data.forEach(item => {
    const parent = map[item[parentIdKey]]
    if (parent) {
      parent[childrenKey].push(map[item[idKey]])
    } else {
      result.push(map[item[idKey]])
    }
  })
  return result
}

// 部门下拉树选项，防止循环引用及关联停用部门
const deptOptions = computed(() => {
  const tree = handleTree(rawData.value)
  const currentId = formData.id
  
  const markDisabled = (nodes, isParentDisabled) => {
    return nodes.map(node => {
      // 若父节点被禁用，或自身是当前正在编辑的节点，或自身处于停用状态，则该节点及所有子节点都应该被置灰不可选
      const disabled = isParentDisabled || (currentId && node.id === currentId) || node.status === 0
      const newNode = { ...node, disabled }
      if (node.children && node.children.length > 0) {
        newNode.children = markDisabled(node.children, disabled)
      }
      return newNode
    })
  }

  const processedTree = markDisabled(tree, false)
  return [{ id: 0, deptName: '无（一级部门）', children: processedTree }]
})

// 获取部门列表
const getDeptData = async () => {
  loading.value = true
  try {
    const params = {
      deptName: searchForm.deptName || undefined,
      status: searchForm.status
    }

    const response = await getDepartmentList(params)

    if (response.code === 200) {
      rawData.value = response.data
      totalCount.value = response.data.length
      // 构建部门树给表格渲染
      tableData.value = handleTree(response.data)
    } else {
      ElMessage.error(response.message || '获取部门列表失败')
    }
  } catch (error) {
    console.error('获取部门列表失败:', error)
    ElMessage.error('获取部门列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  getDeptData()
}

// 重置
const handleReset = () => {
  searchForm.deptName = ''
  searchForm.status = null
  getDeptData()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  formData.id = row.id
  formData.parentId = row.parentId || 0
  formData.deptName = row.deptName
  formData.status = row.status
  formData.sort = row.sort || 0
  formData.remark = row.remark
  dialogVisible.value = true
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除部门"${row.deptName}"吗？删除后将无法恢复。`,
    '删除确认',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
      draggable: true
    }
  ).then(async () => {
    try {
      const response = await deleteDepartment(row.id)
      if (response.code === 200) {
        ElMessage.success('删除成功')
        getDeptData()
      } else {
        ElMessage.error(response.message || '删除失败')
      }
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

// 状态切换
const handleStatusChange = (row) => {
  const action = row.status === 1 ? '禁用' : '启用'
  const newStatus = row.status === 1 ? 0 : 1
  ElMessageBox.confirm(
    `确定要${action}部门"${row.deptName}"吗？`,
    '状态变更',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      draggable: true
    }
  ).then(async () => {
    try {
      const response = await updateDepartmentStatus(row.id, newStatus)
      if (response.code === 200) {
        ElMessage.success(`${action}成功`)
        getDeptData()
      } else {
        ElMessage.error(response.message || `${action}失败`)
      }
    } catch (error) {
      console.error(`${action}失败:`, error)
      ElMessage.error(`${action}失败`)
    }
  }).catch(() => {
    // 取消操作
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitLoading.value = true

    // 准备提交数据
    const submitData = {
      parentId: formData.parentId || 0,
      deptName: formData.deptName,
      status: formData.status,
      sort: formData.sort,
      remark: formData.remark || undefined
    }

    // 如果是编辑模式
    if (isEdit.value) {
      const response = await updateDepartment(formData.id, submitData)
      if (response.code === 200) {
        ElMessage.success('更新成功')
        dialogVisible.value = false
        getDeptData()
      } else {
        ElMessage.error(response.message || '更新失败')
      }
    } else {
      const response = await createDepartment(submitData)
      if (response.code === 200) {
        ElMessage.success('创建成功')
        dialogVisible.value = false
        getDeptData()
      } else {
        ElMessage.error(response.message || '创建失败')
      }
    }
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    submitLoading.value = false
  }
}

// 关闭对话框
const handleDialogClose = () => {
  formRef.value?.resetFields()
  formData.id = null
  formData.parentId = 0
  formData.deptName = ''
  formData.status = 1
  formData.sort = 0
  formData.remark = ''
}

// 处理下拉菜单命令
const handleDropdownCommand = (command, row) => {
  if (command === 'edit') {
    handleEdit(row)
  } else if (command === 'status') {
    handleStatusChange(row)
  } else if (command === 'delete') {
    handleDelete(row)
  }
}

// 初始化
onMounted(() => {
  getDeptData()
})
</script>

<style scoped>
/* 导入优雅字体 */
@import url('https://fonts.googleapis.com/css2?family=Fira+Code:wght@400;500;600;700&family=Fira+Sans:wght@300;400;500;600;700&display=swap');

.dept-management {
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
.dept-table {
  font-family: 'Fira Sans', sans-serif;
  --el-table-border-color: #F3F4F6;
  --el-table-header-bg-color: #F3E8FF;
  --el-table-row-hover-bg-color: #FAF5FF;
}

.dept-table :deep(.table-header th) {
  font-family: 'Fira Code', monospace;
  font-weight: 600;
  color: #4C1D95;
  background-color: var(--el-table-header-bg-color);
  padding: 12px;
  border-bottom: 2px solid #E9D5FF;
}

.dept-table :deep(.table-row) {
  transition: all 0.2s ease;
}

.dept-table :deep(.table-row:hover) {
  transform: translateX(2px);
}

.dept-table :deep(.el-table__cell) {
  padding: 14px 12px;
  border-bottom: 1px solid #F3F4F6;
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

/* 对话框 */
.dept-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  background: #ffffff;
  box-shadow: 0 20px 25px -5px rgba(124, 58, 237, 0.08), 0 10px 10px -5px rgba(124, 58, 237, 0.04);
  border: 1px solid rgba(124, 58, 237, 0.08);
}

.dept-dialog :deep(.el-dialog__header) {
  background: #ffffff;
  padding: 24px;
  border-bottom: 1px solid rgba(124, 58, 237, 0.08);
  margin-right: 0;
}

.dept-dialog :deep(.el-dialog__title) {
  font-family: 'Fira Code', monospace;
  font-weight: 700;
  color: #4C1D95;
  font-size: 20px;
}

.dept-dialog :deep(.el-dialog__body) {
  padding: 24px;
}

.dept-dialog :deep(.el-dialog__footer) {
  padding: 16px 24px 24px;
  border-top: 1px solid rgba(124, 58, 237, 0.08);
}

.dept-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-row {
  display: flex;
  gap: 16px;
}

.form-col-half {
  flex: 1;
}

.form-col-full {
  width: 100%;
}

.dept-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #4C1D95;
  margin-bottom: 6px;
}

.dept-form :deep(.el-input__wrapper),
.dept-form :deep(.el-textarea__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px #E5E7EB inset;
}

.dept-form :deep(.el-input__wrapper:hover),
.dept-form :deep(.el-textarea__wrapper:hover) {
  box-shadow: 0 0 0 1px #C4B5FD inset;
}

.dept-form :deep(.el-input__wrapper.is-focus),
.dept-form :deep(.el-textarea__wrapper.is-focus) {
  box-shadow: 0 0 0 2px #7C3AED inset;
}

.form-tree-select {
  width: 100%;
}

.form-tree-select :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px #E5E7EB inset;
}

.sort-input {
  width: 100% !important;
}

.sort-input :deep(.el-input-number__decrease),
.sort-input :deep(.el-input-number__increase) {
  background: #F3E8FF;
  border-color: #E9D5FF;
  color: #7C3AED;
}

.sort-input :deep(.el-input-number__decrease:hover),
.sort-input :deep(.el-input-number__increase:hover) {
  background: #7C3AED;
  color: white;
}

.status-radio {
  display: flex;
  gap: 12px;
  width: 100%;
}

.status-radio :deep(.el-radio) {
  flex: 1;
  margin-right: 0 !important;
  border-radius: 8px;
  border: 1px solid #E5E7EB;
  transition: all 0.2s ease;
  height: 40px;
}

.status-radio :deep(.el-radio.is-checked) {
  border-color: #7C3AED;
  background: #FAF5FF;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.cancel-btn {
  border: 1px solid #D1D5DB;
  color: #4B5563;
}

.cancel-btn:hover {
  border-color: #7C3AED;
  color: #7C3AED;
  background: #F3E8FF;
}
</style>
