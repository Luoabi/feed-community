import request from './request'

/**
 * 角色管理API
 */
export default {
  // 角色列表
  getRoleList: (params) => request.get('/role/list', { params }),
  
  // 角色详情
  getRoleDetail: (id) => request.get(`/role/detail/${id}`),
  
  // 新增角色
  addRole: (data) => request.post('/role/add', data),
  
  // 更新角色
  updateRole: (data) => request.put('/role/update', data),
  
  // 删除角色
  deleteRole: (id) => request.delete(`/role/delete/${id}`),
  
  // 分配权限
  assignPermission: (data) => request.post('/role/assignPermission', data),
  
  // 修改状态
  changeStatus: (data) => request.put('/role/changeStatus', data)
}
