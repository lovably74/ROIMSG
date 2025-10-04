<template>
  <v-container fluid class="py-6">
    <v-row>
      <v-col cols="12" md="3" v-for="card in summaryCards" :key="card.title">
        <v-card elevation="3" class="pa-4">
          <div class="text-subtitle-1 font-weight-medium">{{ card.title }}</div>
          <div class="text-h4 font-weight-bold mt-2">{{ card.value }}</div>
          <div class="text-caption text-medium-emphasis">{{ card.subtitle }}</div>
        </v-card>
      </v-col>
    </v-row>

    <v-row class="mt-4">
      <v-col cols="12" md="6">
        <v-card elevation="2" class="pa-4">
          <div class="text-subtitle-1 font-weight-medium mb-2">최근 알림</div>
          <v-list density="compact">
            <v-list-item v-for="notice in recentNotices" :key="notice.id">
              <v-list-item-title>{{ notice.title }}</v-list-item-title>
              <v-list-item-subtitle>{{ notice.date }}</v-list-item-subtitle>
            </v-list-item>
          </v-list>
        </v-card>
      </v-col>
      <v-col cols="12" md="6">
        <v-card elevation="2" class="pa-4">
          <div class="text-subtitle-1 font-weight-medium mb-2">진행 중인 작업</div>
          <v-timeline density="compact">
            <v-timeline-item
              v-for="task in tasks"
              :key="task.id"
              :dot-color="task.color"
              size="small"
            >
              <div class="text-subtitle-2">{{ task.title }}</div>
              <div class="text-caption text-medium-emphasis">{{ task.description }}</div>
            </v-timeline-item>
          </v-timeline>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const summaryCards = computed(() => [
  { title: '활성 사용자', value: 128, subtitle: '지난 7일 기준' },
  { title: '새 메시지', value: 342, subtitle: '오늘 접수된 메시지' },
  { title: '게시글', value: 58, subtitle: '이번 주 등록된 글' },
  { title: '파일 업로드', value: 24, subtitle: '오늘 업로드된 파일' }
])

const recentNotices = computed(() => [
  { id: 1, title: '시스템 점검 예정 안내', date: '2025-10-03' },
  { id: 2, title: '신규 기능 베타 오픈', date: '2025-10-01' },
  { id: 3, title: '데이터 이관 완료 보고', date: '2025-09-29' }
])

const tasks = computed(() => [
  { id: 1, title: '알림 시스템 개선', description: '푸시 알림 연동 테스트 중', color: 'primary' },
  { id: 2, title: '접근성 점검', description: 'WCAG 2.2 AA 셀프 점검', color: 'success' },
  { id: 3, title: '보안 패치 적용', description: 'OWASP 권고 사항 반영', color: 'warning' }
])
</script>
