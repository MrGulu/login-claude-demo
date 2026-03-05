<template>
  <Teleport to="body">
    <Transition name="modal-fade">
      <div v-if="visible" class="forgot-password-modal" @click.self="handleClose">
        <div class="modal-content" @click.stop>
          <!-- 关闭按钮 -->
          <button type="button" class="close-btn" @click="handleClose">
        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </button>

          <!-- 头部 -->
          <div class="modal-header">
            <div class="icon-wrapper">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <rect x="3" y="11" width="18" height="11" rx="2" stroke="currentColor" stroke-width="2"/>
       <path d="M7 11V7C7 4.79086 8.79086 3 11 3H13C15.2091 3 17 4.79086 17 7V11" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
           <circle cx="12" cy="16" r="1" fill="currentColor"/>
              </svg>
            </div>
       <h2 class="modal-title">重置密码</h2>
    <p class="modal-subtitle">请按照以下步骤完成密码重置</p>
          </div>

          <!-- 步骤指示器 -->
          <div class="steps-indicator">
            <div
              v-for="(step, index) in steps"
              :key="index"
        class="step-item"
              :class="{ active: currentStep === index, completed: currentStep > index }"
            >
          <div class="step-number">
          <span v-if="currentStep <= index">{{ index + 1 }}</span>
            <svg v-else viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M5 13L9 17L19 7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
       </svg>
              </div>
              <div class="step-info">
                <div class="step-title">{{ step.title }}</div>
           <div class="step-desc">{{ step.desc }}</div>
              </div>
            </div>
            <div class="step-connector" :style="{ width: connectorWidth }"></div>
          </div>

          <!-- 步骤内容 -->
          <div class="modal-body">
            <!-- 步骤1: 验证身份 -->
            <Transition name="slide-fade" mode="out-in">
         <div v-if="currentStep === 0" key="step1" class="step-content">
             <el-form ref="step1FormRef" :model="step1Form" :rules="step1Rules" label-position="top">
                  <el-form-item label="手机号 / 邮箱" prop="account">
                    <el-input
         v-model="step1Form.account"
                 placeholder="请输入注册时的手机号 / 邮箱"
        size="large"
                   prefix-icon="Message"
                   maxlength="50"
                 />
         <div class="hint-text">验证码将发送至你的手机号 / 邮箱，5 分钟内有效</div>
           </el-form-item>

                  <el-form-item label="验证码" prop="verificationCode">
                <div class="verification-wrapper">
                <el-input
                   v-model="step1Form.verificationCode"
                 placeholder="请输入6位验证码"
                    size="large"
                    maxlength="6"
                      prefix-icon="Key"
          />
           <button type="button"
                   class="send-code-btn"
             :disabled="countdown > 0 || sendingCode"
                   @click="handleSendCode"
              >
                  <span v-if="countdown > 0">{{ countdown }}秒后重试</span>
           <span v-else-if="sendingCode">发送中...</span>
             <span v-else>获取验证码</span>
                      </button>
                    </div>
                  </el-form-item>
                </el-form>

                <button type="button" class="primary-btn" :disabled="loading" @click="handleStep1">
                  <span v-if="!loading">下一步</span>
                  <span v-else class="loading-spinner"></span>
          </button>
              </div>

              <!-- 步骤2: 重置密码 -->
              <div v-else key="step2" class="step-content">
        <el-form ref="step2FormRef" :model="step2Form" :rules="step2Rules" label-position="top">
                  <el-form-item label="新密码" prop="newPassword">
           <el-input
                 v-model="step2Form.newPassword"
            type="password"
                     placeholder="6-20位字符"
            size="large"
              prefix-icon="Lock"
               show-password
                    />
            </el-form-item>

                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input
             v-model="step2Form.confirmPassword"
                  type="password"
         placeholder="请再次输入新密码"
                 size="large"
              prefix-icon="Lock"
              show-password
            />
         </el-form-item>
                </el-form>

                <div class="button-group">
                  <button type="button" class="secondary-btn" @click="currentStep = 0">
                    <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M19 12H5M5 12L12 19M5 12L12 5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
             上一步
                  </button>
         <button type="button" class="primary-btn" :disabled="loading" @click="handleStep2">
                 <span v-if="!loading">完成重置</span>
             <span v-else class="loading-spinner"></span>
                  </button>
                </div>
              </div>
         </Transition>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { ref, reactive, computed, watch, onUnmounted } from 'vue'
