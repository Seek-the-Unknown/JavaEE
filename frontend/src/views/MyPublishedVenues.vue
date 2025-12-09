<template>
  <div class="manage-page">
    <header class="page-header">
      <h1>我发布的场馆</h1>
      <button class="add-btn" @click="router.push('/venue/new')">
        发布新场馆
      </button>
    </header>

    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="venues.length === 0" class="empty-state">
      <p>暂无发布记录</p>
    </div>

    <div v-else class="table-container">
      <table class="venue-table">
        <thead>
          <tr>
            <th>封面</th>
            <th>名称</th>
            <th>类型</th>
            <th>价格</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="venue in venues" :key="venue.id">
            <td><img :src="venue.imageUrl" class="thumb" /></td>
            <td class="name-cell">{{ venue.name }}</td>
            <td>
              <span class="tag">{{ venue.type }}</span>
            </td>
            <td class="price">¥{{ venue.pricePerHour }}/h</td>
            <td class="actions">
              <button
                class="icon-btn edit"
                @click="router.push(`/admin/venue/edit/${venue.id}`)"
              >
                编辑
              </button>
              <button class="icon-btn delete" @click="handleDelete(venue.id)">
                删除
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { getMyVenues, deleteVenue } from "@/api/venue";

const router = useRouter();
const venues = ref([]);
const loading = ref(true);

const fetchMyVenues = async () => {
  try {
    loading.value = true;
    venues.value = await getMyVenues();
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const handleDelete = async (id) => {
  if (confirm("确定要删除吗？")) {
    try {
      await deleteVenue(id);
      fetchMyVenues();
    } catch (e) {
      alert(e.message);
    }
  }
};

onMounted(fetchMyVenues);
</script>

<style scoped>
.manage-page {
  max-width: 1000px;
  margin: 40px auto;
  padding: 0 20px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.page-header h1 {
  font-size: 24px;
  color: #101828;
}
.add-btn {
  background: var(--gradient-primary);
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
}
.table-container {
  background: white;
  border: 1px solid #eaecf0;
  border-radius: 12px;
  overflow: hidden;
}
.venue-table {
  width: 100%;
  border-collapse: collapse;
}
.venue-table th {
  background: #f9fafb;
  text-align: left;
  padding: 12px 24px;
  font-size: 12px;
  color: #667085;
}
.venue-table td {
  padding: 16px 24px;
  border-bottom: 1px solid #eaecf0;
  font-size: 14px;
  vertical-align: middle;
}
.thumb {
  width: 48px;
  height: 48px;
  border-radius: 6px;
  object-fit: cover;
}
.name-cell {
  font-weight: 500;
}
.tag {
  background: #f2f4f7;
  padding: 4px 10px;
  border-radius: 16px;
  font-size: 12px;
}
.price {
  font-weight: 600;
  color: var(--color-primary-start);
}
.actions {
  display: flex;
  gap: 8px;
}
.icon-btn {
  border: 1px solid #d0d5dd;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  padding: 6px 12px;
  font-size: 12px;
}
.edit:hover {
  border-color: var(--color-primary-start);
  color: var(--color-primary-start);
}
.delete:hover {
  border-color: #fecaca;
  color: #d92d20;
  background: #fef2f2;
}
.empty-state {
  text-align: center;
  padding: 60px 0;
  color: #667085;
}
</style>
