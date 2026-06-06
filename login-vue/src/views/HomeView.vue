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

    <!-- 数据图表大盘 -->
    <div class="charts-section">
      <div class="chart-card trend-card">
        <div ref="trendChartRef" class="chart-container"></div>
      </div>
      <div class="chart-card browser-card">
        <div ref="browserChartRef" class="chart-container"></div>
      </div>
    </div>

    <!-- 活动和快捷操作 -->
    <div class="content-grid">
      <ActivityFeed />
      <QuickActions />
    </div>

    <!-- 最新公告强提醒弹窗 -->
    <el-dialog
      v-model="noticeDialogVisible"
      title="📢 最新公告提醒"
      width="550px"
      :show-close="false"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      class="premium-notice-dialog"
    >
      <div class="premium-notice-content">
        <h3 class="notice-title">{{ latestNotice.title }}</h3>
        <p class="notice-meta">
          发布人: {{ latestNotice.author }} &nbsp;|&nbsp; 发布时间: {{ latestNotice.createTime }}
        </p>
        <div class="notice-body" v-html="latestNotice.content"></div>
      </div>
      <template #footer>
        <div class="premium-notice-footer">
          <el-button type="primary" @click="handleReadLatestNotice" class="read-confirm-btn">
            我已阅读
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
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
import { getStatistics } from '@/api/loginLog'
import { getNoticeList, markAsRead } from '@/api/notice'

// 图表 refs
const trendChartRef = ref(null)
const browserChartRef = ref(null)
let trendChart = null
let browserChart = null

// 统计数据
const loadingCharts = ref(false)

// 最新公告弹窗
const noticeDialogVisible = ref(false)
const latestNotice = ref({})

// 初始化统计图表
const loadStatistics = async () => {
  loadingCharts.value = true
  try {
    const response = await getStatistics()
    if (response.code === 200 && response.data) {
      const { trend, browser } = response.data
      await nextTick()
      initCharts(trend, browser)
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  } finally {
    loadingCharts.value = false
  }
}

const initCharts = (trendData, browserData) => {
  // 销毁旧实例防止内存泄露
  if (trendChart) trendChart.dispose()
  if (browserChart) browserChart.dispose()

  if (trendChartRef.value) {
    trendChart = echarts.init(trendChartRef.value)
    trendChart.setOption({
      title: { 
        text: '7日系统登录活跃趋势', 
        left: '20px', 
        top: '20px',
        textStyle: { 
          color: '#1f2937', 
          fontSize: 16,
          fontWeight: '600',
          fontFamily: 'Inter, sans-serif'
        } 
      },
      tooltip: { 
        trigger: 'axis',
        backgroundColor: 'rgba(255, 255, 255, 0.9)',
        borderColor: '#f3f4f6',
        textStyle: { color: '#1f2937' },
        boxShadow: '0 4px 12px rgba(0, 0, 0, 0.05)'
      },
      grid: {
        left: '4%',
        right: '4%',
        bottom: '8%',
        top: '25%',
        containLabel: true
      },
      xAxis: { 
        type: 'category', 
        data: trendData.dates, 
        axisLine: { lineStyle: { color: '#e5e7eb' } },
        axisLabel: { color: '#4b5563', fontSize: 11 }
      },
      yAxis: { 
        type: 'value', 
        axisLabel: { color: '#4b5563', fontSize: 11 },
        splitLine: { lineStyle: { type: 'dashed', color: '#f3f4f6' } } 
      },
      series: [{
        name: '登录次数',
        data: trendData.counts,
        type: 'line',
        smooth: true,
        lineStyle: { color: '#dc2626', width: 3.5 },
        itemStyle: { color: '#dc2626' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(220, 38, 38, 0.25)' },
            { offset: 1, color: 'rgba(220, 38, 38, 0.001)' }
          ])
        }
      }]
    })
  }

  if (browserChartRef.value) {
    browserChart = echarts.init(browserChartRef.value)
    browserChart.setOption({
      title: { 
        text: '登录客户端占比', 
        left: '20px', 
        top: '20px',
        textStyle: { 
          color: '#1f2937', 
          fontSize: 16,
          fontWeight: '600',
          fontFamily: 'Inter, sans-serif'
        } 
      },
      tooltip: { 
        trigger: 'item', 
        formatter: '{b}: {c}次 ({d}%)',
        backgroundColor: 'rgba(255, 255, 255, 0.9)',
        borderColor: '#f3f4f6',
        textStyle: { color: '#1f2937' }
      },
      legend: { 
        bottom: '20px', 
        left: 'center',
        icon: 'circle',
        itemWidth: 10,
        itemHeight: 10,
        textStyle: { color: '#4b5563', fontSize: 12 }
      },
      series: [{
        name: '浏览器',
        type: 'pie',
        radius: ['38%', '68%'],
        center: ['50%', '46%'],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
        label: { show: false, position: 'center' },
        emphasis: { 
          label: { 
            show: true, 
            fontSize: 14, 
            fontWeight: 'bold',
            formatter: '{b}\n{d}%'
          } 
        },
        labelLine: { show: false },
        data: browserData,
        color: ['#dc2626', '#f59e0b', '#10b981', '#3b82f6', '#8b5cf6', '#ec4899', '#6b7280']
      }]
    })
  }
}

