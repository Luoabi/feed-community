<template>
  <div class="audit-container">
    <el-card>
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="标题">
          <el-input v-model="queryForm.title" placeholder="请输入标题" clearable />
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="queryForm.author" placeholder="请输入作者" clearable />
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
            <el-option label="待审核" value="pending" />
            <el-option label="已发布" value="published" />
            <el-option label="已拒绝" value="rejected" />
          </el-select>
        </el-form-item>
        <el-form-item label="违规类型">
          <el-select v-model="queryForm.violationType" placeholder="请选择类型" clearable>
            <el-option label="色情低俗" value="porn" />
            <el-option label="暴力血腥" value="violence" />
            <el-option label="政治敏感" value="politics" />
            <el-option label="虚假信息" value="fake" />
            <el-option label="广告营销" value="ad" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div style="margin-bottom: 15px;">
        <el-button type="success" :disabled="selectedIds.length === 0" @click="handleBatchApprove">
          批量通过 ({{ selectedIds.length }})
        </el-button>
        <el-button type="danger" :disabled="selectedIds.length === 0" @click="handleBatchReject">
          批量拒绝 ({{ selectedIds.length }})
        </el-button>
      </div>

      <el-table :data="tableData" style="width: 100%" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" width="200" show-overflow-tooltip />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="contentType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getContentTypeTag(row.contentType)">
              {{ getContentTypeText(row.contentType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="审核状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="violationType" label="违规类型" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.violationType" type="danger" size="small">
              {{ getViolationText(row.violationType) }}
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="submitTime" label="提交时间" width="180" />
        <el-table-column prop="auditTime" label="审核时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleViewDetail(row)">查看详情</el-button>
            <el-button v-if="row.status === 'pending'" type="success" size="small" @click="handleApprove(row)">
              通过
            </el-button>
            <el-button v-if="row.status === 'pending'" type="danger" size="small" @click="handleReject(row)">
              拒绝
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
    </el-card>

    <!-- 内容详情对话框 -->
    <el-dialog v-model="detailVisible" title="内容详情" width="900px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="标题">{{ detailData.title }}</el-descriptions-item>
        <el-descriptions-item label="作者">{{ detailData.author }}</el-descriptions-item>
        <el-descriptions-item label="内容类型">{{ getContentTypeText(detailData.contentType) }}</el-descriptions-item>
        <el-descriptions-item label="分类">{{ detailData.category }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ detailData.submitTime }}</el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag :type="getStatusType(detailData.status)">
            {{ getStatusText(detailData.status) }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
      <el-divider />
      <div style="margin-bottom: 15px;">
        <strong>内容摘要：</strong>
        <p>{{ detailData.summary }}</p>
      </div>
      <div style="margin-bottom: 15px;">
        <strong>内容正文：</strong>
        <div style="max-height: 300px; overflow-y: auto; padding: 10px; background: #f5f5f5; border-radius: 4px;">
          {{ detailData.content }}
        </div>
      </div>
      <div v-if="detailData.coverImage" style="margin-bottom: 15px;">
        <strong>封面图：</strong>
        <el-image :src="detailData.coverImage" style="width: 200px; margin-top: 10px;" fit="cover" />
      </div>
      <div v-if="detailData.rejectReason" style="margin-top: 15px;">
        <el-alert type="error" :closable="false">
          <template #title>
            <strong>拒绝原因：</strong>{{ detailData.rejectReason }}
          </template>
        </el-alert>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button v-if="detailData.status === 'pending'" type="success" @click="handleApprove(detailData)">
          通过审核
        </el-button>
        <el-button v-if="detailData.status === 'pending'" type="danger" @click="handleReject(detailData)">
          拒绝审核
        </el-button>
      </template>
    </el-dialog>

    <!-- 拒绝原因对话框 -->
    <el-dialog v-model="rejectVisible" title="拒绝审核" width="600px">
      <el-form ref="rejectFormRef" :model="rejectForm" :rules="rejectRules" label-width="100px">
        <el-form-item label="违规类型" prop="violationType">
          <el-select v-model="rejectForm.violationType" placeholder="请选择违规类型" style="width: 100%">
            <el-option label="色情低俗" value="porn" />
            <el-option label="暴力血腥" value="violence" />
            <el-option label="政治敏感" value="politics" />
            <el-option label="虚假信息" value="fake" />
            <el-option label="广告营销" value="ad" />
            <el-option label="其他违规" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="拒绝原因" prop="reason">
          <el-input v-model="rejectForm.reason" type="textarea" :rows="5" placeholder="请输入拒绝原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="danger" @click="handleRejectSubmit">确定拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import auditApi from '@/api/audit'
import Logger from '@/utils/logger'

const queryForm = reactive({
  title: '',
  author: '',
  status: 'pending',
  violationType: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const tableData = ref([])
const selectedIds = ref([])
const detailVisible = ref(false)
const rejectVisible = ref(false)
const rejectFormRef = ref(null)
const currentRow = ref(null)

const detailData = reactive({
  id: null,
  title: '',
  author: '',
  category: '',
  contentType: '',
  summary: '',
  content: '',
  coverImage: '',
  status: '',
  submitTime: '',
  auditTime: '',
  rejectReason: '',
  violationType: ''
})

const rejectForm = reactive({
  violationType: '',
  reason: ''
})

const rejectRules = {
  violationType: [{ required: true, message: '请选择违规类型', trigger: 'change' }],
  reason: [{ required: true, message: '请输入拒绝原因', trigger: 'blur' }]
}

const loadData = async () => {
  try {
    const params = {
      pageNum: pagination.page,
      pageSize: pagination.size,
      title: queryForm.title,
      status: queryForm.status || 'pending',  // 默认查询待审核
      contentSource: 'user'  // 只查询用户发布的内容
    }
    
    // 如果有作者筛选，添加到参数中
    if (queryForm.author) {
      params.authorName = queryForm.author
    }
    
    const res = await auditApi.getAuditList(params)
    
    // 处理返回的数据
    const list = res.data.list || res.data.records || []
    tableData.value = list.map(item => ({
      id: item.id,
      title: item.title,
      author: item.authorName,
      contentType: item.contentType,
      status: item.status,
      category: item.categoryName,
      summary: item.summary,
      content: item.content,
      coverImage: item.coverImage,
      submitTime: item.createTime,
      auditTime: item.updateTime,
      violationType: item.violationType || '',
      rejectReason: item.rejectReason || ''
    }))
    
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error('加载审核列表失败:', error)
    ElMessage.error('加载数据失败')
  }
}

const getStatusType = (status) => {
  const map = {
    pending: 'warning',
    published: 'success',
    rejected: 'danger',
    draft: 'info'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    pending: '待审核',
    published: '已发布',
    rejected: '已拒绝',
    draft: '草稿'
  }
  return map[status] || status
}

const getContentTypeTag = (type) => {
  const map = {
    article: '',
    video: 'success',
    image: 'warning'
  }
  return map[type]
}

const getContentTypeText = (type) => {
  const map = {
    article: '文章',
    post: '帖子',
    text: '文本',
    video: '视频',
    image: '图片',
    link: '链接'
  }
  return map[type] || type
}

const getViolationText = (type) => {
  const map = {
    porn: '色情低俗',
    violence: '暴力血腥',
    politics: '政治敏感',
    fake: '虚假信息',
    ad: '广告营销',
    other: '其他违规'
  }
  return map[type] || ''
}

const handleQuery = () => {
  Logger.query('内容审核', '查询审核列表', queryForm)
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  queryForm.title = ''
  queryForm.author = ''
  queryForm.status = 'pending'
  queryForm.violationType = ''
  handleQuery()
}

const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

const handleViewDetail = (row) => {
  Object.assign(detailData, row)
  detailVisible.value = true
}

const handleApprove = (row) => {
  ElMessageBox.confirm(`确定要通过审核 "${row.title}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'success'
  }).then(async () => {
    try {
      await auditApi.approveContent({ id: row.id })
      Logger.update('内容审核', `通过审核: ${row.title}`, { id: row.id })
      ElMessage.success('审核通过')
      detailVisible.value = false
      handleQuery()
    } catch (error) {
      console.error('审核失败:', error)
    }
  })
}

const handleReject = (row) => {
  currentRow.value = row
  rejectForm.violationType = ''
  rejectForm.reason = ''
  rejectVisible.value = true
}

const handleRejectSubmit = async () => {
  rejectFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await auditApi.rejectContent({ 
          id: currentRow.value.id, 
          violationType: rejectForm.violationType,
          reason: rejectForm.reason
        })
        Logger.update('内容审核', `拒绝审核: ${currentRow.value.title}`, { 
          id: currentRow.value.id, 
          violationType: rejectForm.violationType,
          reason: rejectForm.reason
        })
        ElMessage.success('已拒绝')
        rejectVisible.value = false
        detailVisible.value = false
        handleQuery()
      } catch (error) {
        console.error('拒绝失败:', error)
      }
    }
  })
}

const handleBatchApprove = () => {
  ElMessageBox.confirm(`确定要批量通过 ${selectedIds.value.length} 条内容吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'success'
  }).then(async () => {
    try {
      await auditApi.batchApprove({ ids: selectedIds.value })
      Logger.update('内容审核', `批量通过审核`, { ids: selectedIds.value })
      ElMessage.success(`已批量通过 ${selectedIds.value.length} 条内容`)
      selectedIds.value = []
      handleQuery()
    } catch (error) {
      console.error('批量审核失败:', error)
    }
  })
}

const handleBatchReject = () => {
  currentRow.value = { ids: selectedIds.value }
  rejectForm.violationType = ''
  rejectForm.reason = ''
  rejectVisible.value = true
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.audit-container {
  padding: 20px;
}

.query-form {
  margin-bottom: 20px;
}
</style>
