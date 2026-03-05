<template>
  <div class="login-section">
    <div class="form-header">
      <h2 class="form-title">欢迎回来</h2>
      <p class="form-subtitle">登录您的管理账户</p>
    </div>

    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      class="login-form"
      @submit.prevent="handleSubmit"
    >
      <el-form-item label="用户名" prop="username">
        <el-input
          v-model="formData.username"
          placeholder="请输入用户名"
          size="large"
        />
      </el-form-item>

      <el-form-item label="密码" prop="password">
        <el-input
          v-model="formData.password"
          type="password"
          placeholder="请输入密码"
          size="large"
          show-password
        />
      </el-form-item>

      <div class="form-options">
        <el-checkbox v-model="formData.remember" label="记住我" />
        <a href="#" class="forgot-link" @click.prevent="showForgotPassword">忘记密码？</a>
      </div>

      <el-button
        type="primary"
        size="large"
        class="submit-btn"
        :loading="loading"
        @click="handleSubmit"
      >
        {{ loading ? '登录中...' : '登录' }}
      </el-button>

      <div class="divider">或使用以下方式登录</div>

      <SocialLogin />
    </el-form>

    <!-- 忘记密码弹窗 -->
    <ForgotPasswordDialog v-model="dialogVisible" />
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import SocialLogin from './SocialLogin.vue'
import ForgotPasswordDialog from './ForgotPasswordDialog.vue'
import { login } from '../api/auth'
import { showSuccess } from '../utils/toast'
import { useUserInfo } from '../composables/useUserInfo'

const router = useRouter()
const { refreshUserInfo } = useUserInfo()
const formRef = ref()
const loading = ref(false)
const dialogVisible = ref(false)

const formData = reactive({
  username: '',
  password: '',
  remember: false
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 个字符', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true

      try {
        const response = await login({
          username: formData.username,
          password: formData.password,
          remember: formData.remember
        })

        // 保存token和用户信息
        localStorage.setItem('token', response.data.token)
        localStorage.setItem('userInfo', JSON.stringify(response.data.userInfo))

        // 刷新全局用户信息状态
        refreshUserInfo()

        // 登录成功提示
        showSuccess('登录成功！正在跳转...')

        // 延迟跳转，让用户看到成功提示
        setTimeout(() => {
       router.push('/home')
        }, 1000)
      } catch (error) {
        console.error('登录失败:', error)
        // 错误信息已在axios拦截器中处理
      } finally {
        loading.value = false
      }
    }
  })
}

const showForgotPassword = () => {
  dialogVisible.value = true
}
</script>

<style scoped>
.login-section {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 480px;
  background: linear-gradient(135deg,
    rgba(255, 255, 255, 0.95) 0%,
    rgba(255, 255, 255, 0.85) 100%);
  backdrop-filter: blur(30px);
  border-radius: 32px;
  padding: 3rem 2.5rem;
  box-shadow:
    0 40px 100px rgba(220, 38, 38, 0.25),
    inset 0 0 0 1px rgba(255, 255, 255, 1),
    0 0 0 1px rgba(220, 38, 38, 0.1);
  animation: slideInRight 1s cubic-bezier(0.16, 1, 0.3, 1);
  z-index: 20;
}

.login-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--color-primary) 0%, var(--color-accent) 100%);
  border-radius: 32px 32px 0 0;
}

.form-header {
  margin-bottom: 2rem;
  animation: fadeIn 1s cubic-bezier(0.16, 1, 0.3, 1) 0.4s backwards;
}

.form-title {
  font-family: var(--font-display);
  font-size: 2rem;
  font-weight: 700;
  color: var(--color-text);
  margin-bottom: 0.5rem;
  letter-spacing: -0.02em;
}

.form-subtitle {
  font-size: 1rem;
  color: var(--color-text-muted);
  font-weight: 400;
}

.login-form {
  animation: fadeIn 1s cubic-bezier(0.16, 1, 0.3, 1) 0.6s backwards;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  font-size: 0.875rem;
}

.forgot-link {
  color: var(--color-primary);
  text-decoration: none;
  font-weight: 700;
  transition: all 0.2s;
}

.forgot-link:hover {
  color: var(--color-primary-dark);
  text-decoration: underline;
}

.submit-btn {
  width: 100%;
  padding: 1.125rem !important;
  font-size: 1rem !important;
  position: relative;
  overflow: hidden;
}

.submit-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.6s;
}

.submit-btn:hover::before {
  left: 100%;
}

.divider {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin: 2rem 0;
  color: var(--color-text-muted);
  font-size: 0.875rem;
  font-weight: 600;
  white-space: nowrap;
  flex-direction: row;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  height: 2px;
  background: linear-gradient(90deg, transparent, rgba(220, 38, 38, 0.2), transparent);
}

@media (max-width: 1200px) {
  .login-section {
    width: 420px;
  }
}

@media (max-width: 768px) {
  .login-section {
    position: relative;
    width: 100%;
    transform: none;
  }
}

/* Override Element Plus styles for this component */
:deep(.el-form-item) {
  display: flex !important;
  align-items: center !important;
}

:deep(.el-form-item__label) {
  font-size: 0.875rem !important;
  font-weight: 700 !important;
  color: var(--color-text) !important;
  text-transform: uppercase !important;
  letter-spacing: 0.05em !important;
  width: 80px !important;
  flex-shrink: 0 !important;
  display: flex !important;
  align-items: center !important;
  margin-bottom: 0 !important;
  line-height: 1 !important;
  justify-content: flex-start !important;
  text-align: left !important;
}

:deep(.el-form-item__content) {
  display: flex !important;
  align-items: center !important;
  flex: 1 !important;
}

/* 确保所有输入框宽度一致并对齐 */
:deep(.el-form-item__content) {
  width: 100%;
}

:deep(.el-input) {
  width: 100% !important;
}

:deep(.el-input__wrapper) {
  width: 100% !important;
  box-sizing: border-box !important;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1) !important;
}

:deep(.el-input__wrapper.is-focus) {
  transform: translateY(-2px);
}
</style>
