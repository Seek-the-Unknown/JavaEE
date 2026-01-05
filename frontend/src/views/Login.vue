<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 style="text-align: center; margin-bottom: 20px;">系统登录</h2>
      <el-form :model="form" label-width="60px">
        <el-form-item label="账号">
          <el-input v-model="form.username" placeholder="admin / user" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="123" @keyup.enter="handleLogin" />
        </el-form-item>
        <el-button type="primary" style="width: 100%; margin-top: 10px;" @click="handleLogin">登录</el-button>
      </el-form>
      <div style="text-align: right; margin-top: 10px;">
        <el-button link type="primary" @click="$router.push('/register')">没有账号？去注册</el-button>
      </div>
    </el-card>

  </div>

</template>

<script setup>
// <script setup> 部分修改
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const form = ref({ username: '', password: '' })

const handleLogin = async () => {
  if(!form.value.username || !form.value.password) return ElMessage.warning('请输入账号密码')

  try {
    // ★★★ 修改 1：接口路径改为 /api/user/login
    const res = await axios.post('http://localhost:8888/api/user/login', form.value)

    if(res.data.code === 200) {
      ElMessage.success('登录成功')

      // ★★★ 修改 2：保存 Token 和 用户信息
      const { token, user } = res.data.data
      localStorage.setItem('token', token)
      localStorage.setItem('user', JSON.stringify(user))

      router.push('/')
      setTimeout(() => location.reload(), 500)
    } else {
      ElMessage.error(res.data.msg || '登录失败')
    }
  } catch (e) {
    ElMessage.error('登录失败')
  }
}
</script>

<style scoped>
.login-container {
  display: flex; justify-content: center; align-items: center; height: 80vh;
}
.login-card { width: 400px; padding: 20px; }
</style>