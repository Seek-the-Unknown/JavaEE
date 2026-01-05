<template>
  <div class="dashboard-bg">
    <div class="dashboard-container">

      <div class="sidebar">
        <div class="brand">
          <div class="logo-box">
            <el-icon><Platform /></el-icon>
          </div>
          <span class="brand-text">Venue Admin</span>
        </div>

        <div class="nav-menu">
          <div
              class="nav-item"
              :class="{ active: currentView === 'profile' }"
              @click="switchView('profile')"
          >
            <el-icon><User /></el-icon>
            <span>个人中心</span>
          </div>

          <div
              class="nav-item"
              :class="{ active: currentView === 'my-venues' }"
              @click="switchView('my-venues')"
          >
            <el-icon><House /></el-icon>
            <span>我的场馆</span>
          </div>

          <div
              v-if="userInfo.role === 'admin'"
              class="nav-item admin-item"
              :class="{ active: currentView === 'admin' }"
              @click="switchView('admin')"
          >
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </div>
        </div>

        <div class="sidebar-footer">
          <div class="user-mini" v-if="userInfo.username">
            <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
            <span class="mini-name">{{ userInfo.username }}</span>
          </div>
          <el-button link type="danger" @click="logout">
            <el-icon><SwitchButton /></el-icon>
          </el-button>
        </div>
      </div>

      <div class="main-content">

        <transition name="fade-slide" mode="out-in">
          <div v-if="currentView === 'profile'" key="profile">
            <h2 class="page-title">欢迎回来，{{ userInfo.username }} 👋</h2>

            <el-row :gutter="20">
              <el-col :span="14" :xs="24">
                <el-card class="info-card" shadow="hover">
                  <div class="user-profile-header">
                    <el-avatar :size="80" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" class="avatar-hover"/>
                    <div class="text-info">
                      <h3>{{ userInfo.username }}</h3>
                      <div class="roles">
                        <el-tag effect="dark" :type="userInfo.role === 'admin' ? 'danger' : 'success'" round>
                          {{ userInfo.role === 'admin' ? '超级管理员' : '认证房东' }}
                        </el-tag>
                        <span class="user-id">ID: #{{ userInfo.id }}</span>
                      </div>
                    </div>
                  </div>
                  <el-divider content-position="left">账户状态</el-divider>
                  <div class="account-stats">
                    <div class="stat-item">
                      <div class="label">注册时间</div>
                      <div class="value">2023-10-24</div>
                    </div>
                    <div class="stat-item">
                      <div class="label">信用评分</div>
                      <div class="value green">100 (极好)</div>
                    </div>
                  </div>
                </el-card>
              </el-col>

              <el-col :span="10" :xs="24">
                <div class="asset-card">
                  <div class="card-bg-decoration"></div>
                  <div class="asset-content">
                    <div class="asset-header">
                      <span>账户余额 (CNY)</span>
                      <el-icon><Wallet /></el-icon>
                    </div>
                    <div class="asset-amount">
                      <span class="symbol">¥</span>
                      <span class="number">{{ userInfo.balance }}</span>
                    </div>
                    <div class="asset-actions">
                      <el-button color="#fff" text class="recharge-btn" @click="rechargeDialogVisible = true">
                        <span style="color: #409eff; font-weight: bold;">立即充值</span>
                      </el-button>
                    </div>
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
                <h2 class="page-title">我的场馆</h2>
                <p class="sub-title">管理您发布的所有租赁资源</p>
              </div>
              <el-button type="primary" size="large" icon="Plus" @click="$router.push('/add')" round>发布新场馆</el-button>
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
                <el-table-column prop="name" label="场馆名称" min-width="150" />
                <el-table-column prop="type" label="类型" width="100">
                  <template #default="scope"><el-tag effect="plain">{{ scope.row.type }}</el-tag></template>
                </el-table-column>
                <el-table-column prop="price" label="定价/小时" width="120">
                  <template #default="scope">
                    <span class="price-text">¥{{ scope.row.price }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="180" align="right">
                  <template #default="scope">
                    <el-button circle icon="Edit" type="primary" plain @click="handleEdit(scope.row.id)"></el-button>
                    <el-popconfirm title="确定删除该场馆吗?" @confirm="handleDeleteMyVenue(scope.row.id)">
                      <template #reference>
                        <el-button circle icon="Delete" type="danger" plain></el-button>
                      </template>
                    </el-popconfirm>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-if="myVenueList.length === 0" description="暂无场馆，快去发布一个吧！" />
            </el-card>
          </div>
        </transition>

        <transition name="fade-slide" mode="out-in">
          <div v-if="currentView === 'admin'" key="admin">
            <div class="flex-header">
              <h2 class="page-title">系统控制台</h2>
              <el-tag type="danger" effect="dark" round>管理员权限</el-tag>
            </div>

            <el-card shadow="never" class="admin-card">
              <el-tabs v-model="activeAdminTab">
                <el-tab-pane label="用户管理" name="users">
                  <el-table :data="adminUserList" stripe style="width: 100%">
                    <el-table-column prop="id" label="ID" width="60" />
                    <el-table-column prop="username" label="用户名">
                      <template #default="scope">
                        <div style="display:flex; align-items:center; gap:8px;">
                          <el-avatar :size="24" :src="'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'"/>
                          {{ scope.row.username }}
                        </div>
                      </template>
                    </el-table-column>
                    <el-table-column prop="role" label="角色">
                      <template #default="scope">
                        <el-tag :type="scope.row.role === 'admin' ? 'danger' : 'info'" size="small">{{ scope.row.role }}</el-tag>
                      </template>
                    </el-table-column>
                    <el-table-column prop="balance" label="余额" />
                    <el-table-column label="操作" width="150" align="right">
                      <template #default="scope">
                        <el-button link type="primary" @click="resetPwd(scope.row.id)">重置密码</el-button>
                        <el-popconfirm v-if="scope.row.username !== 'admin'" title="确认删除?" @confirm="deleteUser(scope.row.id)">
                          <template #reference><el-button link type="danger">删除</el-button></template>
                        </el-popconfirm>
                      </template>
                    </el-table-column>
                  </el-table>
                </el-tab-pane>

                <el-tab-pane label="全站场馆" name="venues">
                  <el-table :data="adminVenueList" stripe style="width: 100%">
                    <el-table-column prop="id" label="ID" width="60" />
                    <el-table-column prop="name" label="名称" show-overflow-tooltip/>
                    <el-table-column prop="ownerId" label="房东ID" width="100" />
                    <el-table-column label="操作" width="120" align="right">
                      <template #default="scope">
                        <el-popconfirm title="强制下架?" @confirm="deleteVenueForce(scope.row.id)">
                          <template #reference><el-button type="danger" size="small" plain>下架</el-button></template>
                        </el-popconfirm>
                      </template>
                    </el-table-column>
                  </el-table>
                </el-tab-pane>
              </el-tabs>
            </el-card>
          </div>
        </transition>
      </div>
    </div>

    <el-dialog v-model="rechargeDialogVisible" title="账户充值" width="360px" align-center class="custom-dialog">
      <div class="recharge-content">
        <p>请输入充值金额</p>
        <el-input-number v-model="rechargeAmount" :min="100" :step="100" size="large" style="width: 100%; margin: 15px 0;" />
        <div class="tips">⚠️ 演示环境，点击确认后资金实时到账</div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="rechargeDialogVisible = false" size="large">取消</el-button>
          <el-button type="primary" @click="handleRecharge" size="large">确认支付</el-button>
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
const activeAdminTab = ref('users')
const userInfo = ref({})
const rechargeDialogVisible = ref(false)
const rechargeAmount = ref(100)
const myVenueList = ref([])
const adminUserList = ref([])
const adminVenueList = ref([])

const init = async () => {
  // ★★★ 修改：不再依赖 localStorage 里的旧数据，直接拉取最新的
  const token = localStorage.getItem('token')
  if (!token) {
    router.push('/login')
    return
  }
  await loadUserInfo()
}

const switchView = async (viewName) => {
  currentView.value = viewName
  if (viewName === 'profile') loadUserInfo()
  else if (viewName === 'my-venues') loadMyVenues()
  else if (viewName === 'admin') loadAdminData()
}

// ★★★ 修改：获取用户信息接口变为 /api/user/me，不需要传 ID
const loadUserInfo = async () => {
  try {
    const res = await axios.get(`http://localhost:8888/api/user/me`)
    userInfo.value = res.data.data
    // 更新本地缓存以防万一
    localStorage.setItem('user', JSON.stringify(res.data.data))
  } catch (e) { console.error(e) }
}

// ★★★ 修改：充值接口，去掉 userId 参数
const handleRecharge = async () => {
  try {
    await axios.post('http://localhost:8888/api/user/recharge', { amount: rechargeAmount.value })
    ElMessage.success('充值成功')
    rechargeDialogVisible.value = false
    loadUserInfo()
  } catch(e) { ElMessage.error('充值失败') }
}

// ★★★ 修改：我的场馆接口，去掉 userId 参数
const loadMyVenues = async () => {
  try {
    const res = await axios.get(`http://localhost:8888/api/my-venues`)
    if(res.data.code === 200) myVenueList.value = res.data.data
  } catch(e) { console.error(e) }
}

const handleEdit = (id) => router.push(`/edit/${id}`)

const handleDeleteMyVenue = async (id) => {
  try {
    await axios.delete(`http://localhost:8888/api/venue/${id}`)
    ElMessage.success('删除成功')
    loadMyVenues()
  } catch(e) { ElMessage.error('删除失败') }
}

// ★★★ 修改：管理员接口路径调整
const loadAdminData = async () => {
  const [usersRes, venuesRes] = await Promise.all([
    axios.get('http://localhost:8888/api/admin/users'),
    axios.get('http://localhost:8888/api/admin/venues')
  ])
  adminUserList.value = usersRes.data.data
  adminVenueList.value = venuesRes.data.data
}

const resetPwd = async (id) => {
  await axios.post('http://localhost:8888/api/admin/user/reset-password', { id })
  ElMessage.success('密码重置成功')
}
const deleteUser = async (id) => {
  await axios.delete(`http://localhost:8888/api/admin/user/${id}`)
  ElMessage.success('用户已删除')
  loadAdminData()
}
const deleteVenueForce = async (id) => {
  await axios.delete(`http://localhost:8888/api/venue/${id}`)
  ElMessage.success('已强制下架')
  loadAdminData()
}
const logout = () => {
  localStorage.clear()
  router.push('/login')
}

onMounted(init)
</script>

<style scoped>
/* 全局背景 */
.dashboard-bg {
  min-height: 100vh;
  background-color: #f2f3f5; /* 柔和的浅灰背景 */
  color: #1f2329;
}

.dashboard-container {
  display: flex;
  max-width: 1400px;
  margin: 0 auto;
  min-height: 100vh;
}

/* 1. 左侧侧边栏设计 */
.sidebar {
  width: 240px;
  background: #fff;
  padding: 24px 16px;
  display: flex;
  flex-direction: column;
  border-right: 1px solid rgba(0,0,0,0.05);
  position: sticky;
  top: 0;
  height: 100vh;
  box-sizing: border-box;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 40px;
  padding-left: 10px;
}
.logo-box {
  width: 36px; height: 36px;
  background: linear-gradient(135deg, #409EFF, #337ecc);
  border-radius: 8px;
  display: flex; align-items: center; justify-content: center;
  color: #fff; font-size: 20px;
}
.brand-text {
  font-size: 18px; font-weight: 700; color: #1f2329;
}

.nav-menu { flex: 1; display: flex; flex-direction: column; gap: 8px; }

.nav-item {
  display: flex; align-items: center; gap: 12px;
  padding: 12px 16px;
  border-radius: 12px; /* 圆角胶囊 */
  cursor: pointer;
  color: #646a73;
  font-weight: 500;
  transition: all 0.2s ease;
}

.nav-item:hover { background-color: #f5f6f7; color: #1f2329; }
.nav-item.active { background-color: #e1f0ff; color: #409EFF; }
.nav-item .el-icon { font-size: 18px; }

.admin-item { margin-top: 20px; color: #ff9a2e; }
.admin-item.active { background-color: #fff7e6; color: #fa8c16; }

.sidebar-footer {
  border-top: 1px solid #eee;
  padding-top: 15px;
  display: flex; justify-content: space-between; align-items: center;
}
.user-mini { display: flex; align-items: center; gap: 8px; }
.mini-name { font-size: 14px; font-weight: 600; }

/* 2. 右侧内容区 */
.main-content {
  flex: 1;
  padding: 30px 40px;
  overflow-y: auto;
}

.page-title { margin: 0 0 10px 0; font-size: 26px; font-weight: 700; color: #1f2329; }
.sub-title { color: #8f959e; margin: 0 0 24px 0; }
.flex-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }

/* 个人中心卡片 */
.info-card { border: none; border-radius: 16px; height: 100%; }
.user-profile-header { display: flex; align-items: center; gap: 24px; margin-bottom: 20px; }
.avatar-hover { border: 4px solid #fff; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
.text-info h3 { margin: 0 0 8px 0; font-size: 22px; }
.roles { display: flex; align-items: center; gap: 10px; }
.user-id { font-size: 13px; color: #999; }
.account-stats { display: flex; gap: 40px; padding: 10px 0; }
.stat-item .label { color: #8f959e; font-size: 13px; margin-bottom: 4px; }
.stat-item .value { font-size: 16px; font-weight: 600; }
.stat-item .value.green { color: #52c41a; }

/* 资产卡片 (核心亮点) */
.asset-card {
  background: linear-gradient(135deg, #409EFF 0%, #0958d9 100%);
  border-radius: 16px;
  padding: 30px;
  color: #fff;
  position: relative;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(64, 158, 255, 0.3);
  height: 100%; box-sizing: border-box;
  display: flex; flex-direction: column; justify-content: center;
}
.card-bg-decoration {
  position: absolute; top: -50px; right: -50px;
  width: 200px; height: 200px;
  background: rgba(255,255,255,0.1);
  border-radius: 50%;
}
.asset-header { display: flex; justify-content: space-between; align-items: center; opacity: 0.9; margin-bottom: 10px; }
.asset-amount { font-size: 42px; font-weight: 700; margin-bottom: 20px; }
.asset-amount .symbol { font-size: 24px; opacity: 0.8; margin-right: 5px; }

/* 表格优化 */
.table-card { border: none; border-radius: 16px; }
.img-wrapper { width: 60px; height: 45px; border-radius: 6px; overflow: hidden; box-shadow: 0 2px 6px rgba(0,0,0,0.1); }
.table-img { width: 100%; height: 100%; object-fit: cover; }
.price-text { font-family: 'DIN', sans-serif; font-weight: 600; color: #f56c6c; }

/* 动画 */
.fade-slide-enter-active, .fade-slide-leave-active { transition: all 0.3s ease; }
.fade-slide-enter-from { opacity: 0; transform: translateY(10px); }
.fade-slide-leave-to { opacity: 0; transform: translateY(-10px); }

/* 弹窗内容 */
.recharge-content { text-align: center; padding: 10px 0; }
.tips { font-size: 12px; color: #e6a23c; background: #fdf6ec; padding: 8px; border-radius: 4px; display: inline-block; }
.dialog-footer { display: flex; justify-content: flex-end; gap: 12px; }

/* 响应式 */
@media (max-width: 768px) {
  .dashboard-container { flex-direction: column; }
  .sidebar { width: 100%; height: auto; position: static; border-right: none; border-bottom: 1px solid #eee; }
  .nav-menu { flex-direction: row; overflow-x: auto; padding-bottom: 10px; }
  .nav-item { white-space: nowrap; }
  .main-content { padding: 20px; }
}
</style>