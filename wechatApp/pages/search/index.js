const api = require('../../api/index.js')
const { handleImageUrl } = require('../../utils/image.js')

Page({
  data: {
    keyword: '',
    autoFocus: true,
    hasSearched: false,
    searchHistory: [],
    hotSearchList: [
      '人工智能',
      '微信小程序开发',
      '前端技术',
      'Vue3教程',
      'React最佳实践',
      'Node.js后端',
      'Python数据分析',
      'Java Spring Boot'
    ],
    suggestList: [],
    searchResults: [],
    page: 1,
    size: 10,
    loading: false,
    hasMore: true,
    totalCount: 0,
    statusBarHeight: 0,
    navBarHeight: 0
  },

  onLoad(options) {
    // 获取系统信息
    const systemInfo = wx.getSystemInfoSync()
    const statusBarHeight = systemInfo.statusBarHeight || 0
    const navBarHeight = statusBarHeight + 44 // 44是导航栏高度
    
    this.setData({
      statusBarHeight,
      navBarHeight
    })
    
    // 从首页传入的关键词
    if (options.keyword) {
      this.setData({ 
        keyword: options.keyword,
        autoFocus: false
      })
      this.performSearch()
    }
    
    // 加载搜索历史
    this.loadSearchHistory()
  },

  onShow() {
    // 每次显示页面时刷新搜索历史
    this.loadSearchHistory()
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading && this.data.hasSearched) {
      this.loadMoreResults()
    }
  },

  // 加载搜索历史
  loadSearchHistory() {
    try {
      const history = wx.getStorageSync('searchHistory') || []
      this.setData({ searchHistory: history })
    } catch (e) {
      console.error('加载搜索历史失败', e)
    }
  },

  // 保存搜索历史
  saveSearchHistory(keyword) {
    try {
      let history = wx.getStorageSync('searchHistory') || []
      
      // 移除重复项
      history = history.filter(item => item !== keyword)
      
      // 添加到开头
      history.unshift(keyword)
      
      // 最多保存10条
      if (history.length > 10) {
        history = history.slice(0, 10)
      }
      
      wx.setStorageSync('searchHistory', history)
      this.setData({ searchHistory: history })
    } catch (e) {
      console.error('保存搜索历史失败', e)
    }
  },

  // 输入关键词
  onKeywordInput(e) {
    const keyword = e.detail.value
    this.setData({ keyword })
    
    // 实时搜索建议（可选）
    if (keyword) {
      this.getSuggest(keyword)
    } else {
      this.setData({ suggestList: [] })
    }
  },

  // 获取搜索建议
  getSuggest(keyword) {
    // 简单的本地建议，实际项目可以调用后端接口
    const allSuggests = [
      '人工智能技术',
      '人工智能应用',
      '微信小程序开发教程',
      '微信小程序实战',
      '前端技术栈',
      '前端框架对比',
      'Vue3新特性',
      'Vue3组合式API',
      'React Hooks',
      'React性能优化'
    ]
    
    const suggests = allSuggests.filter(item => 
      item.toLowerCase().includes(keyword.toLowerCase())
    ).slice(0, 5)
    
    this.setData({ suggestList: suggests })
  },

  // 清空关键词
  onClearKeyword() {
    this.setData({ 
      keyword: '',
      suggestList: [],
      hasSearched: false,
      searchResults: []
    })
  },

  // 取消搜索
  onCancel() {
    wx.navigateBack()
  },

  // 执行搜索
  onSearch() {
    const keyword = this.data.keyword.trim()
    if (!keyword) {
      wx.showToast({
        title: '请输入搜索关键词',
        icon: 'none',
        duration: 2000
      })
      return
    }
    
    this.performSearch()
  },

  // 执行搜索逻辑
  async performSearch() {
    const keyword = this.data.keyword.trim()
    if (!keyword) return
    
    // 保存搜索历史
    this.saveSearchHistory(keyword)
    
    // 重置状态
    this.setData({
      hasSearched: true,
      page: 1,
      searchResults: [],
      hasMore: true,
      suggestList: []
    })
    
    // 执行搜索
    await this.loadSearchResults()
  },

  // 加载搜索结果
  async loadSearchResults() {
    if (this.data.loading) return
    
    this.setData({ loading: true })
    
    try {
      const params = {
        keyword: this.data.keyword,
        page: this.data.page,
        size: this.data.size,
        status: 'published',
        deleted: 0
      }
      
      // 调用搜索接口
      const res = await api.searchContent(params)
      
      if (res.data) {
        // 处理图片 URL 和时间格式
        const list = res.data.list.map(item => ({
          ...item,
          coverImage: handleImageUrl(item.coverImage, 'cover'),
          authorAvatar: handleImageUrl(item.authorAvatar, 'avatar'),
          createTime: this.formatTime(item.createTime)
        }))
        
        const searchResults = this.data.page === 1 
          ? list 
          : [...this.data.searchResults, ...list]
        
        this.setData({
          searchResults,
          totalCount: res.data.total || searchResults.length,
          hasMore: list.length >= this.data.size,
          loading: false
        })
      }
    } catch (e) {
      console.error('搜索失败', e)
      wx.showToast({
        title: '搜索失败，请重试',
        icon: 'none',
        duration: 2000
      })
      this.setData({ loading: false })
    }
  },

  // 加载更多结果
  loadMoreResults() {
    this.setData({ page: this.data.page + 1 }, () => {
      this.loadSearchResults()
    })
  },

  // 点击搜索建议
  onSuggestTap(e) {
    const keyword = e.currentTarget.dataset.keyword
    this.setData({ keyword })
    this.performSearch()
  },

  // 点击搜索历史
  onHistoryTap(e) {
    const keyword = e.currentTarget.dataset.keyword
    this.setData({ keyword })
    this.performSearch()
  },

  // 删除单条历史
  onDeleteHistory(e) {
    const index = e.currentTarget.dataset.index
    let history = this.data.searchHistory
    history.splice(index, 1)
    
    wx.setStorageSync('searchHistory', history)
    this.setData({ searchHistory: history })
  },

  // 清空搜索历史
  onClearHistory() {
    wx.showModal({
      title: '提示',
      content: '确定要清空搜索历史吗？',
      success: (res) => {
        if (res.confirm) {
          wx.removeStorageSync('searchHistory')
          this.setData({ searchHistory: [] })
          wx.showToast({
            title: '已清空',
            icon: 'success',
            duration: 1500
          })
        }
      }
    })
  },

  // 点击热门搜索
  onHotTap(e) {
    const keyword = e.currentTarget.dataset.keyword
    this.setData({ keyword })
    this.performSearch()
  },

  // 点击内容卡片
  onContentTap(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/content/detail?id=${id}`
    })
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
