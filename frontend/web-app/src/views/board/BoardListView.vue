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
        
        <!-- 필터 섹션 -->
        <v-card class="mb-4" elevation="1">
          <v-card-text>
            <v-row>
              <v-col cols="12" sm="6" md="3">
                <v-select
                  v-model="selectedCategory"
                  :items="categories"
                  label="카테고리"
                  variant="outlined"
                  density="compact"
                  hide-details
                  @update:model-value="handleFilterChange"
                />
              </v-col>
              <v-col cols="12" sm="6" md="6">
                <v-text-field
                  v-model="searchQuery"
                  label="검색"
                  placeholder="제목 또는 작성자를 입력하세요"
                  prepend-inner-icon="mdi-magnify"
                  variant="outlined"
                  density="compact"
                  hide-details
                  clearable
                  @keyup.enter="handleFilterChange"
                  @click:clear="handleFilterChange"
                />
              </v-col>
              <v-col cols="12" sm="6" md="2">
                <v-btn
                  block
                  color="primary"
                  variant="outlined"
                  @click="handleFilterChange"
                >
                  검색
                </v-btn>
              </v-col>
              <v-col cols="12" sm="6" md="1">
                <v-btn
                  block
                  variant="text"
                  @click="resetFilters"
                >
                  초기화
                </v-btn>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
        
        <!-- TOAST UI Grid -->
        <v-card elevation="2">
          <v-card-title>
            <span>게시글 목록</span>
            <v-spacer />
            <span class="text-subtitle-2">총 {{ totalCount }}개</span>
          </v-card-title>
          
          <v-card-text>
            <ToastGrid
              ref="gridRef"
              :data="gridData"
              :columns="gridColumns"
              :body-height="500"
              :row-headers="['rowNum']"
              :use-client-sort="false"
              @click="handleRowClick"
              @dblclick="handleRowDoubleClick"
            />
          </v-card-text>
          
          <v-divider />
          
          <!-- 페이지네이션 -->
          <v-card-actions class="justify-center">
            <v-pagination
              v-model="currentPage"
              :length="totalPages"
              :total-visible="7"
              @update:model-value="handlePageChange"
            />
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import ToastGrid from '@/components/ToastGrid.vue'
import { boardApi } from '@/utils/api'
import { useToast } from 'vue-toastification'

const router = useRouter()
const toast = useToast()

const loading = ref(false)
const searchQuery = ref('')
const selectedCategory = ref('all')
const gridRef = ref()
const currentPage = ref(1)
const pageSize = ref(20)
const totalCount = ref(0)

const categories = [
  { title: '전체', value: 'all' },
  { title: '공지사항', value: 'notice' },
  { title: '일반', value: 'general' },
  { title: '질문', value: 'question' },
  { title: '자유', value: 'free' }
]

// TOAST UI Grid 셸럼 정의 (간단하게 처리)
const gridColumns = computed(() => [
  {
    header: '카테고리',
    name: 'categoryDisplay',
    width: 100,
    align: 'center'
  },
  {
    header: '제목',
    name: 'titleDisplay',
    minWidth: 300
  },
  {
    header: '작성자',
    name: 'author',
    width: 120,
    align: 'center'
  },
  {
    header: '작성일',
    name: 'createdAtDisplay',
    width: 120,
    align: 'center'
  },
  {
    header: '조회수',
    name: 'views',
    width: 80,
    align: 'center'
  },
  {
    header: '좋아요',
    name: 'likes',
    width: 80,
    align: 'center'
  }
])

const allPosts = ref([])

// 필터된 게시글 목록
const filteredPosts = computed(() => {
  let filtered = allPosts.value
  
  // 카테고리 필터
  if (selectedCategory.value !== 'all') {
    filtered = filtered.filter(post => post.category === selectedCategory.value)
  }
  
  // 검색 필터
  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(post => 
      post.title.toLowerCase().includes(query) ||
      post.author.toLowerCase().includes(query)
    )
  }
  
  return filtered
})

// 페이지네이션된 데이터
const paginatedPosts = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredPosts.value.slice(start, end)
})

// TOAST UI Grid 데이터
const gridData = computed(() => {
  return paginatedPosts.value.map((post, index) => ({
    ...post,
    rowKey: post.id,
    categoryDisplay: getCategoryName(post.category),
    titleDisplay: (post.isNotice ? '[공지] ' : '') + post.title,
    createdAtDisplay: formatDate(post.createdAt),
    _attributes: {
      className: {
        row: post.isNotice ? 'notice-row' : ''
      }
    }
  }))
})

