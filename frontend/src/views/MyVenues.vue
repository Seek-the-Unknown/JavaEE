<template>
  <div class="my-venue-container">
    <div class="page-header">
      <div class="header-left">
        <h2>我的场馆管理</h2>
        <span class="subtitle">查看并管理您发布的场馆信息</span>
      </div>
      <el-button type="primary" size="large" @click="goPublish">
        <el-icon style="margin-right: 5px"><Plus /></el-icon> 发布新场馆
      </el-button>
    </div>

    <el-card shadow="never" class="table-card">
      <el-table
          :data="venueList"
          style="width: 100%"
          v-loading="loading"
          size="large"
          :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
      >

        <el-table-column label="场馆封面" width="160">
          <template #default="scope">
            <div class="img-wrapper">
              <el-image
                  :src="scope.row.cover"
                  :preview-src-list="[scope.row.cover]"
                  fit="cover"
                  class="venue-thumb"
                  preview-teleported
              >
                <template #error>
                  <div class="image-slot"><el-icon><Picture /></el-icon></div>
                </template>
              </el-image>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="场馆信息" min-width="200">
          <template #default="scope">
            <div class="info-cell">
              <h4 class="venue-name" @click="goDetail(scope.row.id)">{{ scope.row.name }}</h4>
              <p class="venue-loc">
                <el-icon><Location /></el-icon> {{ scope.row.region }}
              </p>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="type" label="运动类型" width="120">
          <template #default="scope">
            <el-tag>{{ scope.row.type }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="price" label="价格" width="150" sortable>
          <template #default="scope">
            <span class="price-text">¥ {{ scope.row.price }}</span> <span style="font-size:12px;color:#999">/小时</span>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag type="success" effect="plain" v-if="scope.row.status === 1">已上架</el-tag>
            <el-tag type="info" effect="plain" v-else>已下架</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row.id)">
              <el-icon><Edit /></el-icon> 编辑
            </el-button>
            <el-divider direction="vertical" />
            <el-button type="danger" link @click="handleDelete(scope.row)">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && venueList.length === 0" description="您还没有发布任何场馆">
        <el-button type="primary" @click="goPublish">立即发布</el-button>
      </el-empty>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Picture, Location } from '@element-plus/icons-vue'

const router = useRouter()
const venueList = ref([])
const loading = ref(false)

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await axios.get('http://localhost:8888/api/my-venues')
    if(res.data.code === 200) {
      venueList.value = res.data.data
    } else {
      ElMessage.warning(res.data.msg || '获取数据失败')
    }
  } catch (e) {
    ElMessage.error('加载失败，请检查网络')
  } finally {
    loading.value = false
  }
}

// 跳转逻辑
const goPublish = () => router.push('/add')
const goDetail = (id) => router.push(`/venue/${id}`)
const handleEdit = (id) => router.push(`/edit/${id}`)

// 删除逻辑
const handleDelete = (row) => {
  ElMessageBox.confirm(
      `确定要删除场馆 "${row.name}" 吗？此操作无法撤销。`,
      '删除确认',
      { confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'warning' }
  ).then(async () => {
    try {
      const res = await axios.delete(`http://localhost:8888/api/venue/${row.id}`)
      if (res.data.code === 200) {
        ElMessage.success('删除成功')
        loadData() // 刷新列表
      } else {
        ElMessage.error(res.data.msg || '删除失败')
      }
    } catch (e) {
      ElMessage.error('删除请求出错')
    }
  })
}

onMounted(loadData)
</script>

<style scoped>
.my-venue-container {
  max-width: 1200px;
  margin: 30px auto;
  padding: 0 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.header-left h2 { margin: 0; color: #303133; font-size: 24px; }
.header-left .subtitle { color: #909399; font-size: 14px; margin-top: 8px; display: block; }

.table-card {
  border-radius: 8px;
  border: 1px solid #ebeef5;
  min-height: 500px;
}

/* 图片封装 */
.img-wrapper {
  width: 120px;
  height: 80px;
  border-radius: 6px;
  overflow: hidden;
  border: 1px solid #eee;
  background: #f5f7fa;
}
.venue-thumb { width: 100%; height: 100%; }
.image-slot { display: flex; justify-content: center; align-items: center; height: 100%; color: #c0c4cc; font-size: 20px; }

/* 文本信息 */
.info-cell { display: flex; flex-direction: column; justify-content: center; gap: 8px; }
.venue-name { margin: 0; font-size: 16px; color: #303133; cursor: pointer; transition: color 0.2s; font-weight: 600; }
.venue-name:hover { color: #409eff; }
.venue-loc { margin: 0; color: #909399; font-size: 13px; display: flex; align-items: center; gap: 4px; }

.price-text { color: #f56c6c; font-weight: bold; font-size: 18px; font-family: 'DIN Alternate', sans-serif; }
</style>