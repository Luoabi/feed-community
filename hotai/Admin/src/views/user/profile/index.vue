<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <!-- 左侧个人信息卡片 -->
      <el-col :span="8">
        <el-card class="profile-card">
          <div class="profile-header">
            <el-avatar :size="100" :src="userInfo.avatar || defaultAvatar" />
            <el-upload
              class="avatar-uploader"
              action="#"
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :http-request="uploadAvatar"
            >
              <el-button size="small" type="primary" plain>更换头像</el-button>
            </el-upload>
          </div>
          <div class="profile-info">
            <h2>{{ userInfo.nickname || userInfo.username }}</h2>
            <p class="role-tags">
              <el-tag v-for="role in roles" :key="role" type="success" size="small">
                {{ role }}
              </el-tag>
            </p>
            <p class="bio">{{ userInfo.bio || '这个人很懒，什么都没留下~' }}</p>
          </div>
          <el-divider />
          <div class="profile-stats">
            <div class="stat-item">
              <div class="stat-value">{{ stats.contentCount }}</div>
              <div class="stat-label">发布内容</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ stats.commentCount }}</div>
              <div class="stat-label">评论数</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ stats.likeCount }}</div>
              <div class="stat-label">获赞数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧信息编辑 -->
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>个人信息</span>
              <el-button type="primary" @click="handleSave" :loading="saving">保存修改</el-button>
            </div>
          </template>
          <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" disabled />
            </el-form-item>
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="form.nickname" placeholder="请输入昵称" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio :label="0">保密</el-radio>
                <el-radio :label="1">男</el-radio>
                <el-radio :label="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="个人简介" prop="bio">
              <el-input
                v-model="form.bio"
                type="textarea"
                :rows="4"
                placeholder="介绍一下自己吧~"
                maxlength="200"
                show-word-limit
              />
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 修改密码 -->
        <el-card style="margin-top: 20px;">
          <template #header>
            <span>修改密码</span>
          </template>
          <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原密码" show-password />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleChangePassword" :loading="changingPassword">修改密码</el-button>
              <el-button @click="resetPasswordForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import userApi from '@/api/user'

const store = useStore()
const formRef = ref(null)
const passwordFormRef = ref(null)
const saving = ref(false)
const changingPassword = ref(false)

const userInfo = computed(() => store.state.user.userInfo)
const roles = computed(() => store.state.user.roles || [])

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const stats = reactive({
  contentCount: 0,
  commentCount: 0,
  likeCount: 0
})

const form = reactive({
  username: '',
  nickname: '',
  email: '',
  phone: '',
  gender: 0,
  bio: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const rules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 初始化表单数据
const initForm = () => {
  form.username = userInfo.value.username || ''
  form.nickname = userInfo.value.nickname || ''
  form.email = userInfo.value.email || ''
  form.phone = userInfo.value.phone || ''
  form.gender = userInfo.value.gender || 0
  form.bio = userInfo.value.bio || ''
}

// 加载统计数据
const loadStats = async () => {
  // 这里可以调用API获取真实数据
  stats.contentCount = 128
  stats.commentCount = 456
  stats.likeCount = 1234
}

// 保存个人信息
const handleSave = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      saving.value = true
      try {
        await userApi.updateUser({
          id: userInfo.value.id,
          ...form
        })
        
        // 更新Vuex中的用户信息
        await store.dispatch('user/getUserInfo')
        
        ElMessage.success('保存成功')
      } catch (error) {
        ElMessage.error('保存失败：' + (error.message || '未知错误'))
      } finally {
        saving.value = false
      }
    }
  })
}

// 修改密码
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      changingPassword.value = true
      try {
        // 调用重置密码API
        await userApi.resetPassword({
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        })
        
        ElMessage.success('密码修改成功，请重新登录')
        
        // 清空表单
        resetPasswordForm()
        
        // 退出登录
        setTimeout(() => {
          store.dispatch('user/logout')
          window.location.href = '/login'
        }, 1500)
      } catch (error) {
        ElMessage.error('密码修改失败：' + (error.message || '未知错误'))
      } finally {
        changingPassword.value = false
      }
    }
  })
}

// 重置密码表单
const resetPasswordForm = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordFormRef.value?.clearValidate()
}

// 上传头像前的验证
const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 上传头像
const uploadAvatar = async (options) => {
  const formData = new FormData()
  formData.append('file', options.file)
  
  try {
    // 这里调用上传API
    // const res = await uploadApi.uploadImage(formData)
    // await userApi.updateUser({ id: userInfo.value.id, avatar: res.data.url })
    // await store.dispatch('user/getUserInfo')
    
    ElMessage.success('头像上传成功')
  } catch (error) {
    ElMessage.error('头像上传失败')
  }
}

onMounted(() => {
  initForm()
  loadStats()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.profile-card {
  text-align: center;
}

.profile-header {
  padding: 20px 0;
}

.avatar-uploader {
  margin-top: 15px;
}

.profile-info {
  padding: 20px 0;
}

.profile-info h2 {
  margin: 10px 0;
  font-size: 24px;
  color: #2c3e50;
}

.role-tags {
  margin: 10px 0;
  display: flex;
  gap: 8px;
  justify-content: center;
}

.bio {
  color: #7f8c8d;
  font-size: 14px;
  line-height: 1.6;
  margin: 15px 0;
}

.profile-stats {
  display: flex;
  justify-content: space-around;
  padding: 20px 0;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #667eea;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #95a5a6;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
