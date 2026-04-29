import request from './request'

/**
 * 内容审核API
 */
export default {
  // 审核列表（待审核的内容）
  getAuditList: (params) => request.get('/content/list', { params }),
  
  // 通过审核
  approveContent: (data) => request.put('/content/approve', data),
  
  // 拒绝审核
  rejectContent: (data) => request.put('/content/reject', data),
  
  // 批量通过审核
  batchApprove: (data) => request.put('/content/batchApprove', data),
  
  // 批量拒绝审核
  batchReject: (data) => request.put('/content/batchReject', data),
  
  // 审核统计
  getAuditStats: () => request.get('/content/auditStats')
}
