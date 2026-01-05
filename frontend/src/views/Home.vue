<template>
  <div class="page-bg">
    <div class="home-container">

      <div class="filter-section">
        <div class="search-row">
          <div class="left-controls">
            <span class="filter-label">城市区域</span>
            <el-cascader
                v-model="searchRegionArr"
                :options="pcaTextArr"
                placeholder="选择省 / 市 / 区"
                clearable
                filterable
                class="region-select"
                separator=" / "
                @change="handleRegionChange"
            />
          </div>

          <div class="search-box">
            <el-input
                v-model="keyword"
                placeholder="搜索场馆名称 (如: 体育中心)"
                class="custom-search-input"
                clearable
                @keyup.enter="loadData"
                @clear="loadData"
            >
              <template #prefix><el-icon><Search /></el-icon></template>
              <template #append>
                <el-button type="primary" class="search-btn" @click="loadData">搜索</el-button>
              </template>
            </el-input>
          </div>
        </div>

        <el-divider style="margin: 16px 0;" />

        <div class="category-row">
          <span class="filter-label">运动类型</span>
          <div class="type-tags">
            <div
                class="type-pill"
                :class="{ active: searchType === item }"
                v-for="item in ['全部', '篮球', '足球', '羽毛球', '网球', '游泳']"
                :key="item"
                @click="handleTypeChange(item)"
            >
              {{ item }}
            </div>
          </div>
        </div>
      </div>

      <div class="list-content">
        <div class="list-header">
          <h3>精选场馆 <span class="count-badge" v-if="list.length">{{ list.length }}</span></h3>
        </div>

        <el-row :gutter="24">
          <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="item in list" :key="item.id">
            <div class="venue-card" @click="goToDetail(item.id)">
              <div class="card-cover">
                <img :src="item.cover || 'https://via.placeholder.com/300x200'" class="cover-img"/>
                <div class="card-badges">
                  <span class="badge-region" v-if="item.region">
                    <el-icon><Location /></el-icon> {{ item.region.split(' / ').pop() }}
                  </span>
                  <span class="badge-type">{{ item.type }}</span>
                </div>
                <div class="overlay"></div>
              </div>

              <div class="card-body">
                <div class="card-main">
                  <h4 class="venue-title" :title="item.name">{{ item.name }}</h4>
                  <div class="venue-price">
                    <span class="currency">¥</span>
                    <span class="amount">{{ item.price }}</span>
                    <span class="unit">/小时</span>
                  </div>
                </div>
                <p class="venue-desc">{{ item.description || '暂无描述信息...' }}</p>

                <div class="card-footer">
                  <el-button class="book-btn" type="primary" plain @click.stop="openBook(item)">
                    立即预订
                  </el-button>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>

        <el-empty
            v-if="list.length === 0"
            description="暂无符合条件的场馆，换个搜索词试试？"
            :image-size="200"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" title="快速预约" width="420px" align-center class="booking-dialog">
      <div class="booking-content">
        <div class="venue-preview">
          <h4>{{ currentItem?.name }}</h4>
          <span class="price-tag">¥{{ currentItem?.price }}/小时</span>
        </div>

        <div class="form-item">
          <span class="label">预约时长</span>
          <el-input-number v-model="bookHours" :min="1" :max="12" size="large" style="width: 100%" />
        </div>

        <div class="booking-tips">
          <el-icon><InfoFilled /></el-icon>
          <span>当前为快速预约模式 (从当前时间开始计算)</span>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false" size="large">取消</el-button>
          <el-button type="primary" @click="submitQuickBook" size="large" color="#409eff">确认支付并提交</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { Search, Location, InfoFilled } from '@element-plus/icons-vue' // 引入需要的图标
import { pcaTextArr } from 'element-china-area-data'

const router = useRouter()
const list = ref([])
const keyword = ref('')
const searchType = ref('全部')
const searchRegionArr = ref([])
const searchRegionStr = ref('')

const dialogVisible = ref(false)
const currentItem = ref(null)
const bookHours = ref(2)
const user = JSON.parse(localStorage.getItem('user'))

const handleRegionChange = (val) => {
  if (val && val.length > 0) {
    searchRegionStr.value = val.join(' / ')
  } else {
    searchRegionStr.value = ''
  }
  loadData()
}

const handleTypeChange = (type) => {
  searchType.value = type
  loadData()
}

const loadData = async () => {
  try {
    const params = {}
    if(keyword.value) params.name = keyword.value
    if(searchType.value !== '全部') params.type = searchType.value
    if(searchRegionStr.value) params.region = searchRegionStr.value

    const res = await axios.get('http://localhost:8888/api/venues', { params })
    list.value = res.data.data
  } catch (e) {
    ElMessage.error('加载失败')
  }
}

const goToDetail = (id) => { router.push(`/venue/${id}`) }
const openBook = (item) => {
  if(!user) return router.push('/login')
  currentItem.value = item
  bookHours.value = 2
  dialogVisible.value = true
}

