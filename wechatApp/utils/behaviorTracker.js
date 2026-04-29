const request = require('./request.js')

/**
 * 用户行为追踪工具
 */
class BehaviorTracker {
  constructor() {
    this.viewStartTime = null
    this.currentContentId = null
  }

  /**
   * 获取当前用户ID
   */
  getCurrentUserId() {
    const userInfo = wx.getStorageSync('userInfo')
    return userInfo ? userInfo.id : null
  }

  /**
   * 记录浏览开始
   */
  startView(contentId) {
    this.viewStartTime = Date.now()
    this.currentContentId = contentId
    console.log('开始浏览内容:', contentId)
  }

  /**
   * 记录浏览结束
   */
  async endView(source = 'feed') {
    if (!this.viewStartTime || !this.currentContentId) {
      return
    }

    const userId = this.getCurrentUserId()
    if (!userId) {
      console.log('用户未登录，不记录浏览行为')
      this.viewStartTime = null
      this.currentContentId = null
      return
    }

    const duration = Math.floor((Date.now() - this.viewStartTime) / 1000)
    
    // 只记录停留时间超过3秒的浏览
    if (duration >= 3) {
      try {
        await request.post('/behavior/view', {
          userId,
          contentId: this.currentContentId,
          duration,
          source
        })
        console.log('记录浏览行为成功:', {
          contentId: this.currentContentId,
          duration,
          source
        })
      } catch (error) {
        console.error('记录浏览行为失败:', error)
      }
    }

    this.viewStartTime = null
    this.currentContentId = null
  }

  /**
   * 记录点击行为
   */
  async trackClick(contentId, source = 'feed') {
    const userId = this.getCurrentUserId()
    if (!userId) {
      console.log('用户未登录，不记录点击行为')
      return
    }

    try {
      await request.post('/behavior/click', {
        userId,
        contentId,
        source
      })
      console.log('记录点击行为成功:', { contentId, source })
    } catch (error) {
      console.error('记录点击行为失败:', error)
    }
  }

  /**
   * 记录通用行为
   */
  async trackBehavior(contentId, actionType, options = {}) {
    const userId = this.getCurrentUserId()
    if (!userId) {
      console.log('用户未登录，不记录行为')
      return
    }

    try {
      await request.post('/behavior/track', {
        userId,
        contentId,
        actionType,
        ...options
      })
      console.log('记录行为成功:', { contentId, actionType })
    } catch (error) {
      console.error('记录行为失败:', error)
    }
  }
}

// 导出单例
module.exports = new BehaviorTracker()
