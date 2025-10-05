<template>
  <div class="toast-file-uploader">
    <div
      :class="[
        'upload-area',
        {
          'drag-over': isDragOver,
          'has-error': hasError,
          'disabled': disabled
        }
      ]"
      @click="!disabled && triggerFileInput"
      @drop.prevent="handleDrop"
      @dragover.prevent="handleDragOver"
      @dragleave.prevent="handleDragLeave"
      @dragenter.prevent
    >
      <input
        ref="fileInput"
        type="file"
        :multiple="multiple"
        :accept="acceptedTypesString"
        :disabled="disabled"
        @change="handleFileSelect"
        style="display: none"
      />
      
      <div class="upload-content">
        <div class="upload-icon">
          <v-icon size="48" color="primary">mdi-cloud-upload</v-icon>
        </div>
        
        <div class="upload-text">
          <h3 class="upload-title">
            {{ isDragOver ? '파일을 여기에 놓으세요' : '파일을 업로드하세요' }}
          </h3>
          <p class="upload-description">
            파일을 드래그하여 놓거나 클릭하여 선택하세요
          </p>
          <p class="upload-restrictions" v-if="showRestrictions">
            <span v-if="maxFiles > 1">최대 {{ maxFiles }}개, </span>
            <span v-if="maxSize">각 파일당 {{ maxSize }}MB 이하, </span>
            <span v-if="acceptedTypesString">{{ formatAcceptedTypes }}</span>
          </p>
        </div>
      </div>
      
      <slot name="hint"></slot>
    </div>

    <!-- 업로드된 파일 목록 -->
    <div v-if="showUploadList && files.length > 0" class="uploaded-files">
      <h4 class="files-title">
        업로드된 파일 ({{ files.length }}{{ maxFiles > 1 ? '/' + maxFiles : '' }})
      </h4>
      
      <v-list class="files-list">
        <v-list-item
          v-for="(file, index) in files"
          :key="file.id"
          class="file-item"
          :class="{ 'upload-error': file.error, 'upload-success': file.uploaded }"
        >
          <template #prepend>
            <v-avatar size="40" class="file-icon">
              <v-icon :color="getFileTypeColor(file.type)">
                {{ getFileTypeIcon(file.name) }}
              </v-icon>
            </v-avatar>
          </template>
          
          <v-list-item-title class="file-name">
            {{ file.name }}
          </v-list-item-title>
          
          <v-list-item-subtitle class="file-details">
            {{ formatFileSize(file.size) }}
            <span v-if="file.error" class="error-message">
              - {{ file.error }}
            </span>
            <span v-else-if="file.uploading" class="upload-status">
              - 업로드 중...
            </span>
            <span v-else-if="file.uploaded" class="upload-status success">
              - 업로드 완료
            </span>
          </v-list-item-subtitle>
          
          <template #append>
            <div class="file-actions">
              <v-progress-circular
                v-if="file.uploading"
                :model-value="file.progress || 0"
                size="24"
                width="2"
                color="primary"
              />
              
              <v-btn
                v-if="!file.uploading"
                icon="mdi-delete"
                size="small"
                variant="text"
                color="error"
                @click="removeFile(index)"
              />
            </div>
          </template>
          
          <v-progress-linear
            v-if="file.uploading && file.progress !== undefined"
            :model-value="file.progress"
            color="primary"
            height="2"
            class="file-progress"
          />
        </v-list-item>
      </v-list>
    </div>

    <!-- 에러 메시지 -->
    <v-alert
      v-if="errorMessage"
      type="error"
      variant="tonal"
      closable
      class="mt-4"
      @click:close="errorMessage = ''"
    >
      {{ errorMessage }}
    </v-alert>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, nextTick } from 'vue'

interface FileItem {
  id: string
  name: string
  size: number
  type: string
  file: File
  uploaded: boolean
  uploading: boolean
  error?: string
  progress?: number
  url?: string
}

