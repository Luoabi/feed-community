<template>
  <div class="dashboard-container">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="banner-content">
        <div class="banner-text">
          <h1 class="banner-title">欢迎回来 👋</h1>
          <p class="banner-subtitle">今天是 {{ currentDate }}，让我们开始高效的一天</p>
        </div>
        <div class="banner-decoration">
          <div class="decoration-circle circle-1"></div>
          <div class="decoration-circle circle-2"></div>
          <div class="decoration-circle circle-3"></div>
        </div>
      </div>
    </div>

    <!-- 数据统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6" v-for="item in statsData" :key="item.title">
        <div class="stat-card" :style="{ '--card-color': item.color }">
          <div class="stat-card-bg"></div>
          <div class="stat-content">
            <div class="stat-icon-wrapper">
              <el-icon class="stat-icon">
                <component :is="item.icon" />
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ item.value }}</div>
              <div class="stat-title">{{ item.title }}</div>
              <div class="stat-trend" :class="item.trend > 0 ? 'trend-up' : 'trend-down'">
                <span>{{ item.trend > 0 ? '↑' : '↓' }} {{ Math.abs(item.trend) }}%</span>
                <span class="trend-label">较昨日</span>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <div class="quick-actions">
      <h3 class="section-title">快捷操作</h3>
      <el-row :gutter="15">
        <el-col :span="4" v-for="action in quickActions" :key="action.name">
          <div class="action-card" @click="handleQuickAction(action)">
            <el-icon class="action-icon" :style="{ color: action.color }">
              <component :is="action.icon" />
            </el-icon>
            <span class="action-name">{{ action.name }}</span>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 内容管理区域 -->
    <el-row :gutter="20" class="content-row">
      <el-col :span="12">
        <div class="modern-card">
          <div class="card-header">
            <div class="header-left">
              <el-icon class="header-icon"><Document /></el-icon>
              <span class="header-title">最近内容</span>
            </div>
            <el-button text type="primary" @click="viewAllContent">查看全部 →</el-button>
          </div>
          <div class="card-body">
            <div class="content-list" v-if="recentContent.length > 0">
              <div class="content-item" v-for="item in recentContent" :key="item.id">
                <div class="content-item-left">
                  <div class="content-avatar" :style="{ background: getRandomGradient() }">
                    {{ item.author?.charAt(0) || 'U' }}
                  </div>
                  <div class="content-details">
                    <div class="content-title">{{ item.title }}</div>
                    <div class="content-meta">
                      <span class="meta-author">{{ item.author }}</span>
                      <span class="meta-dot">•</span>
                      <span class="meta-time">{{ item.timeAgo }}</span>
                    </div>
                  </div>
                </div>
                <el-tag :type="item.status === '已发布' ? 'success' : 'warning'" effect="plain" size="small">
                  {{ item.status }}
                </el-tag>
              </div>
            </div>
            <div v-else class="empty-state">
              <el-icon class="empty-icon"><Document /></el-icon>
              <p>暂无最近内容</p>
            </div>
          </div>
        </div>
      </el-col>
      
      <el-col :span="12">
        <div class="modern-card">
          <div class="card-header">
            <div class="header-left">
              <el-icon class="header-icon"><Warning /></el-icon>
              <span class="header-title">待审核内容</span>
              <el-badge :value="pendingAudit.length" class="header-badge" />
            </div>
            <el-button text type="primary" @click="viewAllAudit">查看全部 →</el-button>
          </div>
          <div class="card-body">
            <div class="content-list" v-if="pendingAudit.length > 0">
              <div class="audit-item" v-for="item in pendingAudit" :key="item.id">
                <div class="audit-item-left">
                  <div class="audit-avatar" :style="{ background: getRandomGradient() }">
                    {{ item.author?.charAt(0) || 'U' }}
                  </div>
                  <div class="audit-details">
                    <div class="audit-title">{{ item.title }}</div>
                    <div class="audit-meta">
                      <span class="meta-author">{{ item.author }}</span>
                      <span class="meta-dot">•</span>
                      <span class="meta-time">{{ item.timeAgo }}</span>
                    </div>
                  </div>
                </div>
                <div class="audit-actions">
                  <el-button type="success" size="small" circle @click="handleAudit(item, 'pass')">
                    <el-icon><Check /></el-icon>
                  </el-button>
                  <el-button type="danger" size="small" circle @click="handleAudit(item, 'reject')">
                    <el-icon><Close /></el-icon>
                  </el-button>
                </div>
              </div>
            </div>
            <div v-else class="empty-state">
              <el-icon class="empty-icon"><Warning /></el-icon>
              <p>暂无待审核内容</p>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 数据图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="16">
        <div class="modern-card">
          <div class="card-header">
            <div class="header-left">
              <el-icon class="header-icon"><TrendCharts /></el-icon>
              <span class="header-title">访问趋势</span>
            </div>
            <el-radio-group v-model="chartPeriod" size="small" @change="handleChartPeriodChange">
              <el-radio-button label="week">本周</el-radio-button>
              <el-radio-button label="month">本月</el-radio-button>
              <el-radio-button label="year">本年</el-radio-button>
            </el-radio-group>
          </div>
          <div class="card-body chart-container">
            <canvas ref="chartRef" class="chart-canvas"></canvas>
          </div>
        </div>
      </el-col>
      
      <el-col :span="8">
        <div class="modern-card">
          <div class="card-header">
            <div class="header-left">
              <el-icon class="header-icon"><PieChart /></el-icon>
              <span class="header-title">内容分布</span>
            </div>
          </div>
          <div class="card-body">
            <div class="distribution-list">
              <div class="distribution-item" v-for="item in contentDistribution" :key="item.type">
                <div class="distribution-info">
                  <span class="distribution-label">{{ item.type }}</span>
                  <span class="distribution-value">{{ item.count }}</span>
                </div>
                <div class="distribution-bar">
                  <div class="bar-fill" :style="{ width: item.percentage + '%', background: item.color }"></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  User, Document, ChatDotRound, DataLine, 
  Plus, Edit, Delete, Setting, TrendCharts, 
  PieChart, Warning, Check, Close 
} from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import dashboardApi from '@/api/dashboard'
import auditApi from '@/api/audit'

