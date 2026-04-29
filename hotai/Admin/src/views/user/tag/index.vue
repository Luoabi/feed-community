<template>
  <div class="tag-container">
    <el-row :gutter="20">
      <!-- 标签管理 -->
      <el-col :span="10">
        <el-card header="标签管理">
          <el-button type="primary" icon="Plus" @click="handleAddTag" style="margin-bottom: 15px;">
            新增标签
          </el-button>

          <el-table :data="tagList" style="width: 100%">
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="tagName" label="标签名称" />
            <el-table-column prop="tagType" label="类型" width="100">
              <template #default="{ row }">
                <el-tag :type="getTagTypeTag(row.tagType)" size="small">
                  {{ getTagTypeText(row.tagType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="userCount" label="用户数" width="80" />
            <el-table-column prop="sort" label="排序" width="60" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-switch
                  v-model="row.status"
                  :active-value="1"
                  :inactive-value="0"
                  @change="handleStatusChange(row)"
                />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="handleEditTag(row)">编辑</el-button>
                <el-button type="danger" size="small" @click="handleDeleteTag(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 用户标签分配 -->
      <el-col :span="14">
        <el-card>
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span>用户标签分配</span>
              <el-button type="primary" size="small" @click="handleBatchAssign">批量分配</el-button>
            </div>
          </template>

          <el-form :inline="true" :model="queryForm" class="query-form">
            <el-form-item label="用户名">
              <el-input v-model="queryForm.userName" placeholder="请输入用户名" clearable />
            </el-form-item>
            <el-form-item label="标签">
              <el-select v-model="queryForm.tagId" placeholder="请选择标签" clearable>
                <el-option
                  v-for="tag in tagList"
                  :key="tag.id"
                  :label="tag.tagName"
                  :value="tag.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
              <el-button icon="Refresh" @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>

          <el-table :data="userTagList" style="width: 100%" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55" />
            <el-table-column prop="userName" label="用户" width="120">
              <template #default="{ row }">
                <div style="display: flex; align-items: center;">
                  <el-avatar :src="row.userAvatar" :size="30" style="margin-right: 8px;" />
                  <span>{{ row.userName }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="tags" label="用户标签">
              <template #default="{ row }">
                <el-tag
                  v-for="tag in row.tags"
                  :key="tag.id"
                  :type="getTagTypeTag(tag.tagType)"
                  size="small"
                  style="margin-right: 5px;"
                  closable
                  @close="handleRemoveUserTag(row, tag)"
                >
                  {{ tag.tagName }}
                </el-tag>
                <el-button
                  type="primary"
                  size="small"
                  icon="Plus"
                  circle
                  @click="handleAssignTag(row)"
                />
              </template>
            </el-table-column>
            <el-table-column prop="updateTime" label="更新时间" width="180" />
          </el-table>

          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.size"
            :total="pagination.total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleQuery"
            @current-change="handleQuery"
            style="margin-top: 20px; justify-content: flex-end;"
          />
        </el-card>
      </el-col>
    </el-row>

    <!-- 新增/编辑标签对话框 -->
    <el-dialog v-model="tagDialogVisible" :title="tagDialogTitle" width="600px">
      <el-form ref="tagFormRef" :model="tagForm" :rules="tagRules" label-width="100px">
        <el-form-item label="标签名称" prop="tagName">
          <el-input v-model="tagForm.tagName" placeholder="请输入标签名称" />
        </el-form-item>
        <el-form-item label="标签类型" prop="tagType">
          <el-select v-model="tagForm.tagType" placeholder="请选择类型" style="width: 100%">
            <el-option label="兴趣爱好" value="interest" />
            <el-option label="内容偏好" value="content" />
            <el-option label="行为特征" value="behavior" />
            <el-option label="用户属性" value="attribute" />
          </el-select>
        </el-form-item>
        <el-form-item label="标签描述" prop="description">
          <el-input v-model="tagForm.description" type="textarea" :rows="3" placeholder="请输入标签描述" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="tagForm.sort" :min="0" :max="999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="tagForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="tagDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleTagSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配标签对话框 -->
    <el-dialog v-model="assignDialogVisible" title="分配标签" width="500px">
      <el-form label-width="80px">
        <el-form-item label="选择标签">
          <el-select v-model="assignTags" multiple placeholder="请选择标签" style="width: 100%">
            <el-option
              v-for="tag in tagList.filter(t => t.status === 1)"
              :key="tag.id"
              :label="tag.tagName"
              :value="tag.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssignSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import tagApi from '@/api/tag'
import Logger from '@/utils/logger'

const queryForm = reactive({
  userName: '',
  tagId: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const tagList = ref([])
const userTagList = ref([])
const selectedUsers = ref([])
const tagDialogVisible = ref(false)
const assignDialogVisible = ref(false)
const isEditTag = ref(false)
const tagFormRef = ref(null)
const currentUser = ref(null)
const assignTags = ref([])

const tagForm = reactive({
  id: null,
  tagName: '',
  tagType: '',
  description: '',
  sort: 0,
  status: 1
})

const tagRules = {
  tagName: [{ required: true, message: '请输入标签名称', trigger: 'blur' }],
  tagType: [{ required: true, message: '请选择标签类型', trigger: 'change' }]
}

const tagDialogTitle = computed(() => isEditTag.value ? '编辑标签' : '新增标签')

const loadTagList = async () => {
  try {
    const res = await tagApi.getTagList()
    tagList.value = res.data || []
  } catch (error) {
    ElMessage.error('加载标签列表失败')
    console.error('加载标签列表失败:', error)
  }
}

const loadUserTagList = async () => {
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...queryForm
    }
    const res = await tagApi.getUserTagList(params)
    userTagList.value = res.data.list || []
    pagination.total = res.data.total || 0
  } catch (error) {
    ElMessage.error('加载用户标签列表失败')
    console.error('加载用户标签列表失败:', error)
  }
}

const getTagTypeTag = (type) => {
  const map = {
    interest: 'success',
    content: 'warning',
    behavior: '',
    attribute: 'info'
  }
  return map[type]
}

const getTagTypeText = (type) => {
  const map = {
    interest: '兴趣爱好',
    content: '内容偏好',
    behavior: '行为特征',
    attribute: '用户属性'
  }
  return map[type]
}

const handleAddTag = () => {
  isEditTag.value = false
  tagDialogVisible.value = true
  Object.assign(tagForm, {
    id: null,
    tagName: '',
    tagType: '',
    description: '',
    sort: 0,
    status: 1
  })
}

const handleEditTag = (row) => {
  isEditTag.value = true
  tagDialogVisible.value = true
  Object.assign(tagForm, row)
}

const handleTagSubmit = () => {
  tagFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEditTag.value) {
          await tagApi.updateTag(tagForm)
          Logger.update('标签管理', `编辑标签: ${tagForm.tagName}`, tagForm)
          ElMessage.success('编辑成功')
        } else {
          await tagApi.addTag(tagForm)
          Logger.create('标签管理', `新增标签: ${tagForm.tagName}`, tagForm)
          ElMessage.success('新增成功')
        }
        tagDialogVisible.value = false
        loadTagList()
      } catch (error) {
        ElMessage.error(isEditTag.value ? '编辑失败' : '新增失败')
        console.error('标签操作失败:', error)
      }
    }
  })
}

