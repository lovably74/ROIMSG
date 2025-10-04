<template>
  <v-container fluid class="py-6">
    <v-row>
      <v-col cols="12">
        <v-btn
          icon="mdi-arrow-left"
          @click="goBack"
          class="mb-4"
        >
          뒤로
        </v-btn>
        
        <h1 class="text-h4 mb-6">파일 업로드</h1>
        
        <v-row>
          <v-col cols="12" md="8">
            <v-card elevation="2" class="pa-6">
              <v-form @submit.prevent="uploadFiles" ref="form">
                <!-- 드래그 앤 드롭 영역 -->
                <div
                  class="upload-area"
                  :class="{ 'drag-over': isDragOver }"
                  @dragover.prevent="handleDragOver"
                  @dragleave.prevent="handleDragLeave"
                  @drop.prevent="handleDrop"
                  @click="selectFiles"
                >
                  <div class="upload-content text-center">
                    <v-icon size="64" color="primary" class="mb-4">
                      mdi-cloud-upload
                    </v-icon>
                    <h3 class="text-h6 mb-2">파일을 여기에 드래그하거나 클릭하여 선택하세요</h3>
                    <p class="text-body-2 text-medium-emphasis">
                      지원 형식: PDF, DOC, DOCX, XLS, XLSX, PPT, PPTX, JPG, PNG, GIF, MP4, MP3
                    </p>
                    <p class="text-caption text-medium-emphasis">
                      최대 파일 크기: 10MB
                    </p>
                  </div>
                </div>
                
                <!-- 선택된 파일 목록 -->
                <div v-if="selectedFiles.length > 0" class="selected-files mt-6">
                  <h3 class="text-h6 mb-4">선택된 파일 ({{ selectedFiles.length }}개)</h3>
                  
                  <v-list>
                    <v-list-item
                      v-for="(file, index) in selectedFiles"
                      :key="index"
                      class="file-item"
                    >
                      <template v-slot:prepend>
                        <v-icon :color="getFileIconColor(file.type)">
                          {{ getFileIcon(file.type) }}
                        </v-icon>
                      </template>
                      
                      <v-list-item-title>{{ file.name }}</v-list-item-title>
                      <v-list-item-subtitle>
                        {{ formatFileSize(file.size) }}
                        <v-progress-linear
                          v-if="uploadProgress[index] > 0"
                          :model-value="uploadProgress[index]"
                          class="mt-2"
                        />
                      </v-list-item-subtitle>
                      
                      <template v-slot:append>
                        <v-btn
                          icon="mdi-close"
                          size="small"
                          @click="removeFile(index)"
                        />
                      </template>
                    </v-list-item>
                  </v-list>
                </div>
                
                <!-- 업로드 옵션 -->
                <div class="upload-options mt-6">
                  <h3 class="text-h6 mb-4">업로드 옵션</h3>
                  
                  <v-row>
                    <v-col cols="12" md="6">
                      <v-select
                        v-model="uploadOptions.category"
                        label="카테고리"
                        :items="categories"
                        variant="outlined"
                        class="mb-4"
                      />
                    </v-col>
                    
                    <v-col cols="12" md="6">
                      <v-switch
                        v-model="uploadOptions.isPublic"
                        label="공개 파일"
                        class="mb-4"
                      />
                    </v-col>
                  </v-row>
                  
                  <v-textarea
                    v-model="uploadOptions.description"
                    label="설명 (선택사항)"
                    variant="outlined"
                    rows="3"
                    class="mb-4"
                  />
                </div>
                
                <!-- 업로드 버튼 -->
                <div class="upload-actions mt-6">
                  <v-btn
                    type="submit"
                    color="primary"
                    size="large"
                    :loading="uploading"
                    :disabled="selectedFiles.length === 0"
                    prepend-icon="mdi-upload"
                  >
                    업로드 시작
                  </v-btn>
                  
                  <v-btn
                    color="secondary"
                    variant="outlined"
                    size="large"
                    @click="clearFiles"
                    class="ml-2"
                    :disabled="uploading"
                  >
                    초기화
                  </v-btn>
                </div>
              </v-form>
            </v-card>
          </v-col>
          
          <v-col cols="12" md="4">
            <v-card elevation="2" class="pa-4">
              <h3 class="text-h6 mb-4">업로드 가이드</h3>
              
              <v-list>
                <v-list-item>
                  <template v-slot:prepend>
                    <v-icon color="success">mdi-check-circle</v-icon>
                  </template>
                  <v-list-item-title>지원되는 파일 형식</v-list-item-title>
                </v-list-item>
                
                <v-list-item>
                  <template v-slot:prepend>
                    <v-icon color="warning">mdi-alert-circle</v-icon>
                  </template>
                  <v-list-item-title>최대 파일 크기: 10MB</v-list-item-title>
                </v-list-item>
                
                <v-list-item>
                  <template v-slot:prepend>
                    <v-icon color="info">mdi-information</v-icon>
                  </template>
                  <v-list-item-title>동시 업로드: 최대 5개</v-list-item-title>
                </v-list-item>
              </v-list>
            </v-card>
          </v-col>
        </v-row>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useToast } from 'vue-toastification'