const router = useRouter()

const currentDate = computed(() => {
  const date = new Date()
  const options = { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' }
  return date.toLocaleDateString('zh-CN', options)
})

const statsData = ref([
  { title: '用户总数', value: '0', icon: User, color: '#667eea', trend: 12.5 },
  { title: '内容总数', value: '0', icon: Document, color: '#f093fb', trend: 8.3 },
  { title: '评论总数', value: '0', icon: ChatDotRound, color: '#4facfe', trend: -3.2 },
  { title: '今日访问', value: '0', icon: DataLine, color: '#43e97b', trend: 15.7 }
])

const quickActions = ref([
  { name: '新建内容', icon: Plus, color: '#667eea', action: 'create' },
  { name: '编辑内容', icon: Edit, color: '#f093fb', action: 'edit' },
  { name: '内容审核', icon: Warning, color: '#4facfe', action: 'audit' },
  { name: '删除内容', icon: Delete, color: '#fa709a', action: 'delete' },
  { name: '系统设置', icon: Setting, color: '#43e97b', action: 'settings' },
])

const chartPeriod = ref('week')
const chartRef = ref(null)
let chartInstance = null

const contentDistribution = ref([
  { type: '文章', count: 245, percentage: 45, color: '#667eea' },
  { type: '帖子', count: 189, percentage: 35, color: '#f093fb' },
  { type: '评论', count: 98, percentage: 18, color: '#4facfe' },
  { type: '其他', count: 12, percentage: 2, color: '#43e97b' }
])

const recentContent = ref([])
const pendingAudit = ref([])
const loading = ref(false)

const gradients = [
  'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
  'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
  'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
  'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
  'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
  'linear-gradient(135deg, #30cfd0 0%, #330867 100%)'
]

const getRandomGradient = () => {
  return gradients[Math.floor(Math.random() * gradients.length)]
}

const loadStats = async () => {
  try {
    const res = await dashboardApi.getDashboardStats()
    if (res.data) {
      statsData.value[0].value = formatNumber(res.data.userCount || 0)
      statsData.value[1].value = formatNumber(res.data.contentCount || 0)
      statsData.value[2].value = formatNumber(res.data.commentCount || 0)
      statsData.value[3].value = formatNumber(res.data.todayUserCount || 0)
      
      // 计算趋势（如果后端提供了昨日数据）
      if (res.data.yesterdayUserCount) {
        statsData.value[0].trend = calculateTrend(res.data.userCount, res.data.yesterdayUserCount)
      }
      if (res.data.yesterdayContentCount) {
        statsData.value[1].trend = calculateTrend(res.data.contentCount, res.data.yesterdayContentCount)
      }
      if (res.data.yesterdayCommentCount) {
        statsData.value[2].trend = calculateTrend(res.data.commentCount, res.data.yesterdayCommentCount)
      }
      if (res.data.yesterdayVisit) {
        statsData.value[3].trend = calculateTrend(res.data.todayUserCount, res.data.yesterdayVisit)
      }
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
    // 使用默认值
    statsData.value[0].value = '1,250'
    statsData.value[1].value = '3,680'
    statsData.value[2].value = '8,920'
    statsData.value[3].value = '125'
  }
}

// 格式化数字（添加千位分隔符）
const formatNumber = (num) => {
  if (typeof num !== 'number') {
    num = parseInt(num) || 0
  }
  return num.toLocaleString('zh-CN')
}

// 计算趋势百分比
const calculateTrend = (current, previous) => {
  if (!previous || previous === 0) return 0
  const trend = ((current - previous) / previous * 100).toFixed(1)
  return parseFloat(trend)
}

const loadRecentContent = async () => {
  try {
    const res = await dashboardApi.getRecentContent()
    if (res.data && Array.isArray(res.data)) {
      recentContent.value = res.data
        .slice(0, 3) // 只取前3条
        .map(item => ({
          ...item,
          timeAgo: formatTimeAgo(item.createTime || item.publishTime)
        }))
    } else {
      // 使用模拟数据（只显示3条）
      recentContent.value = [
        { id: 1, title: 'Vue3 Composition API 最佳实践', author: '张三', status: '已发布', createTime: new Date(Date.now() - 2 * 60 * 60 * 1000), timeAgo: '2小时前' },
        { id: 2, title: 'Spring Boot 微服务架构设计', author: '李四', status: '已发布', createTime: new Date(Date.now() - 5 * 60 * 60 * 1000), timeAgo: '5小时前' },
        { id: 3, title: 'MySQL 性能优化技巧', author: '王五', status: '已发布', createTime: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000), timeAgo: '1天前' }
      ]
    }
  } catch (error) {
    console.error('加载最近内容失败:', error)
    // 使用模拟数据（只显示3条）
    recentContent.value = [
      { id: 1, title: 'Vue3 Composition API 最佳实践', author: '张三', status: '已发布', timeAgo: '2小时前' },
      { id: 2, title: 'Spring Boot 微服务架构设计', author: '李四', status: '已发布', timeAgo: '5小时前' },
      { id: 3, title: 'MySQL 性能优化技巧', author: '王五', status: '已发布', timeAgo: '1天前' }
    ]
  }
}

const loadPendingAudit = async () => {
  try {
    const res = await dashboardApi.getPendingAudit()
    if (res.data && Array.isArray(res.data)) {
      pendingAudit.value = res.data
        .slice(0, 3) // 只取前3条
        .map(item => ({
          ...item,
          timeAgo: formatTimeAgo(item.createTime)
        }))
    } else {
      // 使用模拟数据（只显示3条）
      pendingAudit.value = [
        { id: 101, title: 'React Hooks 深度解析', author: '用户A', createTime: new Date(Date.now() - 30 * 60 * 1000), timeAgo: '30分钟前' },
        { id: 102, title: 'TypeScript 类型体操', author: '用户B', createTime: new Date(Date.now() - 1 * 60 * 60 * 1000), timeAgo: '1小时前' },
        { id: 103, title: 'Webpack 5 配置指南', author: '用户C', createTime: new Date(Date.now() - 3 * 60 * 60 * 1000), timeAgo: '3小时前' }
      ]
    }
  } catch (error) {
    console.error('加载待审核内容失败:', error)
    // 使用模拟数据（只显示3条）
    pendingAudit.value = [
      { id: 101, title: 'React Hooks 深度解析', author: '用户A', timeAgo: '30分钟前' },
      { id: 102, title: 'TypeScript 类型体操', author: '用户B', timeAgo: '1小时前' },
      { id: 103, title: 'Webpack 5 配置指南', author: '用户C', timeAgo: '3小时前' }
    ]
  }
}

// 格式化时间为"xx前"的格式
const formatTimeAgo = (time) => {
  if (!time) return '刚刚'
  
  const now = new Date()
  const past = new Date(time)
  const diff = now - past // 毫秒差
  
  const seconds = Math.floor(diff / 1000)
  const minutes = Math.floor(seconds / 60)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)
  const months = Math.floor(days / 30)
  const years = Math.floor(days / 365)
  
  if (seconds < 60) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 30) return `${days}天前`
  if (months < 12) return `${months}个月前`
  return `${years}年前`
}

