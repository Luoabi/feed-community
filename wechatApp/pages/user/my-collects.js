const api = require('../../api/index.js')
const { handleImageUrl, handleImageList } = require('../../utils/image.js')

Page({
  data: {
    collects: [],
    page: 1,
    size: 10,
    loading: false,
    hasMore: true
  },

  onLoad() {
    this.loadMyCollects()
  },

  onPullDownRefresh() {
    this.setData({ page: 1, hasMore: true })
    this.loadMyCollects()
    wx.stopPullDownRefresh()
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadMoreCollects()
    }
  },

  async loadMyCollects() {
    if (this.data.loading) return
    
    this.setData({ loading: true })
    try {
      const res = await api.getMyCollects({
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
        
        const collects = this.data.page === 1 
          ? list 
          : [...this.data.collects, ...list]
        
        this.setData({
          collects,
          hasMore: res.data.list.length >= this.data.size,
          loading: false
        })
      }
    } catch (e) {
      console.error('加载我的收藏失败', e)
      this.setData({ loading: false })
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      })
    }
  },

  loadMoreCollects() {
    this.setData({ page: this.data.page + 1 }, () => {
      this.loadMyCollects()
    })
  },

  onItemTap(e) {
    const { id } = e.currentTarget.dataset
    // ✅ 所有内容都统一跳转到 content/detail
    wx.navigateTo({ 
      url: `/pages/content/detail?id=${id}` 
    })
  },

  async onCancelCollect(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '提示',
      content: '确定要取消收藏吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await api.cancelCollect(id)
            wx.showToast({
              title: '已取消收藏',
              icon: 'success'
            })
            this.setData({ page: 1 })
            this.loadMyCollects()
          } catch (e) {
            console.error('取消收藏失败', e)
            wx.showToast({
              title: '操作失败',
              icon: 'none'
            })
          }
        }
      }
    })
  }
})
