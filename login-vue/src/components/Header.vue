<template>
  <header class="header">
    <div class="header-left">
      <el-button
        class="menu-toggle"
        :icon="Expand"
        circle
        @click="$emit('toggle-sidebar')"
      />
      <h2 class="page-title">系统中心</h2>
    </div>

    <div class="header-right">
      <!-- 通知铃铛图标 -->
      <el-badge 
        :value="unreadCount" 
        :max="99" 
        :hidden="unreadCount === 0" 
        class="notification-badge"
      >
        <el-button :icon="Bell" circle @click="handleBellClick" class="bell-btn" />
      </el-badge>

      <!-- 用户菜单 -->
      <el-dropdown @command="handleCommand">
        <div class="user-info">
          <el-avatar :size="40" :src="userAvatar">
            {{ userInitial }}
          </el-avatar>
          <span class="username">{{ userName }}</span>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">
              <el-icon><User /></el-icon>
              个人资料
            </el-dropdown-item>
            <el-dropdown-item command="settings">
              <el-icon><Setting /></el-icon>
              设置
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <!-- 消息通知抽屉 -->
    <el-drawer
      v-model="drawerVisible"
      title="通知公告中心"
      size="420px"
      direction="rtl"
      class="premium-drawer"
      append-to-body
    >
      <template #header>
        <div class="drawer-header-custom">
          <span class="drawer-title">通知公告中心</span>
          <el-button 
            v-if="unreadCount > 0"
            type="primary" 
            link 
            @click="handleReadAll" 
            class="read-all-link"
          >
            一键全部已读
          </el-button>
        </div>
      </template>

      <div v-loading="listLoading" class="notice-list-container">
        <el-empty 
          v-if="noticeList.length === 0" 
          description="暂无公告信息" 
          :image-size="120"
        />
        
        <template v-else>
          <div class="notice-items">
            <div 
              v-for="item in noticeList" 
              :key="item.id"
              class="notice-card-item"
              :class="{ 'unread': item.readStatus === 0, 'expanded': expandedId === item.id }"
              @click="toggleExpand(item)"
            >
              <div class="notice-card-top">
                <div class="title-wrapper">
                  <el-tag v-if="item.readStatus === 0" type="danger" size="small" effect="dark" class="status-badge">未读</el-tag>
                  <span v-else class="status-badge el-tag el-tag--info el-tag--light">已读</span>
                  <span class="notice-card-title">{{ item.title }}</span>
                </div>
                <span class="notice-card-time">{{ formatTime(item.createTime) }}</span>
              </div>

              <!-- 公告详情 -->
              <div class="notice-card-detail">
                <div class="notice-card-meta">
                  <span>发布人: {{ item.author }}</span>
                </div>
                <div class="notice-card-body" v-html="item.content"></div>
                
                <div class="notice-card-actions" v-if="item.readStatus === 0">
                  <el-button 
                    size="small" 
                    type="success" 
                    plain 
                    class="action-read-btn" 
                    @click.stop="handleMarkRead(item)"
                  >
                    设为已读
                  </el-button>
                </div>
              </div>
            </div>
          </div>

          <!-- 分页器组件 -->
          <div class="drawer-pagination-wrapper">
            <el-pagination
              v-model:current-page="currentPage"
              :page-size="pageSize"
              :total="total"
              layout="prev, pager, next"
              small
              background
              @current-change="loadNoticeList"
            />
          </div>
        </template>
      </div>
    </el-drawer>
  </header>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Expand, Bell, User, Setting, SwitchButton } from '@element-plus/icons-vue'
import { useUserInfo } from '../composables/useUserInfo'
import { getUnreadCount, getNoticeList, markAsRead, markAllAsRead } from '@/api/notice'

const router = useRouter()
const { userName, userAvatar, userInitial, clearUserInfo } = useUserInfo()

const unreadCount = ref(0)
const drawerVisible = ref(false)
const noticeList = ref([])
const listLoading = ref(false)
const expandedId = ref(null)

// 分页相关状态
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 格式化时间显示
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  // 仅显示月-日 时:分
  try {
    const date = new Date(timeStr.replace('T', ' '))
    const m = String(date.getMonth() + 1).padStart(2, '0')
    const d = String(date.getDate()).padStart(2, '0')
    const h = String(date.getHours()).padStart(2, '0')
    const min = String(date.getMinutes()).padStart(2, '0')
    return `${m}-${d} ${h}:${min}`
  } catch (e) {
    return timeStr
  }
}

// 展开/收起公告详情
const toggleExpand = async (item) => {
  if (expandedId.value === item.id) {
    expandedId.value = null
  } else {
    expandedId.value = item.id
    // 如果是未读的，点击展开时自动设为已读
    if (item.readStatus === 0) {
      await handleMarkRead(item)
    }
  }
}

