import axios from "axios";
import Constants from "@/utils/constants"
import { Message } from "element-ui";
import router from '@/router'; 
import systemService from '@/services/systemService';

const request = axios.create({
    baseURL: systemService.getApiBaseUrl(),
    timeout:60000
})

request.interceptors.request.use(
    (request) => {
        let token = localStorage.getItem(Constants.ID.USER_TOKEN_KEY)
        if (token) {
            console.log(request.headers);
            request.headers.Authorization = 'Bearer '+token;
        }
        return request;
    },
    (error) => {
        Promise.reject(error);
    }
);
// 响应拦截器
request.interceptors.response.use(
    (response) => {
        if(response.data.code != 200){
            if(response.data.code == 401){
                // **只在当前路由不是 `/login` 时才跳转**
                if (router.currentRoute.path !== '/login') {
                    router.replace('/login'); // **不会触发重复导航**
                }
                // 先 reject 这个请求，阻止后续代码执行
                    // **阻止后续代码执行，但不抛出异常**
                return new Promise(() => {});
            }
            // Message({
            //     message: response.data.message,
            //     type: "error"
            // });
            // return new Promise(() => {});
        
        }
        console.log("response拦截器处理: ",response.data)
        return response.data;
    },
    (error) => {
        Promise.reject(error);
    }
);


export default request;