import Mock from 'mockjs'

// 首页统计数据
export const getDashboardStats = () => {
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      userStats: {
        'total|1000-10000': 5000,
        'todayNew|10-100': 50,
        'activeToday|100-1000': 500,
        'activeRate|50-100': 80
      },
      contentStats: {
        'total|5000-50000': 20000,
        'todayNew|50-500': 200,
        'pendingAudit|10-100': 50,
        'published|1000-10000': 5000
      },
      postStats: {
        'total|3000-30000': 15000,
        'todayNew|30-300': 150,
        'hot|100-1000': 500,
        'essence|50-500': 200
      },
      commentStats: {
        'total|10000-100000': 50000,
        'todayNew|100-1000': 500,
        'avgPerContent|5-50': 20
      },
      viewStats: {
        'todayViews|10000-100000': 50000,
        'todayLikes|1000-10000': 5000,
        'todayShares|100-1000': 500,
        'todayCollects|100-1000': 300
      }
    }
  })
}

// 最近内容
export const getRecentContent = () => {
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      'list|10': [{
        'id|+1': 1,
        title: '@ctitle(10,30)',
        author: '@cname',
        'status|1': ['published', 'pending', 'draft'],
        'views|100-10000': 1000,
        createTime: '@datetime'
      }]
    }
  })
}

// 待审核内容
export const getPendingAudit = () => {
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      'list|5': [{
        'id|+1': 1,
        title: '@ctitle(10,30)',
        author: '@cname',
        'contentType|1': ['text', 'image', 'video'],
        submitTime: '@datetime'
      }]
    }
  })
}

// 热门内容
export const getHotContent = () => {
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      'list|10': [{
        'id|+1': 1,
        title: '@ctitle(10,30)',
        author: '@cname',
        'views|1000-50000': 10000,
        'likes|100-5000': 1000,
        'score|60-100': 85
      }]
    }
  })
}

// 用户增长趋势（最近7天）
export const getUserGrowthTrend = () => {
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      'dates|7': ['@date("MM-dd")'],
      'newUsers|7': ['@integer(10, 100)'],
      'activeUsers|7': ['@integer(100, 1000)']
    }
  })
}

// 内容发布趋势（最近7天）
export const getContentPublishTrend = () => {
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      'dates|7': ['@date("MM-dd")'],
      'contents|7': ['@integer(50, 500)'],
      'posts|7': ['@integer(30, 300)']
    }
  })
}

// 注册Mock接口
Mock.mock(/\/api\/dashboard\/stats/, 'get', getDashboardStats)
Mock.mock(/\/api\/dashboard\/recentContent/, 'get', getRecentContent)
Mock.mock(/\/api\/dashboard\/pendingAudit/, 'get', getPendingAudit)
Mock.mock(/\/api\/dashboard\/hotContent/, 'get', getHotContent)
Mock.mock(/\/api\/dashboard\/userGrowthTrend/, 'get', getUserGrowthTrend)
Mock.mock(/\/api\/dashboard\/contentPublishTrend/, 'get', getContentPublishTrend)
