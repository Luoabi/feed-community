import Mock from 'mockjs'

// 内容列表
export const getContentList = (params) => {
  const { page = 1, size = 10 } = params
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      [`list|${size}`]: [{
        'id|+1': (page - 1) * size + 1,
        title: '@ctitle(10,30)',
        content: '@cparagraph(3,10)',
        summary: '@csentence(20,50)',
        coverImage: '@image("400x300", "@color", "#FFF", "封面")',
        'contentType|1': ['text', 'image', 'video', 'mixed'],
        'categoryId|1-10': 1,
        categoryName: '@ctitle(2,6)',
        'authorId|1-100': 1,
        authorName: '@cname',
        authorAvatar: '@image("100x100", "@color", "#FFF", "@cname")',
        'views|100-10000': 1,
        'likes|10-1000': 1,
        'comments|5-500': 1,
        'shares|0-100': 1,
        'collects|0-200': 1,
        'status|1': ['draft', 'pending', 'published', 'rejected'],
        'isTop|1': [0, 1],
        'isHot|1': [0, 1],
        'isRecommend|1': [0, 1],
        tags: '@ctitle(2,4),@ctitle(2,4),@ctitle(2,4)',
        publishTime: '@datetime',
        createTime: '@datetime',
        updateTime: '@datetime'
      }],
      total: 100,
      page: parseInt(page),
      size: parseInt(size)
    }
  })
}

// 内容详情
export const getContentDetail = () => {
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      id: 1,
      title: '@ctitle(10,30)',
      content: '@cparagraph(10,20)',
      summary: '@csentence(20,50)',
      coverImage: '@image("800x600", "@color", "#FFF", "封面")',
      contentType: 'text',
      categoryId: 1,
      categoryName: '@ctitle(2,6)',
      authorId: 1,
      authorName: '@cname',
      authorAvatar: '@image("100x100", "@color", "#FFF", "@cname")',
      views: 1000,
      likes: 100,
      comments: 50,
      shares: 10,
      collects: 20,
      status: 'published',
      isTop: 1,
      isHot: 1,
      isRecommend: 1,
      tags: '技术,Vue3,前端',
      publishTime: '@datetime',
      createTime: '@datetime',
      updateTime: '@datetime'
    }
  })
}

// 删除内容
export const deleteContent = () => {
  return Mock.mock({
    code: 200,
    message: '删除成功',
    data: null
  })
}

// 批量删除
export const batchDeleteContent = () => {
  return Mock.mock({
    code: 200,
    message: '批量删除成功',
    data: null
  })
}

// 置顶/取消置顶
export const toggleTop = () => {
  return Mock.mock({
    code: 200,
    message: '操作成功',
    data: null
  })
}

// 推荐/取消推荐
export const toggleRecommend = () => {
  return Mock.mock({
    code: 200,
    message: '操作成功',
    data: null
  })
}

// 注册Mock接口
Mock.mock(/\/api\/content\/list/, 'get', getContentList)
Mock.mock(/\/api\/content\/detail/, 'get', getContentDetail)
Mock.mock(/\/api\/content\/delete/, 'delete', deleteContent)
Mock.mock(/\/api\/content\/batchDelete/, 'delete', batchDeleteContent)
Mock.mock(/\/api\/content\/toggleTop/, 'put', toggleTop)
Mock.mock(/\/api\/content\/toggleRecommend/, 'put', toggleRecommend)