const handleAudit = async (row, action) => {
  try {
    if (action === 'pass') {
      // 通过审核
      await auditApi.approveContent({ id: row.id })
      ElMessage.success(`《${row.title}》审核通过`)
      loadPendingAudit()
    } else {
      // 拒绝审核 - 弹出对话框让用户输入拒绝原因
      ElMessageBox.prompt('请输入拒绝原因', '拒绝审核', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPlaceholder: '请输入拒绝原因',
        inputValidator: (value) => {
          if (!value || value.trim() === '') {
            return '拒绝原因不能为空'
          }
          return true
        }
      }).then(async ({ value }) => {
        await auditApi.rejectContent({ 
          id: row.id, 
          reason: value,
          violationType: '内容不符合规范'
        })
        ElMessage.success(`《${row.title}》已拒绝`)
        loadPendingAudit()
      }).catch(() => {
        // 用户取消操作
      })
    }
  } catch (error) {
    console.error('审核操作失败:', error)
    ElMessage.error('审核操作失败，请重试')
  }
}

const handleQuickAction = (action) => {
  // 根据action.action跳转到对应页面
  switch(action.action) {
    case 'create':
      router.push('/content/list')
      ElMessage.info('跳转到内容管理页面，可以新建内容')
      break
    case 'edit':
      router.push('/content/list')
      ElMessage.info('跳转到内容管理页面')
      break
    case 'audit':
      router.push('/content/audit')
      break
    case 'delete':
      router.push('/content/list')
      ElMessage.info('跳转到内容管理页面，可以删除内容')
      break
    case 'settings':
      router.push('/system/user')
      ElMessage.info('跳转到系统管理')
      break
    case 'stats':
      ElMessage.info('数据统计功能开发中...')
      break
    default:
      ElMessage.info(`执行操作: ${action.name}`)
  }
}

