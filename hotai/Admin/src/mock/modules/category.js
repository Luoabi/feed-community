import Mock from 'mockjs'

// 分类列表
export const getCategoryList = () => {
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      'list|10': [{
        'id|+1': 1,
        categoryName: '@ctitle(2,6)',
        categoryKey: '@word(5,10)',
        icon: '@image("100x100", "@color", "#FFF", "图标")',
        'sort|1-100': 1,
        'contentCount|10-1000': 1,
        description: '@csentence',
        'status|1': [0, 1],
        createTime: '@datetime',
        updateTime: '@datetime'
      }]
    }
  })
}

// 分类详情
export const getCategoryDetail = () => {
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      id: 1,
      categoryName: '@ctitle(2,6)',
      categoryKey: '@word(5,10)',
      icon: '@image("100x100", "@color", "#FFF", "图标")',
      sort: 1,
      contentCount: 100,
      description: '@csentence',
      status: 1,
      createTime: '@datetime',
      updateTime: '@datetime'
    }
  })
}

// 新增分类
export const addCategory = () => {
  return Mock.mock({
    code: 200,
    message: '新增成功',
    data: {
      id: '@increment'
    }
  })
}

// 更新分类
export const updateCategory = () => {
  return Mock.mock({
    code: 200,
    message: '更新成功',
    data: null
  })
}

// 删除分类
export const deleteCategory = () => {
  return Mock.mock({
    code: 200,
    message: '删除成功',
    data: null
  })
}

// 修改分类状态
export const changeCategoryStatus = () => {
  return Mock.mock({
    code: 200,
    message: '状态修改成功',
    data: null
  })
}

// 注册Mock接口
Mock.mock(/\/api\/category\/list/, 'get', getCategoryList)
Mock.mock(/\/api\/category\/detail/, 'get', getCategoryDetail)
Mock.mock(/\/api\/category\/add/, 'post', addCategory)
Mock.mock(/\/api\/category\/update/, 'put', updateCategory)
Mock.mock(/\/api\/category\/delete/, 'delete', deleteCategory)
Mock.mock(/\/api\/category\/changeStatus/, 'put', changeCategoryStatus)
