import { createRouter, createWebHistory } from 'vue-router'
import store from '@/store'

// 路由配置
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      }
    ]
  },
  {
    path: '/system',
    component: () => import('@/layout/index.vue'),
    redirect: '/system/user',
    meta: {
      title: '系统管理',
      icon: 'Setting',
      requiresAuth: true,
      roles: ['超级管理员', '普通管理员'] // ✅ 只有超级管理员和普通管理员可以访问
    },
    children: [
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'role',
        name: 'Role',
        component: () => import('@/views/system/role/index.vue'),
        meta: {
          title: '角色管理',
          icon: 'UserFilled',
          roles: ['超级管理员'] // ✅ 只有超级管理员可以访问
        }
      },
      {
        path: 'menu',
        name: 'Menu',
        component: () => import('@/views/system/menu/index.vue'),
        meta: {
          title: '菜单管理',
          icon: 'Menu',
          roles: ['超级管理员'] // ✅ 只有超级管理员可以访问
        }
      },
      {
        path: 'dept',
        name: 'Dept',
        component: () => import('@/views/system/dept/index.vue'),
        meta: { title: '部门管理', icon: 'OfficeBuilding' }
      },
      {
        path: 'log',
        name: 'Log',
        component: () => import('@/views/system/log/index.vue'),
        meta: { title: '操作日志', icon: 'Document',hidden:"ture" }
      }
    ]
  },
  {
    path: '/content',
    component: () => import('@/layout/index.vue'),
    redirect: '/content/list',
    meta: {
      title: '内容管理',
      icon: 'Document',
      requiresAuth: true,
      roles: ['超级管理员', '普通管理员', '内容创作者'] // ✅ 三种角色可以访问
    },
    children: [
      {
        path: 'list',
        name: 'ContentList',
        component: () => import('@/views/content/list/index.vue'),
        meta: { title: '内容列表', icon: 'List' }
      },
      {
        path: 'audit',
        name: 'ContentAudit',
        component: () => import('@/views/content/audit/index.vue'),
        meta: {
          title: '内容审核',
          icon: 'Check',
          roles: ['超级管理员', '普通管理员'] // ✅ 只有管理员可以审核
        }
      },
      {
        path: 'category',
        name: 'ContentCategory',
        component: () => import('@/views/content/category/index.vue'),
     
        meta: { title: '分类管理', icon: 'Folder',  hidden: true,}
      }
    ]
  },
  {
    path: '/community',
    component: () => import('@/layout/index.vue'),
    redirect: '/community/post',
    meta: {
      title: '社区管理',
      icon: 'ChatDotRound',
      requiresAuth: true,
      roles: ['超级管理员', '内容创作者'] // ✅ 超级管理员和内容创作者可以访问
    },
    children: [
      {
        path: 'post',
        name: 'Post',
        component: () => import('@/views/community/post/index.vue'),
        meta: { title: '帖子管理', icon: 'ChatLineRound' }
      },
      {
        path: 'comment',
        name: 'Comment',
        component: () => import('@/views/community/comment/index.vue'),
        meta: { title: '评论管理', icon: 'ChatSquare' }
      },
      {
        path: 'feed',
        name: 'Feed',
        component: () => import('@/views/community/feed/index.vue'),
        meta: {
          title: 'Feed流管理',
          icon: 'DataLine',
          roles: ['超级管理员'] // ✅ 只有超级管理员可以管理Feed流
        }
      }
    ]
  },
  {
    path: '/interaction',
    component: () => import('@/layout/index.vue'),
    redirect: '/interaction/like',
    meta: {
      title: '互动数据',
      icon: 'DataAnalysis',
      requiresAuth: true,
      roles: ['超级管理员', '内容创作者'] // ✅ 超级管理员和内容创作者可以访问
    },
    children: [
      {
        path: 'like',
        name: 'Like',
        component: () => import('@/views/interaction/like/index.vue'),
        meta: { title: '点赞管理', icon: 'CaretTop' }
      },
      {
        path: 'collect',
        name: 'Collect',
        component: () => import('@/views/interaction/collect/index.vue'),
        meta: { title: '收藏管理', icon: 'Star' }
      },
      {
        path: 'share',
        name: 'Share',
        component: () => import('@/views/interaction/share/index.vue'),
        meta: { title: '分享管理', icon: 'Share' }
      }
    ]
  },
  {
    path: '/user',
    component: () => import('@/layout/index.vue'),
    redirect: '/user/profile',
    meta: {
      requiresAuth: true,
      hidden: true // 不在侧边栏显示
    },
    children: [
      {
        path: 'profile',
        name: 'UserProfile',
        component: () => import('@/views/user/profile/index.vue'),
        meta: { title: '个人中心', icon: 'User' }
      },
      {
        path: 'settings',
        name: 'UserSettings',
        component: () => import('@/views/user/settings/index.vue'),
        meta: { title: '设置', icon: 'Setting' }
      },
      {
        path: 'tag',
        name: 'UserTag',
        component: () => import('@/views/user/tag/index.vue'),
        meta: { 
          title: '用户标签', 
          icon: 'CollectionTag',
          hidden: true,
          roles: ['超级管理员'] // ✅ 只有超级管理员可以管理用户标签
        }
      }
    ]
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '404', requiresAuth: false }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = store.state.user.token
  const userRoles = store.state.user.roles || []

  // 需要认证的路由
  if (to.meta.requiresAuth && !token) {
    next('/login')
    return
  }

  // 已登录访问登录页，跳转首页
  if (to.path === '/login' && token) {
    next('/')
    return
  }

  // ✅ 检查路由角色权限
  if (to.meta.roles && to.meta.roles.length > 0) {
    const hasRole = userRoles.some(role => to.meta.roles.includes(role))
    if (!hasRole) {
      // 无权限，跳转到首页或403页面
      next('/')
      return
    }
  }

  next()
})

export default router
