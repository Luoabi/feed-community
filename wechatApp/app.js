const api = require('./api/index.js')

App({
  onLaunch() {
    this.checkLogin()
  },
  
  checkLogin() {
    const token = wx.getStorageSync('token')
    if (token) {
      this.globalData.isLogin = true
      this.globalData.token = token
      this.getUserInfo()
    } else {
      this.globalData.isLogin = false
      this.globalData.token = null
    }
  },
  
  async getUserInfo() {
    try {
      const res = await api.getUserInfo()
      if (res.data) {
        this.globalData.userInfo = res.data.userInfo
      }
    } catch (e) {
      console.error('获取用户信息失败', e)
      // 如果获取用户信息失败（可能是 token 过期），清除登录状态
      if (e.code === 401) {
        this.logout()
      }
    }
  },
  
  /**
   * 登出方法
   */
  logout() {
    // 清除本地存储
    wx.removeStorageSync('token')
    wx.removeStorageSync('userInfo')
    
    // 清除全局数据
    this.globalData.isLogin = false
    this.globalData.userInfo = null
    this.globalData.token = null
    
    // 跳转到登录页
    wx.reLaunch({
      url: '/pages/login/index'
    })
  },
  
  /**
   * 检查是否登录，未登录则跳转到登录页
   * @returns {boolean} 是否已登录
   */
  checkLoginStatus() {
    if (!this.globalData.isLogin || !wx.getStorageSync('token')) {
      wx.showToast({
        title: '请先登录',
        icon: 'none',
        duration: 2000
      })
      
      setTimeout(() => {
        wx.navigateTo({
          url: '/pages/login/index'
        })
      }, 2000)
      
      return false
    }
    return true
  },

  globalData: {
    userInfo: null,
    isLogin: false,
    token: null
  }
})
