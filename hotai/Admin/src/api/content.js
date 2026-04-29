import request from './request'

/**
 * 内容管理API
 */
export default {
  // 内容列表
  getContentList: (params) => request.get('/content/list', { params }),
  
  // 内容详情
  getContentDetail: (id) => request.get(`/content/detail/${id}`),
  
  // 新增内容
  addContent: (data) => request.post('/content/add', data),
  
  // 更新内容
  updateContent: (data) => request.put('/content/update', data),
  
  // 删除内容
  deleteContent: (id) => request.delete(`/content/delete/${id}`),
  
  // 批量删除
  batchDelete: (ids) => request.delete('/content/batchDelete', { data: { ids } }),
  
  // 置顶/取消置顶
  toggleTop: (data) => request.put('/content/toggleTop', data),
  
  // 推荐/取消推荐
  toggleRecommend: (data) => request.put('/content/toggleRecommend', data)
}
