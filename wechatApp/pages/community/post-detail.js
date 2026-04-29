const api = require('../../api/index.js')
const { handleImageUrl, handleImageList } = require('../../utils/image.js')

Page({
  data: {
    postId: null,
    post: null,
    comments: [],
    commentText: '',
    isLiked: false,
    isCollected: false,
    loading: true
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ postId: options.id })
      this.loadPost()
      this.loadComments()
      this.checkInteractionStatus()
    }
  },

  async checkInteractionStatus() {
    try {
      const res = await api.checkInteractionStatus(this.data.postId)
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

  onShareAppMessage() {
    return {
      title: this.data.post?.title || '分享帖子',
      path: `/pages/community/post-detail?id=${this.data.postId}`
    }
  },

  async loadPost() {
    try {
      // ✅ 使用统一的 content 详情接口
      const res = await api.getContentDetail(this.data.postId)
      if (res.data) {
        // 处理图片 URL
        const post = {
          ...res.data,
          authorAvatar: handleImageUrl(res.data.authorAvatar, 'avatar'),
          images: handleImageList(res.data.images)
        }
        this.setData({ 
          post,
          loading: false
        })
      }
    } catch (e) {
      console.error('加载帖子失败', e)
      this.setData({ loading: false })
    }
  },

  async loadComments() {
    try {
      // ✅ 评论类型统一为 'content'
      const res = await api.getCommentList({
        contentType: 'content',
        contentId: this.data.postId,
        page: 1,
        size: 50
      })
      if (res.data && res.data.list) {
        // 处理评论用户头像
        const comments = res.data.list.map(item => ({
          ...item,
          userAvatar: handleImageUrl(item.userAvatar, 'avatar')
        }))
        this.setData({ comments })
      }
    } catch (e) {
      console.error('加载评论失败', e)
    }
  },

  async onLikeTap() {
    try {
      const res = await api.likeContent({ contentId: this.data.postId })
      const post = this.data.post
      const newIsLiked = !this.data.isLiked
      
      // 更新点赞数
      post.likes = newIsLiked ? (post.likes || 0) + 1 : (post.likes || 0) - 1
      
      this.setData({ 
        post,
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
      const res = await api.collectContent({ contentId: this.data.postId })
      const post = this.data.post
      const newIsCollected = !this.data.isCollected
      
      // 更新收藏数
      post.collects = newIsCollected ? (post.collects || 0) + 1 : (post.collects || 0) - 1
      
      this.setData({ 
        post,
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

  onCommentInput(e) {
    this.setData({ commentText: e.detail.value })
  },

  async onCommentSubmit() {
    if (!this.data.commentText.trim()) {
      wx.showToast({
        title: '请输入评论内容',
        icon: 'none'
      })
      return
    }

    try {
      // ✅ 评论类型统一为 'content'
      await api.addComment({
        contentType: 'content',
        contentId: this.data.postId,
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

  onImagePreview(e) {
    const current = e.currentTarget.dataset.url
    wx.previewImage({
      current,
      urls: this.data.post.images || []
    })
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
  }
})
