import Mock from 'mockjs'

// 菜单列表（树形）
export const getMenuList = () => {
  return {
    code: 200,
    message: '成功',
    data: [
      {
        id: 1,
        parentId: 0,
        menuName: '系统管理',
        menuType: 'M',
        icon: 'Setting',
        path: '/system',
        component: 'Layout',
        perms: '',
        sort: 1,
        status: 1,
        visible: 1,
        createTime: '2024-01-01 10:00:00',
        children: [
          {
            id: 11,
            parentId: 1,
            menuName: '用户管理',
            menuType: 'C',
            icon: 'User',
            path: 'user',
            component: 'system/user/index',
            perms: 'system:user:list',
            sort: 1,
            status: 1,
            visible: 1,
            createTime: '2024-01-01 10:00:00'
          },
          {
            id: 12,
            parentId: 1,
            menuName: '角色管理',
            menuType: 'C',
            icon: 'UserFilled',
            path: 'role',
            component: 'system/role/index',
            perms: 'system:role:list',
            sort: 2,
            status: 1,
            visible: 1,
            createTime: '2024-01-01 10:00:00'
          },
          {
            id: 13,
            parentId: 1,
            menuName: '菜单管理',
            menuType: 'C',
            icon: 'Menu',
            path: 'menu',
            component: 'system/menu/index',
            perms: 'system:menu:list',
            sort: 3,
            status: 1,
            visible: 1,
            createTime: '2024-01-01 10:00:00'
          },
          {
            id: 14,
            parentId: 1,
            menuName: '部门管理',
            menuType: 'C',
            icon: 'OfficeBuilding',
            path: 'dept',
            component: 'system/dept/index',
            perms: 'system:dept:list',
            sort: 4,
            status: 1,
            visible: 1,
            createTime: '2024-01-01 10:00:00'
          }
        ]
      },
      {
        id: 2,
        parentId: 0,
        menuName: '内容管理',
        menuType: 'M',
        icon: 'Document',
        path: '/content',
        component: 'Layout',
        perms: '',
        sort: 2,
        status: 1,
        visible: 1,
        createTime: '2024-01-01 10:00:00',
        children: [
          {
            id: 21,
            parentId: 2,
            menuName: '内容列表',
            menuType: 'C',
            icon: 'List',
            path: 'list',
            component: 'content/list/index',
            perms: 'content:list:query',
            sort: 1,
            status: 1,
            visible: 1,
            createTime: '2024-01-01 10:00:00'
          },
          {
            id: 22,
            parentId: 2,
            menuName: '内容审核',
            menuType: 'C',
            icon: 'Check',
            path: 'audit',
            component: 'content/audit/index',
            perms: 'content:audit:query',
            sort: 2,
            status: 1,
            visible: 1,
            createTime: '2024-01-01 10:00:00'
          },
          {
            id: 23,
            parentId: 2,
            menuName: '分类管理',
            menuType: 'C',
            icon: 'Folder',
            path: 'category',
            component: 'content/category/index',
            perms: 'content:category:query',
            sort: 3,
            status: 1,
            visible: 1,
            createTime: '2024-01-01 10:00:00'
          }
        ]
      },
      {
        id: 3,
        parentId: 0,
        menuName: '社区管理',
        menuType: 'M',
        icon: 'ChatDotRound',
        path: '/community',
        component: 'Layout',
        perms: '',
        sort: 3,
        status: 1,
        visible: 1,
        createTime: '2024-01-01 10:00:00',
        children: [
          {
            id: 31,
            parentId: 3,
            menuName: '帖子管理',
            menuType: 'C',
            icon: 'ChatLineRound',
            path: 'post',
            component: 'community/post/index',
            perms: 'community:post:query',
            sort: 1,
            status: 1,
            visible: 1,
            createTime: '2024-01-01 10:00:00'
          },
          {
            id: 32,
            parentId: 3,
            menuName: '评论管理',
            menuType: 'C',
            icon: 'ChatSquare',
            path: 'comment',
            component: 'community/comment/index',
            perms: 'community:comment:query',
            sort: 2,
            status: 1,
            visible: 1,
            createTime: '2024-01-01 10:00:00'
          },
          {
            id: 33,
            parentId: 3,
            menuName: 'Feed流管理',
            menuType: 'C',
            icon: 'DataLine',
            path: 'feed',
            component: 'community/feed/index',
            perms: 'community:feed:query',
            sort: 3,
            status: 1,
            visible: 1,
            createTime: '2024-01-01 10:00:00'
          }
        ]
      }
    ]
  }
}

// 新增菜单
export const addMenu = () => {
  return Mock.mock({
    code: 200,
    message: '新增成功',
    data: {
      id: '@increment'
    }
  })
}

// 更新菜单
export const updateMenu = () => {
  return Mock.mock({
    code: 200,
    message: '更新成功',
    data: null
  })
}

// 删除菜单
export const deleteMenu = () => {
  return Mock.mock({
    code: 200,
    message: '删除成功',
    data: null
  })
}

// 注册Mock接口
Mock.mock(/\/api\/menu\/list/, 'get', getMenuList)
Mock.mock(/\/api\/menu\/add/, 'post', addMenu)
Mock.mock(/\/api\/menu\/update/, 'put', updateMenu)
Mock.mock(/\/api\/menu\/delete/, 'delete', deleteMenu)
