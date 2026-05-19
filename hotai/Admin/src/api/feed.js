import request from './request'

/**
 * Feed流管理API
 */
export default {
  // 获取个性化Feed流
  getPersonalizedFeed: (params) => request.get('/feed/personalized', { params }),
  
  // 获取配置
  getFeedConfig: () => request.get('/feed/config'),
  
  // 更新配置
  updateFeedConfig: (data) => request.put('/feed/updateConfig', data),
  
  // Feed统计
  getFeedStats: () => request.get('/feed/stats'),
  
  // Feed内容列表
  getFeedContentList: (params) => request.get('/feed/contentList', { params }),
  
  // 推送到Feed
  pushToFeed: (data) => request.post('/feed/push', data),
  
  // 从Feed移除
  removeFromFeed: (id) => request.delete(`/feed/remove/${id}`),
  
  // 调整热度分数
  updateHotScore: (data) => request.put('/feed/updateHotScore', data)
}
