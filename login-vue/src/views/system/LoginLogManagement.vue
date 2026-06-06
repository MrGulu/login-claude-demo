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
        <el-table-column prop="loginTime" label="登录时间" min-width="180" />
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

onMounted(() => {
  getLogData()
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;600;700&family=Inter:wght@300;400;500;600&display=swap');

.login-log-management {
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

.table-container {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  animation: slideUp 0.8s ease-out 0.2s both;
}

.toolbar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 12px;
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

.log-table {
  font-family: 'Inter', sans-serif;
}

.log-table :deep(.table-header) {
  background: #f8f9fa;
}

.log-table :deep(.table-header th) {
  font-weight: 600;
  font-size: 13px;
  color: #495057;
  letter-spacing: 0.5px;
  text-transform: uppercase;
  padding: 12px 12px;
  border-bottom: 2px solid #e9ecef;
}

.log-table :deep(.table-row) {
  transition: all 0.3s ease;
}

.log-table :deep(.table-row:hover) {
  background: #f8f9fa;
  transform: scale(1.002);
}

.log-table :deep(.el-table__cell) {
  padding: 12px 12px;
  border-bottom: 1px solid #f1f3f5;
}

.status-tag {
  font-weight: 500;
  padding: 4px 12px;
  border-radius: 6px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px;
}

.custom-pagination :deep(.el-pager li.is-active) {
  background-color: #dc2626 !important;
  color: #ffffff !important;
}
</style>
