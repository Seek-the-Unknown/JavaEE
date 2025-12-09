<template>
  <div class="orders-page">
    <h1 class="page-title">我的订单</h1>
    <div class="filter-tabs">
      <button
        @click="currentStatus = 'all'"
        :class="{ active: currentStatus === 'all' }"
      >
        全部
      </button>
      <!-- ... 其他按钮 ... -->
    </div>
    <div v-if="loading">加载中...</div>
    <div v-else-if="filteredOrders.length === 0" class="empty-state">...</div>
    <div v-else class="orders-list">
      <div v-for="order in filteredOrders" :key="order.id" class="order-card">
        <div class="order-main">
          <div
            class="venue-image"
            :style="{
              backgroundImage: `url(${
                order.venueImageUrl ||
                `https://picsum.photos/seed/${order.venueId}/200/200`
              })`,
            }"
          ></div>
          <div class="order-info">
            <h3 class="venue-name">{{ order.venueName }}</h3>
            <span class="date-time"
              >📅 {{ order.bookingDate }} 🕒
              {{ order.startTime.substring(0, 5) }} -
              {{ order.endTime.substring(0, 5) }}</span
            >
          </div>
        </div>
        <div class="order-details">
          <span class="price">¥{{ order.totalAmount }}</span>
          <span class="status" :class="getStatusClass(order.status)">{{
            formatStatus(order.status)
          }}</span>
          <button
            v-if="order.status === 0 || order.status === 1"
            @click="handleCancel(order)"
            class="cancel-btn"
          >
            取消
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted, computed } from "vue";
import { getOrdersByUserId, cancelOrder } from "@/api/order";
import { useUserStore } from "@/store/user";

const { userState } = useUserStore();
const allOrders = ref([]);
const loading = ref(true);
const currentStatus = ref("all");

const filteredOrders = computed(() => {
  if (currentStatus.value === "all") {
    return allOrders.value;
  }
  return allOrders.value.filter(
    (order) => order.status === currentStatus.value
  );
});

const formatStatus = (status) => {
  const statusMap = { 0: "待支付", 1: "预约成功", 2: "已完成", 3: "已取消" };
  return statusMap[status] || "未知状态";
};
const getStatusClass = (status) => `status-${status}`;

const handleCancel = async (order) => {
  if (confirm(`您确定要取消订单 #${order.id} 吗？`)) {
    try {
      await cancelOrder(order.id);
      alert("订单取消成功！");
      order.status = 3;
    } catch (error) {
      alert("取消失败：" + error.message);
    }
  }
};

onMounted(async () => {
  if (userState.userInfo && userState.userInfo.id) {
    try {
      loading.value = true;
      allOrders.value = await getOrdersByUserId(userState.userInfo.id);
    } catch (error) {
      console.error("获取订单列表失败:", error);
    } finally {
      loading.value = false;
    }
  }
});
</script>

<style scoped>
.orders-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.order-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--card-bg);
  padding: 16px;
  border-radius: var(--border-radius);
  box-shadow: 0 4px 6px -1px var(--shadow-color);
}
.order-main {
  display: flex;
  align-items: center;
  gap: 16px;
}
.venue-image {
  width: 64px;
  height: 64px;
  border-radius: 8px;
  background-size: cover;
  background-position: center;
}
.venue-name {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 4px;
}
.date-time {
  color: var(--text-secondary);
  font-size: 14px;
}

.order-details {
  display: flex;
  align-items: center;
  gap: 24px;
}
.price {
  font-size: 16px;
  font-weight: 600;
}
.status {
  font-weight: 500;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
}
.status-0 {
  background: #fffbeb;
  color: #f59e0b;
}
.status-1 {
  background: #f0fdf4;
  color: #22c55e;
}
.status-3 {
  background: #fef2f2;
  color: #ef4444;
}
.cancel-btn {
  background: #fee2e2;
  color: #dc2626;
  border: none;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
}
</style>
