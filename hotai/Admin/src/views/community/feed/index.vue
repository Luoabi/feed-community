<template>
  <div class="feed-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="bg-circle circle-1"></div>
      <div class="bg-circle circle-2"></div>
      <div class="bg-circle circle-3"></div>
      <div class="bg-wave"></div>
    </div>

    <el-row :gutter="20" class="content-wrapper">
      <!-- Feed流配置 -->
      <el-col :span="8">
        <div class="config-card modern-card">
          <div class="card-header-custom">
            <el-icon class="header-icon-custom"><Setting /></el-icon>
            <span class="header-title-custom">Feed流算法配置</span>
          </div>
          <el-form :model="configForm" label-width="120px" class="config-form">
            <el-form-item label="推荐算法">
              <el-select v-model="configForm.algorithm" style="width: 100%" size="large">
                <el-option label="🔥 热度排序" value="hot" />
                <el-option label="⏰ 时间排序" value="time" />
                <el-option label="⚡ 综合排序" value="mixed" />
                <el-option label="🎯 个性化推荐" value="personalized" />
              </el-select>
            </el-form-item>
            <el-form-item label="热度权重">
              <el-slider v-model="configForm.hotWeight" :max="100" show-input />
            </el-form-item>
            <el-form-item label="时间权重">
              <el-slider v-model="configForm.timeWeight" :max="100" show-input />
            </el-form-item>
            <el-form-item label="互动权重">
              <el-slider v-model="configForm.interactionWeight" :max="100" show-input />
            </el-form-item>
            <el-form-item label="刷新间隔(秒)">
              <el-input-number v-model="configForm.refreshInterval" :min="60" :max="3600" style="width: 100%" />
            </el-form-item>
            <el-form-item label="缓存时长(分钟)">
              <el-input-number v-model="configForm.cacheTime" :min="5" :max="60" style="width: 100%" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSaveConfig" class="gradient-button" style="width: 100%;">
                <el-icon><Check /></el-icon>
                <span>保存配置</span>
              </el-button>
              <el-button @click="handleResetConfig" style="width: 100%; margin-top: 10px;">
                <el-icon><RefreshLeft /></el-icon>
                <span>重置</span>
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <div class="cache-card modern-card" style="margin-top: 20px;">
          <div class="card-header-custom">
            <el-icon class="header-icon-custom"><DataLine /></el-icon>
            <span class="header-title-custom">缓存管理</span>
          </div>
          <div class="cache-stats">
            <div class="cache-stat-item">
              <div class="stat-icon-wrapper" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                <el-icon><Checked /></el-icon>
              </div>
              <div class="stat-details">
                <div class="stat-label">缓存状态</div>
                <div class="stat-value-text">运行中</div>
              </div>
            </div>
            <div class="cache-stat-item">
              <div class="stat-icon-wrapper" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
                <el-icon><Document /></el-icon>
              </div>
              <div class="stat-details">
                <div class="stat-label">缓存数量</div>
                <div class="stat-value-text">{{ cacheInfo.count }} 条</div>
              </div>
            </div>
            <div class="cache-stat-item">
              <div class="stat-icon-wrapper" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
                <el-icon><Coin /></el-icon>
              </div>
              <div class="stat-details">
                <div class="stat-label">缓存大小</div>
                <div class="stat-value-text">{{ cacheInfo.size }} MB</div>
              </div>
            </div>
            <div class="cache-stat-item">
              <div class="stat-icon-wrapper" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">
                <el-icon><Clock /></el-icon>
              </div>
              <div class="stat-details">
                <div class="stat-label">最后更新</div>
                <div class="stat-value-text">{{ cacheInfo.lastUpdate }}</div>
              </div>
            </div>
          </div>
          <div class="cache-actions">
            <el-button type="warning" @click="handleRefreshCache" class="action-btn">
              <el-icon><Refresh /></el-icon>
              <span>刷新缓存</span>
            </el-button>
            <el-button type="danger" @click="handleClearCache" class="action-btn">
              <el-icon><Delete /></el-icon>
              <span>清空缓存</span>
            </el-button>
          </div>
        </div>
      </el-col>

      <!-- Feed流内容列表 -->
      <el-col :span="16">
        <div class="content-card modern-card">
          <div class="card-header-custom">
            <div class="header-left-custom">
              <el-icon class="header-icon-custom"><List /></el-icon>
              <span class="header-title-custom">Feed流内容管理</span>
            </div>
            <el-button type="primary" size="default" @click="handlePushContent" class="gradient-button">
              <el-icon><Plus /></el-icon>
              <span>推送内容</span>
            </el-button>
          </div>

          <el-form :inline="true" :model="queryForm" class="query-form">
            <el-form-item label="标题">
              <el-input v-model="queryForm.title" placeholder="请输入标题" clearable />
            </el-form-item>
            <el-form-item label="类型">
              <el-select v-model="queryForm.contentType" placeholder="请选择类型" clearable>
                <el-option label="📝 帖子" value="post" />
                <el-option label="📄 文章" value="article" />
                <el-option label="🎬 视频" value="video" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
              <el-button icon="Refresh" @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>

          <el-table :data="tableData" style="width: 100%" class="modern-table">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="title" label="标题" width="200" show-overflow-tooltip />
            <el-table-column prop="contentType" label="类型" width="100">
              <template #default="{ row }">
                <el-tag :type="getContentTypeTag(row.contentType)" effect="plain">
                  {{ getContentTypeText(row.contentType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="hotScore" label="热度分数" width="120">
              <template #default="{ row }">
                <div class="score-badge">
                  <el-icon><TrendCharts /></el-icon>
                  <span>{{ row.hotScore }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="views" label="浏览" width="80" />
            <el-table-column prop="likes" label="点赞" width="80" />
            <el-table-column prop="pushTime" label="推送时间" width="180" />
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="handleAdjustScore(row)" link>
                  调整分数
                </el-button>
                <el-button type="danger" size="small" @click="handleRemove(row)" link>
                  移除
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.size"
            :total="pagination.total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleQuery"
            @current-change="handleQuery"
            style="margin-top: 20px; justify-content: flex-end;"
          />
        </div>
      </el-col>
    </el-row>

    <!-- 调整分数对话框 -->
    <el-dialog v-model="scoreVisible" title="调整热度分数" width="500px" :destroy-on-close="true" class="modern-dialog">
      <el-form :model="scoreForm" label-width="100px">
        <el-form-item label="当前分数">
          <el-input-number v-model="scoreForm.currentScore" disabled style="width: 100%" />
        </el-form-item>
        <el-form-item label="新分数">
          <el-input-number v-model="scoreForm.newScore" :min="0" :max="10000" style="width: 100%" />
        </el-form-item>
        <el-form-item label="调整原因">
          <el-input v-model="scoreForm.reason" type="textarea" :rows="3" placeholder="请输入调整原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="scoreVisible = false">取消</el-button>
        <el-button type="primary" @click="handleScoreSubmit" class="gradient-button">确定</el-button>
      </template>
    </el-dialog>

    <!-- 推送内容对话框 -->
    <el-dialog v-model="pushVisible" title="推送内容到Feed流" width="600px" :destroy-on-close="true" class="modern-dialog">
      <el-form :model="pushForm" label-width="100px">
        <el-form-item label="内容类型">
          <el-select v-model="pushForm.contentType" placeholder="请选择类型" style="width: 100%">
            <el-option label="📝 帖子" value="post" />
            <el-option label="📄 文章" value="article" />
            <el-option label="🎬 视频" value="video" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容ID">
          <el-input v-model="pushForm.contentId" placeholder="请输入内容ID" />
        </el-form-item>
        <el-form-item label="初始分数">
          <el-input-number v-model="pushForm.initialScore" :min="0" :max="10000" style="width: 100%" />
        </el-form-item>
        <el-form-item label="推送位置">
          <el-radio-group v-model="pushForm.position">
            <el-radio label="top">置顶</el-radio>
            <el-radio label="normal">正常</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pushVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePushSubmit" class="gradient-button">确定推送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Setting, DataLine, Checked, Document, Coin, Clock, 
  Refresh, Delete, List, Plus, Check, RefreshLeft, 
  TrendCharts 
} from '@element-plus/icons-vue'
import feedApi from '@/api/feed'
import Logger from '@/utils/logger'

const configForm = reactive({
  algorithm: 'mixed',
  hotWeight: 40,
  timeWeight: 30,
  interactionWeight: 30,
  refreshInterval: 300,
  cacheTime: 15
})

const cacheInfo = reactive({
  count: 0,
  size: 0,
  lastUpdate: new Date().toLocaleString()
})

const queryForm = reactive({
  title: '',
  contentType: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const tableData = ref([])
const scoreVisible = ref(false)
const pushVisible = ref(false)
const currentRow = ref(null)

const scoreForm = reactive({
  currentScore: 0,
  newScore: 0,
  reason: ''
})

const pushForm = reactive({
  contentType: 'post',
  contentId: '',
  initialScore: 100,
  position: 'normal'
})

const loadData = async () => {
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...queryForm
    }
    const res = await feedApi.getFeedContentList(params)
    tableData.value = res.data.list || []
    pagination.total = res.data.total || 0
  } catch (error) {
    ElMessage.error('加载数据失败')
    console.error('加载Feed流内容失败:', error)
  }
}

const loadStats = async () => {
  try {
    const res = await feedApi.getFeedStats()
    if (res.data) {
      cacheInfo.count = res.data.totalContent || 0
      cacheInfo.size = res.data.cacheSize || 0
      cacheInfo.lastUpdate = new Date().toLocaleString()
    }
  } catch (error) {
    console.error('加载统计失败:', error)
  }
}

const getContentTypeTag = (type) => {
  const map = {
    post: '',
    article: 'success',
    video: 'warning'
  }
  return map[type]
}

const getContentTypeText = (type) => {
  const map = {
    post: '帖子',
    article: '文章',
    video: '视频'
  }
  return map[type]
}

const handleSaveConfig = async () => {
  try {
    await feedApi.updateFeedConfig(configForm)
    Logger.update('Feed流管理', '保存Feed流配置', configForm)
    ElMessage.success('配置保存成功')
  } catch (error) {
    ElMessage.error('保存配置失败')
    console.error('保存Feed流配置失败:', error)
  }
}

const handleResetConfig = () => {
  Object.assign(configForm, {
    algorithm: 'mixed',
    hotWeight: 40,
    timeWeight: 30,
    interactionWeight: 30,
    refreshInterval: 300,
    cacheTime: 15
  })
  ElMessage.info('配置已重置')
}

const handleRefreshCache = async () => {
  ElMessageBox.confirm('确定要刷新Feed流缓存吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await loadStats()
      await loadData()
      Logger.update('Feed流管理', '刷新Feed流缓存', {})
      ElMessage.success('缓存刷新成功')
    } catch (error) {
      ElMessage.error('刷新缓存失败')
      console.error('刷新Feed流缓存失败:', error)
    }
  })
}

const handleClearCache = () => {
  ElMessageBox.confirm('确定要清空Feed流缓存吗？此操作不可恢复！', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'error'
  }).then(() => {
    Logger.delete('Feed流管理', '清空Feed流缓存', {})
    cacheInfo.count = 0
    cacheInfo.size = 0
    cacheInfo.lastUpdate = new Date().toLocaleString()
    ElMessage.success('缓存已清空')
  })
}