// 총 페이지 수
const totalPages = computed(() => {
  return Math.ceil(filteredPosts.value.length / pageSize.value)
})

const createPost = () => {
  router.push('/board/create')
}

const viewPost = (postId: string) => {
  router.push(`/board/${postId}`)
}

const handleRowClick = (ev: any) => {
  const rowKey = ev.rowKey
  if (rowKey) {
    viewPost(rowKey)
  }
}

const handleRowDoubleClick = (ev: any) => {
  const rowKey = ev.rowKey
  if (rowKey) {
    viewPost(rowKey)
  }
}

const handleFilterChange = () => {
  currentPage.value = 1
  updateTotalCount()
}

const resetFilters = () => {
  searchQuery.value = ''
  selectedCategory.value = 'all'
  currentPage.value = 1
  updateTotalCount()
}

const handlePageChange = (page: number) => {
  currentPage.value = page
}

const updateTotalCount = () => {
  totalCount.value = filteredPosts.value.length
}

const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString('ko-KR')
}

const getCategoryName = (category: string) => {
  const categoryItem = categories.find(cat => cat.value === category)
  return categoryItem?.title || category
}

const getCategoryColor = (category: string) => {
  const colorMap: Record<string, string> = {
    notice: 'error',
    general: 'primary',
    question: 'success',
    free: 'info'
  }
  return colorMap[category] || 'default'
}

// 간단한 렌더러 제거 - 데이터에서 직접 처리

// 게시글 목록 로드
const loadPosts = async () => {
  try {
    loading.value = true
    const response = await boardApi.getPosts()
    
    // API 응답을 프론트엔드 형식으로 변환
    allPosts.value = response.map((post: any) => ({
      id: post.id,
      title: post.title,
      author: post.authorName || '작성자',
      authorAvatar: null,
      createdAt: post.createdAt,
      views: post.views || 0,
      likes: post.likes || 0,
      isNotice: post.isNotice || false,
      category: post.category || 'general'
    }))
    
    updateTotalCount()
  } catch (error: any) {
    console.error('게시글 목록 로드 실패:', error)
    toast.error('게시글 목록을 불러오는데 실패했습니다.')
    
    // 에러 시 빈 배열로 초기화
    allPosts.value = []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadPosts()
})
</script>

<style scoped>
/* Grid 커스텀 스타일 */
:deep(.toast-grid-container .tui-grid-body-area .tui-grid-row:hover) {
  background-color: rgb(var(--v-theme-primary), 0.04);
  cursor: pointer;
}

:deep(.notice-row) {
  background-color: rgb(var(--v-theme-warning), 0.08);
  font-weight: 500;
}

/* 카테고리 셀 스타일 */
:deep(.category-cell) {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

:deep(.category-chip) {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
  text-align: center;
  white-space: nowrap;
}

:deep(.category-chip.error) {
  background-color: rgb(var(--v-theme-error), 0.1);
  color: rgb(var(--v-theme-error));
}

:deep(.category-chip.primary) {
  background-color: rgb(var(--v-theme-primary), 0.1);
  color: rgb(var(--v-theme-primary));
}

:deep(.category-chip.success) {
  background-color: rgb(var(--v-theme-success), 0.1);
  color: rgb(var(--v-theme-success));
}

:deep(.category-chip.info) {
  background-color: rgb(var(--v-theme-info), 0.1);
  color: rgb(var(--v-theme-info));
}

/* 제목 셀 스타일 */
:deep(.title-cell) {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 4px;
}

:deep(.notice-badge) {
  background-color: rgb(var(--v-theme-error));
  color: rgb(var(--v-theme-on-error));
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 10px;
  font-weight: bold;
  white-space: nowrap;
}

:deep(.title-text) {
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;
}

:deep(.title-text:hover) {
  color: rgb(var(--v-theme-primary));
  text-decoration: underline;
}

/* 작성자 셀 스타일 */
:deep(.author-cell) {
  text-align: center;
  font-weight: 500;
}

/* 날짜 셀 스타일 */
:deep(.date-cell) {
  text-align: center;
  font-size: 13px;
  color: rgb(var(--v-theme-on-surface), 0.8);
}

/* 반응형 */
@media (max-width: 768px) {
  :deep(.title-text) {
    max-width: 200px;
  }
}
</style>
