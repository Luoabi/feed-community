import axios from 'axios'
import { ElMessage } from 'element-plus'
import store from '@/store'

// 创建axios实例
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 15000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 添加token
    const token = store.state.user.token
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    
    // 根据后端返回的code判断
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      
      // token过期或未登录
      if (res.code === 401) {
        // 清除本地token和用户信息
        store.commit('user/CLEAR_USER')
        // 跳转到登录页
        if (window.location.pathname !== '/login') {
          window.location.href = '/login'
        }
      }
      
      return Promise.reject(new Error(res.message || '请求失败'))
    } else {
      return res
    }
  },
  error => {
    console.error('响应错误:', error)
    
    // 处理401错误
    if (error.response && error.response.status === 401) {
      store.commit('user/CLEAR_USER')
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }
    
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

// 封装常用请求方法
const request = {
  get(url, config) {
    return service.get(url, config)
  },
  post(url, data, config) {
    return service.post(url, data, config)
  },
  put(url, data, config) {
    return service.put(url, data, config)
  },
  delete(url, config) {
    return service.delete(url, config)
  },
  patch(url, data, config) {
    return service.patch(url, data, config)
  }
}

export default request