interface Props {
  multiple?: boolean
  maxFiles?: number
  maxSize?: number // MB
  acceptedTypes?: string[]
  showUploadList?: boolean
  autoUpload?: boolean
  uploadUrl?: string
  uploadHeaders?: Record<string, string>
  disabled?: boolean
  showRestrictions?: boolean
}

interface Emits {
  (e: 'change', files: File[]): void
  (e: 'upload-success', file: FileItem, response: any): void
  (e: 'upload-error', file: FileItem, error: any): void
  (e: 'progress', file: FileItem, progress: number): void
  (e: 'error', error: { type: string; message: string; file?: File }): void
  (e: 'remove', file: FileItem): void
}

const props = withDefaults(defineProps<Props>(), {
  multiple: false,
  maxFiles: 1,
  maxSize: 10, // 10MB
  acceptedTypes: () => ['image/*', '.pdf', '.doc', '.docx', '.txt'],
  showUploadList: true,
  autoUpload: false,
  uploadUrl: '',
  uploadHeaders: () => ({}),
  disabled: false,
  showRestrictions: true
})

const emit = defineEmits<Emits>()

const fileInput = ref<HTMLInputElement>()
const isDragOver = ref(false)
const files = ref<FileItem[]>([])
const errorMessage = ref('')
const hasError = ref(false)

// Computed properties
const acceptedTypesString = computed(() => {
  return props.acceptedTypes.join(',')
})

const formatAcceptedTypes = computed(() => {
  const types = props.acceptedTypes.map(type => {
    if (type.startsWith('image/')) return '이미지'
    if (type === '.pdf') return 'PDF'
    if (type === '.doc' || type === '.docx') return 'Word'
    if (type === '.xls' || type === '.xlsx') return 'Excel'
    if (type === '.txt') return '텍스트'
    return type
  })
  return types.join(', ') + ' 파일만 가능'
})

// Methods
const triggerFileInput = () => {
  fileInput.value?.click()
}

const handleDragOver = (event: DragEvent) => {
  if (props.disabled) return
  isDragOver.value = true
  if (event.dataTransfer) {
    event.dataTransfer.dropEffect = 'copy'
  }
}

const handleDragLeave = () => {
  isDragOver.value = false
}

const handleDrop = (event: DragEvent) => {
  if (props.disabled) return
  isDragOver.value = false
  
  const droppedFiles = Array.from(event.dataTransfer?.files || [])
  processFiles(droppedFiles)
}

const handleFileSelect = (event: Event) => {
  if (props.disabled) return
  
  const target = event.target as HTMLInputElement
  const selectedFiles = Array.from(target.files || [])
  processFiles(selectedFiles)
  
  // Reset input value
  target.value = ''
}

const processFiles = (newFiles: File[]) => {
  if (newFiles.length === 0) return

  // Check file count limit
  const totalFiles = files.value.length + newFiles.length
  if (totalFiles > props.maxFiles) {
    showError('file-count', `최대 ${props.maxFiles}개의 파일만 업로드할 수 있습니다.`)
    return
  }

  const validFiles: FileItem[] = []
  
  for (const file of newFiles) {
    // Check file size
    if (file.size > props.maxSize * 1024 * 1024) {
      showError('file-size', `파일 크기는 ${props.maxSize}MB 이하여야 합니다: ${file.name}`, file)
      continue
    }

    // Check file type
    if (!isValidFileType(file)) {
      showError('file-type', `지원하지 않는 파일 형식입니다: ${file.name}`, file)
      continue
    }

    // Check duplicate
    if (files.value.some(f => f.name === file.name && f.size === file.size)) {
      showError('duplicate', `이미 같은 파일이 존재합니다: ${file.name}`, file)
      continue
    }

    validFiles.push({
      id: generateId(),
      name: file.name,
      size: file.size,
      type: file.type,
      file,
      uploaded: false,
      uploading: false
    })
  }

  if (validFiles.length > 0) {
    files.value.push(...validFiles)
    emit('change', files.value.map(f => f.file))

    if (props.autoUpload && props.uploadUrl) {
      validFiles.forEach(uploadFile)
    }
  }
}

