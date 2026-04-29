import request from './request'

/**
 * 操作日志API
 */
export default {
  // 日志列表
  getLogList: (params) => request.get('/log/list', { params }),
  
  // 删除日志
  deleteLog: (id) => request.delete(`/log/delete/${id}`),
  
  // 清空日志
  clearLog: () => request.delete('/log/clear'),
  
  // 导出日志
  exportLog: (params) => request.get('/log/export', { params })
}
