import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

import BesskyUI from 'bessky-ui'
import 'bessky-ui/dist/bessky-ui.css'
import { routes } from './router'
Vue.use(BesskyUI, {
  routes,
  baseUrl: '/warehouse/api',
  settings: {
    title: '仓库管理系统'
  }
})

Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