const isValidFileType = (file: File): boolean => {
  return props.acceptedTypes.some(type => {
    if (type.startsWith('image/')) {
      return file.type.startsWith('image/')
    }
    if (type.includes('*')) {
      const baseType = type.split('/')[0]
      return file.type.startsWith(baseType)
    }
    return file.name.toLowerCase().endsWith(type.toLowerCase()) || 
           file.type === type
  })
}

const uploadFile = async (fileItem: FileItem) => {
  if (!props.uploadUrl) return

  fileItem.uploading = true
  fileItem.progress = 0

  try {
    const formData = new FormData()
    formData.append('file', fileItem.file)

    const response = await fetch(props.uploadUrl, {
      method: 'POST',
      headers: {
        ...props.uploadHeaders
      },
      body: formData
    })

    if (response.ok) {
      const result = await response.json()
      fileItem.uploaded = true
      fileItem.uploading = false
      fileItem.progress = 100
      fileItem.url = result.url
      
      emit('upload-success', fileItem, result)
    } else {
      throw new Error(`업로드 실패: ${response.statusText}`)
    }
  } catch (error) {
    fileItem.uploading = false
    fileItem.error = error instanceof Error ? error.message : '업로드 중 오류가 발생했습니다.'
    emit('upload-error', fileItem, error)
  }
}

const removeFile = (index: number) => {
  const fileItem = files.value[index]
  files.value.splice(index, 1)
  emit('remove', fileItem)
  emit('change', files.value.map(f => f.file))
}

const clearFiles = () => {
  files.value = []
  emit('change', [])
}

const showError = (type: string, message: string, file?: File) => {
  hasError.value = true
  errorMessage.value = message
  emit('error', { type, message, file })
  
  // Auto-hide error after 5 seconds
  setTimeout(() => {
    hasError.value = false
    errorMessage.value = ''
  }, 5000)
}

const generateId = (): string => {
  return Date.now().toString(36) + Math.random().toString(36).substr(2)
}

const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const getFileTypeIcon = (filename: string): string => {
  const ext = filename.toLowerCase().split('.').pop() || ''
  
  if (['jpg', 'jpeg', 'png', 'gif', 'bmp', 'svg', 'webp'].includes(ext)) {
    return 'mdi-image'
  }
  if (['pdf'].includes(ext)) {
    return 'mdi-file-pdf-box'
  }
  if (['doc', 'docx'].includes(ext)) {
    return 'mdi-file-word'
  }
  if (['xls', 'xlsx'].includes(ext)) {
    return 'mdi-file-excel'
  }
  if (['txt'].includes(ext)) {
    return 'mdi-file-document'
  }
  return 'mdi-file'
}

const getFileTypeColor = (type: string): string => {
  if (type.startsWith('image/')) return 'green'
  if (type === 'application/pdf') return 'red'
  if (type.includes('word')) return 'blue'
  if (type.includes('sheet')) return 'green'
  return 'grey'
}

// Expose public methods
defineExpose({
  clearFiles,
  removeFile,
  uploadFile,
  files: () => files.value.map(f => f.file)
})
</script>

<style scoped>
.toast-file-uploader {
  width: 100%;
}

