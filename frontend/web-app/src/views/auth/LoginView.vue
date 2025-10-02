<template>
  <div class="login-view">
    <h1 class="sr-only">로그인</h1>
    <button class="btn btn-google" type="button" @click="onGoogleLogin">
      Google 계정으로 로그인
    </button>
  </div>
</template>

<script setup lang="ts">
import { useToast } from 'vue-toastification'
import { getEnv } from '@/utils/env'

const toast = useToast()

function buildGoogleAuthUrl(clientId: string, redirectUri: string) {
  const params = new URLSearchParams({
    client_id: clientId,
    redirect_uri: redirectUri,
    response_type: 'code',
    scope: 'openid profile email',
    access_type: 'offline',
    include_granted_scopes: 'true',
    prompt: 'consent'
  })
  const { VITE_GOOGLE_AUTH_URI } = getEnv()
  return `${VITE_GOOGLE_AUTH_URI}?${params.toString()}`
}

function onGoogleLogin() {
  const { VITE_GOOGLE_CLIENT_ID, VITE_GOOGLE_REDIRECT_URI } = getEnv()
  if (!VITE_GOOGLE_CLIENT_ID) {
    toast.error('Google Client ID가 설정되지 않았습니다.')
    return
  }
  const url = buildGoogleAuthUrl(VITE_GOOGLE_CLIENT_ID, VITE_GOOGLE_REDIRECT_URI)
  window.location.assign(url)
}
</script>

<style scoped>
.login-view {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
}
.btn-google {
  background: #fff;
  color: #333;
  border: 1px solid #ccc;
  border-radius: 6px;
  padding: 0.75rem 1.25rem;
  cursor: pointer;
}
.btn-google:hover {
  background: #f9f9f9;
}
.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}
</style>