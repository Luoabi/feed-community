import request from './request'

/**
 * 部门管理API
 */
export default {
  // 部门列表
  getDeptList: () => request.get('/dept/list'),
  
  // 新增部门
  addDept: (data) => request.post('/dept/add', data),
  
  // 更新部门
  updateDept: (data) => request.put('/dept/update', data),
  
  // 删除部门
  deleteDept: (id) => request.delete(`/dept/delete/${id}`)
}
