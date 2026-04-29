import userApi from '@/api/user'

export default {
  namespaced: true,
  state: {
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}'),
    roles: JSON.parse(localStorage.getItem('roles') || '[]')  // ✅ 从 localStorage 读取
  },
  mutations: {
    SET_TOKEN(state, token) {
      state.token = token
      localStorage.setItem('token', token)
    },
    SET_USER_INFO(state, userInfo) {
      state.userInfo = userInfo
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
    },
    SET_ROLES(state, roles) {
      state.roles = roles
      localStorage.setItem('roles', JSON.stringify(roles))  // ✅ 持久化存储
    },
    CLEAR_USER(state) {
      state.token = ''
      state.userInfo = {}
      state.roles = []
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      localStorage.removeItem('roles')  // ✅ 清除 roles
    }
  },
  actions: {
    // 登录
    async login({ commit }, loginForm) {
      try {
        const res = await userApi.login(loginForm)
        // 响应拦截器返回的是response.data，后端数据在res.data中
        commit('SET_TOKEN', res.data.token)
        commit('SET_USER_INFO', res.data.userInfo)
        
        // ✅ 如果后端返回了 roles，则使用；否则设置默认角色
        const roles = res.data.roles || ['admin']
        commit('SET_ROLES', roles)
        
        return res
      } catch (error) {
        return Promise.reject(error)
      }
    },
    
    // 登出
    async logout({ commit }) {
      // 直接清除本地数据，不调用后端接口（避免401循环）
      commit('CLEAR_USER')
      return Promise.resolve()
    },
    
    // 获取用户信息
    async getUserInfo({ commit }) {
      try {
        const res = await userApi.getUserInfo()
        commit('SET_USER_INFO', res.data.userInfo || res.data)
        
        // ✅ 如果后端返回了 roles，则使用；否则保持当前角色
        if (res.data.roles) {
          commit('SET_ROLES', res.data.roles)
        }
        
        return res
      } catch (error) {
        return Promise.reject(error)
      }
    }
  }
}