// 检查并弹窗最新未读公告
const checkLatestNotice = async () => {
  try {
    const response = await getNoticeList({ page: 1, size: 5, status: 1 })
    if (response.code === 200 && response.data.records && response.data.records.length > 0) {
      // 寻找第一条未读公告进行强提醒
      const firstUnread = response.data.records.find(item => item.readStatus === 0)
      if (firstUnread) {
        latestNotice.value = firstUnread
        noticeDialogVisible.value = true
      }
    }
  } catch (error) {
    console.error('检查公告消息失败:', error)
  }
}

// 阅读并关闭强提醒
const handleReadLatestNotice = async () => {
  try {
    await markAsRead(latestNotice.value.id)
    noticeDialogVisible.value = false
    // 派发事件让顶栏 Header 铃铛重新拉取未读数
    window.dispatchEvent(new CustomEvent('notice-read-change'))
    // 局部重新请求统计，或刷新公告状态
  } catch (error) {
    console.error('标记已读失败:', error)
  }
}

// 响应尺寸调整
const handleResize = () => {
  if (trendChart) trendChart.resize()
  if (browserChart) browserChart.resize()
}

onMounted(() => {
  loadStatistics()
  checkLatestNotice()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (trendChart) trendChart.dispose()
  if (browserChart) browserChart.dispose()
})
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

.charts-section {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
  margin-bottom: 30px;
}

.chart-card {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.03);
  padding: 10px;
  min-height: 380px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.chart-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 36px rgba(0, 0, 0, 0.06);
}

.chart-container {
  width: 100%;
  height: 360px;
}

.content-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

/* 强弹窗磨砂玻璃样式 */
:deep(.premium-notice-dialog .el-dialog) {
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 25px 60px rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.7);
  background: rgba(255, 255, 255, 0.85) !important;
  backdrop-filter: blur(20px);
}

:deep(.premium-notice-dialog .el-dialog__header) {
  padding: 24px 28px 12px;
  background: linear-gradient(135deg, rgba(220, 38, 38, 0.08) 0%, rgba(245, 158, 11, 0.08) 100%);
  border-bottom: 1px solid rgba(220, 38, 38, 0.08);
}

:deep(.premium-notice-dialog .el-dialog__title) {
  font-size: 20px;
  font-weight: 700;
  color: #b91c1c;
  font-family: 'Playfair Display', serif;
}

.premium-notice-content {
  padding: 20px 28px;
}

.notice-title {
  font-size: 22px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 10px 0;
  line-height: 1.4;
}

.notice-meta {
  font-size: 13px;
  color: #6b7280;
  margin: 0 0 20px 0;
  border-bottom: 1px dashed rgba(0, 0, 0, 0.08);
  padding-bottom: 12px;
}

.notice-body {
  font-size: 15px;
  line-height: 1.7;
  color: #374151;
  max-height: 250px;
  overflow-y: auto;
  padding-right: 8px;
}

.notice-body::-webkit-scrollbar {
  width: 4px;
}

.notice-body::-webkit-scrollbar-thumb {
  background: rgba(220, 38, 38, 0.2);
  border-radius: 2px;
}

.premium-notice-footer {
  padding: 0 28px 24px;
  display: flex;
  justify-content: flex-end;
}

.read-confirm-btn {
  border-radius: 12px;
  padding: 12px 36px;
  font-size: 15px;
  font-weight: 600;
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  border: none;
  box-shadow: 0 4px 14px rgba(220, 38, 38, 0.25);
  transition: all 0.3s ease;
}

.read-confirm-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(220, 38, 38, 0.35);
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .charts-section {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .charts-section {
    gap: 16px;
  }

  .content-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
}
</style>
