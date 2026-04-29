import request from './request'

/**
 * 标签管理API
 */
export default {
  // 标签列表
  getTagList: () => request.get('/tag/list'),
  
  // 标签详情
  getTagDetail: (id) => request.get(`/tag/detail/${id}`),
  
  // 新增标签
  addTag: (data) => request.post('/tag/add', data),
  
  // 更新标签
  updateTag: (data) => request.put('/tag/update', data),
  
  // 删除标签
  deleteTag: (id) => request.delete(`/tag/delete/${id}`),
  
  // 用户标签列表
  getUserTagList: (params) => request.get('/tag/userList', { params }),
  
  // 分配标签
  assignTag: (data) => request.post('/tag/assign', data),
  
  // 移除标签
  removeTag: (data) => request.delete('/tag/remove', { data }),
  
  // 批量分配
  batchAssign: (data) => request.post('/tag/batchAssign', data),
  
  // 标签统计
  getTagStats: () => request.get('/tag/stats'),
  
  // 标签推荐
  getTagRecommend: (userId) => request.get(`/tag/recommend/${userId}`),
  
  // 用户兴趣分析
  getUserInterest: (userId) => request.get(`/tag/userInterest/${userId}`)
}
