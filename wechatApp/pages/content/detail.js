const api = require('../../api/index.js')
const { handleImageUrl, handleImageList } = require('../../utils/image.js')

Page({
  data: {
    contentId: null,
    content: null,
    comments: [],
    commentText: '',
    isLiked: false,
    isCollected: false
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ contentId: options.id })
      this.loadContentDetail()
      this.loadComments()
      this.checkInteractionStatus()
    }
  },

  async checkInteractionStatus() {
    try {
      const res = await api.checkInteractionStatus(this.data.contentId)
      if (res.data) {
        this.setData({
          isLiked: res.data.isLiked || false,
          isCollected: res.data.isCollected || false
        })
      }
    } catch (e) {
      console.error('检查互动状态失败', e)
    }
  },

  async loadContentDetail() {
    try {
      const res = await api.getContentDetail(this.data.contentId)
      if (res.data) {
        // 处理图片 URL
        const content = {
          ...res.data,
          coverImage: handleImageUrl(res.data.coverImage, 'cover'),
          authorAvatar: handleImageUrl(res.data.authorAvatar, 'avatar'),
          images: handleImageList(res.data.images)
        }
        this.setData({ content })
      }
    } catch (e) {
      console.error('加载内容详情失败', e)
    }
  },

  async loadComments() {
    try {
      // ✅ 使用评论树接口，获取一级评论和部分回复
      const res = await api.getCommentTree({
        contentId: this.data.contentId,
        page: 1,
        size: 20
      })
      if (res.data && res.data.list) {
        // 处理评论用户头像
        const comments = res.data.list.map(item => ({
          ...item.comment,
          userAvatar: handleImageUrl(item.comment.userAvatar, 'avatar'),
          replies: item.replies.map(reply => ({
            ...reply,
            userAvatar: handleImageUrl(reply.userAvatar, 'avatar')
          })),
          hasMore: item.hasMore
        }))
        this.setData({ comments })
      }
    } catch (e) {
      console.error('加载评论失败', e)
    }
  },

  async onLikeTap() {
    try {
      const res = await api.likeContent({ contentId: this.data.contentId })
      const content = this.data.content
      const newIsLiked = !this.data.isLiked
      
      // 更新点赞数
      content.likes = newIsLiked ? (content.likes || 0) + 1 : (content.likes || 0) - 1
      
      this.setData({ 
        content,
        isLiked: newIsLiked
      })
      
      wx.showToast({
        title: newIsLiked ? '点赞成功' : '取消点赞',
        icon: 'success'
      })
    } catch (e) {
      console.error('点赞失败', e)
      wx.showToast({
        title: '操作失败',
        icon: 'none'
      })
    }
  },

  async onCollectTap() {
    try {
      const res = await api.collectContent({ contentId: this.data.contentId })
      const content = this.data.content
      const newIsCollected = !this.data.isCollected
      
      // 更新收藏数
      content.collects = newIsCollected ? (content.collects || 0) + 1 : (content.collects || 0) - 1
      
      this.setData({ 
        content,
        isCollected: newIsCollected
      })
      
      wx.showToast({
        title: newIsCollected ? '收藏成功' : '取消收藏',
        icon: 'success'
      })
    } catch (e) {
      console.error('收藏失败', e)
      wx.showToast({
        title: '操作失败',
        icon: 'none'
      })
    }
  },

  async onShareTap() {
    try {
      // ✅ 使用统一的分享接口
      await api.shareContent({ 
        contentId: this.data.contentId,
        platform: 'wechat'
      })
      const content = this.data.content
      content.shares = (content.shares || 0) + 1
      this.setData({ content })
      wx.showShareMenu({
        withShareTicket: true
      })
    } catch (e) {
      console.error('分享失败', e)
    }
  },

  onCommentInput(e) {
    this.setData({ commentText: e.detail.value })
  },

  async onSendComment() {
    if (!this.data.commentText.trim()) {
      wx.showToast({
        title: '请输入评论内容',
        icon: 'none'
      })
      return
    }
    
    try {
      await api.addComment({
        contentType: 'content',
        contentId: this.data.contentId,
        content: this.data.commentText
      })
      wx.showToast({
        title: '评论成功',
        icon: 'success'
      })
      this.setData({ commentText: '' })
      this.loadComments()
    } catch (e) {
      console.error('评论失败', e)
    }
  },

  onShareAppMessage() {
    return {
      title: this.data.content?.title || '精彩内容',
      path: `/pages/content/detail?id=${this.data.contentId}`
    }
  },
  
  // 复制链接
  onCopyLink(e) {
    const url = e.currentTarget.dataset.url
    wx.setClipboardData({
      data: url,
      success: () => {
        wx.showToast({
          title: '链接已复制',
          icon: 'success',
          duration: 2000
        })
      },
      fail: () => {
        wx.showToast({
          title: '复制失败',
          icon: 'none',
          duration: 2000
        })
      }
    })
  },
  
  // 预览图片
  onPreviewImage(e) {
    const index = e.currentTarget.dataset.index
    wx.previewImage({
      current: this.data.content.images[index],
      urls: this.data.content.images
    })
  }
})
