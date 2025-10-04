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
        
        <h1 class="text-h4 mb-6">글쓰기</h1>
        
        <v-card elevation="2" class="pa-6">
          <v-form @submit.prevent="submitPost" ref="form">
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
                
                <v-file-input
                  v-model="form.attachments"
                  label="첨부파일"
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
                  
                  <div class="d-flex gap-2">
                    <v-btn
                      type="submit"
                      color="primary"
                      :loading="loading"
                      block
                    >
                      게시
                    </v-btn>
                    
                    <v-btn
                      color="secondary"
                      variant="outlined"
                      @click="saveDraft"
                      block
                    >
                      임시저장
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
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useToast } from 'vue-toastification'

const router = useRouter()
const toast = useToast()

const loading = ref(false)
const formRef = ref()

const form = reactive({
  title: '',
  category: 'general',
  content: '',
  attachments: [],
  isNotice: false,
  isSecret: false,
  allowComments: true
})

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
  router.push('/board')
}

const submitPost = async () => {
  const { valid } = await formRef.value.validate()
  if (!valid) return
  
  try {
    loading.value = true
    
    // 게시글 작성 API 호출
    console.log('게시글 작성:', form)
    
    toast.success('게시글이 작성되었습니다.')
    router.push('/board')
  } catch (error: any) {
    toast.error(error.message || '게시글 작성에 실패했습니다.')
  } finally {
    loading.value = false
  }
}

const saveDraft = () => {
  // 임시저장 로직
  console.log('임시저장:', form)
  toast.success('임시저장되었습니다.')
}
</script>

<style scoped>
/* 글쓰기 스타일 */
</style>