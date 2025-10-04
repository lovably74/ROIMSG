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
        
        <h1 class="text-h4 mb-6">게시글 수정</h1>
        
        <v-card elevation="2" class="pa-6">
          <v-form @submit.prevent="updatePost" ref="form">
            <v-row>
              <v-col cols="12" md="8">
                <v-text-field
                  v-model="form.title"
                  label="제목"
                  variant="outlined"
                  :rules="titleRules"
                  class="mb-4"
                  required
                />
                
                <v-select
                  v-model="form.category"
                  label="카테고리"
                  :items="categories"
                  variant="outlined"
                  class="mb-4"
                  required
                />
                
                <v-textarea
                  v-model="form.content"
                  label="내용"
                  variant="outlined"
                  :rules="contentRules"
                  rows="10"
                  class="mb-4"
                  required
                />
                
                <!-- 기존 첨부파일 -->
                <div v-if="existingAttachments.length > 0" class="existing-attachments mb-4">
                  <h3 class="text-h6 mb-3">기존 첨부파일</h3>
                  <v-list>
                    <v-list-item
                      v-for="attachment in existingAttachments"
                      :key="attachment.id"
                    >
                      <template v-slot:prepend>
                        <v-icon>mdi-paperclip</v-icon>
                      </template>
                      
                      <v-list-item-title>{{ attachment.name }}</v-list-item-title>
                      <v-list-item-subtitle>{{ attachment.size }}</v-list-item-subtitle>
                      
                      <template v-slot:append>
                        <v-btn
                          icon="mdi-delete"
                          size="small"
                          @click="removeExistingAttachment(attachment.id)"
                        />
                      </template>
                    </v-list-item>
                  </v-list>
                </div>
                
                <!-- 새 첨부파일 -->
                <v-file-input
                  v-model="form.newAttachments"
                  label="새 첨부파일"
                  variant="outlined"
                  multiple
                  show-size
                  class="mb-4"
                />
              </v-col>
              
              <v-col cols="12" md="4">
                <v-card variant="outlined" class="pa-4">
                  <h3 class="text-h6 mb-4">게시 옵션</h3>
                  
                  <v-switch
                    v-model="form.isNotice"
                    label="공지사항"
                    class="mb-2"
                  />
                  
                  <v-switch
                    v-model="form.isSecret"
                    label="비밀글"
                    class="mb-2"
                  />
                  
                  <v-switch
                    v-model="form.allowComments"
                    label="댓글 허용"
                    class="mb-4"
                  />
                  
                  <v-divider class="mb-4" />
                  
                  <div class="d-flex flex-column gap-2">
                    <v-btn
                      type="submit"
                      color="primary"
                      :loading="loading"
                      block
                    >
                      수정 완료
                    </v-btn>
                    
                    <v-btn
                      color="secondary"
                      variant="outlined"
                      @click="goBack"
                      block
                    >
                      취소
                    </v-btn>
                  </div>
                </v-card>
              </v-col>
            </v-row>
          </v-form>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useToast } from 'vue-toastification'

const route = useRoute()
const router = useRouter()
const toast = useToast()

const loading = ref(false)
const formRef = ref()

const form = reactive({
  title: '',
  category: 'general',
  content: '',
  newAttachments: [],
  isNotice: false,
  isSecret: false,
  allowComments: true
})

const existingAttachments = ref([
  {
    id: '1',
    name: '프로젝트계획서.pdf',
    size: '1.2 MB'
  }
])

const categories = [
  { title: '일반', value: 'general' },
  { title: '질문', value: 'question' },
  { title: '자유', value: 'free' }
]

const titleRules = [
  (v: string) => !!v || '제목은 필수입니다.',
  (v: string) => v.length >= 2 || '제목은 2자 이상이어야 합니다.'
]

const contentRules = [
  (v: string) => !!v || '내용은 필수입니다.',
  (v: string) => v.length >= 10 || '내용은 10자 이상이어야 합니다.'
]

const goBack = () => {
  const postId = route.params.id
  router.push(`/board/${postId}`)
}

const updatePost = async () => {
  const { valid } = await formRef.value.validate()
  if (!valid) return
  
  try {
    loading.value = true
    
    // 게시글 수정 API 호출
    console.log('게시글 수정:', form)
    
    toast.success('게시글이 수정되었습니다.')
    goBack()
  } catch (error: any) {
    toast.error(error.message || '게시글 수정에 실패했습니다.')
  } finally {
    loading.value = false
  }
}

const removeExistingAttachment = (attachmentId: string) => {
  if (confirm('첨부파일을 삭제하시겠습니까?')) {
    existingAttachments.value = existingAttachments.value.filter(
      att => att.id !== attachmentId
    )
  }
}

onMounted(() => {
  // 게시글 ID로 기존 데이터 로드
  const postId = route.params.id
  console.log('수정할 게시글 ID:', postId)
  
  // 기존 게시글 데이터 로드
  form.title = '프로젝트 진행 상황 공유'
  form.category = 'general'
  form.content = '안녕하세요. 현재 진행 중인 프로젝트의 상황을 공유드립니다.\n\n1. 백엔드 API 개발 완료\n2. 프론트엔드 UI 구현 중\n3. 테스트 계획 수립\n\n모두 수고하셨습니다!'
})
</script>

<style scoped>
.existing-attachments {
  background: #f5f5f5;
  padding: 16px;
  border-radius: 8px;
}
</style>