const viewAllContent = () => {
  router.push('/content/list')
}

const viewAllAudit = () => {
  router.push('/content/audit')
}

// 初始化图表
const initChart = () => {
  if (!chartRef.value) return
  
  // 使用CSS绘制简单的趋势图
  const chartData = generateChartData(chartPeriod.value)
  renderSimpleChart(chartData)
}

// 生成图表数据
const generateChartData = (period) => {
  let labels = []
  let data = []
  
  if (period === 'week') {
    labels = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    // 模拟一周的访问量数据，周末较高
    data = [320, 450, 380, 520, 680, 850, 920]
  } else if (period === 'month') {
    labels = ['第1周', '第2周', '第3周', '第4周']
    // 模拟一个月的访问量数据，呈上升趋势
    data = [2680, 3150, 3520, 4200]
  } else {
    labels = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
    // 模拟一年的访问量数据，整体上升
    data = [8200, 9100, 10500, 11800, 13200, 14800, 16200, 17500, 18900, 20100, 21500, 23000]
  }
  
  return { labels, data }
}

// 渲染Canvas图表
const renderSimpleChart = (chartData) => {
  if (!chartRef.value) return
  
  const canvas = chartRef.value
  const ctx = canvas.getContext('2d')
  const dpr = window.devicePixelRatio || 1
  
  // 设置Canvas尺寸
  const rect = canvas.getBoundingClientRect()
  canvas.width = rect.width * dpr
  canvas.height = rect.height * dpr
  ctx.scale(dpr, dpr)
  
  const width = rect.width
  const height = rect.height
  const padding = { top: 20, right: 30, bottom: 40, left: 60 }
  const chartWidth = width - padding.left - padding.right
  const chartHeight = height - padding.top - padding.bottom
  
  // 计算数据范围
  const maxValue = Math.max(...chartData.data)
  const minValue = Math.min(...chartData.data)
  const range = maxValue - minValue || 1
  const step = Math.ceil(range / 4)
  const adjustedMin = Math.floor(minValue / step) * step
  const adjustedMax = adjustedMin + step * 4
  const adjustedRange = adjustedMax - adjustedMin
  
  // 清空画布
  ctx.clearRect(0, 0, width, height)
  
  // 绘制网格线和Y轴标签
  ctx.strokeStyle = '#f0f0f0'
  ctx.lineWidth = 1
  ctx.fillStyle = '#95a5a6'
  ctx.font = '11px Arial'
  ctx.textAlign = 'right'
  ctx.textBaseline = 'middle'
  
  for (let i = 0; i <= 4; i++) {
    const y = padding.top + chartHeight - (i / 4) * chartHeight
    const value = adjustedMin + step * i
    
    // 绘制网格线
    ctx.beginPath()
    ctx.moveTo(padding.left, y)
    ctx.lineTo(padding.left + chartWidth, y)
    ctx.stroke()
    
    // 绘制Y轴标签
    ctx.fillText(value.toLocaleString(), padding.left - 10, y)
  }
  
  // 计算数据点坐标
  const points = chartData.data.map((value, index) => {
    const x = padding.left + (index / (chartData.data.length - 1)) * chartWidth
    const y = padding.top + chartHeight - ((value - adjustedMin) / adjustedRange) * chartHeight
    return { x, y, value, label: chartData.labels[index] }
  })
  
  // 绘制渐变填充区域
  const gradient = ctx.createLinearGradient(0, padding.top, 0, padding.top + chartHeight)
  gradient.addColorStop(0, 'rgba(102, 126, 234, 0.3)')
  gradient.addColorStop(1, 'rgba(102, 126, 234, 0.05)')
  
  ctx.fillStyle = gradient
  ctx.beginPath()
  ctx.moveTo(points[0].x, padding.top + chartHeight)
  points.forEach(point => {
    ctx.lineTo(point.x, point.y)
  })
  ctx.lineTo(points[points.length - 1].x, padding.top + chartHeight)
  ctx.closePath()
  ctx.fill()
  
  // 绘制渐变线条
  const lineGradient = ctx.createLinearGradient(padding.left, 0, padding.left + chartWidth, 0)
  lineGradient.addColorStop(0, '#667eea')
  lineGradient.addColorStop(1, '#764ba2')
  
  ctx.strokeStyle = lineGradient
  ctx.lineWidth = 2.5
  ctx.lineJoin = 'round'
  ctx.lineCap = 'round'
  
  ctx.beginPath()
  ctx.moveTo(points[0].x, points[0].y)
  points.forEach(point => {
    ctx.lineTo(point.x, point.y)
  })
  ctx.stroke()
  
  // 绘制数据点
  points.forEach((point, index) => {
    // 外圈阴影
    ctx.shadowColor = 'rgba(102, 126, 234, 0.3)'
    ctx.shadowBlur = 4
    ctx.shadowOffsetX = 0
    ctx.shadowOffsetY = 2
    
    // 绘制白色背景圆
    ctx.fillStyle = '#ffffff'
    ctx.beginPath()
    ctx.arc(point.x, point.y, 5, 0, Math.PI * 2)
    ctx.fill()
    
    // 绘制彩色边框
    ctx.shadowColor = 'transparent'
    ctx.strokeStyle = '#667eea'
    ctx.lineWidth = 2.5
    ctx.beginPath()
    ctx.arc(point.x, point.y, 5, 0, Math.PI * 2)
    ctx.stroke()
  })
  
  // 绘制X轴标签
  ctx.fillStyle = '#95a5a6'
  ctx.font = '11px Arial'
  ctx.textAlign = 'center'
  ctx.textBaseline = 'top'
  
  chartData.labels.forEach((label, index) => {
    const x = padding.left + (index / (chartData.data.length - 1)) * chartWidth
    ctx.fillText(label, x, padding.top + chartHeight + 10)
  })
  
  // 保存点坐标用于交互
  canvas.chartPoints = points
  
  // 添加鼠标移动事件
  canvas.onmousemove = (e) => {
    const rect = canvas.getBoundingClientRect()
    const mouseX = e.clientX - rect.left
    const mouseY = e.clientY - rect.top
    
    let hoveredPoint = null
    points.forEach(point => {
      const distance = Math.sqrt(Math.pow(mouseX - point.x, 2) + Math.pow(mouseY - point.y, 2))
      if (distance < 10) {
        hoveredPoint = point
      }
    })
    
    if (hoveredPoint) {
      canvas.style.cursor = 'pointer'
      // 重绘图表
      renderSimpleChart(chartData)
      
      // 高亮当前点
      ctx.shadowColor = 'rgba(102, 126, 234, 0.5)'
      ctx.shadowBlur = 8
      ctx.fillStyle = '#ffffff'
      ctx.beginPath()
      ctx.arc(hoveredPoint.x, hoveredPoint.y, 7, 0, Math.PI * 2)
      ctx.fill()
      
      ctx.shadowColor = 'transparent'
      ctx.strokeStyle = '#764ba2'
      ctx.lineWidth = 3
      ctx.beginPath()
      ctx.arc(hoveredPoint.x, hoveredPoint.y, 7, 0, Math.PI * 2)
      ctx.stroke()
      
      // 绘制工具提示
      const tooltipText = `${hoveredPoint.label}: ${hoveredPoint.value.toLocaleString()} 次访问`
      ctx.font = '12px Arial'
      const textWidth = ctx.measureText(tooltipText).width
      const tooltipWidth = textWidth + 20
      const tooltipHeight = 40
      const tooltipX = hoveredPoint.x - tooltipWidth / 2
      const tooltipY = hoveredPoint.y - tooltipHeight - 15
      
      // 绘制工具提示背景
      ctx.shadowColor = 'rgba(0, 0, 0, 0.2)'
      ctx.shadowBlur = 10
      ctx.fillStyle = 'rgba(0, 0, 0, 0.85)'
      ctx.beginPath()
      ctx.roundRect(tooltipX, tooltipY, tooltipWidth, tooltipHeight, 6)
      ctx.fill()
      
      // 绘制小三角
      ctx.shadowColor = 'transparent'
      ctx.beginPath()
      ctx.moveTo(hoveredPoint.x - 5, tooltipY + tooltipHeight)
      ctx.lineTo(hoveredPoint.x + 5, tooltipY + tooltipHeight)
      ctx.lineTo(hoveredPoint.x, tooltipY + tooltipHeight + 5)
      ctx.closePath()
      ctx.fill()
      
      // 绘制工具提示文字
      ctx.fillStyle = '#ffffff'
      ctx.font = '11px Arial'
      ctx.textAlign = 'center'
      ctx.textBaseline = 'top'
      ctx.fillText(hoveredPoint.label, hoveredPoint.x, tooltipY + 8)
      
      ctx.font = 'bold 13px Arial'
      ctx.fillStyle = '#a8b3ff'
      ctx.fillText(`${hoveredPoint.value.toLocaleString()} 次访问`, hoveredPoint.x, tooltipY + 22)
    } else {
      canvas.style.cursor = 'default'
    }
  }
  
  canvas.onmouseleave = () => {
    canvas.style.cursor = 'default'
    renderSimpleChart(chartData)
  }
}

