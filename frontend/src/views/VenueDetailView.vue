<template>
  <div class="detail-page">
    <div v-if="loading" class="loading-state">加载中...</div>
    <div v-else-if="venue" class="content-wrapper">
      <div class="venue-header">
        <div
          class="header-image"
          :style="{
            backgroundImage: `url(${
              venue.imageUrl ||
              `https://picsum.photos/seed/${venue.id}/1200/600`
            })`,
          }"
        >
          <div class="header-overlay">
            <span class="type-badge">{{ venue.type }}</span>
            <h1 class="venue-title">{{ venue.name }}</h1>
            <p class="venue-desc">{{ venue.description }}</p>
          </div>
        </div>
      </div>

      <div class="main-grid">
        <div class="info-section">
          <div class="info-card">
            <h3 class="card-title">场馆详情</h3>
            <ul class="detail-list">
              <li>
                <span class="label">价格</span
                ><span class="value price"
                  >¥{{ venue.pricePerHour }} / 小时</span
                >
              </li>
              <li>
                <span class="label">容量</span
                ><span class="value">{{ venue.capacity }} 人</span>
              </li>
            </ul>
          </div>
        </div>

        <div class="booking-section">
          <div class="booking-card sticky-card">
            <h3 class="card-title">预约时段 (可多选)</h3>
            <div v-if="isLoggedIn">
              <div class="date-picker-wrapper">
                <label>日期</label>
                <input
                  type="date"
                  v-model="selectedDate"
                  :min="todayDate"
                  class="date-input"
                />
              </div>

              <div class="slots-grid">
                <button
                  v-for="slot in timeSlots"
                  :key="slot.time"
                  class="time-slot-btn"
                  :class="{
                    'is-booked': slot.isBooked,
                    'is-selected': selectedSlots.has(slot.time),
                  }"
                  :disabled="slot.isBooked"
                  @click="toggleSlot(slot)"
                >
                  {{ slot.time }}
                </button>
              </div>

              <div class="booking-summary" v-if="selectedSlots.size > 0">
                <p>
                  已选: <strong>{{ selectedSlots.size }} 个时段</strong>
                </p>
                <p>
                  总计: <strong class="total-price">¥{{ totalPrice }}</strong>
                </p>
                <p :class="['balance-text', { 'no-balance': !canAfford }]">
                  当前余额: ¥{{ userState.userInfo.balance || 0 }}
                  <span v-if="!canAfford">(余额不足)</span>
                </p>
              </div>

              <button
                @click="handleBatchBooking"
                class="confirm-btn"
                :disabled="selectedSlots.size === 0 || isBooking"
              >
                {{ isBooking ? "提交中..." : "确认预约" }}
              </button>

              <p
                v-if="bookingMessage"
                :class="['message', isError ? 'error' : 'success']"
              >
                {{ bookingMessage }}
              </p>
            </div>
            <div v-else class="login-mask">
              <p>登录后即可预约</p>
              <router-link to="/login" class="login-link-btn"
                >去登录</router-link
              >
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-else class="not-found">场馆未找到</div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from "vue";
import { getVenueById } from "@/api/venue";
import { createBatchOrders, getBookedSlots } from "@/api/order";
import { useUserStore } from "@/store/user";

// defineProps 自动可用，无需导入
// eslint-disable-next-line no-undef
const props = defineProps({ id: { type: [String, Number], required: true } });
const { userState, isLoggedIn, fetchUserInfo } = useUserStore();

const venue = ref(null);
const loading = ref(true);
const isBooking = ref(false);
const bookingMessage = ref("");
const isError = ref(false);
const todayDate = new Date().toISOString().split("T")[0];
const selectedDate = ref(todayDate);
const bookedSlots = ref([]);
const selectedSlots = ref(new Set());

const canAfford = computed(() => {
  if (!isLoggedIn.value || !venue.value) return true;
  const balance = parseFloat(userState.userInfo.balance || 0);
  return balance >= totalPrice.value;
});

const timeSlots = computed(() => {
  const slots = [];
  for (let i = 9; i < 21; i++) {
    const timeDisplay = `${String(i).padStart(2, "0")}:00`;
    const isBooked = (bookedSlots.value || []).some(
      (b) => b.startTime && b.startTime.substring(0, 5) === timeDisplay
    );
    slots.push({ time: timeDisplay, isBooked });
  }
  return slots;
});

const totalPrice = computed(() => {
  if (!venue.value) return 0;
  return venue.value.pricePerHour * selectedSlots.value.size;
});

const getEndTime = (t) =>
  `${String(parseInt(t.split(":")[0]) + 1).padStart(2, "0")}:00`;

const fetchBookedSlots = async (date) => {
  try {
    const data = await getBookedSlots(props.id, date);
    bookedSlots.value = data || [];
    selectedSlots.value.clear();
    bookingMessage.value = "";
  } catch (e) {
    console.error(e);
  }
};

watch(selectedDate, fetchBookedSlots);

const toggleSlot = (slot) => {
  if (slot.isBooked) return;
  if (selectedSlots.value.has(slot.time)) selectedSlots.value.delete(slot.time);
  else selectedSlots.value.add(slot.time);
  bookingMessage.value = "";
};

const handleBatchBooking = async () => {
  if (selectedSlots.value.size === 0) return;
  if (!canAfford.value) {
    alert("余额不足，请去个人中心充值！");
    return;
  }
  isBooking.value = true;
  bookingMessage.value = "";

  try {
    const orders = Array.from(selectedSlots.value).map((startTime) => ({
      userId: userState.userInfo.id,
      venueId: venue.value.id,
      bookingDate: selectedDate.value,
      startTime: `${startTime}:00`,
      endTime: `${getEndTime(startTime)}:00`,
    }));

    await createBatchOrders(orders);
    isError.value = false;
    bookingMessage.value = "预约成功！";
    alert(`预约成功！消费 ¥${totalPrice.value}`);

    await fetchUserInfo();
    await fetchBookedSlots(selectedDate.value);
  } catch (error) {
    isError.value = true;
    bookingMessage.value = error.message || "预约失败";
  } finally {
    isBooking.value = false;
  }
};

onMounted(async () => {
  loading.value = true;
  try {
    venue.value = await getVenueById(props.id);
    await fetchBookedSlots(selectedDate.value);
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
/* 样式复用之前，增加余额相关样式 */
.detail-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}
.venue-header {
  border-radius: 16px;
  overflow: hidden;
  margin-bottom: 32px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
.header-image {
  height: 320px;
  background-size: cover;
  background-position: center;
  position: relative;
}
.header-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.8), transparent);
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 32px;
  color: white;
}
.type-badge {
  background: #7f56d9;
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  align-self: flex-start;
  margin-bottom: 12px;
}
.venue-title {
  font-size: 32px;
  margin: 0 0 8px 0;
}
.venue-desc {
  opacity: 0.9;
  max-width: 600px;
}
.main-grid {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 32px;
}
.info-card,
.booking-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  border: 1px solid #eaecf0;
}
.detail-list {
  list-style: none;
  padding: 0;
}
.detail-list li {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px dashed #eee;
}
.price {
  color: #7f56d9;
  font-weight: 700;
  font-size: 18px;
}
.slots-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
  margin-bottom: 24px;
}
.time-slot-btn {
  padding: 8px 0;
  border: 1px solid #ddd;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 13px;
}
.time-slot-btn:hover:not(:disabled) {
  border-color: #7f56d9;
  color: #7f56d9;
}
.time-slot-btn.is-booked {
  background: #f3f4f6;
  color: #bbb;
  border-color: #eee;
  cursor: not-allowed;
  text-decoration: line-through;
}
.time-slot-btn.is-selected {
  background: #7f56d9;
  color: white;
  border-color: #7f56d9;
}
.booking-summary {
  background: #f9f5ff;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 16px;
  font-size: 14px;
}
.total-price {
  color: #7f56d9;
  font-size: 20px;
}
.balance-text {
  font-size: 13px;
  margin-top: 4px;
  color: #667085;
}
.no-balance {
  color: #ef4444;
  font-weight: 600;
}
.confirm-btn {
  width: 100%;
  padding: 14px;
  background: linear-gradient(90deg, #7f56d9 0%, #6366f1 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
}
.confirm-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.message {
  margin-top: 12px;
  text-align: center;
  font-size: 14px;
}
.message.success {
  color: #16a34a;
}
.message.error {
  color: #dc2626;
}
@media (max-width: 768px) {
  .main-grid {
    grid-template-columns: 1fr;
  }
}
</style>