const router = useRouter()
const toast = useToast()

const formRef = ref()
const isDragOver = ref(false)
const uploading = ref(false)
const selectedFiles = ref<File[]>([])
const uploadProgress = ref<number[]>([])

const uploadOptions = reactive({
  category: 'document',
  isPublic: true,
  description: ''
})

const categories = [
  { title: '문서', value: 'document' },
  { title: '이미지', value: 'image' },
  { title: '동영상', value: 'video' },
  { title: '음악', value: 'audio' },
  { title: '기타', value: 'other' }
]

const selectFiles = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.multiple = true
  input.accept = '.pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.jpg,.jpeg,.png,.gif,.mp4,.mp3'
  
  input.onchange = (e: any) => {
    const files = Array.from(e.target.files)
    addFiles(files)
  }
  
  input.click()
}

const handleDragOver = (e: DragEvent) => {
  e.preventDefault()
  isDragOver.value = true
}

const handleDragLeave = (e: DragEvent) => {
  e.preventDefault()
  isDragOver.value = false
}

const handleDrop = (e: DragEvent) => {
  e.preventDefault()
  isDragOver.value = false
  
  const files = Array.from(e.dataTransfer?.files || [])
  addFiles(files)
}

const addFiles = (files: File[]) => {
  const validFiles = files.filter(file => {
    const maxSize = 10 * 1024 * 1024 // 10MB
    if (file.size > maxSize) {
      toast.error(`${file.name}: 파일 크기가 너무 큽니다. (최대 10MB)`)
      return false
    }
    return true
  })
  
  selectedFiles.value.push(...validFiles)
  uploadProgress.value.push(...new Array(validFiles.length).fill(0))
}

const removeFile = (index: number) => {
  selectedFiles.value.splice(index, 1)
  uploadProgress.value.splice(index, 1)
}

const clearFiles = () => {
  selectedFiles.value = []
  uploadProgress.value = []
}

const uploadFiles = async () => {
  if (selectedFiles.value.length === 0) return
  
  try {
    uploading.value = true
    
    // 파일 업로드 시뮬레이션
    for (let i = 0; i < selectedFiles.value.length; i++) {
      for (let progress = 0; progress <= 100; progress += 10) {
        uploadProgress.value[i] = progress
        await new Promise(resolve => setTimeout(resolve, 100))
      }
    }
    
    toast.success(`${selectedFiles.value.length}개 파일이 업로드되었습니다.`)
    router.push('/files')
  } catch (error: any) {
    toast.error(error.message || '파일 업로드에 실패했습니다.')
  } finally {
    uploading.value = false
  }
}

const goBack = () => {
  router.push('/files')
}

const getFileIcon = (type: string) => {
  const icons = {
    'application/pdf': 'mdi-file-pdf',
    'application/msword': 'mdi-file-word',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document': 'mdi-file-word',
    'image/jpeg': 'mdi-image',
    'image/png': 'mdi-image',
    'image/gif': 'mdi-image',
    'video/mp4': 'mdi-video',
    'audio/mpeg': 'mdi-music'
  }
  return icons[type as keyof typeof icons] || 'mdi-file'
}

const getFileIconColor = (type: string) => {
  if (type.startsWith('image/')) return 'green'
  if (type.startsWith('video/')) return 'purple'
  if (type.startsWith('audio/')) return 'orange'
  if (type.includes('pdf')) return 'red'
  if (type.includes('word') || type.includes('document')) return 'blue'
  return 'grey'
}

const formatFileSize = (bytes: number) => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}
</script>

<style scoped>
.upload-area {
  border: 2px dashed #ccc;
  border-radius: 8px;
  padding: 48px 24px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #fafafa;
}

.upload-area:hover,
.upload-area.drag-over {
  border-color: #1976d2;
  background: #e3f2fd;
}

.upload-content {
  pointer-events: none;
}

.selected-files {
  background: #f5f5f5;
  border-radius: 8px;
  padding: 16px;
}

.file-item {
  background: white;
  border-radius: 4px;
  margin-bottom: 8px;
}

.upload-options {
  background: #f9f9f9;
  border-radius: 8px;
  padding: 16px;
}

.upload-actions {
  text-align: center;
}
</style>