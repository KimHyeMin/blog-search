import Vue from 'vue'
import App from './App.vue'
import router from "./router"
import store from './store';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'

Vue.config.productionTip = false
Vue.use(BootstrapVue)
Vue.use(BootstrapVueIcons)

new Vue({
  el: "#app",
  render: h => h(App),
  store,
  router,
})