import { sendVerificationCode, verifyCode, resetPassword } from '../api/password'
import { showSuccess } from '../utils/toast'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue'])

const visible = ref(false)
const currentStep = ref(0)
const loading = ref(false)

const steps = [
  { title: '验证身份', desc: '手机号/邮箱验证' },
  { title: '重置密码', desc: '设置新密码' }
]

const connectorWidth = computed(() => {
  return currentStep.value > 0 ? '100%' : '0%'
})

// 倒计时相关
const countdown = ref(0)
const sendingCode = ref(false)
const codeSent = ref(false) // 标记是否已发送验证码
let countdownTimer = null

// 重置令牌
const resetToken = ref('')

// 步骤1表单
const step1FormRef = ref()
const step1Form = reactive({
  account: '',
  verificationCode: ''
})

const step1Rules = {
  account: [
    { required: true, message: '请输入手机号或邮箱', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        const phoneRegex = /^1[3-9]\d{9}$/
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
        if (!phoneRegex.test(value) && !emailRegex.test(value)) {
          callback(new Error('请输入正确的手机号或邮箱格式'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  verificationCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { pattern: /^\d{6}$/, message: '验证码为6位数字', trigger: 'blur' }
  ]
}

// 步骤2表单
const step2FormRef = ref()
const step2Form = reactive({
  newPassword: '',
  confirmPassword: ''
})

const step2Rules = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度必须在6-20个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== step2Form.newPassword) {
          callback(new Error('两次密码输入不一致'))
     } else {
        callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 监听modelValue变化
watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val) {
    currentStep.value = 0
    resetForms()
  }
})

// 监听visible变化
watch(visible, (val) => {
  emit('update:modelValue', val)
})

// 发送验证码
const handleSendCode = async () => {
  // 验证账号格式
  if (!step1Form.account) {
    ElMessage.error('请先输入手机号或邮箱')
    return
  }

  const phoneRegex = /^1[3-9]\d{9}$/
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!phoneRegex.test(step1Form.account) && !emailRegex.test(step1Form.account)) {
    ElMessage.error('请输入正确的手机号或邮箱格式')
    return
  }

  sendingCode.value = true
  try {
    await sendVerificationCode({ account: step1Form.account })
    ElMessage.success('验证码已发送，请查收')
    codeSent.value = true // 标记已发送验证码

    // 启动60秒倒计时
    countdown.value = 60
    countdownTimer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(countdownTimer)
      }
    }, 1000)
  } catch (error) {
    // 错误已由axios拦截器处理
  } finally {
    sendingCode.value = false
  }
}

// 处理步骤1
const handleStep1 = async () => {
  if (!step1FormRef.value) return
  // 检查是否已发送验证码
  if (!codeSent.value) {
    ElMessage.warning('请先点击"获取验证码"按钮')
    return
  }

  step1FormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const response = await verifyCode({
          account: step1Form.account,
          verificationCode: step1Form.verificationCode
        })
        resetToken.value = response.data.resetToken
        currentStep.value = 1
      } catch (error) {
        // 错误已由axios拦截器处理
      } finally {
        loading.value = false
      }
    }
  })
}

// 处理步骤2
const handleStep2 = () => {
  if (!step2FormRef.value) return

  step2FormRef.value.validate((valid) => {
    console.log('表单验证结果:', valid)

    if (valid) {
      loading.value = true

      resetPassword({
        resetToken: resetToken.value,
        newPassword: step2Form.newPassword,
        confirmPassword: step2Form.confirmPassword
      }).then(result => {
        console.log('resetPassword 响应:', result)
        showSuccess('密码重置成功！请使用新密码登录')
        setTimeout(() => {
          visible.value = false
        }, 1500)
      }).catch(error => {
        console.error('重置失败:', error)
        console.error('错误详情:', error.response?.data || error.message)
      }).finally(() => {
        loading.value = false
      })
    } else {
      console.error('表单验证失败')
    }
  })
}

