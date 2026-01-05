<template>
  <el-container class="app-layout">
    <el-header class="nav-header">
      <div class="header-inner">
        <div class="logo" @click="$router.push('/')">
          <div class="logo-icon-box">
            <el-icon :size="22"><Trophy /></el-icon>
          </div>
          <span class="logo-text">智能场馆预约</span>
        </div>

        <div class="nav-right">
          <template v-if="user">
            <div class="nav-menu">
              <el-button link class="nav-btn" @click="$router.push('/')">首页</el-button>
              <el-button link class="nav-btn" @click="$router.push('/add')">发布场馆</el-button>
              <el-button link class="nav-btn" @click="$router.push('/my-bookings')">我的订单</el-button>
            </div>

            <el-dropdown trigger="click" style="margin-left: 20px">
              <span class="user-dropdown">
                <el-avatar :size="32" class="custom-avatar">
                  {{ user.username ? user.username.charAt(0).toUpperCase() : 'U' }}
                </el-avatar>
                <span class="username">{{ user.username }}</span>
                <el-tag v-if="user.role === 'admin'" size="small" effect="dark" type="danger" round class="role-tag">管</el-tag>
                <el-icon class="el-icon--right"><CaretBottom /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu class="custom-dropdown">
                  <el-dropdown-item v-if="user.role === 'admin'" @click="$router.push('/admin')" class="admin-item">
                    <el-icon><Setting /></el-icon> 系统管理
                  </el-dropdown-item>

                    <el-dropdown-item v-if="user.role === 'USER'" @click="$router.push('/profile')" class = "admin-item">
                      <el-icon><Odometer /></el-icon>个人中心
                    </el-dropdown-item>


                  <el-dropdown-item divided @click="logout">
                    <el-icon><SwitchButton /></el-icon> 退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>

          <div v-else>
            <el-button type="primary" round class="login-btn" @click="$router.push('/login')">立即登录</el-button>
          </div>
        </div>
      </div>
    </el-header>

    <el-main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </el-main>
  </el-container>
</template>

<!--<script setup>-->
<!--import { ref } from 'vue'-->
<!--import { useRouter } from 'vue-router'-->
<!--import { Setting, Trophy, CaretBottom, SwitchButton } from '@element-plus/icons-vue'-->

<!--const router = useRouter()-->
<!--const user = ref(null)-->
<!--try {-->
<!--  const storedUser = localStorage.getItem('user')-->
<!--  if (storedUser) user.value = JSON.parse(storedUser)-->
<!--} catch (e) {-->
<!--  console.error('User parse error', e)-->
<!--}-->

<!--const logout = () => {-->
<!--  localStorage.removeItem('user')-->
<!--  window.location.href = '/login'-->
<!--}-->
<!--</script>-->

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { Setting, Trophy, CaretBottom, SwitchButton } from '@element-plus/icons-vue'

const router = useRouter()
const user = ref(null)

// --- 核心逻辑 1：定义一个读取用户信息的函数 ---
const refreshUser = () => {
  try {
    const storedUser = localStorage.getItem('user')
    if (storedUser) {
      user.value = JSON.parse(storedUser)
    } else {
      user.value = null // 如果本地没有数据，确保内存状态也是空的
    }
  } catch (e) {
    console.error('User parse error', e)
    user.value = null
  }
}

// --- 核心逻辑 2：监听登录成功事件 (来自 Login.vue) ---
const handleLoginSuccess = () => {
  refreshUser()
}

// 组件挂载时：读取一次数据，并开始监听登录事件
onMounted(() => {
  refreshUser()
  window.addEventListener('login-success', handleLoginSuccess)
})

// 组件卸载时：移除监听
onUnmounted(() => {
  window.removeEventListener('login-success', handleLoginSuccess)
})

// --- 核心逻辑 3：退出登录 ---
const logout = () => {
  // 1. 清除硬盘数据 (Local Storage)
  localStorage.removeItem('user')
  localStorage.removeItem('token')

  // 2. ★★★ 关键修复：手动清空内存数据 (响应式变量) ★★★
  // 这一步会让导航栏立刻从 "头像" 变成 "登录按钮"
  user.value = null

  // 3. 跳转回登录页 (页面不刷新)
  router.push('/login')
}
</script>

<style scoped>
/* 全局布局容器 */
.app-layout {
  min-height: 100vh;
  background-color: #f5f7fa;
}

/* --- 头部样式 --- */
.nav-header {
  padding: 0;
  height: 64px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-inner {
  /* 【修改核心】去掉了 max-width: 1200px */
  /* 使用 width: 100% 铺满屏幕，但加一点 padding 防止贴边 */
  width: 100%;
  padding: 0 40px;

  height: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-sizing: border-box; /* 确保 padding 包含在宽度内 */
}

/* Logo */
.logo {
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: opacity 0.3s;
}
.logo:hover { opacity: 0.8; }

.logo-icon-box {
  background: linear-gradient(135deg, #409EFF, #36cfc9);
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: 10px;
  box-shadow: 0 2px 6px rgba(64, 158, 255, 0.3);
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  background: linear-gradient(90deg, #303133, #606266);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  letter-spacing: 0.5px;
}

/* 导航 */
.nav-right { display: flex; align-items: center; }

.nav-menu .nav-btn {
  font-size: 15px;
  color: #606266;
  margin: 0 5px;
  padding: 8px 12px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.nav-menu .nav-btn:hover {
  background-color: #ecf5ff;
  color: #409EFF;
}

/* 用户信息 */
.user-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 20px;
  transition: background 0.3s;
}
.user-dropdown:hover { background: rgba(0,0,0,0.03); }

.custom-avatar {
  background: linear-gradient(135deg, #66b1ff, #409EFF);
  border: 2px solid #fff;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.username {
  margin-left: 8px;
  font-weight: 500;
  color: #303133;
  font-size: 14px;
}

.role-tag {
  margin-left: 6px;
  transform: scale(0.9);
}

.login-btn {
  font-weight: bold;
  padding: 0 24px;
}

/* --- 主体内容 --- */
.main-content {
  /* 【修改核心】去掉了 max-width: 1200px */
  width: 100%;

  /* 左右给 40px 的内边距，保持和头部对齐，同时视觉上变宽 */
  padding: 20px 40px;

  margin: 0 auto;
  min-height: calc(100vh - 64px);
  box-sizing: border-box; /* 确保 padding 包含在宽度内 */
}

/* 动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 下拉菜单 */
.admin-item {
  color: #F56C6C !important;
  font-weight: 500;
}
</style>

