const api = require('../../api/index.js')
const { handleImageUrl, handleImageList } = require('../../utils/image.js')

Page({
  data: {
    posts: [],
    page: 1,
    size: 10,
    loading: false,
    hasMore: true
  },

  onLoad() {
    this.loadPosts()
  },
  
  // ✅ 页面显示时刷新数据（从详情页返回时）
  onShow() {
    // 如果不是第一次加载，刷新当前页数据
    const pages = getCurrentPages()
    const currentPage = pages[pages.length - 1]
    
    // 判断是否从其他页面返回（不是首次加载）
    if (currentPage.data.posts && currentPage.data.posts.length > 0) {
      console.log('从详情页返回，刷新数据')
      this.refreshCurrentPage()
    }
  },

  onPullDownRefresh() {
    this.setData({ 
      page: 1, 
      hasMore: true,
      posts: []  // 清空现有内容
    })
    this.loadPosts()
    wx.stopPullDownRefresh()
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadMorePosts()
    }
  },

  async loadPosts() {
    if (this.data.loading) return
    
    this.setData({ loading: true })
    try {
      // ✅ 使用 getPostList - 只获取用户发布的帖子
      // 查询条件：content_source='user' AND status='published' AND deleted=0
      const res = await api.getPostList({
        page: this.data.page,
        size: this.data.size
        // contentSource='user', status='published', deleted=0 已在 api.getPostList 中自动添加
      })
      
      if (res.data) {
        // 处理图片 URL
        const list = res.data.list.map(item => ({
          ...item,
          authorAvatar: handleImageUrl(item.authorAvatar, 'avatar'),
          images: handleImageList(item.images)
        }))
        
        const posts = this.data.page === 1 
          ? list 
          : [...this.data.posts, ...list]
        
        this.setData({
          posts,
          hasMore: res.data.list.length >= this.data.size,
          loading: false
        })
      }
    } catch (e) {
      console.error('加载帖子失败', e)
      this.setData({ loading: false })
    }
  },

  loadMorePosts() {
    this.setData({ page: this.data.page + 1 }, () => {
      this.loadPosts()
    })
  },
  
  // ✅ 刷新当前页数据（保持分页状态，更新点赞、评论等数据）
  async refreshCurrentPage() {
    try {
      const currentPage = this.data.page
      const totalSize = this.data.size * currentPage
      
      const res = await api.getPostList({
        page: 1,
        size: totalSize  // 获取当前所有页的数据
      })
      
      if (res.data && res.data.list) {
        // 处理图片 URL
        const list = res.data.list.map(item => ({
          ...item,
          authorAvatar: handleImageUrl(item.authorAvatar, 'avatar'),
          images: handleImageList(item.images)
        }))
        
        // 只更新已加载的内容数量
        const updatedPosts = list.slice(0, this.data.posts.length)
        
        this.setData({
          posts: updatedPosts,
          hasMore: res.data.list.length >= totalSize
        })
        
        console.log('数据刷新成功，更新了', updatedPosts.length, '条帖子')
      }
    } catch (e) {
      console.error('刷新数据失败', e)
    }
  },

  onPostTap(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/community/post-detail?id=${id}`
    })
  }
})
