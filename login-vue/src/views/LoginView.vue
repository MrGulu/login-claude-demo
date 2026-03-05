<template>
  <div class="login-view">
    <Background />

    <div class="container">
      <div class="diagonal-wrapper">
        <!-- Decorative Lines -->
        <div class="deco-line deco-line-1"></div>
        <div class="deco-line deco-line-2"></div>

        <!-- Brand Section -->
        <BrandSection />

        <!-- Login Form -->
        <LoginForm />
      </div>
    </div>
  </div>
</template>

<script setup>
import Background from '../components/Background.vue'
import BrandSection from '../components/BrandSection.vue'
import LoginForm from '../components/LoginForm.vue'
</script>

<style scoped>
.login-view {
  width: 100%;
  height: 100vh;
  position: relative;
}

.container {
  position: relative;
  z-index: 10;
  display: flex;
  min-height: 100vh;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}

.diagonal-wrapper {
  position: relative;
  width: 100%;
  max-width: 1400px;
  height: 700px;
  display: flex;
  align-items: center;
}

/* Decorative Lines */
.deco-line {
  position: absolute;
  background: linear-gradient(90deg, transparent, rgba(220, 38, 38, 0.2), transparent);
  height: 2px;
  animation: lineMove 3s ease-in-out infinite;
}

.deco-line-1 {
  top: 20%;
  left: 0;
  width: 40%;
  animation-delay: 0s;
}

.deco-line-2 {
  bottom: 30%;
  right: 0;
  width: 35%;
  animation-delay: 1.5s;
}

@media (max-width: 1200px) {
  .diagonal-wrapper {
    max-width: 1000px;
    height: 600px;
  }
}

@media (max-width: 768px) {
  .container {
    padding: 1rem;
  }

  .diagonal-wrapper {
    flex-direction: column;
    height: auto;
  }
}
</style>
