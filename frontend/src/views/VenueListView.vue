<template>
  <div class="venue-list-page">
    <header class="page-header">
      <h1 class="page-title">所有场馆</h1>
      <div class="header-controls">
        <div class="search-bar">
          <i class="icon icon-search"></i>
          <input
            type="text"
            placeholder="按名称搜索..."
            v-model="searchQuery"
          />
        </div>
        <button v-if="isAdmin" @click="handleAddNew" class="add-new-btn">
          <i class="icon icon-plus"></i>
          <span>新增场馆</span>
        </button>
      </div>
    </header>

    <div v-if="loading" class="loading-state">加载中...</div>
    <div v-else-if="venues.length === 0" class="empty-state">
      未找到匹配的场馆。
    </div>
    <div v-else class="venue-grid">
      <router-link
        v-for="venue in venues"
        :key="venue.id"
        :to="`/venue/${venue.id}`"
        class="venue-card-link"
      >
        <div class="venue-card">
          <div
            class="card-image"
            :style="{
              backgroundImage: `url(${
                venue.imageUrl ||
                `https://picsum.photos/seed/${venue.id}/400/250`
              })`,
            }"
          >
            <span class="type-tag">{{ venue.type }}</span>
          </div>
          <div class="card-body">
            <h3 class="venue-name">{{ venue.name }}</h3>
            <p class="venue-description">
              {{ venue.description || "暂无描述" }}
            </p>
            <div v-if="isAdmin" class="admin-actions">
              <button @click.prevent="handleEdit(venue.id)">编辑</button>
              <button @click.prevent="handleDelete(venue.id)">删除</button>
            </div>
          </div>
          <div class="card-footer">
            <span class="price">¥{{ venue.pricePerHour }}/小时</span>
          </div>
        </div>
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/store/user";
import { getVenueList, deleteVenue } from "@/api/venue";

const { isAdmin } = useUserStore();
const router = useRouter();
const venues = ref([]);
const loading = ref(true);
const searchQuery = ref("");

const handleAddNew = () => router.push("/admin/venue/new");
const handleEdit = (id) => router.push(`/admin/venue/edit/${id}`);
const handleDelete = async (id) => {
  if (confirm(`您确定要删除此场馆吗？`)) {
    try {
      await deleteVenue(id);
      fetchVenues(searchQuery.value);
    } catch (error) {
      alert("删除失败: " + error.message);
    }
  }
};

const fetchVenues = async (query) => {
  try {
    loading.value = true;
    venues.value = await getVenueList(query);
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
};
let debounceTimer = null;
watch(searchQuery, (newQuery) => {
  clearTimeout(debounceTimer);
  debounceTimer = setTimeout(() => {
    fetchVenues(newQuery);
  }, 300);
});
onMounted(() => {
  fetchVenues("");
});
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}
.header-controls {
  display: flex;
  align-items: center;
  gap: 16px;
}
.search-bar {
  position: relative;
}
.search-bar .icon {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--color-text-secondary);
}
.search-bar input {
  padding: 10px 16px 10px 44px;
  border: 1px solid var(--color-border);
  border-radius: var(--border-radius);
  width: 280px;
  font-size: 14px;
}
.add-new-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  background: var(--gradient-primary);
  color: white;
  border: none;
  padding: 10px 16px;
  border-radius: var(--border-radius);
  cursor: pointer;
  font-weight: 500;
}

.venue-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}
.venue-card-link {
  text-decoration: none;
  color: inherit;
}
.venue-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  border: 1px solid var(--color-border);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  height: 100%;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.venue-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.07);
}
.card-image {
  height: 180px;
  background-size: cover;
  background-position: center;
  position: relative;
}
.type-tag {
  position: absolute;
  top: 12px;
  left: 12px;
  background: rgba(0, 0, 0, 0.4);
  color: white;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
}
.card-body {
  padding: 20px;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}
.venue-name {
  margin: 0 0 8px;
  font-size: 18px;
  font-weight: 600;
}
.venue-description {
  margin: 0 0 16px;
  font-size: 14px;
  color: var(--color-text-secondary);
  flex-grow: 1;
}
.admin-actions {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}
.admin-actions button {
  background: none;
  border: 1px solid var(--color-border);
  padding: 4px 8px;
  font-size: 12px;
  border-radius: 4px;
  cursor: pointer;
}
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  margin-top: auto;
  border-top: 1px solid var(--color-border);
}
.price {
  font-weight: 600;
  font-size: 16px;
  color: var(--color-primary-text);
}
</style>
