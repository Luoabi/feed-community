import Mock from 'mockjs'

// 获取Feed流配置
export const getFeedConfig = () => {
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      'algorithm|1': ['time', 'hot', 'personalized'],
      'pageSize|10-50': 20,
      'refreshInterval|1-60': 5,
      'enableCache|1': [true, false],
      'cacheExpire|60-600': 300,
      'hotWeight|0.1-1': 0.6,
      'timeWeight|0.1-1': 0.4,
      'personalWeight|0.1-1': 0.5,
      updateTime: '@datetime'
    }
  })
}

// 更新Feed流配置
export const updateFeedConfig = () => {
  return Mock.mock({
    code: 200,
    message: '配置更新成功',
    data: null
  })
}

// Feed流统计
export const getFeedStats = () => {
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      'todayPush|1000-50000': 12345,
      'avgClickRate|1-20': 8.5,
      'avgStayTime|1-10': 3.2,
      'contentCoverage|50-100': 85,
      'activeUsers|1000-10000': 5678,
      'totalPush|10000-100000': 50000,
      'totalClick|5000-50000': 25000,
      updateTime: '@datetime'
    }
  })
}

// Feed流内容列表
export const getFeedContentList = (params) => {
  const { page = 1, size = 10 } = params
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      [`list|${size}`]: [{
        'id|+1': (page - 1) * size + 1,
        'contentId|1-1000': 1,
        title: '@ctitle(10,30)',
        coverImage: '@image("400x300", "@color", "#FFF", "封面")',
        'contentType|1': ['post', 'content'],
        authorName: '@cname',
        'score|60-100': 1,
        'hotScore|60-100': 1,
        'timeScore|60-100': 1,
        'personalScore|60-100': 1,
        'pushCount|100-10000': 1,
        'clickCount|50-5000': 1,
        'clickRate|1-20': 1,
        pushTime: '@datetime',
        createTime: '@datetime'
      }],
      total: 100,
      page: parseInt(page),
      size: parseInt(size)
    }
  })
}

// 手动推送到Feed流
export const pushToFeed = () => {
  return Mock.mock({
    code: 200,
    message: '推送成功',
    data: null
  })
}

// 从Feed流移除
export const removeFromFeed = () => {
  return Mock.mock({
    code: 200,
    message: '移除成功',
    data: null
  })
}

// 注册Mock接口
Mock.mock(/\/api\/feed\/config/, 'get', getFeedConfig)
Mock.mock(/\/api\/feed\/updateConfig/, 'put', updateFeedConfig)
Mock.mock(/\/api\/feed\/stats/, 'get', getFeedStats)
Mock.mock(/\/api\/feed\/contentList/, 'get', getFeedContentList)
Mock.mock(/\/api\/feed\/push/, 'post', pushToFeed)
Mock.mock(/\/api\/feed\/remove/, 'delete', removeFromFeed)
