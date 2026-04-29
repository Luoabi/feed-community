import request from './request'

/**
 * 互动数据API
 */
export default {
  // 点赞列表
  getLikeList: (params) => request.get('/interaction/like/list', { params }),
  
  // 收藏列表
  getCollectList: (params) => request.get('/interaction/collect/list', { params }),
  
  // 分享列表
  getShareList: (params) => request.get('/interaction/share/list', { params }),
  
  // 互动统计
  getInteractionStats: () => request.get('/interaction/stats'),
  
  // 用户行为分析
  getUserBehavior: () => request.get('/interaction/userBehavior'),
  
  // 内容互动排行
  getContentRank: () => request.get('/interaction/contentRank')
}
