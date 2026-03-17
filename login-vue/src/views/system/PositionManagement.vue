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
      width="600px"
      @close="handleDialogClose"
      class="position-dialog"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        class="position-form"
        label-position="top"
      >
        <div class="form-row">
          <el-form-item label="岗位名称" prop="positionName" class="form-col-half">
            <el-input
              v-model="formData.positionName"
              placeholder="请输入岗位名称（2-50字符）"
            />
          </el-form-item>
          <el-form-item label="岗位编码" prop="positionCode" class="form-col-half">
            <el-input
              v-model="formData.positionCode"
              placeholder="请输入岗位编码（2-50字符）"
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
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;600;700&family=Inter:wght@300;400;500;600&display=swap');

.position-management {
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
.position-table {
  font-family: 'Inter', sans-serif;
}

.position-table :deep(.table-header) {
  background: #f8f9fa;
}

.position-table :deep(.table-header th) {
  font-weight: 600;
  font-size: 13px;
  color: #495057;
  letter-spacing: 0.5px;
  text-transform: uppercase;
  padding: 12px 12px;
  border-bottom: 2px solid #e9ecef;
}

.position-table :deep(.table-row) {
  transition: all 0.3s ease;
}

.position-table :deep(.table-row:hover) {
  background: #f8f9fa;
  transform: scale(1.01);
}

.position-table :deep(.el-table__cell) {
  padding: 12px 12px;
  border-bottom: 1px solid #f1f3f5;
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
.position-dialog :deep(.el-dialog) {
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  overflow: hidden;
}

.position-dialog :deep(.el-dialog__header) {
  padding: 24px 32px;
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  border-bottom: none;
}

.position-dialog :deep(.el-dialog__title) {
  font-family: 'Playfair Display', serif;
  font-size: 22px;
  font-weight: 700;
  color: #ffffff;
  letter-spacing: 0.5px;
}

.position-dialog :deep(.el-dialog__headerbtn .el-dialog__close) {
  color: #ffffff;
  font-size: 20px;
}

.position-dialog :deep(.el-dialog__headerbtn .el-dialog__close):hover {
  color: #fef2f2;
}

.position-dialog :deep(.el-dialog__body) {
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

.form-col-half {
  flex: 0 0 250px;
  margin-bottom: 0 !important;
}

.form-col-full {
  flex: 1;
  margin-bottom: 0 !important;
}

.position-form :deep(.el-form-item__label) {
  font-family: 'Inter', sans-serif;
  font-weight: 600;
  color: #1f2937;
  font-size: 14px;
  margin-bottom: 8px;
  padding: 0;
  line-height: 1.5;
}

.position-form :deep(.el-form-item__content) {
  line-height: normal;
}

.position-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px #d1d5db inset;
  transition: all 0.2s ease;
  background: #ffffff;
  padding: 6px 12px;
}

.position-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #9ca3af inset;
}

.position-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px #dc2626 inset;
}

.position-form :deep(.el-input__inner) {
  font-size: 14px;
  color: #111827;
  line-height: 1.5;
}

.position-form :deep(.el-input__inner::placeholder) {
  color: #9ca3af;
}

.sort-input {
  width: 100%;
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

.cancel-btn {
  border-radius: 8px;
  padding: 10px 24px;
  font-weight: 600;
  border: 2px solid #d1d5db;
  color: #6b7280;
  background: #ffffff;
}

.cancel-btn:hover {
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

/* 响应式 */
@media (max-width: 768px) {
  .position-management {
    padding: 20px;
  }

  .page-title {
    font-size: 32px;
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
  .position-dialog :deep(.el-dialog) {
    width: 95% !important;
    margin: 20px auto;
  }

  .form-row {
    flex-direction: column;
    gap: 0;
  }

  .form-col-half {
    margin-bottom: 20px !important;
  }

  .position-dialog :deep(.el-dialog__header),
  .position-dialog :deep(.el-dialog__body),
  .dialog-footer {
    padding-left: 20px;
    padding-right: 20px;
  }
}
</style>
