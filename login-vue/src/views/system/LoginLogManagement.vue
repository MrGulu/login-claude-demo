<template>
  <div class="login-log-management">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">登录日志</h1>
      <p class="page-subtitle">查看系统用户的登录历史，分析登录状态与客户端环境</p>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item>
          <el-input
            v-model="searchForm.username"
            placeholder="搜索用户名"
            clearable
            prefix-icon="User"
            class="search-input"
          />
        </el-form-item>
        <el-form-item>
          <el-select
            v-model="searchForm.status"
            placeholder="登录状态"
            clearable
            class="search-select"
          >
            <el-option label="全部状态" :value="null" />
            <el-option label="登录成功" :value="1" />
            <el-option label="登录失败" :value="0" />
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

    <!-- 数据表格 -->
    <div class="table-container">
      <div class="toolbar">
        <div class="toolbar-info">
          共 <span class="count">{{ pagination.total }}</span> 条记录
        </div>
      </div>

      <el-table
        :data="tableData"
        v-loading="loading"
        class="log-table"
        header-row-class-name="table-header"
        row-class-name="table-row"
      >
        <el-table-column prop="username" label="用户名" min-width="120" show-overflow-tooltip />
        <el-table-column prop="ipAddress" label="IP 地址" min-width="130" />
        <el-table-column prop="loginLocation" label="登录地点" min-width="120" show-overflow-tooltip />
        <el-table-column prop="browser" label="浏览器" min-width="150" show-overflow-tooltip />
        <el-table-column prop="os" label="操作系统" min-width="120" show-overflow-tooltip />
        <el-table-column label="登录状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag
              v-if="row.status === 1"
              type="success"
              effect="light"
              class="status-tag"
            >
              登录成功
            </el-tag>
            <el-tag
              v-else
              type="danger"
              effect="light"
              class="status-tag"
            >
              登录失败
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="message" label="提示信息" min-width="150" show-overflow-tooltip />
        <el-table-column label="登录时间" min-width="180">
          <template #default="{ row }">
            <span class="time-cell">{{ formatLoginTime(row.loginTime) }}</span>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, User } from '@element-plus/icons-vue'
import { getLoginLogList } from '@/api/loginLog'

// 搜索表单
const searchForm = reactive({
  username: '',
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

// 获取登录日志数据
const getLogData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.pageNum,
      size: pagination.pageSize,
      username: searchForm.username || undefined,
      status: searchForm.status
    }

    const response = await getLoginLogList(params)

    if (response.code === 200) {
      tableData.value = response.data.records
      pagination.total = response.data.total
    } else {
      ElMessage.error(response.message || '获取登录日志失败')
    }
  } catch (error) {
    console.error('获取登录日志失败:', error)
    ElMessage.error('获取登录日志失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  getLogData()
}

// 重置
const handleReset = () => {
  searchForm.username = ''
  searchForm.status = null
  handleSearch()
}

// 分页
const handleSizeChange = (val) => {
  pagination.pageSize = val
  getLogData()
}

const handleCurrentChange = (val) => {
  pagination.pageNum = val
  getLogData()
}

// 格式化登录时间
const formatLoginTime = (timeStr) => {
  if (!timeStr) return ''
  try {
    const dateStr = timeStr.replace('T', ' ')
    const dotIndex = dateStr.indexOf('.')
    if (dotIndex !== -1) {
      return dateStr.substring(0, dotIndex)
    }
    return dateStr
  } catch (e) {
    return timeStr
  }
}

onMounted(() => {
  getLogData()
})
</script>

<style scoped>
/* 导入优雅字体 */
@import url('https://fonts.googleapis.com/css2?family=Fira+Code:wght@400;500;600;700&family=Fira+Sans:wght@300;400;500;600;700&display=swap');

.login-log-management {
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
.search-section, .table-container {
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

.search-btn {
  background: linear-gradient(135deg, #7C3AED 0%, #6D28D9 100%) !important;
  border: none !important;
  font-family: 'Fira Sans', sans-serif;
  font-weight: 500;
  color: white !important;
}

.search-btn:hover {
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

/* 表格容器 */
.table-container {
  padding: 24px;
  overflow: hidden;
}

.toolbar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 12px;
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

/* 表格样式 */
.log-table {
  font-family: 'Fira Sans', sans-serif;
  --el-table-border-color: #F3F4F6;
  --el-table-header-bg-color: #F3E8FF;
  --el-table-row-hover-bg-color: #FAF5FF;
}

.log-table :deep(.table-header th) {
  font-family: 'Fira Code', monospace;
  font-weight: 600;
  color: #4C1D95;
  background-color: var(--el-table-header-bg-color);
  padding: 12px;
  border-bottom: 2px solid #E9D5FF;
}

.log-table :deep(.table-row) {
  transition: all 0.2s ease;
}

.log-table :deep(.table-row:hover) {
  transform: translateX(2px);
}

.log-table :deep(.el-table__cell) {
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

.time-cell {
  font-family: 'Fira Code', monospace;
  font-size: 13px;
  color: #4B5563;
}
</style>
