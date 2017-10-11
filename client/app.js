import Vue from 'vue'
import { sync } from 'vuex-router-sync'
import App from './components/App'
import router from './router'
import store from './store'
import { test } from 'env.js'

sync(store, router)

console.log(test)

const app = new Vue({
  router,
  store,
  ...App
})

export { app, router, store }
