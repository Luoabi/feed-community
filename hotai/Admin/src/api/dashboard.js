import request from './request'

/**
 * 仪表盘API
 */
export default {
  // 统计数据
  getDashboardStats: () => request.get('/dashboard/stats'),
  
  // 最近内容
  getRecentContent: () => request.get('/dashboard/recentContent'),
  
  // 待审核内容
  getPendingAudit: () => request.get('/dashboard/pendingAudit'),
  
  // 热门内容
  getHotContent: () => request.get('/dashboard/hotContent'),
  
  // 用户增长趋势
  getUserGrowthTrend: () => request.get('/dashboard/userGrowthTrend'),
  
  // 内容发布趋势
  getContentPublishTrend: () => request.get('/dashboard/contentPublishTrend')
}
