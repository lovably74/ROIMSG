# TOAST UI 컴포넌트 사용 가이드

## 📝 개요

이 프로젝트에는 Vue 3와 Vuetify를 기반으로 한 TOAST UI 컴포넌트들이 통합되어 있습니다.

### 포함된 컴포넌트

1. **ToastEditor** - TOAST UI Editor (WYSIWYG)
2. **ToastGrid** - TOAST UI Grid (데이터 그리드)
3. **ToastFileUploader** - 드래그앤드롭 파일 업로더

## 🛠️ 설치된 패키지

```json
{
  "@toast-ui/editor": "^3.2.2",
  "@toast-ui/editor-plugin-color-syntax": "^3.1.0",
  "tui-grid": "^4.21.19",
  "tui-pagination": "^3.4.1"
}
```

## 📋 컴포넌트 사용법

### 1. ToastEditor

WYSIWYG 에디터 컴포넌트

```vue
<template>
  <ToastEditor
    v-model="content"
    height="500px"
    placeholder="내용을 작성하세요..."
    initial-edit-type="wysiwyg"
    :hooks="{
      addImageBlobHook: handleImageUpload
    }"
    @change="handleContentChange"
  />
</template>

<script setup>
import { ref } from 'vue'

const content = ref('')

const handleImageUpload = (blob, callback) => {
  // 이미지 업로드 처리
  const reader = new FileReader()
  reader.onload = () => {
    callback(reader.result, blob.name)
  }
  reader.readAsDataURL(blob)
}

const handleContentChange = (newContent) => {
  console.log('Content changed:', newContent)
}
</script>
```

### 2. ToastGrid

데이터 그리드 컴포넌트

```vue
<template>
  <ToastGrid
    :data="gridData"
    :columns="columns"
    :body-height="500"
    :row-headers="['rowNum']"
    @click="handleRowClick"
    @dblclick="handleRowDoubleClick"
  />
</template>

<script setup>
import { ref, computed } from 'vue'

const posts = ref([
  { id: 1, title: '제목1', author: '작성자1', createdAt: '2025-01-01' },
  { id: 2, title: '제목2', author: '작성자2', createdAt: '2025-01-02' }
])

const columns = computed(() => [
  {
    header: '제목',
    name: 'title',
    width: 300
  },
  {
    header: '작성자', 
    name: 'author',
    width: 120,
    align: 'center'
  },
  {
    header: '작성일',
    name: 'createdAt', 
    width: 120,
    align: 'center'
  }
])

const gridData = computed(() => {
  return posts.value.map(post => ({
    ...post,
    rowKey: post.id
  }))
})

const handleRowClick = (ev) => {
  console.log('Row clicked:', ev.rowKey)
}
</script>
```

### 3. ToastFileUploader

파일 업로드 컴포넌트

```vue
<template>
  <ToastFileUploader
    :multiple="true"
    :max-files="10"
    :max-size="10"
    :accepted-types="['image/*', '.pdf', '.doc', '.docx']"
    :show-upload-list="true"
    :auto-upload="false"
    @change="handleFilesChange"
    @error="handleFileError"
  >
    <template #hint>
      <div class="upload-hint">
        <v-icon size="32" color="primary">mdi-cloud-upload</v-icon>
        <p>드래그하여 파일을 첨부하거나 클릭하여 선택하세요</p>
      </div>
    </template>
  </ToastFileUploader>
</template>

<script setup>
const handleFilesChange = (files) => {
  console.log('Files changed:', files)
}

const handleFileError = (error) => {
  console.error('File error:', error)
}
</script>
```

## 🔧 해결된 문제점들

### 1. 폼 컨트롤 문제
- **문제**: v-model이 작동하지 않는 문제
- **해결**: ref/reactive 구조 수정, 올바른 이벤트 핸들러 구현

### 2. 컴포넌트 import 문제  
- **문제**: 컴포넌트명 불일치 (kebab-case vs PascalCase)
- **해결**: 일관된 PascalCase 컴포넌트명 사용

### 3. TypeScript 타입 오류
- **문제**: TOAST UI 라이브러리 타입 오류
- **해결**: 적절한 타입 캐스팅 및 인터페이스 정의

### 4. 그리드 렌더러 복잡성
- **문제**: 커스텀 렌더러로 인한 복잡성
- **해결**: 데이터 전처리로 간소화

## 📁 파일 구조

```
src/
├── components/
│   ├── ToastEditor.vue       # WYSIWYG 에디터
│   ├── ToastGrid.vue         # 데이터 그리드
│   └── ToastFileUploader.vue # 파일 업로더
├── views/
│   └── board/
│       ├── BoardListView.vue   # 게시글 목록 (Grid 사용)
│       └── BoardCreateView.vue # 게시글 작성 (Editor + Uploader 사용)
└── components.d.ts            # 자동 컴포넌트 타입 등록
```

## 🚀 개발 서버 실행

```bash
# 패키지 설치
npm install

# 개발 서버 시작
npm run dev
```

## 📝 사용 예제 페이지

1. **게시글 목록**: `http://localhost:3000/board`
   - TOAST UI Grid 사용
   - 검색, 필터링, 페이지네이션 기능

2. **게시글 작성**: `http://localhost:3000/board/create`
   - TOAST UI Editor 사용
   - 파일 업로더 통합
   - 폼 유효성 검사

## ⚠️ 주의사항

1. **타입 안전성**: TypeScript 환경에서 일부 타입 경고가 발생할 수 있습니다.
2. **성능**: 대용량 데이터 처리 시 가상 스크롤링 고려 필요
3. **호환성**: Vue 3.3+ 버전 권장

## 🔗 관련 문서

- [TOAST UI Editor](https://ui.toast.com/tui-editor)
- [TOAST UI Grid](https://ui.toast.com/tui-grid)
- [Vue 3 Composition API](https://vuejs.org/guide/extras/composition-api-faq.html)
- [Vuetify 3](https://vuetifyjs.com/en/)

## 🐛 알려진 이슈

1. **SASS 경고**: Legacy JS API 사용으로 인한 deprecation warnings (기능상 문제없음)
2. **한국어 지원**: 현재 기본 영어 UI (필요시 언어팩 추가 가능)
3. **이미지 업로드**: 현재 base64 변환 사용 (실제 서버 업로드 구현 필요)

---

## 📞 지원

문제가 발생하거나 개선사항이 있으면 이슈를 생성해 주세요.