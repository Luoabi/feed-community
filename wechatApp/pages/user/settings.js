const app = getApp()

Page({
  data: {
    userInfo: {},
    settings: {
      likeNotify: true,
      commentNotify: true,
      followNotify: true,
      privateAccount: false,
      showOnlineStatus: true
    },
    cacheSize: '0 MB'
  },

  onLoad() {
    this.loadUserInfo()
    this.loadSettings()
    this.calculateCacheSize()
  },

  loadUserInfo() {
    const userInfo = app.globalData.userInfo || {}
    this.setData({ userInfo })
  },

  loadSettings() {
    // 从本地存储加载设置
    const settings = wx.getStorageSync('userSettings') || this.data.settings
    this.setData({ settings })
  },

  saveSettings() {
    // 保存设置到本地存储
    wx.setStorageSync('userSettings', this.data.settings)
  },

  // 修改密码
  onChangePassword() {
    wx.showModal({
      title: '修改密码',
      content: '请前往个人中心修改密码',
      showCancel: false
    })
  },

  // 绑定手机
  onBindPhone() {
    wx.showModal({
      title: '绑定手机',
      content: '该功能正在开发中',
      showCancel: false
    })
  },

  // 绑定邮箱
  onBindEmail() {
    wx.showModal({
      title: '绑定邮箱',
      content: '该功能正在开发中',
      showCancel: false
    })
  },

  // 点赞通知开关
  onLikeNotifyChange(e) {
    const settings = this.data.settings
    settings.likeNotify = e.detail.value
    this.setData({ settings })
    this.saveSettings()
    wx.showToast({
      title: e.detail.value ? '已开启点赞通知' : '已关闭点赞通知',
      icon: 'success'
    })
  },

  // 评论通知开关
  onCommentNotifyChange(e) {
    const settings = this.data.settings
    settings.commentNotify = e.detail.value
    this.setData({ settings })
    this.saveSettings()
    wx.showToast({
      title: e.detail.value ? '已开启评论通知' : '已关闭评论通知',
      icon: 'success'
    })
  },

  // 关注通知开关
  onFollowNotifyChange(e) {
    const settings = this.data.settings
    settings.followNotify = e.detail.value
    this.setData({ settings })
    this.saveSettings()
    wx.showToast({
      title: e.detail.value ? '已开启关注通知' : '已关闭关注通知',
      icon: 'success'
    })
  },

  // 私密账号开关
  onPrivateAccountChange(e) {
    const settings = this.data.settings
    settings.privateAccount = e.detail.value
    this.setData({ settings })
    this.saveSettings()
    wx.showToast({
      title: e.detail.value ? '已设为私密账号' : '已设为公开账号',
      icon: 'success'
    })
  },

  // 显示在线状态开关
  onShowOnlineStatusChange(e) {
    const settings = this.data.settings
    settings.showOnlineStatus = e.detail.value
    this.setData({ settings })
    this.saveSettings()
    wx.showToast({
      title: e.detail.value ? '已显示在线状态' : '已隐藏在线状态',
      icon: 'success'
    })
  },

  // 清除缓存
  onClearCache() {
    wx.showModal({
      title: '清除缓存',
      content: '确定要清除所有缓存吗？',
      success: (res) => {
        if (res.confirm) {
          wx.clearStorage({
            success: () => {
              // 保留登录信息
              const token = wx.getStorageSync('token')
              const userInfo = wx.getStorageSync('userInfo')
              if (token) wx.setStorageSync('token', token)
              if (userInfo) wx.setStorageSync('userInfo', userInfo)
              
              this.setData({ cacheSize: '0 MB' })
              wx.showToast({
                title: '缓存已清除',
                icon: 'success'
              })
            }
          })
        }
      }
    })
  },

  // 检查更新
  onCheckUpdate() {
    wx.showLoading({ title: '检查中...' })
    
    setTimeout(() => {
      wx.hideLoading()
      wx.showToast({
        title: '已是最新版本',
        icon: 'success'
      })
    }, 1000)
  },

  // 计算缓存大小
  calculateCacheSize() {
    try {
      const info = wx.getStorageInfoSync()
      const sizeKB = info.currentSize
      const sizeMB = (sizeKB / 1024).toFixed(2)
      this.setData({ cacheSize: `${sizeMB} MB` })
    } catch (e) {
      console.error('获取缓存大小失败', e)
    }
  }
})
