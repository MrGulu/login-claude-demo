<template>
  <div class="notice-management">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">公告管理</h1>
      <p class="page-subtitle">管理与发布系统通知及公告信息</p>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item>
          <el-input
            v-model="searchForm.title"
            placeholder="搜索公告标题"
            clearable
            prefix-icon="Search"
            class="search-input"
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="searchForm.author"
            placeholder="搜索发布人"
            clearable
            prefix-icon="User"
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
            <el-option label="关闭" :value="0" />
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
      <el-button v-permission="'system:notice:add'" type="primary" :icon="Plus" @click="handleAdd" class="add-btn">
        新增公告
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
        class="notice-table"
        header-row-class-name="table-header"
        row-class-name="table-row"
      >
        <el-table-column prop="title" label="公告标题" min-width="180" show-overflow-tooltip />
        <el-table-column prop="content" label="公告内容" min-width="280" show-overflow-tooltip />
        <el-table-column prop="author" label="发布人" width="120" />
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
              关闭
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="180" />
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
                      v-if="hasPermission('system:notice:edit')"
                      command="edit"
                      class="dropdown-item-custom"
                    >
                      <el-icon class="item-icon primary-icon"><Edit /></el-icon>
                      <span class="item-text">编辑公告</span>
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-if="hasPermission('system:notice:edit')"
                      command="status"
                      class="dropdown-item-custom"
                    >
                      <el-icon class="item-icon warning-icon"><Switch /></el-icon>
                      <span class="item-text">{{ row.status === 1 ? '关闭公告' : '启用公告' }}</span>
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-if="hasPermission('system:notice:delete')"
                      command="delete"
                      divided
                      class="dropdown-item-custom danger-item"
                    >
                      <el-icon class="item-icon danger-icon"><Delete /></el-icon>
                      <span class="item-text">删除公告</span>
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
      class="notice-dialog"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        class="notice-form"
        label-position="top"
      >
        <div class="form-row">
          <el-form-item label="公告标题" prop="title" class="form-col-full">
            <el-input
              v-model="formData.title"
              placeholder="请输入公告标题（2-100字符）"
              maxlength="100"
              show-word-limit
            />
          </el-form-item>
        </div>

        <div class="form-row">
          <el-form-item label="公告内容" prop="content" class="form-col-full">
            <el-input
              v-model="formData.content"
              type="textarea"
              :rows="6"
              placeholder="请输入公告正文内容"
            />
          </el-form-item>
        </div>

        <div class="form-row">
          <el-form-item label="状态" prop="status" class="form-col-half">
            <el-radio-group v-model="formData.status" class="status-radio">
              <el-radio :label="1" border>正常</el-radio>
              <el-radio :label="0" border>关闭</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="备注" prop="remark" class="form-col-half">
            <el-input
              v-model="formData.remark"
              placeholder="请输入备注信息（选填）"
              maxlength="200"
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
            {{ isEdit ? '保存修改' : '立即发布' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Edit, Delete, Switch, User, Operation } from '@element-plus/icons-vue'
import { getNoticeList, createNotice, updateNotice, deleteNotice, updateNoticeStatus } from '@/api/notice'

// 搜索表单
const searchForm = reactive({
  title: '',
  author: '',
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
const dialogTitle = computed(() => isEdit.value ? '编辑公告' : '发布新公告')
const isEdit = ref(false)
const submitLoading = ref(false)

// 表单
const formRef = ref()
const formData = reactive({
  id: null,
  title: '',
  content: '',
  status: 1,
  remark: ''
})

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入公告标题', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在2-100个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入公告内容', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择公告状态', trigger: 'change' }
  ]
}

// 权限检查函数
const hasPermission = (permission) => {
  const userPerms = JSON.parse(localStorage.getItem('userPerms') || '[]')
  return userPerms.includes(permission)
}

// 获取公告列表
const getNoticeData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.pageNum,
      size: pagination.pageSize,
      title: searchForm.title || undefined,
      author: searchForm.author || undefined,
      status: searchForm.status
    }

    const response = await getNoticeList(params)

    if (response.code === 200) {
      tableData.value = response.data.records
      pagination.total = response.data.total
    } else {
      ElMessage.error(response.message || '获取公告列表失败')
    }
  } catch (error) {
    console.error('获取公告列表失败:', error)
    ElMessage.error('获取公告列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  getNoticeData()
}

// 重置
const handleReset = () => {
  searchForm.title = ''
  searchForm.author = ''
  searchForm.status = null
}

// 分页
const handleSizeChange = (val) => {
  pagination.pageSize = val
  getNoticeData()
}

const handleCurrentChange = (val) => {
  pagination.pageNum = val
  getNoticeData()
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
  formData.title = row.title
  formData.content = row.content
  formData.status = row.status
  formData.remark = row.remark || ''
  dialogVisible.value = true
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除公告"${row.title}"吗？删除后将无法恢复。`,
    '删除确认',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
      draggable: true
    }
  ).then(async () => {
    try {
      const response = await deleteNotice(row.id)
      if (response.code === 200) {
        ElMessage.success('删除成功')
        getNoticeData()
      } else {
        ElMessage.error(response.message || '删除失败')
      }
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 状态切换
const handleStatusChange = (row) => {
  const action = row.status === 1 ? '关闭' : '开启'
  const newStatus = row.status === 1 ? 0 : 1
  ElMessageBox.confirm(
    `确定要${action}公告"${row.title}"吗？`,
    '状态变更',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      draggable: true
    }
  ).then(async () => {
    try {
      const response = await updateNoticeStatus(row.id, newStatus)
      if (response.code === 200) {
        ElMessage.success(`${action}成功`)
        getNoticeData()
      } else {
        ElMessage.error(response.message || `${action}失败`)
      }
    } catch (error) {
      console.error(`${action}失败:`, error)
      ElMessage.error(`${action}失败`)
    }
  }).catch(() => {})
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitLoading.value = true

    const submitData = {
      title: formData.title,
      content: formData.content,
      status: formData.status,
      remark: formData.remark || undefined
    }

    if (isEdit.value) {
      const response = await updateNotice(formData.id, submitData)
      if (response.code === 200) {
        ElMessage.success('更新公告成功')
        dialogVisible.value = false
        getNoticeData()
      } else {
        ElMessage.error(response.message || '更新公告失败')
      }
    } else {
      const response = await createNotice(submitData)
      if (response.code === 200) {
        ElMessage.success('发布公告成功')
        dialogVisible.value = false
        getNoticeData()
      } else {
        ElMessage.error(response.message || '发布公告失败')
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
  formData.title = ''
  formData.content = ''
  formData.status = 1
  formData.remark = ''
}

// 下拉命令处理
const handleDropdownCommand = (command, row) => {
  if (command === 'edit') {
    handleEdit(row)
  } else if (command === 'status') {
    handleStatusChange(row)
  } else if (command === 'delete') {
    handleDelete(row)
  }
}

onMounted(() => {
  getNoticeData()
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;600;700&family=Inter:wght@300;400;500;600&display=swap');

.notice-management {
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
}

.search-form :deep(.el-form-item) {
  margin-bottom: 0 !important;
  margin-right: 16px;
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

.table-container {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  animation: slideUp 0.8s ease-out 0.3s both;
}

.notice-table {
  font-family: 'Inter', sans-serif;
}

.notice-table :deep(.table-header) {
  background: #f8f9fa;
}

.notice-table :deep(.table-header th) {
  font-weight: 600;
  font-size: 13px;
  color: #495057;
  letter-spacing: 0.5px;
  text-transform: uppercase;
  padding: 12px 12px;
  border-bottom: 2px solid #e9ecef;
}

.notice-table :deep(.table-row) {
  transition: all 0.3s ease;
}

.notice-table :deep(.table-row:hover) {
  background: #f8f9fa;
  transform: scale(1.005);
}

.notice-table :deep(.el-table__cell) {
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
}

.action-menu-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(220, 38, 38, 0.3);
}

.action-icon {
  font-size: 17px;
  color: #ffffff;
}

.action-dropdown {
  min-width: 160px;
  padding: 8px 0;
  border-radius: 12px;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
  border: 1px solid #f1f3f5;
  background: #ffffff;
}

.dropdown-item-custom {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  transition: all 0.2s ease;
  cursor: pointer;
}

.dropdown-item-custom:hover {
  background-color: #f3f4f6;
}

.danger-item {
  color: #ef4444;
}

.danger-item:hover {
  background-color: #fef2f2;
}

.item-icon {
  font-size: 16px;
}

.primary-icon { color: #dc2626; }
.warning-icon { color: #f59e0b; }
.danger-icon { color: #ef4444; }

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px;
}

.custom-pagination :deep(.el-pager li.is-active) {
  background-color: #dc2626 !important;
  color: #ffffff !important;
}

.notice-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 24px 48px rgba(0, 0, 0, 0.18);
  border: 1px solid rgba(255, 255, 255, 0.5);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
}

.notice-dialog :deep(.el-dialog__header) {
  padding: 24px 24px 0;
  margin: 0;
}

.notice-dialog :deep(.el-dialog__title) {
  font-family: 'Playfair Display', serif;
  font-size: 22px;
  font-weight: 700;
  color: #111827;
}

.notice-dialog :deep(.el-dialog__body) {
  padding: 24px;
}

.notice-dialog :deep(.el-dialog__footer) {
  padding: 0 24px 24px;
}

.notice-form {
  margin: 0;
}

.form-row {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.form-row:last-child {
  margin-bottom: 0;
}

.form-col-full {
  flex: 1;
}

.form-col-half {
  flex: 1;
  width: 50%;
}

.notice-form :deep(.el-form-item__label) {
  font-family: 'Inter', sans-serif;
  font-weight: 500;
  color: #374151;
  margin-bottom: 6px;
}

.notice-form :deep(.el-input__wrapper),
.notice-form :deep(.el-textarea__inner) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px #e5e7eb inset;
}

.notice-form :deep(.el-input__wrapper:hover),
.notice-form :deep(.el-textarea__inner:hover) {
  box-shadow: 0 0 0 1px #d1d5db inset;
}

.notice-form :deep(.el-input__wrapper.is-focus),
.notice-form :deep(.el-textarea__inner:focus) {
  box-shadow: 0 0 0 2px #dc2626 inset !important;
}

.status-radio {
  display: flex;
  gap: 12px;
}

.status-radio :deep(.el-radio) {
  margin-right: 0;
  border-radius: 8px;
  height: 40px;
  padding: 0 20px;
}

.status-radio :deep(.el-radio.is-checked) {
  border-color: #dc2626;
}

.status-radio :deep(.el-radio__input.is-checked .el-radio__inner) {
  background-color: #dc2626;
  border-color: #dc2626;
}

.status-radio :deep(.el-radio__input.is-checked + .el-radio__label) {
  color: #dc2626;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.cancel-btn {
  border-radius: 10px;
  padding: 12px 24px;
}

.submit-btn {
  border-radius: 10px;
  padding: 12px 24px;
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  border: none;
}

.submit-btn:hover {
  box-shadow: 0 4px 12px rgba(220, 38, 38, 0.3);
}
</style>