const handleDeleteTag = (row) => {
  if (row.userCount > 0) {
    ElMessage.warning('该标签下还有用户，无法删除')
    return
  }
  ElMessageBox.confirm(`确定要删除标签 "${row.tagName}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await tagApi.deleteTag(row.id)
      Logger.delete('标签管理', `删除标签: ${row.tagName}`, { id: row.id })
      ElMessage.success('删除成功')
      loadTagList()
    } catch (error) {
      ElMessage.error('删除失败')
      console.error('删除标签失败:', error)
    }
  })
}

const handleStatusChange = async (row) => {
  try {
    await tagApi.updateTag({ id: row.id, status: row.status })
    Logger.update('标签管理', `修改标签状态: ${row.tagName}`, { id: row.id, status: row.status })
    ElMessage.success('状态修改成功')
  } catch (error) {
    ElMessage.error('状态修改失败')
    console.error('修改标签状态失败:', error)
    row.status = row.status === 1 ? 0 : 1
  }
}

const handleQuery = () => {
  Logger.query('用户标签', '查询用户标签', queryForm)
  loadUserTagList()
}

const handleReset = () => {
  queryForm.userName = ''
  queryForm.tagId = ''
  handleQuery()
}

const handleSelectionChange = (selection) => {
  selectedUsers.value = selection
}

const handleAssignTag = (row) => {
  currentUser.value = row
  assignTags.value = row.tags.map(t => t.id)
  assignDialogVisible.value = true
}

const handleBatchAssign = () => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请先选择用户')
    return
  }
  currentUser.value = null
  assignTags.value = []
  assignDialogVisible.value = true
}

const handleAssignSubmit = async () => {
  if (assignTags.value.length === 0) {
    ElMessage.warning('请选择标签')
    return
  }
  try {
    if (currentUser.value) {
      await tagApi.assignTag({
        userId: currentUser.value.userId,
        tagIds: assignTags.value
      })
      Logger.update('用户标签', `为用户 ${currentUser.value.userName} 分配标签`, {
        userId: currentUser.value.userId,
        tagIds: assignTags.value
      })
      ElMessage.success('分配成功')
    } else {
      await tagApi.batchAssign({
        userIds: selectedUsers.value.map(u => u.userId),
        tagIds: assignTags.value
      })
      Logger.update('用户标签', `批量分配标签`, {
        userIds: selectedUsers.value.map(u => u.userId),
        tagIds: assignTags.value
      })
      ElMessage.success(`已为 ${selectedUsers.value.length} 个用户分配标签`)
    }
    assignDialogVisible.value = false
    loadUserTagList()
  } catch (error) {
    ElMessage.error('分配失败')
    console.error('分配标签失败:', error)
  }
}

const handleRemoveUserTag = (user, tag) => {
  ElMessageBox.confirm(`确定要移除用户 "${user.userName}" 的标签 "${tag.tagName}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await tagApi.removeTag({ userId: user.userId, tagId: tag.id })
      Logger.delete('用户标签', `移除用户标签`, { userId: user.userId, tagId: tag.id })
      ElMessage.success('移除成功')
      loadUserTagList()
    } catch (error) {
      ElMessage.error('移除失败')
      console.error('移除用户标签失败:', error)
    }
  })
}

onMounted(() => {
  loadTagList()
  loadUserTagList()
})
</script>

<style scoped>
.tag-container {
  padding: 20px;
}

.query-form {
  margin-bottom: 20px;
}
</style>
