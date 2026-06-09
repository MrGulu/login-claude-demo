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
/* 导入优雅字体 */
@import url('https://fonts.googleapis.com/css2?family=Fira+Code:wght@400;500;600;700&family=Fira+Sans:wght@300;400;500;600;700&display=swap');

.notice-management {
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
.notice-table {
  font-family: 'Fira Sans', sans-serif;
  --el-table-border-color: #F3F4F6;
  --el-table-header-bg-color: #F3E8FF;
  --el-table-row-hover-bg-color: #FAF5FF;
}

.notice-table :deep(.table-header th) {
  font-family: 'Fira Code', monospace;
  font-weight: 600;
  color: #4C1D95;
  background-color: var(--el-table-header-bg-color);
  padding: 12px;
  border-bottom: 2px solid #E9D5FF;
}

.notice-table :deep(.table-row) {
  transition: all 0.2s ease;
}

.notice-table :deep(.table-row:hover) {
  transform: translateX(2px);
}

.notice-table :deep(.el-table__cell) {
  padding: 14px 12px;
  border-bottom: 1px solid #F3F4F6;
}

/* 状态标签徽章化 */
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

/* 类型徽章化 */
.type-tag {
  border-radius: 6px;
  font-weight: 600;
  font-size: 11px;
  padding: 2px 8px;
  border: 1px solid transparent;
}
.type-tag.el-tag--primary {
  background-color: #EEF2FF !important;
  color: #4F46E5 !important;
  border-color: #C7D2FE !important;
}
.type-tag.el-tag--warning {
  background-color: #FFFBEB !important;
  color: #D97706 !important;
  border-color: #FDE68A !important;
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
.notice-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  background: #ffffff;
  box-shadow: 0 20px 25px -5px rgba(124, 58, 237, 0.08), 0 10px 10px -5px rgba(124, 58, 237, 0.04);
  border: 1px solid rgba(124, 58, 237, 0.08);
}

.notice-dialog :deep(.el-dialog__header) {
  background: #ffffff;
  margin: 0;
  padding: 24px 28px 20px 28px;
  border-bottom: 1px solid rgba(124, 58, 237, 0.06);
}

.notice-dialog :deep(.el-dialog__title) {
  font-family: 'Fira Sans', sans-serif;
  color: #1F2937;
  font-weight: 600;
  font-size: 18px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.notice-dialog :deep(.el-dialog__title::before) {
  content: "";
  display: inline-block;
  width: 4px;
  height: 16px;
  background: linear-gradient(135deg, #7C3AED 0%, #6D28D9 100%);
  border-radius: 2px;
}

.notice-dialog :deep(.el-dialog__headerbtn) {
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

.notice-dialog :deep(.el-dialog__headerbtn:hover) {
  background: #F3E8FF;
}

.notice-dialog :deep(.el-dialog__headerbtn .el-dialog__close) {
  color: #9CA3AF;
  transition: all 0.2s ease;
}

.notice-dialog :deep(.el-dialog__headerbtn:hover .el-dialog__close) {
  color: #7C3AED;
}

.notice-dialog :deep(.el-dialog__body) {
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

.form-col-full {
  flex: 1;
}

.form-col-half {
  flex: 1;
  width: 50%;
}

.notice-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: #4B5563;
  font-size: 13px;
  padding-bottom: 6px !important;
}

.notice-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px #E5E7EB inset !important;
  background: #F9FAFB;
  transition: all 0.2s ease;
}

.notice-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #C4B5FD inset !important;
  background: #ffffff;
}

.notice-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(124, 58, 237, 0.2) inset, 0 0 0 3px rgba(124, 58, 237, 0.15) !important;
  background: #ffffff;
}

.notice-form :deep(.el-textarea__inner) {
  border-radius: 8px;
  border: 1px solid #E5E7EB !important;
  background: #F9FAFB;
  transition: all 0.2s ease;
  resize: none;
}

.notice-form :deep(.el-textarea__inner:hover) {
  border-color: #C4B5FD !important;
  background: #ffffff;
}

.notice-form :deep(.el-textarea__inner:focus) {
  border-color: #7C3AED !important;
  box-shadow: 0 0 0 3px rgba(124, 58, 237, 0.15) !important;
  background: #ffffff;
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
  .notice-management {
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

  .form-col-half {
    margin-bottom: 20px !important;
    width: 100%;
  }
}
</style>
