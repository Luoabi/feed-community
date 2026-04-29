import Mock from 'mockjs'

// 标签列表
export const getTagList = () => {
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      'list|20': [{
        'id|+1': 1,
        tagName: '@ctitle(2,6)',
        tagKey: '@word(5,10)',
        'tagType|1': ['interest', 'content', 'behavior', 'attribute'],
        description: '@csentence',
        'userCount|10-1000': 1,
        'contentCount|50-5000': 1,
        'sort|1-100': 1,
        'status|1': [0, 1],
        createTime: '@datetime',
        updateTime: '@datetime'
      }]
    }
  })
}

// 标签详情
export const getTagDetail = () => {
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      id: 1,
      tagName: '@ctitle(2,6)',
      tagKey: '@word(5,10)',
      tagType: 'interest',
      description: '@csentence',
      userCount: 100,
      contentCount: 500,
      sort: 1,
      status: 1,
      createTime: '@datetime',
      updateTime: '@datetime'
    }
  })
}

// 新增标签
export const addTag = () => {
  return Mock.mock({
    code: 200,
    message: '新增成功',
    data: {
      id: '@increment'
    }
  })
}

// 更新标签
export const updateTag = () => {
  return Mock.mock({
    code: 200,
    message: '更新成功',
    data: null
  })
}

// 删除标签
export const deleteTag = () => {
  return Mock.mock({
    code: 200,
    message: '删除成功',
    data: null
  })
}

// 用户标签列表
export const getUserTagList = (params) => {
  const { page = 1, size = 10 } = params
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      [`list|${size}`]: [{
        'userId|+1': (page - 1) * size + 1,
        userName: '@cname',
        userAvatar: '@image("100x100", "@color", "#FFF", "@cname")',
        email: '@email',
        phone: /^1[3-9]\d{9}$/,
        'tags|2-5': [{
          'id|1-20': 1,
          tagName: '@ctitle(2,6)',
          'tagType|1': ['interest', 'content', 'behavior', 'attribute']
        }],
        updateTime: '@datetime'
      }],
      total: 100,
      page: parseInt(page),
      size: parseInt(size)
    }
  })
}

// 分配用户标签
export const assignUserTag = () => {
  return Mock.mock({
    code: 200,
    message: '分配成功',
    data: null
  })
}

// 移除用户标签
export const removeUserTag = () => {
  return Mock.mock({
    code: 200,
    message: '移除成功',
    data: null
  })
}

// 批量分配标签
export const batchAssignTag = () => {
  return Mock.mock({
    code: 200,
    message: '批量分配成功',
    data: null
  })
}

// 标签统计
export const getTagStats = () => {
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      'totalTags|50-200': 1,
      'activeTags|30-150': 1,
      'totalUsers|1000-10000': 1,
      'avgTagsPerUser|2-8': 1,
      'topTags|10': [{
        'id|+1': 1,
        tagName: '@ctitle(2,6)',
        'userCount|100-1000': 1
      }]
    }
  })
}

// 标签推荐
export const getTagRecommend = () => {
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      'recommendTags|10': [{
        'id|+1': 1,
        tagName: '@ctitle(2,6)',
        'tagType|1': ['interest', 'content', 'behavior', 'attribute'],
        'score|60-100': 1,
        reason: '@csentence'
      }]
    }
  })
}

// 用户兴趣分析
export const getUserInterestAnalysis = () => {
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      'interestDistribution|5': [{
        tagName: '@ctitle(2,6)',
        'percentage|10-30': 1
      }],
      'behaviorPattern|5': [{
        behavior: '@ctitle(4,8)',
        'frequency|10-100': 1
      }],
      'contentPreference|5': [{
        category: '@ctitle(2,6)',
        'score|60-100': 1
      }]
    }
  })
}

// 注册Mock接口
Mock.mock(/\/api\/tag\/list/, 'get', getTagList)
Mock.mock(/\/api\/tag\/detail/, 'get', getTagDetail)
Mock.mock(/\/api\/tag\/add/, 'post', addTag)
Mock.mock(/\/api\/tag\/update/, 'put', updateTag)
Mock.mock(/\/api\/tag\/delete/, 'delete', deleteTag)
Mock.mock(/\/api\/tag\/userList/, 'get', getUserTagList)
Mock.mock(/\/api\/tag\/assign/, 'post', assignUserTag)
Mock.mock(/\/api\/tag\/remove/, 'delete', removeUserTag)
Mock.mock(/\/api\/tag\/batchAssign/, 'post', batchAssignTag)
Mock.mock(/\/api\/tag\/stats/, 'get', getTagStats)
Mock.mock(/\/api\/tag\/recommend/, 'get', getTagRecommend)
Mock.mock(/\/api\/tag\/userInterest/, 'get', getUserInterestAnalysis)
