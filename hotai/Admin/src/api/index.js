/**
 * API统一导出
 * 所有API模块集中管理
 */

// 导入所有API模块
import userApi from './user'
import roleApi from './role'
import menuApi from './menu'
import deptApi from './dept'
import logApi from './log'
import contentApi from './content'
import auditApi from './audit'
import categoryApi from './category'
import postApi from './post'
import commentApi from './comment'
import feedApi from './feed'
import interactionApi from './interaction'
import tagApi from './tag'
import dashboardApi from './dashboard'

// 统一导出
export default {
  user: userApi,
  role: roleApi,
  menu: menuApi,
  dept: deptApi,
  log: logApi,
  content: contentApi,
  audit: auditApi,
  category: categoryApi,
  post: postApi,
  comment: commentApi,
  feed: feedApi,
  interaction: interactionApi,
  tag: tagApi,
  dashboard: dashboardApi
}

// 也可以按模块导出，方便按需引入
export {
  userApi,
  roleApi,
  menuApi,
  deptApi,
  logApi,
  contentApi,
  auditApi,
  categoryApi,
  postApi,
  commentApi,
  feedApi,
  interactionApi,
  tagApi,
  dashboardApi
}
