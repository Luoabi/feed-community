<template>
  <div class="settings-container">
    <el-card>
      <template #header>
        <span>系统设置</span>
      </template>
      
      <el-tabs v-model="activeTab" class="settings-tabs">
        <!-- 基本设置 -->
        <el-tab-pane label="基本设置" name="basic">
          <el-form :model="basicSettings" label-width="120px">
            <el-form-item label="系统名称">
              <el-input v-model="basicSettings.systemName" placeholder="请输入系统名称" />
            </el-form-item>
            <el-form-item label="系统Logo">
              <el-upload
                class="logo-uploader"
                action="#"
                :show-file-list="false"
                :before-upload="beforeLogoUpload"
              >
                <img v-if="basicSettings.logo" :src="basicSettings.logo" class="logo" />
                <el-icon v-else class="logo-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </el-form-item>
            <el-form-item label="系统描述">
              <el-input
                v-model="basicSettings.description"
                type="textarea"
                :rows="3"
                placeholder="请输入系统描述"
              />
            </el-form-item>
            <el-form-item label="版权信息">
              <el-input v-model="basicSettings.copyright" placeholder="请输入版权信息" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveBasicSettings">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 通知设置 -->
        <el-tab-pane label="通知设置" name="notification">
          <el-form :model="notificationSettings" label-width="120px">
            <el-form-item label="邮件通知">
              <el-switch v-model="notificationSettings.emailEnabled" />
              <span class="setting-desc">开启后将通过邮件接收系统通知</span>
            </el-form-item>
            <el-form-item label="浏览器通知">
              <el-switch v-model="notificationSettings.browserEnabled" />
              <span class="setting-desc">开启后将通过浏览器推送通知</span>
            </el-form-item>
            <el-form-item label="新内容通知">
              <el-switch v-model="notificationSettings.newContentEnabled" />
              <span class="setting-desc">有新内容发布时通知</span>
            </el-form-item>
            <el-form-item label="评论通知">
              <el-switch v-model="notificationSettings.commentEnabled" />
              <span class="setting-desc">有新评论时通知</span>
            </el-form-item>
            <el-form-item label="审核通知">
              <el-switch v-model="notificationSettings.auditEnabled" />
              <span class="setting-desc">有待审核内容时通知</span>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveNotificationSettings">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 安全设置 -->
        <el-tab-pane label="安全设置" name="security">
          <el-form :model="securitySettings" label-width="120px">
            <el-form-item label="登录超时">
              <el-select v-model="securitySettings.sessionTimeout" placeholder="请选择">
                <el-option label="30分钟" :value="30" />
                <el-option label="1小时" :value="60" />
                <el-option label="2小时" :value="120" />
                <el-option label="4小时" :value="240" />
                <el-option label="8小时" :value="480" />
              </el-select>
              <span class="setting-desc">超过此时间未操作将自动退出登录</span>
            </el-form-item>
            <el-form-item label="密码强度">
              <el-radio-group v-model="securitySettings.passwordStrength">
                <el-radio label="low">低（6位以上）</el-radio>
                <el-radio label="medium">中（8位以上，包含字母和数字）</el-radio>
                <el-radio label="high">高（10位以上，包含字母、数字和特殊字符）</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="登录验证码">
              <el-switch v-model="securitySettings.captchaEnabled" />
              <span class="setting-desc">开启后登录时需要输入验证码</span>
            </el-form-item>
            <el-form-item label="双因素认证">
              <el-switch v-model="securitySettings.twoFactorEnabled" />
              <span class="setting-desc">开启后需要手机验证码或邮箱验证</span>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveSecuritySettings">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 外观设置 -->
        <el-tab-pane label="外观设置" name="appearance">
          <el-form :model="appearanceSettings" label-width="120px">
            <el-form-item label="主题模式">
              <el-radio-group v-model="appearanceSettings.theme">
                <el-radio label="light">浅色</el-radio>
                <el-radio label="dark">深色</el-radio>
                <el-radio label="auto">跟随系统</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="主题色">
              <el-color-picker v-model="appearanceSettings.primaryColor" />
            </el-form-item>
            <el-form-item label="侧边栏">
              <el-switch v-model="appearanceSettings.sidebarCollapsed" />
              <span class="setting-desc">默认收起侧边栏</span>
            </el-form-item>
            <el-form-item label="面包屑导航">
              <el-switch v-model="appearanceSettings.breadcrumbEnabled" />
              <span class="setting-desc">显示面包屑导航</span>
            </el-form-item>
            <el-form-item label="页面动画">
              <el-switch v-model="appearanceSettings.animationEnabled" />
              <span class="setting-desc">开启页面切换动画</span>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveAppearanceSettings">保存设置</el-button>
              <el-button @click="resetAppearanceSettings">恢复默认</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 关于系统 -->
        <el-tab-pane label="关于系统" name="about">
          <div class="about-section">
            <el-descriptions :column="1" border>
              <el-descriptions-item label="系统名称">社区互动平台管理系统</el-descriptions-item>
              <el-descriptions-item label="系统版本">v1.0.0</el-descriptions-item>
              <el-descriptions-item label="前端框架">Vue 3.5.32 + Element Plus 2.13.6</el-descriptions-item>
              <el-descriptions-item label="后端框架">Spring Boot 3.4.1 + MyBatis-Plus 3.5.7</el-descriptions-item>
              <el-descriptions-item label="数据库">MySQL 8.0+</el-descriptions-item>
              <el-descriptions-item label="开发者">开发团队</el-descriptions-item>
              <el-descriptions-item label="更新时间">2024-01-15</el-descriptions-item>
            </el-descriptions>

            <el-divider />

            <div class="tech-stack">
              <h3>技术栈</h3>
              <el-tag v-for="tech in techStack" :key="tech" type="info" style="margin: 5px;">
                {{ tech }}
              </el-tag>
            </div>

            <el-divider />

            <div class="links">
              <h3>相关链接</h3>
              <el-link href="https://cn.vuejs.org/" target="_blank" type="primary">Vue 官方文档</el-link>
              <el-link href="https://element-plus.org/" target="_blank" type="primary">Element Plus 文档</el-link>
              <el-link href="https://spring.io/projects/spring-boot" target="_blank" type="primary">Spring Boot 文档</el-link>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const activeTab = ref('basic')

