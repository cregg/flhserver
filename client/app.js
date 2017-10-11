import Vue from 'vue'
import { sync } from 'vuex-router-sync'
import App from './components/App.vue'
import router from './router'
import store from './store'
import ENV from 'env.js'

sync(store, router)

console.log(ENV.apiURL)

const app = new Vue({
  router,
  store,
  ...App
})

export { app, router, store }
