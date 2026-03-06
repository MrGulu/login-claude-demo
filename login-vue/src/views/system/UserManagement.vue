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
      <el-button v-permission="'system:user:add'" type="primary" :icon="Plus" @click="handleAdd" class="add-btn">
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
                      v-if="hasPermission('system:user:edit')"
                      command="edit"
                      class="dropdown-item-custom"
                    >
                      <el-icon class="item-icon primary-icon"><Edit /></el-icon>
                      <span class="item-text">编辑用户</span>
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-if="hasPermission('system:user:role')"
                      :disabled="row.username === 'admin'"
                      command="role"
                      class="dropdown-item-custom"
                    >
                      <el-icon class="item-icon info-icon"><UserFilled /></el-icon>
                      <span class="item-text">分配角色</span>
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-if="hasPermission('system:user:status')"
                      :disabled="row.username === 'admin'"
                      command="status"
                      class="dropdown-item-custom"
                    >
                      <el-icon class="item-icon warning-icon"><Switch /></el-icon>
                      <span class="item-text">{{ row.status === 1 ? '禁用用户' : '启用用户' }}</span>
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-if="hasPermission('system:user:delete')"
                      :disabled="row.username === 'admin'"
                      command="delete"
                      divided
                      class="dropdown-item-custom danger-item"
                    >
                      <el-icon class="item-icon danger-icon"><Delete /></el-icon>
                      <span class="item-text">删除用户</span>
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
      width="850px"
      @close="handleDialogClose"
      class="user-dialog"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        class="user-form"
        label-position="top"
      >
        <div class="form-row">
          <el-form-item label="用户名" prop="username" class="form-col-small">
            <el-input
              v-model="formData.username"
              placeholder="请输入用户名（3-50字符）"
              :disabled="isEdit"
              size="large"
            />
          </el-form-item>
          <el-form-item label="密码" prop="password" :required="!isEdit" class="form-col-small">
            <el-input
              v-model="formData.password"
              type="password"
              placeholder="请输入密码（6-20字符）"
              show-password
              size="large"
            />
            <div v-if="isEdit" class="form-tip">
              <el-icon><InfoFilled /></el-icon>
              <span>留空则不修改密码</span>
            </div>
          </el-form-item>
          <el-form-item label="昵称" prop="nickname" class="form-col-small">
            <el-input
              v-model="formData.nickname"
              placeholder="请输入昵称"
              size="large"
            />
          </el-form-item>
        </div>

        <div class="form-row">
          <el-form-item label="邮箱" prop="email" class="form-col-small">
            <el-input
              v-model="formData.email"
              placeholder="请输入邮箱地址"
              size="large"
            />
          </el-form-item>
          <el-form-item label="手机号" prop="phone" class="form-col-small">
            <el-input
              v-model="formData.phone"
              placeholder="请输入手机号"
              maxlength="11"
              @input="handleFormPhoneInput"
              size="large"
            />
          </el-form-item>
          <el-form-item label="状态" prop="status" class="form-col-small">
            <el-radio-group v-model="formData.status" class="status-radio" size="large">
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
          <el-button @click="dialogVisible = false" size="large" class="cancel-btn">取消</el-button>
          <el-button
            type="primary"
            @click="handleSubmit"
            :loading="submitLoading"
            size="large"
            class="submit-btn"
          >
            {{ isEdit ? '保存修改' : '立即创建' }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 分配角色对话框 -->
    <el-dialog
      v-model="roleDialogVisible"
      title="分配角色"
      width="500px"
      @close="handleRoleDialogClose"
      class="role-dialog"
    >
      <el-checkbox-group v-model="selectedRoles" class="role-checkbox-group">
        <el-checkbox
          v-for="role in roleList"
          :key="role.id"
          :label="role.id"
          :disabled="role.isSystem === 1"
          class="role-checkbox"
        >
          {{ role.roleName }}
          <el-tag v-if="role.isSystem === 1" type="danger" size="small" style="margin-left: 8px">
            系统角色
          </el-tag>
        </el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="roleDialogVisible = false" class="cancel-btn">取消</el-button>
          <el-button
            type="primary"
            @click="handleRoleSubmit"
            :loading="roleSubmitting"
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
import { Plus, Search, User, UserFilled, Edit, Delete, Switch, Phone, InfoFilled, MoreFilled, Operation } from '@element-plus/icons-vue'
import { getUserList as getUserListApi, createUser, updateUser, deleteUser as deleteUserApi, updateUserStatus as updateUserStatusApi, getUserRoles, assignRoles } from '@/api/userManagement'
import { getRoleList } from '@/api/role'

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

// 角色分配对话框
const roleDialogVisible = ref(false)
const roleList = ref([])
const selectedRoles = ref([])
const currentUserId = ref(null)
const roleSubmitting = ref(false)

// 权限检查函数
const hasPermission = (permission) => {
  const userPerms = JSON.parse(localStorage.getItem('userPerms') || '[]')
  return userPerms.includes(permission)
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

// 分配角色
const handleAssignRole = async (row) => {
  currentUserId.value = row.id
  roleDialogVisible.value = true

  // 加载角色列表
  try {
    const response = await getRoleList({ page: 1, size: 100 })
    if (response.code === 200) {
      roleList.value = response.data.records
    }
  } catch (error) {
    ElMessage.error('加载角色列表失败')
    return
  }

  // 加载用户已有的角色
  try {
    const response = await getUserRoles(row.id)
    if (response.code === 200) {
      selectedRoles.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载用户角色失败')
  }
}

// 提交角色分配
const handleRoleSubmit = async () => {
  if (selectedRoles.value.length === 0) {
    ElMessage.warning('请至少选择一个角色')
    return
  }

  try {
    roleSubmitting.value = true
    const response = await assignRoles(currentUserId.value, selectedRoles.value)
    if (response.code === 200) {
      ElMessage.success('分配角色成功')
      roleDialogVisible.value = false
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '分配角色失败')
  } finally {
    roleSubmitting.value = false
  }
}

// 关闭角色对话框
const handleRoleDialogClose = () => {
  selectedRoles.value = []
  currentUserId.value = null
}

// 处理下拉菜单命令
const handleDropdownCommand = (command, row) => {
  if (command === 'edit') {
    handleEdit(row)
  } else if (command === 'role') {
    handleAssignRole(row)
  } else if (command === 'status') {
    handleStatusChange(row)
  } else if (command === 'delete') {
    handleDelete(row)
  }
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

.info-icon {
  color: #6366f1;
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
.user-dialog :deep(.el-dialog) {
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  overflow: hidden;
}

.user-dialog :deep(.el-dialog__header) {
  padding: 24px 32px;
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  border-bottom: none;
}

.user-dialog :deep(.el-dialog__title) {
  font-family: 'Playfair Display', serif;
  font-size: 22px;
  font-weight: 700;
  color: #ffffff;
  letter-spacing: 0.5px;
}

.user-dialog :deep(.el-dialog__headerbtn .el-dialog__close) {
  color: #ffffff;
  font-size: 20px;
}

.user-dialog :deep(.el-dialog__headerbtn .el-dialog__close):hover {
  color: #fef2f2;
}

.user-dialog :deep(.el-dialog__body) {
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
  flex: 0 0 250px;
  margin-bottom: 0 !important;
}

.form-col-full {
  flex: 1;
  margin-bottom: 0 !important;
}

.user-form :deep(.el-form-item__label) {
  font-family: 'Inter', sans-serif;
  font-weight: 600;
  color: #1f2937;
  font-size: 14px;
  margin-bottom: 8px;
  padding: 0;
  line-height: 1.5;
}

.user-form :deep(.el-form-item__content) {
  line-height: normal;
}

.user-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px #d1d5db inset;
  transition: all 0.2s ease;
  background: #ffffff;
}

.user-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #9ca3af inset;
}

.user-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px #dc2626 inset;
}

.user-form :deep(.el-input__inner) {
  font-size: 14px;
  color: #111827;
}

.user-form :deep(.el-input__inner::placeholder) {
  color: #9ca3af;
}

.user-form :deep(.el-textarea__inner) {
  border-radius: 8px;
  border: 1px solid #d1d5db;
  transition: all 0.2s ease;
  font-size: 14px;
  line-height: 1.6;
  background: #ffffff;
  resize: none;
}

.user-form :deep(.el-textarea__inner:hover) {
  border-color: #9ca3af;
}

.user-form :deep(.el-textarea__inner:focus) {
  border-color: #dc2626;
  box-shadow: 0 0 0 3px rgba(220, 38, 38, 0.1);
}

.user-form :deep(.el-textarea__inner::placeholder) {
  color: #9ca3af;
}

.form-tip {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #f59e0b;
  margin-top: 6px;
  font-weight: 500;
}

.form-tip .el-icon {
  font-size: 14px;
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

/* 角色对话框 */
.role-dialog :deep(.el-dialog) {
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

.role-checkbox-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.role-checkbox {
  padding: 12px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.role-checkbox:hover {
  border-color: #dc2626;
  background: rgba(220, 38, 38, 0.05);
}

.role-checkbox :deep(.el-checkbox__label) {
  font-weight: 500;
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

  /* 对话框响应式 */
  .user-dialog :deep(.el-dialog) {
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

  .user-dialog :deep(.el-dialog__header),
  .user-dialog :deep(.el-dialog__body),
  .dialog-footer {
    padding-left: 20px;
    padding-right: 20px;
  }
}
</style>
