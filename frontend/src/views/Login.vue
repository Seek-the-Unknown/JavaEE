<template>
  <div class="login-container">
    <div class="login-content">
      <div class="login-header">
        <div class="logo-box">
          <el-icon :size="40" color="#409EFF"><Trophy /></el-icon>
        </div>
        <h2 class="login-title">智能场馆预约系统</h2>
        <p class="login-subtitle">Smart Venue Booking System</p>
      </div>

      <el-card class="login-card" shadow="hover">
        <h3 class="form-title">欢迎登录</h3>

        <el-form ref="loginFormRef" :model="form" :rules="rules" size="large">

          <el-form-item prop="username">
            <el-input
                v-model="form.username"
                placeholder="请输入账号 (admin / user)"
                :prefix-icon="User"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
                v-model="form.password"
                type="password"
                placeholder="请输入密码"
                show-password
                :prefix-icon="Lock"
            />
          </el-form-item>

          <el-form-item prop="code">
            <div class="captcha-row">
              <el-input
                  v-model="form.code"
                  placeholder="验证码"
                  class="captcha-input"
                  :prefix-icon="Key"
                  @keyup.enter="handleLogin"
              />
              <div class="captcha-box">
                <CaptchaCanvas @code-updated="handleCodeUpdated" />
              </div>
            </div>
          </el-form-item>

          <el-button
              type="primary"
              class="login-btn"
              :loading="loading"
              @click="handleLogin"
          >
            立即登录
          </el-button>

          <div class="form-footer">
            <el-button link type="info" @click="$router.push('/register')">
              注册新账号
            </el-button>
            <el-button link type="primary" disabled>忘记密码？</el-button>
          </div>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Key, Trophy } from '@element-plus/icons-vue'

// ★★★ 修改点：同一文件夹下，使用 ./ 引入
import CaptchaCanvas from './CaptchaCanvas.vue'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

// 表单数据
const form = reactive({
  username: '',
  password: '',
  code: ''
})

// 正确的验证码
const generatedCode = ref('')

// 校验规则
const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

// 监听子组件传来的验证码
const handleCodeUpdated = (code) => {
  generatedCode.value = code
}

// 登录逻辑
const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      // 1. 验证码校验 (忽略大小写)
      if (form.code.toLowerCase() !== generatedCode.value.toLowerCase()) {
        ElMessage.error('验证码错误，请重新输入')
        return
      }

      loading.value = true
      try {
        const res = await axios.post('http://localhost:8888/api/user/login', {
          username: form.username,
          password: form.password
        })

        if (res.data.code === 200) {
          ElMessage.success('登录成功')

          const { token, user } = res.data.data
          localStorage.setItem('token', token)
          localStorage.setItem('user', JSON.stringify(user))

          // ★★★ 新增代码：派发事件，通知 App.vue 更新导航栏 ★★★
          window.dispatchEvent(new Event('login-success'))

          setTimeout(() => {
            if (user.role === 'admin') {
              router.push('/admin')
            } else {
              router.push('/')
            }
          }, 500)

        } else {
          ElMessage.error(res.data.msg || '登录失败')
        }
      } catch (error) {
        console.error(error)
        ElMessage.error('服务器连接失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
/* 样式保持不变，直接复用 */
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.login-content {
  display: flex;
  align-items: center;
  gap: 60px;
}

.login-header {
  text-align: center;
  color: #333;
}

.logo-box {
  width: 64px; height: 64px;
  background: #fff;
  border-radius: 16px;
  display: flex; align-items: center; justify-content: center;
  margin: 0 auto 16px;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.login-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0;
  color: #2c3e50;
}

.login-subtitle {
  margin: 8px 0 0;
  color: #7f8c8d;
  font-size: 14px;
  letter-spacing: 1px;
}

.login-card {
  width: 400px;
  border-radius: 12px;
  border: none;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.05);
}

.form-title {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
  font-weight: 500;
}

.captcha-row {
  display: flex;
  width: 100%;
  gap: 12px;
}

.captcha-input {
  flex: 1;
}

.captcha-box {
  width: 100px;
  height: 40px;
  border-radius: 4px;
  overflow: hidden;
  border: 1px solid #dcdfe6;
  cursor: pointer;
  display: flex; /* 防止出现对齐问题 */
}

.login-btn {
  width: 100%;
  font-weight: bold;
  letter-spacing: 1px;
  padding: 20px 0;
  margin-top: 10px;
}

.form-footer {
  margin-top: 16px;
  display: flex;
  justify-content: space-between;
}

@media (max-width: 768px) {
  .login-content {
    flex-direction: column;
    gap: 20px;
  }
  .login-card {
    width: 90vw;
  }
  .login-header {
    margin-bottom: 20px;
  }
}
</style>

