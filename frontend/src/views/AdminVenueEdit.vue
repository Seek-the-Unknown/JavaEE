<template>
  <div class="admin-edit-page">
    <div class="page-container">
      <header class="page-header">
        <div class="header-left">
          <h1 class="page-title">{{ isEditMode ? "编辑场馆" : "新增场馆" }}</h1>
          <p class="page-subtitle">请完善以下信息，带 * 为必填项</p>
        </div>
        <button class="back-btn" @click="router.push('/venues')">
          <span class="arrow">←</span> 返回列表
        </button>
      </header>

      <div class="main-card">
        <div class="card-body">
          <div class="upload-section">
            <div
              class="image-uploader"
              :class="{ 'has-image': imagePreview, 'is-loading': isUploading }"
              @click="triggerFileUpload"
            >
              <img
                v-if="imagePreview"
                :src="imagePreview"
                alt="预览"
                class="preview-img"
              />
              <div v-else class="upload-placeholder">
                <div class="icon-circle">
                  <i class="plus-icon">+</i>
                </div>
                <span class="upload-tip">点击上传封面</span>
                <span class="upload-subtip">支持 JPG, PNG</span>
              </div>

              <div class="upload-mask" v-if="imagePreview">
                <span>更换图片</span>
              </div>

              <div class="loading-mask" v-if="isUploading">
                <div class="spinner"></div>
              </div>

              <input
                type="file"
                ref="fileInput"
                @change="handleFileChange"
                accept="image/*"
                hidden
              />
            </div>
          </div>

          <div class="form-section">
            <VenueForm
              :initial-data="venueData"
              :is-loading="isLoading"
              :error-message="errorMessage"
              @submit="saveVenue"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import VenueForm from "@/components/VenueForm.vue";
import {
  getVenueById,
  createVenue,
  updateVenue,
  uploadImage,
} from "@/api/venue";

const route = useRoute();
const router = useRouter();

const venueData = ref({ imageUrl: "" });
const isLoading = ref(false);
const errorMessage = ref("");
const isUploading = ref(false);
const imagePreview = ref("");
const fileInput = ref(null);

const isEditMode = computed(() => !!route.params.id);
const venueId = computed(() => route.params.id);

const triggerFileUpload = () => {
  if (isUploading.value) return;
  fileInput.value.click();
};

const handleFileChange = async (event) => {
  const file = event.target.files[0];
  if (!file) return;

  const reader = new FileReader();
  reader.onload = (e) => (imagePreview.value = e.target.result);
  reader.readAsDataURL(file);

  isUploading.value = true;
  try {
    venueData.value.imageUrl = await uploadImage(file);
  } catch (error) {
    alert("上传失败: " + error.message);
  } finally {
    isUploading.value = false;
  }
};

const saveVenue = async (formData) => {
  isLoading.value = true;
  errorMessage.value = "";
  const finalData = { ...formData, imageUrl: venueData.value.imageUrl };

  try {
    if (isEditMode.value) {
      await updateVenue(venueId.value, finalData);
    } else {
      await createVenue(finalData);
    }
    router.push("/venues");
  } catch (error) {
    errorMessage.value = error.message;
  } finally {
    isLoading.value = false;
  }
};

onMounted(async () => {
  if (isEditMode.value) {
    isLoading.value = true;
    try {
      const data = await getVenueById(venueId.value);
      venueData.value = data;
      if (data.imageUrl) imagePreview.value = data.imageUrl;
    } catch (error) {
      errorMessage.value = "加载失败";
    } finally {
      isLoading.value = false;
    }
  }
});
</script>

<style scoped>
.admin-edit-page {
  background-color: var(--color-bg-main);
  min-height: calc(100vh - 72px);
  padding: 40px 0;
}

.page-container {
  max-width: 960px;
  margin: 0 auto;
  padding: 0 24px;
}

/* Header */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #101828;
  margin: 0 0 4px;
}
.page-subtitle {
  color: #667085;
  font-size: 14px;
  margin: 0;
}
.back-btn {
  height: 40px;
  padding: 0 16px;
  background: #fff;
  border: 1px solid #d0d5dd;
  border-radius: 8px;
  color: #344054;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;
}
.back-btn:hover {
  background: #f9fafb;
  border-color: #b2b8c2;
}

/* Main Card */
.main-card {
  background: #fff;
  border: 1px solid #eaecf0;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(16, 24, 40, 0.05);
  overflow: hidden;
}
.card-body {
  display: flex;
  flex-direction: row;
}

/* Left: Image Uploader */
.upload-section {
  width: 320px;
  background-color: #f9fafb;
  border-right: 1px solid #eaecf0;
  padding: 32px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
}

.image-uploader {
  width: 100%;
  aspect-ratio: 1;
  background: #fff; /* 默认白色背景 */
  border: 1px dashed #eaecf0;
  border-radius: 12px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}
.image-uploader:hover {
  border-color: var(--color-primary-start);
  background: #f4ebff; /* 悬停时变淡紫色 */
}

.upload-placeholder {
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.icon-circle {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #f4ebff;
  color: var(--color-primary-start);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
}
.plus-icon {
  font-style: normal;
  font-size: 24px;
  line-height: 1;
}
.upload-tip {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-primary-text);
  margin-bottom: 4px;
}
.upload-subtip {
  font-size: 12px;
  color: #667085;
}

/* --- 核心修改：让图片完整显示 --- */
.preview-img {
  width: 100%;
  height: 100%;
  object-fit: contain; /* 完整显示，不裁剪 */
  padding: 4px; /* 防止图片紧贴边缘 */
  background-color: #f9fafb; /* 给图片底部加一个浅灰背景，防止透明图片看不清 */
}
/* -------------------------------- */

.upload-mask {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 500;
  opacity: 0;
  transition: opacity 0.2s;
}
.image-uploader:hover .upload-mask {
  opacity: 1;
}

/* Spinner */
.spinner {
  width: 24px;
  height: 24px;
  border: 3px solid #e2e8f0;
  border-top-color: var(--color-primary-start);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Right: Form */
.form-section {
  flex: 1;
  padding: 32px;
}

@media (max-width: 800px) {
  .card-body {
    flex-direction: column;
  }
  .upload-section {
    width: 100%;
    border-right: none;
    border-bottom: 1px solid #eaecf0;
  }
  .image-uploader {
    width: 200px;
  }
}
</style>
