import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import axios from 'axios' // 引入 axios

// --- ★★★ 新增：全局拦截器配置 ★★★ ---
// 请求拦截器：每次发请求前，自动带上 Token
axios.interceptors.request.use(config => {
    const token = localStorage.getItem('token')
    if (token) {
        config.headers['token'] = token
    }
    return config
}, error => {
    return Promise.reject(error)
})

// 响应拦截器：如果后端返回 401 (Token失效)，自动跳回登录页
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
// --------------------------------------

const app = createApp(App)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.use(router)
app.use(ElementPlus)
app.mount('#app')