<template>
  <div class="share-container">
    <el-card>
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="用户名">
          <el-input v-model="queryForm.userName" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="分享平台">
          <el-select v-model="queryForm.platform" placeholder="请选择平台" clearable>
            <el-option label="微信" value="wechat" />
            <el-option label="朋友圈" value="moments" />
            <el-option label="QQ" value="qq" />
            <el-option label="微博" value="weibo" />
            <el-option label="复制链接" value="link" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="queryForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="handleReset">重置</el-button>
          <el-button type="success" icon="Download" @click="handleExport">导出数据</el-button>
        </el-form-item>
      </el-form>

      <!-- 统计卡片 -->
      <el-row :gutter="20" style="margin-bottom: 20px;">
        <el-col :span="6">
          <el-card shadow="hover">
            <el-statistic title="今日分享" :value="stats.todayShares">
              <template #suffix>
                <el-icon style="color: #409eff;"><Share /></el-icon>
              </template>
            </el-statistic>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <el-statistic title="总分享数" :value="stats.totalShares" />
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <el-statistic title="分享用户" :value="stats.shareUsers" />
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <el-statistic title="平均分享率" :value="stats.avgShareRate" :precision="2">
              <template #suffix>%</template>
            </el-statistic>
          </el-card>
        </el-col>
      </el-row>

      <!-- 平台分布图表 -->
      <el-card style="margin-bottom: 20px;">
        <template #header>
          <span>分享平台分布</span>
        </template>
        <el-row :gutter="20">
          <el-col :span="4" v-for="item in platformStats" :key="item.platform">
            <div style="text-align: center; padding: 10px;">
              <div style="font-size: 24px; font-weight: bold; color: #409eff;">{{ item.count }}</div>
              <div style="color: #909399; margin-top: 5px;">{{ item.name }}</div>
              <el-progress :percentage="item.percentage" :show-text="false" style="margin-top: 10px;" />
            </div>
          </el-col>
        </el-row>
      </el-card>

      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userName" label="用户" width="120">
          <template #default="{ row }">
            <div style="display: flex; align-items: center;">
              <el-avatar :src="row.userAvatar" :size="30" style="margin-right: 8px;" />
              <span>{{ row.userName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="targetTitle" label="分享内容" show-overflow-tooltip />
        <el-table-column prop="platform" label="分享平台" width="120">
          <template #default="{ row }">
            <el-tag :type="getPlatformTag(row.platform)">
              {{ getPlatformText(row.platform) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="clickCount" label="点击次数" width="100" />
        <el-table-column prop="shareTime" label="分享时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleViewDetail(row)">查看详情</el-button>
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
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="分享详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户">{{ detailData.userName }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ detailData.userId }}</el-descriptions-item>
        <el-descriptions-item label="分享内容" :span="2">{{ detailData.targetTitle }}</el-descriptions-item>
        <el-descriptions-item label="分享平台">{{ getPlatformText(detailData.platform) }}</el-descriptions-item>
        <el-descriptions-item label="分享时间">{{ detailData.shareTime }}</el-descriptions-item>
        <el-descriptions-item label="点击次数">{{ detailData.clickCount }}</el-descriptions-item>
        <el-descriptions-item label="转化次数">{{ detailData.conversionCount }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ detailData.ip }}</el-descriptions-item>
        <el-descriptions-item label="设备">{{ detailData.device }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Share } from '@element-plus/icons-vue'
import interactionApi from '@/api/interaction'
import Logger from '@/utils/logger'

const queryForm = reactive({
  userName: '',
  platform: '',
  dateRange: []
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const stats = reactive({
  todayShares: 456,
  totalShares: 12345,
  shareUsers: 890,
  avgShareRate: 2.3
})

const platformStats = ref([
  { platform: 'wechat', name: '微信', count: 3456, percentage: 35 },
  { platform: 'moments', name: '朋友圈', count: 2890, percentage: 29 },
  { platform: 'qq', name: 'QQ', count: 1567, percentage: 16 },
  { platform: 'weibo', name: '微博', count: 1234, percentage: 12 },
  { platform: 'link', name: '复制链接', count: 798, percentage: 8 }
])

const tableData = ref([])
const detailVisible = ref(false)

const detailData = reactive({
  id: null,
  userId: null,
  userName: '',
  userAvatar: '',
  targetTitle: '',
  platform: '',
  shareTime: '',
  clickCount: 0,
  conversionCount: 0,
  ip: '',
  device: ''
})

const loadData = async () => {
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...queryForm,
      startDate: queryForm.dateRange?.[0],
      endDate: queryForm.dateRange?.[1]
    }
    delete params.dateRange
    const res = await interactionApi.getShareList(params)
    tableData.value = res.data.list || []
    pagination.total = res.data.total || 0
  } catch (error) {
    ElMessage.error('加载数据失败')
    console.error('加载分享列表失败:', error)
  }
}

const getPlatformTag = (platform) => {
  const map = {
    wechat: 'success',
    moments: 'warning',
    qq: '',
    weibo: 'danger',
    link: 'info'
  }
  return map[platform]
}

const getPlatformText = (platform) => {
  const map = {
    wechat: '微信',
    moments: '朋友圈',
    qq: 'QQ',
    weibo: '微博',
    link: '复制链接'
  }
  return map[platform]
}

const handleQuery = () => {
  Logger.query('分享管理', '查询分享记录', queryForm)
  loadData()
}

const handleReset = () => {
  queryForm.userName = ''
  queryForm.platform = ''
  queryForm.dateRange = []
  handleQuery()
}

const handleExport = () => {
  Logger.query('分享管理', '导出分享数据', queryForm)
  ElMessage.success('导出成功')
}

const handleViewDetail = (row) => {
  Object.assign(detailData, row)
  detailVisible.value = true
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.share-container {
  padding: 20px;
}

.query-form {
  margin-bottom: 20px;
}
</style>
