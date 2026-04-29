import request from './request'

/**
 * 菜单管理API
 */
export default {
  // 菜单列表
  getMenuList: () => request.get('/menu/list'),
  
  // 新增菜单
  addMenu: (data) => request.post('/menu/add', data),
  
  // 更新菜单
  updateMenu: (data) => request.put('/menu/update', data),
  
  // 删除菜单
  deleteMenu: (id) => request.delete(`/menu/delete/${id}`)
}
