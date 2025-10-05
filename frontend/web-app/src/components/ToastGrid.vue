<template>
  <div class="toast-grid-container">
    <div ref="gridElement" class="toast-grid"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch, nextTick } from 'vue'
import Grid from 'tui-grid'
import 'tui-grid/dist/tui-grid.css'

interface Props {
  data?: any[]
  columns?: any[]
  options?: Record<string, any>
  height?: string | number
  width?: string | number
  pagination?: boolean | object
  rowHeaders?: string[]
  summary?: object | null
  useClientSort?: boolean
  scrollX?: boolean
  scrollY?: boolean
  bodyHeight?: string | number
  columnOptions?: Record<string, any>
}

interface Emits {
  (e: 'selection-change', selectedRows: any[]): void
  (e: 'click', ev: any): void
  (e: 'dblclick', ev: any): void
  (e: 'mousedown', ev: any): void
  (e: 'mouseup', ev: any): void
  (e: 'mouseover', ev: any): void
  (e: 'mouseout', ev: any): void
  (e: 'focus-change', ev: any): void
  (e: 'sort', ev: any): void
  (e: 'filter', ev: any): void
  (e: 'before-request', ev: any): void
  (e: 'response', ev: any): void
  (e: 'success-response', ev: any): void
  (e: 'fail-response', ev: any): void
  (e: 'error-response', ev: any): void
}

const props = withDefaults(defineProps<Props>(), {
  data: () => [],
  columns: () => [],
  options: () => ({}),
  height: 'auto',
  width: 'auto',
  pagination: false,
  rowHeaders: () => [],
  summary: null,
  useClientSort: true,
  scrollX: true,
  scrollY: true,
  bodyHeight: 'auto',
  columnOptions: () => ({
    resizable: true,
    minWidth: 50,
    frozenCount: 0,
    frozenBorderWidth: 1
  })
})

const emit = defineEmits<Emits>()

const gridElement = ref<HTMLElement>()
const grid = ref<Grid | null>(null)

// 기본 그리드 옵션
const getGridOptions = () => {
  const baseOptions = {
    el: gridElement.value,
    data: props.data,
    columns: props.columns,
    width: props.width,
    bodyHeight: props.bodyHeight,
    rowHeaders: props.rowHeaders,
    summary: props.summary,
    useClientSort: props.useClientSort,
    scrollX: props.scrollX,
    scrollY: props.scrollY,
    columnOptions: props.columnOptions,
    // 한국어 설정
    header: {
      height: 40,
      complexColumns: []
    },
    // 페이지네이션 설정
    ...(props.pagination && {
      pagination: typeof props.pagination === 'boolean' 
        ? { perPage: 20 } 
        : props.pagination
    }),
    // 커스텀 옵션 병합
    ...props.options
  }

  return baseOptions
}

// 그리드 초기화
const initializeGrid = () => {
  if (!gridElement.value) return

  try {
    const options = getGridOptions()
    grid.value = new Grid(options)

    // 이벤트 리스너 등록
    setupEventListeners()

  } catch (error) {
    console.error('TOAST UI Grid initialization failed:', error)
  }
}

// 이벤트 리스너 설정
const setupEventListeners = () => {
  if (!grid.value) return

  // 선택 변경 이벤트
  grid.value.on('selection', () => {
    const selectedRows = grid.value!.getCheckedRows()
    emit('selection-change', selectedRows)
  })

  // 클릭 이벤트
  grid.value.on('click', (ev) => {
    emit('click', ev)
  })

  // 더블클릭 이벤트
  grid.value.on('dblclick', (ev) => {
    emit('dblclick', ev)
  })

  // 마우스 이벤트들
  grid.value.on('mousedown', (ev) => emit('mousedown', ev))
  grid.value.on('mouseup', (ev) => emit('mouseup', ev))
  grid.value.on('mouseover', (ev) => emit('mouseover', ev))
  grid.value.on('mouseout', (ev) => emit('mouseout', ev))

  // 포커스 변경
  grid.value.on('focusChange', (ev) => {
    emit('focus-change', ev)
  })

  // 정렬 이벤트
  grid.value.on('sort', (ev) => {
    emit('sort', ev)
  })

  // 필터 이벤트
  grid.value.on('filter', (ev) => {
    emit('filter', ev)
  })

  // 서버 요청 관련 이벤트들
  grid.value.on('beforeRequest', (ev) => emit('before-request', ev))
  grid.value.on('response', (ev) => emit('response', ev))
  grid.value.on('successResponse', (ev) => emit('success-response', ev))
  grid.value.on('failResponse', (ev) => emit('fail-response', ev))
  grid.value.on('errorResponse', (ev) => emit('error-response', ev))
}

