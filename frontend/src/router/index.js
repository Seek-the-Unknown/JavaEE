import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import VenueAdd from '../views/VenueAdd.vue'
import MyBooking from '../views/MyBooking.vue'
import UserProfile from '../views/UserProfile.vue'
import VenueDetail from '../views/VenueDetail.vue'
import MyVenues from '../views/MyVenues.vue'
import AdminDashboard from '../views/AdminDashboard.vue' // 1. 引入

const routes = [
    { path: '/', name: 'Home', component: Home },
    { path: '/login', name: 'Login', component: Login },
    { path: '/register', name: 'Register', component: Register },
    { path: '/add', name: 'VenueAdd', component: VenueAdd },
    { path: '/my-bookings', name: 'MyBooking', component: MyBooking },
    { path: '/profile', name: 'UserProfile', component: UserProfile },
    { path: '/venue/:id', name: 'VenueDetail', component: VenueDetail },
    { path: '/my-venues', name: 'MyVenues', component: MyVenues },
    // 2. 新增管理员路由
    { path: '/admin', name: 'AdminDashboard', component: AdminDashboard },
    { path: '/edit/:id', name: 'VenueEdit', component: VenueAdd }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router