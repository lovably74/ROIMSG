<template>
  <v-container fluid class="py-6">
    <v-row>
      <v-col cols="12">
        <div class="d-flex justify-space-between align-center mb-6">
          <h1 class="text-h4">게시판</h1>
          <v-btn
            color="primary"
            prepend-icon="mdi-pencil"
            @click="createPost"
          >
            글쓰기
          </v-btn>
        </div>
        
        <v-card elevation="2">
          <v-card-title class="d-flex justify-space-between align-center">
            <span>게시글 목록</span>
            
            <div class="d-flex align-center gap-2">
              <v-text-field
                v-model="searchQuery"
                placeholder="검색..."
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
            :items="posts"
            :loading="loading"
            class="elevation-0"
            @click:row="viewPost"
          >
            <template v-slot:item.title="{ item }">
              <span class="post-title">{{ item.title }}</span>
              <v-chip
                v-if="item.isNotice"
                color="primary"
                size="x-small"
                class="ml-2"
              >
                공지
              </v-chip>
            </template>
            
            <template v-slot:item.author="{ item }">
              <div class="d-flex align-center">
                <v-avatar size="24" class="mr-2">
                  <v-img v-if="item.authorAvatar" :src="item.authorAvatar" />
                  <v-icon v-else size="16">mdi-account</v-icon>
                </v-avatar>
                {{ item.author }}
              </div>
            </template>
            
            <template v-slot:item.createdAt="{ item }">
              {{ formatDate(item.createdAt) }}
            </template>
            
            <template v-slot:item.views="{ item }">
              <v-icon size="16" class="mr-1">mdi-eye</v-icon>
              {{ item.views }}
            </template>
            
            <template v-slot:item.likes="{ item }">
              <v-icon size="16" class="mr-1">mdi-heart</v-icon>
              {{ item.likes }}
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

const router = useRouter()

const loading = ref(false)
const searchQuery = ref('')
const selectedCategory = ref('all')

const categories = [
  { title: '전체', value: 'all' },
  { title: '공지사항', value: 'notice' },
  { title: '일반', value: 'general' },
  { title: '질문', value: 'question' },
  { title: '자유', value: 'free' }
]

const headers = [
  { title: '제목', key: 'title', sortable: false },
  { title: '작성자', key: 'author' },
  { title: '작성일', key: 'createdAt' },
  { title: '조회수', key: 'views' },
  { title: '좋아요', key: 'likes' }
]

const posts = ref([
  {
    id: '1',
    title: '시스템 점검 안내',
    author: '관리자',
    authorAvatar: null,
    createdAt: '2025-01-27',
    views: 156,
    likes: 12,
    isNotice: true,
    category: 'notice'
  },
  {
    id: '2',
    title: '프로젝트 진행 상황 공유',
    author: '김철수',
    authorAvatar: null,
    createdAt: '2025-01-26',
    views: 89,
    likes: 8,
    isNotice: false,
    category: 'general'
  },
  {
    id: '3',
    title: '새로운 기능 사용법 질문',
    author: '이영희',
    authorAvatar: null,
    createdAt: '2025-01-25',
    views: 67,
    likes: 5,
    isNotice: false,
    category: 'question'
  },
  {
    id: '4',
    title: '오늘 날씨 정말 좋네요!',
    author: '박민수',
    authorAvatar: null,
    createdAt: '2025-01-24',
    views: 34,
    likes: 3,
    isNotice: false,
    category: 'free'
  }
])

const createPost = () => {
  router.push('/board/create')
}

const viewPost = (event: any, item: any) => {
  router.push(`/board/${item.id}`)
}

const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString('ko-KR')
}

onMounted(() => {
  // 게시글 목록 로드
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

.post-title {
  cursor: pointer;
}

.post-title:hover {
  color: #1976d2;
  text-decoration: underline;
}
</style>