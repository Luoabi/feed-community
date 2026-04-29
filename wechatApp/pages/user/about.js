Page({
  data: {},

  // 复制邮箱
  onCopyEmail() {
    wx.setClipboardData({
      data: 'support@hotai.com',
      success: () => {
        wx.showToast({
          title: '邮箱已复制',
          icon: 'success'
        })
      }
    })
  },

  // 复制微信号
  onCopyWechat() {
    wx.setClipboardData({
      data: 'hotai_support',
      success: () => {
        wx.showToast({
          title: '微信号已复制',
          icon: 'success'
        })
      }
    })
  },

  // 拨打电话
  onCallPhone() {
    wx.makePhoneCall({
      phoneNumber: '4001234567',
      fail: () => {
        wx.showToast({
          title: '拨号失败',
          icon: 'none'
        })
      }
    })
  },

  // 用户协议
  onUserAgreement() {
    wx.showModal({
      title: '用户协议',
      content: '用户协议详情页面正在开发中',
      showCancel: false
    })
  },

  // 隐私政策
  onPrivacyPolicy() {
    wx.showModal({
      title: '隐私政策',
      content: '隐私政策详情页面正在开发中',
      showCancel: false
    })
  },

  // 社区规范
  onCommunityRules() {
    wx.showModal({
      title: '社区规范',
      content: '社区规范详情页面正在开发中',
      showCancel: false
    })
  },

  // 意见反馈
  onFeedback() {
    wx.showModal({
      title: '意见反馈',
      editable: true,
      placeholderText: '请输入您的宝贵意见...',
      success: (res) => {
        if (res.confirm && res.content) {
          wx.showToast({
            title: '感谢您的反馈',
            icon: 'success'
          })
          // 这里可以调用API提交反馈
          console.log('用户反馈:', res.content)
        }
      }
    })
  }
})
