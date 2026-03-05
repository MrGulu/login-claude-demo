<template>
  <div class="user-management">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">用户管理</h1>
      <p class="page-subtitle">管理系统用户账号与权限</p>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item>
          <el-input
            v-model="searchForm.username"
            placeholder="搜索用户名"
            clearable
            prefix-icon="Search"
            class="search-input"
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="searchForm.phone"
            placeholder="搜索手机号"
            clearable
            prefix-icon="Phone"
            class="search-input"
            @input="handlePhoneInput"
            maxlength="11"
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
      <el-button type="primary" :icon="Plus" @click="handleAdd" class="add-btn">
        新增用户
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
        class="user-table"
        header-row-class-name="table-header"
        row-class-name="table-row"
      >
        <el-table-column prop="username" label="用户名" width="140">
          <template #default="{ row }">
            <div class="username-cell">
              <el-icon class="user-icon"><User /></el-icon>
              <span>{{ row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="昵称" width="140" />
        <el-table-column prop="email" label="邮箱" width="200" show-overflow-tooltip />
        <el-table-column prop="phone" label="手机号" width="140" />
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
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right" align="center">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                type="primary"
                size="small"
                @click="handleEdit(row)"
                class="action-btn"
              >
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button
                :type="row.status === 1 ? 'warning' : 'success'"
                size="small"
                @click="handleStatusChange(row)"
                class="action-btn"
              >
                <el-icon><Switch /></el-icon>
                {{ row.status === 1 ? '禁用' : '启用' }}
              </el-button>
              <el-button
                type="danger"
                size="small"
                @click="handleDelete(row)"
                class="action-btn"
              >
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
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
      width="560px"
      @close="handleDialogClose"
      class="user-dialog"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="90px"
        class="user-form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="formData.username"
            placeholder="请输入用户名（3-50字符）"
            :disabled="isEdit"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password" :required="!isEdit">
          <el-input
            v-model="formData.password"
            type="password"
            placeholder="请输入密码（6-20字符）"
            show-password
          />
          <div v-if="isEdit" class="form-tip">
            <el-icon><InfoFilled /></el-icon>
            不填写则不修改密码
          </div>
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input
            v-model="formData.nickname"
            placeholder="请输入昵称"
          />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="formData.email"
            placeholder="请输入邮箱地址"
          />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input
            v-model="formData.phone"
            placeholder="请输入手机号"
            maxlength="11"
            @input="handleFormPhoneInput"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status" class="status-radio">
            <el-radio :label="1" border>正常</el-radio>
            <el-radio :label="0" border>禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息（选填）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
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
import { Plus, Search, User, Edit, Delete, Switch, Phone, InfoFilled } from '@element-plus/icons-vue'
import { getUserList as getUserListApi, createUser, updateUser, deleteUser as deleteUserApi, updateUserStatus as updateUserStatusApi } from '@/api/userManagement'

// 搜索表单
const searchForm = reactive({
  username: '',
  phone: '',
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
const dialogTitle = computed(() => isEdit.value ? '编辑用户' : '新增用户')
const isEdit = ref(false)
const submitLoading = ref(false)

// 表单
const formRef = ref()
const formData = reactive({
  id: null,
  username: '',
  password: '',
  nickname: '',
  email: '',
  phone: '',
  status: 1,
  remark: ''
})

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '长度在3-50个字符', trigger: 'blur' }
  ],
  password: [
    {
      validator: (rule, value, callback) => {
        if (!isEdit.value && !value) {
          callback(new Error('请输入密码'))
        } else if (value && (value.length < 6 || value.length > 20)) {
          callback(new Error('密码长度在6-20个字符'))
        } else {
          callback()
        }
      },
      trigger: ['blur', 'change']
    }
  ],
  nickname: [
    { max: 50, message: '昵称长度不能超过50个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' },
    { max: 100, message: '邮箱长度不能超过100个字符', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  remark: [
    { max: 500, message: '备注长度不能超过500个字符', trigger: 'blur' }
  ]
}

// 获取用户列表
const getUserList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      username: searchForm.username || undefined,
      phone: searchForm.phone || undefined,
      status: searchForm.status
    }

    const response = await getUserListApi(params)

    if (response.code === 200) {
      tableData.value = response.data.list
      pagination.total = response.data.total
    } else {
      ElMessage.error(response.message || '获取用户列表失败')
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  getUserList()
}

// 限制手机号只能输入数字
const handlePhoneInput = (value) => {
  searchForm.phone = value.replace(/\D/g, '')
}

// 限制表单手机号只能输入数字
const handleFormPhoneInput = (value) => {
  formData.phone = value.replace(/\D/g, '')
}

// 重置
const handleReset = () => {
  searchForm.username = ''
  searchForm.phone = ''
  searchForm.status = null
}

// 分页
const handleSizeChange = (val) => {
  pagination.pageSize = val
  getUserList()
}

const handleCurrentChange = (val) => {
  pagination.pageNum = val
  getUserList()
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
  formData.username = row.username
  formData.password = ''
  formData.nickname = row.nickname
  formData.email = row.email
  formData.phone = row.phone
  formData.status = row.status
  formData.remark = row.remark
  dialogVisible.value = true
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除用户"${row.username}"吗？删除后将无法恢复。`,
    '删除确认',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
      draggable: true
    }
  ).then(async () => {
    try {
      const response = await deleteUserApi(row.id)
      if (response.code === 200) {
        ElMessage.success('删除成功')
        getUserList()
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
    `确定要${action}用户"${row.username}"吗？`,
    '状态变更',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      draggable: true
    }
  ).then(async () => {
    try {
      const response = await updateUserStatusApi(row.id, newStatus)
      if (response.code === 200) {
        ElMessage.success(`${action}成功`)
        getUserList()
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
      username: formData.username,
      nickname: formData.nickname || undefined,
      email: formData.email || undefined,
      phone: formData.phone || undefined,
      status: formData.status,
      remark: formData.remark || undefined
    }

    // 如果是编辑模式
    if (isEdit.value) {
      // 只有填写了密码才提交密码字段
      if (formData.password) {
        submitData.password = formData.password
      }
      const response = await updateUser(formData.id, submitData)
      if (response.code === 200) {
        ElMessage.success('更新成功')
        dialogVisible.value = false
        getUserList()
      } else {
        ElMessage.error(response.message || '更新失败')
      }
    } else {
      // 创建模式，密码必填
      submitData.password = formData.password
      const response = await createUser(submitData)
      if (response.code === 200) {
        ElMessage.success('创建成功')
        dialogVisible.value = false
        getUserList()
      } else {
        ElMessage.error(response.message || '创建失败')
      }
    }
  } catch (error) {
    console.error('提交失败:', error)
    // 表单验证失败时不显示错误提示（Element Plus会自动显示字段错误）
    // 只有API调用失败时才显示错误提示
  } finally {
    submitLoading.value = false
  }
}

// 关闭对话框
const handleDialogClose = () => {
  formRef.value?.resetFields()
  formData.id = null
  formData.username = ''
  formData.password = ''
  formData.nickname = ''
  formData.email = ''
  formData.phone = ''
  formData.status = 1
  formData.remark = ''
}

// 初始化
onMounted(() => {
  getUserList()
})
</script>

<style scoped>
/* 导入优雅字体 */
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;600;700&family=Inter:wght@300;400;500;600&display=swap');

.user-management {
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
.user-table {
  font-family: 'Inter', sans-serif;
}

.user-table :deep(.table-header) {
  background: #f8f9fa;
}

.user-table :deep(.table-header th) {
  font-weight: 600;
  font-size: 13px;
  color: #495057;
  letter-spacing: 0.5px;
  text-transform: uppercase;
  padding: 12px 12px;
  border-bottom: 2px solid #e9ecef;
}

.user-table :deep(.table-row) {
  transition: all 0.3s ease;
}

.user-table :deep(.table-row:hover) {
  background: #f8f9fa;
  transform: scale(1.01);
}

.user-table :deep(.el-table__cell) {
  padding: 12px 12px;
  border-bottom: 1px solid #f1f3f5;
}

.username-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-icon {
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
  gap: 6px;
  justify-content: center;
  align-items: center;
  flex-wrap: nowrap;
}

.action-btn {
  font-weight: 500;
  font-size: 12px;
  padding: 5px 10px;
  border-radius: 6px;
  transition: all 0.2s ease;
  display: inline-flex;
  align-items: center;
  gap: 3px;
  white-space: nowrap;
  border: none;
  box-shadow: none !important;
}

.action-btn:hover {
  box-shadow: none !important;
}

.action-btn.el-button--primary {
  background: #3b82f6;
  color: white;
  border: none;
}

.action-btn.el-button--primary:hover {
  background: #2563eb;
  border: none;
  transform: translateY(-1px);
}

.action-btn.el-button--warning {
  background: #f59e0b;
  color: white;
  border: none;
}

.action-btn.el-button--warning:hover {
  background: #d97706;
  border: none;
  transform: translateY(-1px);
}

.action-btn.el-button--success {
  background: #10b981;
  color: white;
  border: none;
}

.action-btn.el-button--success:hover {
  background: #059669;
  border: none;
  transform: translateY(-1px);
}

.action-btn.el-button--danger {
  background: #ef4444;
  color: white;
  border: none;
}

.action-btn.el-button--danger:hover {
  background: #dc2626;
  border: none;
  transform: translateY(-1px);
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
.user-dialog :deep(.el-dialog) {
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

.user-dialog :deep(.el-dialog__header) {
  padding: 32px 32px 24px;
  border-bottom: 1px solid #f1f3f5;
}

.user-dialog :deep(.el-dialog__title) {
  font-family: 'Playfair Display', serif;
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
}

.user-dialog :deep(.el-dialog__body) {
  padding: 32px;
}

.user-form :deep(.el-form-item__label) {
  font-family: 'Inter', sans-serif;
  font-weight: 500;
  color: #495057;
}

.user-form :deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 0 0 1px #e0e0e0 inset;
  transition: all 0.3s ease;
}

.user-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c0c0c0 inset;
}

.user-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px #dc2626 inset;
}

.user-form :deep(.el-textarea__inner) {
  border-radius: 10px;
  border: 1px solid #e0e0e0;
  transition: all 0.3s ease;
}

.user-form :deep(.el-textarea__inner:hover) {
  border-color: #c0c0c0;
}

.user-form :deep(.el-textarea__inner:focus) {
  border-color: #dc2626;
  box-shadow: 0 0 0 3px rgba(220, 38, 38, 0.1);
}

.form-tip {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #6c757d;
  margin-top: 8px;
}

.status-radio :deep(.el-radio) {
  margin-right: 16px;
}

.status-radio :deep(.el-radio.is-bordered) {
  border-radius: 10px;
  padding: 10px 20px;
  transition: all 0.3s ease;
}

.status-radio :deep(.el-radio.is-bordered:hover) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 24px 32px 32px;
  border-top: 1px solid #f1f3f5;
}

.cancel-btn {
  border-radius: 10px;
  padding: 12px 28px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.cancel-btn:hover {
  transform: translateY(-2px);
}

.submit-btn {
  border-radius: 10px;
  padding: 12px 28px;
  font-weight: 500;
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  border: none;
  transition: all 0.3s ease;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(220, 38, 38, 0.3);
}

/* 响应式 */
@media (max-width: 768px) {
  .user-management {
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
}
</style>
