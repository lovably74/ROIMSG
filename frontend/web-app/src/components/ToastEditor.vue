<template>
  <div class="toast-editor-wrapper">
    <div ref="editorRef" class="toast-editor"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch, nextTick } from 'vue'
import Editor from '@toast-ui/editor'
import '@toast-ui/editor/dist/toastui-editor.css'

interface Props {
  modelValue?: string
  height?: string
  placeholder?: string
  initialEditType?: 'markdown' | 'wysiwyg'
  previewStyle?: 'tab' | 'vertical'
  hooks?: {
    addImageBlobHook?: (blob: File, callback: (url: string, altText?: string) => void) => void
  }
}

interface Emits {
  (e: 'update:modelValue', value: string): void
  (e: 'change', value: string): void
  (e: 'focus'): void
  (e: 'blur'): void
  (e: 'load'): void
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: '',
  height: '400px',
  placeholder: '내용을 입력하세요...',
  initialEditType: 'wysiwyg',
  previewStyle: 'vertical'
})

const emit = defineEmits<Emits>()

const editorRef = ref<HTMLElement>()
let editor: Editor | null = null

// Initialize TOAST UI Editor
const initializeEditor = () => {
  if (!editorRef.value) return

  try {
    const editorConfig: any = {
      el: editorRef.value,
      height: props.height,
      initialValue: props.modelValue || '',
      placeholder: props.placeholder,
      initialEditType: props.initialEditType,
      previewStyle: props.previewStyle,
      hideModeSwitch: false,
      useCommandShortcut: true,
      usageStatistics: false,
      toolbarItems: [
        ['heading', 'bold', 'italic', 'strike'],
        ['hr', 'quote'],
        ['ul', 'ol', 'indent', 'outdent'],
        ['table', 'image', 'link'],
        ['code', 'codeblock']
      ]
    }

    // Add hooks if provided
    if (props.hooks) {
      editorConfig.hooks = props.hooks
    }

    editor = new Editor(editorConfig)

    // Event listeners
    editor.on('change', () => {
      const content = editor!.getHTML()
      emit('update:modelValue', content)
      emit('change', content)
    })

    editor.on('focus', () => {
      emit('focus')
    })

    editor.on('blur', () => {
      emit('blur')
    })

    editor.on('load', () => {
      emit('load')
    })

  } catch (error) {
    console.error('TOAST UI Editor initialization failed:', error)
  }
}

// Destroy editor
const destroyEditor = () => {
  if (editor) {
    try {
      editor.destroy()
      editor = null
    } catch (error) {
      console.error('Error destroying editor:', error)
    }
  }
}

// Public methods
const setContent = (content: string) => {
  if (editor) {
    editor.setHTML(content)
  }
}

const getContent = (): string => {
  return editor ? editor.getHTML() : ''
}

const getMarkdown = (): string => {
  return editor ? editor.getMarkdown() : ''
}

const insertText = (text: string) => {
  if (editor) {
    editor.insertText(text)
  }
}

const focus = () => {
  if (editor) {
    editor.focus()
  }
}

const blur = () => {
  if (editor) {
    editor.blur()
  }
}

// Watch for external value changes
watch(
  () => props.modelValue,
  (newValue) => {
    if (editor && editor.getHTML() !== newValue) {
      editor.setHTML(newValue || '')
    }
  }
)

onMounted(() => {
  nextTick(() => {
    initializeEditor()
  })
})

onBeforeUnmount(() => {
  destroyEditor()
})

// Expose public methods
defineExpose({
  setContent,
  getContent,
  getMarkdown,
  insertText,
  focus,
  blur,
  getEditor: () => editor
})
</script>

<style scoped>
.toast-editor-wrapper {
  border: 1px solid rgb(var(--v-theme-on-surface), 0.12);
  border-radius: 8px;
  overflow: hidden;
  background-color: rgb(var(--v-theme-surface));
}

/* TOAST UI Editor 커스텀 스타일 */
.toast-editor-wrapper :deep(.toastui-editor-defaultUI) {
  border: none;
  font-family: 'Roboto', sans-serif;
}

.toast-editor-wrapper :deep(.toastui-editor-defaultUI-toolbar) {
  border-bottom: 1px solid rgb(var(--v-theme-on-surface), 0.12);
  background-color: rgb(var(--v-theme-surface-variant));
  padding: 8px 16px;
}

.toast-editor-wrapper :deep(.toastui-editor-toolbar-icons) {
  color: rgb(var(--v-theme-on-surface));
  transition: color 0.2s ease;
}

.toast-editor-wrapper :deep(.toastui-editor-toolbar-icons:hover) {
  color: rgb(var(--v-theme-primary));
  background-color: rgb(var(--v-theme-primary), 0.04);
}

.toast-editor-wrapper :deep(.toastui-editor-toolbar-icons.active) {
  color: rgb(var(--v-theme-primary));
  background-color: rgb(var(--v-theme-primary), 0.12);
}

/* 에디터 영역 */
.toast-editor-wrapper :deep(.toastui-editor-main) {
  background-color: rgb(var(--v-theme-surface));
}

.toast-editor-wrapper :deep(.toastui-editor-md-container),
.toast-editor-wrapper :deep(.toastui-editor-ww-container) {
  background-color: rgb(var(--v-theme-surface));
  color: rgb(var(--v-theme-on-surface));
}

/* 위지위그 에디터 */
.toast-editor-wrapper :deep(.ProseMirror) {
  font-family: 'Roboto', sans-serif;
  font-size: 14px;
  line-height: 1.6;
  color: rgb(var(--v-theme-on-surface));
  padding: 16px;
  min-height: 200px;
}

.toast-editor-wrapper :deep(.ProseMirror:focus) {
  outline: none;
}

/* 모드 스위치 탭 */
.toast-editor-wrapper :deep(.toastui-editor-mode-switch) {
  border-bottom: 1px solid rgb(var(--v-theme-on-surface), 0.12);
  background-color: rgb(var(--v-theme-surface-variant));
}

.toast-editor-wrapper :deep(.tab-item) {
  color: rgb(var(--v-theme-on-surface));
  background-color: transparent;
  border: none;
  border-radius: 4px 4px 0 0;
  padding: 8px 16px;
  margin: 0 4px;
  transition: all 0.2s ease;
}

.toast-editor-wrapper :deep(.tab-item:hover) {
  background-color: rgb(var(--v-theme-primary), 0.04);
  color: rgb(var(--v-theme-primary));
}

.toast-editor-wrapper :deep(.tab-item.active) {
  background-color: rgb(var(--v-theme-primary));
  color: rgb(var(--v-theme-on-primary));
}

/* 미리보기 영역 */
.toast-editor-wrapper :deep(.toastui-editor-md-preview) {
  background-color: rgb(var(--v-theme-surface));
  color: rgb(var(--v-theme-on-surface));
  padding: 16px;
}

/* 반응형 */
@media (max-width: 768px) {
  .toast-editor-wrapper :deep(.toastui-editor-defaultUI-toolbar) {
    flex-wrap: wrap;
    padding: 8px;
  }
  
  .toast-editor-wrapper :deep(.ProseMirror) {
    padding: 12px;
  }
}
</style>