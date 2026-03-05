<template>
  <aside class="sidebar" :class="{ collapsed }">
    <div class="sidebar-header">
      <div class="logo">
        <div class="logo-icon">
          <el-icon><TrendCharts /></el-icon>
        </div>
        <transition name="fade">
          <span v-if="!collapsed" class="logo-text">管理系统</span>
        </transition>
      </div>
    </div>

    <nav class="sidebar-nav">
      <el-menu
        :default-active="activeMenu"
        :collapse="collapsed"
        :collapse-transition="false"
        background-color="transparent"
        text-color="#333"
        active-text-color="#dc2626"
      >
        <el-menu-item index="/home" @click="navigate('/home')">
          <el-icon><HomeFilled /></el-icon>
          <template #title>首页</template>
        </el-menu-item>

        <el-menu-item index="/profile" @click="navigate('/profile')">
          <el-icon><User /></el-icon>
          <template #title>个人资料</template>
        </el-menu-item>

        <el-sub-menu index="system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/system/users" @click="navigate('/system/users')">
            <el-icon><User /></el-icon>
            <template #title>用户管理</template>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </nav>

    <div class="sidebar-footer">
      <el-button
        class="logout-btn"
        :icon="SwitchButton"
        @click="handleLogout"
        :circle="collapsed"
      >
        <span v-if="!collapsed">退出登录</span>
      </el-button>
    </div>
  </aside>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  TrendCharts,
  HomeFilled,
  User,
  DataAnalysis,
  Setting,
  SwitchButton
} from '@element-plus/icons-vue'

const props = defineProps({
  collapsed: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['toggle'])

const router = useRouter()
const route = useRoute()

const activeMenu = computed(() => route.path)

const navigate = (path) => {
  if (path === route.path) return

  router.push(path)
}

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.sidebar {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  width: 280px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.95), rgba(255, 255, 255, 0.9));
  backdrop-filter: blur(20px);
  border-right: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 4px 0 20px rgba(220, 38, 38, 0.08);
  display: flex;
  flex-direction: column;
  z-index: 200;
  transition: width 0.3s ease;
}

.sidebar.collapsed {
  width: 80px;
}

.sidebar-header {
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid rgba(220, 38, 38, 0.1);
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, var(--color-primary), var(--color-accent));
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  flex-shrink: 0;
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
  background: linear-gradient(135deg, var(--color-primary), var(--color-accent));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  white-space: nowrap;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.sidebar-nav {
  flex: 1;
  padding: 20px 0;
  overflow-y: auto;
}

.sidebar-nav::-webkit-scrollbar {
  width: 4px;
}

.sidebar-nav::-webkit-scrollbar-thumb {
  background: rgba(220, 38, 38, 0.3);
  border-radius: 2px;
}

:deep(.el-menu) {
  border: none;
}

:deep(.el-menu-item) {
  margin: 4px 12px;
  border-radius: 12px;
  transition: all 0.3s ease;
}

:deep(.el-menu-item:hover) {
  background: rgba(220, 38, 38, 0.08) !important;
}

:deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, rgba(220, 38, 38, 0.15), rgba(245, 158, 11, 0.15)) !important;
  color: var(--color-primary) !important;
}

/* 子菜单样式统一 */
:deep(.el-sub-menu) {
  margin: 4px 12px;
}

:deep(.el-sub-menu__title) {
  border-radius: 12px;
  transition: all 0.3s ease;
  margin: 0;
}

:deep(.el-sub-menu__title:hover) {
  background: rgba(220, 38, 38, 0.08) !important;
}

:deep(.el-sub-menu.is-active .el-sub-menu__title) {
  background: linear-gradient(135deg, rgba(220, 38, 38, 0.15), rgba(245, 158, 11, 0.15)) !important;
  color: var(--color-primary) !important;
}

/* 二级菜单项样式 */
:deep(.el-sub-menu .el-menu-item) {
  margin: 4px 12px 4px 24px;
  min-width: auto;
}

.sidebar-footer {
  padding: 20px;
  border-top: 1px solid rgba(220, 38, 38, 0.1);
}

.logout-btn {
  width: 100%;
  background: rgba(220, 38, 38, 0.1);
  border: none;
  color: var(--color-primary);
  font-weight: 500;
}

.logout-btn:hover {
  background: rgba(220, 38, 38, 0.2);
}

.sidebar.collapsed .logout-btn {
  width: 40px;
  height: 40px;
}

@media (max-width: 768px) {
  .sidebar {
    transform: translateX(-100%);
  }

  .sidebar.collapsed {
    transform: translateX(0);
    width: 80px;
  }
}
</style>