// 监听图表周期变化
const handleChartPeriodChange = () => {
  nextTick(() => {
    initChart()
  })
}

// 加载内容分布数据
const loadContentDistribution = async () => {
  try {
    // 这里可以调用API获取真实数据
    // const res = await dashboardApi.getContentDistribution()
    // 暂时使用模拟数据
    const total = contentDistribution.value.reduce((sum, item) => sum + item.count, 0)
    contentDistribution.value.forEach(item => {
      item.percentage = Math.round((item.count / total) * 100)
    })
  } catch (error) {
    console.error('加载内容分布失败:', error)
  }
}

onMounted(() => {
  loadStats()
  loadRecentContent()
  loadPendingAudit()
  loadContentDistribution()
  nextTick(() => {
    initChart()
  })
})
</script>

<style scoped>
.dashboard-container {
  padding: 24px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: calc(100vh - 60px);
}

/* 欢迎横幅 */
.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  padding: 40px;
  margin-bottom: 24px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(102, 126, 234, 0.3);
}

.banner-content {
  position: relative;
  z-index: 2;
}

.banner-text {
  color: white;
}

.banner-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 12px 0;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.banner-subtitle {
  font-size: 16px;
  opacity: 0.95;
  margin: 0;
}

.banner-decoration {
  position: absolute;
  top: 0;
  right: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
}

