<template>
  <div class="app-container">
    <header class="main-header">
      <div class="header-left">
        <router-link to="/" class="logo">
          <img src="@/assets/logo.svg" alt="Logo" />
          <span>智能场馆预约</span>
        </router-link>
      </div>
      <nav class="main-nav">
        <router-link to="/venues">场馆列表</router-link>
        <router-link to="/my-orders">我的订单</router-link>
        <router-link to="/about">关于</router-link>
      </nav>
      <div class="header-right">
        <div class="user-profile" v-if="isLoggedIn">
          <span class="username">{{ username }}</span>
          <a href="#" @click.prevent="authAction" class="auth-link">退出登录</a>
        </div>
        <router-link v-else to="/login" class="login-btn"
          >登录/注册</router-link
        >
      </div>
    </header>

    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/store/user";

const { userState, isLoggedIn, logout } = useUserStore();
const router = useRouter();

const username = computed(() =>
  isLoggedIn.value ? userState.userInfo.username : "访客"
);
const authAction = () => {
  if (isLoggedIn.value) {
    if (confirm("您确定要退出登录吗？")) {
      logout();
      router.push("/login");
    }
  }
};
</script>

<style scoped>
/* App.vue 只保留自己独有的布局样式 */
.main-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  height: 72px;
  background-color: var(--color-bg-header);
  border-bottom: 1px solid var(--color-border);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  color: var(--color-text-primary);
  font-weight: 600;
  font-size: 20px;
}
.logo img {
  height: 32px;
  width: 32px;
  background: var(--gradient-primary);
  border-radius: 8px;
  padding: 4px;
  box-sizing: border-box;
}

.main-nav {
  display: flex;
  gap: 32px;
}
.main-nav a {
  text-decoration: none;
  color: var(--color-text-secondary);
  font-weight: 500;
  padding: 26px 0;
  border-bottom: 2px solid transparent;
  transition: all 0.2s;
}
.main-nav a.router-link-exact-active,
.main-nav a:hover {
  color: var(--color-primary-text);
  border-bottom-color: var(--color-primary-text);
}

.header-right {
  display: flex;
  align-items: center;
}
.user-profile {
  display: flex;
  align-items: center;
  gap: 12px;
}
.username {
  font-weight: 500;
}
.auth-link {
  color: var(--color-text-secondary);
  font-size: 14px;
  margin-left: 8px;
}
.login-btn {
  background: var(--gradient-primary);
  color: white;
  padding: 10px 18px;
  border-radius: var(--border-radius);
  text-decoration: none;
  font-weight: 500;
  border: none;
  transition: opacity 0.2s;
}
.login-btn:hover {
  opacity: 0.9;
}

.main-content {
  max-width: 1440px;
  margin: 0 auto;
  padding: 48px 32px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
}
</style>
