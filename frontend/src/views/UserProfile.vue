<template>
  <div style="display: flex; justify-content: center; padding-top: 40px;">
    <el-card style="width: 500px;">
      <div style="display: flex; align-items: center; gap: 20px;">
        <el-avatar :size="80" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
        <div>
          <h2>{{ userInfo.username }}</h2>
          <el-tag :type="userInfo.role === 'admin' ? 'danger' : 'success'">
            {{ userInfo.role === 'admin' ? '管理员' : '普通用户' }}
          </el-tag>
        </div>
      </div>
      <el-divider />

      <div style="text-align: center; padding: 20px 0;">
        <p style="color: #909399;">账户余额</p>
        <h1 style="color: #f56c6c; font-size: 40px; margin: 10px 0;">¥ {{ userInfo.balance }}</h1>
        <el-button type="primary" round @click="dialogVisible = true">充值 / 加钱</el-button>
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="余额充值" width="30%">
      <div style="text-align: center;">
        <el-input-number v-model="amount" :min="100" :step="100" size="large" />
        <p style="margin-top: 10px; color: #999;">演示系统，点击确认直接到账</p>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRecharge">确认充值</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const userInfo = ref({})
const dialogVisible = ref(false)
const amount = ref(100)
const localUser = JSON.parse(localStorage.getItem('user'))

// 获取最新数据
const loadUser = async () => {
  if(!localUser) return
  const res = await axios.get(`http://localhost:8888/api/user/${localUser.id}`)
  userInfo.value = res.data.data
  // 同步更新本地缓存，防止刷新后变回旧值
  localStorage.setItem('user', JSON.stringify(res.data.data))
}

const handleRecharge = async () => {
  try {
    await axios.post('http://localhost:8888/api/recharge', {
      userId: userInfo.value.id,
      amount: amount.value
    })
    ElMessage.success('充值成功')
    dialogVisible.value = false
    loadUser() // 刷新显示
  } catch(e) {
    ElMessage.error('充值失败')
  }
}

onMounted(loadUser)
</script>