// 重置表单
const resetForms = () => {
  step1Form.account = ''
  step1Form.verificationCode = ''
  step2Form.newPassword = ''
  step2Form.confirmPassword = ''
  resetToken.value = ''
  countdown.value = 0
  codeSent.value = false // 重置验证码发送标记

  if (countdownTimer) {
    clearInterval(countdownTimer)
  }

  if (step1FormRef.value) {
    step1FormRef.value.clearValidate()
  }
  if (step2FormRef.value) {
    step2FormRef.value.clearValidate()
  }
}

// 关闭弹窗
const handleClose = () => {
  visible.value = false
  setTimeout(() => {
    resetForms()
    currentStep.value = 0
  }, 300)
}

// 组件卸载时清理定时器
onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
})
</script>

<style scoped>
.forgot-password-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: transparent;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
  padding: 2rem;
  overflow-y: auto;
}

.modal-content {
  position: relative;
  width: 100%;
  max-width: 560px;
  max-height: 90vh;
  overflow-y: auto;
  background: linear-gradient(135deg,
    rgba(255, 255, 255, 0.98) 0%,
    rgba(255, 255, 255, 0.95) 100%);
  backdrop-filter: blur(40px) saturate(180%);
  border-radius: 32px;
  padding: 3rem;
  box-shadow:
    0 60px 140px rgba(220, 38, 38, 0.4),
    0 0 0 1px rgba(255, 255, 255, 1),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
  animation: modalSlideUp 0.5s cubic-bezier(0.16, 1, 0.3, 1);
  transform: translateZ(0);
}

.modal-content::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--color-primary) 0%, var(--color-accent) 100%);
  border-radius: 32px 32px 0 0;
}

@keyframes modalSlideUp {
  from {
    opacity: 0;
    transform: translateY(40px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.close-btn {
  position: absolute;
  top: 1.5rem;
  right: 1.5rem;
  width: 40px;
  height: 40px;
  border: none;
  background: rgba(220, 38, 38, 0.1);
  border-radius: 12px;
  color: var(--color-primary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  z-index: 10;
}

.close-btn svg {
  width: 20px;
  height: 20px;
}

.close-btn:hover {
  background: var(--color-primary);
  color: white;
  transform: rotate(90deg);
}

.modal-header {
  text-align: center;
  margin-bottom: 2.5rem;
}

.icon-wrapper {
  width: 80px;
  height: 80px;
  margin: 0 auto 1.5rem;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-accent) 100%);
  border-radius: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 20px 40px rgba(220, 38, 38, 0.3);
  animation: iconPulse 2s ease-in-out infinite;
}

.icon-wrapper svg {
  width: 40px;
  height: 40px;
}

@keyframes iconPulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.modal-title {
  font-family: var(--font-display);
  font-size: 2rem;
  font-weight: 700;
  color: var(--color-text);
  margin-bottom: 0.5rem;
  letter-spacing: -0.02em;
}

.modal-subtitle {
  font-size: 1rem;
  color: var(--color-text-muted);
  font-weight: 500;
}

.steps-indicator {
  position: relative;
  display: flex;
  gap: 2rem;
  margin-bottom: 3rem;
  padding: 0 1rem;
}

.step-connector {
  position: absolute;
  top: 20px;
  left: 1rem;
  right: 1rem;
  height: 3px;
  background: linear-gradient(90deg, var(--color-primary) 0%, var(--color-accent) 100%);
  transition: width 0.6s cubic-bezier(0.16, 1, 0.3, 1);
  transform-origin: left;
  z-index: 0;
}

.step-item {
  position: relative;
  flex: 1;
  display: flex;
  gap: 1rem;
  z-index: 1;
}

.step-number {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: rgba(220, 38, 38, 0.1);
  color: var(--color-text-muted);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 1.125rem;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

.step-item.active .step-number {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-accent) 100%);
  color: white;
  box-shadow: 0 8px 24px rgba(220, 38, 38, 0.3);
  transform: scale(1.1);
}

.step-item.completed .step-number {
  background: var(--color-primary);
  color: white;
}

.step-number svg {
  width: 20px;
  height: 20px;
}

.step-info {
  flex: 1;
}

.step-title {
  font-weight: 700;
  font-size: 0.875rem;
  color: var(--color-text);
  margin-bottom: 0.25rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.step-desc {
  font-size: 0.75rem;
  color: var(--color-text-muted);
}

.step-item.active .step-title {
  color: var(--color-primary);
}

.modal-body {
  min-height: 400px;
}

.step-content {
  animation: fadeIn 0.4s ease-in-out;
}

.hint-text {
  font-size: 0.75rem;
  color: #999;
  margin-top: 0.5rem;
  line-height: 1.5;
}

.verification-wrapper {
  display: flex;
  gap: 1rem;
  align-items: flex-start;
}

.verification-wrapper .el-input {
  flex: 1;
}

.send-code-btn {
  flex-shrink: 0;
  width: 140px;
  height: 48px;
  border: 2px solid var(--color-primary);
  border-radius: 12px;
  background: transparent;
  color: var(--color-primary);
  font-size: 0.875rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s;
}

.send-code-btn:hover:not(:disabled) {
  background: var(--color-primary);
  color: white;
  transform: translateY(-2px);
}

.send-code-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

.primary-btn {
  width: 100%;
  padding: 1.125rem 2rem;
  border: none;
  border-radius: 16px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-accent) 100%);
  color: white;
  font-size: 1rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  box-shadow: 0 8px 24px rgba(220, 38, 38, 0.3);
  margin-top: 1.5rem;
  position: relative;
  overflow: hidden;
}