// 获取未读总数
const loadUnreadCount = async () => {
  try {
    const response = await getUnreadCount()
    if (response.code === 200) {
      unreadCount.value = response.data
    }
  } catch (error) {
    console.error('获取未读数失败:', error)
  }
}

// 获取列表数据
const loadNoticeList = async () => {
  listLoading.value = true
  try {
    const response = await getNoticeList({ 
      page: currentPage.value, 
      size: pageSize.value, 
      status: 1 
    })
    if (response.code === 200) {
      noticeList.value = response.data.records
      total.value = Number(response.data.total)
    }
  } catch (error) {
    console.error('获取公告列表失败:', error)
  } finally {
    listLoading.value = false
  }
}

// 点击铃铛
const handleBellClick = () => {
  drawerVisible.value = true
  expandedId.value = null
  currentPage.value = 1  // 打开时重置为第一页
  loadNoticeList()
}

// 标记单条已读
const handleMarkRead = async (item) => {
  try {
    const response = await markAsRead(item.id)
    if (response.code === 200) {
      item.readStatus = 1
      loadUnreadCount()
    }
  } catch (error) {
    console.error('标记已读失败:', error)
  }
}

// 全部标为已读
const handleReadAll = async () => {
  try {
    const response = await markAllAsRead()
    if (response.code === 200) {
      ElMessage.success('全部已标记为已读')
      loadUnreadCount()
      loadNoticeList()
    }
  } catch (error) {
    console.error('一键已读失败:', error)
  }
}

// 下拉菜单事件
const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'settings':
      ElMessage.info('设置功能开发中')
      break
    case 'logout':
      handleLogout()
      break
  }
}

// 退出登录
const handleLogout = () => {
  localStorage.removeItem('token')
  clearUserInfo()
  ElMessage.success('已退出登录')
  router.push('/login')
}

// 监听通知状态变更事件
const onNoticeReadChange = () => {
  loadUnreadCount()
  if (drawerVisible.value) {
    loadNoticeList()
  }
}

onMounted(() => {
  loadUnreadCount()
  window.addEventListener('notice-read-change', onNoticeReadChange)
})

onUnmounted(() => {
  window.removeEventListener('notice-read-change', onNoticeReadChange)
})
</script>

<style scoped>
.header {
  position: fixed;
  top: 0;
  right: 0;
  left: 280px;
  height: 70px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(255, 255, 255, 0.85));
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 4px 20px rgba(124, 58, 237, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 40px;
  z-index: 100;
  transition: left 0.3s ease;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.menu-toggle {
  background: rgba(124, 58, 237, 0.1);
  border: none;
  color: var(--color-primary);
}

.menu-toggle:hover {
  background: rgba(124, 58, 237, 0.2);
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: var(--color-primary);
  margin: 0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.notification-badge {
  cursor: pointer;
}

.bell-btn {
  border: 1px solid rgba(124, 58, 237, 0.2);
  background: transparent;
  color: var(--color-primary);
  font-size: 18px;
  transition: all 0.3s ease;
}

.bell-btn:hover {
  background: rgba(124, 58, 237, 0.08);
  transform: scale(1.05);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 50px;
  transition: all 0.3s ease;
}

.user-info:hover {
  background: rgba(124, 58, 237, 0.05);
}

.username {
  font-size: 15px;
  font-weight: 500;
  color: #333;
}

.el-avatar {
  background: linear-gradient(135deg, var(--color-primary), var(--color-accent));
  color: white;
  font-weight: 600;
}

/* 消息抽屉定制化 */
.drawer-header-custom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding-right: 16px;
}

.drawer-title {
  font-size: 18px;
  font-weight: 700;
  color: #1a1a1a;
  position: relative;
  padding-left: 12px;
}

.drawer-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 18px;
  background: linear-gradient(to bottom, #7C3AED, #9F67FF);
  border-radius: 4px;
}

.read-all-link {
  font-size: 14px;
  color: #7C3AED;
  font-weight: 600;
  transition: all 0.3s;
}

.read-all-link:hover {
  color: #5B21B6;
  transform: translateY(-1px);
}

.notice-list-container {
  height: 100%;
  overflow-y: auto;
  padding: 8px 4px;
}

.notice-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.notice-card-item {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(124, 58, 237, 0.06);
  border-radius: 16px;
  padding: 18px;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(124, 58, 237, 0.02);
}

.notice-card-item:hover {
  background: #ffffff;
  transform: translateY(-3px) translateX(2px);
  box-shadow: 0 10px 25px rgba(124, 58, 237, 0.08);
  border-color: rgba(124, 58, 237, 0.25);
}

.notice-card-item.unread {
  background: linear-gradient(135deg, rgba(124, 58, 237, 0.03) 0%, rgba(255, 255, 255, 0.95) 100%);
  border-color: rgba(124, 58, 237, 0.15);
  box-shadow: 0 4px 16px rgba(124, 58, 237, 0.04);
}

.notice-card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.title-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
}

