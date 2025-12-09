<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="card-header">
        <h1 class="auth-title">{{ title }}</h1>
        <p class="auth-subtitle">{{ subtitle }}</p>
      </div>
      <div class="card-body">
        <!-- 登录表单 -->
        <form v-if="mode === 'login'" @submit.prevent="handleSubmit">
          <div class="form-group">
            <label for="username">用户名</label>
            <input
              id="username"
              type="text"
              v-model="form.username"
              required
              placeholder="请输入用户名"
            />
          </div>
          <div class="form-group">
            <label for="password">密码</label>
            <input
              id="password"
              type="password"
              v-model="form.password"
              required
              placeholder="请输入密码"
            />
          </div>
          <div class="form-footer">
            <a
              href="#"
              class="forgot-password"
              @click.prevent="switchMode('forgot')"
              >忘记密码?</a
            >
          </div>
          <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
          <button type="submit" class="submit-button" :disabled="isLoading">
            {{ isLoading ? "登录中..." : "登 录" }}
          </button>
          <p class="switch-mode-text">
            没有账户?
            <a href="#" @click.prevent="switchMode('register')">立即注册</a>
          </p>
        </form>
        <!-- 注册表单 -->
        <form v-if="mode === 'register'" @submit.prevent="handleSubmit">
          <div class="form-group">
            <label for="reg-username">用户名</label>
            <input
              id="reg-username"
              type="text"
              v-model="form.username"
              required
              placeholder="设置用户名"
            />
          </div>
          <div class="form-group">
            <label for="reg-password">密码</label>
            <input
              id="reg-password"
              type="password"
              v-model="form.password"
              required
              placeholder="设置密码"
            />
          </div>
          <div class="form-group">
            <label for="confirmPassword">确认密码</label>
            <input
              id="confirmPassword"
              type="password"
              v-model="form.confirmPassword"
              required
              placeholder="确认密码"
            />
          </div>
          <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
          <button type="submit" class="submit-button" :disabled="isLoading">
            {{ isLoading ? "注册中..." : "创建账户" }}
          </button>
          <p class="switch-mode-text">
            已有账户?
            <a href="#" @click.prevent="switchMode('login')">返回登录</a>
          </p>
        </form>
        <!-- 忘记密码表单 -->
        <form v-if="mode === 'forgot'" @submit.prevent="handleForgotPassword">
          <div class="form-group">
            <label for="email">邮箱地址</label>
            <input
              id="email"
              type="email"
              v-model="form.email"
              required
              placeholder="请输入注册邮箱"
            />
          </div>
          <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
          <p v-if="successMessage" class="success-message">
            {{ successMessage }}
          </p>
          <button type="submit" class="submit-button" :disabled="isLoading">
            {{ isLoading ? "发送中..." : "发送重置链接" }}
          </button>
          <p class="switch-mode-text">
            记起密码了?
            <a href="#" @click.prevent="switchMode('login')">返回登录</a>
          </p>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
// ... <script setup> 部分保持不变 ...
import { ref, reactive, computed } from "vue";
import { useRouter } from "vue-router";
import { login as apiLogin, register as apiRegister } from "@/api/user";
import { useUserStore } from "@/store/user";
const mode = ref("login");
const isLoading = ref(false);
const errorMessage = ref("");
const successMessage = ref("");
const router = useRouter();
const { login: storeLogin } = useUserStore();
const form = reactive({
  username: "",
  password: "",
  confirmPassword: "",
  email: "",
});
const title = computed(() => {
  if (mode.value === "login") return "欢迎回来";
  if (mode.value === "register") return "创建新账户";
  return "重置您的密码";
});
const subtitle = computed(() => {
  if (mode.value === "login") return "登录以继续您的预约之旅";
  if (mode.value === "register") return "只需几步，即可开启探索";
  return "我们将向您的邮箱发送重置链接";
});
const switchMode = (newMode) => {
  mode.value = newMode;
  errorMessage.value = "";
  successMessage.value = "";
  Object.assign(form, {
    username: "",
    password: "",
    confirmPassword: "",
    email: "",
  });
};
const handleForgotPassword = () => {
  if (!form.email) {
    errorMessage.value = "请输入您的邮箱地址。";
    return;
  }
  isLoading.value = true;
  errorMessage.value = "";
  successMessage.value = "";
  setTimeout(() => {
    successMessage.value = `一封重置密码的邮件已发送至 ${form.email}，请查收。`;
    isLoading.value = false;
  }, 1500);
};
const handleSubmit = async () => {
  isLoading.value = true;
  errorMessage.value = "";
  successMessage.value = "";
  if (mode.value === "login") {
    try {
      const userData = await apiLogin({
        username: form.username,
        password: form.password,
      });
      storeLogin(userData);
      router.push("/venues");
    } catch (error) {
      errorMessage.value = error.message || "登录失败，请检查您的凭据。";
    }
  } else if (mode.value === "register") {
    if (form.password !== form.confirmPassword) {
      errorMessage.value = "两次输入的密码不一致！";
      isLoading.value = false;
      return;
    }
    try {
      await apiRegister({ username: form.username, password: form.password });
      successMessage.value = "注册成功！现在您可以登录了。";
      setTimeout(() => switchMode("login"), 2000);
    } catch (error) {
      errorMessage.value = error.message || "注册失败，请稍后再试。";
    }
  }
  isLoading.value = false;
};
</script>

<style scoped>
.auth-page {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 20px;
}
.auth-card {
  width: 100%;
  max-width: 420px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.07);
  border: 1px solid var(--color-border);
  overflow: hidden;
}
.card-header {
  text-align: center;
  padding: 32px 40px;
}
.auth-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
}
.auth-subtitle {
  margin: 8px 0 0;
  color: var(--color-text-secondary);
}
.card-body {
  padding: 0 40px 40px;
}
.form-group {
  margin-bottom: 20px;
}
.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  font-size: 14px;
}
.form-group input {
  width: 100%;
  padding: 10px 14px;
  box-sizing: border-box;
  background-color: var(--color-bg-main);
  border: 1px solid var(--color-border);
  border-radius: var(--border-radius);
  font-size: 14px;
}
.form-footer {
  text-align: right;
  margin-bottom: 20px;
}
.forgot-password {
  color: var(--color-primary-text);
  text-decoration: none;
  font-size: 14px;
}
.error-message,
.success-message {
  margin-bottom: 15px;
  text-align: center;
  padding: 10px;
  border-radius: 6px;
  font-size: 14px;
}
.error-message {
  color: #d8000c;
  background-color: #ffbaba;
}
.success-message {
  color: #270;
  background-color: #dff2bf;
}
.submit-button {
  width: 100%;
  padding: 12px;
  background: var(--gradient-primary);
  color: white;
  border: none;
  border-radius: var(--border-radius);
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: opacity 0.3s;
}
.submit-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.switch-mode-text {
  margin-top: 25px;
  text-align: center;
  color: var(--color-text-secondary);
  font-size: 14px;
}
.switch-mode-text a {
  color: var(--color-primary-text);
  text-decoration: none;
  font-weight: 500;
}
</style>