.circle-1 {
  width: 200px;
  height: 200px;
  top: -50px;
  right: -50px;
}

.circle-2 {
  width: 150px;
  height: 150px;
  top: 50px;
  right: 100px;
}

.circle-3 {
  width: 100px;
  height: 100px;
  bottom: -30px;
  right: 200px;
}

/* 统计卡片 */
.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  position: relative;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.stat-card-bg {
  position: absolute;
  top: 0;
  right: 0;
  width: 120px;
  height: 120px;
  background: var(--card-color);
  opacity: 0.1;
  border-radius: 50%;
  transform: translate(30%, -30%);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
  position: relative;
  z-index: 2;
}

.stat-icon-wrapper {
  width: 60px;
  height: 60px;
  border-radius: 14px;
  background: var(--card-color);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon {
  font-size: 28px;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 4px;
}

.stat-title {
  font-size: 14px;
  color: #7f8c8d;
  margin-bottom: 8px;
}

.stat-trend {
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.trend-up {
  color: #27ae60;
}

.trend-down {
  color: #e74c3c;
}

.trend-label {
  color: #95a5a6;
}

/* 快捷操作 */
.quick-actions {
  margin-bottom: 24px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 16px 0;
}

.action-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.action-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
}

.action-icon {
  font-size: 32px;
  margin-bottom: 12px;
}

.action-name {
  display: block;
  font-size: 14px;
  color: #2c3e50;
  font-weight: 500;
}

/* 现代卡片 */
.content-row,
.chart-row {
  margin-bottom: 24px;
}

.modern-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  height: 100%;
}

