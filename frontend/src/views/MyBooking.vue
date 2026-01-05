<template>
  <div style="max-width: 1000px; margin: 0 auto;">
    <h2>我的订单</h2>
    <el-table :data="tableData" style="width: 100%">
      <el-table-column prop="id" label="订单号" width="80" />
      <el-table-column prop="venueId" label="场馆ID" width="100" />
      <el-table-column prop="hours" label="时长(h)" />
      <el-table-column prop="totalCost" label="总费用">
        <template #default="scope">¥{{ scope.row.totalCost }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="scope.row.status===1?'success':'info'">
            {{ scope.row.status===1 ? '已预约':'已取消' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button v-if="scope.row.status===1" type="danger" size="small" @click="cancel(scope.row)">取消预约</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const user = JSON.parse(localStorage.getItem('user'))
const tableData = ref([])

const load = async () => {
  const res = await axios.get(`http://localhost:8888/api/booking/my`)
  if(res.data.code === 200) {
    tableData.value = res.data.data
  }
}

const cancel = async (row) => {
  try {
    await axios.post('http://localhost:8888/api/booking/cancel', { bookingId: row.id })
    ElMessage.success('取消成功，费用已退回')
    load()
  } catch(e) {
    ElMessage.error(e.response?.data?.msg || '取消失败')
  }
}

onMounted(load)
</script>