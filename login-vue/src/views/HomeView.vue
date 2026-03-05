<template>
  <div class="home-view">
    <!-- 欢迎横幅 -->
    <WelcomeBanner />

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <StatsCard
        label="总用户数"
        :value="15234"
        :icon="UserFilled"
        iconBg="linear-gradient(135deg, #dc2626, #b91c1c)"
        trend="+12%"
        trendType="up"
        :delay="0"
      />
      <StatsCard
        label="总收入"
        :value="128456"
        :icon="Money"
        iconBg="linear-gradient(135deg, #f59e0b, #d97706)"
        trend="+8%"
        trendType="up"
        prefix="¥"
        :delay="100"
      />
      <StatsCard
        label="活跃会话"
        :value="1847"
        :icon="Connection"
        iconBg="linear-gradient(135deg, #10b981, #059669)"
        trend="实时"
        trendType="neutral"
        :delay="200"
      />
      <StatsCard
        label="增长率"
        :value="23.5"
        :icon="TrendCharts"
        iconBg="linear-gradient(135deg, #3b82f6, #2563eb)"
        trend="↑"
        trendType="up"
        suffix="%"
        :delay="300"
      />
    </div>

    <!-- 活动和快捷操作 -->
    <div class="content-grid">
      <ActivityFeed />
      <QuickActions />
    </div>
  </div>
</template>

<script setup>
import WelcomeBanner from '../components/dashboard/WelcomeBanner.vue'
import StatsCard from '../components/dashboard/StatsCard.vue'
import ActivityFeed from '../components/dashboard/ActivityFeed.vue'
import QuickActions from '../components/dashboard/QuickActions.vue'
import {
  UserFilled,
  Money,
  Connection,
  TrendCharts
} from '@element-plus/icons-vue'
</script>

<style scoped>
.home-view {
  max-width: 1400px;
  margin: 0 auto;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-bottom: 30px;
}

.content-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .content-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
}
</style>