const submitQuickBook = async () => {
  try {
    const start = new Date()
    const end = new Date(start.getTime() + bookHours.value * 60 * 60 * 1000)
    await axios.post('http://localhost:8888/api/booking/create', {
      venueId: currentItem.value.id,
      startTime: start,
      endTime: end
    })

    ElMessage.success('预约成功')
    dialogVisible.value = false
    router.push('/my-bookings')
  } catch (e) {
    ElMessage.error(e.response?.data?.msg || '预约失败')
  }
}

onMounted(loadData)
</script>

<style scoped>
/* 1. 全局背景与容器 */
.page-bg {
  min-height: 100vh;
  background-color: #f5f7fa; /* 浅灰底色，突显卡片 */
  padding-bottom: 40px;
}
.home-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 20px;
}

/* 2. 筛选区域 - 类似现代App的控制台 */
.filter-section {
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
  margin-bottom: 30px;
}

.search-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 20px;
  justify-content: space-between;
}

.left-controls {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-label {
  font-weight: 600;
  color: #1f2329;
  font-size: 14px;
  white-space: nowrap;
}

.region-select {
  width: 240px;
}

.search-box {
  flex: 1;
  min-width: 300px;
  max-width: 500px;
}

/* 自定义搜索框样式 */
.custom-search-input :deep(.el-input__wrapper) {
  border-radius: 8px 0 0 8px;
  box-shadow: 0 0 0 1px #dcdfe6 inset;
}
.custom-search-input :deep(.el-input-group__append) {
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 0 8px 8px 0;
  font-weight: bold;
}
.search-btn:hover {
  background-color: #66b1ff;
}

.category-row {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 胶囊式标签选择器 */
.type-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.type-pill {
  padding: 6px 20px;
  background: #f2f3f5;
  color: #646a73;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  user-select: none;
}

.type-pill:hover {
  background: #e1eaff;
  color: #409eff;
}

.type-pill.active {
  background: #409eff;
  color: #fff;
  font-weight: 500;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

/* 3. 列表区域 */
.list-header {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}
.list-header h3 {
  font-size: 20px;
  color: #1f2329;
  font-weight: 700;
  margin: 0;
}
.count-badge {
  background: #e1f0ff;
  color: #409eff;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
  margin-left: 8px;
  vertical-align: middle;
}

/* 4. 场馆卡片 (核心视觉优化) */
.venue-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 24px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
  display: flex;
  flex-direction: column;
  height: 100%; /* 保证高度一致 */
}

.venue-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.08);
  border-color: rgba(64, 158, 255, 0.2);
}

.card-cover {
  height: 180px;
  position: relative;
  overflow: hidden;
}

.cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.venue-card:hover .cover-img {
  transform: scale(1.05); /* 悬停微缩放 */
}

/* 卡片上的徽标 */
.card-badges {
  position: absolute;
  top: 10px;
  left: 10px;
  right: 10px;
  display: flex;
  justify-content: space-between;
  z-index: 2;
}

.badge-region, .badge-type {
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  color: #fff;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}
.badge-type {
  background: rgba(64, 158, 255, 0.9);
  font-weight: 600;
}

.card-body {
  padding: 16px;
  display: flex;
  flex-direction: column;
  flex: 1; /* 撑满剩余高度 */
}

.card-main {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.venue-title {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  color: #1f2329;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  flex: 1;
  margin-right: 10px;
}

.venue-price {
  text-align: right;
  white-space: nowrap;
}
.venue-price .currency { font-size: 12px; color: #f56c6c; font-weight: bold; }
.venue-price .amount { font-size: 20px; color: #f56c6c; font-weight: 800; font-family: 'DIN Alternate', sans-serif; }
.venue-price .unit { font-size: 12px; color: #999; }

.venue-desc {
  font-size: 13px;
  color: #8f959e;
  line-height: 1.5;
  margin: 0 0 16px 0;
  height: 40px; /* 固定两行高度 */
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.card-footer {
  margin-top: auto; /* 将按钮推到底部 */
}

.book-btn {
  width: 100%;
  border-radius: 8px;
  font-weight: 600;
}

/* 5. 弹窗美化 */
.booking-content {
  padding: 10px 20px;
}
.venue-preview {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f5f7fa;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
}
.venue-preview h4 { margin: 0; font-size: 16px; color: #333; }
.price-tag { color: #f56c6c; font-weight: bold; }

.form-item { margin-bottom: 20px; }
.form-item .label { display: block; margin-bottom: 8px; color: #606266; font-weight: 500; }

.booking-tips {
  display: flex;
  align-items: center;
  gap: 6px;
  background: #ecf5ff;
  color: #409eff;
  padding: 10px;
  border-radius: 6px;
  font-size: 12px;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .search-row { flex-direction: column; align-items: stretch; }
  .region-select, .search-box { width: 100%; max-width: none; }
  .category-row { flex-direction: column; align-items: flex-start; }
}
</style>