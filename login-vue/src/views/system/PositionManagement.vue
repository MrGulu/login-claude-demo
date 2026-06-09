<template>
  <div class="position-management">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">岗位管理</h1>
      <p class="page-subtitle">管理系统岗位与职务</p>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item>
          <el-input
            v-model="searchForm.positionName"
            placeholder="搜索岗位名称"
            clearable
            prefix-icon="Search"
            class="search-input"
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="searchForm.positionCode"
            placeholder="搜索岗位编码"
            clearable
            prefix-icon="Key"
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
      <el-button v-permission="'system:position:add'" type="primary" :icon="Plus" @click="handleAdd" class="add-btn">
        新增岗位
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
        class="position-table"
        header-row-class-name="table-header"
        row-class-name="table-row"
      >
        <el-table-column prop="positionName" label="岗位名称" width="180" />
        <el-table-column prop="positionCode" label="岗位编码" width="180" />
        <el-table-column prop="sort" label="排序" width="100" align="center" />
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
                      v-if="hasPermission('system:position:edit')"
                      command="edit"
                      class="dropdown-item-custom"
                    >
                      <el-icon class="item-icon primary-icon"><Edit /></el-icon>
                      <span class="item-text">编辑岗位</span>
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-if="hasPermission('system:position:edit')"
                      command="status"
                      class="dropdown-item-custom"
                    >
                      <el-icon class="item-icon warning-icon"><Switch /></el-icon>
                      <span class="item-text">{{ row.status === 1 ? '禁用岗位' : '启用岗位' }}</span>
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-if="hasPermission('system:position:delete')"
                      command="delete"
                      divided
                      class="dropdown-item-custom danger-item"
                    >
                      <el-icon class="item-icon danger-icon"><Delete /></el-icon>
                      <span class="item-text">删除岗位</span>
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
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
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

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="650px"
      @close="handleDialogClose"
      class="position-dialog"
      :close-on-click-modal="false"
    >
      <div class="dialog-content-wrapper">
        <el-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          class="position-form"
          label-position="top"
        >
          <div class="form-grid">
            <el-form-item label="岗位名称" prop="positionName">
              <el-input
                v-model="formData.positionName"
                placeholder="请输入岗位名称（2-50字符）"
              />
            </el-form-item>
            <el-form-item label="岗位编码" prop="positionCode">
              <el-input
                v-model="formData.positionCode"
                placeholder="请输入岗位编码（2-50字符）"
              />
            </el-form-item>
            <el-form-item label="排序" prop="sort">
              <el-input-number
                v-model="formData.sort"
                :min="0"
                :max="999"
                placeholder="请输入排序号"
                class="sort-input"
              />
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="formData.status" class="status-radio">
                <el-radio :label="1" border>正常</el-radio>
                <el-radio :label="0" border>禁用</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="备注" prop="remark" class="full-width-item">
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
      </div>
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
import { Plus, Search, Edit, Delete, Switch, Key, Operation } from '@element-plus/icons-vue'
import { getPositionList, createPosition, updatePosition, deletePosition, updatePositionStatus } from '@/api/position'

// 搜索表单
const searchForm = reactive({
  positionName: '',
  positionCode: '',
  status: null
})

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = computed(() => isEdit.value ? '编辑岗位' : '新增岗位')
const isEdit = ref(false)
const submitLoading = ref(false)

// 表单
const formRef = ref()
const formData = reactive({
  id: null,
  positionName: '',
  positionCode: '',
  status: 1,
  sort: 0,
  remark: ''
})

