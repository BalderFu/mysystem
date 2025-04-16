import Vue from 'vue'
import App from './App.vue'
import router from './router'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import axios from 'axios';

// 引入全局样式
import '@/styles/main.scss';

// 配置 axios
axios.defaults.headers.common['Content-Type'] = 'application/json;charset=utf-8';
Vue.prototype.$axios = axios;

// 使用 Element UI
Vue.use(ElementUI, {
  size: 'medium' // 设置组件默认尺寸
});

// 设置生产环境
Vue.config.productionTip = false

// 创建 Vue 实例
new Vue({
  router,
  render: function (h) { return h(App) }
}).$mount('#app')
