<template>
  <div class="auth-callback">
    <div class="spinner" aria-busy="true" aria-live="polite">로그인 처리 중...</div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useToast } from 'vue-toastification'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const toast = useToast()
const auth = useAuthStore()

onMounted(async () => {
  const code = route.query.code as string | undefined
  if (!code) {
    toast.error('인증 코드가 없습니다.')
    router.push('/auth/login')
    return
  }
  try {
    await auth.loginWithGoogle(code)
    toast.success('로그인되었습니다.')
    router.push('/dashboard')
  } catch (e: any) {
    toast.error(e?.message || '로그인에 실패했습니다.')
    router.push('/auth/login')
  }
})
</script>

<style scoped>
.auth-callback {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 50vh;
}
.spinner {
  font-size: 1rem;
}
</style>