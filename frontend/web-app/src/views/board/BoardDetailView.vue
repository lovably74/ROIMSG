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
        
        <v-card elevation="2" class="pa-6">
          <div v-if="post">
            <!-- 게시글 헤더 -->
            <div class="post-header mb-6">
              <div class="d-flex justify-space-between align-start mb-4">
                <div>
                  <h1 class="text-h4 mb-2">{{ post.title }}</h1>
                  <div class="d-flex align-center gap-4">
                    <v-chip v-if="post.isNotice" color="primary" size="small">
                      공지
                    </v-chip>
                    <v-chip v-if="post.isSecret" color="warning" size="small">
                      비밀글
                    </v-chip>
                    <span class="text-body-2 text-medium-emphasis">
                      {{ post.category }}
                    </span>
                  </div>
                </div>
                
                <div class="d-flex gap-2">
                  <v-btn
                    v-if="canEdit"
                    color="primary"
                    variant="outlined"
                    @click="editPost"
                  >
                    수정
                  </v-btn>
                  <v-btn
                    v-if="canDelete"
                    color="error"
                    variant="outlined"
                    @click="deletePost"
                  >
                    삭제
                  </v-btn>
                </div>
              </div>
              
              <!-- 게시글 메타 정보 -->
              <div class="post-meta d-flex align-center gap-4">
                <div class="d-flex align-center">
                  <v-avatar size="32" class="mr-2">
                    <v-img v-if="post.authorAvatar" :src="post.authorAvatar" />
                    <v-icon v-else>mdi-account</v-icon>
                  </v-avatar>
                  <span class="font-weight-medium">{{ post.author }}</span>
                </div>
                
                <v-divider vertical />
                
                <div class="d-flex align-center gap-4">
                  <span class="text-body-2">
                    <v-icon size="16" class="mr-1">mdi-clock</v-icon>
                    {{ formatDate(post.createdAt) }}
                  </span>
                  
                  <span class="text-body-2">
                    <v-icon size="16" class="mr-1">mdi-eye</v-icon>
                    {{ post.views }}
                  </span>
                  
                  <span class="text-body-2">
                    <v-icon size="16" class="mr-1">mdi-heart</v-icon>
                    {{ post.likes }}
                  </span>
                </div>
              </div>
            </div>
            
            <!-- 게시글 내용 -->
            <div class="post-content mb-6">
              <div class="content-text" v-html="post.content"></div>
              
              <!-- 첨부파일 -->
              <div v-if="post.attachments && post.attachments.length > 0" class="attachments mt-6">
                <h3 class="text-h6 mb-3">첨부파일</h3>
                <v-list>
                  <v-list-item
                    v-for="attachment in post.attachments"
                    :key="attachment.id"
                  >
                    <template v-slot:prepend>
                      <v-icon>mdi-paperclip</v-icon>
                    </template>
                    
                    <v-list-item-title>{{ attachment.name }}</v-list-item-title>
                    <v-list-item-subtitle>{{ attachment.size }}</v-list-item-subtitle>
                    
                    <template v-slot:append>
                      <v-btn
                        icon="mdi-download"
                        size="small"
                        @click="downloadAttachment(attachment)"
                      />
                    </template>
                  </v-list-item>
                </v-list>
              </div>
            </div>
            
            <!-- 좋아요/싫어요 버튼 -->
            <div class="post-actions mb-6">
              <v-btn
                color="primary"
                variant="outlined"
                prepend-icon="mdi-heart"
                @click="toggleLike"
                class="mr-2"
              >
                좋아요 {{ post.likes }}
              </v-btn>
              
              <v-btn
                color="secondary"
                variant="outlined"
                prepend-icon="mdi-heart-broken"
                @click="toggleDislike"
              >
                싫어요 {{ post.dislikes }}
              </v-btn>
            </div>
            
            <!-- 댓글 섹션 -->
            <div class="comments">
              <h3 class="text-h6 mb-4">댓글 {{ comments.length }}</h3>
              
              <!-- 댓글 작성 -->
              <div class="comment-form mb-6">
                <v-textarea
                  v-model="newComment"
                  label="댓글을 입력하세요..."
                  variant="outlined"
                  rows="3"
                  class="mb-2"
                />
                <v-btn
                  color="primary"
                  @click="addComment"
                  :disabled="!newComment.trim()"
                >
                  댓글 작성
                </v-btn>
              </div>
              
              <!-- 댓글 목록 -->
              <div class="comment-list">
                <v-card
                  v-for="comment in comments"
                  :key="comment.id"
                  variant="outlined"
                  class="pa-4 mb-3"
                >
                  <div class="d-flex justify-space-between align-start mb-2">
                    <div class="d-flex align-center">
                      <v-avatar size="24" class="mr-2">
                        <v-img v-if="comment.authorAvatar" :src="comment.authorAvatar" />
                        <v-icon v-else size="16">mdi-account</v-icon>
                      </v-avatar>
                      <span class="font-weight-medium">{{ comment.author }}</span>
                    </div>
                    
                    <div class="d-flex align-center gap-2">
                      <span class="text-caption text-medium-emphasis">
                        {{ formatDate(comment.createdAt) }}
                      </span>
                      <v-btn
                        v-if="canDeleteComment(comment)"
                        icon="mdi-delete"
                        size="small"
                        @click="deleteComment(comment.id)"
                      />
                    </div>
                  </div>
                  
                  <p class="text-body-2 mb-0">{{ comment.content }}</p>
                </v-card>
              </div>
            </div>
          </div>
          
          <div v-else class="text-center pa-8">
            <v-progress-circular indeterminate />
            <p class="text-h6 text-medium-emphasis mt-4">게시글을 불러오는 중...</p>
          </div>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const post = ref({
  id: '1',
  title: '프로젝트 진행 상황 공유',
  content: '안녕하세요. 현재 진행 중인 프로젝트의 상황을 공유드립니다.<br/><br/>1. 백엔드 API 개발 완료<br/>2. 프론트엔드 UI 구현 중<br/>3. 테스트 계획 수립<br/><br/>모두 수고하셨습니다!',
  author: '김철수',
  authorAvatar: null,
  category: 'general',
  isNotice: false,
  isSecret: false,
  createdAt: '2025-01-26T10:30:00',
  updatedAt: '2025-01-26T10:30:00',
  views: 89,
  likes: 8,
  dislikes: 1,
  attachments: [
    {
      id: '1',
      name: '프로젝트계획서.pdf',
      size: '1.2 MB'
    }
  ]
})

