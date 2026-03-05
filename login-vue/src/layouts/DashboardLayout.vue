<template>
  <div class="dashboard-layout">
    <Background />

    <!-- 侧边栏 -->
    <Sidebar :collapsed="sidebarCollapsed" @toggle="toggleSidebar" />

    <!-- 主内容区 -->
    <div class="main-wrapper" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
      <!-- 顶部导航栏 -->
      <Header @toggle-sidebar="toggleSidebar" />

      <!-- 内容区域 -->
      <main class="content-area">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import Background from '../components/Background.vue'
import Header from '../components/Header.vue'
import Sidebar from '../components/Sidebar.vue'

const sidebarCollapsed = ref(false)

const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value
}
</script>

<style scoped>
.dashboard-layout {
  min-height: 100vh;
  position: relative;
  overflow-x: hidden;
}

.main-wrapper {
  margin-left: 280px;
  min-height: 100vh;
  transition: margin-left 0.3s ease;
}

.main-wrapper.sidebar-collapsed {
  margin-left: 80px;
}

.content-area {
  padding: 100px 40px 40px;
  position: relative;
  z-index: 1;
}

@media (max-width: 768px) {
  .main-wrapper {
    margin-left: 0;
  }

  .main-wrapper.sidebar-collapsed {
    margin-left: 0;
  }

  .content-area {
    padding: 80px 20px 20px;
  }
}
</style>
