<template>
  <header class="header">
    <div class="header-left">
      <el-button
        class="menu-toggle"
        :icon="Expand"
        circle
        @click="$emit('toggle-sidebar')"
      />
      <h2 class="page-title">仪表盘</h2>
    </div>

    <div class="header-right">
      <!-- 通知图标 -->
      <el-badge :value="3" class="notification-badge">
        <el-button :icon="Bell" circle />
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
  </header>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Expand, Bell, User, Setting, SwitchButton } from '@element-plus/icons-vue'
import { useUserInfo } from '../composables/useUserInfo'

const router = useRouter()
// 使用全局用户信息
const { userName, userAvatar, userInitial, clearUserInfo } = useUserInfo()

// 处理下拉菜单命令
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
  box-shadow: 0 4px 20px rgba(220, 38, 38, 0.08);
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
  background: rgba(220, 38, 38, 0.1);
  border: none;
  color: var(--color-primary);
}

.menu-toggle:hover {
  background: rgba(220, 38, 38, 0.2);
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
  background: rgba(220, 38, 38, 0.05);
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
</style>
