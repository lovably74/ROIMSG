<template>
  <div class="register-view">
    <h1 class="text-h4 text-center mb-6">회원가입</h1>
    
    <v-form @submit.prevent="onRegister" ref="form">
      <v-text-field
        v-model="form.email"
        label="이메일"
        type="email"
        :rules="emailRules"
        variant="outlined"
        class="mb-4"
        required
      />
      
      <v-text-field
        v-model="form.password"
        label="비밀번호"
        type="password"
        :rules="passwordRules"
        variant="outlined"
        class="mb-4"
        required
      />
      
      <v-text-field
        v-model="form.confirmPassword"
        label="비밀번호 확인"
        type="password"
        :rules="confirmPasswordRules"
        variant="outlined"
        class="mb-4"
        required
      />
      
      <v-text-field
        v-model="form.name"
        label="이름"
        :rules="nameRules"
        variant="outlined"
        class="mb-6"
        required
      />
      
      <v-btn
        type="submit"
        color="primary"
        size="large"
        block
        :loading="loading"
        class="mb-4"
      >
        회원가입
      </v-btn>
    </v-form>
    
    <v-divider class="my-6" />
    
    <v-btn
      @click="onGoogleLogin"
      variant="outlined"
      size="large"
      block
      class="mb-4"
    >
      <v-icon left>mdi-google</v-icon>
      Google로 회원가입
    </v-btn>
    
    <div class="text-center">
      <span class="text-body-2 text-medium-emphasis">이미 계정이 있으신가요? </span>
      <router-link to="/auth/login" class="text-primary text-decoration-none">
        로그인
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useToast } from 'vue-toastification'
import { useAuthStore } from '@/stores/auth'
import { getEnv } from '@/utils/env'

const router = useRouter()
const toast = useToast()
const authStore = useAuthStore()

const form = reactive({
  email: '',
  password: '',
  confirmPassword: '',
  name: ''
})

const loading = ref(false)
const formRef = ref()

// 유효성 검사 규칙
const emailRules = [
  (v: string) => !!v || '이메일은 필수입니다.',
  (v: string) => /.+@.+\..+/.test(v) || '올바른 이메일 형식이 아닙니다.'
]

const passwordRules = [
  (v: string) => !!v || '비밀번호는 필수입니다.',
  (v: string) => v.length >= 8 || '비밀번호는 8자 이상이어야 합니다.'
]

const confirmPasswordRules = [
  (v: string) => !!v || '비밀번호 확인은 필수입니다.',
  (v: string) => v === form.password || '비밀번호가 일치하지 않습니다.'
]

const nameRules = [
  (v: string) => !!v || '이름은 필수입니다.',
  (v: string) => v.length >= 2 || '이름은 2자 이상이어야 합니다.'
]

const onRegister = async () => {
  const { valid } = await formRef.value.validate()
  if (!valid) return
  
  try {
    loading.value = true
    await authStore.login({
      email: form.email,
      password: form.password
    })
    toast.success('회원가입에 성공했습니다.')
    router.push('/dashboard')
  } catch (error: any) {
    toast.error(error.message || '회원가입에 실패했습니다.')
  } finally {
    loading.value = false
  }
}

const onGoogleLogin = () => {
  // Google OAuth 로그인 로직은 LoginView와 동일
  const { VITE_GOOGLE_CLIENT_ID, VITE_GOOGLE_REDIRECT_URI } = getEnv()
  if (!VITE_GOOGLE_CLIENT_ID) {
    toast.error('Google Client ID가 설정되지 않았습니다.')
    return
  }
  
  const params = new URLSearchParams({
    client_id: VITE_GOOGLE_CLIENT_ID,
    redirect_uri: VITE_GOOGLE_REDIRECT_URI,
    response_type: 'code',
    scope: 'openid profile email',
    access_type: 'offline',
    include_granted_scopes: 'true',
    prompt: 'consent'
  })
  
  const authUrl = `https://accounts.google.com/o/oauth2/v2/auth?${params.toString()}`
  window.location.assign(authUrl)
}
</script>

<style scoped>
.register-view {
  max-width: 400px;
  margin: 0 auto;
}
</style>