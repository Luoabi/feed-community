<template>
  <div class="content-list-container">
    <el-card>
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="标题">
          <el-input v-model="queryForm.title" placeholder="请输入标题" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="queryForm.category" placeholder="请选择分类" clearable>
            <el-option label="技术" value="tech" />
            <el-option label="生活" value="life" />
            <el-option label="娱乐" value="entertainment" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
            <el-option label="已发布" value="published" />
            <el-option label="待审核" value="pending" />
            <el-option label="已拒绝" value="rejected" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="handleReset">重置</el-button>
          <el-button type="success" icon="Plus" @click="handleAdd">新增内容</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" width="200" show-overflow-tooltip />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="views" label="浏览量" width="100" />
        <el-table-column prop="likes" label="点赞数" width="100" />
        <el-table-column label="标签" width="150">
          <template #default="{ row }">
            <el-tag v-if="row.isTop" type="danger" size="small" style="margin-right: 5px;">置顶</el-tag>
            <el-tag v-if="row.isHot" type="warning" size="small" style="margin-right: 5px;">热门</el-tag>
            <el-tag v-if="row.isRecommend" type="success" size="small">推荐</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" size="small" @click="handleToggleTop(row)">
              {{ row.isTop ? '取消置顶' : '置顶' }}
            </el-button>
            <el-button type="success" size="small" @click="handleToggleRecommend(row)">
              {{ row.isRecommend ? '取消推荐' : '推荐' }}
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

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="formData.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option label="技术" :value="1" />
            <el-option label="生活" :value="2" />
            <el-option label="娱乐" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容类型" prop="contentType">
          <el-radio-group v-model="formData.contentType">
            <el-radio label="article">文章</el-radio>
            <el-radio label="video">视频</el-radio>
            <el-radio label="image">图片</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="封面图" prop="coverImage">
          <el-input v-model="formData.coverImage" placeholder="请输入封面图URL" />
        </el-form-item>
        <el-form-item label="摘要" prop="summary">
          <el-input v-model="formData.summary" type="textarea" :rows="3" placeholder="请输入内容摘要" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="formData.content" type="textarea" :rows="6" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="标签" prop="tags">
          <el-input v-model="formData.tags" placeholder="多个标签用逗号分隔" />
        </el-form-item>
        <el-form-item label="设置">
          <el-checkbox v-model="formData.isTop">置顶</el-checkbox>
          <el-checkbox v-model="formData.isHot">热门</el-checkbox>
          <el-checkbox v-model="formData.isRecommend">推荐</el-checkbox>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio label="published">已发布</el-radio>
            <el-radio label="pending">待审核</el-radio>
            <el-radio label="draft">草稿</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import contentApi from '@/api/content'
import Logger from '@/utils/logger'

const queryForm = reactive({
  title: '',
  category: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const formData = reactive({
  id: null,
  title: '',
  categoryId: null,
  categoryName: '',
  contentType: 'article',
  coverImage: '',
  summary: '',
  content: '',
  tags: '',
  isTop: false,
  isHot: false,
  isRecommend: false,
  status: 'published'
})

const formRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const dialogTitle = computed(() => isEdit.value ? '编辑内容' : '新增内容')

const loadData = async () => {
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...queryForm
    }
    const res = await contentApi.getContentList(params)
    tableData.value = res.data.list || res.data
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error('加载内容列表失败:', error)
    ElMessage.error('加载数据失败')
  }
}

const getStatusType = (status) => {
  const map = {
    published: 'success',
    pending: 'warning',
    rejected: 'danger',
    draft: 'info'
  }
  return map[status]
}

const getStatusText = (status) => {
  const map = {
    published: '已发布',
    pending: '待审核',
    rejected: '已拒绝',
    draft: '草稿'
  }
  return map[status]
}

const handleQuery = () => {
  Logger.query('内容管理', '查询内容列表', queryForm)
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  queryForm.title = ''
  queryForm.category = ''
  queryForm.status = ''
  handleQuery()
}

const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
  Object.assign(formData, {
    id: null,
    title: '',
    categoryId: null,
    categoryName: '',
    contentType: 'article',
    coverImage: '',
    summary: '',
    content: '',
    tags: '',
    isTop: false,
    isHot: false,
    isRecommend: false,
    status: 'published'
  })
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogVisible.value = true
  Object.assign(formData, row)
}

const handleSubmit = async () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 根据 categoryId 设置 categoryName
        const categoryMap = {
          1: '技术',
          2: '生活',
          3: '娱乐'
        }
        
        // 准备提交数据
        const submitData = {
          ...formData,
          categoryName: categoryMap[formData.categoryId] || '',
          // 转换布尔值为数字
          isTop: formData.isTop ? 1 : 0,
          isHot: formData.isHot ? 1 : 0,
          isRecommend: formData.isRecommend ? 1 : 0,
          // 设置默认值
          contentSource: 'admin',
          views: 0,
          likes: 0,
          comments: 0,
          shares: 0,
          collects: 0,
          isEssence: 0,
          deleted: 0
        }
        
        if (isEdit.value) {
          await contentApi.updateContent(submitData)
          Logger.update('内容管理', `编辑内容: ${submitData.title}`, submitData)
          ElMessage.success('编辑成功')
        } else {
          await contentApi.addContent(submitData)
          Logger.create('内容管理', `新增内容: ${submitData.title}`, submitData)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        handleQuery()
      } catch (error) {
        console.error('保存内容失败:', error)
        ElMessage.error('保存失败：' + (error.response?.data?.message || error.message || '未知错误'))
      }
    }
  })
}

const handleToggleTop = async (row) => {
  try {
    const newTopStatus = !row.isTop
    await contentApi.toggleTop({ id: row.id, isTop: newTopStatus })
    row.isTop = newTopStatus
    Logger.update('内容管理', `${row.isTop ? '置顶' : '取消置顶'}内容: ${row.title}`, { id: row.id, isTop: row.isTop })
    ElMessage.success(row.isTop ? '置顶成功' : '取消置顶成功')
  } catch (error) {
    console.error('操作失败:', error)
  }
}

const handleToggleRecommend = async (row) => {
  try {
    const newRecommendStatus = !row.isRecommend
    await contentApi.toggleRecommend({ id: row.id, isRecommend: newRecommendStatus })
    row.isRecommend = newRecommendStatus
    Logger.update('内容管理', `${row.isRecommend ? '推荐' : '取消推荐'}内容: ${row.title}`, { id: row.id, isRecommend: row.isRecommend })
    ElMessage.success(row.isRecommend ? '推荐成功' : '取消推荐成功')
  } catch (error) {
    console.error('操作失败:', error)
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除内容 "${row.title}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await contentApi.deleteContent(row.id)
      Logger.delete('内容管理', `删除内容: ${row.title}`, { id: row.id })
      ElMessage.success('删除成功')
      handleQuery()
    } catch (error) {
      console.error('删除内容失败:', error)
    }
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.content-list-container {
  padding: 20px;
}

.query-form {
  margin-bottom: 20px;
}
</style>
