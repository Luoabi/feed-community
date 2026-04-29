<template>
  <div class="collect-container">
    <el-card>
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="用户名">
          <el-input v-model="queryForm.userName" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="内容类型">
          <el-select v-model="queryForm.targetType" placeholder="请选择类型" clearable>
            <el-option label="帖子" value="post" />
            <el-option label="文章" value="content" />
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
            <el-statistic title="今日收藏" :value="stats.todayCollects">
              <template #suffix>
                <el-icon style="color: #e6a23c;"><Star /></el-icon>
              </template>
            </el-statistic>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <el-statistic title="总收藏数" :value="stats.totalCollects" />
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <el-statistic title="收藏用户" :value="stats.collectUsers" />
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <el-statistic title="平均收藏率" :value="stats.avgCollectRate" :precision="2">
              <template #suffix>%</template>
            </el-statistic>
          </el-card>
        </el-col>
      </el-row>

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
        <el-table-column prop="targetType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTargetTypeTag(row.targetType)">
              {{ getTargetTypeText(row.targetType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetTitle" label="内容标题" show-overflow-tooltip />
        <el-table-column prop="targetAuthor" label="内容作者" width="120" />
        <el-table-column prop="collectTime" label="收藏时间" width="180" />
        <el-table-column prop="folderName" label="收藏夹" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '有效' : '已取消' }}
            </el-tag>
          </template>
        </el-table-column>
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
    <el-dialog v-model="detailVisible" title="收藏详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户">{{ detailData.userName }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ detailData.userId }}</el-descriptions-item>
        <el-descriptions-item label="内容类型">{{ getTargetTypeText(detailData.targetType) }}</el-descriptions-item>
        <el-descriptions-item label="内容ID">{{ detailData.targetId }}</el-descriptions-item>
        <el-descriptions-item label="内容标题" :span="2">{{ detailData.targetTitle }}</el-descriptions-item>
        <el-descriptions-item label="内容作者">{{ detailData.targetAuthor }}</el-descriptions-item>
        <el-descriptions-item label="收藏夹">{{ detailData.folderName }}</el-descriptions-item>
        <el-descriptions-item label="收藏时间">{{ detailData.collectTime }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ detailData.ip }}</el-descriptions-item>
        <el-descriptions-item label="设备">{{ detailData.device }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detailData.status === 1 ? 'success' : 'info'">
            {{ detailData.status === 1 ? '有效' : '已取消' }}
          </el-tag>
        </el-descriptions-item>
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
import { Star } from '@element-plus/icons-vue'
import interactionApi from '@/api/interaction'
import Logger from '@/utils/logger'

const queryForm = reactive({
  userName: '',
  targetType: '',
  dateRange: []
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const stats = reactive({
  todayCollects: 567,
  totalCollects: 23456,
  collectUsers: 1234,
  avgCollectRate: 3.8
})

const tableData = ref([])
const detailVisible = ref(false)

const detailData = reactive({
  id: null,
  userId: null,
  userName: '',
  userAvatar: '',
  targetType: '',
  targetId: null,
  targetTitle: '',
  targetAuthor: '',
  folderName: '',
  collectTime: '',
  ip: '',
  device: '',
  status: 1
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
    const res = await interactionApi.getCollectList(params)
    tableData.value = res.data.list || []
    pagination.total = res.data.total || 0
  } catch (error) {
    ElMessage.error('加载数据失败')
    console.error('加载收藏列表失败:', error)
  }
}

const getTargetTypeTag = (type) => {
  const map = {
    post: '',
    content: 'success'
  }
  return map[type]
}

const getTargetTypeText = (type) => {
  const map = {
    post: '帖子',
    content: '文章'
  }
  return map[type]
}

const handleQuery = () => {
  Logger.query('收藏管理', '查询收藏记录', queryForm)
  loadData()
}

const handleReset = () => {
  queryForm.userName = ''
  queryForm.targetType = ''
  queryForm.dateRange = []
  handleQuery()
}

const handleExport = () => {
  Logger.query('收藏管理', '导出收藏数据', queryForm)
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
.collect-container {
  padding: 20px;
}

.query-form {
  margin-bottom: 20px;
}
</style>
