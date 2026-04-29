const api = require('../../api/index.js')

Page({
  data: {
    username: '',
    password: '',
    confirmPassword: '',
    nickname: '',
    phone: '',
    registering: false,
    // 验证状态
    usernameError: '',
    passwordError: '',
    confirmPasswordError: '',
    nicknameError: '',
    phoneError: ''
  },

  onUsernameInput(e) {
    const username = e.detail.value
    this.setData({ username })
    this.validateUsername(username)
  },

  onPasswordInput(e) {
    const password = e.detail.value
    this.setData({ password })
    this.validatePassword(password)
  },

  onConfirmPasswordInput(e) {
    const confirmPassword = e.detail.value
    this.setData({ confirmPassword })
    this.validateConfirmPassword(confirmPassword)
  },

  onNicknameInput(e) {
    const nickname = e.detail.value
    this.setData({ nickname })
    this.validateNickname(nickname)
  },

  onPhoneInput(e) {
    const phone = e.detail.value
    this.setData({ phone })
    this.validatePhone(phone)
  },

  // ✅ 用户名验证：3-20个字符，只能包含字母、数字、下划线
  validateUsername(username) {
    if (!username || username.trim() === '') {
      this.setData({ usernameError: '请输入用户名' })
      return false
    }
    if (username.length < 3) {
      this.setData({ usernameError: '用户名至少3个字符' })
      return false
    }
    if (username.length > 20) {
      this.setData({ usernameError: '用户名最多20个字符' })
      return false
    }
    if (!/^[a-zA-Z0-9_]+$/.test(username)) {
      this.setData({ usernameError: '只能包含字母、数字、下划线' })
      return false
    }
    this.setData({ usernameError: '' })
    return true
  },

  // ✅ 密码验证：6-20个字符
  validatePassword(password) {
    if (!password || password.trim() === '') {
      this.setData({ passwordError: '请输入密码' })
      return false
    }
    if (password.length < 6) {
      this.setData({ passwordError: '密码至少6个字符' })
      return false
    }
    if (password.length > 20) {
      this.setData({ passwordError: '密码最多20个字符' })
      return false
    }
    this.setData({ passwordError: '' })
    return true
  },

  // ✅ 确认密码验证
  validateConfirmPassword(confirmPassword) {
    if (!confirmPassword || confirmPassword.trim() === '') {
      this.setData({ confirmPasswordError: '请再次输入密码' })
      return false
    }
    if (confirmPassword !== this.data.password) {
      this.setData({ confirmPasswordError: '两次密码不一致' })
      return false
    }
    this.setData({ confirmPasswordError: '' })
    return true
  },

  // ✅ 昵称验证：1-20个字符
  validateNickname(nickname) {
    if (!nickname || nickname.trim() === '') {
      this.setData({ nicknameError: '请输入昵称' })
      return false
    }
    if (nickname.length > 20) {
      this.setData({ nicknameError: '昵称最多20个字符' })
      return false
    }
    this.setData({ nicknameError: '' })
    return true
  },

  // ✅ 手机号验证：可选，但如果填写必须是11位数字
  validatePhone(phone) {
    if (!phone || phone.trim() === '') {
      this.setData({ phoneError: '' })
      return true // 手机号可选
    }
    if (!/^1[3-9]\d{9}$/.test(phone)) {
      this.setData({ phoneError: '手机号格式不正确' })
      return false
    }
    this.setData({ phoneError: '' })
    return true
  },

  async onRegister() {
    // ✅ 统一验证所有字段
    const usernameValid = this.validateUsername(this.data.username)
    const passwordValid = this.validatePassword(this.data.password)
    const confirmPasswordValid = this.validateConfirmPassword(this.data.confirmPassword)
    const nicknameValid = this.validateNickname(this.data.nickname)
    const phoneValid = this.validatePhone(this.data.phone)

    if (!usernameValid || !passwordValid || !confirmPasswordValid || !nicknameValid || !phoneValid) {
      wx.showToast({
        title: '请检查输入信息',
        icon: 'none'
      })
      return
    }

    if (this.data.registering) return

    this.setData({ registering: true })

    try {
      const res = await api.register({
        username: this.data.username.trim(),
        password: this.data.password,
        nickname: this.data.nickname.trim(),
        phone: this.data.phone.trim() || null
      })

      if (res.code === 200) {
        wx.showToast({
          title: '注册成功',
          icon: 'success'
        })

        setTimeout(() => {
          wx.navigateBack()
        }, 1500)
      } else {
        wx.showToast({
          title: res.message || '注册失败',
          icon: 'none'
        })
        this.setData({ registering: false })
      }
    } catch (e) {
      console.error('注册失败', e)
      wx.showToast({
        title: '注册失败，请重试',
        icon: 'none'
      })
      this.setData({ registering: false })
    }
  },

  onBackToLogin() {
    wx.navigateBack()
  }
})
