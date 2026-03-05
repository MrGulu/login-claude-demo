<template>
  <div class="quick-actions">
    <div class="actions-header">
      <h3 class="actions-title">快捷操作</h3>
    </div>
    <div class="actions-grid">
      <div
        v-for="(action, index) in actions"
        :key="index"
        class="action-card"
        :style="{ animationDelay: `${index * 100}ms` }"
        @click="handleAction(action)"
      >
        <div class="action-icon" :style="{ background: action.iconBg }">
          <el-icon :size="32">
            <component :is="action.icon" />
          </el-icon>
        </div>
        <h4 class="action-title">{{ action.title }}</h4>
        <p class="action-desc">{{ action.description }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  UserFilled,
  Document,
  Setting,
  DataAnalysis
} from '@element-plus/icons-vue'

const actions = ref([
  {
    title: '添加用户',
    description: '创建新的用户账户',
    icon: UserFilled,
    iconBg: 'linear-gradient(135deg, #dc2626, #b91c1c)',
    action: 'add-user'
  },
  {
    title: '生成报告',
    description: '导出数据分析报告',
    icon: Document,
    iconBg: 'linear-gradient(135deg, #3b82f6, #2563eb)',
    action: 'generate-report'
  },
  {
    title: '系统设置',
    description: '配置系统参数',
    icon: Setting,
    iconBg: 'linear-gradient(135deg, #8b5cf6, #7c3aed)',
    action: 'settings'
  },
  {
    title: '查看分析',
    description: '查看详细数据分析',
    icon: DataAnalysis,
    iconBg: 'linear-gradient(135deg, #f59e0b, #d97706)',
    action: 'analytics'
  }
])

const handleAction = (action) => {
  ElMessage.info(`${action.title}功能开发中`)
}
</script>

<style scoped>
.quick-actions {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(255, 255, 255, 0.85));
  backdrop-filter: blur(20px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 10px 40px rgba(220, 38, 38, 0.1);
  padding: 30px;
  animation: fadeInUp 0.6s ease 0.4s both;
}

.actions-header {
  margin-bottom: 24px;
}

.actions-title {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  margin: 0;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.action-card {
  background: rgba(255, 255, 255, 0.6);
  border-radius: 16px;
  border: 1px solid rgba(220, 38, 38, 0.05);
  padding: 24px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  animation: scaleIn 0.5s ease both;
}

.action-card:hover {
  background: rgba(255, 255, 255, 0.9);
  transform: translateY(-4px) scale(1.02);
  box-shadow: 0 12px 30px rgba(220, 38, 38, 0.15);
}

.action-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin: 0 auto 16px;
  transition: transform 0.3s ease;
}

.action-card:hover .action-icon {
  transform: scale(1.1) rotate(5deg);
}

.action-title {
  font-size: 16px;
  font-weight: 700;
  color: #333;
  margin: 0 0 8px 0;
}

.action-desc {
  font-size: 13px;
  color: #666;
  margin: 0;
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

@keyframes scaleIn {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

@media (max-width: 768px) {
  .quick-actions {
    padding: 20px;
  }

  .actions-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .action-card {
    padding: 20px;
  }

  .action-icon {
    width: 56px;
    height: 56px;
  }
}
</style>
