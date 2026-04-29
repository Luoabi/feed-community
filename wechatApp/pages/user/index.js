const app = getApp()
const api = require('../../api/index.js')
const { handleImageUrl } = require('../../utils/image.js')

Page({
  data: {
    userInfo: null,
    isLogin: false,
    stats: {
      posts: 0,      // 发布数
      likes: 0,      // 点赞数
      collects: 0    // 收藏数
    }
  },

  onLoad() {
    this.checkLogin()
    if (app.globalData.isLogin) {
      this.loadUserInfo()
      this.loadUserStats()
    }
  },

  onShow() {
    // 每次显示页面都重新加载用户信息
    if (app.globalData.isLogin) {
      this.loadUserInfo()
      this.loadUserStats()
    } else {
      this.checkLogin()
    }
  },

  checkLogin() {
    const isLogin = app.globalData.isLogin
    const userInfo = app.globalData.userInfo
    this.setData({ 
      isLogin,
      userInfo
    })
  },

  async loadUserInfo() {
    try {
      const res = await api.getUserInfo()
      console.log('获取用户信息响应:', res)
      
      if (res.data && res.data.userInfo) {
        // ✅ 头像在 userInfo 对象里
        const rawUserInfo = res.data.userInfo
        console.log('原始用户信息:', rawUserInfo)
        console.log('原始头像URL:', rawUserInfo.avatar)
        
        // 处理用户头像 URL
        const userInfo = {
          ...rawUserInfo,
          avatar: handleImageUrl(rawUserInfo.avatar, 'avatar')
        }
        
        console.log('处理后的用户信息:', userInfo)
        console.log('处理后的头像URL:', userInfo.avatar)
        
        // 更新页面数据
        this.setData({ 
          userInfo,
          isLogin: true
        })
        
        // ✅ 更新全局数据
        app.globalData.userInfo = rawUserInfo  // 保存原始数据（包含真实URL）
        app.globalData.isLogin = true
        
        // ✅ 更新 Storage（保存原始数据，不保存处理后的）
        wx.setStorageSync('userInfo', rawUserInfo)
        
        console.log('页面数据已更新')
        console.log('Storage 已更新')
      }
    } catch (e) {
      console.error('获取用户信息失败', e)
    }
  },

  async loadUserStats() {
    try {
      // ✅ 调用后端统计接口
      const res = await api.getMyStats()
      if (res.data) {
        this.setData({
          stats: {
            posts: res.data.postsCount || 0,      // 发布数
            likes: res.data.likesCount || 0,      // 点赞数
            collects: res.data.collectsCount || 0 // 收藏数
          }
        })
      }
    } catch (e) {
      console.error('获取用户统计失败', e)
      // 失败时显示默认值
      this.setData({
        stats: {
          posts: 0,
          likes: 0,
          collects: 0
        }
      })
    }
  },

  onLoginTap() {
    wx.navigateTo({
      url: '/pages/login/index'
    })
  },

  onEditProfileTap() {
    wx.navigateTo({
      url: '/pages/user/edit-profile'
    })
  },

  onMyPostsTap() {
    wx.navigateTo({
      url: '/pages/user/my-posts'
    })
  },

  onMyLikesTap() {
    wx.navigateTo({
      url: '/pages/user/my-likes'
    })
  },

  onMyCollectsTap() {
    wx.navigateTo({
      url: '/pages/user/my-collects'
    })
  },

  onSettingsTap() {
    wx.navigateTo({
      url: '/pages/user/settings'
    })
  },

  onAboutTap() {
    wx.navigateTo({
      url: '/pages/user/about'
    })
  },

  onLogoutTap() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          wx.removeStorageSync('token')
          app.globalData.isLogin = false
          app.globalData.userInfo = null
          this.setData({ 
            isLogin: false,
            userInfo: null
          })
          wx.showToast({
            title: '已退出登录',
            icon: 'success'
          })
        }
      }
    })
  }
})
