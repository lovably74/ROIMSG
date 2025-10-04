<template>
  <v-container fluid class="py-6">
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">프로필</h1>
        
        <v-card elevation="2" class="pa-6">
          <v-row>
            <v-col cols="12" md="4" class="text-center">
              <v-avatar size="120" class="mb-4">
                <v-img
                  v-if="user?.profileImageUrl"
                  :src="user.profileImageUrl"
                  :alt="user.name"
                />
                <v-icon v-else size="60">mdi-account</v-icon>
              </v-avatar>
              
              <h3 class="text-h6 mb-2">{{ user?.name }}</h3>
              <p class="text-body-2 text-medium-emphasis">{{ user?.email }}</p>
            </v-col>
            
            <v-col cols="12" md="8">
              <v-form>
                <v-text-field
                  v-model="user?.name"
                  label="이름"
                  variant="outlined"
                  readonly
                  class="mb-4"
                />
                
                <v-text-field
                  v-model="user?.email"
                  label="이메일"
                  variant="outlined"
                  readonly
                  class="mb-4"
                />
                
                <v-text-field
                  v-model="user?.phoneNumber"
                  label="전화번호"
                  variant="outlined"
                  placeholder="전화번호를 입력하세요"
                  class="mb-4"
                />
                
                <v-textarea
                  v-model="user?.address"
                  label="주소"
                  variant="outlined"
                  placeholder="주소를 입력하세요"
                  rows="3"
                  class="mb-4"
                />
                
                <v-btn color="primary" @click="updateProfile">
                  프로필 수정
                </v-btn>
              </v-form>
            </v-col>
          </v-row>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const user = computed(() => authStore.user)

const updateProfile = () => {
  // 프로필 수정 로직
  console.log('프로필 수정')
}
</script>

<style scoped>
/* 프로필 스타일 */
</style>