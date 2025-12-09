<template>
  <div class="admin-edit-page">
    <header class="page-header">
      <div>
        <h1 class="page-title">{{ isEditMode ? "编辑场馆" : "新增场馆" }}</h1>
        <p class="page-subtitle">填写场馆信息并上传图片</p>
      </div>
      <router-link to="/venues" class="back-link">返回列表 &rarr;</router-link>
    </header>

    <div class="content-grid">
      <!-- 左侧图片上传 -->
      <div class="card">
        <h2 class="card-title">场馆图片</h2>
        <div class="image-uploader" @click="triggerFileUpload">
          <img
            v-if="imagePreview"
            :src="imagePreview"
            alt="场馆预览"
            class="preview-image"
          />
          <div v-else class="upload-placeholder">
            <div class="upload-icon-wrapper">
              <i class="icon icon-upload"></i>
            </div>
            <p class="upload-text">点击上传图片</p>
            <p class="upload-hint">支持 JPG, PNG 格式</p>
          </div>
          <input
            type="file"
            ref="fileInput"
            @change="handleFileChange"
            accept="image/*"
            style="display: none"
          />
        </div>
        <div v-if="isUploading" class="upload-status">图片上传中...</div>
      </div>

      <!-- 右侧表单 -->
      <div class="card">
        <VenueForm
          :initial-data="venueData"
          :is-loading="isLoading"
          :error-message="errorMessage"
          @submit="saveVenue"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
// ... <script setup> 部分保持不变 ...
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
  fileInput.value.click();
};
const handleFileChange = async (event) => {
  const file = event.target.files[0];
  if (!file) return;
  const reader = new FileReader();
  reader.onload = (e) => {
    imagePreview.value = e.target.result;
  };
  reader.readAsDataURL(file);

  isUploading.value = true;
  try {
    venueData.value.imageUrl = await uploadImage(file);
  } catch (error) {
    errorMessage.value = "图片上传失败: " + error.message;
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
      alert("场馆更新成功！");
    } else {
      await createVenue(finalData);
      alert("场馆新增成功！");
    }
    router.push("/venues");
  } catch (error) {
    errorMessage.value = error.message || "操作失败";
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
      if (data.imageUrl) {
        imagePreview.value = data.imageUrl;
      }
    } catch (error) {
      errorMessage.value = "加载场馆数据失败";
    } finally {
      isLoading.value = false;
    }
  }
});
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
  color: var(--color-primary);
  text-decoration: none;
  font-weight: 500;
}

.content-grid {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 32px;
}
.card {
  background: #fff;
  padding: 24px;
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--color-border);
}
.card-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 16px;
}

.image-uploader {
  aspect-ratio: 1 / 1;
  border: 2px dashed var(--color-border);
  border-radius: var(--border-radius-lg);
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  overflow: hidden;
  transition: border-color 0.3s;
}
.image-uploader:hover {
  border-color: var(--color-primary);
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
  background: #f8fafc;
  color: var(--color-primary);
  display: inline-flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 12px;
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
