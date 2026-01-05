<template>
  <div class="detail-container">
    <div class="header">
      <el-button @click="$router.back()">返回</el-button>
    </div>

    <div v-if="venue" class="content-wrapper">
      <div class="left-section">
        <div class="img-box">
          <img :src="venue.cover || 'https://via.placeholder.com/400'" class="detail-img" />
        </div>

        <el-card class="owner-card" shadow="never">
          <div class="owner-header">
            <span class="card-title">场馆发布者</span>
          </div>
          <div class="owner-info">
            <el-avatar :size="50" style="background:#626aef; font-size: 20px; color: white;">
              {{ owner?.username?.charAt(0).toUpperCase() }}
            </el-avatar>
            <div class="text-info">
              <div class="name">{{ owner?.username || '未知用户' }}</div>
              <el-tag size="small" type="info">已认证房东</el-tag>
            </div>
          </div>
        </el-card>
      </div>

      <div class="right-info">
        <h1 class="title">{{ venue.name }}</h1>
        <el-tag size="large" effect="dark">{{ venue.type }}</el-tag>

        <div class="price-box">
          <span class="price">¥ {{ venue.price }}</span> <span class="unit">/ 小时</span>
        </div>

        <div class="meta-info">
          <p class="location-text">
            <el-icon size="18"><Location /></el-icon>
            <span>{{ venue.region || '暂无位置信息' }}</span>
          </p>

          <p><el-icon size="18"><User /></el-icon> 容纳人数：{{ venue.capacity }} 人</p>
          <p><el-icon size="18"><Timer /></el-icon> 营业状态：09:00 - 22:00</p>
        </div>

        <el-divider content-position="left">场馆描述</el-divider>
        <p class="desc">{{ venue.description || '暂无详细描述' }}</p>

        <div class="action-box">
          <div class="picker-row">
            <span class="label">预约日期:</span>
            <el-date-picker
                v-model="selectedDate"
                type="date"
                placeholder="请选择日期"
                :disabled-date="disabledDate"
                style="width: 100%"
            />
          </div>

          <div class="picker-row">
            <span class="label">具体时段:</span>
            <div style="display: flex; gap: 10px; flex: 1;">
              <el-time-select
                  v-model="startTimeStr"
                  start="09:00"
                  step="01:00"
                  end="22:00"
                  placeholder="开始时间"
                  style="flex: 1"
              />
              <span style="align-self: center;">至</span>
              <el-time-select
                  v-model="endTimeStr"
                  start="09:00"
                  step="01:00"
                  end="22:00"
                  :min-time="startTimeStr"
                  placeholder="结束时间"
                  style="flex: 1"
              />
            </div>
          </div>

          <div class="total-row" v-if="totalPrice > 0">
            <span>预计总价:</span>
            <span class="total-price">¥ {{ totalPrice }}</span>
            <span class="total-hours">({{ totalHours }} 小时)</span>
          </div>

          <el-button
              type="primary"
              size="large"
              style="width: 100%; margin-top: 20px; font-weight:bold; height: 50px;"
              @click="handleBook"
              :disabled="!canBook"
          >
            立即预约
          </el-button>
        </div>
      </div>
    </div>

    <div v-else v-loading="true" style="height: 300px;"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Timer, Location } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const venue = ref(null)
const owner = ref(null)
const currentUser = JSON.parse(localStorage.getItem('user'))

const selectedDate = ref(null)
const startTimeStr = ref('')
const endTimeStr = ref('')

const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

const canBook = computed(() => {
  return selectedDate.value && startTimeStr.value && endTimeStr.value && (endTimeStr.value > startTimeStr.value)
})

const totalHours = computed(() => {
  if (!canBook.value) return 0
  const startHour = parseInt(startTimeStr.value.split(':')[0])
  const endHour = parseInt(endTimeStr.value.split(':')[0])
  return endHour - startHour
})

const totalPrice = computed(() => {
  if (!venue.value || totalHours.value <= 0) return 0
  return (venue.value.price * totalHours.value).toFixed(2)
})

