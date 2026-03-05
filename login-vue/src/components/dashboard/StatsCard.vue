<template>
  <div class="stats-card" :style="{ animationDelay: `${delay}ms` }">
    <div class="card-icon" :style="{ background: iconBg }">
      <el-icon :size="28">
        <component :is="icon" />
      </el-icon>
    </div>
    <div class="card-content">
      <p class="card-label">{{ label }}</p>
      <h3 class="card-value">{{ displayValue }}</h3>
      <div class="card-trend" :class="trendClass">
        <el-icon><component :is="trendIcon" /></el-icon>
        <span>{{ trend }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ArrowUp, ArrowDown, Minus } from '@element-plus/icons-vue'

const props = defineProps({
  label: {
    type: String,
    required: true
  },
  value: {
    type: [Number, String],
    required: true
  },
  icon: {
    type: Object,
    required: true
  },
  iconBg: {
    type: String,
    default: 'linear-gradient(135deg, #dc2626, #f59e0b)'
  },
  trend: {
    type: String,
    default: ''
  },
  trendType: {
    type: String,
    default: 'up', // up, down, neutral
    validator: (value) => ['up', 'down', 'neutral'].includes(value)
  },
  delay: {
    type: Number,
    default: 0
  },
  prefix: {
    type: String,
    default: ''
  },
  suffix: {
    type: String,
    default: ''
  }
})

const displayValue = ref('0')
const animatedValue = ref(0)

// 趋势图标
const trendIcon = computed(() => {
  switch (props.trendType) {
    case 'up':
      return ArrowUp
    case 'down':
      return ArrowDown
    default:
      return Minus
  }
})

// 趋势样式类
const trendClass = computed(() => {
  return `trend-${props.trendType}`
})

// 数字动画
const animateValue = () => {
  const targetValue = typeof props.value === 'string'
    ? parseFloat(props.value.replace(/[^0-9.]/g, ''))
    : props.value

  if (isNaN(targetValue)) {
    displayValue.value = props.value
    return
  }

  const duration = 2000
  const steps = 60
  const increment = targetValue / steps
  let current = 0

  const timer = setInterval(() => {
    current += increment
    if (current >= targetValue) {
      current = targetValue
      clearInterval(timer)
    }

    // 格式化显示值
    let formatted = Math.floor(current).toLocaleString('zh-CN')

    // 处理小数
    if (props.value.toString().includes('.')) {
      const decimals = props.value.toString().split('.')[1]?.length || 1
      formatted = current.toFixed(decimals)
    }

    displayValue.value = props.prefix + formatted + props.suffix
  }, duration / steps)
}

onMounted(() => {
  setTimeout(() => {
    animateValue()
  }, props.delay)
})
</script>

<style scoped>
.stats-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(255, 255, 255, 0.85));
  backdrop-filter: blur(20px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 10px 40px rgba(220, 38, 38, 0.1);
  padding: 30px;
  display: flex;
  gap: 20px;
  transition: all 0.3s ease;
  animation: fadeInUp 0.6s ease;
  cursor: pointer;
}

.stats-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 60px rgba(220, 38, 38, 0.2);
}

.card-icon {
  width: 60px;
  height: 60px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.card-content {
  flex: 1;
}

.card-label {
  font-size: 14px;
  color: #666;
  margin: 0 0 8px 0;
  font-weight: 500;
}

.card-value {
  font-size: 32px;
  font-weight: 700;
  color: #333;
  margin: 0 0 12px 0;
  line-height: 1;
}

.card-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  font-weight: 600;
}

.trend-up {
  color: #10b981;
}

.trend-down {
  color: #ef4444;
}

.trend-neutral {
  color: #6b7280;
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

@media (max-width: 768px) {
  .stats-card {
    padding: 20px;
  }

  .card-icon {
    width: 50px;
    height: 50px;
  }

  .card-value {
    font-size: 24px;
  }
}
</style>
