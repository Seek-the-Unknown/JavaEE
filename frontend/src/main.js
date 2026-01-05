import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import axios from 'axios' // 1. 引入 axios

// --- ★★★ 核心修复代码开始 ★★★ ---
// 配置请求拦截器：每次请求自动带上 Token
axios.interceptors.request.use(config => {
    const token = localStorage.getItem('token')
    if (token) {
        config.headers['token'] = token // 必须与后端 LoginInterceptor getHeader("token") 一致
    }
    return config
}, error => {
    return Promise.reject(error)
})

// 配置响应拦截器：Token 失效(401)自动跳转登录页
axios.interceptors.response.use(response => {
    return response
}, error => {
    if (error.response && error.response.status === 401) {
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        window.location.href = '/login'
    }
    return Promise.reject(error)
})
// --- ★★★ 核心修复代码结束 ★★★ ---

const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.use(router)
app.use(ElementPlus)

app.mount('#app')