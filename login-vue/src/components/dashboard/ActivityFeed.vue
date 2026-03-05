<template>
  <div class="activity-feed">
    <div class="feed-header">
      <h3 class="feed-title">最近活动</h3>
      <el-button text type="primary">查看全部</el-button>
    </div>
    <div class="feed-list">
      <div
        v-for="(activity, index) in activities"
        :key="index"
        class="activity-item"
        :style="{ animationDelay: `${index * 100}ms` }"
      >
        <div class="activity-icon" :style="{ background: activity.iconBg }">
          <el-icon :size="20">
            <component :is="activity.icon" />
          </el-icon>
        </div>
        <div class="activity-content">
          <p class="activity-title">{{ activity.title }}</p>
          <p class="activity-time">{{ activity.time }}</p>
        </div>
        <div class="activity-status" :class="`status-${activity.status}`">
          {{ activity.statusText }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import {
  UserFilled,
  Refresh,
  DocumentChecked,
  Lock,
  Document,
  CircleCheck
} from '@element-plus/icons-vue'

const activities = ref([
  {
    title: '新用户注册',
    time: '2分钟前',
    icon: UserFilled,
    iconBg: 'linear-gradient(135deg, #10b981, #059669)',
    status: 'success',
    statusText: '成功'
  },
  {
    title: '系统更新',
    time: '15分钟前',
    icon: Refresh,
    iconBg: 'linear-gradient(135deg, #3b82f6, #2563eb)',
    status: 'info',
    statusText: '进行中'
  },
  {
    title: '数据备份完成',
    time: '1小时前',
    icon: DocumentChecked,
    iconBg: 'linear-gradient(135deg, #8b5cf6, #7c3aed)',
    status: 'success',
    statusText: '完成'
  },
  {
    title: '安全扫描',
    time: '2小时前',
    icon: Lock,
    iconBg: 'linear-gradient(135deg, #f59e0b, #d97706)',
    status: 'warning',
    statusText: '警告'
  },
  {
    title: '报告生成',
    time: '3小时前',
    icon: Document,
    iconBg: 'linear-gradient(135deg, #06b6d4, #0891b2)',
    status: 'success',
    statusText: '完成'
  },
  {
    title: '用户登录',
    time: '5小时前',
    icon: CircleCheck,
    iconBg: 'linear-gradient(135deg, #dc2626, #b91c1c)',
    status: 'success',
    statusText: '成功'
  }
])
</script>

<style scoped>
.activity-feed {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(255, 255, 255, 0.85));
  backdrop-filter: blur(20px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 10px 40px rgba(220, 38, 38, 0.1);
  padding: 30px;
  animation: fadeInUp 0.6s ease 0.3s both;
}

.feed-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.feed-title {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  margin: 0;
}

.feed-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 12px;
  border: 1px solid rgba(220, 38, 38, 0.05);
  transition: all 0.3s ease;
  animation: slideInLeft 0.5s ease both;
}

.activity-item:hover {
  background: rgba(255, 255, 255, 0.8);
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(220, 38, 38, 0.08);
}

.activity-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.activity-content {
  flex: 1;
}

.activity-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin: 0 0 4px 0;
}

.activity-time {
  font-size: 13px;
  color: #999;
  margin: 0;
}

.activity-status {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  flex-shrink: 0;
}

.status-success {
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
}

.status-info {
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
}

.status-warning {
  background: rgba(245, 158, 11, 0.1);
  color: #f59e0b;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@media (max-width: 768px) {
  .activity-feed {
    padding: 20px;
  }

  .activity-item {
    padding: 12px;
  }

  .activity-icon {
    width: 36px;
    height: 36px;
  }

  .activity-status {
    display: none;
  }
}
</style>
