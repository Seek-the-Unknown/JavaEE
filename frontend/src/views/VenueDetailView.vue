<template>
  <div class="detail-page">
    <div v-if="loading">加载中...</div>
    <div v-if="venue">
      <div class="venue-header">
        <div
          class="header-image"
          :style="{
            backgroundImage: `url(${
              venue.imageUrl ||
              `https://picsum.photos/seed/${venue.id}/1200/400`
            })`,
          }"
        ></div>
        <div class="header-content">
          <span class="type-tag">{{ venue.type }}</span>
          <h1 class="page-title">{{ venue.name }}</h1>
          <p>{{ venue.description }}</p>
        </div>
      </div>

      <div class="booking-container">
        <div class="info-cards">
          <div class="info-card">
            <span>价格</span><strong>¥{{ venue.pricePerHour }}/小时</strong>
          </div>
          <div class="info-card">
            <span>容量</span><strong>{{ venue.capacity }} 人</strong>
          </div>
        </div>

        <div class="booking-panel">
          <h3>立即预约</h3>
          <div v-if="isLoggedIn">
            <div class="form-group">
              <label>选择日期</label>
              <input type="date" v-model="selectedDate" />
            </div>
            <label>选择时段 (每小时)</label>
            <div class="time-slots-grid">
              <div
                v-for="slot in timeSlots"
                :key="slot.time"
                class="time-slot"
                :class="{
                  booked: slot.isBooked,
                  selected: selectedSlot === slot.time,
                }"
                @click="selectSlot(slot)"
              >
                {{ slot.time }}
              </div>
            </div>
            <p
              v-if="bookingMessage"
              :class="isError ? 'error-text' : 'success-text'"
            >
              {{ bookingMessage }}
            </p>
            <button
              @click="handleBooking"
              class="book-btn"
              :disabled="!selectedSlot || isBooking"
            >
              {{ isBooking ? "处理中..." : `确认预约 ${selectedSlot || ""}` }}
            </button>
          </div>
          <div v-else class="login-prompt">
            <p>请先登录以查看可预约时段并进行预约。</p>
            <router-link to="/login" class="book-btn">前往登录</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// ==================== 问题修复：移除了未使用的 reactive 和 useRouter ====================
import { ref, onMounted, computed, watch, defineProps } from "vue";
import { getVenueById } from "@/api/venue";
import { createOrder, getBookedSlots } from "@/api/order";
import { useUserStore } from "@/store/user";
// ===================================================================================

const props = defineProps({ id: { type: [String, Number], required: true } });
const { userState, isLoggedIn } = useUserStore();

const venue = ref(null);
const loading = ref(true);
const isBooking = ref(false);
const bookingMessage = ref("");
const isError = ref(false);

const selectedDate = ref(new Date().toISOString().split("T")[0]);
const bookedSlots = ref([]);
const selectedSlot = ref(null);

// 生成一天的时间段 (9:00 - 21:00)
const timeSlots = computed(() => {
  const slots = [];
  for (let i = 9; i < 22; i++) {
    const time = `${String(i).padStart(2, "0")}:00`;
    // 检查这个时间段是否已被预订
    const isBooked = bookedSlots.value.some((booking) => {
      const startTime = booking.startTime.substring(0, 5); // '09:00:00' -> '09:00'
      return startTime === time;
    });
    slots.push({ time, isBooked });
  }
  return slots;
});

// 获取某日已预订时段
const fetchBookedSlots = async (date) => {
  if (!isLoggedIn.value) return;
  try {
    const data = await getBookedSlots(props.id, date);
    bookedSlots.value = data;
    selectedSlot.value = null; // 日期改变后清空选择
  } catch (error) {
    console.error("获取已预约时段失败:", error);
  }
};

// 监听日期变化，重新获取数据
watch(selectedDate, (newDate) => {
  fetchBookedSlots(newDate);
});

// 点击选择时间段
const selectSlot = (slot) => {
  if (slot.isBooked) return; // 已被预订的不能选
  selectedSlot.value = slot.time;
};

const handleBooking = async () => {
  if (!selectedSlot.value) {
    alert("请先选择一个预约时间段！");
    return;
  }
  isBooking.value = true;
  bookingMessage.value = "";

  try {
    const startTime = selectedSlot.value;
    const endTime = `${(parseInt(startTime.split(":")[0]) + 1)
      .toString()
      .padStart(2, "0")}:00`;

    await createOrder({
      userId: userState.userInfo.id,
      venueId: venue.value.id,
      bookingDate: selectedDate.value,
      startTime,
      endTime,
    });

    isError.value = false;
    bookingMessage.value = "恭喜，预约成功！";
    alert('预约成功！您可以在"我的订单"页面查看。');
    // 预约成功后刷新当前页的预订状态
    fetchBookedSlots(selectedDate.value);
  } catch (error) {
    isError.value = true;
    bookingMessage.value = error.message || "预约失败，请稍后再试。";
  } finally {
    isBooking.value = false;
  }
};

onMounted(async () => {
  try {
    loading.value = true;
    venue.value = await getVenueById(props.id);
    await fetchBookedSlots(selectedDate.value); // 初始加载当天的预订
  } catch (error) {
    console.error("获取场馆详情失败:", error);
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
.venue-header {
  position: relative;
  border-radius: var(--border-radius);
  overflow: hidden;
  margin-bottom: 32px;
}
.header-image {
  height: 300px;
  background-size: cover;
  background-position: center;
}
.header-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 60px 32px 32px;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.7), transparent);
  color: white;
}
.header-content .page-title {
  margin: 8px 0;
  color: white;
}
.header-content p {
  margin: 0;
  opacity: 0.9;
}

.booking-container {
  display: grid;
  grid-template-columns: 1fr 350px;
  gap: 32px;
}
.info-cards {
  background: var(--card-bg);
  padding: 32px;
  border-radius: var(--border-radius);
  box-shadow: 0 4px 6px -1px var(--shadow-color);
  align-self: flex-start; /* 关键，让它不拉伸 */
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}
.info-card span {
  display: block;
  color: var(--text-secondary);
  margin-bottom: 8px;
}
.info-card strong {
  font-size: 20px;
  font-weight: 600;
}

.booking-panel {
  background: var(--card-bg);
  padding: 32px;
  border-radius: var(--border-radius);
  box-shadow: 0 4px 6px -1px var(--shadow-color);
}
.booking-panel h3 {
  margin: 0 0 24px;
  font-size: 20px;
}
.form-group {
  margin-bottom: 16px;
}
.form-group label,
.booking-panel > label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  font-size: 14px;
}
.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid var(--border-color);
  border-radius: 6px;
  box-sizing: border-box;
}

.time-slots-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
  margin-bottom: 24px;
}
.time-slot {
  padding: 8px;
  border: 1px solid var(--border-color);
  border-radius: 6px;
  text-align: center;
  cursor: pointer;
}
.time-slot.booked {
  background: #f1f5f9;
  color: #94a3b8;
  cursor: not-allowed;
  text-decoration: line-through;
}
.time-slot.selected {
  background: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}

.book-btn {
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: 6px;
  background: var(--primary-color);
  color: white;
  font-size: 16px;
  cursor: pointer;
  text-align: center;
  text-decoration: none;
}
.book-btn:disabled {
  background: #cbd5e1;
  cursor: not-allowed;
}

.error-text {
  color: #ef4444;
}
.success-text {
  color: #22c55e;
}
</style>
