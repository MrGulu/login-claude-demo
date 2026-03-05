<template>
  <Transition name="error-slide">
    <div v-if="visible" class="error-toast">
      <div class="error-content">
        <div class="error-icon">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
            <path d="M12 8V12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            <circle cx="12" cy="16" r="1" fill="currentColor"/>
          </svg>
        </div>
        <div class="error-text">
          <div class="error-title">{{ title }}</div>
          <div class="error-message">{{ message }}</div>
        </div>
        <button class="error-close" @click.stop="close">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </button>
      </div>
      <div class="error-progress" :style="{ animationDuration: duration + 'ms' }"></div>
    </div>
  </Transition>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const props = defineProps({
  title: {
    type: String,
    default: '操作失败'
  },
  message: {
    type: String,
    required: true
  },
  duration: {
    type: Number,
    default: 4000
  }
})

const emit = defineEmits(['close'])

const visible = ref(false)
let timer = null

onMounted(() => {
  visible.value = true

  if (props.duration > 0) {
    timer = setTimeout(() => {
      close()
    }, props.duration)
  }
})

const close = () => {
  visible.value = false
  setTimeout(() => {
    emit('close')
  }, 300)
}
</script>

<style scoped>
.error-toast {
  position: fixed;
  top: 2rem;
  right: 2rem;
  width: 420px;
  background: linear-gradient(135deg,
    rgba(220, 38, 38, 0.95) 0%,
    rgba(185, 28, 28, 0.95) 100%);
  backdrop-filter: blur(40px) saturate(180%);
  border-radius: 20px;
  padding: 1.5rem;
  box-shadow:
    0 20px 60px rgba(220, 38, 38, 0.4),
    0 0 0 1px rgba(255, 255, 255, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.3);
  z-index: 9999;
  cursor: pointer;
  overflow: hidden;
  transition: transform 0.2s cubic-bezier(0.16, 1, 0.3, 1);
}

.error-toast:hover {
  transform: translateY(-2px) scale(1.01);
}

.error-toast::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg,
    rgba(255, 255, 255, 0.8) 0%,
    rgba(245, 158, 11, 0.8) 100%);
}

.error-content {
  display: flex;
  align-items: flex-start;
  gap: 1rem;
  position: relative;
  z-index: 1;
}

.error-icon {
  flex-shrink: 0;
  width: 32px;
  height: 32px;
  color: white;
  animation: errorPulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

.error-icon svg {
  width: 100%;
  height: 100%;
  filter: drop-shadow(0 2px 8px rgba(0, 0, 0, 0.3));
}

.error-text {
  flex: 1;
  color: white;
}

.error-title {
  font-family: var(--font-display);
  font-size: 1rem;
  font-weight: 700;
  margin-bottom: 0.25rem;
  letter-spacing: -0.01em;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.error-message {
  font-size: 0.875rem;
  opacity: 0.95;
  line-height: 1.5;
  font-weight: 500;
}

.error-close {
  flex-shrink: 0;
  width: 24px;
  height: 24px;
  border: none;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 8px;
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  padding: 0;
}

.error-close:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: rotate(90deg);
}

.error-close svg {
  width: 14px;
  height: 14px;
}

.error-progress {
  position: absolute;
  bottom: 0;
  left: 0;
  height: 3px;
  background: linear-gradient(90deg,
    rgba(255, 255, 255, 0.6) 0%,
    rgba(245, 158, 11, 0.8) 100%);
  animation: progressShrink linear forwards;
  transform-origin: left;
}

@keyframes progressShrink {
  from {
    transform: scaleX(1);
  }
  to {
    transform: scaleX(0);
  }
}

@keyframes errorPulse {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.8;
    transform: scale(1.05);
  }
}

.error-slide-enter-active {
  animation: errorSlideIn 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

.error-slide-leave-active {
  animation: errorSlideOut 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes errorSlideIn {
  from {
    opacity: 0;
    transform: translateX(100%) scale(0.9);
  }
  to {
    opacity: 1;
    transform: translateX(0) scale(1);
  }
}

@keyframes errorSlideOut {
  from {
    opacity: 1;
    transform: translateX(0) scale(1);
  }
  to {
    opacity: 0;
    transform: translateX(100%) scale(0.9);
  }
}

@media (max-width: 768px) {
  .error-toast {
    width: calc(100% - 2rem);
    right: 1rem;
    top: 1rem;
  }
}
</style>
