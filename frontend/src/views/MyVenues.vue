<template>
  <div class="manage-container">
    <div class="header-row">
      <h2>我发布的场馆</h2>
      <el-button type="primary" @click="$router.push('/add')">发布新场馆</el-button>
    </div>

    <el-table :data="tableData" style="width: 100%" border stripe>
      <el-table-column label="封面" width="120">
        <template #default="scope">
          <img :src="scope.row.cover" style="width: 80px; height: 60px; object-fit: cover; border-radius: 4px;" />
        </template>
      </el-table-column>

      <el-table-column prop="name" label="场馆名称" width="180" />

      <el-table-column prop="type" label="类型" width="100">
        <template #default="scope">
          <el-tag>{{ scope.row.type }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="price" label="价格" width="120">
        <template #default="scope">¥ {{ scope.row.price }} / 小时</template>
      </el-table-column>

      <el-table-column prop="capacity" label="容量" width="100">
        <template #default="scope">{{ scope.row.capacity }} 人</template>
      </el-table-column>

      <el-table-column label="操作">
        <template #default="scope">
          <el-button size="small" type="primary" plain @click="handleEdit(scope.row.id)">编辑</el-button>

          <el-popconfirm title="确定要删除这个场馆吗？" confirm-button-text="删除" cancel-button-text="取消" @confirm="handleDelete(scope.row.id)">
            <template #reference>
              <el-button size="small" type="danger" plain>删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="tableData.length === 0" description="你还没有发布任何场馆" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const tableData = ref([])
const user = JSON.parse(localStorage.getItem('user'))

const loadData = async () => {
  if(!user) return router.push('/login')

  const res = await axios.get(`http://localhost:8888/api/my-venues?userId=${user.id}`)
  if(res.data.code === 200) {
    tableData.value = res.data.data
  }
}

const handleEdit = (id) => {
  router.push(`/edit/${id}`)
}

const handleDelete = async (id) => {
  try {
    await axios.delete(`http://localhost:8888/api/venue/${id}`)
    ElMessage.success('删除成功')
    loadData() // 刷新列表
  } catch(e) {
    ElMessage.error('删除失败')
  }
}

onMounted(loadData)
</script>

<style scoped>
.manage-container { max-width: 1000px; margin: 30px auto; padding: 20px; background: #fff; border-radius: 8px; box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1); }
.header-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
</style>