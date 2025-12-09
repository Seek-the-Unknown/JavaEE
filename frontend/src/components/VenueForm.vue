<template>
  <form @submit.prevent="handleSubmit" class="venue-form">
    <div class="form-group">
      <label for="name">场馆名称 <span class="required">*</span></label>
      <input
        type="text"
        id="name"
        v-model="formData.name"
        required
        placeholder="请输入场馆名称"
      />
    </div>
    <div class="form-group">
      <label for="type">场馆类型 <span class="required">*</span></label>
      <input
        type="text"
        id="type"
        v-model="formData.type"
        required
        placeholder="例如: 篮球, 羽毛球"
      />
    </div>
    <div class="form-row">
      <div class="form-group">
        <label for="price"
          >每小时价格 (元) <span class="required">*</span></label
        >
        <div class="input-with-icon">
          <span>¥</span>
          <input
            type="number"
            id="price"
            v-model.number="formData.pricePerHour"
            required
            min="0"
          />
        </div>
      </div>
      <div class="form-group">
        <label for="capacity">容量 (人) <span class="required">*</span></label>
        <div class="input-with-icon">
          <i class="icon icon-users"></i>
          <input
            type="number"
            id="capacity"
            v-model.number="formData.capacity"
            required
            min="1"
          />
        </div>
      </div>
    </div>
    <div class="form-group">
      <label for="description">描述</label>
      <textarea
        id="description"
        v-model="formData.description"
        placeholder="请输入场馆详细信息..."
      ></textarea>
    </div>
    <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    <div class="form-actions">
      <button type="submit" class="submit-btn" :disabled="isLoading">
        {{ isLoading ? "提交中..." : "确认提交" }}
      </button>
      <button type="button" class="reset-btn" @click="resetForm">
        重置表单
      </button>
    </div>
  </form>
</template>

<script setup>
// ... <script setup> 部分保持不变 ...
import { reactive, watch, defineProps, defineEmits } from "vue";
const props = defineProps({
  initialData: { type: Object, default: () => ({}) },
  isLoading: Boolean,
  errorMessage: String,
});
const emit = defineEmits(["submit"]);
const formData = reactive({
  name: "",
  type: "",
  pricePerHour: 0,
  capacity: 0,
  description: "",
});
const initialFormState = { ...formData };
watch(
  () => props.initialData,
  (newData) => {
    if (newData) {
      Object.assign(formData, newData);
    }
  },
  { immediate: true, deep: true }
);
const handleSubmit = () => {
  emit("submit", { ...formData });
};
const resetForm = () => {
  Object.assign(formData, props.initialData || initialFormState);
};
</script>

<style scoped>
@import url("@/assets/icons.css");
.icon-upload {
  mask-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4'%3E%3C/path%3E%3Cpolyline points='17 8 12 3 7 8'%3E%3C/polyline%3E%3Cline x1='12' y1='3' x2='12' y2='15'%3E%3C/line%3E%3C/svg%3E");
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;
}
.page-subtitle {
  color: var(--color-text-secondary);
  margin-top: 8px;
}
.back-link {
  color: var(--color-primary-text);
  text-decoration: none;
  font-weight: 500;
}
.content-grid {
  display: grid;
  grid-template-columns: 320px 1fr;
  align-items: start;
  gap: 32px;
}
.card {
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  border: 1px solid var(--color-border);
}
.card-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 24px;
}
.image-uploader {
  aspect-ratio: 1 / 1;
  border: 2px dashed var(--color-border);
  border-radius: 12px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  overflow: hidden;
  transition: border-color 0.3s;
  background-color: var(--color-bg-main);
}
.image-uploader:hover {
  border-color: var(--color-primary-text);
}
.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.upload-placeholder {
  text-align: center;
}
.upload-icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background-color: #f0eefc;
  color: var(--color-primary-text);
  display: inline-flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 12px;
  border: 4px solid #fff;
}
.upload-text {
  font-weight: 500;
  margin: 0 0 4px;
}
.upload-hint {
  font-size: 12px;
  color: var(--color-text-secondary);
  margin: 0;
}
.upload-status {
  margin-top: 10px;
  color: var(--color-text-secondary);
  font-size: 14px;
}
</style>
