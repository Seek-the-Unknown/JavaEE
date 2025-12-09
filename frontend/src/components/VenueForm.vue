<template>
  <form @submit.prevent="handleSubmit" class="venue-form">
    <div class="form-group">
      <label class="form-label">场馆名称 <span class="req">*</span></label>
      <input
        type="text"
        v-model="formData.name"
        class="form-control"
        placeholder="给场馆起个名字"
        required
      />
    </div>

    <div class="form-group">
      <label class="form-label">场馆类型 <span class="req">*</span></label>
      <div class="select-wrapper">
        <select v-model="formData.type" class="form-control" required>
          <option value="" disabled>请选择类型</option>
          <option value="篮球">篮球</option>
          <option value="足球">足球</option>
          <option value="羽毛球">羽毛球</option>
          <option value="网球">网球</option>
          <option value="游泳">游泳</option>
          <option value="健身房">健身房</option>
          <option value="其他">其他</option>
        </select>
        <span class="select-arrow">▼</span>
      </div>
    </div>

    <div class="form-row">
      <div class="form-col">
        <label class="form-label">价格/小时 <span class="req">*</span></label>
        <div class="input-group">
          <div class="input-addon addon-left">¥</div>
          <input
            type="number"
            v-model.number="formData.pricePerHour"
            class="form-control has-addon-left"
            placeholder="0.00"
            min="0"
            required
          />
        </div>
      </div>
      <div class="form-col">
        <label class="form-label">容纳人数 <span class="req">*</span></label>
        <div class="input-group">
          <input
            type="number"
            v-model.number="formData.capacity"
            class="form-control has-addon-right"
            placeholder="例如: 10"
            min="1"
            required
          />
          <div class="input-addon addon-right">人</div>
        </div>
      </div>
    </div>

    <div class="form-group">
      <label class="form-label">场馆描述</label>
      <textarea
        v-model="formData.description"
        class="form-control textarea"
        placeholder="请输入详细的场馆描述..."
        rows="4"
      ></textarea>
    </div>

    <div v-if="errorMessage" class="error-msg">
      {{ errorMessage }}
    </div>

    <div class="form-actions">
      <button type="button" class="btn btn-secondary" @click="resetForm">
        重置
      </button>
      <button type="submit" class="btn btn-primary" :disabled="isLoading">
        {{ isLoading ? "提交中..." : "保存场馆" }}
      </button>
    </div>
  </form>
</template>

<script setup>
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
  pricePerHour: "",
  capacity: "",
  description: "",
});

const initialFormState = { ...formData };

watch(
  () => props.initialData,
  (newData) => {
    if (newData) Object.assign(formData, newData);
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
/* 样式变量直接使用，防止外部CSS加载失败 */
.venue-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
  width: 100%;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-row {
  display: flex;
  gap: 20px;
}
.form-col {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-label {
  font-size: 14px;
  font-weight: 500;
  color: #344054; /* 深灰色 */
}
.req {
  color: #ef4444;
  margin-left: 2px;
}

/* --- 核心：统一输入框样式 --- */
.form-control {
  width: 100%;
  height: 40px; /* 强制统一高度 */
  padding: 0 12px;
  font-size: 14px;
  color: #101828;
  background: #fff;
  border: 1px solid #d0d5dd;
  border-radius: 8px;
  box-shadow: 0 1px 2px rgba(16, 24, 40, 0.05);
  transition: all 0.2s ease;
  box-sizing: border-box; /* 确保padding不影响宽度 */
  appearance: none; /* 移除浏览器默认样式 */
}

/* 聚焦状态 - 紫色光晕 */
.form-control:focus {
  outline: none;
  border-color: var(--color-primary-start); /* 使用紫色变量 */
  box-shadow: 0 0 0 4px #f4ebff; /* 紫色光环 */
}

/* 文本域特殊高度 */
.textarea {
  height: auto;
  min-height: 120px;
  padding: 12px;
  resize: vertical;
}

/* --- 输入框组 (¥ 和 人) --- */
.input-group {
  position: relative;
  display: flex;
  align-items: center;
}

.input-addon {
  position: absolute;
  top: 1px; /* 微调 */
  bottom: 1px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #667085;
  font-size: 14px;
  background: transparent;
  pointer-events: none;
  z-index: 10;
}
.addon-left {
  left: 1px;
  width: 36px;
  border-right: 1px solid #d0d5dd;
  border-radius: 8px 0 0 8px;
  background: #f9fafb;
}
.addon-right {
  right: 1px;
  width: 36px;
  border-left: 1px solid #d0d5dd;
  border-radius: 0 8px 8px 0;
  background: #f9fafb;
}

/* 为带前缀后缀的输入框留出padding */
.has-addon-left {
  padding-left: 48px;
}
.has-addon-right {
  padding-right: 48px;
}

/* --- 下拉框优化 --- */
.select-wrapper {
  position: relative;
}
.select-arrow {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 10px;
  color: #667085;
  pointer-events: none;
}

/* --- 按钮样式 --- */
.form-actions {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 20px;
  border-top: 1px solid #eaecf0;
}

.btn {
  height: 40px; /* 按钮高度与输入框严格一致 */
  padding: 0 18px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  border: none;
  transition: all 0.2s;
}

.btn-secondary {
  background: #fff;
  border: 1px solid #d0d5dd;
  color: #344054;
}
.btn-secondary:hover {
  background: #f9fafb;
  border-color: #b2b8c2;
}

.btn-primary {
  background: var(--gradient-primary); /* 使用渐变 */
  color: #fff;
  box-shadow: 0 1px 2px rgba(16, 24, 40, 0.05);
}
.btn-primary:hover {
  opacity: 0.9;
  box-shadow: 0 4px 12px rgba(127, 86, 217, 0.25); /* 强化紫色阴影 */
}
.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error-msg {
  color: #b91c1c;
  font-size: 14px;
  background: #fef2f2;
  padding: 10px;
  border-radius: 6px;
}
</style>
