<template>
  <div class="category-container">
    <el-card>
      <el-button type="primary" icon="Plus" @click="handleAdd" style="margin-bottom: 20px;">新增分类</el-button>

      <el-table :data="tableData" style="width: 100%" row-key="id" :tree-props="{ children: 'children' }">
        <el-table-column prop="categoryName" label="分类名称" width="200" />
        <el-table-column label="图标" width="100">
          <template #default="{ row }">
            <el-icon v-if="row.icon" :size="20">
              <component :is="row.icon" />
            </el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="contentCount" label="内容数量" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="success" size="small" @click="handleAddChild(row)">新增</el-button>
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="上级分类" prop="parentId">
          <el-tree-select
            v-model="formData.parentId"
            :data="categoryTreeData"
            :props="{ label: 'categoryName', value: 'id' }"
            check-strictly
            placeholder="请选择上级分类"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="formData.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类图标" prop="icon">
          <el-select v-model="formData.icon" placeholder="请选择图标" style="width: 100%">
            <el-option label="文件夹" value="Folder">
              <el-icon><Folder /></el-icon> 文件夹
            </el-option>
            <el-option label="文档" value="Document">
              <el-icon><Document /></el-icon> 文档
            </el-option>
            <el-option label="图片" value="Picture">
              <el-icon><Picture /></el-icon> 图片
            </el-option>
            <el-option label="视频" value="VideoCamera">
              <el-icon><VideoCamera /></el-icon> 视频
            </el-option>
            <el-option label="标签" value="CollectionTag">
              <el-icon><CollectionTag /></el-icon> 标签
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="显示排序" prop="sort">
          <el-input-number v-model="formData.sort" :min="0" :max="999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="分类描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入分类描述" />
        </el-form-item>
        <el-form-item label="分类状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
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
import { Folder, Document, Picture, VideoCamera, CollectionTag } from '@element-plus/icons-vue'
import categoryApi from '@/api/category'
import Logger from '@/utils/logger'

const tableData = ref([])
const categoryTreeData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const formData = reactive({
  id: null,
  parentId: 0,
  categoryName: '',
  icon: '',
  sort: 0,
  description: '',
  status: 1
})

const formRules = {
  categoryName: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  sort: [{ required: true, message: '请输入显示排序', trigger: 'blur' }]
}

const dialogTitle = computed(() => isEdit.value ? '编辑分类' : '新增分类')

const loadData = async () => {
  try {
    const res = await categoryApi.getCategoryList()
    const data = res.data || []
    tableData.value = data
    categoryTreeData.value = [{ id: 0, categoryName: '顶级分类', children: data }]
  } catch (error) {
    console.error('加载分类列表失败:', error)
    ElMessage.error('加载数据失败')
  }
}

const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
  Object.assign(formData, {
    id: null,
    parentId: 0,
    categoryName: '',
    icon: '',
    sort: 0,
    description: '',
    status: 1
  })
}

const handleAddChild = (row) => {
  isEdit.value = false
  dialogVisible.value = true
  Object.assign(formData, {
    id: null,
    parentId: row.id,
    categoryName: '',
    icon: '',
    sort: 0,
    description: '',
    status: 1
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
        if (isEdit.value) {
          await categoryApi.updateCategory(formData)
          Logger.update('分类管理', `编辑分类: ${formData.categoryName}`, formData)
          ElMessage.success('编辑成功')
        } else {
          await categoryApi.addCategory(formData)
          Logger.create('分类管理', `新增分类: ${formData.categoryName}`, formData)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        console.error('保存分类失败:', error)
      }
    }
  })
}

const handleDelete = (row) => {
  if (row.children && row.children.length > 0) {
    ElMessage.warning('该分类下还有子分类，无法删除')
    return
  }
  if (row.contentCount > 0) {
    ElMessage.warning('该分类下还有内容，无法删除')
    return
  }
  ElMessageBox.confirm(`确定要删除分类 "${row.categoryName}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await categoryApi.deleteCategory(row.id)
      Logger.delete('分类管理', `删除分类: ${row.categoryName}`, { id: row.id })
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      console.error('删除分类失败:', error)
    }
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.category-container {
  padding: 20px;
}
</style>
