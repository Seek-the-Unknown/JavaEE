<template>
  <div class="venue-list-page">
    <header class="page-header">
      <h1 class="page-title">所有场馆</h1>
      <div class="header-controls">
        <div class="search-wrapper">
          <!--          <i class="icon icon-search"></i>-->
          <input
            type="text"
            placeholder="按名称搜索..."
            v-model="searchQuery"
            class="clean-input"
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
  <div class="card-body">
    <h3 class="venue-name">{{ venue.name }}</h3>
    <p class="venue-description">{{ venue.description || "暂无描述" }}</p>
    <div v-if="canEdit(venue)" class="admin-actions">
      <button @click.prevent="router.push(`/admin/venue/edit/${venue.id}`)">
        编辑
      </button>
      <button @click.prevent="handleDelete(venue.id)">删除</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/store/user";
import { getVenueList, deleteVenue } from "@/api/venue";

const { userState, isAdmin } = useUserStore();
const router = useRouter();
const venues = ref([]);
const loading = ref(true);
const searchQuery = ref("");

// 权限函数
const canEdit = (venue) => {
  if (!userState.userInfo) return false;
  return isAdmin.value || venue.ownerId === userState.userInfo.id;
};
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

/* --- 这里是彻底重写的 CSS --- */

/* 1. 外层容器：负责显示边框、圆角和背景 */
.search-wrapper {
  display: flex;
  align-items: center;
  width: 280px;
  height: 42px; /* 给定一个舒适的高度 */
  padding: 0 12px;
  border: 1px solid var(--color-border); /* 默认灰色边框 */
  border-radius: var(--border-radius);
  background-color: #fff;
  transition: all 0.2s ease;
  overflow: hidden; /* 防止内容溢出 */
}

/* 2. 交互核心：当内部元素被聚焦时，改变外层容器的样式 */
.search-wrapper:focus-within {
  border-color: var(--color-primary-start); /* 变成紫色 */
  /* 可选：加一点极其微弱的紫色光晕，完全没有黑色 */
  box-shadow: 0 0 0 3px rgba(127, 86, 217, 0.1);
}

/* 3. 图标样式 */
.search-wrapper {
  flex-shrink: 0;
  margin-right: 8px;
  color: var(--color-text-secondary);
}

/* 4. 输入框本体：彻底隐形，只负责输入 */
.clean-input {
  flex-grow: 1;
  width: 100%;
  height: 100%;
  border: none !important; /* 绝对无边框 */
  outline: none !important; /* 绝对无轮廓 */
  background: transparent !important; /* 透明背景 */
  padding: 0;
  margin: 0;
  font-size: 14px;
  color: var(--color-text-primary);
  box-shadow: none !important; /* 绝对无阴影 */
  -webkit-appearance: none;
}

/* 5. 双重保险：输入框聚焦时也不允许有任何样式 */
.clean-input:focus {
  border: none !important;
  outline: none !important;
  box-shadow: none !important;
}

/* --- 重写结束 --- */

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