// 表单验证规则
const rules = {
  positionName: [
    { required: true, message: '请输入岗位名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在2-50个字符', trigger: 'blur' }
  ],
  positionCode: [
    { required: true, message: '请输入岗位编码', trigger: 'blur' },
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

// 获取岗位列表
const getPositionData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.pageNum,
      size: pagination.pageSize,
      positionName: searchForm.positionName || undefined,
      positionCode: searchForm.positionCode || undefined,
      status: searchForm.status
    }

    const response = await getPositionList(params)

    if (response.code === 200) {
      tableData.value = response.data.records
      pagination.total = response.data.total
    } else {
      ElMessage.error(response.message || '获取岗位列表失败')
    }
  } catch (error) {
    console.error('获取岗位列表失败:', error)
    ElMessage.error('获取岗位列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  getPositionData()
}

// 重置
const handleReset = () => {
  searchForm.positionName = ''
  searchForm.positionCode = ''
  searchForm.status = null
}

// 分页
const handleSizeChange = (val) => {
  pagination.pageSize = val
  getPositionData()
}

const handleCurrentChange = (val) => {
  pagination.pageNum = val
  getPositionData()
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
  formData.positionName = row.positionName
  formData.positionCode = row.positionCode
  formData.status = row.status
  formData.sort = row.sort || 0
  formData.remark = row.remark
  dialogVisible.value = true
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除岗位"${row.positionName}"吗？删除后将无法恢复。`,
    '删除确认',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
      draggable: true
    }
  ).then(async () => {
    try {
      const response = await deletePosition(row.id)
      if (response.code === 200) {
        ElMessage.success('删除成功')
        getPositionData()
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
    `确定要${action}岗位"${row.positionName}"吗？`,
    '状态变更',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      draggable: true
    }
  ).then(async () => {
    try {
      const response = await updatePositionStatus(row.id, newStatus)
      if (response.code === 200) {
        ElMessage.success(`${action}成功`)
        getPositionData()
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
      positionName: formData.positionName,
      positionCode: formData.positionCode,
      status: formData.status,
      sort: formData.sort,
      remark: formData.remark || undefined
    }

    // 如果是编辑模式
    if (isEdit.value) {
      const response = await updatePosition(formData.id, submitData)
      if (response.code === 200) {
        ElMessage.success('更新成功')
        dialogVisible.value = false
        getPositionData()
      } else {
        ElMessage.error(response.message || '更新失败')
      }
    } else {
      const response = await createPosition(submitData)
      if (response.code === 200) {
        ElMessage.success('创建成功')
        dialogVisible.value = false
        getPositionData()
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
  formData.positionName = ''
  formData.positionCode = ''
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
  getPositionData()
})
</script>

<style scoped>
/* 导入优雅字体 */
@import url('https://fonts.googleapis.com/css2?family=Fira+Code:wght@400;500;600;700&family=Fira+Sans:wght@300;400;500;600;700&display=swap');

.position-management {
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
.position-table {
  font-family: 'Fira Sans', sans-serif;
  --el-table-border-color: #F3F4F6;
  --el-table-header-bg-color: #F3E8FF;
  --el-table-row-hover-bg-color: #FAF5FF;
}

.position-table :deep(.table-header th) {
  font-family: 'Fira Code', monospace;
  font-weight: 600;
  color: #4C1D95;
  background-color: var(--el-table-header-bg-color);
  padding: 12px;
  border-bottom: 2px solid #E9D5FF;
}

.position-table :deep(.table-row) {
  transition: all 0.2s ease;
}

.position-table :deep(.table-row:hover) {
  transform: translateX(2px);
}

.position-table :deep(.el-table__cell) {
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
.position-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  background: #ffffff;
  box-shadow: 0 20px 25px -5px rgba(124, 58, 237, 0.08), 0 10px 10px -5px rgba(124, 58, 237, 0.04);
  border: 1px solid rgba(124, 58, 237, 0.08);
}

.position-dialog :deep(.el-dialog__header) {
  background: #ffffff;
  margin: 0;
  padding: 24px 28px 20px 28px;
  border-bottom: 1px solid rgba(124, 58, 237, 0.06);
}

.position-dialog :deep(.el-dialog__title) {
  font-family: 'Fira Sans', sans-serif;
  color: #1F2937;
  font-weight: 600;
  font-size: 18px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.position-dialog :deep(.el-dialog__title::before) {
  content: "";
  display: inline-block;
  width: 4px;
  height: 16px;
  background: linear-gradient(135deg, #7C3AED 0%, #6D28D9 100%);
  border-radius: 2px;
}

.position-dialog :deep(.el-dialog__headerbtn) {
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

.position-dialog :deep(.el-dialog__headerbtn:hover) {
  background: #F3E8FF;
}

.position-dialog :deep(.el-dialog__headerbtn .el-dialog__close) {
  color: #9CA3AF;
  transition: all 0.2s ease;
}

.position-dialog :deep(.el-dialog__headerbtn:hover .el-dialog__close) {
  color: #7C3AED;
}

.position-dialog :deep(.el-dialog__body) {
  padding: 24px 28px;
  background: #ffffff;
}

.dialog-content-wrapper {
  padding: 10px 0;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 0 20px;
}

.full-width-item {
  grid-column: 1 / -1;
  margin-top: 4px;
}

.position-form :deep(.el-form-item) {
  margin-bottom: 20px;
}

.position-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: #4B5563;
  font-size: 13px;
  padding-bottom: 6px !important;
}

.position-form :deep(.el-form-item__content) {
  line-height: normal;
}

.position-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px #E5E7EB inset !important;
  background: #F9FAFB;
  transition: all 0.2s ease;
}

.position-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #C4B5FD inset !important;
  background: #ffffff;
}

.position-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(124, 58, 237, 0.2) inset, 0 0 0 3px rgba(124, 58, 237, 0.15) !important;
  background: #ffffff;
}

.position-form :deep(.el-input__inner) {
  font-size: 14px;
  color: #111827;
}

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

.cancel-btn {
  border-radius: 8px;
  border: 1px solid #E5E7EB !important;
  color: #4B5563 !important;
  background: #ffffff !important;
  font-weight: 500;
  transition: all 0.2s ease;
}

.cancel-btn:hover {
  border-color: #C4B5FD !important;
  color: #7C3AED !important;
  background: #F5F3FF !important;
}

/* 响应式 */
@media (max-width: 768px) {
  .position-management {
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

  .form-grid {
    grid-template-columns: 1fr;
    gap: 0;
  }
}
</style>
