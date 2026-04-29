import Mock from 'mockjs'

// 角色列表
export const getRoleList = () => {
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      'list|8': [{
        'id|+1': 1,
        roleName: '@ctitle(4,8)',
        roleKey: '@word(5,10)',
        'sort|1-100': 1,
        'status|1': [0, 1],
        remark: '@csentence',
        'menuIds': ['@integer(1, 20)', '@integer(1, 20)', '@integer(1, 20)'],
        'dataScope|1': [1, 2, 3, 4, 5], // 1全部 2自定义 3本部门 4本部门及以下 5仅本人
        createTime: '@datetime',
        updateTime: '@datetime'
      }]
    }
  })
}

// 角色详情
export const getRoleDetail = () => {
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      id: 1,
      roleName: '@ctitle(4,8)',
      roleKey: '@word(5,10)',
      sort: 1,
      status: 1,
      remark: '@csentence',
      menuIds: [1, 2, 3, 11, 12],
      dataScope: 1,
      createTime: '@datetime',
      updateTime: '@datetime'
    }
  })
}

// 新增角色
export const addRole = () => {
  return Mock.mock({
    code: 200,
    message: '新增成功',
    data: {
      id: '@increment'
    }
  })
}

// 更新角色
export const updateRole = () => {
  return Mock.mock({
    code: 200,
    message: '更新成功',
    data: null
  })
}

// 删除角色
export const deleteRole = () => {
  return Mock.mock({
    code: 200,
    message: '删除成功',
    data: null
  })
}

// 分配权限
export const assignPermission = () => {
  return Mock.mock({
    code: 200,
    message: '权限分配成功',
    data: null
  })
}

// 修改角色状态
export const changeRoleStatus = () => {
  return Mock.mock({
    code: 200,
    message: '状态修改成功',
    data: null
  })
}

// 注册Mock接口
Mock.mock(/\/api\/role\/list/, 'get', getRoleList)
Mock.mock(/\/api\/role\/detail/, 'get', getRoleDetail)
Mock.mock(/\/api\/role\/add/, 'post', addRole)
Mock.mock(/\/api\/role\/update/, 'put', updateRole)
Mock.mock(/\/api\/role\/delete/, 'delete', deleteRole)
Mock.mock(/\/api\/role\/assignPermission/, 'post', assignPermission)
Mock.mock(/\/api\/role\/changeStatus/, 'put', changeRoleStatus)
