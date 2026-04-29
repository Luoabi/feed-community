import request from './request'

/**
 * 用户管理API
 */
export default {
  // 登录
  login: (data) => request.post('/user/login', data),
  
  // 获取用户信息
  getUserInfo: () => request.get('/user/info'),
  
  // 用户列表
  getUserList: (params) => request.get('/user/list', { params }),
  
  // 新增用户
  addUser: (data) => request.post('/user/add', data),
  
  // 更新用户
  updateUser: (data) => request.put('/user/update', data),
  
  // 删除用户
  deleteUser: (id) => request.delete(`/user/delete/${id}`),
  
  // 重置密码
  resetPassword: (data) => request.put('/user/resetPassword', data),
  
  // 修改状态
  changeStatus: (data) => request.put('/user/changeStatus', data),
  
  // 修改密码
  changePassword: (data) => request.put('/user/changePassword', data),
  
  // 登出
  logout: () => request.post('/user/logout')
}
