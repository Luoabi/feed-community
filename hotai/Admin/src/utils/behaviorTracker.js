import behaviorApi from '@/api/behavior'

/**
 * 用户行为追踪工具类
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
    const userInfo = localStorage.getItem('userInfo')
    if (userInfo) {
      try {
        const user = JSON.parse(userInfo)
        return user.id
      } catch (e) {
        console.error('解析用户信息失败:', e)
      }
    }
    return null
  }

  /**
   * 记录浏览开始
   */
  startView(contentId) {
    this.viewStartTime = Date.now()
    this.currentContentId = contentId
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
      return
    }

    const duration = Math.floor((Date.now() - this.viewStartTime) / 1000)
    
    // 只记录停留时间超过3秒的浏览
    if (duration >= 3) {
      try {
        await behaviorApi.trackView({
          userId,
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
      return
    }

    try {
      await behaviorApi.trackClick({
        userId,
        contentId,
        source
      })
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
      return
    }

    try {
      await behaviorApi.trackBehavior({
        userId,
        contentId,
        actionType,
        ...options
      })
    } catch (error) {
      console.error('记录行为失败:', error)
    }
  }
}

// 导出单例
export default new BehaviorTracker()
