import request from './request'

/**
 * 用户行为追踪API
 */
export default {
  // 记录用户行为
  trackBehavior: (data) => request.post('/behavior/track', data),
  
  // 记录浏览行为
  trackView: (data) => request.post('/behavior/view', data),
  
  // 记录点击行为
  trackClick: (data) => request.post('/behavior/click', data)
}
