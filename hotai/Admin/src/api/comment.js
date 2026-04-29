import request from './request'

/**
 * 评论管理API
 */
export default {
  // 评论列表
  getCommentList: (params) => request.get('/comment/list', { params }),
  
  // 评论详情
  getCommentDetail: (id) => request.get(`/comment/detail/${id}`),
  
  // 删除评论
  deleteComment: (id) => request.delete(`/comment/delete/${id}`),
  
  // 批量删除
  batchDelete: (data) => request.delete('/comment/batchDelete', { data }),
  
  // 批量通过审核
  batchApprove: (data) => request.put('/comment/batchApprove', data),
  
  // 置顶/取消置顶
  toggleTop: (data) => request.put('/comment/toggleTop', data),
  
  // 修改状态
  changeStatus: (data) => request.put('/comment/changeStatus', data)
}
