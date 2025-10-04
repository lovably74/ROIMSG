<template>
  <v-container fluid class="py-6">
    <v-row>
      <v-col cols="12">
        <div class="d-flex justify-space-between align-center mb-6">
          <h1 class="text-h4">자료실</h1>
          <v-btn
            color="primary"
            prepend-icon="mdi-upload"
            @click="uploadFile"
          >
            파일 업로드
          </v-btn>
        </div>
        
        <v-card elevation="2">
          <v-card-title class="d-flex justify-space-between align-center">
            <span>파일 목록</span>
            
            <div class="d-flex align-center gap-2">
              <v-text-field
                v-model="searchQuery"
                placeholder="파일명 검색..."
                prepend-inner-icon="mdi-magnify"
                variant="outlined"
                density="compact"
                hide-details
                class="search-field"
              />
              
              <v-select
                v-model="selectedCategory"
                :items="categories"
                variant="outlined"
                density="compact"
                hide-details
                class="category-select"
              />
            </div>
          </v-card-title>
          
          <v-data-table
            :headers="headers"
            :items="files"
            :loading="loading"
            class="elevation-0"
          >
            <template v-slot:item.name="{ item }">
              <div class="d-flex align-center">
                <v-icon class="mr-2" :color="getFileIconColor(item.type)">
                  {{ getFileIcon(item.type) }}
                </v-icon>
                <span>{{ item.name }}</span>
              </div>
            </template>
            
            <template v-slot:item.size="{ item }">
              {{ formatFileSize(item.size) }}
            </template>
            
            <template v-slot:item.uploadedBy="{ item }">
              <div class="d-flex align-center">
                <v-avatar size="24" class="mr-2">
                  <v-img v-if="item.uploaderAvatar" :src="item.uploaderAvatar" />
                  <v-icon v-else size="16">mdi-account</v-icon>
                </v-avatar>
                {{ item.uploadedBy }}
              </div>
            </template>
            
            <template v-slot:item.uploadedAt="{ item }">
              {{ formatDate(item.uploadedAt) }}
            </template>
            
            <template v-slot:item.actions="{ item }">
              <v-btn
                icon="mdi-download"
                size="small"
                @click="downloadFile(item)"
                class="mr-1"
              />
              <v-btn
                icon="mdi-eye"
                size="small"
                @click="previewFile(item)"
                class="mr-1"
              />
              <v-btn
                v-if="canDelete(item)"
                icon="mdi-delete"
                size="small"
                @click="deleteFile(item)"
              />
            </template>
          </v-data-table>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const searchQuery = ref('')
const selectedCategory = ref('all')

const categories = [
  { title: '전체', value: 'all' },
  { title: '문서', value: 'document' },
  { title: '이미지', value: 'image' },
  { title: '동영상', value: 'video' },
  { title: '음악', value: 'audio' },
  { title: '기타', value: 'other' }
]

const headers = [
  { title: '파일명', key: 'name', sortable: false },
  { title: '크기', key: 'size' },
  { title: '업로드자', key: 'uploadedBy' },
  { title: '업로드일', key: 'uploadedAt' },
  { title: '액션', key: 'actions', sortable: false }
]

const files = ref([
  {
    id: '1',
    name: '프로젝트계획서.pdf',
    type: 'document',
    size: 1250000,
    uploadedBy: '김철수',
    uploaderAvatar: null,
    uploadedAt: '2025-01-27',
    category: 'document'
  },
  {
    id: '2',
    name: '회의록.docx',
    type: 'document',
    size: 850000,
    uploadedBy: '이영희',
    uploaderAvatar: null,
    uploadedAt: '2025-01-26',
    category: 'document'
  },
  {
    id: '3',
    name: '프로젝트이미지.png',
    type: 'image',
    size: 2100000,
    uploadedBy: '박민수',
    uploaderAvatar: null,
    uploadedAt: '2025-01-25',
    category: 'image'
  },
  {
    id: '4',
    name: '데모동영상.mp4',
    type: 'video',
    size: 15700000,
    uploadedBy: '김철수',
    uploaderAvatar: null,
    uploadedAt: '2025-01-24',
    category: 'video'
  }
])

const uploadFile = () => {
  router.push('/files/upload')
}

const downloadFile = (file: any) => {
  console.log('파일 다운로드:', file)
}

const previewFile = (file: any) => {
  console.log('파일 미리보기:', file)
}

const deleteFile = (file: any) => {
  if (confirm('파일을 삭제하시겠습니까?')) {
    console.log('파일 삭제:', file)
  }
}

const canDelete = (file: any) => {
  return authStore.user?.id === file.uploadedBy || authStore.user?.role === 'admin'
}

const getFileIcon = (type: string) => {
  const icons = {
    document: 'mdi-file-document',
    image: 'mdi-image',
    video: 'mdi-video',
    audio: 'mdi-music',
    other: 'mdi-file'
  }
  return icons[type as keyof typeof icons] || icons.other
}

const getFileIconColor = (type: string) => {
  const colors = {
    document: 'blue',
    image: 'green',
    video: 'purple',
    audio: 'orange',
    other: 'grey'
  }
  return colors[type as keyof typeof colors] || colors.other
}

const formatFileSize = (bytes: number) => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString('ko-KR')
}

onMounted(() => {
  // 파일 목록 로드
  loading.value = true
  setTimeout(() => {
    loading.value = false
  }, 1000)
})
</script>

<style scoped>
.search-field {
  width: 200px;
}

.category-select {
  width: 120px;
}
</style>