.card-header {
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  font-size: 20px;
  color: #667eea;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}

.header-badge {
  margin-left: 8px;
}

.card-body {
  padding: 24px;
}

/* 内容列表 */
.content-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.content-item,
.audit-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
  transition: all 0.2s ease;
}

.content-item:hover,
.audit-item:hover {
  background: #e9ecef;
  transform: translateX(4px);
}

.content-item-left,
.audit-item-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;
}

.content-avatar,
.audit-avatar {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  font-size: 16px;
  flex-shrink: 0;
}

.content-details,
.audit-details {
  flex: 1;
  min-width: 0;
}

.content-title,
.audit-title {
  font-size: 14px;
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.content-meta,
.audit-meta {
  font-size: 12px;
  color: #95a5a6;
  display: flex;
  align-items: center;
  gap: 6px;
}

.meta-author {
  font-weight: 500;
}

.meta-dot {
  opacity: 0.5;
}

.audit-actions {
  display: flex;
  gap: 8px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #95a5a6;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 12px;
  opacity: 0.3;
}

.empty-state p {
  margin: 0;
  font-size: 14px;
}

/* 图表区域 */
.chart-container {
  height: 300px;
}

.chart-canvas {
  width: 100%;
  height: 100%;
  display: block;
}

.chart-placeholder {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #95a5a6;
}

.placeholder-icon {
  font-size: 64px;
  margin-bottom: 16px;
  opacity: 0.3;
}

/* 分布列表 */
.distribution-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.distribution-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.distribution-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.distribution-label {
  font-size: 14px;
  color: #2c3e50;
  font-weight: 500;
}

.distribution-value {
  font-size: 16px;
  font-weight: 700;
  color: #667eea;
}

.distribution-bar {
  height: 8px;
  background: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.6s ease;
}

/* 响应式 */
@media (max-width: 1200px) {
  .banner-title {
    font-size: 24px;
  }
  
  .stat-value {
    font-size: 24px;
  }
}
</style>
