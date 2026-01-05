<template>
  <div class="booking-container">
    <h2>我的预约订单</h2>

    <el-card v-for="item in bookingList" :key="item.id" class="booking-card">
      <div class="card-header">
        <span class="time">下单时间：{{ formatTime(item.createTime) }}</span>
        <el-tag :type="item.status === 1 ? 'success' : 'info'">
          {{ item.status === 1 ? '预约成功' : '已取消' }}
        </el-tag>
      </div>

      <div class="card-content">
        <p><strong>预约时间：</strong> {{ formatTime(item.startTime) }} 至 {{ formatTime(item.endTime) }}</p>
        <p><strong>支付金额：</strong> <span style="color: red; font-weight: bold;">￥{{ item.totalCost }}</span></p>
      </div>

      <div class="card-footer" v-if="item.status === 1">
        <el-button type="danger" size="small" @click="handleCancel(item.id)">取消预约</el-button>
      </div>
    </el-card>

    <el-empty v-if="bookingList.length === 0" description="暂无订单" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const bookingList = ref([])

onMounted(loadBookings)

async function loadBookings() {
  try {
    // ★★★ 这里的接口必须对应 BookingController 的 /my
    const res = await axios.get('http://localhost:8888/api/booking/my')
    if (res.data.code === 200) {
      bookingList.value = res.data.data
    }
  } catch (e) {
    ElMessage.error('加载订单失败')
  }
}

const handleCancel = (id) => {
  ElMessageBox.confirm('确定要取消这个预约吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消' })
      .then(async () => {
        const res = await axios.post(`http://localhost:8888/api/booking/cancel/${id}`)
        if (res.data.code === 200) {
          ElMessage.success('取消成功')
          loadBookings() // 刷新列表
        } else {
          ElMessage.error(res.data.msg)
        }
      })
}

// 格式化时间显示，去掉 'T'
const formatTime = (str) => {
  return str ? str.replace('T', ' ') : ''
}
</script>

<style scoped>
.booking-container { max-width: 800px; margin: 20px auto; padding: 0 20px; }
.booking-card { margin-bottom: 15px; }
.card-header { display: flex; justify-content: space-between; border-bottom: 1px solid #eee; padding-bottom: 10px; margin-bottom: 10px; }
.time { color: #888; font-size: 14px; }
.card-footer { text-align: right; margin-top: 10px; border-top: 1px solid #f5f5f5; padding-top: 10px; }
</style>