const loadDetail = async () => {
  const id = route.params.id
  try {
    const res = await axios.get(`http://localhost:8888/api/venue/${id}`)
    if(res.data.code === 200) {
      venue.value = res.data.data.venue
      owner.value = res.data.data.owner
    } else {
      ElMessage.error('场馆不存在')
      router.push('/')
    }
  } catch (e) {
    ElMessage.error('加载失败')
  }
}

// ★★★ 核心修复：手动格式化时间为 "yyyy-MM-dd HH:mm:ss" ★★★
// 解决后端 jackson 解析报错的问题
const formatDateStr = (dateObj, timeStr) => {
  const y = dateObj.getFullYear()
  const m = (dateObj.getMonth() + 1).toString().padStart(2, '0')
  const d = dateObj.getDate().toString().padStart(2, '0')
  // 拼接成 "2023-10-25 14:00:00"
  return `${y}-${m}-${d} ${timeStr}:00`
}

const handleBook = async () => {
  const token = localStorage.getItem('token')
  if(!token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    await ElMessageBox.confirm(
        `日期：${selectedDate.value.toLocaleDateString()}\n时段：${startTimeStr.value} - ${endTimeStr.value} (${totalHours.value}小时)\n总费用：¥${totalPrice.value}\n确认立即支付吗？`,
        '支付确认',
        { confirmButtonText: '确认支付', cancelButtonText: '取消', type: 'warning' }
    )

    // 使用修复后的格式化函数
    const finalStart = formatDateStr(selectedDate.value, startTimeStr.value)
    const finalEnd = formatDateStr(selectedDate.value, endTimeStr.value)

    const res = await axios.post('http://localhost:8888/api/booking/create', {
      venueId: venue.value.id,
      totalCost: totalPrice.value, // 最好把钱也传给后端校验
      startTime: finalStart, // 字符串格式
      endTime: finalEnd      // 字符串格式
    })

    if (res.data.code === 200) {
      ElMessage.success('预约成功！')
      // 跳转到“我的订单”页
      router.push('/my-bookings')
    } else {
      ElMessage.error(res.data.msg || '预约失败')
    }

  } catch (e) {
    if(e !== 'cancel') {
      console.error(e)
      ElMessage.error(e.response?.data?.msg || '操作错误')
    }
  }
}

onMounted(loadDetail)
</script>

<style scoped>
.detail-container { max-width: 1100px; margin: 30px auto; background: #fff; padding: 40px; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
.header { margin-bottom: 20px; }
.content-wrapper { display: flex; gap: 40px; }
.left-section { flex: 1; display: flex; flex-direction: column; gap: 20px; }
.img-box { border-radius: 12px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
.detail-img { width: 100%; height: 100%; object-fit: cover; aspect-ratio: 4/3; }
.owner-card { border: 1px solid #ebeef5; border-radius: 12px; }
.owner-header { font-size: 14px; color: #909399; margin-bottom: 15px; }
.owner-info { display: flex; align-items: center; gap: 15px; }
.text-info .name { font-weight: bold; font-size: 16px; color: #303133; margin-bottom: 4px; }
.right-info { flex: 1.4; }
.title { font-size: 32px; margin: 0 0 15px 0; color: #303133; }
.price-box { margin: 25px 0; color: #f56c6c; display: flex; align-items: baseline; }
.price { font-size: 36px; font-weight: bold; }
.unit { color: #909399; margin-left: 5px; }
.meta-info p { margin: 12px 0; display: flex; align-items: center; gap: 10px; color: #606266; font-size: 16px; }
.location-text { color: #409EFF !important; font-weight: 500; }
.desc { color: #606266; line-height: 1.8; min-height: 100px; font-size: 15px; margin-bottom: 30px; }
.action-box { background: #f8f9fa; padding: 25px; border-radius: 12px; border: 1px solid #eee; }
.picker-row { display: flex; align-items: center; gap: 15px; margin-bottom: 20px; }
.picker-row .label { font-weight: bold; color: #606266; width: 80px; }
.total-row { display: flex; align-items: baseline; gap: 10px; font-size: 18px; color: #303133; }
.total-price { color: #f56c6c; font-weight: bold; font-size: 26px; }
.total-hours { font-size: 14px; color: #909399; }
</style>