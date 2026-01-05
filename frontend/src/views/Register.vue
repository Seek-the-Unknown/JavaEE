<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 style="text-align: center; margin-bottom: 20px;">新用户注册</h2>
      <el-form :model="form" label-width="70px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="confirmPwd" type="password" placeholder="再次输入密码" />
        </el-form-item>

        <el-button type="success" style="width: 100%; margin-top: 10px;" @click="handleRegister">立即注册</el-button>
        <div style="text-align: right; margin-top: 10px;">
          <el-button link type="primary" @click="$router.push('/login')">已有账号？去登录</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const form = ref({ username: '', password: '' })
const confirmPwd = ref('')

const handleRegister = async () => {
  if(!form.value.username || !form.value.password) return ElMessage.warning('请填写完整')
  if(form.value.password !== confirmPwd.value) return ElMessage.warning('两次密码不一致')

  try {
    const res = await axios.post('http://localhost:8888/api/user/register', form.value)
    if(res.data.code === 200) {
      ElMessage.success('注册成功！请登录')
      router.push('/login')
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch (e) {
    ElMessage.error('注册失败，请稍后重试')
  }
}
</script>

<style scoped>
.login-container {
  display: flex; justify-content: center; align-items: center; height: 80vh;
  background-color: #f0f2f5;
}
.login-card { width: 400px; padding: 20px; }
</style>