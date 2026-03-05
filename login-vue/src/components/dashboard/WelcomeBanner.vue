<template>
  <div class="welcome-banner">
    <div class="banner-content">
      <div class="greeting">
        <h1 class="greeting-text">{{ greetingMessage }}</h1>
        <p class="user-name">{{ userName }}</p>
      </div>
      <div class="banner-info">
        <div class="info-item">
          <el-icon class="info-icon"><Clock /></el-icon>
          <span>{{ currentTime }}</span>
        </div>
        <div class="info-item">
          <el-icon class="info-icon"><Calendar /></el-icon>
          <span>{{ currentDate }}</span>
        </div>
      </div>
    </div>
    <div class="banner-decoration">
      <div class="decoration-circle circle-1"></div>
      <div class="decoration-circle circle-2"></div>
      <div class="decoration-circle circle-3"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { Clock, Calendar } from '@element-plus/icons-vue'

const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
const userName = ref(userInfo.username || '用户')
const currentTime = ref('')
const currentDate = ref('')

let timer = null

// 问候语
const greetingMessage = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '夜深了'
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  if (hour < 22) return '晚上好'
  return '夜深了'
})

// 更新时间
const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
  currentDate.value = now.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
}

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.welcome-banner {
  position: relative;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(255, 255, 255, 0.85));
  backdrop-filter: blur(20px);
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 20px 60px rgba(220, 38, 38, 0.15);
  padding: 40px;
  margin-bottom: 30px;
  overflow: hidden;
  animation: slideIn 0.6s ease;
}

.banner-content {
  position: relative;
  z-index: 2;
}

.greeting {
  margin-bottom: 20px;
}

.greeting-text {
  font-size: 36px;
  font-weight: 700;
  background: linear-gradient(135deg, var(--color-primary), var(--color-accent));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 8px 0;
}

.user-name {
  font-size: 20px;
  color: #666;
  margin: 0;
}

.banner-info {
  display: flex;
  gap: 30px;
  flex-wrap: wrap;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  color: #666;
}

.info-icon {
  color: var(--color-primary);
  font-size: 18px;
}

.banner-decoration {
  position: absolute;
  top: 0;
  right: 0;
  width: 300px;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.6;
  animation: float 25s infinite ease-in-out;
}

.circle-1 {
  width: 150px;
  height: 150px;
  background: radial-gradient(circle, rgba(220, 38, 38, 0.3), transparent);
  top: -50px;
  right: -30px;
  animation-delay: 0s;
}

.circle-2 {
  width: 100px;
  height: 100px;
  background: radial-gradient(circle, rgba(245, 158, 11, 0.3), transparent);
  top: 50%;
  right: 50px;
  animation-delay: 5s;
}

.circle-3 {
  width: 80px;
  height: 80px;
  background: radial-gradient(circle, rgba(220, 38, 38, 0.2), transparent);
  bottom: -20px;
  right: 100px;
  animation-delay: 10s;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) scale(1);
  }
  50% {
    transform: translateY(-20px) scale(1.1);
  }
}

@media (max-width: 768px) {
  .welcome-banner {
    padding: 30px 20px;
  }

  .greeting-text {
    font-size: 28px;
  }

  .user-name {
    font-size: 16px;
  }

  .banner-decoration {
    width: 200px;
  }

  .circle-1 {
    width: 100px;
    height: 100px;
  }

  .circle-2 {
    width: 70px;
    height: 70px;
  }

  .circle-3 {
    width: 50px;
    height: 50px;
  }
}
</style>
