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
        <el-table-column prop="username" label="用户名" min-width="140">
          <template #default="{ row }">
            <div class="username-cell">
              <el-icon class="user-icon"><User /></el-icon>
              <span>{{ row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="昵称" min-width="140" />
        <el-table-column prop="deptName" label="所属部门" min-width="140" show-overflow-tooltip>
          <template #default="{ row }">
            <span>{{ row.deptName || '无部门' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="200" show-overflow-tooltip />
        <el-table-column prop="phone" label="手机号" min-width="140" />
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
        <el-table-column prop="createTime" label="创建时间" min-width="180" />
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
                      v-if="hasPermission('system:user:position')"
                      :disabled="row.username === 'admin'"
                      command="position"
                      class="dropdown-item-custom"
                    >
                      <el-icon class="item-icon info-icon"><Briefcase /></el-icon>
                      <span class="item-text">分配岗位</span>
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
      width="650px"
      @close="handleDialogClose"
      class="user-dialog"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <div class="dialog-content-wrapper">
        <el-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          class="user-form"
          label-position="top"
        >
          <div class="form-grid">
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
                <span>留空则不修改密码</span>
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
            <el-form-item label="所属部门" prop="deptId">
              <el-tree-select
                v-model="formData.deptId"
                :data="deptOptions"
                check-strictly
                :render-after-expand="false"
                placeholder="请选择所属部门"
                value-key="id"
                :props="{ label: 'deptName', value: 'id', children: 'children', disabled: 'disabled' }"
                class="form-tree-select"
                clearable
              />
            </el-form-item>
          </div>
          
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

    <!-- 分配岗位对话框 -->
    <el-dialog
      v-model="positionDialogVisible"
      title="分配岗位"
      width="500px"
      @close="handlePositionDialogClose"
      class="position-dialog"
    >
      <el-checkbox-group v-model="selectedPositions" class="position-checkbox-group">
        <el-checkbox
          v-for="position in positionList"
          :key="position.id"
          :label="position.id"
          class="position-checkbox"
        >
          {{ position.positionName }}
          <el-tag v-if="position.status === 0" type="danger" size="small" style="margin-left: 8px">
            已禁用
          </el-tag>
        </el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="positionDialogVisible = false" class="cancel-btn">取消</el-button>
          <el-button
            type="primary"
            @click="handlePositionSubmit"
            :loading="positionSubmitting"
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
import { Plus, Search, User, UserFilled, Edit, Delete, Switch, Phone, InfoFilled, MoreFilled, Operation, Briefcase } from '@element-plus/icons-vue'
import { getUserList as getUserListApi, createUser, updateUser, deleteUser as deleteUserApi, updateUserStatus as updateUserStatusApi, getUserRoles, assignRoles, getUserPositions, assignPositions } from '@/api/userManagement'
import { getRoleList } from '@/api/role'
import { getPositionList } from '@/api/position'
import { getDepartmentList } from '@/api/dept'

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
  remark: '',
  deptId: null
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

// 岗位分配对话框
const positionDialogVisible = ref(false)
const positionList = ref([])
const selectedPositions = ref([])
const positionSubmitting = ref(false)

// 部门选项
const deptOptions = ref([])

// 扁平结构转树形结构（过滤已被停用的部门）
const handleTreeForUser = (data) => {
  const result = []
  if (!Array.isArray(data)) return result
  const map = {}
  data.forEach(item => {
    map[item.id] = { ...item, disabled: item.status === 0, children: [] }
  })
  data.forEach(item => {
    const parent = map[item.parentId]
    if (parent) {
      parent.children.push(map[item.id])
    } else {
      result.push(map[item.id])
    }
  })
  return result
}

const loadDeptOptions = async () => {
  try {
    const response = await getDepartmentList()
    if (response.code === 200) {
      deptOptions.value = handleTreeForUser(response.data)
    }
  } catch (error) {
    console.error('加载部门树选项失败:', error)
  }
}

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
  formData.deptId = row.deptId || null
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
      remark: formData.remark || undefined,
      deptId: formData.deptId || 0
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
  formData.deptId = null
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

// 分配岗位
const handleAssignPosition = async (row) => {
  currentUserId.value = row.id
  positionDialogVisible.value = true

  // 加载岗位列表
  try {
    const response = await getPositionList({ page: 1, size: 100 })
    if (response.code === 200) {
      positionList.value = response.data.records
    }
  } catch (error) {
    ElMessage.error('加载岗位列表失败')
    return
  }

  // 加载用户已有的岗位
  try {
    const response = await getUserPositions(row.id)
    if (response.code === 200) {
      selectedPositions.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载用户岗位失败')
  }
}

// 提交岗位分配
const handlePositionSubmit = async () => {
  try {
    positionSubmitting.value = true
    const response = await assignPositions(currentUserId.value, selectedPositions.value)
    if (response.code === 200) {
      ElMessage.success('分配岗位成功')
      positionDialogVisible.value = false
    }
  } catch (error) {
    console.error('分配岗位失败:', error)
    ElMessage.error('分配岗位失败')
  } finally {
    positionSubmitting.value = false
  }
}

// 关闭岗位对话框
const handlePositionDialogClose = () => {
  selectedPositions.value = []
  currentUserId.value = null
}

// 处理下拉菜单命令
const handleDropdownCommand = (command, row) => {
  if (command === 'edit') {
    handleEdit(row)
  } else if (command === 'role') {
    handleAssignRole(row)
  } else if (command === 'position') {
    handleAssignPosition(row)
  } else if (command === 'status') {
    handleStatusChange(row)
  } else if (command === 'delete') {
    handleDelete(row)
  }
}

// 初始化
onMounted(() => {
  getUserList()
  loadDeptOptions()
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Fira+Code:wght@400;500;600;700&family=Fira+Sans:wght@300;400;500;600;700&display=swap');

.user-management {
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

/* 通用卡片容器样式 */
.search-section, .toolbar, .table-container {
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(124, 58, 237, 0.05);
  border: 1px solid rgba(124, 58, 237, 0.1);
  margin-bottom: 16px;
  transition: all 0.3s ease;
}

/* 搜索区域 */
.search-section {
  padding: 16px 24px;
}
.search-form {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}
.search-form :deep(.el-form-item) {
  margin: 0 !important;
}
.search-input, .search-select {
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

/* 按钮通用交互 */
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
  box-shadow: 0 4px 12px rgba(124, 58, 237, 0.3);
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

.user-table {
  font-family: 'Fira Sans', sans-serif;
  --el-table-border-color: #F3F4F6;
  --el-table-header-bg-color: #F3E8FF;
  --el-table-row-hover-bg-color: #FAF5FF;
}
.user-table :deep(.table-header th) {
  font-family: 'Fira Code', monospace;
  font-weight: 600;
  color: #4C1D95;
  background-color: var(--el-table-header-bg-color);
  padding: 12px;
  border-bottom: 2px solid #E9D5FF;
}
.user-table :deep(.table-row) {
  transition: all 0.2s ease;
  cursor: pointer;
}
.user-table :deep(.table-row:hover) {
  transform: translateX(2px);
}
.user-table :deep(.el-table__cell) {
  padding: 14px 12px;
  border-bottom: 1px solid #F3F4F6;
}

.username-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}
.user-icon {
  color: #7C3AED;
}
.status-tag {
  border-radius: 6px;
  font-weight: 600;
  font-size: 12px;
  letter-spacing: 0.5px;
}

/* 操作菜单按钮 */
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
}
.action-menu-btn:hover {
  background: #7C3AED;
  color: white;
  box-shadow: 0 4px 12px rgba(124, 58, 237, 0.2);
  transform: translateY(-2px);
}

/* 下拉菜单 */
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
}
.dropdown-item-custom.danger-item:hover {
  background: #FAF5FF;
  color: #7C3AED;
}
.dropdown-item-custom .item-icon {
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
:deep(.el-dialog.user-dialog),
:deep(.el-dialog.role-dialog),
:deep(.el-dialog.position-dialog) {
  border-radius: 16px;
  overflow: hidden;
  background: #ffffff;
  box-shadow: 0 20px 25px -5px rgba(124, 58, 237, 0.08), 0 10px 10px -5px rgba(124, 58, 237, 0.04);
  border: 1px solid rgba(124, 58, 237, 0.08);
}
:deep(.el-dialog.user-dialog .el-dialog__header),
:deep(.el-dialog.role-dialog .el-dialog__header),
:deep(.el-dialog.position-dialog .el-dialog__header) {
  background: #ffffff;
  margin: 0;
  padding: 24px 28px 20px 28px;
  border-bottom: 1px solid rgba(124, 58, 237, 0.06);
}
:deep(.el-dialog.user-dialog .el-dialog__title),
:deep(.el-dialog.role-dialog .el-dialog__title),
:deep(.el-dialog.position-dialog .el-dialog__title) {
  font-family: 'Fira Sans', sans-serif;
  color: #1F2937;
  font-weight: 600;
  font-size: 18px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}
:deep(.el-dialog.user-dialog .el-dialog__title::before),
:deep(.el-dialog.role-dialog .el-dialog__title::before),
:deep(.el-dialog.position-dialog .el-dialog__title::before) {
  content: "";
  display: inline-block;
  width: 4px;
  height: 16px;
  background: linear-gradient(135deg, #7C3AED 0%, #6D28D9 100%);
  border-radius: 2px;
}
:deep(.el-dialog.user-dialog .el-dialog__headerbtn),
:deep(.el-dialog.role-dialog .el-dialog__headerbtn),
:deep(.el-dialog.position-dialog .el-dialog__headerbtn) {
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
:deep(.el-dialog.user-dialog .el-dialog__headerbtn:hover),
:deep(.el-dialog.role-dialog .el-dialog__headerbtn:hover),
:deep(.el-dialog.position-dialog .el-dialog__headerbtn:hover) {
  background: #F3E8FF;
}
:deep(.el-dialog.user-dialog .el-dialog__headerbtn .el-dialog__close),
:deep(.el-dialog.role-dialog .el-dialog__headerbtn .el-dialog__close),
:deep(.el-dialog.position-dialog .el-dialog__headerbtn .el-dialog__close) {
  color: #9CA3AF;
  transition: all 0.2s ease;
}
:deep(.el-dialog.user-dialog .el-dialog__headerbtn:hover .el-dialog__close),
:deep(.el-dialog.role-dialog .el-dialog__headerbtn:hover .el-dialog__close),
:deep(.el-dialog.position-dialog .el-dialog__headerbtn:hover .el-dialog__close) {
  color: #7C3AED;
}

.user-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: #4B5563;
  font-size: 13px;
  padding-bottom: 6px !important;
}
.user-form :deep(.el-input__wrapper),
.user-form :deep(.el-textarea__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px #E5E7EB inset !important;
  background: #F9FAFB;
  transition: all 0.2s ease;
}
.user-form :deep(.el-input__wrapper:hover),
.user-form :deep(.el-textarea__wrapper:hover) {
  box-shadow: 0 0 0 1px #C4B5FD inset !important;
  background: #ffffff;
}
.user-form :deep(.el-input__wrapper.is-focus),
.user-form :deep(.el-textarea__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(124, 58, 237, 0.2) inset, 0 0 0 3px rgba(124, 58, 237, 0.15) !important;
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

/* 角色与岗位分配复选框美化 */
.role-checkbox-group, .position-checkbox-group {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  padding: 12px 8px;
}
.role-checkbox, .position-checkbox {
  border: 1px solid #E5E7EB !important;
  border-radius: 8px !important;
  padding: 10px 16px !important;
  background: #F9FAFB !important;
  margin: 0 !important;
  display: flex !important;
  align-items: center !important;
  transition: all 0.2s ease !important;
}
.role-checkbox:hover, .position-checkbox:hover {
  border-color: #C4B5FD !important;
  background: #ffffff !important;
}
.role-checkbox.is-checked, .position-checkbox.is-checked {
  border-color: #7C3AED !important;
  background: #F5F3FF !important;
}
.role-checkbox :deep(.el-checkbox__input.is-checked .el-checkbox__inner),
.position-checkbox :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #7C3AED !important;
  border-color: #7C3AED !important;
}
.role-checkbox :deep(.el-checkbox__label),
.position-checkbox :deep(.el-checkbox__label) {
  color: #374151 !important;
  font-weight: 500 !important;
}
.role-checkbox :deep(.el-checkbox__input.is-checked + .el-checkbox__label),
.position-checkbox :deep(.el-checkbox__input.is-checked + .el-checkbox__label) {
  color: #7C3AED !important;
}

.dialog-footer {
  padding: 16px 24px;
  border-top: 1px solid rgba(124, 58, 237, 0.06);
  margin: 10px -24px -20px -24px;
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

/* 响应式 */
@media (max-width: 768px) {
  .user-management {
    padding: 16px;
  }
  .search-input, .search-select {
    width: 100%;
  }
  .form-grid {
    grid-template-columns: 1fr;
    gap: 0;
  }
  .role-checkbox-group, .position-checkbox-group {
    grid-template-columns: 1fr;
  }
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
.user-form :deep(.el-form-item) {
  margin-bottom: 20px;
}
</style>

