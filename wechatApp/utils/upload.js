/**
 * 文件上传工具函数
 */

// 获取后端地址
const BASE_URL = 'http://localhost:8080/api'

/**
 * 上传头像
 * @param {string} filePath - 本地文件路径
 * @returns {Promise<string>} 返回上传后的URL
 */
function uploadAvatar(filePath) {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token')
    if (!token) {
      reject(new Error('请先登录'))
      return
    }

    wx.showLoading({ 
      title: '上传中...',
      mask: true
    })
    
    wx.uploadFile({
      url: `${BASE_URL}/upload/avatar`,
      filePath: filePath,
      name: 'file',
      header: {
        'token': token
      },
      success: (res) => {
        wx.hideLoading()
        try {
          console.log('头像上传响应:', res.data)
          const data = JSON.parse(res.data)
          if (data.code === 200) {
            resolve(data.data)
          } else {
            reject(new Error(data.message || '上传失败'))
          }
        } catch (e) {
          console.error('响应解析失败:', e, res.data)
          reject(new Error('响应解析失败'))
        }
      },
      fail: (err) => {
        wx.hideLoading()
        console.error('上传失败:', err)
        reject(new Error('网络请求失败'))
      }
    })
  })
}

/**
 * 上传单张图片
 * @param {string} filePath - 本地文件路径
 * @returns {Promise<string>} 返回上传后的URL
 */
function uploadImage(filePath) {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token')
    if (!token) {
      reject(new Error('请先登录'))
      return
    }

    wx.uploadFile({
      url: `${BASE_URL}/upload/image`,
      filePath: filePath,
      name: 'file',
      header: {
        'token': token
      },
      success: (res) => {
        try {
          const data = JSON.parse(res.data)
          if (data.code === 200) {
            resolve(data.data)
          } else {
            reject(new Error(data.message || '上传失败'))
          }
        } catch (e) {
          reject(new Error('响应解析失败'))
        }
      },
      fail: (err) => {
        reject(new Error('网络请求失败'))
      }
    })
  })
}

/**
 * 批量上传图片
 * @param {Array<string>} filePaths - 本地文件路径数组
 * @returns {Promise<Array<string>>} 返回上传后的URL数组
 */
function uploadImages(filePaths) {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token')
    if (!token) {
      reject(new Error('请先登录'))
      return
    }

    if (!filePaths || filePaths.length === 0) {
      resolve([])
      return
    }

    let uploadedCount = 0
    const totalCount = filePaths.length
    
    const uploadPromises = filePaths.map((filePath, index) => {
      return new Promise((resolveUpload, rejectUpload) => {
        wx.uploadFile({
          url: `${BASE_URL}/upload/image`,
          filePath: filePath,
          name: 'file',
          header: {
            'token': token
          },
          success: (res) => {
            uploadedCount++
            wx.showLoading({
              title: `上传图片中(${uploadedCount}/${totalCount})`,
              mask: true
            })
            
            try {
              const data = JSON.parse(res.data)
              if (data.code === 200) {
                resolveUpload(data.data)
              } else {
                rejectUpload(new Error(data.message || '上传失败'))
              }
            } catch (e) {
              rejectUpload(new Error('响应解析失败'))
            }
          },
          fail: (err) => {
            rejectUpload(new Error('网络请求失败'))
          }
        })
      })
    })

    wx.showLoading({
      title: `上传图片中(0/${totalCount})`,
      mask: true
    })

    Promise.all(uploadPromises)
      .then(urls => {
        wx.hideLoading()
        console.log('所有图片上传成功:', urls)
        resolve(urls)
      })
      .catch(err => {
        wx.hideLoading()
        console.error('图片上传失败:', err)
        reject(err)
      })
  })
}

/**
 * 上传视频
 * @param {string} filePath - 本地文件路径
 * @returns {Promise<string>} 返回上传后的URL
 */
function uploadVideo(filePath) {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token')
    if (!token) {
      reject(new Error('请先登录'))
      return
    }

    if (!filePath) {
      resolve('')
      return
    }

    wx.showLoading({
      title: '上传视频中...',
      mask: true
    })

    wx.uploadFile({
      url: `${BASE_URL}/upload/video`,
      filePath: filePath,
      name: 'file',
      header: {
        'token': token
      },
      success: (res) => {
        wx.hideLoading()
        try {
          const data = JSON.parse(res.data)
          if (data.code === 200) {
            resolve(data.data)
          } else {
            reject(new Error(data.message || '视频上传失败'))
          }
        } catch (e) {
          reject(new Error('响应解析失败'))
        }
      },
      fail: (err) => {
        wx.hideLoading()
        reject(new Error('网络请求失败'))
      }
    })
  })
}

module.exports = {
  uploadAvatar,
  uploadImage,
  uploadImages,
  uploadVideo
}
