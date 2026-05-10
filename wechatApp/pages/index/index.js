const api = require('../../api/index.js')
const { handleImageUrl, handleImageList } = require('../../utils/image.js')
const behaviorTracker = require('../../utils/behaviorTracker.js')

Page({
  data: {
    categories: [],
    contents: [],
    currentCategory: null,
    page: 1,
    size: 10,
    loading: false,
    hasMore: true,
    currentPlayingVideoId: null, // 当前正在播放的视频ID
    usePersonalized: false // 是否使用个性化推荐
  },

  onLoad() {
    this.checkPersonalizedMode()
    this.loadCategories()
    this.loadContents()
  },
  
  // ✅ 检查是否使用个性化推荐
  checkPersonalizedMode() {
    const userInfo = wx.getStorageSync('userInfo')
    if (userInfo && userInfo.id) {
      this.setData({ usePersonalized: true })
      console.log('使用个性化推荐模式')
    } else {
      console.log('使用热门推荐模式（未登录）')
    }
  },
  
  // ✅ 页面显示时刷新数据（从详情页返回时）
  onShow() {
    // 如果不是第一次加载，刷新当前页数据
    const pages = getCurrentPages()
    const currentPage = pages[pages.length - 1]
    
    // 判断是否从其他页面返回（不是首次加载）
    if (currentPage.data.contents && currentPage.data.contents.length > 0) {
      console.log('从详情页返回，刷新数据')
      this.refreshCurrentPage()
    }
  },
  
  // ✅ 页面隐藏时暂停所有视频
  onHide() {
    this.pauseAllVideos()
  },

  onPullDownRefresh() {
    console.log('下拉刷新开始')
    // 暂停所有视频
    this.pauseAllVideos()
    
    // 重置分页
    this.setData({ 
      page: 1, 
      hasMore: true,
      contents: [] // 清空现有内容
    })
    
    // 重新加载数据
    Promise.all([
      this.loadCategories(),
      this.loadContents()
    ]).then(() => {
      wx.stopPullDownRefresh()
      wx.showToast({
        title: '刷新成功',
        icon: 'success',
        duration: 1500
      })
    }).catch(() => {
      wx.stopPullDownRefresh()
      wx.showToast({
        title: '刷新失败',
        icon: 'none',
        duration: 1500
      })
    })
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadMoreContents()
    }
  },

  async loadCategories() {
    try {
      const res = await api.getCategoryList()
      if (res.data && res.data.list) {
        // 按照 categoryId 排序
        const sortedCategories = res.data.list.sort((a, b) => a.id - b.id)
        const categories = [{ id: null, categoryName: '全部' }, ...sortedCategories]
        this.setData({ categories, currentCategory: categories[0] })
      }
    } catch (e) {
      console.error('加载分类失败', e)
    }
  },

  async loadContents() {
    if (this.data.loading) return
    
    this.setData({ loading: true })
    try {
      const userInfo = wx.getStorageSync('userInfo')
      
      // ✅ 优先检查是否选择了分类，如果有分类则使用分类筛选
      let res
      if (this.data.currentCategory && this.data.currentCategory.id) {
        // 有分类选择，使用分类筛选
        const params = {
          page: this.data.page,
          size: this.data.size,
          categoryId: this.data.currentCategory.id
        }
        res = await api.getFeedList(params)
        console.log(`使用分类筛选: ${this.data.currentCategory.categoryName}`)
      } else if (this.data.usePersonalized && userInfo && userInfo.id) {
        // 个性化推荐（仅在未选择分类时）
        const params = {
          userId: userInfo.id,
          page: this.data.page,
          size: this.data.size
        }
        res = await api.getPersonalizedFeed(params)
        console.log('使用个性化推荐')
      } else {
        // 热门推荐（未登录或无兴趣画像）
        const params = {
          page: this.data.page,
          size: this.data.size
        }
        res = await api.getFeedList(params)
        console.log('使用热门推荐')
      }
      
      if (res.data) {
        // 处理图片 URL 和时间格式
        const list = res.data.list.map(item => {
          console.log('原始内容数据:', item)
          
          return {
            ...item,
            coverImage: handleImageUrl(item.coverImage, 'cover'),
            authorAvatar: handleImageUrl(item.authorAvatar, 'avatar'),
            images: item.images ? handleImageList(item.images) : null, // ✅ 处理图片数组
            createTime: this.formatTime(item.createTime)
          }
        })
        
        console.log('处理后的内容列表:', list)
        
        const contents = this.data.page === 1 
          ? list 
          : [...this.data.contents, ...list]
        
        this.setData({
          contents,
          hasMore: res.data.list.length >= this.data.size,
          loading: false
        })
      }
    } catch (e) {
      console.error('加载内容失败', e)
      this.setData({ loading: false })
    }
  },

  loadMoreContents() {
    this.setData({ page: this.data.page + 1 }, () => {
      this.loadContents()
    })
  },
  
  // ✅ 刷新当前页数据（保持分页状态，更新点赞、评论等数据）
  async refreshCurrentPage() {
    try {
      const currentPage = this.data.page
      const totalSize = this.data.size * currentPage
      const userInfo = wx.getStorageSync('userInfo')
      
      let res
      if (this.data.currentCategory && this.data.currentCategory.id) {
        // 有分类选择，使用分类筛选
        const params = {
          page: 1,
          size: totalSize,
          categoryId: this.data.currentCategory.id
        }
        res = await api.getFeedList(params)
      } else if (this.data.usePersonalized && userInfo && userInfo.id) {
        // 个性化推荐
        const params = {
          userId: userInfo.id,
          page: 1,
          size: totalSize
        }
        res = await api.getPersonalizedFeed(params)
      } else {
        // 热门推荐
        const params = {
          page: 1,
          size: totalSize
        }
        res = await api.getFeedList(params)
      }
      if (res.data && res.data.list) {
        // 处理图片 URL
        const list = res.data.list.map(item => ({
          ...item,
          coverImage: handleImageUrl(item.coverImage, 'cover'),
          authorAvatar: handleImageUrl(item.authorAvatar, 'avatar'),
          images: item.images ? handleImageList(item.images) : null, // ✅ 处理图片数组
          createTime: this.formatTime(item.createTime)
        }))
        
        // 只更新已加载的内容数量
        const updatedContents = list.slice(0, this.data.contents.length)
        
        this.setData({
          contents: updatedContents,
          hasMore: res.data.list.length >= totalSize
        })
        
        console.log('数据刷新成功，更新了', updatedContents.length, '条内容')
      }
    } catch (e) {
      console.error('刷新数据失败', e)
    }
  },

  onCategoryTap(e) {
    const category = e.currentTarget.dataset.category
    // 切换分类时暂停所有视频
    this.pauseAllVideos()
    
    this.setData({ 
      currentCategory: category, 
      page: 1, 
      hasMore: true,
      contents: []  // 清空内容
    })
    this.loadContents()
  },

  onContentTap(e) {
    const id = e.currentTarget.dataset.id
    
    // ✅ 记录点击行为
    behaviorTracker.trackClick(id, 'feed')
    
    // 暂停当前播放的视频
    if (this.data.currentPlayingVideoId) {
      const videoContext = wx.createVideoContext(`video-${this.data.currentPlayingVideoId}`, this)
      videoContext.pause()
    }
    
    wx.navigateTo({
      url: `/pages/content/detail?id=${id}`
    })
  },
  
  // ✅ 阻止事件冒泡
  stopPropagation(e) {
    // 阻止事件冒泡，视频区域不跳转到详情页
  },
  
  // ✅ 视频开始播放
  onVideoPlay(e) {
    const videoId = e.currentTarget.dataset.id
    console.log('视频开始播放:', videoId)
    
    // 如果有其他视频正在播放，先暂停
    if (this.data.currentPlayingVideoId && this.data.currentPlayingVideoId !== videoId) {
      const videoContext = wx.createVideoContext(`video-${this.data.currentPlayingVideoId}`, this)
      videoContext.pause()
    }
    
    // 更新当前播放的视频ID
    this.setData({
      currentPlayingVideoId: videoId
    })
  },
  
  // ✅ 视频暂停
  onVideoPause(e) {
    const videoId = e.currentTarget.dataset.id
    console.log('视频暂停:', videoId)
  },
  
  // ✅ 视频播放结束
  onVideoEnded(e) {
    const videoId = e.currentTarget.dataset.id
    console.log('视频播放结束:', videoId)
    this.setData({
      currentPlayingVideoId: null
    })
  },
  
  // ✅ 视频播放错误
  onVideoError(e) {
    const videoId = e.currentTarget.dataset.id
    console.error('视频播放错误:', videoId, e.detail)
    wx.showToast({
      title: '视频加载失败',
      icon: 'none',
      duration: 2000
    })
  },
  
  // ✅ 暂停所有视频
  pauseAllVideos() {
    if (this.data.currentPlayingVideoId) {
      const videoContext = wx.createVideoContext(`video-${this.data.currentPlayingVideoId}`, this)
      videoContext.pause()
      this.setData({
        currentPlayingVideoId: null
      })
    }
  },

  // 格式化时间
  formatTime(timeStr) {
    if (!timeStr) return ''
    
    const time = new Date(timeStr)
    const now = new Date()
    const diff = now - time
    
    // 1分钟内
    if (diff < 60000) {
      return '刚刚'
    }
    // 1小时内
    if (diff < 3600000) {
      return Math.floor(diff / 60000) + '分钟前'
    }
    // 24小时内
    if (diff < 86400000) {
      return Math.floor(diff / 3600000) + '小时前'
    }
    // 7天内
    if (diff < 604800000) {
      return Math.floor(diff / 86400000) + '天前'
    }
    // 超过7天显示日期
    const year = time.getFullYear()
    const month = (time.getMonth() + 1).toString().padStart(2, '0')
    const day = time.getDate().toString().padStart(2, '0')
    
    // 同一年只显示月日
    if (year === now.getFullYear()) {
      return `${month}-${day}`
    }
    return `${year}-${month}-${day}`
  }
})
