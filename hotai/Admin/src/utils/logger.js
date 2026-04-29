import store from '@/store'

/**
 * 操作日志记录工具
 */
class Logger {
  /**
   * 记录操作日志
   * @param {Object} logData 日志数据
   * @param {String} logData.module 操作模块
   * @param {String} logData.operationType 操作类型: create/update/delete/query/login/logout
   * @param {String} logData.description 操作描述
   * @param {Object} logData.params 请求参数
   */
  static log(logData) {
    const userInfo = store.state.user.userInfo
    
    const log = {
      operator: userInfo.nickname || userInfo.username || '未知用户',
      module: logData.module,
      operationType: logData.operationType,
      description: logData.description,
      method: logData.method || 'POST',
      url: logData.url || window.location.href,
      params: JSON.stringify(logData.params || {}),
      ip: '127.0.0.1', // 实际应从后端获取
      browser: this.getBrowserInfo(),
      os: this.getOSInfo(),
      status: 'success',
      operationTime: new Date().toLocaleString(),
      duration: logData.duration || 0
    }

    // 存储到localStorage（实际应发送到后端）
    this.saveLog(log)
    
    console.log('操作日志:', log)
  }

  /**
   * 保存日志到localStorage
   */
  static saveLog(log) {
    try {
      const logs = JSON.parse(localStorage.getItem('operationLogs') || '[]')
      logs.unshift(log)
      // 只保留最近1000条
      if (logs.length > 1000) {
        logs.splice(1000)
      }
      localStorage.setItem('operationLogs', JSON.stringify(logs))
    } catch (error) {
      console.error('保存日志失败:', error)
    }
  }

  /**
   * 获取浏览器信息
   */
  static getBrowserInfo() {
    const ua = navigator.userAgent
    let browser = 'Unknown'
    
    if (ua.indexOf('Chrome') > -1) {
      browser = 'Chrome ' + ua.match(/Chrome\/(\d+)/)?.[1]
    } else if (ua.indexOf('Firefox') > -1) {
      browser = 'Firefox ' + ua.match(/Firefox\/(\d+)/)?.[1]
    } else if (ua.indexOf('Safari') > -1) {
      browser = 'Safari ' + ua.match(/Version\/(\d+)/)?.[1]
    } else if (ua.indexOf('Edge') > -1) {
      browser = 'Edge ' + ua.match(/Edge\/(\d+)/)?.[1]
    }
    
    return browser
  }

  /**
   * 获取操作系统信息
   */
  static getOSInfo() {
    const ua = navigator.userAgent
    let os = 'Unknown'
    
    if (ua.indexOf('Windows NT 10.0') > -1) os = 'Windows 10/11'
    else if (ua.indexOf('Windows NT 6.3') > -1) os = 'Windows 8.1'
    else if (ua.indexOf('Windows NT 6.2') > -1) os = 'Windows 8'
    else if (ua.indexOf('Windows NT 6.1') > -1) os = 'Windows 7'
    else if (ua.indexOf('Mac OS X') > -1) os = 'macOS ' + ua.match(/Mac OS X (\d+_\d+)/)?.[1]?.replace('_', '.')
    else if (ua.indexOf('Linux') > -1) os = 'Linux'
    else if (ua.indexOf('Android') > -1) os = 'Android'
    else if (ua.indexOf('iOS') > -1) os = 'iOS'
    
    return os
  }

  /**
   * 快捷方法：记录新增操作
   */
  static create(module, description, params) {
    this.log({ module, operationType: 'create', description, params })
  }

  /**
   * 快捷方法：记录更新操作
   */
  static update(module, description, params) {
    this.log({ module, operationType: 'update', description, params })
  }

  /**
   * 快捷方法：记录删除操作
   */
  static delete(module, description, params) {
    this.log({ module, operationType: 'delete', description, params })
  }

  /**
   * 快捷方法：记录查询操作
   */
  static query(module, description, params) {
    this.log({ module, operationType: 'query', description, params })
  }

  /**
   * 快捷方法：记录登录操作
   */
  static login(username) {
    this.log({ 
      module: '系统登录', 
      operationType: 'login', 
      description: `用户 ${username} 登录系统`,
      params: { username }
    })
  }

  /**
   * 快捷方法：记录登出操作
   */
  static logout(username) {
    this.log({ 
      module: '系统登出', 
      operationType: 'logout', 
      description: `用户 ${username} 退出系统`,
      params: { username }
    })
  }
}

export default Logger
