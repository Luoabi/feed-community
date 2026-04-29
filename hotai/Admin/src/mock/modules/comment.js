import Mock from 'mockjs'

// 评论列表
export const getCommentList = (params) => {
  const { page = 1, size = 10 } = params
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      [`list|${size}`]: [{
        'id|+1': (page - 1) * size + 1,
        'contentType|1': ['post', 'content'],
        'contentId|1-1000': 1,
        contentTitle: '@ctitle(10,20)',
        'parentId|0-10': 0,
        'rootId|0-10': 0,
        'userId|1-100': 1,
        userName: '@cname',
        userAvatar: '@image("100x100", "@color", "#FFF", "@cname")',
        content: '@csentence(10,50)',
        'likes|0-100': 1,
        'replyCount|0-20': 1,
        'status|1': [0, 1],
        'isTop|1': [0, 1],
        createTime: '@datetime',
        updateTime: '@datetime'
      }],
      total: 100,
      page: parseInt(page),
      size: parseInt(size)
    }
  })
}

// 评论详情
export const getCommentDetail = () => {
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      id: 1,
      contentType: 'post',
      contentId: 100,
      contentTitle: '@ctitle(10,20)',
      parentId: 0,
      rootId: 0,
      userId: 1,
      userName: '@cname',
      userAvatar: '@image("100x100", "@color", "#FFF", "@cname")',
      content: '@csentence(10,50)',
      likes: 10,
      replyCount: 5,
      status: 1,
      isTop: 0,
      createTime: '@datetime',
      updateTime: '@datetime',
      'replies|5': [{
        'id|+1': 100,
        parentId: 1,
        rootId: 1,
        'userId|1-100': 1,
        userName: '@cname',
        userAvatar: '@image("100x100", "@color", "#FFF", "@cname")',
        content: '@csentence(10,30)',
        'likes|0-50': 1,
        createTime: '@datetime'
      }]
    }
  })
}

// 删除评论
export const deleteComment = () => {
  return Mock.mock({
    code: 200,
    message: '删除成功',
    data: null
  })
}

// 批量删除
export const batchDeleteComment = () => {
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

// 修改评论状态
export const changeCommentStatus = () => {
  return Mock.mock({
    code: 200,
    message: '状态修改成功',
    data: null
  })
}

// 注册Mock接口
Mock.mock(/\/api\/comment\/list/, 'get', getCommentList)
Mock.mock(/\/api\/comment\/detail/, 'get', getCommentDetail)
Mock.mock(/\/api\/comment\/delete/, 'delete', deleteComment)
Mock.mock(/\/api\/comment\/batchDelete/, 'delete', batchDeleteComment)
Mock.mock(/\/api\/comment\/toggleTop/, 'put', toggleTop)
Mock.mock(/\/api\/comment\/changeStatus/, 'put', changeCommentStatus)
