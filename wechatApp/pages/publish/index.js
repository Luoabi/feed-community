const api = require('../../api/index.js')

Page({
  data: {
    contentType: 'text', // text, image, video, link
    title: '',
    content: '',
    images: [],
    videoUrl: '',
    linkUrl: '',
    maxImages: 9,
    publishing: false,
    categories: [], // 分类列表
    selectedCategory: null, // 选中的分类
    tags: [], // 标签列表
    selectedTags: [] // 选中的标签
  },

  onLoad() {
    // 加载分类和标签
    this.loadCategories()
    this.loadTags()
  },

  async loadCategories() {
    try {
      console.log('开始加载分类...')
      const res = await api.getCategoryList()
      console.log('分类接口返回:', res)
      
      if (res.data && res.data.list) {
        console.log('分类列表:', res.data.list)
        console.log('分类数量:', res.data.list.length)
        this.setData({ categories: res.data.list })
      } else {
        console.warn('分类数据格式异常:', res)
        this.setData({ categories: [] })
      }
    } catch (e) {
      console.error('加载分类失败', e)
      wx.showToast({
        title: '加载分类失败',
        icon: 'none'
      })
    }
  },

  async loadTags() {
    try {
      console.log('开始加载标签...')
      const res = await api.getTagList()
      console.log('标签接口返回:', res)
      
      if (res.data && res.data.list) {
        console.log('标签列表:', res.data.list)
        console.log('标签数量:', res.data.list.length)
        this.setData({ tags: res.data.list })
      } else {
        console.warn('标签数据格式异常:', res)
        this.setData({ tags: [] })
      }
    } catch (e) {
      console.error('加载标签失败', e)
      wx.showToast({
        title: '加载标签失败',
        icon: 'none'
      })
    }
  },

  onPostTypeChange(e) {
    this.setData({ contentType: e.detail.value })
  },

  onTypeSelect(e) {
    const type = e.currentTarget.dataset.type
    this.setData({ contentType: type })
  },

  onCategorySelect(e) {
    const index = e.detail.value
    const category = this.data.categories[index]
    this.setData({ selectedCategory: category })
  },

  onTagToggle(e) {
    const tagId = e.currentTarget.dataset.id
    const selectedTags = [...this.data.selectedTags]
    const index = selectedTags.indexOf(tagId)
    
    if (index > -1) {
      // 已选中，取消选择
      selectedTags.splice(index, 1)
    } else {
      // 未选中，添加选择（最多5个）
      if (selectedTags.length >= 5) {
        wx.showToast({
          title: '最多选择5个标签',
          icon: 'none'
        })
        return
      }
      selectedTags.push(tagId)
    }
    
    this.setData({ selectedTags })
  },

  onTitleInput(e) {
    this.setData({ title: e.detail.value })
  },

  onContentInput(e) {
    this.setData({ content: e.detail.value })
  },

  onLinkInput(e) {
    this.setData({ linkUrl: e.detail.value })
  },

  chooseImage() {
    const remainCount = this.data.maxImages - this.data.images.length
    if (remainCount <= 0) {
      wx.showToast({
        title: `最多上传${this.data.maxImages}张图片`,
        icon: 'none'
      })
      return
    }

    wx.chooseImage({
      count: remainCount,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const images = [...this.data.images, ...res.tempFilePaths]
        this.setData({ images })
      }
    })
  },

  deleteImage(e) {
    const index = e.currentTarget.dataset.index
    const images = this.data.images.filter((_, i) => i !== index)
    this.setData({ images })
  },

  previewImage(e) {
    const index = e.currentTarget.dataset.index
    wx.previewImage({
      current: this.data.images[index],
      urls: this.data.images
    })
  },

  chooseVideo() {
    wx.chooseVideo({
      sourceType: ['album', 'camera'],
      maxDuration: 60,
      camera: 'back',
      success: (res) => {
        this.setData({ videoUrl: res.tempFilePath })
      }
    })
  },

  deleteVideo() {
    this.setData({ videoUrl: '' })
  },

  // 验证表单
  validateForm() {
    if (!this.data.title.trim()) {
      wx.showToast({
        title: '请输入标题',
        icon: 'none'
      })
      return false
    }

    if (this.data.title.length < 5) {
      wx.showToast({
        title: '标题至少5个字符',
        icon: 'none'
      })
      return false
    }

    if (!this.data.content.trim()) {
      wx.showToast({
        title: '请输入内容',
        icon: 'none'
      })
      return false
    }

    if (this.data.content.length < 10) {
      wx.showToast({
        title: '内容至少10个字符',
        icon: 'none'
      })
      return false
    }

    // 图片类型必须有图片
    if (this.data.contentType === 'image' && this.data.images.length === 0) {
      wx.showToast({
        title: '请至少上传一张图片',
        icon: 'none'
      })
      return false
    }

    // 视频类型必须有视频
    if (this.data.contentType === 'video' && !this.data.videoUrl) {
      wx.showToast({
        title: '请上传视频',
        icon: 'none'
      })
      return false
    }

    // 链接类型必须有链接
    if (this.data.contentType === 'link' && !this.data.linkUrl.trim()) {
      wx.showToast({
        title: '请输入链接地址',
        icon: 'none'
      })
      return false
    }

    // 验证链接格式
    if (this.data.contentType === 'link') {
      const urlPattern = /^https?:\/\/.+/
      if (!urlPattern.test(this.data.linkUrl)) {
        wx.showToast({
          title: '请输入正确的链接格式',
          icon: 'none'
        })
        return false
      }
    }

    return true
  },

  // 保存草稿
  onSaveDraft() {
    const draft = {
      contentType: this.data.contentType,
      title: this.data.title,
      content: this.data.content,
      images: this.data.images,
      videoUrl: this.data.videoUrl,
      linkUrl: this.data.linkUrl,
      selectedCategory: this.data.selectedCategory,
      selectedTags: this.data.selectedTags,
      saveTime: new Date().getTime()
    }
    
    wx.setStorageSync('publish_draft', draft)
    wx.showToast({
      title: '草稿已保存',
      icon: 'success'
    })
  },

  // 加载草稿
  onLoadDraft() {
    const draft = wx.getStorageSync('publish_draft')
    if (draft) {
      wx.showModal({
        title: '提示',
        content: '是否加载上次保存的草稿？',
        success: (res) => {
          if (res.confirm) {
            this.setData({
              contentType: draft.contentType,
              title: draft.title,
              content: draft.content,
              images: draft.images || [],
              videoUrl: draft.videoUrl || '',
              linkUrl: draft.linkUrl || '',
              selectedCategory: draft.selectedCategory,
              selectedTags: draft.selectedTags || []
            })
            wx.showToast({
              title: '草稿已加载',
              icon: 'success'
            })
          }
        }
      })
    } else {
      wx.showToast({
        title: '暂无草稿',
        icon: 'none'
      })
    }
  },

  async onPublish() {
    // 验证表单
    if (!this.validateForm()) {
      return
    }

    if (this.data.publishing) return

    this.setData({ publishing: true })

    wx.showLoading({
      title: '发布中...',
      mask: true
    })

    try {
      // 上传图片
      let uploadedImages = []
      if (this.data.images.length > 0) {
        uploadedImages = await this.uploadImages()
      }

      // 上传视频
      let uploadedVideo = ''
      if (this.data.videoUrl) {
        uploadedVideo = await this.uploadVideo()
      }

      // 获取用户信息
      const userInfo = wx.getStorageSync('userInfo') || {}
      
      // ✅ 验证用户信息
      if (!userInfo || !userInfo.id) {
        wx.hideLoading()
        wx.showModal({
          title: '提示',
          content: '请先登录',
          success: (res) => {
            if (res.confirm) {
              wx.navigateTo({
                url: '/pages/login/index'
              })
            }
          }
        })
        this.setData({ publishing: false })
        return
      }

      console.log('发布时的用户信息:', userInfo)

      // 获取选中的标签名称
      const selectedTagNames = this.data.tags
        .filter(tag => this.data.selectedTags.includes(tag.id))
        .map(tag => tag.tagName)
        .join(',')

      // ✅ 确定 content_type：post（文本）、image（图片）、video（视频）
      let contentType = 'post' // 默认文本类型
      if (this.data.contentType === 'image') {
        contentType = 'image'
      } else if (this.data.contentType === 'video') {
        contentType = 'video'
      } else if (this.data.contentType === 'link') {
        contentType = 'post' // 链接也归类为 post
      }

      // ✅ 发布内容 - 只传必要字段
      const contentData = {
        title: this.data.title.trim(),
        content: this.data.content.trim(),
        contentType: contentType,  // post/image/video
        tags: selectedTagNames,
        authorId: userInfo.id,     // ✅ 必须传 author_id
        authorName: userInfo.nickname || userInfo.username, // ✅ 作者名称
        authorAvatar: userInfo.avatar || null, // ✅ 作者头像
        
        // 可选字段
        // ✅ images 直接传数组，后端会自动转换为JSON
        images: uploadedImages.length > 0 ? uploadedImages : null,
        videoUrl: uploadedVideo || null,
        linkUrl: this.data.linkUrl.trim() || null,
        categoryId: this.data.selectedCategory?.id || null,
        categoryName: this.data.selectedCategory?.categoryName || null
      }

      console.log('发布数据：', contentData)

      await api.publishContent(contentData)

      wx.hideLoading()
      
      // 清除草稿
      wx.removeStorageSync('publish_draft')

      wx.showToast({
        title: '发布成功，等待审核',
        icon: 'success',
        duration: 2000
      })

      // 重置表单
      this.setData({
        title: '',
        content: '',
        images: [],
        videoUrl: '',
        linkUrl: '',
        selectedCategory: null,
        selectedTags: [],
        publishing: false
      })

      setTimeout(() => {
        wx.switchTab({
          url: '/pages/community/posts'
        })
      }, 2000)

    } catch (e) {
      console.error('发布失败', e)
      wx.hideLoading()
      wx.showToast({
        title: e.message || '发布失败，请重试',
        icon: 'none',
        duration: 2000
      })
      this.setData({ publishing: false })
    }
  },

  uploadImages() {
    return new Promise((resolve, reject) => {
      const token = wx.getStorageSync('token')
      if (!token) {
        reject(new Error('请先登录'))
        return
      }

      // 如果没有图片，直接返回空数组
      if (!this.data.images || this.data.images.length === 0) {
        resolve([])
        return
      }

      let uploadedCount = 0
      const totalCount = this.data.images.length
      
      const uploadPromises = this.data.images.map((imagePath, index) => {
        return new Promise((resolveUpload, rejectUpload) => {
          wx.uploadFile({
            url: 'http://localhost:8080/api/upload/image',
            filePath: imagePath,
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
                console.log(`图片${index + 1}上传响应:`, res.data)
                const data = JSON.parse(res.data)
                if (data.code === 200) {
                  resolveUpload(data.data)
                } else {
                  console.error(`图片${index + 1}上传失败:`, data.message)
                  rejectUpload(new Error(data.message || '上传失败'))
                }
              } catch (e) {
                console.error(`图片${index + 1}解析失败:`, e, res.data)
                rejectUpload(new Error('响应解析失败'))
              }
            },
            fail: (err) => {
              console.error(`图片${index + 1}上传失败:`, err)
              rejectUpload(new Error('网络请求失败'))
            }
          })
        })
      })

      wx.showLoading({
        title: `上传图片中(0/${totalCount})`,
        mask: true
      })

      // 使用 Promise.all 等待所有图片上传完成
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
  },

  uploadVideo() {
    return new Promise((resolve, reject) => {
      const token = wx.getStorageSync('token')
      if (!token) {
        reject(new Error('请先登录'))
        return
      }

      // 如果没有视频，直接返回空字符串
      if (!this.data.videoUrl) {
        resolve('')
        return
      }

      wx.showLoading({
        title: '上传视频中...',
        mask: true
      })

      wx.uploadFile({
        url: 'http://localhost:8080/api/upload/video',
        filePath: this.data.videoUrl,
        name: 'file',
        header: {
          'token': token
        },
        success: (res) => {
          wx.hideLoading()
          try {
            console.log('视频上传响应:', res.data)
            const data = JSON.parse(res.data)
            if (data.code === 200) {
              resolve(data.data)
            } else {
              reject(new Error(data.message || '视频上传失败'))
            }
          } catch (e) {
            console.error('视频上传响应解析失败:', e, res.data)
            reject(new Error('响应解析失败'))
          }
        },
        fail: (err) => {
          wx.hideLoading()
          console.error('视频上传失败:', err)
          reject(new Error('网络请求失败'))
        }
      })
    })
  }
})
