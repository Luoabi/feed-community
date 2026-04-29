import Mock from 'mockjs'

// 待审核列表
export const getAuditList = (params) => {
  const { page = 1, size = 10 } = params
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      [`list|${size}`]: [{
        'id|+1': (page - 1) * size + 1,
        'contentId|1-1000': 1,
        title: '@ctitle(10,30)',
        content: '@cparagraph(2,5)',
        coverImage: '@image("400x300", "@color", "#FFF", "封面")',
        'contentType|1': ['text', 'image', 'video', 'mixed'],
        'authorId|1-100': 1,
        authorName: '@cname',
        authorAvatar: '@image("100x100", "@color", "#FFF", "@cname")',
        'categoryId|1-10': 1,
        categoryName: '@ctitle(2,6)',
        'auditStatus|1': ['pending', 'pass', 'reject'],
        auditReason: '@csentence',
        'auditorId|1-10': 1,
        auditorName: '@cname',
        submitTime: '@datetime',
        auditTime: '@datetime',
        createTime: '@datetime'
      }],
      total: 50,
      page: parseInt(page),
      size: parseInt(size)
    }
  })
}

// 审核内容
export const auditContent = () => {
  return Mock.mock({
    code: 200,
    message: '审核成功',
    data: null
  })
}

// 批量审核
export const batchAudit = () => {
  return Mock.mock({
    code: 200,
    message: '批量审核成功',
    data: null
  })
}

// 审核统计
export const getAuditStats = () => {
  return Mock.mock({
    code: 200,
    message: '成功',
    data: {
      'pendingCount|10-100': 1,
      'passCount|100-1000': 1,
      'rejectCount|10-100': 1,
      'todayAuditCount|20-200': 1,
      'avgAuditTime|1-10': 1
    }
  })
}

// 注册Mock接口
Mock.mock(/\/api\/audit\/list/, 'get', getAuditList)
Mock.mock(/\/api\/audit\/audit/, 'post', auditContent)
Mock.mock(/\/api\/audit\/batchAudit/, 'post', batchAudit)
Mock.mock(/\/api\/audit\/stats/, 'get', getAuditStats)