.upload-area {
  border: 2px dashed rgb(var(--v-theme-on-surface), 0.2);
  border-radius: 12px;
  padding: 32px 24px;
  text-align: center;
  background-color: rgb(var(--v-theme-surface));
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.upload-area:hover {
  border-color: rgb(var(--v-theme-primary));
  background-color: rgb(var(--v-theme-primary), 0.02);
}

.upload-area.drag-over {
  border-color: rgb(var(--v-theme-primary));
  background-color: rgb(var(--v-theme-primary), 0.08);
  transform: scale(1.02);
}

.upload-area.has-error {
  border-color: rgb(var(--v-theme-error));
  background-color: rgb(var(--v-theme-error), 0.04);
}

.upload-area.disabled {
  opacity: 0.6;
  cursor: not-allowed;
  background-color: rgb(var(--v-theme-on-surface), 0.04);
}

.upload-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.upload-icon {
  opacity: 0.8;
  transition: transform 0.3s ease;
}

.upload-area:hover .upload-icon,
.upload-area.drag-over .upload-icon {
  transform: scale(1.1);
}

.upload-text {
  max-width: 400px;
}

.upload-title {
  font-size: 20px;
  font-weight: 600;
  color: rgb(var(--v-theme-on-surface));
  margin: 0 0 8px 0;
}

.upload-description {
  font-size: 14px;
  color: rgb(var(--v-theme-on-surface), 0.7);
  margin: 0 0 8px 0;
}

.upload-restrictions {
  font-size: 12px;
  color: rgb(var(--v-theme-on-surface), 0.5);
  margin: 0;
  line-height: 1.4;
}

.uploaded-files {
  margin-top: 24px;
}

.files-title {
  font-size: 16px;
  font-weight: 600;
  color: rgb(var(--v-theme-on-surface));
  margin-bottom: 12px;
}

.files-list {
  background-color: rgb(var(--v-theme-surface-variant), 0.3);
  border-radius: 8px;
  padding: 8px;
}

.file-item {
  background-color: rgb(var(--v-theme-surface));
  border-radius: 8px;
  margin-bottom: 8px;
  border: 1px solid rgb(var(--v-theme-on-surface), 0.08);
  transition: all 0.2s ease;
  position: relative;
  overflow: hidden;
}

.file-item:last-child {
  margin-bottom: 0;
}

.file-item:hover {
  background-color: rgb(var(--v-theme-primary), 0.02);
  border-color: rgb(var(--v-theme-primary), 0.2);
}

.file-item.upload-error {
  border-color: rgb(var(--v-theme-error), 0.5);
  background-color: rgb(var(--v-theme-error), 0.04);
}

.file-item.upload-success {
  border-color: rgb(var(--v-theme-success), 0.5);
  background-color: rgb(var(--v-theme-success), 0.04);
}

.file-icon {
  background-color: rgb(var(--v-theme-surface-variant));
}

.file-name {
  font-weight: 500;
  color: rgb(var(--v-theme-on-surface));
  word-break: break-all;
}

.file-details {
  font-size: 12px;
  color: rgb(var(--v-theme-on-surface), 0.6);
}

.error-message {
  color: rgb(var(--v-theme-error));
  font-weight: 500;
}

.upload-status {
  color: rgb(var(--v-theme-info));
}

.upload-status.success {
  color: rgb(var(--v-theme-success));
}

.file-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.file-progress {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .upload-area {
    padding: 24px 16px;
  }
  
  .upload-title {
    font-size: 18px;
  }
  
  .upload-description {
    font-size: 13px;
  }
  
  .upload-restrictions {
    font-size: 11px;
  }
  
  .files-title {
    font-size: 14px;
  }
}

/* 드래그 상태에서의 애니메이션 */
.upload-area.drag-over::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent,
    rgb(var(--v-theme-primary), 0.1),
    transparent
  );
  animation: shimmer 1.5s infinite;
}

@keyframes shimmer {
  0% {
    left: -100%;
  }
  100% {
    left: 100%;
  }
}

/* 다크 테마 지원 */
[data-theme="dark"] .upload-area {
  background-color: rgb(var(--v-theme-surface-variant), 0.3);
}

[data-theme="dark"] .upload-area:hover {
  background-color: rgb(var(--v-theme-primary), 0.08);
}

[data-theme="dark"] .file-item {
  background-color: rgb(var(--v-theme-surface-variant));
}

[data-theme="dark"] .files-list {
  background-color: rgb(var(--v-theme-surface), 0.5);
}
</style>