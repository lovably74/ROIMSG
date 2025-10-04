<template>
  <v-container fluid class="py-6">
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">사용자</h1>
        
        <v-card elevation="2">
          <v-card-title class="d-flex justify-space-between align-center">
            <span>사용자 목록</span>
            <v-btn color="primary" prepend-icon="mdi-plus">
              사용자 추가
            </v-btn>
          </v-card-title>
          
          <v-data-table
            :headers="headers"
            :items="users"
            :loading="loading"
            class="elevation-0"
          >
            <template v-slot:item.status="{ item }">
              <v-chip
                :color="item.status === 'active' ? 'success' : 'error'"
                size="small"
              >
                {{ item.status === 'active' ? '활성' : '비활성' }}
              </v-chip>
            </template>
            
            <template v-slot:item.actions="{ item }">
              <v-btn
                icon="mdi-pencil"
                size="small"
                @click="editUser(item)"
              />
              <v-btn
                icon="mdi-delete"
                size="small"
                @click="deleteUser(item)"
              />
            </template>
          </v-data-table>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

const loading = ref(false)
const users = ref([
  {
    id: '1',
    name: '김철수',
    email: 'kim@example.com',
    role: 'admin',
    status: 'active',
    lastLogin: '2025-01-27 10:30:00',
    createdAt: '2025-01-01 09:00:00'
  },
  {
    id: '2',
    name: '이영희',
    email: 'lee@example.com',
    role: 'user',
    status: 'active',
    lastLogin: '2025-01-26 15:45:00',
    createdAt: '2025-01-02 10:30:00'
  },
  {
    id: '3',
    name: '박민수',
    email: 'park@example.com',
    role: 'user',
    status: 'inactive',
    lastLogin: '2025-01-20 08:15:00',
    createdAt: '2025-01-03 14:20:00'
  }
])

const headers = [
  { title: '이름', key: 'name' },
  { title: '이메일', key: 'email' },
  { title: '역할', key: 'role' },
  { title: '상태', key: 'status' },
  { title: '마지막 로그인', key: 'lastLogin' },
  { title: '가입일', key: 'createdAt' },
  { title: '액션', key: 'actions', sortable: false }
]

const editUser = (user: any) => {
  console.log('사용자 수정:', user)
}

const deleteUser = (user: any) => {
  console.log('사용자 삭제:', user)
}

onMounted(() => {
  // 사용자 목록 로드
  loading.value = true
  setTimeout(() => {
    loading.value = false
  }, 1000)
})
</script>

<style scoped>
/* 사용자 목록 스타일 */
</style>