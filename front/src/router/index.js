import Vue from 'vue'
import VueRouter from 'vue-router'
import Layout from '@/layout/index.vue';
import Cookies from "js-cookie";
import Constants from "@/utils/constants";
import {userInfo} from "@/utils/inputs";
Vue.use(VueRouter)

const routes = [{
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [{
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index'),
        meta: { title: '首页', icon: 'el-icon-s-home' }
    }
    ,{
        path: 'personal',
        name: 'Personal',
        component: () => import('@/views/personal/index'),
        meta: { title: '个人中心', icon: 'el-icon-user' }
    },{
        path: 'user',
        name: 'User',
        component: () => import('@/views/user/index'),
        meta: { 
            title: '用户管理',
            icon: 'user',
            roles: ['管理员'] // 确保与后端返回的角色值完全匹配
        }
    },{
        path: 'log',
        name: 'Log',
        component: () => import('@/views/log/index'),
        meta: { title: '日志管理', icon: 'el-icon-document' }
    }]
}, {
    path: "/login",
    name: "Login",
    component: () => import('@/views/login/index'),
}]

// 修改Vue Router的push方法，避免重复导航错误
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => {
    // 只打印错误，不抛出异常
    console.log('路由导航错误:', err)
    return Promise.resolve(false)
  })
}

// 同样修改replace方法
const originalReplace = VueRouter.prototype.replace
VueRouter.prototype.replace = function replace(location) {
  return originalReplace.call(this, location).catch(err => {
    // 只打印错误，不抛出异常
    console.log('路由导航错误:', err)
    return Promise.resolve(false)
  })
}

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes
})


router.beforeEach((to, from, next) => {
    const token = localStorage.getItem(Constants.ID.USER_TOKEN_KEY);
    if(token == null && to.path != '/login'){
        return next('/login');
    }else {
        // 如果目标路径是登录页面，则不需要判断登录状态
        if (to.path == '/login') {
            next();
        } else{
            // 非登陆页跳转需要有效token
            userInfo().then(resp=>{
                if(resp == null){
                    next('/login');
                }else{
                    // 如果已经登录，继续执行路由
                    localStorage.setItem(Constants.ID.USER_KEY,JSON.stringify(resp));
                    next();
                }
            }).catch(e=>{
                return next('/login');
            })
        }
    }
});

export default router
