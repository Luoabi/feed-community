<template>
  <div class="navbar">
    <div class="left-menu">
      <el-icon class="hamburger" @click="toggleSideBar">
        <Fold v-if="sidebar.opened" />
        <Expand v-else />
      </el-icon>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">
          {{ item.meta?.title }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="right-menu">
      <el-dropdown @command="handleCommand">
        <div class="avatar-wrapper">
          <el-avatar :src="userInfo.avatar" />
          <span class="username">{{ userInfo.nickname }}</span>
          <el-icon><CaretBottom /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">个人中心</el-dropdown-item>
            <el-dropdown-item command="settings">设置</el-dropdown-item>
            <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useStore } from 'vuex'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Fold, Expand, CaretBottom } from '@element-plus/icons-vue'

const store = useStore()
const route = useRoute()
const router = useRouter()

const sidebar = computed(() => store.state.app.sidebar)
const userInfo = computed(() => store.state.user.userInfo)
const breadcrumbs = computed(() => {
  return route.matched.filter(item => item.meta?.title)
})

const toggleSideBar = () => {
  store.dispatch('app/toggleSideBar')
}

const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/user/profile')
      break
    case 'settings':
      router.push('/user/settings')
      break
    case 'logout':
      ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 记录登出日志
        import('@/utils/logger').then(({ default: Logger }) => {
          Logger.logout(userInfo.value.username)
        })
        store.dispatch('user/logout').then(() => {
          router.push('/login')
          ElMessage.success('退出成功')
        })
      })
      break
  }
}
</script>

<style scoped>
.navbar {
  height: 50px;
  overflow: hidden;
  position: relative;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.left-menu {
  display: flex;
  align-items: center;
  gap: 20px;
}

.hamburger {
  font-size: 24px;
  cursor: pointer;
}

.right-menu {
  display: flex;
  align-items: center;
}

.avatar-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.username {
  font-size: 14px;
}
</style>
