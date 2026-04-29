<template>
  <div class="sidebar">
    <div class="logo-container">
      <h1 class="logo-title">社区管理后台</h1>
    </div>
    <el-scrollbar>
      <el-menu
        :default-active="activeMenu"
        :collapse="!sidebar.opened"
        :unique-opened="false"
        :collapse-transition="false"
        mode="vertical"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <SidebarItem
          v-for="route in routes"
          :key="route.path"
          :item="route"
          :base-path="route.path"
        />
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useStore } from 'vuex'
import { useRoute, useRouter } from 'vue-router'
import SidebarItem from './SidebarItem.vue'

const store = useStore()
const route = useRoute()
const router = useRouter()

const sidebar = computed(() => store.state.app.sidebar)

// ✅ 根据用户角色过滤菜单
const routes = computed(() => {
  const userRoles = store.state.user.roles || []
  
  // 获取所有路由
  const allRoutes = router.options.routes.filter(r => {
    return r.path !== '/login' && 
           r.path !== '/404' && 
           r.path !== '/:pathMatch(.*)*' && 
           r.children && 
           r.children.length > 0
  })
  
  // ✅ 如果没有角色信息（刷新页面后可能出现），默认显示所有菜单
  if (userRoles.length === 0) {
    return allRoutes
  }
  
  // 根据角色过滤路由
  return allRoutes.filter(route => {
    return hasPermission(route, userRoles)
  })
})

const activeMenu = computed(() => route.path)

/**
 * 判断用户是否有权限访问该路由
 */
function hasPermission(route, userRoles) {
  // 如果路由没有定义roles，则所有登录用户都可以访问
  if (!route.meta || !route.meta.roles) {
    return true
  }
  
  // 检查用户角色是否在路由允许的角色列表中
  return userRoles.some(role => route.meta.roles.includes(role))
}
</script>

<style scoped>
.sidebar {
  height: 100%;
  background-color: #304156;
}

.logo-container {
  height: 50px;
  line-height: 50px;
  background: #2b2f3a;
  text-align: center;
  overflow: hidden;
}

.logo-title {
  font-size: 18px;
  color: #fff;
  font-weight: 600;
  margin: 0;
}

:deep(.el-menu) {
  border: none;
}
</style>
