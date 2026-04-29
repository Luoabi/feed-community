import { createStore } from 'vuex'
import user from './modules/user'
import app from './modules/app'
import permission from './modules/permission'

export default createStore({
  modules: {
    user,
    app,
    permission
  }
})
