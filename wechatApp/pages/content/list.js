const api = require('../../api/index.js')
const { handleImageUrl } = require('../../utils/image.js')

Page({
  data: {
    contents: [],
    page: 1,
    size: 10,
    loading: false,
    hasMore: true
  },

  onLoad() {
    this.loadContents()
  },

  onPullDownRefresh() {
    this.setData({ page: 1, hasMore: true })
    this.loadContents()
    wx.stopPullDownRefresh()
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadMoreContents()
    }
  },

  async loadContents() {
    if (this.data.loading) return
    
    this.setData({ loading: true })
    try {
      // ✅ 使用 getFeedList 获取所有已发布内容
      const res = await api.getFeedList({
        page: this.data.page,
        size: this.data.size
        // status='published' 和 deleted=0 已在 api.getFeedList 中自动添加
      })
      
      if (res.data) {
        // 处理图片 URL
        const list = res.data.list.map(item => ({
          ...item,
          coverImage: handleImageUrl(item.coverImage, 'cover'),
          authorAvatar: handleImageUrl(item.authorAvatar, 'avatar')
        }))
        
        const contents = this.data.page === 1 
          ? list 
          : [...this.data.contents, ...list]
        
        this.setData({
          contents,
          hasMore: res.data.list.length >= this.data.size,
          loading: false
        })
      }
    } catch (e) {
      console.error('加载内容失败', e)
      this.setData({ loading: false })
    }
  },

  loadMoreContents() {
    this.setData({ page: this.data.page + 1 }, () => {
      this.loadContents()
    })
  },

  onContentTap(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/content/detail?id=${id}`
    })
  }
})
