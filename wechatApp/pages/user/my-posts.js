const api = require('../../api/index.js')
const { handleImageUrl, handleImageList } = require('../../utils/image.js')

Page({
  data: {
    posts: [],
    page: 1,
    size: 10,
    loading: false,
    hasMore: true,
    statusFilter: '',  // ✅ 状态筛选
    totalCount: 0,
    pendingCount: 0,
    publishedCount: 0,
    rejectedCount: 0
  },

  onLoad() {
    this.loadMyPosts()
    this.loadStatusCounts()
  },

  onPullDownRefresh() {
    this.setData({ page: 1, hasMore: true })
    this.loadMyPosts()
    this.loadStatusCounts()
    wx.stopPullDownRefresh()
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadMorePosts()
    }
  },

  // ✅ 切换状态筛选
  onFilterChange(e) {
    const status = e.currentTarget.dataset.status
    this.setData({ 
      statusFilter: status,
      page: 1,
      hasMore: true
    })
    this.loadMyPosts()
  },

  // ✅ 加载各状态数量
  async loadStatusCounts() {
    try {
      // 加载全部
      const allRes = await api.getMyPosts({ page: 1, size: 1 })
      // 加载待审核
      const pendingRes = await api.getMyPosts({ page: 1, size: 1, status: 'pending' })
      // 加载已发布
      const publishedRes = await api.getMyPosts({ page: 1, size: 1, status: 'published' })
      // 加载已拒绝
      const rejectedRes = await api.getMyPosts({ page: 1, size: 1, status: 'rejected' })
      
      this.setData({
        totalCount: allRes.data.total || 0,
        pendingCount: pendingRes.data.total || 0,
        publishedCount: publishedRes.data.total || 0,
        rejectedCount: rejectedRes.data.total || 0
      })
    } catch (e) {
      console.error('加载状态统计失败', e)
    }
  },

  async loadMyPosts() {
    if (this.data.loading) return
    
    this.setData({ loading: true })
    try {
      // ✅ 使用新的 API，支持状态筛选
      const params = {
        page: this.data.page,
        size: this.data.size
      }
      
      // 如果有状态筛选，添加到参数中
      if (this.data.statusFilter) {
        params.status = this.data.statusFilter
      }
      
      const res = await api.getMyPosts(params)
      
      if (res.data) {
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
      console.error('加载我的帖子失败', e)
      this.setData({ loading: false })
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      })
    }
  },

  loadMorePosts() {
    this.setData({ page: this.data.page + 1 }, () => {
      this.loadMyPosts()
    })
  },

  onPostTap(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/community/post-detail?id=${id}`
    })
  },

  async onDeletePost(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '提示',
      content: '确定要删除这条帖子吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            // ✅ 使用新的 API
            await api.deleteMyPost(id)
            wx.showToast({
              title: '删除成功',
              icon: 'success'
            })
            this.setData({ page: 1 })
            this.loadMyPosts()
            this.loadStatusCounts()  // ✅ 刷新统计数据
          } catch (e) {
            console.error('删除失败', e)
            wx.showToast({
              title: '删除失败',
              icon: 'none'
            })
          }
        }
      }
    })
  }
})
