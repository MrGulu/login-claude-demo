<template>
  <div class="brand-section">
    <div class="brand-header">
      <div class="logo-container">
        <div class="logo-symbol">A</div>
        <div class="logo-name">AdminHub</div>
      </div>
    </div>

    <div class="brand-content">
      <h1 class="brand-title">
        智能管理<br>
        <span class="highlight">从这里开始</span>
      </h1>
      <p class="brand-description">
        现代化企业管理平台，为您提供强大、安全、高效的管理解决方案。
        让数据驱动决策，让管理更加智能。
      </p>
      <div class="feature-list">
        <div class="feature-item">
          <div class="feature-icon">✓</div>
          <span>企业级安全保障</span>
        </div>
        <div class="feature-item">
          <div class="feature-icon">✓</div>
          <span>实时数据分析</span>
        </div>
        <div class="feature-item">
          <div class="feature-icon">✓</div>
          <span>多端协同办公</span>
        </div>
      </div>
    </div>

    <div class="brand-footer">
      <div class="stat-item">
        <div class="stat-value">50K+</div>
        <div class="stat-label">企业用户</div>
      </div>
      <div class="stat-item">
        <div class="stat-value">99.9%</div>
        <div class="stat-label">系统可用性</div>
      </div>
      <div class="stat-item">
        <div class="stat-value">24/7</div>
        <div class="stat-label">技术支持</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.brand-section {
  position: absolute;
  left: 0;
  top: 0;
  width: 55%;
  height: 100%;
  background: linear-gradient(135deg,
    rgba(255, 255, 255, 0.9) 0%,
    rgba(255, 255, 255, 0.7) 100%);
  backdrop-filter: blur(20px);
  border-radius: 40px;
  padding: 4rem;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  box-shadow:
    0 30px 90px rgba(220, 38, 38, 0.2),
    inset 0 0 0 1px rgba(255, 255, 255, 0.9);
  clip-path: polygon(0 0, 100% 0, 85% 100%, 0 100%);
  animation: slideInLeft 1s cubic-bezier(0.16, 1, 0.3, 1);
}

.brand-header {
  animation: fadeIn 1s cubic-bezier(0.16, 1, 0.3, 1) 0.3s backwards;
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 3rem;
}

.logo-symbol {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-accent) 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: var(--font-display);
  font-size: 2rem;
  font-weight: 700;
  color: white;
  box-shadow: 0 10px 30px rgba(220, 38, 38, 0.4);
  position: relative;
}

.logo-symbol::before {
  content: '';
  position: absolute;
  inset: -3px;
  background: linear-gradient(135deg, var(--color-primary-light), var(--color-accent));
  border-radius: 18px;
  z-index: -1;
  opacity: 0.3;
  filter: blur(8px);
}

.logo-name {
  font-family: var(--font-display);
  font-size: 1.75rem;
  font-weight: 700;
  color: var(--color-text);
  letter-spacing: -0.02em;
}

.brand-content {
  max-width: 500px;
  animation: fadeIn 1s cubic-bezier(0.16, 1, 0.3, 1) 0.5s backwards;
}

.brand-title {
  font-family: var(--font-display);
  font-size: 3.5rem;
  font-weight: 700;
  line-height: 1.1;
  color: var(--color-text);
  margin-bottom: 1.5rem;
  letter-spacing: -0.03em;
}

.brand-title .highlight {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-accent) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.brand-description {
  font-size: 1.125rem;
  line-height: 1.7;
  color: var(--color-text-light);
  margin-bottom: 2rem;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-size: 1rem;
  color: var(--color-text-light);
  font-weight: 600;
}

.feature-icon {
  width: 24px;
  height: 24px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-accent) 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 0.75rem;
  flex-shrink: 0;
}

.brand-footer {
  display: flex;
  gap: 3rem;
  animation: fadeIn 1s cubic-bezier(0.16, 1, 0.3, 1) 0.7s backwards;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.stat-value {
  font-family: var(--font-display);
  font-size: 2rem;
  font-weight: 700;
  color: var(--color-primary);
}

.stat-label {
  font-size: 0.875rem;
  color: var(--color-text-muted);
  font-weight: 600;
}

@media (max-width: 1200px) {
  .brand-section {
    padding: 3rem;
  }

  .brand-title {
    font-size: 2.5rem;
  }
}

@media (max-width: 768px) {
  .brand-section {
    position: relative;
    width: 100%;
    clip-path: none;
    margin-bottom: 2rem;
  }
}
</style>
