const app = getApp()
const api = require('../../api/index.js')

Page({
  data: {
    username: '',
    password: '',
    logging: false
  },

  onUsernameInput(e) {
    this.setData({ username: e.detail.value })
  },

  onPasswordInput(e) {
    this.setData({ password: e.detail.value })
  },

  async onLogin() {
    if (!this.data.username.trim()) {
      wx.showToast({
        title: '请输入用户名',
        icon: 'none'
      })
      return
    }

    if (!this.data.password.trim()) {
      wx.showToast({
        title: '请输入密码',
        icon: 'none'
      })
      return
    }

    if (this.data.logging) return

    this.setData({ logging: true })

    try {
      const res = await api.login({
        username: this.data.username,
        password: this.data.password
      })

      console.log('登录响应:', res)

      if (res.data && res.data.token) {
        // ✅ 保存 token 到本地存储
        wx.setStorageSync('token', res.data.token)
        
        // ✅ 保存 userInfo 到本地存储（完整的用户信息对象）
        if (res.data.userInfo) {
          wx.setStorageSync('userInfo', res.data.userInfo)
          console.log('已保存用户信息到 Storage:', res.data.userInfo)
        }
        
        // ✅ 更新全局状态
        app.globalData.isLogin = true
        app.globalData.userInfo = res.data.userInfo
        app.globalData.token = res.data.token

        wx.showToast({
          title: '登录成功',
          icon: 'success'
        })

        setTimeout(() => {
          wx.switchTab({
            url: '/pages/user/index'
          })
        }, 1500)
      }
    } catch (e) {
      console.error('登录失败', e)
      this.setData({ logging: false })
    }
  },

  onQuickLogin() {
    // 快速登录（使用测试账号）
    this.setData({
      username: 'admin',
      password: 'admin123'
    }, () => {
      this.onLogin()
    })
  },

  onRegister() {
    wx.navigateTo({
      url: '/pages/register/index'
    })
  }
})
