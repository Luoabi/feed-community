<template>
  <div class="post-container">
    <el-card>
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="标题">
          <el-input v-model="queryForm.title" placeholder="请输入标题" clearable />
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="queryForm.author" placeholder="请输入作者" clearable />
        </el-form-item>
        <el-form-item label="帖子类型">
          <el-select v-model="queryForm.postType" placeholder="请选择类型" clearable>
            <el-option label="文本" value="text" />
            <el-option label="图片" value="image" />
            <el-option label="视频" value="video" />
            <el-option label="链接" value="link" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
            <el-option label="正常" value="1" />
            <el-option label="已删除" value="0" />
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
      </div>

      <el-table :data="tableData" style="width: 100%" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" width="200" show-overflow-tooltip />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="postType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getPostTypeTag(row.postType)">
              {{ getPostTypeText(row.postType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="views" label="浏览" width="80" />
        <el-table-column prop="likes" label="点赞" width="80" />
        <el-table-column prop="comments" label="评论" width="80" />
        <el-table-column label="标签" width="150">
          <template #default="{ row }">
            <el-tag v-if="row.isTop" type="danger" size="small" style="margin-right: 5px;">置顶</el-tag>
            <el-tag v-if="row.isEssence" type="warning" size="small" style="margin-right: 5px;">精华</el-tag>
            <el-tag v-if="row.isHot" type="success" size="small">热门</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="180" />
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleViewDetail(row)">查看</el-button>
            <el-button type="warning" size="small" @click="handleToggleTop(row)">
              {{ row.isTop ? '取消置顶' : '置顶' }}
            </el-button>
            <el-button type="success" size="small" @click="handleToggleEssence(row)">
              {{ row.isEssence ? '取消精华' : '精华' }}
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

    <!-- 帖子详情对话框 -->
    <el-dialog v-model="detailVisible" title="帖子详情" width="900px" :destroy-on-close="true">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="标题">{{ detailData.title }}</el-descriptions-item>
        <el-descriptions-item label="作者">{{ detailData.author }}</el-descriptions-item>
        <el-descriptions-item label="帖子类型">{{ getPostTypeText(detailData.postType) }}</el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="浏览量">{{ detailData.views }}</el-descriptions-item>
        <el-descriptions-item label="点赞数">{{ detailData.likes }}</el-descriptions-item>
        <el-descriptions-item label="评论数">{{ detailData.comments }}</el-descriptions-item>
        <el-descriptions-item label="分享数">{{ detailData.shares }}</el-descriptions-item>
      </el-descriptions>
      <el-divider />
      <div style="margin-bottom: 15px;">
        <strong>帖子内容：</strong>
        <div style="max-height: 300px; overflow-y: auto; padding: 10px; background: #f5f5f5; border-radius: 4px; margin-top: 10px;">
          {{ detailData.content }}
        </div>
      </div>
      <div v-if="detailData.images && detailData.images.length > 0" style="margin-bottom: 15px;">
        <strong>图片：</strong>
        <div style="display: flex; gap: 10px; margin-top: 10px; flex-wrap: wrap;">
          <el-image 
            v-for="(img, index) in detailData.images" 
            :key="index"
            :src="img" 
            style="width: 150px; height: 150px;" 
            fit="cover"
            :preview-src-list="detailData.images"
            :initial-index="index"
            :z-index="3000"
          />
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import postApi from '@/api/post'
import Logger from '@/utils/logger'

const queryForm = reactive({
  title: '',
  author: '',
  postType: '',
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
  title: '',
  content: '',
  author: '',
  postType: '',
  images: [],
  videoUrl: '',
  views: 0,
  likes: 0,
  comments: 0,
  shares: 0,
  isTop: false,
  isEssence: false,
  isHot: false,
  createTime: ''
})

const loadData = async () => {
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...queryForm
    }
    const res = await postApi.getPostList(params)
    tableData.value = res.data.list || res.data
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error('加载帖子列表失败:', error)
    ElMessage.error('加载数据失败')
  }
}

const getPostTypeTag = (type) => {
  const map = {
    text: '',
    image: 'success',
    video: 'warning',
    link: 'info'
  }
  return map[type]
}

const getPostTypeText = (type) => {
  const map = {
    text: '文本',
    image: '图片',
    video: '视频',
    link: '链接'
  }
  return map[type]
}

const handleQuery = () => {
  Logger.query('帖子管理', '查询帖子列表', queryForm)
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  queryForm.title = ''
  queryForm.author = ''
  queryForm.postType = ''
  queryForm.status = ''
  handleQuery()
}

const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

const handleViewDetail = (row) => {
  Object.assign(detailData, {
    ...row,
    images: row.images || []
  })
  detailVisible.value = true
}

const handleToggleTop = async (row) => {
  try {
    const newTopStatus = !row.isTop
    await postApi.toggleTop({ id: row.id, isTop: newTopStatus })
    row.isTop = newTopStatus
    Logger.update('帖子管理', `${row.isTop ? '置顶' : '取消置顶'}帖子: ${row.title}`, { id: row.id, isTop: row.isTop })
    ElMessage.success(row.isTop ? '置顶成功' : '取消置顶成功')
  } catch (error) {
    console.error('操作失败:', error)
  }
}

const handleToggleEssence = async (row) => {
  try {
    const newEssenceStatus = !row.isEssence
    await postApi.toggleEssence({ id: row.id, isEssence: newEssenceStatus })
    row.isEssence = newEssenceStatus
    Logger.update('帖子管理', `${row.isEssence ? '设为精华' : '取消精华'}帖子: ${row.title}`, { id: row.id, isEssence: row.isEssence })
    ElMessage.success(row.isEssence ? '设为精华成功' : '取消精华成功')
  } catch (error) {
    console.error('操作失败:', error)
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除帖子 "${row.title}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await postApi.deletePost(row.id)
      Logger.delete('帖子管理', `删除帖子: ${row.title}`, { id: row.id })
      ElMessage.success('删除成功')
      handleQuery()
    } catch (error) {
      console.error('删除帖子失败:', error)
    }
  })
}

const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要批量删除 ${selectedIds.value.length} 条帖子吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await postApi.batchDelete({ ids: selectedIds.value })
      Logger.delete('帖子管理', `批量删除帖子`, { ids: selectedIds.value })
      ElMessage.success(`已批量删除 ${selectedIds.value.length} 条帖子`)
      selectedIds.value = []
      handleQuery()
    } catch (error) {
      console.error('批量删除失败:', error)
    }
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.post-container {
  padding: 20px;
}

.query-form {
  margin-bottom: 20px;
}
</style>
