import request from './request'

/**
 * 分类管理API
 */
export default {
  // 分类列表
  getCategoryList: () => request.get('/category/list'),
  
  // 分类详情
  getCategoryDetail: (id) => request.get(`/category/detail/${id}`),
  
  // 新增分类
  addCategory: (data) => request.post('/category/add', data),
  
  // 更新分类
  updateCategory: (data) => request.put('/category/update', data),
  
  // 删除分类
  deleteCategory: (id) => request.delete(`/category/delete/${id}`),
  
  // 修改状态
  changeStatus: (data) => request.put('/category/changeStatus', data)
}
