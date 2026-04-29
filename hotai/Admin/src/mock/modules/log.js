import Mock from 'mockjs'

// 操作日志列表
export const getLogList = (params) => {
  const { page = 1, size = 10 } = params
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      [`list|${size}`]: [{
        'id|+1': (page - 1) * size + 1,
        operator: '@cname',
        'module|1': ['用户管理', '角色管理', '菜单管理', '部门管理', '内容管理', '帖子管理', '评论管理'],
        'operationType|1': ['create', 'update', 'delete', 'query', 'login', 'logout'],
        description: '@csentence(10,30)',
        'method|1': ['GET', 'POST', 'PUT', 'DELETE'],
        url: '@url("http")',
        params: JSON.stringify({ id: '@id', name: '@cname' }),
        ip: '@ip',
        browser: '@pick(["Chrome 120.0", "Firefox 115.0", "Safari 17.0", "Edge 120.0"])',
        os: '@pick(["Windows 11", "macOS 14.0", "Ubuntu 22.04", "iOS 17"])',
        'status|1': ['success', 'fail'],
        operationTime: '@datetime',
        'duration|50-500': 1
      }],
      total: 100,
      page: parseInt(page),
      size: parseInt(size)
    }
  })
}

// 删除日志
export const deleteLog = () => {
  return Mock.mock({
    code: 200,
    message: '删除成功',
    data: null
  })
}

// 清空日志
export const clearLog = () => {
  return Mock.mock({
    code: 200,
    message: '清空成功',
    data: null
  })
}

// 导出日志
export const exportLog = () => {
  return Mock.mock({
    code: 200,
    message: '导出成功',
    data: {
      url: '@url("http")'
    }
  })
}

// 注册Mock接口
Mock.mock(/\/api\/log\/list/, 'get', getLogList)
Mock.mock(/\/api\/log\/delete/, 'delete', deleteLog)
Mock.mock(/\/api\/log\/clear/, 'delete', clearLog)
Mock.mock(/\/api\/log\/export/, 'get', exportLog)