const handleQuery = () => {
  Logger.query('Feed流管理', '查询Feed流内容', queryForm)
  loadData()
}

const handleReset = () => {
  queryForm.title = ''
  queryForm.contentType = ''
  handleQuery()
}

const handleAdjustScore = (row) => {
  currentRow.value = row
  scoreForm.currentScore = row.hotScore
  scoreForm.newScore = row.hotScore
  scoreForm.reason = ''
  scoreVisible.value = true
}

const handleScoreSubmit = async () => {
  if (!scoreForm.reason) {
    ElMessage.warning('请输入调整原因')
    return
  }
  try {
    await feedApi.updateHotScore({
      contentId: currentRow.value.id,
      hotScore: scoreForm.newScore
    })
    currentRow.value.hotScore = scoreForm.newScore
    Logger.update('Feed流管理', `调整热度分数: ${currentRow.value.title}`, {
      id: currentRow.value.id,
      oldScore: scoreForm.currentScore,
      newScore: scoreForm.newScore,
      reason: scoreForm.reason
    })
    ElMessage.success('分数调整成功')
    scoreVisible.value = false
  } catch (error) {
    ElMessage.error('分数调整失败')
    console.error('调整热度分数失败:', error)
  }
}

const handleRemove = (row) => {
  ElMessageBox.confirm(`确定要从Feed流中移除 "${row.title}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await feedApi.removeFromFeed(row.id)
      Logger.delete('Feed流管理', `移除Feed流内容: ${row.title}`, { id: row.id })
      ElMessage.success('移除成功')
      handleQuery()
    } catch (error) {
      ElMessage.error('移除失败')
      console.error('移除Feed流内容失败:', error)
    }
  })
}

const handlePushContent = () => {
  pushForm.contentType = 'post'
  pushForm.contentId = ''
  pushForm.initialScore = 100
  pushForm.position = 'normal'
  pushVisible.value = true
}

const handlePushSubmit = async () => {
  if (!pushForm.contentId) {
    ElMessage.warning('请输入内容ID')
    return
  }
  try {
    await feedApi.pushToFeed(pushForm)
    Logger.create('Feed流管理', '推送内容到Feed流', pushForm)
    ElMessage.success('推送成功')
    pushVisible.value = false
    handleQuery()
  } catch (error) {
    ElMessage.error('推送失败')
    console.error('推送内容到Feed流失败:', error)
  }
}

onMounted(() => {
  loadData()
  loadStats()
})
</script>

<style scoped>
.feed-container {
  padding: 24px;
  min-height: calc(100vh - 60px);
  position: relative;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow: hidden;
}

/* 背景装饰 */
.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 20s infinite ease-in-out;
}

.circle-1 {
  width: 400px;
  height: 400px;
  top: -200px;
  left: -200px;
  animation-delay: 0s;
}

.circle-2 {
  width: 300px;
  height: 300px;
  top: 50%;
  right: -150px;
  animation-delay: 5s;
}

.circle-3 {
  width: 250px;
  height: 250px;
  bottom: -125px;
  left: 30%;
  animation-delay: 10s;
}

.bg-wave {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 200px;
  background: linear-gradient(180deg, transparent 0%, rgba(255, 255, 255, 0.05) 100%);
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) scale(1);
  }
  50% {
    transform: translateY(-30px) scale(1.05);
  }
}

.content-wrapper {
  position: relative;
  z-index: 1;
}

/* 现代卡片 */
.modern-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.modern-card:hover {
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.card-header-custom {
  padding: 24px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
}

.header-left-custom {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon-custom {
  font-size: 22px;
  color: #667eea;
}

.header-title-custom {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

/* 配置表单 */
.config-form {
  padding: 20px;
}

.config-form .el-form-item {
  margin-bottom: 24px;
}

/* 渐变按钮 */
.gradient-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
  font-weight: 500;
  transition: all 0.3s ease;
}

.gradient-button:hover {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

/* 缓存统计 */
.cache-stats {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.cache-stat-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
  transition: all 0.2s ease;
}

.cache-stat-item:hover {
  background: #e9ecef;
  transform: translateX(4px);
}

.stat-icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  flex-shrink: 0;
}

.stat-details {
  flex: 1;
}

.stat-label {
  font-size: 13px;
  color: #7f8c8d;
  margin-bottom: 4px;
}

.stat-value-text {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}

/* 缓存操作 */
.cache-actions {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.action-btn {
  width: 100%;
  height: 44px;
  font-size: 15px;
  font-weight: 500;
  border-radius: 10px;
}

/* 查询表单 */
.query-form {
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
  margin-bottom: 20px;
}

/* 现代表格 */
.modern-table {
  border-radius: 12px;
  overflow: hidden;
}

.score-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 12px;
  background: linear-gradient(135deg, #feca57 0%, #ff9ff3 100%);
  color: white;
  border-radius: 20px;
  font-weight: 600;
  font-size: 13px;
}

/* 对话框 */
.modern-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

.modern-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  padding: 20px 24px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.modern-dialog :deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

/* 响应式 */
@media (max-width: 1200px) {
  .feed-container {
    padding: 16px;
  }
  
  .card-header-custom {
    padding: 16px;
  }
  
  .cache-stats,
  .cache-actions {
    padding: 16px;
  }
}
</style>
