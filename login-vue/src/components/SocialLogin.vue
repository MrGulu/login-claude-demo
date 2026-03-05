<template>
  <div class="social-login">
    <el-button class="social-btn" @click="handleSocialLogin('facebook')">
      <svg viewBox="0 0 24 24" fill="currentColor">
        <path d="M12 2C6.477 2 2 6.477 2 12c0 4.991 3.657 9.128 8.438 9.879V14.89h-2.54V12h2.54V9.797c0-2.506 1.492-3.89 3.777-3.89 1.094 0 2.238.195 2.238.195v2.46h-1.26c-1.243 0-1.63.771-1.63 1.562V12h2.773l-.443 2.89h-2.33v6.989C18.343 21.129 22 16.99 22 12c0-5.523-4.477-10-10-10z"/>
      </svg>
      Facebook
    </el-button>
    <el-button class="social-btn" @click="handleSocialLogin('google')">
      <svg viewBox="0 0 24 24" fill="currentColor">
        <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
        <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
        <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
        <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
      </svg>
      Google
    </el-button>
  </div>
</template>

<script setup>
const handleSocialLogin = (provider) => {
  console.log(`Social login with ${provider}`)
  alert(`${provider} 登录功能演示`)
}
</script>

<style scoped>
.social-login {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.social-btn {
  padding: 1rem !important;
  background: rgba(255, 255, 255, 0.8) !important;
  border: 2px solid rgba(220, 38, 38, 0.2) !important;
  border-radius: 16px !important;
  color: var(--color-text) !important;
  font-family: var(--font-body) !important;
  font-size: 0.875rem !important;
  font-weight: 700 !important;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1) !important;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  height: auto !important;
}

.social-btn:hover {
  background: white !important;
  border-color: var(--color-primary) !important;
  transform: translateY(-2px) !important;
  box-shadow: 0 10px 25px rgba(220, 38, 38, 0.2) !important;
}

.social-btn svg {
  width: 20px;
  height: 20px;
}
</style>