// 그리드 제거
const destroyGrid = () => {
  if (grid.value) {
    try {
      grid.value.destroy()
      grid.value = null
    } catch (error) {
      console.error('Error destroying grid:', error)
    }
  }
}

// 데이터 업데이트
const updateData = (newData: any[]) => {
  if (grid.value) {
    grid.value.resetData(newData)
  }
}

// 컬럼 업데이트
const updateColumns = (newColumns: any[]) => {
  if (grid.value) {
    grid.value.setColumns(newColumns)
  }
}

// 행 추가
const appendRow = (row: any, options = {}) => {
  if (grid.value) {
    grid.value.appendRow(row, options)
  }
}

// 행 삭제
const removeRow = (rowKey: string | number) => {
  if (grid.value) {
    grid.value.removeRow(rowKey)
  }
}

// 행 수정
const setValue = (rowKey: string | number, columnName: string, value: any) => {
  if (grid.value) {
    grid.value.setValue(rowKey, columnName, value)
  }
}

// 선택된 행들 가져오기
const getCheckedRows = () => {
  return grid.value ? grid.value.getCheckedRows() : []
}

// 전체 데이터 가져오기
const getData = () => {
  return grid.value ? grid.value.getData() : []
}

// 특정 행 가져오기
const getRow = (rowKey: string | number) => {
  return grid.value ? grid.value.getRow(rowKey) : null
}

// 그리드 새로고침
const refreshLayout = () => {
  if (grid.value) {
    grid.value.refreshLayout()
  }
}

// 페이지네이션 관련 메서드들
const goToPage = (pageNumber: number) => {
  if (grid.value && grid.value.getPagination) {
    const pagination = grid.value.getPagination()
    if (pagination) {
      pagination.movePageTo(pageNumber)
    }
  }
}

const setPerPage = (perPage: number) => {
  if (grid.value && grid.value.getPagination) {
    const pagination = grid.value.getPagination()
    if (pagination) {
      pagination.setPerPage(perPage)
    }
  }
}

const getPaginationInfo = () => {
  if (grid.value && grid.value.getPagination) {
    const pagination = grid.value.getPagination()
    if (pagination) {
      return {
        currentPage: pagination.getCurrentPage(),
        totalPages: pagination.getTotalPages(),
        perPage: pagination.getPerPage(),
        totalCount: grid.value.getRowCount()
      }
    }
  }
  return null
}

// props 변경 감지
watch(
  () => props.data,
  (newData) => {
    if (grid.value && newData) {
      updateData(newData)
    }
  },
  { deep: true }
)

watch(
  () => props.columns,
  (newColumns) => {
    if (grid.value && newColumns) {
      updateColumns(newColumns)
    }
  },
  { deep: true }
)

// 라이프사이클
onMounted(() => {
  nextTick(() => {
    initializeGrid()
  })
})

onBeforeUnmount(() => {
  destroyGrid()
})

// 외부에서 사용할 수 있는 메서드들 노출
defineExpose({
  getGrid: () => grid.value,
  updateData,
  updateColumns,
  appendRow,
  removeRow,
  setValue,
  getCheckedRows,
  getData,
  getRow,
  refreshLayout,
  goToPage,
  setPerPage,
  getPaginationInfo
})
</script>