/* 状态徽章标签美化 */
.status-badge.el-tag {
  flex-shrink: 0;
  border-radius: 8px;
  font-weight: 600;
  padding: 0 10px;
  height: 24px;
  line-height: 22px;
}

/* 未读状态 - 紫金通透微章 */
.status-badge.el-tag--danger {
  background-color: rgba(124, 58, 237, 0.08) !important;
  border-color: rgba(124, 58, 237, 0.15) !important;
  color: #7C3AED !important;
}

/* 已读状态 - 灰色轻量徽章 */
.status-badge.el-tag--info {
  background-color: rgba(0, 0, 0, 0.03) !important;
  border-color: rgba(0, 0, 0, 0.06) !important;
  color: #8c939d !important;
}

.notice-card-title {
  font-size: 15px;
  font-weight: 600;
  color: #374151;
  line-height: 1.4;
  transition: color 0.3s;
}

.notice-card-item:hover .notice-card-title {
  color: #7C3AED;
}

.notice-card-item.unread .notice-card-title {
  color: #111827;
  font-weight: 700;
}

.notice-card-time {
  font-size: 11px;
  color: #9ca3af;
  white-space: nowrap;
  font-family: 'Fira Code', monospace;
}

/* 详情折叠机制 */
.notice-card-detail {
  max-height: 0;
  opacity: 0;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

.notice-card-item.expanded {
  border-color: rgba(124, 58, 237, 0.3);
  box-shadow: 0 12px 30px rgba(124, 58, 237, 0.1);
  background: #ffffff;
}

.notice-card-item.expanded .notice-card-detail {
  max-height: 350px;
  opacity: 1;
  margin-top: 14px;
  padding-top: 14px;
  border-top: 1px dashed rgba(124, 58, 237, 0.08);
}

.notice-card-meta {
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 10px;
  display: flex;
  justify-content: space-between;
}

.notice-card-body {
  font-size: 13.5px;
  line-height: 1.6;
  color: #4b5563;
  word-break: break-all;
}

.notice-card-actions {
  margin-top: 14px;
  display: flex;
  justify-content: flex-end;
}

/* 设为已读按钮紫金化 */
.action-read-btn.el-button--success.is-plain {
  font-size: 11px;
  border-radius: 10px;
  background-color: rgba(124, 58, 237, 0.06) !important;
  border-color: rgba(124, 58, 237, 0.2) !important;
  color: #7C3AED !important;
  font-weight: 600;
  transition: all 0.3s;
}

.action-read-btn.el-button--success.is-plain:hover {
  background-color: #7C3AED !important;
  border-color: #7C3AED !important;
  color: #ffffff !important;
  transform: translateY(-1px);
}

@media (max-width: 768px) {
  .header {
    left: 0;
    padding: 0 20px;
  }

  .page-title {
    font-size: 18px;
  }

  .username {
    display: none;
  }
}

/* 消息抽屉分页器定制 */
.drawer-pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: center;
  padding-bottom: 12px;
}

:deep(.drawer-pagination-wrapper .el-pagination.is-background .el-pager li:not(.is-active)) {
  background-color: rgba(255, 255, 255, 0.8) !important;
  border: 1px solid rgba(124, 58, 237, 0.08) !important;
  color: #6b7280 !important;
}

:deep(.drawer-pagination-wrapper .el-pagination.is-background .btn-prev),
:deep(.drawer-pagination-wrapper .el-pagination.is-background .btn-next) {
  background-color: rgba(255, 255, 255, 0.8) !important;
  border: 1px solid rgba(124, 58, 237, 0.08) !important;
  color: #6b7280 !important;
}

:deep(.drawer-pagination-wrapper .el-pagination.is-background .el-pager li.is-active) {
  background: linear-gradient(135deg, #7C3AED 0%, #5B21B6 100%) !important;
  color: #ffffff !important;
  box-shadow: 0 4px 10px rgba(124, 58, 237, 0.2) !important;
  border: none !important;
}
</style>

<style>
/* 覆盖 Element Plus 抽屉背景实现毛玻璃磨砂 */
.premium-drawer.el-drawer {
  background: rgba(255, 255, 255, 0.8) !important;
  backdrop-filter: blur(25px);
  border-left: 1px solid rgba(255, 255, 255, 0.7);
  box-shadow: -15px 0 50px rgba(124, 58, 237, 0.06);
}

.premium-drawer .el-drawer__body {
  background: linear-gradient(135deg, rgba(250, 245, 255, 0.6) 0%, rgba(243, 232, 255, 0.6) 100%) !important;
  padding: 24px 20px !important;
}

.premium-drawer .el-drawer__header {
  margin-bottom: 0 !important;
  padding: 20px 20px 16px !important;
  border-bottom: 1px solid rgba(124, 58, 237, 0.06);
  background: rgba(255, 255, 255, 0.5);
}
</style>
