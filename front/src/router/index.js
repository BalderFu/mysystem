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
    },{
        path: 'openapi',
        name: 'OpenApi',
        component: () => import('@/views/openapi/index'),
    },{
        path: 'sensitivetext',
        name: 'SensitiveText',
        component: () => import('@/views/sensitivetext/index'),
    },{
        path: 'sensitivetextcheck',
        name: 'SensitiveTextCheck',
        component: () => import('@/views/sensitivetextcheck/index'),
    },{
        path: 'sensitiveupload',
        name: 'SensitiveUpload',
        component: () => import('@/views/sensitiveupload/index'),
    },{
        path: 'history',
        name: 'History',
        component: () => import('@/views/history/index'),
    }]
}, {
    path: "/login",
    component: () => import('@/views/login/index'),
}]

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