<style scoped>
/* TOAST UI Grid 컨테이너 */
.toast-grid-container {
  font-family: 'Roboto', sans-serif;
  background-color: rgb(var(--v-theme-surface));
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 그리드 전체 스타일 */
.toast-grid-container :deep(.tui-grid-container) {
  border: 1px solid rgb(var(--v-theme-on-surface), 0.12);
  border-radius: 8px;
  overflow: hidden;
  background-color: rgb(var(--v-theme-surface));
}

/* 헤더 스타일 */
.toast-grid-container :deep(.tui-grid-header-area) {
  background-color: rgb(var(--v-theme-surface-variant));
  border-bottom: 1px solid rgb(var(--v-theme-on-surface), 0.12);
}

.toast-grid-container :deep(.tui-grid-header-area .tui-grid-cell) {
  background-color: rgb(var(--v-theme-surface-variant));
  font-weight: 600;
  color: rgb(var(--v-theme-on-surface));
  border-right: 1px solid rgb(var(--v-theme-on-surface), 0.12);
  font-size: 14px;
  padding: 12px 16px;
}

.toast-grid-container :deep(.tui-grid-header-area .tui-grid-cell:hover) {
  background-color: rgb(var(--v-theme-primary), 0.04);
  color: rgb(var(--v-theme-primary));
}

/* 바디 스타일 */
.toast-grid-container :deep(.tui-grid-body-area) {
  background-color: rgb(var(--v-theme-surface));
}

.toast-grid-container :deep(.tui-grid-body-area .tui-grid-cell) {
  border-right: 1px solid rgb(var(--v-theme-on-surface), 0.08);
  border-bottom: 1px solid rgb(var(--v-theme-on-surface), 0.08);
  color: rgb(var(--v-theme-on-surface));
  padding: 12px 16px;
  font-size: 14px;
  line-height: 1.5;
}

.toast-grid-container :deep(.tui-grid-body-area .tui-grid-row:hover) {
  background-color: rgb(var(--v-theme-primary), 0.04);
}

.toast-grid-container :deep(.tui-grid-body-area .tui-grid-row.tui-grid-row-selected) {
  background-color: rgb(var(--v-theme-primary), 0.08);
}

.toast-grid-container :deep(.tui-grid-body-area .tui-grid-row.tui-grid-row-odd) {
  background-color: rgb(var(--v-theme-surface));
}

.toast-grid-container :deep(.tui-grid-body-area .tui-grid-row.tui-grid-row-even) {
  background-color: rgb(var(--v-theme-surface-variant), 0.3);
}

/* 체크박스 스타일 */
.toast-grid-container :deep(.tui-grid-cell-header.tui-grid-cell-row-header input[type="checkbox"]),
.toast-grid-container :deep(.tui-grid-cell.tui-grid-cell-row-header input[type="checkbox"]) {
  width: 16px;
  height: 16px;
  margin: 0;
  accent-color: rgb(var(--v-theme-primary));
}

/* 정렬 아이콘 스타일 */
.toast-grid-container :deep(.tui-grid-btn-sorting) {
  color: rgb(var(--v-theme-on-surface), 0.6);
}

.toast-grid-container :deep(.tui-grid-btn-sorting.tui-grid-btn-sorting-up),
.toast-grid-container :deep(.tui-grid-btn-sorting.tui-grid-btn-sorting-down) {
  color: rgb(var(--v-theme-primary));
}

/* 로딩 스타일 */
.toast-grid-container :deep(.tui-grid-layer-state-loading) {
  background-color: rgba(var(--v-theme-surface), 0.9);
}

.toast-grid-container :deep(.tui-grid-layer-state-loading .tui-grid-layer-state-content) {
  color: rgb(var(--v-theme-on-surface));
}

/* 페이지네이션 스타일 */
.toast-grid-container :deep(.tui-pagination) {
  margin-top: 20px;
  text-align: center;
  background-color: rgb(var(--v-theme-surface));
  padding: 16px;
}

.toast-grid-container :deep(.tui-pagination .tui-page-btn) {
  border: 1px solid rgb(var(--v-theme-on-surface), 0.12);
  background-color: rgb(var(--v-theme-surface));
  color: rgb(var(--v-theme-on-surface));
  padding: 8px 12px;
  margin: 0 4px;
  border-radius: 6px;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.toast-grid-container :deep(.tui-pagination .tui-page-btn:hover) {
  background-color: rgb(var(--v-theme-primary), 0.04);
  border-color: rgb(var(--v-theme-primary));
  color: rgb(var(--v-theme-primary));
}

.toast-grid-container :deep(.tui-pagination .tui-page-btn.tui-is-selected) {
  background-color: rgb(var(--v-theme-primary));
  border-color: rgb(var(--v-theme-primary));
  color: rgb(var(--v-theme-on-primary));
}

.toast-grid-container :deep(.tui-pagination .tui-page-btn.tui-is-disabled) {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 스크롤바 스타일 */
.toast-grid-container :deep(.tui-grid-content-area::-webkit-scrollbar) {
  width: 8px;
  height: 8px;
}

.toast-grid-container :deep(.tui-grid-content-area::-webkit-scrollbar-track) {
  background-color: rgb(var(--v-theme-surface-variant));
}

.toast-grid-container :deep(.tui-grid-content-area::-webkit-scrollbar-thumb) {
  background-color: rgb(var(--v-theme-on-surface), 0.3);
  border-radius: 4px;
}

.toast-grid-container :deep(.tui-grid-content-area::-webkit-scrollbar-thumb:hover) {
  background-color: rgb(var(--v-theme-on-surface), 0.5);
}

/* 빈 데이터 메시지 스타일 */
.toast-grid-container :deep(.tui-grid-layer-state-empty) {
  color: rgb(var(--v-theme-on-surface), 0.6);
  font-size: 14px;
  text-align: center;
  padding: 40px 20px;
}

/* 행 번호 스타일 */
.toast-grid-container :deep(.tui-grid-cell-row-header) {
  background-color: rgb(var(--v-theme-surface-variant)) !important;
  color: rgb(var(--v-theme-on-surface), 0.8);
  text-align: center;
  font-weight: 500;
}

/* 포커스 스타일 */
.toast-grid-container :deep(.tui-grid-cell-content:focus),
.toast-grid-container :deep(.tui-grid-cell:focus) {
  outline: 2px solid rgb(var(--v-theme-primary));
  outline-offset: -2px;
}

/* 드래그 선택 영역 */
.toast-grid-container :deep(.tui-grid-selection-layer) {
  background-color: rgb(var(--v-theme-primary), 0.12);
  border: 1px solid rgb(var(--v-theme-primary));
}

/* 컨텍스트 메뉴 */
.toast-grid-container :deep(.tui-grid-contextmenu) {
  background-color: rgb(var(--v-theme-surface));
  border: 1px solid rgb(var(--v-theme-on-surface), 0.12);
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  color: rgb(var(--v-theme-on-surface));
}

.toast-grid-container :deep(.tui-grid-contextmenu-item:hover) {
  background-color: rgb(var(--v-theme-primary), 0.04);
  color: rgb(var(--v-theme-primary));
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .toast-grid-container :deep(.tui-grid-header-area .tui-grid-cell) {
    padding: 8px 12px;
    font-size: 13px;
  }
  
  .toast-grid-container :deep(.tui-grid-body-area .tui-grid-cell) {
    padding: 8px 12px;
    font-size: 13px;
  }
  
  .toast-grid-container :deep(.tui-pagination) {
    padding: 12px;
  }
  
  .toast-grid-container :deep(.tui-pagination .tui-page-btn) {
    padding: 6px 8px;
    font-size: 13px;
    margin: 0 2px;
  }
}

/* 다크 테마 추가 지원 */
[data-theme="dark"] .toast-grid-container :deep(.tui-grid-container) {
  border-color: rgb(var(--v-theme-on-surface), 0.2);
}

[data-theme="dark"] .toast-grid-container :deep(.tui-grid-header-area) {
  background-color: rgb(var(--v-theme-surface-variant), 0.8);
}

[data-theme="dark"] .toast-grid-container :deep(.tui-grid-body-area .tui-grid-row:hover) {
  background-color: rgb(var(--v-theme-primary), 0.08);
}

[data-theme="dark"] .toast-grid-container :deep(.tui-grid-body-area .tui-grid-row-even) {
  background-color: rgb(var(--v-theme-surface-variant), 0.2);
}
</style>