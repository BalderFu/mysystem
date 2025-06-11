import Vue from 'vue'
import App from './App.vue'
import router from './router'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import '@/assets/css/variables.css';
import axios from 'axios';

import '@/styles/main.scss';

axios.defaults.headers.common['Content-Type'] = 'application/json;charset=utf-8';
axios.defaults.baseURL = 'http://localhost:8090';
Vue.prototype.$axios = axios;

Vue.prototype.$eventBus = new Vue();

Vue.use(ElementUI, {
  size: 'medium'
});

Vue.config.productionTip = false

new Vue({
  router,
  render: function (h) { return h(App) }
}).$mount('#app')
