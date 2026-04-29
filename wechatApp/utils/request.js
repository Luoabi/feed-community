// 开发环境使用本地地址，生产环境使用线上地址
const BASE_URL = 'http://localhost:8080/api'
// const BASE_URL = 'https://your-domain.com/api' // 生产环境

// 真机调试时，请将 localhost 改为你电脑的局域网 IP
// Windows 查看 IP：命令行输入 ipconfig，查看 IPv4 地址
// Mac 查看 IP：命令行输入 ifconfig，查看 en0 的 inet 地址
// 例如：const BASE_URL = 'http://192.168.1.100:8080/api'

// 是否正在跳转到登录页（防止重复跳转）
let isNavigatingToLogin = false

function request(options) {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token')
    
    wx.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : ''
      },
      success: (res) => {
        if (res.statusCode === 200) {
          if (res.data.code === 200 || res.data.code === 0) {
            resolve(res.data)
          } else {
            wx.showToast({
              title: res.data.message || '请求失败',
              icon: 'none'
            })
            reject(res.data)
          }
        } else if (res.statusCode === 401) {
          // 401 未授权 - Token 过期或无效
          handleUnauthorized()
          reject({ code: 401, message: '登录已过期，请重新登录' })
        } else if (res.statusCode === 404) {
          wx.showToast({
            title: '接口不存在',
            icon: 'none'
          })
          reject(res)
        } else {
          wx.showToast({
            title: '网络错误',
            icon: 'none'
          })
          reject(res)
        }
      },
      fail: (err) => {
        console.error('请求失败:', err)
        wx.showToast({
          title: '网络请求失败，请检查网络连接',
          icon: 'none',
          duration: 2000
        })
        reject(err)
      }
    })
  })
}

/**
 * 处理未授权（登录过期）
 */
function handleUnauthorized() {
  // 防止重复跳转
  if (isNavigatingToLogin) {
    return
  }
  
  isNavigatingToLogin = true
  
  // 清除本地存储的登录信息
  wx.removeStorageSync('token')
  wx.removeStorageSync('userInfo')
  
  // 清除全局数据
  const app = getApp()
  if (app) {
    app.globalData.isLogin = false
    app.globalData.userInfo = null
    app.globalData.token = null
  }
  
  // 提示用户
  wx.showToast({
    title: '登录已过期，请重新登录',
    icon: 'none',
    duration: 2000
  })
  
  // 延迟跳转，让提示显示完整
  setTimeout(() => {
    // 获取当前页面栈
    const pages = getCurrentPages()
    const currentPage = pages[pages.length - 1]
    const currentRoute = currentPage ? currentPage.route : ''
    
    // 如果当前不在登录页，则跳转到登录页
    if (currentRoute !== 'pages/login/index') {
      wx.reLaunch({
        url: '/pages/login/index',
        success: () => {
          isNavigatingToLogin = false
        },
        fail: () => {
          isNavigatingToLogin = false
        }
      })
    } else {
      isNavigatingToLogin = false
    }
  }, 2000)
}

module.exports = {
  get: (url, data) => request({ url, method: 'GET', data }),
  post: (url, data) => request({ url, method: 'POST', data }),
  put: (url, data) => request({ url, method: 'PUT', data }),
  delete: (url, data) => request({ url, method: 'DELETE', data })
}
