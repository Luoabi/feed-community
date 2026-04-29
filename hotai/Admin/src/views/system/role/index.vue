<template>
  <div class="role-container">
    <el-card>
      <el-button type="primary" icon="Plus" @click="handleAdd" style="margin-bottom: 20px;">新增角色</el-button>

      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roleName" label="角色名称" />
        <el-table-column prop="roleKey" label="角色标识" />
        <el-table-column prop="sort" label="排序" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" />
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" size="small" @click="handlePermission(row)">权限</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="formData.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色标识" prop="roleKey">
          <el-input v-model="formData.roleKey" placeholder="请输入角色标识，如: admin" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="formData.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="数据权限" prop="dataScope">
          <el-select v-model="formData.dataScope" placeholder="请选择数据权限范围" style="width: 100%">
            <el-option label="全部数据权限" :value="1" />
            <el-option label="自定义数据权限" :value="2" />
            <el-option label="本部门数据权限" :value="3" />
            <el-option label="本部门及以下数据权限" :value="4" />
            <el-option label="仅本人数据权限" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 权限分配对话框 -->
    <el-dialog v-model="permissionDialogVisible" title="分配权限" width="600px">
      <el-tree
        ref="treeRef"
        :data="menuTree"
        show-checkbox
        node-key="id"
        :default-checked-keys="checkedKeys"
        :props="{ children: 'children', label: 'menuName' }"
      />
      <template #footer>
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePermissionSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import roleApi from '@/api/role'
import menuApi from '@/api/menu'
import Logger from '@/utils/logger'

const tableData = ref([])
const dialogVisible = ref(false)
const permissionDialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const treeRef = ref(null)
const currentRole = ref(null)
const checkedKeys = ref([])

const formData = reactive({
  id: null,
  roleName: '',
  roleKey: '',
  sort: 0,
  dataScope: 1,
  status: 1,
  remark: ''
})

const formRules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleKey: [{ required: true, message: '请输入角色标识', trigger: 'blur' }]
}

const menuTree = ref([
  {
    id: 1,
    menuName: '系统管理',
    children: [
      { id: 11, menuName: '用户管理' },
      { id: 12, menuName: '角色管理' },
      { id: 13, menuName: '菜单管理' },
      { id: 14, menuName: '部门管理' },
      { id: 15, menuName: '操作日志' }
    ]
  },
  {
    id: 2,
    menuName: '内容管理',
    children: [
      { id: 21, menuName: '内容列表' },
      { id: 22, menuName: '内容审核' },
      { id: 23, menuName: '分类管理' }
    ]
  },
  {
    id: 3,
    menuName: '社区管理',
    children: [
      { id: 31, menuName: '帖子管理' },
      { id: 32, menuName: '评论管理' },
      { id: 33, menuName: 'Feed流管理' }
    ]
  }
])

const dialogTitle = computed(() => isEdit.value ? '编辑角色' : '新增角色')

const loadData = async () => {
  try {
    const res = await roleApi.getRoleList()
    tableData.value = res.data.list || res.data
  } catch (error) {
    console.error('加载角色列表失败:', error)
    ElMessage.error('加载数据失败')
  }
}

const loadMenuTree = async () => {
  try {
    const res = await menuApi.getMenuList()
    menuTree.value = res.data
  } catch (error) {
    console.error('加载菜单树失败:', error)
  }
}

const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
  Object.assign(formData, {
    id: null,
    roleName: '',
    roleKey: '',
    sort: 0,
    dataScope: 1,
    status: 1,
    remark: ''
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
          await roleApi.updateRole(formData)
          Logger.update('角色管理', `编辑角色: ${formData.roleName}`, formData)
          ElMessage.success('编辑成功')
        } else {
          await roleApi.addRole(formData)
          Logger.create('角色管理', `新增角色: ${formData.roleName}`, formData)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        console.error('保存角色失败:', error)
      }
    }
  })
}

const handlePermission = (row) => {
  currentRole.value = row
  checkedKeys.value = row.menuIds || [] // 使用后端返回的已选中权限
  permissionDialogVisible.value = true
}

const handlePermissionSubmit = async () => {
  try {
    const keys = treeRef.value.getCheckedKeys()
    await roleApi.assignPermission({ roleId: currentRole.value.id, menuIds: keys })
    Logger.update('角色管理', `分配权限: ${currentRole.value.roleName}`, { roleId: currentRole.value.id, menuIds: keys })
    ElMessage.success('权限分配成功')
    permissionDialogVisible.value = false
  } catch (error) {
    console.error('分配权限失败:', error)
  }
}

const handleStatusChange = async (row) => {
  try {
    await roleApi.changeStatus({ id: row.id, status: row.status })
    Logger.update('角色管理', `修改角色状态: ${row.roleName}`, { id: row.id, status: row.status })
    ElMessage.success('状态修改成功')
  } catch (error) {
    console.error('修改状态失败:', error)
    row.status = row.status === 1 ? 0 : 1 // 恢复原状态
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除角色 ${row.roleName} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await roleApi.deleteRole(row.id)
      Logger.delete('角色管理', `删除角色: ${row.roleName}`, { id: row.id })
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      console.error('删除角色失败:', error)
    }
  })
}

onMounted(() => {
  loadData()
  loadMenuTree()
})
</script>

<style scoped>
.role-container {
  padding: 20px;
}
</style>
