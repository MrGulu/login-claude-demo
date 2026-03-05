<template>
  <div class="background">
    <div class="gradient-orb orb-1"></div>
    <div class="gradient-orb orb-2"></div>
    <div class="gradient-orb orb-3"></div>
    <div class="grid-pattern"></div>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted } from 'vue'

const handleMouseMove = (e) => {
  const orbs = document.querySelectorAll('.gradient-orb')
  const mouseX = e.clientX / window.innerWidth
  const mouseY = e.clientY / window.innerHeight

  orbs.forEach((orb, index) => {
    const speed = (index + 1) * 15
    const x = (mouseX - 0.5) * speed
    const y = (mouseY - 0.5) * speed
    orb.style.transform = `translate(${x}px, ${y}px)`
  })
}

onMounted(() => {
  document.addEventListener('mousemove', handleMouseMove)
})

onUnmounted(() => {
  document.removeEventListener('mousemove', handleMouseMove)
})
</script>

<style scoped>
.background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  overflow: hidden;
}

.gradient-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.6;
  animation: float 25s ease-in-out infinite;
}

.orb-1 {
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(220, 38, 38, 0.4) 0%, transparent 70%);
  top: -10%;
  left: -10%;
  animation-delay: 0s;
}

.orb-2 {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(245, 158, 11, 0.3) 0%, transparent 70%);
  bottom: -10%;
  right: -10%;
  animation-delay: 8s;
}

.orb-3 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(239, 68, 68, 0.35) 0%, transparent 70%);
  top: 40%;
  left: 50%;
  animation-delay: 15s;
}

.grid-pattern {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image:
    linear-gradient(rgba(220, 38, 38, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(220, 38, 38, 0.03) 1px, transparent 1px);
  background-size: 50px 50px;
  animation: gridMove 20s linear infinite;
}
</style>
