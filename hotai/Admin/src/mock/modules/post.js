import Mock from 'mockjs'

// 帖子列表
export const getPostList = (params) => {
  const { page = 1, size = 10 } = params
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      [`list|${size}`]: [{
        'id|+1': (page - 1) * size + 1,
        title: '@ctitle(10,30)',
        content: '@cparagraph(3,10)',
        'postType|1': ['text', 'image', 'video', 'link'],
        'images|2-5': ['@image("400x300", "@color", "#FFF", "图片")'],
        videoUrl: '@url',
        linkUrl: '@url',
        'authorId|1-100': 1,
        authorName: '@cname',
        authorAvatar: '@image("100x100", "@color", "#FFF", "@cname")',
        'views|100-10000': 1,
        'likes|10-1000': 1,
        'comments|5-500': 1,
        'shares|0-100': 1,
        'collects|0-200': 1,
        'isTop|1': [0, 1],
        'isEssence|1': [0, 1],
        'isHot|1': [0, 1],
        'status|1': [0, 1],
        createTime: '@datetime',
        updateTime: '@datetime'
      }],
      total: 100,
      page: parseInt(page),
      size: parseInt(size)
    }
  })
}

// 帖子详情
export const getPostDetail = () => {
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      id: 1,
      title: '@ctitle(10,30)',
      content: '@cparagraph(5,15)',
      postType: 'text',
      'images|3': ['@image("800x600", "@color", "#FFF", "图片")'],
      authorId: 1,
      authorName: '@cname',
      authorAvatar: '@image("100x100", "@color", "#FFF", "@cname")',
      views: 1000,
      likes: 100,
      comments: 50,
      shares: 10,
      collects: 20,
      isTop: 1,
      isEssence: 1,
      isHot: 1,
      status: 1,
      createTime: '@datetime',
      updateTime: '@datetime'
    }
  })
}

// 删除帖子
export const deletePost = () => {
  return Mock.mock({
    code: 200,
    message: '删除成功',
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

// 设置精华/取消精华
export const toggleEssence = () => {
  return Mock.mock({
    code: 200,
    message: '操作成功',
    data: null
  })
}

// 批量删除
export const batchDeletePost = () => {
  return Mock.mock({
    code: 200,
    message: '批量删除成功',
    data: null
  })
}

// 注册Mock接口
Mock.mock(/\/api\/post\/list/, 'get', getPostList)
Mock.mock(/\/api\/post\/detail/, 'get', getPostDetail)
Mock.mock(/\/api\/post\/delete/, 'delete', deletePost)
Mock.mock(/\/api\/post\/toggleTop/, 'put', toggleTop)
Mock.mock(/\/api\/post\/toggleEssence/, 'put', toggleEssence)
Mock.mock(/\/api\/post\/batchDelete/, 'delete', batchDeletePost)
