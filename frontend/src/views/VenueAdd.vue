<template>
  <div class="add-container">
    <div class="page-header">
      <h2 style="margin: 0; color: #303133;">{{ isEditMode ? '编辑场馆' : '发布新场馆' }}</h2>
      <el-button @click="$router.back()">返回</el-button>
    </div>

    <el-card shadow="always" class="form-card">
      <el-form :model="form" label-position="top" size="large">
        <el-row :gutter="40">

          <el-col :span="9">
            <div class="upload-section">
              <el-form-item label="场馆封面图" required>
                <el-upload
                    class="avatar-uploader"
                    action="http://localhost:8888/api/file/upload"
                    :show-file-list="false"
                    :on-success="handleSuccess"
                    :before-upload="beforeUpload"
                >
                  <div v-if="form.cover" class="image-preview">
                    <img :src="form.cover" class="avatar" />
                    <div class="reupload-mask">
                      <el-icon><Edit /></el-icon>
                      <span>点击更换</span>
                    </div>
                  </div>

                  <div v-else class="upload-placeholder">
                    <el-icon :size="50" color="#a8abb2"><Plus /></el-icon>
                    <div class="upload-text">点击上传图片</div>
                  </div>
                </el-upload>
              </el-form-item>
            </div>
          </el-col>

          <el-col :span="15">
            <el-form-item label="场馆名称" required>
              <el-input v-model="form.name" placeholder="请输入名称" />
            </el-form-item>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="场馆类型" required>
                  <el-select v-model="form.type" style="width: 100%">
                    <el-option value="篮球" label="篮球" />
                    <el-option value="足球" label="足球" />
                    <el-option value="羽毛球" label="羽毛球" />
                    <el-option value="网球" label="网球" />
                    <el-option value="游泳" label="游泳" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="所在地区" required>
                  <el-cascader
                      v-model="selectedRegionArr"
                      :options="pcaTextArr"
                      placeholder="请选择省/市/区"
                      style="width: 100%"
                      separator=" / "
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="价格 (元/小时)" required>
                  <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="最大容纳人数" required>
                  <el-input-number v-model="form.capacity" :min="1" style="width: 100%" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="详细描述">
              <el-input v-model="form.description" type="textarea" :rows="5" placeholder="请输入详细地址、设施情况..." maxlength="200" show-word-limit />
            </el-form-item>

            <div style="margin-top: 30px;">
              <el-button type="primary" size="large" style="width: 180px; font-weight: bold;" @click="submit" :loading="loading">
                {{ isEditMode ? '保存修改' : '立即发布' }}
              </el-button>
            </div>
          </el-col>
        </el-row>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Edit } from '@element-plus/icons-vue'
import { pcaTextArr } from 'element-china-area-data'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const user = JSON.parse(localStorage.getItem('user'))
const isEditMode = computed(() => !!route.params.id)

const selectedRegionArr = ref([])

const form = ref({
  id: null,
  name: '',
  type: '篮球',
  region: '',
  price: 50,
  capacity: 10,
  description: '',
  cover: '',
})

onMounted(async () => {
  if (isEditMode.value) {
    const id = route.params.id
    const res = await axios.get(`http://localhost:8888/api/venue/${id}`)
    if (res.data.code === 200) {
      const data = res.data.data.venue
      form.value = { ...data }
      if (data.region) {
        selectedRegionArr.value = data.region.split(' / ')
      }
    }
  }
})

const handleSuccess = (res) => {
  if(res.code === 200) {
    form.value.cover = res.data;
    ElMessage.success('上传成功')
  }
}
const beforeUpload = (file) => file.type === 'image/jpeg' || file.type === 'image/png'

const submit = async () => {
  if(!form.value.name || !form.value.cover) return ElMessage.warning('请完善信息')
  if(!selectedRegionArr.value || selectedRegionArr.value.length === 0) return ElMessage.warning('请选择地区')

  loading.value = true
  form.value.region = selectedRegionArr.value.join(' / ')

  try {
    const url = isEditMode.value ? 'http://localhost:8888/api/venue/update' : 'http://localhost:8888/api/venue/add'
    await axios.post(url, form.value)
    ElMessage.success(isEditMode.value ? '修改成功' : '发布成功')
    router.push(isEditMode.value ? '/my-venues' : '/')
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.add-container { max-width: 1100px; margin: 30px auto; padding: 0 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.form-card { border-radius: 12px; }
.upload-section {
  text-align: center;
  background-color: #fafafa;
  border-radius: 8px;
  padding: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* ★★★ 关键 CSS 修改：强制居中 ★★★ */
.avatar-uploader {
  width: 100%;
  height: 350px; /* 固定高度，确保长方形区域 */
  display: block;
}

/* 使用 :deep 穿透 Element Plus 的内部样式 */
:deep(.el-upload) {
  border: 1px dashed #d9d9d9;
  border-radius: 8px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
  width: 100%;
  height: 100%;

  /* 强制 Flex 布局实现居中 */
  display: flex !important;
  justify-content: center;
  align-items: center;
  background-color: #fff;
}

:deep(.el-upload:hover) {
  border-color: #409EFF;
}

/* 占位符内容的布局 */
.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center; /* 垂直居中 */
  color: #8c939d;
  width: 100%;
  height: 100%;
}

.upload-text { margin-top: 10px; font-size: 14px; font-weight: 500; }

.image-preview { width: 100%; height: 100%; position: relative; }
.avatar { width: 100%; height: 100%; object-fit: cover; display: block; }

.reupload-mask {
  position: absolute; top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(0,0,0,0.5); color: white;
  display: flex; flex-direction: column;
  justify-content: center; align-items: center;
  opacity: 0; transition: opacity 0.3s;
}
.image-preview:hover .reupload-mask { opacity: 1; }
</style>