<template>
  <div class="register-google">
    <h1 class="title">Google 계정으로 회원가입</h1>

    <div v-if="!ready" class="loading">사전 정보를 불러오는 중...</div>
    <div v-else>
      <div class="profile">
        <img v-if="profile?.picture" :src="profile.picture" alt="Profile" class="avatar" />
        <div class="info">
          <div class="field">
            <label>이메일</label>
            <input type="email" :value="profile?.email" disabled />
          </div>
          <div class="field">
            <label>이름</label>
            <input type="text" v-model="name" />
          </div>
        </div>
      </div>

      <div class="consent">
        <h2>개인정보 처리방침 동의</h2>
        <div class="policy-box">
          <p>
            본 서비스는 회원가입 및 서비스 제공을 위해 Google 계정에서 제공한 기본 프로필 정보(이메일, 이름, 프로필 이미지)를 수집·이용합니다.
            수집된 정보는 서비스 이용에 필요한 범위에서만 사용되며, 관련 법령에 따라 안전하게 관리됩니다. 자세한 내용은 개인정보 처리방침을 참고해 주세요.
          </p>
        </div>
        <label class="checkbox">
          <input type="checkbox" v-model="agreePrivacy" /> 개인정보 처리방침에 동의합니다.
        </label>
        <label class="checkbox">
          <input type="checkbox" v-model="agreeTerms" /> (선택) 이용약관에 동의합니다.
        </label>
      </div>

      <div class="actions">
        <button class="btn" :disabled="!canSubmit || loading" @click="onSubmit">
          {{ loading ? '처리 중...' : '회원가입' }}
        </button>
        <button class="btn btn-secondary" :disabled="loading" @click="onCancel">취소</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useToast } from 'vue-toastification'
import { useAuthStore } from '@/stores/auth'
import { authApi } from '@/utils/api'

const router = useRouter()
const toast = useToast()
const auth = useAuthStore()

const ready = ref(false)
const loading = ref(false)
const name = ref('')
const agreePrivacy = ref(false)
const agreeTerms = ref(false)

const profile = computed(() => auth.pendingGoogleSignup?.profile)
const signupToken = computed(() => auth.pendingGoogleSignup?.signupToken)

const canSubmit = computed(() => !!signupToken.value && agreePrivacy.value && name.value.trim().length > 0)

onMounted(() => {
  if (!signupToken.value || !profile.value) {
    toast.error('회원가입 정보가 없습니다. 처음부터 다시 시도해주세요.')
    router.replace('/auth/login')
    return
  }
  name.value = profile.value.name || ''
  ready.value = true
})

async function onSubmit() {
  if (!canSubmit.value) return
  try {
    loading.value = true
    const res = await authApi.signupWithGoogle({
      signupToken: signupToken.value!,
      agreePrivacy: agreePrivacy.value,
      agreeTerms: agreeTerms.value,
      name: name.value.trim()
    })
    // 토큰 저장
    auth.applyAuthResponse(res as any)
    toast.success('회원가입이 완료되었습니다.')
    router.replace('/dashboard')
  } catch (e: any) {
    toast.error(e?.message || '회원가입에 실패했습니다.')
  } finally {
    loading.value = false
  }
}

function onCancel() {
  router.replace('/auth/login')
}
</script>

<style scoped>
.register-google {
  max-width: 560px;
  margin: 2rem auto;
  padding: 1.5rem;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #fff;
}
.title {
  font-size: 1.25rem;
  margin-bottom: 1rem;
}
.loading {
  text-align: center;
}
.profile {
  display: flex;
  gap: 1rem;
  align-items: center;
}
.avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
}
.field {
  margin-bottom: 0.75rem;
}
.field label {
  display: block;
  font-size: 0.9rem;
  color: #555;
  margin-bottom: 0.25rem;
}
.field input {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 6px;
}
.consent {
  margin-top: 1rem;
}
.policy-box {
  border: 1px solid #eee;
  border-radius: 6px;
  padding: 0.75rem;
  margin-bottom: 0.5rem;
  max-height: 140px;
  overflow: auto;
  background: #fafafa;
}
.checkbox {
  display: block;
  margin: 0.5rem 0;
}
.actions {
  display: flex;
  gap: 0.5rem;
  margin-top: 1rem;
}
.btn {
  padding: 0.6rem 1rem;
  border: 1px solid #0ea5e9;
  color: #fff;
  background: #0ea5e9;
  border-radius: 6px;
  cursor: pointer;
}
.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.btn-secondary {
  border-color: #d1d5db;
  background: #f3f4f6;
  color: #111827;
}
</style>
