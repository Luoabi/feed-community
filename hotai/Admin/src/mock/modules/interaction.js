import Mock from 'mockjs'

// 点赞列表
export const getLikeList = (params) => {
  const { page = 1, size = 10 } = params
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      [`list|${size}`]: [{
        'id|+1': (page - 1) * size + 1,
        'userId|1-1000': 1,
        userName: '@cname',
        userAvatar: '@image("100x100", "@color", "#FFF", "@cname")',
        'targetType|1': ['post', 'content', 'comment'],
        'targetId|1-10000': 1,
        targetTitle: '@ctitle(10,30)',
        targetAuthor: '@cname',
        likeTime: '@datetime',
        cancelTime: '@datetime',
        ip: '@ip',
        'device|1': ['iPhone 15', 'Android', 'Web', 'iPad'],
        'status|1': [0, 1]
      }],
      total: 100,
      page: parseInt(page),
      size: parseInt(size)
    }
  })
}

// 收藏列表
export const getCollectList = (params) => {
  const { page = 1, size = 10 } = params
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      [`list|${size}`]: [{
        'id|+1': (page - 1) * size + 1,
        'userId|1-1000': 1,
        userName: '@cname',
        userAvatar: '@image("100x100", "@color", "#FFF", "@cname")',
        'targetType|1': ['post', 'content'],
        'targetId|1-10000': 1,
        targetTitle: '@ctitle(10,30)',
        targetAuthor: '@cname',
        'folderId|1-10': 1,
        'folderName|1': ['默认收藏夹', '技术文章', '生活分享', '学习资料'],
        collectTime: '@datetime',
        ip: '@ip',
        'device|1': ['iPhone 15', 'Android', 'Web', 'iPad'],
        'status|1': [0, 1]
      }],
      total: 100,
      page: parseInt(page),
      size: parseInt(size)
    }
  })
}

// 分享列表
export const getShareList = (params) => {
  const { page = 1, size = 10 } = params
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      [`list|${size}`]: [{
        'id|+1': (page - 1) * size + 1,
        'userId|1-1000': 1,
        userName: '@cname',
        userAvatar: '@image("100x100", "@color", "#FFF", "@cname")',
        'targetType|1': ['post', 'content'],
        'targetId|1-10000': 1,
        targetTitle: '@ctitle(10,30)',
        targetAuthor: '@cname',
        'platform|1': ['wechat', 'moments', 'qq', 'weibo', 'link'],
        shareTime: '@datetime',
        'clickCount|0-100': 1,
        'conversionCount|0-20': 1,
        ip: '@ip',
        'device|1': ['iPhone 15', 'Android', 'Web', 'iPad']
      }],
      total: 100,
      page: parseInt(page),
      size: parseInt(size)
    }
  })
}

// 互动统计
export const getInteractionStats = () => {
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      like: {
        'todayCount|100-1000': 1,
        'totalCount|10000-100000': 1,
        'activeUsers|1000-10000': 1,
        'avgRate|1-20': 1
      },
      collect: {
        'todayCount|50-500': 1,
        'totalCount|5000-50000': 1,
        'activeUsers|500-5000': 1,
        'avgRate|1-10': 1
      },
      share: {
        'todayCount|20-200': 1,
        'totalCount|2000-20000': 1,
        'activeUsers|200-2000': 1,
        'avgRate|0.5-5': 1
      }
    }
  })
}

// 用户行为分析
export const getUserBehaviorAnalysis = () => {
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      'topLikeUsers|10': [{
        'userId|+1': 1,
        userName: '@cname',
        userAvatar: '@image("100x100")',
        'likeCount|100-1000': 1
      }],
      'topCollectUsers|10': [{
        'userId|+1': 1,
        userName: '@cname',
        userAvatar: '@image("100x100")',
        'collectCount|50-500': 1
      }],
      'topShareUsers|10': [{
        'userId|+1': 1,
        userName: '@cname',
        userAvatar: '@image("100x100")',
        'shareCount|20-200': 1
      }]
    }
  })
}

// 内容互动排行
export const getContentInteractionRank = () => {
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      'topLikeContent|10': [{
        'contentId|+1': 1,
        title: '@ctitle(10,30)',
        author: '@cname',
        'likeCount|1000-10000': 1,
        'contentType|1': ['post', 'content']
      }],
      'topCollectContent|10': [{
        'contentId|+1': 1,
        title: '@ctitle(10,30)',
        author: '@cname',
        'collectCount|500-5000': 1,
        'contentType|1': ['post', 'content']
      }],
      'topShareContent|10': [{
        'contentId|+1': 1,
        title: '@ctitle(10,30)',
        author: '@cname',
        'shareCount|200-2000': 1,
        'contentType|1': ['post', 'content']
      }]
    }
  })
}

// 注册Mock接口
Mock.mock(/\/api\/interaction\/like\/list/, 'get', getLikeList)
Mock.mock(/\/api\/interaction\/collect\/list/, 'get', getCollectList)
Mock.mock(/\/api\/interaction\/share\/list/, 'get', getShareList)
Mock.mock(/\/api\/interaction\/stats/, 'get', getInteractionStats)
Mock.mock(/\/api\/interaction\/userBehavior/, 'get', getUserBehaviorAnalysis)
Mock.mock(/\/api\/interaction\/contentRank/, 'get', getContentInteractionRank)
