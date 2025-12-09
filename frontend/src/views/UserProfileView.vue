<template>
  <div class="profile-page">
    <div class="profile-card">
      <div class="avatar-section">
        <div class="avatar-circle">{{ userInitials }}</div>
        <h2 class="username">{{ userInfo.username }}</h2>
        <span class="role-tag">{{ isAdmin ? "管理员" : "普通用户" }}</span>
      </div>

      <div class="balance-card">
        <div class="balance-header">
          <span>账户余额</span>
        </div>
        <div class="balance-amount">¥{{ userInfo.balance || "0.00" }}</div>
        <button class="top-up-btn" @click="handleTopUp">充值</button>
      </div>

      <div class="info-list">
        <div class="info-item">
          <label>手机号码</label>
          <span>{{ userInfo.phone || "未设置" }}</span>
        </div>
      </div>

      <div class="action-buttons">
        <button class="action-btn primary" @click="router.push('/venue/new')">
          发布新场馆
        </button>
        <button
          class="action-btn outline"
          @click="router.push('/my-published')"
        >
          管理我的发布
        </button>
        <button class="action-btn outline" @click="router.push('/my-orders')">
          我的预约订单
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/store/user";

const router = useRouter();
const userStore = useUserStore();
const { userState, isAdmin, fetchUserInfo } = userStore;

const userInfo = computed(() => userState.userInfo || {});
const userInitials = computed(() =>
  (userInfo.value.username || "U").substring(0, 1).toUpperCase()
);

const handleTopUp = () => alert("充值功能请联系管理员线下充值");

onMounted(() => fetchUserInfo()); // 进页面刷新余额
</script>

<style scoped>
.profile-page {
  max-width: 600px;
  margin: 40px auto;
  padding: 0 20px;
}
.profile-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  padding: 40px;
  text-align: center;
}
.avatar-circle {
  width: 80px;
  height: 80px;
  background: var(--gradient-primary);
  border-radius: 50%;
  margin: 0 auto 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 32px;
  font-weight: 600;
}
.username {
  margin: 0 0 8px;
  color: #101828;
}
.role-tag {
  background: #f4ebff;
  color: #6941c6;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}
.balance-card {
  margin: 32px 0;
  background: linear-gradient(135deg, #101828 0%, #344054 100%);
  border-radius: 12px;
  padding: 24px;
  color: white;
  text-align: left;
  position: relative;
}
.balance-header {
  font-size: 14px;
  opacity: 0.8;
  margin-bottom: 8px;
}
.balance-amount {
  font-size: 32px;
  font-weight: 700;
}
.top-up-btn {
  position: absolute;
  right: 24px;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.4);
  color: white;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}
.top-up-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}
.info-list {
  text-align: left;
  border-top: 1px solid #eaecf0;
  padding-top: 24px;
  margin-bottom: 32px;
}
.info-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
  font-size: 14px;
}
.info-item label {
  color: #667085;
}
.info-item span {
  color: #101828;
  font-weight: 500;
}
.action-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
.action-btn {
  flex: 1;
  padding: 12px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  border: none;
  white-space: nowrap;
}
.primary {
  background: var(--gradient-primary);
  color: white;
}
.outline {
  background: white;
  border: 1px solid #d0d5dd;
  color: #344054;
}
</style>
