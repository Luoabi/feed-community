export default {
  namespaced: true,
  state: {
    sidebar: {
      opened: localStorage.getItem('sidebarStatus') !== 'closed',
      withoutAnimation: false
    },
    device: 'desktop',
    size: localStorage.getItem('size') || 'default'
  },
  mutations: {
    TOGGLE_SIDEBAR(state) {
      state.sidebar.opened = !state.sidebar.opened
      state.sidebar.withoutAnimation = false
      if (state.sidebar.opened) {
        localStorage.setItem('sidebarStatus', 'opened')
      } else {
        localStorage.setItem('sidebarStatus', 'closed')
      }
    },
    CLOSE_SIDEBAR(state, withoutAnimation) {
      localStorage.setItem('sidebarStatus', 'closed')
      state.sidebar.opened = false
      state.sidebar.withoutAnimation = withoutAnimation
    },
    TOGGLE_DEVICE(state, device) {
      state.device = device
    },
    SET_SIZE(state, size) {
      state.size = size
      localStorage.setItem('size', size)
    }
  },
  actions: {
    toggleSideBar({ commit }) {
      commit('TOGGLE_SIDEBAR')
    },
    closeSideBar({ commit }, { withoutAnimation }) {
      commit('CLOSE_SIDEBAR', withoutAnimation)
    },
    toggleDevice({ commit }, device) {
      commit('TOGGLE_DEVICE', device)
    },
    setSize({ commit }, size) {
      commit('SET_SIZE', size)
    }
  }
}