const newComment = ref('')
const comments = ref([
  {
    id: '1',
    content: '수고하셨습니다!',
    author: '이영희',
    authorAvatar: null,
    createdAt: '2025-01-26T11:00:00'
  },
  {
    id: '2',
    content: '프로젝트 잘 진행되고 있네요.',
    author: '박민수',
    authorAvatar: null,
    createdAt: '2025-01-26T12:30:00'
  }
])

const canEdit = computed(() => {
  return authStore.user?.id === post.value.author
})

const canDelete = computed(() => {
  return authStore.user?.role === 'admin' || authStore.user?.id === post.value.author
})

const goBack = () => {
  router.push('/board')
}

const editPost = () => {
  router.push(`/board/${post.value.id}/edit`)
}

const deletePost = () => {
  if (confirm('게시글을 삭제하시겠습니까?')) {
    console.log('게시글 삭제')
    router.push('/board')
  }
}

const toggleLike = () => {
  console.log('좋아요')
}

const toggleDislike = () => {
  console.log('싫어요')
}

const addComment = () => {
  if (!newComment.value.trim()) return
  
  const comment = {
    id: Date.now().toString(),
    content: newComment.value,
    author: authStore.user?.name || '익명',
    authorAvatar: authStore.user?.profileImageUrl || null,
    createdAt: new Date().toISOString()
  }
  
  comments.value.push(comment)
  newComment.value = ''
}

const deleteComment = (commentId: string) => {
  if (confirm('댓글을 삭제하시겠습니까?')) {
    comments.value = comments.value.filter(c => c.id !== commentId)
  }
}

const canDeleteComment = (comment: any) => {
  return authStore.user?.id === comment.author || authStore.user?.role === 'admin'
}

const downloadAttachment = (attachment: any) => {
  console.log('첨부파일 다운로드:', attachment)
}

const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  // 게시글 ID로 상세 정보 로드
  const postId = route.params.id
  console.log('게시글 ID:', postId)
})
</script>

<style scoped>
.post-header {
  border-bottom: 1px solid #e0e0e0;
  padding-bottom: 24px;
}

.post-meta {
  padding: 16px;
  background: #f5f5f5;
  border-radius: 8px;
}

.content-text {
  line-height: 1.8;
  font-size: 16px;
}

.post-actions {
  border-top: 1px solid #e0e0e0;
  border-bottom: 1px solid #e0e0e0;
  padding: 16px 0;
}

.comment-form {
  background: #f9f9f9;
  padding: 16px;
  border-radius: 8px;
}
</style>