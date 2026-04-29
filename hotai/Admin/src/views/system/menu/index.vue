<template>
  <div class="menu-container">
    <el-card>
      <el-button type="primary" icon="Plus" @click="handleAdd" style="margin-bottom: 20px;">新增菜单</el-button>

      <el-table :data="tableData" style="width: 100%" row-key="id" :tree-props="{ children: 'children' }">
        <el-table-column prop="menuName" label="菜单名称" width="200" />
        <el-table-column label="图标" width="100">
          <template #default="{ row }">
            <el-icon v-if="row.icon">
              <component :is="row.icon" />
            </el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="menuType" label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="getMenuTypeTag(row.menuType)">{{ getMenuTypeText(row.menuType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="path" label="路由地址" />
        <el-table-column prop="component" label="组件路径" show-overflow-tooltip />
        <el-table-column prop="perms" label="权限标识" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '显示' : '隐藏' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="success" size="small" @click="handleAddChild(row)" v-if="row.menuType !== 'F'">新增</el-button>
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="上级菜单" prop="parentId">
              <el-tree-select
                v-model="formData.parentId"
                :data="menuTreeData"
                :props="{ label: 'menuName', value: 'id' }"
                check-strictly
                placeholder="请选择上级菜单"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="菜单类型" prop="menuType">
              <el-radio-group v-model="formData.menuType">
                <el-radio label="M">目录</el-radio>
                <el-radio label="C">菜单</el-radio>
                <el-radio label="F">按钮</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜单名称" prop="menuName">
              <el-input v-model="formData.menuName" placeholder="请输入菜单名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="显示排序" prop="sort">
              <el-input-number v-model="formData.sort" :min="0" :max="999" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜单图标" prop="icon" v-if="formData.menuType !== 'F'">
              <el-select v-model="formData.icon" placeholder="请选择图标" style="width: 100%">
                <el-option label="首页" value="HomeFilled">
                  <el-icon><HomeFilled /></el-icon> 首页
                </el-option>
                <el-option label="设置" value="Setting">
                  <el-icon><Setting /></el-icon> 设置
                </el-option>
                <el-option label="用户" value="User">
                  <el-icon><User /></el-icon> 用户
                </el-option>
                <el-option label="文档" value="Document">
                  <el-icon><Document /></el-icon> 文档
                </el-option>
                <el-option label="列表" value="List">
                  <el-icon><List /></el-icon> 列表
                </el-option>
                <el-option label="文件夹" value="Folder">
                  <el-icon><Folder /></el-icon> 文件夹
                </el-option>
                <el-option label="聊天" value="ChatDotRound">
                  <el-icon><ChatDotRound /></el-icon> 聊天
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20" v-if="formData.menuType !== 'F'">
          <el-col :span="12">
            <el-form-item label="路由地址" prop="path">
              <el-input v-model="formData.path" placeholder="请输入路由地址" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="组件路径" prop="component" v-if="formData.menuType === 'C'">
              <el-input v-model="formData.component" placeholder="请输入组件路径" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="权限标识" prop="perms">
              <el-input v-model="formData.perms" placeholder="如: system:user:list" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜单状态" prop="status">
              <el-radio-group v-model="formData.status">
                <el-radio :label="1">显示</el-radio>
                <el-radio :label="0">隐藏</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20" v-if="formData.menuType !== 'F'">
          <el-col :span="12">
            <el-form-item label="是否外链">
              <el-radio-group v-model="formData.isFrame">
                <el-radio :label="1">是</el-radio>
                <el-radio :label="0">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否缓存">
              <el-radio-group v-model="formData.isCache">
                <el-radio :label="1">缓存</el-radio>
                <el-radio :label="0">不缓存</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
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
import { HomeFilled, Setting, User, Document, List, Folder, ChatDotRound } from '@element-plus/icons-vue'
import menuApi from '@/api/menu'
import Logger from '@/utils/logger'

const tableData = ref([])
const menuTreeData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const formData = reactive({
  id: null,
  parentId: 0,
  menuName: '',
  menuType: 'M',
  icon: '',
  sort: 0,
  path: '',
  component: '',
  perms: '',
  status: 1,
  visible: 1,
  isFrame: 0,
  isCache: 1
})

const formRules = {
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  sort: [{ required: true, message: '请输入显示排序', trigger: 'blur' }]
}

const dialogTitle = computed(() => isEdit.value ? '编辑菜单' : '新增菜单')

const loadData = async () => {
  try {
    const res = await menuApi.getMenuList()
    const data = res.data || []
    tableData.value = data
    menuTreeData.value = [{ id: 0, menuName: '主类目', children: data }]
  } catch (error) {
    console.error('加载菜单列表失败:', error)
    ElMessage.error('加载数据失败')
  }
}

const getMenuTypeTag = (type) => {
  const map = {
    M: '',
    C: 'success',
    F: 'info'
  }
  return map[type]
}

const getMenuTypeText = (type) => {
  const map = {
    M: '目录',
    C: '菜单',
    F: '按钮'
  }
  return map[type]
}

const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
  Object.assign(formData, {
    id: null,
    parentId: 0,
    menuName: '',
    menuType: 'M',
    icon: '',
    sort: 0,
    path: '',
    component: '',
    perms: '',
    status: 1,
    visible: 1,
    isFrame: 0,
    isCache: 1
  })
}

const handleAddChild = (row) => {
  isEdit.value = false
  dialogVisible.value = true
  Object.assign(formData, {
    id: null,
    parentId: row.id,
    menuName: '',
    menuType: row.menuType === 'M' ? 'C' : 'F',
    icon: '',
    sort: 0,
    path: '',
    component: '',
    perms: '',
    status: 1,
    visible: 1,
    isFrame: 0,
    isCache: 1
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
          await menuApi.updateMenu(formData)
          Logger.update('菜单管理', `编辑菜单: ${formData.menuName}`, formData)
          ElMessage.success('编辑成功')
        } else {
          await menuApi.addMenu(formData)
          Logger.create('菜单管理', `新增菜单: ${formData.menuName}`, formData)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        console.error('保存菜单失败:', error)
      }
    }
  })
}

const handleDelete = (row) => {
  if (row.children && row.children.length > 0) {
    ElMessage.warning('该菜单下还有子菜单，无法删除')
    return
  }
  ElMessageBox.confirm(`确定要删除菜单 "${row.menuName}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await menuApi.deleteMenu(row.id)
      Logger.delete('菜单管理', `删除菜单: ${row.menuName}`, { id: row.id })
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      console.error('删除菜单失败:', error)
    }
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.menu-container {
  padding: 20px;
}
</style>