.primary-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.6s;
}

.primary-btn:hover::before {
  left: 100%;
}

.primary-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(220, 38, 38, 0.4);
}

.primary-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.loading-spinner {
  display: inline-block;
  width: 20px;
  height: 20px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.button-group {
  display: flex;
  gap: 1rem;
  margin-top: 1.5rem;
}

.secondary-btn {
  flex: 1;
  padding: 1.125rem 2rem;
  border: 2px solid var(--color-primary);
  border-radius: 16px;
  background: transparent;
  color: var(--color-primary);
  font-size: 1rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.secondary-btn svg {
  width: 20px;
  height: 20px;
}

.secondary-btn:hover {
  background: var(--color-primary);
  color: white;
  transform: translateY(-2px);
}

.button-group .primary-btn {
  flex: 2;
  margin-top: 0;
}

/* Element Plus 样式覆盖 */
:deep(.el-form-item__label) {
  font-size: 0.875rem !important;
  font-weight: 700 !important;
  color: var(--color-text) !important;
  text-transform: uppercase !important;
  letter-spacing: 0.05em !important;
  margin-bottom: 0.5rem !important;
}

:deep(.el-input__wrapper) {
  border-radius: 12px !important;
  padding: 0.75rem 1rem !important;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1) !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05) !important;
}

:deep(.el-input__wrapper.is-focus) {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(220, 38, 38, 0.15) !important;
}

/* 过渡动画 */
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.3s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

.modal-fade-enter-active .modal-content {
  animation: modalSlideUp 0.5s cubic-bezier(0.16, 1, 0.3, 1);
}

.modal-fade-leave-active .modal-content {
  animation: modalSlideDown 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes modalSlideDown {
  from {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
  to {
    opacity: 0;
    transform: translateY(20px) scale(0.98);
  }
}

.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all 0.3s ease;
}

.slide-fade-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.slide-fade-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

@media (max-width: 768px) {
  .modal-content {
    padding: 2rem 1.5rem;
    max-width: 100%;
  }

  .steps-indicator {
    flex-direction: column;
    gap: 1rem;
  }

  .step-connector {
    display: none;
  }

  .verification-wrapper {
    flex-direction: column;
  }

  .send-code-btn {
    width: 100%;
  }

  .button-group {
    flex-direction: column;
  }

  .button-group .primary-btn {
    flex: 1;
  }
}
</style>
