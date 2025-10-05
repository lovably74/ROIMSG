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
          <v-form @submit.prevent="submitPost" ref="formRef">
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
                
                <!-- TOAST UI Editor -->
                <div class="mb-4">
                  <label class="editor-label">내용 *</label>
                  <ToastEditor
                    ref="editorRef"
                    v-model="form.content"
                    height="500px"
                    placeholder="게시글 내용을 작성하세요..."
                    initial-edit-type="wysiwyg"
                    :hooks="{
                      addImageBlobHook: handleImageUpload
                    }"
                    @change="handleEditorChange"
                  />
                </div>
                
                <!-- 파일 업로더 -->
                <div class="mb-4">
                  <label class="uploader-label">첨부파일</label>
                  <ToastFileUploader
                    ref="fileUploaderRef"
                    :multiple="true"
                    :max-files="10"
                    :max-size="10"
                    :accepted-types="['image/*', '.pdf', '.doc', '.docx', '.xls', '.xlsx', '.txt']"
                    :show-upload-list="true"
                    :auto-upload="false"
                    @change="handleFilesChange"
                    @error="handleFileError"
                  >
                    <template #hint>
                      <div class="upload-hint">
                        <v-icon size="32" color="primary">mdi-cloud-upload</v-icon>
                        <p class="text-body-2 mt-2">
                          드래그하여 파일을 첨부하거나 클릭하여 선택하세요<br>
                          <small>최대 10개, 각 파일당 10MB 이하</small>
                        </p>
                      </div>
                    </template>
                  </ToastFileUploader>
                </div>
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
import ToastEditor from '@/components/ToastEditor.vue'
import ToastFileUploader from '@/components/ToastFileUploader.vue'
import { boardApi } from '@/utils/api'

const router = useRouter()
const toast = useToast()

const loading = ref(false)
const formRef = ref()
const editorRef = ref()
const fileUploaderRef = ref()

const form = reactive({
  title: '',
  category: 'general',
  content: '',
  attachments: [] as File[],
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
  // 폼 유효성 검사
  if (!validateForm()) {
    toast.error('필수 항목을 모두 입력해주세요.')
    return
  }
  
  try {
    loading.value = true
    
    // FormData 생성 (파일 첨부를 위해)
    const formData = new FormData()
    formData.append('title', form.title)
    formData.append('category', form.category)
    formData.append('content', form.content)
    formData.append('isNotice', String(form.isNotice))
    formData.append('isSecret', String(form.isSecret))
    formData.append('allowComments', String(form.allowComments))
    
    // 첨부파일 추가
    form.attachments.forEach((file, index) => {
      formData.append(`attachment_${index}`, file)
    })
    
    // 게시글 작성 API 호출
    const postData = {
      title: form.title,
      content: form.content,
      category: form.category,
      isNotice: form.isNotice,
      isSecret: form.isSecret,
      allowComments: form.allowComments
    }
    
    await boardApi.createPost(postData)
    
    // 성공 시 리다이렉트
    toast.success('게시글이 작성되었습니다.')
    router.push('/board')
  } catch (error: any) {
    console.error('게시글 작성 오류:', error)
    toast.error(error.message || '게시글 작성에 실패했습니다.')
  } finally {
    loading.value = false
  }
}

const saveDraft = () => {
  // 로컬 스토리지에 임시저장
  const draftData = {
    title: form.title,
    category: form.category,
    content: form.content,
    isNotice: form.isNotice,
    isSecret: form.isSecret,
    allowComments: form.allowComments,
    timestamp: Date.now()
  }
  
  localStorage.setItem('board_draft', JSON.stringify(draftData))
  toast.success('임시저장되었습니다.')
}

// 에디터 관련 메서드
const handleEditorChange = (content: string) => {
  form.content = content
}

const handleImageUpload = (blob: File, callback: (url: string, altText?: string) => void) => {
  // 이미지 업로드 처리
  const reader = new FileReader()
  reader.onload = () => {
    callback(reader.result as string, blob.name)
  }
  reader.readAsDataURL(blob)
}

// 파일 업로더 관련 메서드
const handleFilesChange = (files: File[]) => {
  form.attachments = files
}

const handleFileError = (error: { type: string; message: string; file?: File }) => {
  toast.error(error.message)
}

// 폼 유효성 검사
const validateForm = (): boolean => {
  if (!form.title.trim()) {
    toast.error('제목을 입력해주세요.')
    return false
  }
  
  if (form.title.length < 2) {
    toast.error('제목은 2자 이상이어야 합니다.')
    return false
  }
  
  if (!form.content.trim()) {
    toast.error('내용을 입력해주세요.')
    return false
  }
  
  if (form.content.length < 10) {
    toast.error('내용은 10자 이상이어야 합니다.')
    return false
  }
  
  return true
}

// 임시저장된 내용 불러오기
const loadDraft = () => {
  const saved = localStorage.getItem('board_draft')
  if (saved) {
    try {
      const draftData = JSON.parse(saved)
      form.title = draftData.title || ''
      form.category = draftData.category || 'general'
      form.content = draftData.content || ''
      form.isNotice = draftData.isNotice || false
      form.isSecret = draftData.isSecret || false
      form.allowComments = draftData.allowComments ?? true
      
      if (form.title || form.content) {
        toast.info('임시저장된 내용을 불러왔습니다.')
      }
    } catch (error) {
      console.error('임시저장 내용 로드 실패:', error)
    }
  }
}

// 컴포넌트 마운트 시 임시저장 내용 로드
loadDraft()
</script>

<style scoped>
.editor-label,
.uploader-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: rgb(var(--v-theme-on-surface));
  margin-bottom: 8px;
}

.upload-hint {
  text-align: center;
  padding: 20px;
  color: rgb(var(--v-theme-on-surface), 0.7);
}

.upload-hint p {
  margin: 0;
  line-height: 1.5;
}

.upload-hint small {
  color: rgb(var(--v-theme-on-surface), 0.5);
  font-size: 11px;
}

/* 에디터 래퍼 */
:deep(.toast-editor-wrapper) {
  border-radius: 8px;
  overflow: hidden;
}

/* 반응형 */
@media (max-width: 960px) {
  .v-col.md-4 {
    order: -1;
  }
}
</style>
