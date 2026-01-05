<template>
  <div class="dashboard-bg">
    <div class="dashboard-container">

      <div class="sidebar">
        <div class="brand">
          <div class="logo-box">
            <el-icon><UserFilled /></el-icon>
          </div>
          <span class="brand-text">用户中心</span>
        </div>

        <div class="nav-menu">
          <div
              class="nav-item"
              :class="{ active: currentView === 'profile' }"
              @click="switchView('profile')"
          >
            <el-icon><DataBoard /></el-icon>
            <span>个人概览</span>
          </div>

          <div
              class="nav-item"
              :class="{ active: currentView === 'my-venues' }"
              @click="switchView('my-venues')"
          >
            <el-icon><House /></el-icon>
            <span>我发布的场馆</span>
          </div>

          <div
              class="nav-item"
              :class="{ active: currentView === 'my-bookings' }"
              @click="switchView('my-bookings')"
          >
            <el-icon><Calendar /></el-icon>
            <span>我的预约记录</span>
          </div>
        </div>

        <div class="sidebar-footer">
          <div class="user-mini" v-if="userInfo.username">
            <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
            <span class="mini-name">{{ userInfo.username }}</span>
          </div>
          <el-button link type="danger" @click="logout">
            退出
            <el-icon class="el-icon--right"><SwitchButton /></el-icon>
          </el-button>
        </div>
      </div>

      <div class="main-content">

        <transition name="fade-slide" mode="out-in">
          <div v-if="currentView === 'profile'" key="profile">
            <div class="flex-header">
              <h2 class="page-title">你好，{{ userInfo.username }} ☀️</h2>
            </div>

            <el-row :gutter="20">
              <el-col :span="14" :xs="24">
                <el-card class="info-card" shadow="hover">
                  <div class="user-profile-header">
                    <el-avatar :size="80" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" class="avatar-hover"/>
                    <div class="text-info">
                      <h3>{{ userInfo.username }}</h3>
                      <div class="roles">
                        <el-tag effect="dark" type="success" round>普通用户</el-tag>
                        <span class="user-id">ID: #{{ userInfo.id }}</span>
                      </div>
                    </div>
                  </div>
                  <el-divider content-position="left">我的钱包</el-divider>
                  <div class="account-stats">
                    <div class="stat-item">
                      <div class="label">当前余额</div>
                      <div class="value" style="color: #f56c6c; font-size: 24px;">¥ {{ userInfo.balance }}</div>
                    </div>
                  </div>
                  <div style="margin-top: 20px;">
                    <el-button type="primary" plain round @click="rechargeDialogVisible = true">充值余额</el-button>
                  </div>
                </el-card>
              </el-col>

              <el-col :span="10" :xs="24">
                <div class="asset-card">
                  <div class="card-bg-decoration"></div>
                  <div class="asset-content">
                    <div class="asset-header">
                      <span>想发布闲置场馆？</span>
                      <el-icon><Promotion /></el-icon>
                    </div>
                    <div style="font-size: 16px; opacity: 0.9; margin-bottom: 20px; line-height: 1.6;">
                      成为房东，将您的闲置场地分享给更多人使用，获取收益。
                    </div>
                    <el-button color="#fff" text class="recharge-btn" @click="goToAddVenue">
                      <span style="color: #409eff; font-weight: bold;">去发布场馆 -></span>
                    </el-button>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
        </transition>

        <transition name="fade-slide" mode="out-in">
          <div v-if="currentView === 'my-venues'" key="venues">
            <div class="flex-header">
              <div>
                <h2 class="page-title">我管理的场馆</h2>
                <p class="sub-title">您发布的所有资源</p>
              </div>
              <el-button type="primary" icon="Plus" @click="goToAddVenue" round>发布新场馆</el-button>
            </div>

            <el-card shadow="never" class="table-card">
              <el-table :data="myVenueList" style="width: 100%" :header-cell-style="{background:'#f8f9fb'}">
                <el-table-column label="封面" width="100">
                  <template #default="scope">
                    <div class="img-wrapper">
                      <img :src="scope.row.cover" class="table-img" />
                    </div>
                  </template>
                </el-table-column>
                <el-table-column prop="name" label="名称" min-width="150" />
                <el-table-column prop="price" label="价格" width="120">
                  <template #default="scope">
                    <span class="price-text">¥{{ scope.row.price }}/时</span>
                  </template>
                </el-table-column>
                <el-table-column label="状态" width="100">
                  <template #default="scope">
                    <el-tag v-if="scope.row.status === 1" type="success">已上架</el-tag>
                    <el-tag v-else type="info">下架中</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="150" align="right">
                  <template #default="scope">
                    <el-button circle icon="Edit" type="primary" plain @click="handleEdit(scope.row.id)"></el-button>
                    <el-popconfirm title="确定删除?" @confirm="handleDeleteMyVenue(scope.row.id)">
                      <template #reference>
                        <el-button circle icon="Delete" type="danger" plain></el-button>
                      </template>
                    </el-popconfirm>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-if="myVenueList.length === 0" description="您还没有发布过场馆" />
            </el-card>
          </div>
        </transition>

        <transition name="fade-slide" mode="out-in">
          <div v-if="currentView === 'my-bookings'" key="bookings">
            <el-empty description="开发中... 请自行对接 Booking 接口" />
          </div>
        </transition>

      </div>
    </div>

    <el-dialog v-model="rechargeDialogVisible" title="账户充值" width="360px" align-center class="custom-dialog">
      <div class="recharge-content">
        <p>请输入充值金额</p>
        <el-input-number v-model="rechargeAmount" :min="100" :step="100" size="large" style="width: 100%; margin: 15px 0;" />
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="rechargeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleRecharge">确认支付</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const currentView = ref('profile')
const userInfo = ref({})
const rechargeDialogVisible = ref(false)
const rechargeAmount = ref(100)
const myVenueList = ref([])

