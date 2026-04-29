const api = require('../../api/index.js')
const { handleImageUrl, handleImageList } = require('../../utils/image.js')

Page({
  data: {
    likes: [],
    page: 1,
    size: 10,
    loading: false,
    hasMore: true
  },

  onLoad() {
    this.loadMyLikes()
  },

  onPullDownRefresh() {
    this.setData({ page: 1, hasMore: true })
    this.loadMyLikes()
    wx.stopPullDownRefresh()
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadMoreLikes()
    }
  },

  async loadMyLikes() {
    if (this.data.loading) return
    
    this.setData({ loading: true })
    try {
      const res = await api.getMyLikes({
        page: this.data.page,
        size: this.data.size
      })
      
      if (res.data) {
        const list = res.data.list.map(item => ({
          ...item,
          authorAvatar: handleImageUrl(item.authorAvatar, 'avatar'),
          images: handleImageList(item.images),
          coverImage: handleImageUrl(item.coverImage, 'cover')
        }))
        
        const likes = this.data.page === 1 
          ? list 
          : [...this.data.likes, ...list]
        
        this.setData({
          likes,
          hasMore: res.data.list.length >= this.data.size,
          loading: false
        })
      }
    } catch (e) {
      console.error('加载我的点赞失败', e)
      this.setData({ loading: false })
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      })
    }
  },

  loadMoreLikes() {
    this.setData({ page: this.data.page + 1 }, () => {
      this.loadMyLikes()
    })
  },

  onItemTap(e) {
    const { id } = e.currentTarget.dataset
    // ✅ 所有内容都统一跳转到 content/detail
    wx.navigateTo({ 
      url: `/pages/content/detail?id=${id}` 
    })
  }
})
