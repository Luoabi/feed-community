import Mock from 'mockjs'

// 用户登录
export const login = () => {
  return Mock.mock({
    code: 200,
    message: '登录成功',
    data: {
      token: '@guid',
      userInfo: {
        id: 1,
        username: 'admin',
        nickname: '管理员',
        avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
        email: 'admin@example.com',
        phone: '13800138000',
        gender: 1,
        deptId: 1,
        deptName: '技术部'
      },
      roles: ['admin'],
      permissions: ['*:*:*']
    }
  })
}

// 获取用户信息
export const getUserInfo = () => {
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      userInfo: {
        id: 1,
        username: 'admin',
        nickname: '管理员',
        avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
        email: 'admin@example.com',
        phone: '13800138000',
        gender: 1,
        deptId: 1,
        deptName: '技术部'
      },
      roles: ['admin'],
      permissions: ['*:*:*']
    }
  })
}

// 用户列表
export const getUserList = (params) => {
  const { page = 1, size = 10 } = params
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      [`list|${size}`]: [{
        'id|+1': (page - 1) * size + 1,
        username: '@word(5,10)',
        nickname: '@cname',
        email: '@email',
        phone: /^1[3-9]\d{9}$/,
        avatar: '@image("200x200", "@color", "#FFF", "@cname")',
        'gender|1': [0, 1, 2],
        'deptId|1-10': 1,
        deptName: '@ctitle(2,4)部',
        'roleIds': ['@integer(1, 5)'],
        roleNames: '@ctitle(2,4)角色',
        'status|1': [0, 1],
        createTime: '@datetime',
        updateTime: '@datetime',
        lastLoginTime: '@datetime',
        lastLoginIp: '@ip'
      }],
      total: 100,
      page: parseInt(page),
      size: parseInt(size)
    }
  })
}

// 新增用户
export const addUser = () => {
  return Mock.mock({
    code: 200,
    message: '新增成功',
    data: {
      id: '@increment'
    }
  })
}

// 更新用户
export const updateUser = () => {
  return Mock.mock({
    code: 200,
    message: '更新成功',
    data: null
  })
}

// 删除用户
export const deleteUser = () => {
  return Mock.mock({
    code: 200,
    message: '删除成功',
    data: null
  })
}

// 重置密码
export const resetPassword = () => {
  return Mock.mock({
    code: 200,
    message: '密码重置成功',
    data: null
  })
}

// 修改用户状态
export const changeUserStatus = () => {
  return Mock.mock({
    code: 200,
    message: '状态修改成功',
    data: null
  })
}

// 修改密码
export const changePassword = () => {
  return Mock.mock({
    code: 200,
    message: '密码修改成功',
    data: null
  })
}

// 注册Mock接口
Mock.mock(/\/api\/user\/login/, 'post', login)
Mock.mock(/\/api\/user\/info/, 'get', getUserInfo)
Mock.mock(/\/api\/user\/list/, 'get', getUserList)
Mock.mock(/\/api\/user\/add/, 'post', addUser)
Mock.mock(/\/api\/user\/update/, 'put', updateUser)
Mock.mock(/\/api\/user\/delete/, 'delete', deleteUser)
Mock.mock(/\/api\/user\/resetPassword/, 'put', resetPassword)
Mock.mock(/\/api\/user\/changeStatus/, 'put', changeUserStatus)
Mock.mock(/\/api\/user\/changePassword/, 'put', changePassword)