// 初始化
const init = async () => {
  const token = localStorage.getItem('token')
  if (!token) {
    router.push('/login')
    return
  }
  await loadUserInfo()
}

// 切换视图
const switchView = async (viewName) => {
  currentView.value = viewName
  if (viewName === 'profile') loadUserInfo()
  else if (viewName === 'my-venues') loadMyVenues()
  // else if (viewName === 'my-bookings') loadMyBookings() // 需自行实现
}

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const res = await axios.get(`http://localhost:8888/api/user/me`)
    if(res.data.code === 200) {
      userInfo.value = res.data.data
    }
  } catch (e) { console.error(e) }
}

// 加载我的场馆
const loadMyVenues = async () => {
  try {
    const res = await axios.get(`http://localhost:8888/api/my-venues`)
    if(res.data.code === 200) myVenueList.value = res.data.data
  } catch(e) { console.error(e) }
}

// 充值
const handleRecharge = async () => {
  try {
    await axios.post('http://localhost:8888/api/user/recharge', { amount: rechargeAmount.value })
    ElMessage.success('充值成功')
    rechargeDialogVisible.value = false
    loadUserInfo()
  } catch(e) { ElMessage.error('充值失败') }
}

// 删除场馆
const handleDeleteMyVenue = async (id) => {
  try {
    await axios.delete(`http://localhost:8888/api/venue/${id}`)
    ElMessage.success('删除成功')
    loadMyVenues()
  } catch(e) { ElMessage.error('删除失败') }
}

// 路由跳转
const goToAddVenue = () => router.push('/add') // 假设你有添加页
const handleEdit = (id) => router.push(`/edit/${id}`)
const logout = () => {
  localStorage.clear()
  router.push('/login')
}

onMounted(init)
</script>

<style scoped>
/* 此处直接复用 AdminDashboard.vue 的所有 CSS 即可，
   为了代码整洁，你可以将 CSS 提取到一个公共的 .css 文件中，
   或者直接把 AdminDashboard.vue 的 <style> 块原样复制到这里。

   关键的样式类：
   .dashboard-bg, .sidebar, .nav-item, .asset-card 等
   都已经包含在上面的 Template 结构中。
*/

/* --- 以下为关键样式复刻 (精简版) --- */
.dashboard-bg { min-height: 100vh; background-color: #f2f3f5; color: #1f2329; }
.dashboard-container { display: flex; max-width: 1400px; margin: 0 auto; min-height: 100vh; }
.sidebar { width: 240px; background: #fff; padding: 24px 16px; display: flex; flex-direction: column; border-right: 1px solid rgba(0,0,0,0.05); position: sticky; top: 0; height: 100vh; }
.brand { display: flex; align-items: center; gap: 12px; margin-bottom: 40px; padding-left: 10px; }
.logo-box { width: 36px; height: 36px; background: #67c23a; /* 普通用户用绿色区分 */ border-radius: 8px; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 20px; }
.brand-text { font-size: 18px; font-weight: 700; color: #1f2329; }
.nav-menu { flex: 1; display: flex; flex-direction: column; gap: 8px; }
.nav-item { display: flex; align-items: center; gap: 12px; padding: 12px 16px; border-radius: 12px; cursor: pointer; color: #646a73; font-weight: 500; transition: all 0.2s; }
.nav-item:hover { background-color: #f5f6f7; color: #1f2329; }
.nav-item.active { background-color: #f0f9eb; color: #67c23a; /* 绿色系高亮 */ }
.sidebar-footer { border-top: 1px solid #eee; padding-top: 15px; display: flex; justify-content: space-between; align-items: center; }
.main-content { flex: 1; padding: 30px 40px; overflow-y: auto; }
.info-card, .table-card { border: none; border-radius: 16px; height: 100%; }
.user-profile-header { display: flex; align-items: center; gap: 24px; margin-bottom: 20px; }
.asset-card { background: linear-gradient(135deg, #67c23a 0%, #389e0d 100%); /* 绿色渐变 */ border-radius: 16px; padding: 30px; color: #fff; position: relative; overflow: hidden; height: 100%; display: flex; flex-direction: column; justify-content: center; box-shadow: 0 10px 30px rgba(103, 194, 58, 0.3); }
.card-bg-decoration { position: absolute; top: -50px; right: -50px; width: 200px; height: 200px; background: rgba(255,255,255,0.1); border-radius: 50%; }
.asset-header { display: flex; justify-content: space-between; align-items: center; opacity: 0.9; margin-bottom: 10px; }
.fade-slide-enter-active, .fade-slide-leave-active { transition: all 0.3s ease; }
.fade-slide-enter-from { opacity: 0; transform: translateY(10px); }
.fade-slide-leave-to { opacity: 0; transform: translateY(-10px); }
.img-wrapper { width: 60px; height: 45px; border-radius: 6px; overflow: hidden; }
.table-img { width: 100%; height: 100%; object-fit: cover; }
.price-text { font-weight: 600; color: #f56c6c; }
</style>