const basicSettings = reactive({
  systemName: '社区互动平台',
  logo: '',
  description: '基于Vue3和Spring Boot的社区互动平台管理系统',
  copyright: '© 2024 社区互动平台. All rights reserved.'
})

const notificationSettings = reactive({
  emailEnabled: true,
  browserEnabled: false,
  newContentEnabled: true,
  commentEnabled: true,
  auditEnabled: true
})

const securitySettings = reactive({
  sessionTimeout: 120,
  passwordStrength: 'medium',
  captchaEnabled: false,
  twoFactorEnabled: false
})

const appearanceSettings = reactive({
  theme: 'light',
  primaryColor: '#667eea',
  sidebarCollapsed: false,
  breadcrumbEnabled: true,
  animationEnabled: true
})

const techStack = [
  'Vue 3', 'Element Plus', 'Vite', 'Vue Router', 'Vuex',
  'Spring Boot', 'MyBatis-Plus', 'MySQL', 'JWT', 'Swagger'
]

const beforeLogoUpload = (file) => {
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

const saveBasicSettings = () => {
  ElMessage.success('基本设置保存成功')
}

const saveNotificationSettings = () => {
  ElMessage.success('通知设置保存成功')
}

const saveSecuritySettings = () => {
  ElMessage.success('安全设置保存成功')
}

const saveAppearanceSettings = () => {
  ElMessage.success('外观设置保存成功')
}

const resetAppearanceSettings = () => {
  appearanceSettings.theme = 'light'
  appearanceSettings.primaryColor = '#667eea'
  appearanceSettings.sidebarCollapsed = false
  appearanceSettings.breadcrumbEnabled = true
  appearanceSettings.animationEnabled = true
  ElMessage.success('已恢复默认设置')
}
</script>

<style scoped>
.settings-container {
  padding: 20px;
}

.settings-tabs {
  margin-top: 20px;
}

.setting-desc {
  margin-left: 10px;
  color: #95a5a6;
  font-size: 12px;
}

.logo-uploader {
  display: inline-block;
}

.logo {
  width: 100px;
  height: 100px;
  display: block;
  object-fit: contain;
}

.logo-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

.logo-uploader-icon:hover {
  border-color: #667eea;
  color: #667eea;
}

.about-section {
  padding: 20px;
}

.tech-stack,
.links {
  margin-top: 20px;
}

.tech-stack h3,
.links h3 {
  margin-bottom: 15px;
  color: #2c3e50;
}

.links .el-link {
  display: block;
  margin: 10px 0;
}
</style>
