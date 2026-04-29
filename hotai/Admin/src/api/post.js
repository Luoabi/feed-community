import request from './request'

/**
 * 帖子管理API
 */
export default {
  // 帖子列表
  getPostList: (params) => request.get('/post/list', { params }),
  
  // 帖子详情
  getPostDetail: (id) => request.get(`/post/detail/${id}`),
  
  // 新增帖子
  addPost: (data) => request.post('/post/add', data),
  
  // 更新帖子
  updatePost: (data) => request.put('/post/update', data),
  
  // 删除帖子
  deletePost: (id) => request.delete(`/post/delete/${id}`),
  
  // 批量删除
  batchDelete: (ids) => request.delete('/post/batchDelete', { data: { ids } }),
  
  // 置顶/取消置顶
  toggleTop: (data) => request.put('/post/toggleTop', data),
  
  // 精华/取消精华
  toggleEssence: (data) => request.put('/post/toggleEssence', data)
}
