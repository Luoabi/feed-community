const app = getApp()
const api = require('../../api/index.js')
const { uploadAvatar } = require('../../utils/upload.js')

Page({
  data: {
    formData: {
      avatar: '',
      nickname: '',
      gender: 0,
      bio: '',
      phone: '',
      email: ''
    },
    bioLength: 0,
    saving: false,
    uploading: false
  },

  onLoad() {
    this.loadUserInfo()
  },

  async loadUserInfo() {
    try {
      const res = await api.getUserInfo()
      console.log('编辑资料 - 获取用户信息:', res)
      
      if (res.data && res.data.userInfo) {
        const userInfo = res.data.userInfo
        console.log('用户信息:', userInfo)
        
        this.setData({
          formData: {
            avatar: userInfo.avatar || '',
            nickname: userInfo.nickname || '',
            gender: userInfo.gender || 0,
            bio: userInfo.bio || '',
            phone: userInfo.phone || '',
            email: userInfo.email || ''
          },
          bioLength: (userInfo.bio || '').length
        })
      }
    } catch (e) {
      console.error('获取用户信息失败', e)
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      })
    }
  },

  onChooseAvatar() {
    if (this.data.uploading) {
      wx.showToast({
        title: '正在上传中...',
        icon: 'none'
      })
      return
    }

    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const tempFilePath = res.tempFilePaths[0]
        // 先显示临时图片，提升用户体验
        this.setData({
          'formData.avatar': tempFilePath
        })
        // 上传到服务器
        this.uploadAvatarToServer(tempFilePath)
      }
    })
  },

  async uploadAvatarToServer(filePath) {
    this.setData({ uploading: true })
    
    try {
      // 使用上传工具函数
      const avatarUrl = await uploadAvatar(filePath)
      
      // 上传成功，更新头像URL
      this.setData({
        'formData.avatar': avatarUrl,
        uploading: false
      })
      
      wx.showToast({
        title: '头像上传成功',
        icon: 'success',
        duration: 1500
      })
      
      console.log('头像上传成功，URL:', avatarUrl)
    } catch (error) {
      console.error('头像上传失败:', error)
      
      this.setData({ uploading: false })
      
      wx.showToast({
        title: error.message || '上传失败',
        icon: 'none',
        duration: 2000
      })
      
      // 恢复原头像
      this.loadUserInfo()
    }
  },

  onNicknameInput(e) {
    this.setData({
      'formData.nickname': e.detail.value
    })
  },

  onGenderChange(e) {
    const gender = parseInt(e.currentTarget.dataset.gender)
    this.setData({
      'formData.gender': gender
    })
  },

  onBioInput(e) {
    const value = e.detail.value
    this.setData({
      'formData.bio': value,
      bioLength: value.length
    })
  },

  onPhoneInput(e) {
    this.setData({
      'formData.phone': e.detail.value
    })
  },

  onEmailInput(e) {
    this.setData({
      'formData.email': e.detail.value
    })
  },

  async onSave() {
    const { formData } = this.data

    // 验证必填项
    if (!formData.nickname || formData.nickname.trim() === '') {
      wx.showToast({
        title: '请输入昵称',
        icon: 'none'
      })
      return
    }

    // 验证手机号格式
    if (formData.phone && !/^1[3-9]\d{9}$/.test(formData.phone)) {
      wx.showToast({
        title: '手机号格式不正确',
        icon: 'none'
      })
      return
    }

    // 验证邮箱格式
    if (formData.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      wx.showToast({
        title: '邮箱格式不正确',
        icon: 'none'
      })
      return
    }

    this.setData({ saving: true })

    try {
      const res = await api.updateUserInfo(formData)
      
      if (res.code === 200) {
        // 更新全局用户信息
        const userInfoRes = await api.getUserInfo()
        if (userInfoRes.data && userInfoRes.data.userInfo) {
          const updatedUserInfo = userInfoRes.data.userInfo
          
          // ✅ 更新全局数据
          app.globalData.userInfo = updatedUserInfo
          app.globalData.isLogin = true
          
          // ✅ 更新 Storage 中的用户信息
          wx.setStorageSync('userInfo', updatedUserInfo)
          
          console.log('全局用户信息已更新:', updatedUserInfo)
          console.log('Storage 用户信息已更新')
        }

        wx.showToast({
          title: '保存成功',
          icon: 'success',
          duration: 1500
        })

        // 延迟返回上一页，让用户中心的 onShow 自动刷新
        setTimeout(() => {
          wx.navigateBack()
        }, 1500)
      } else {
        wx.showToast({
          title: res.message || '保存失败',
          icon: 'none'
        })
      }
    } catch (e) {
      console.error('保存失败', e)
      wx.showToast({
        title: '保存失败',
        icon: 'none'
      })
    } finally {
      this.setData({ saving: false })
    }
  }
})
