<template>
  <v-container class="not-found-container">
    <v-row justify="center" align="center" class="fill-height">
      <v-col cols="12" md="6" class="text-center">
        <div class="not-found-content">
          <h1 class="text-h1 font-weight-bold text-primary mb-4">404</h1>
          <h2 class="text-h4 mb-4">페이지를 찾을 수 없습니다</h2>
          <p class="text-body-1 text-medium-emphasis mb-6">
            요청하신 페이지가 존재하지 않거나 이동되었을 수 있습니다.
          </p>
          <v-btn
            color="primary"
            size="large"
            @click="goHome"
          >
            홈으로 돌아가기
          </v-btn>
        </div>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}
</script>

<style scoped>
.not-found-container {
  min-height: 100vh;
}

.not-found-content {
  max-width: 500px;
  margin: 0 auto;
}
</style>