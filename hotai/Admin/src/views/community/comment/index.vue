<template>
  <div class="comment-container">
    <el-card>
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="评论内容">
          <el-input v-model="queryForm.content" placeholder="请输入评论内容" clearable />
        </el-form-item>
        <el-form-item label="评论人">
          <el-input v-model="queryForm.author" placeholder="请输入评论人" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
            <el-option label="正常" value="1" />
            <el-option label="已删除" value="0" />
            <el-option label="待审核" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div style="margin-bottom: 15px;">
        <el-button type="danger" :disabled="selectedIds.length === 0" @click="handleBatchDelete">
          批量删除 ({{ selectedIds.length }})
        </el-button>
        <el-button type="success" :disabled="selectedIds.length === 0" @click="handleBatchApprove">
          批量通过 ({{ selectedIds.length }})
        </el-button>
      </div>

      <el-table :data="tableData" style="width: 100%" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="content" label="评论内容" width="300" show-overflow-tooltip />
        <el-table-column prop="author" label="评论人" width="120" />
        <el-table-column prop="postTitle" label="所属帖子" width="200" show-overflow-tooltip />
        <el-table-column prop="likes" label="点赞数" width="100" />
        <el-table-column prop="replyCount" label="回复数" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="评论时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleViewDetail(row)">查看</el-button>
            <el-button v-if="row.status === 2" type="success" size="small" @click="handleApprove(row)">
              通过
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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

    <!-- 评论详情对话框 -->
    <el-dialog v-model="detailVisible" title="评论详情" width="900px" :destroy-on-close="true">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="评论人">{{ detailData.author }}</el-descriptions-item>
        <el-descriptions-item label="评论时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="所属帖子" :span="2">{{ detailData.postTitle }}</el-descriptions-item>
        <el-descriptions-item label="点赞数">{{ detailData.likes }}</el-descriptions-item>
        <el-descriptions-item label="回复数">{{ detailData.replyCount }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(detailData.status)">
            {{ getStatusText(detailData.status) }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
      <el-divider />
      <div style="margin-bottom: 15px;">
        <strong>评论内容：</strong>
        <div style="padding: 10px; background: #f5f5f5; border-radius: 4px; margin-top: 10px;">
          {{ detailData.content }}
        </div>
      </div>
      <div v-if="detailData.replies && detailData.replies.length > 0">
        <el-divider />
        <strong>回复列表 ({{ detailData.replies.length }})：</strong>
        <div style="margin-top: 10px; max-height: 300px; overflow-y: auto;">
          <div 
            v-for="reply in detailData.replies" 
            :key="reply.id"
            style="padding: 10px; background: #fafafa; border-radius: 4px; margin-bottom: 10px;"
          >
            <div style="display: flex; justify-content: space-between; margin-bottom: 5px;">
              <span style="font-weight: bold; color: #409eff;">{{ reply.author }}</span>
              <span style="color: #909399; font-size: 12px;">{{ reply.createTime }}</span>
            </div>
            <div>{{ reply.content }}</div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button v-if="detailData.status === 2" type="success" @click="handleApprove(detailData)">
          通过审核
        </el-button>
        <el-button type="danger" @click="handleDelete(detailData)">删除评论</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import commentApi from '@/api/comment'
import Logger from '@/utils/logger'

const queryForm = reactive({
  content: '',
  author: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const tableData = ref([])
const selectedIds = ref([])
const detailVisible = ref(false)

const detailData = reactive({
  id: null,
  content: '',
  author: '',
  postTitle: '',
  likes: 0,
  replyCount: 0,
  status: 1,
  createTime: '',
  replies: []
})

const loadData = async () => {
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...queryForm
    }
    const res = await commentApi.getCommentList(params)
    tableData.value = res.data.list || res.data
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error('加载评论列表失败:', error)
    ElMessage.error('加载数据失败')
  }
}

const getStatusType = (status) => {
  const map = {
    0: 'danger',
    1: 'success',
    2: 'warning'
  }
  return map[status]
}

const getStatusText = (status) => {
  const map = {
    0: '已删除',
    1: '正常',
    2: '待审核'
  }
  return map[status]
}

const handleQuery = () => {
  Logger.query('评论管理', '查询评论列表', queryForm)
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  queryForm.content = ''
  queryForm.author = ''
  queryForm.status = ''
  handleQuery()
}

const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

const handleViewDetail = (row) => {
  Object.assign(detailData, {
    ...row,
    replies: row.replies || []
  })
  detailVisible.value = true
}

const handleApprove = (row) => {
  ElMessageBox.confirm(`确定要通过该评论吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'success'
  }).then(async () => {
    try {
      await commentApi.changeStatus({ id: row.id, status: 1 })
      Logger.update('评论管理', `通过评论审核`, { id: row.id })
      ElMessage.success('审核通过')
      detailVisible.value = false
      handleQuery()
    } catch (error) {
      console.error('审核失败:', error)
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除该评论吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await commentApi.deleteComment(row.id)
      Logger.delete('评论管理', `删除评论`, { id: row.id })
      ElMessage.success('删除成功')
      detailVisible.value = false
      handleQuery()
    } catch (error) {
      console.error('删除评论失败:', error)
    }
  })
}

const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要批量删除 ${selectedIds.value.length} 条评论吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await commentApi.batchDelete({ ids: selectedIds.value })
      Logger.delete('评论管理', `批量删除评论`, { ids: selectedIds.value })
      ElMessage.success(`已批量删除 ${selectedIds.value.length} 条评论`)
      selectedIds.value = []
      handleQuery()
    } catch (error) {
      console.error('批量删除失败:', error)
    }
  })
}

const handleBatchApprove = () => {
  ElMessageBox.confirm(`确定要批量通过 ${selectedIds.value.length} 条评论吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'success'
  }).then(async () => {
    try {
      await commentApi.batchApprove({ ids: selectedIds.value })
      Logger.update('评论管理', `批量通过评论审核`, { ids: selectedIds.value })
      ElMessage.success(`已批量通过 ${selectedIds.value.length} 条评论`)
      selectedIds.value = []
      handleQuery()
    } catch (error) {
      console.error('批量审核失败:', error)
    }
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.comment-container {
  padding: 20px;
}

.query-form {
  margin-bottom: 20px;
}
</style>
