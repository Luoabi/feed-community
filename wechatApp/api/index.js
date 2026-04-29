const request = require('../utils/request.js')

module.exports = {
  // ==================== 用户相关 ====================
  login: (data) => request.post('/user/login', data),
  register: (data) => request.post('/user/register', data),
  getUserInfo: () => request.get('/user/info'),
  updateUserInfo: (data) => request.put('/user/update', data),
  
  // ==================== 内容相关（统一使用 content 接口）====================
  
  // 首页 Feed 流 - 个性化内容推荐（UGC + 官方内容）
  // 查询条件：status='published' AND deleted=0
  // ✅ 使用推荐算法排序
  getFeedList: (params) => request.get('/content/list', {
    ...params,
    status: 'published',
    deleted: 0,
    isFeed: true  // ✅ 标记为Feed流，启用推荐算法
  }),
  
  // ✅ 个性化Feed流（基于用户兴趣）
  getPersonalizedFeed: (params) => request.get('/feed/personalized', params),
  
  // 社区帖子列表 - 只显示用户发布的帖子类型内容
  // 查询条件：content_type='post' AND content_source='user' AND status='published' AND deleted=0
  getPostList: (params) => request.get('/content/list', {
    ...params,
    contentType: 'post',      // ✅ 只显示帖子类型
    contentSource: 'user',
    status: 'published',
    deleted: 0
  }),
  
  // 内容详情（通用：文章、帖子都用这个）
  getContentDetail: (id) => request.get(`/content/detail/${id}`),
  
  // 我的帖子
  getMyPosts: (params) => request.get('/content/list', {
    ...params,
    contentSource: 'user',
    // authorId 会在后端通过 JWT Token 自动获取
  }),
  
  // 发布内容（用户发帖）
  // 后端接收：title, content, content_type, images, video_url, link_url, author_id, author_name
  // 存入：content 表，status='pending'（待审核），content_source='user'
  publishContent: (data) => request.post('/content/add', {
    ...data,
    contentSource: 'user',  // 标记为用户发布
    status: 'pending'       // 待审核
  }),
  
  // 删除我的内容
  deleteMyContent: (id) => request.delete(`/content/delete/${id}`),
  
  // 搜索内容
  searchContent: (params) => request.get('/content/search', params),
  
  // ==================== 互动相关 ====================
  
  // 点赞内容
  likeContent: (data) => request.post('/interaction/like', {
    contentType: 'content',
    contentId: data.contentId
  }),
  
  // 收藏内容
  collectContent: (data) => request.post('/interaction/collect', {
    contentType: 'content',
    contentId: data.contentId
  }),
  
  // 分享内容
  shareContent: (data) => request.post('/interaction/share', {
    contentType: 'content',
    contentId: data.contentId,
    platform: data.platform || 'wechat'
  }),
  
  // 检查点赞和收藏状态
  checkInteractionStatus: (contentId) => request.get('/interaction/check-status', {
    contentId: contentId
  }),
  
  // 我的点赞
  getMyLikes: (params) => request.get('/interaction/like/list', params),
  
  // 我的收藏
  getMyCollects: (params) => request.get('/interaction/collect/list', params),
  
  // 取消收藏
  cancelCollect: (id) => request.delete(`/interaction/collect/${id}`),
  
  // ==================== 评论相关 ====================
  
  // 评论列表（管理后台用）
  getCommentList: (params) => request.get('/comment/list', params),
  
  // 评论树（小程序端：一级评论+部分二级回复）
  getCommentTree: (params) => request.get('/comment/tree', {
    ...params,
    contentType: 'content'
  }),
  
  // 获取某条评论的所有回复
  getCommentReplies: (parentId, params) => request.get(`/comment/replies/${parentId}`, params),
  
  // 添加评论
  addComment: (data) => request.post('/comment/add', {
    ...data,
    contentType: 'content'
  }),
  
  // 删除评论
  deleteComment: (id) => request.delete(`/comment/delete/${id}`),
  
  // 点赞评论
  likeComment: (commentId) => request.post('/comment/like', {
    commentId: commentId
  }),
  
  // 检查评论点赞状态
  checkCommentLikeStatus: (commentId) => request.get('/comment/check-like-status', {
    commentId: commentId
  }),
  
  // ==================== 分类和标签 ====================
  
  // 分类列表
  getCategoryList: () => request.get('/category/list'),
  
  // 标签列表
  getTagList: () => request.get('/tag/list'),
  
  // ==================== 用户中心相关 ====================
  
  // 我的发布
  getMyPosts: (params) => request.get('/user-center/my-posts', params),
  
  // 我的点赞
  getMyLikes: (params) => request.get('/user-center/my-likes', params),
  
  // 我的收藏
  getMyCollects: (params) => request.get('/user-center/my-collects', params),
  
  // 删除我的发布
  deleteMyPost: (id) => request.delete(`/user-center/my-posts/${id}`),
  
  // 取消收藏
  cancelCollect: (id) => request.delete(`/user-center/my-collects/${id}`),
  
  // 我的统计数据
  getMyStats: () => request.get('/user-center/my-stats'),
  
  // ==================== 统计相关 ====================
  
  // 统计数据
  getDashboardStats: () => request.get('/dashboard/stats'),
  
  // ==================== 用户兴趣相关 ====================
  
  // 获取用户兴趣标签
  getUserInterests: (userId) => request.get(`/interest/user/${userId}`),
  
  // 获取用户Top N兴趣标签
  getTopInterests: (userId, topN = 5) => request.get(`/interest/user/${userId}/top`, { topN }),
  
  // 设置用户兴趣（冷启动）
  setUserInterests: (userId, tagIds) => request.post(`/interest/user/${userId}/set`, { tagIds }),
  
  // ==================== 行为追踪相关 ====================
  
  // 记录用户行为
  trackBehavior: (data) => request.post('/behavior/track', data),
  
  // 记录浏览行为
  trackView: (data) => request.post('/behavior/view', data),
  
  // 记录点击行为
  trackClick: (data) => request.post('/behavior/click', data)
}
