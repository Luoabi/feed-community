import request from './request'

/**
 * 用户兴趣画像API
 */
export default {
  // 获取用户兴趣标签
  getUserInterests: (userId) => request.get(`/interest/user/${userId}`),
  
  // 获取用户Top N兴趣标签
  getTopInterests: (userId, topN = 5) => request.get(`/interest/user/${userId}/top`, { params: { topN } }),
  
  // 设置用户兴趣（冷启动）
  setUserInterests: (userId, tagIds) => request.post(`/interest/user/${userId}/set`, { tagIds }),
  
  // 手动更新用户兴趣画像
  updateUserInterest: (userId) => request.post(`/interest/user/${userId}/update`),
  
  // 手动更新所有用户兴趣画像
  updateAllUserInterests: () => request.post('/interest/updateAll')
}
