import { defineStore } from 'pinia'
import { ref, computed, readonly } from 'vue'
import { useRouter } from 'vue-router'
import { useToast } from 'vue-toastification'
import type { User, LoginRequest, AuthResponse } from '@/types/auth'
import { authApi } from '@/utils/api'

export const useAuthStore = defineStore('auth', () => {
  const router = useRouter()
  const toast = useToast()

  // 상태
  const user = ref<User | null>(null)
  const accessToken = ref<string | null>(localStorage.getItem('access_token'))
  const refreshToken = ref<string | null>(localStorage.getItem('refresh_token'))
  const isLoading = ref(false)

  // Google 사전가입 상태
  const pendingGoogleSignup = ref<{
    signupToken: string
    profile: { email: string; name: string; picture?: string }
  } | null>(null)

  // 계산된 속성
  const isAuthenticated = computed(() => !!user.value && !!accessToken.value)
  const hasToken = computed(() => !!accessToken.value)

  // 액션
  const login = async (credentials: LoginRequest) => {
    try {
      isLoading.value = true
      const response = await authApi.login(credentials)
      applyAuthResponse(response)
      toast.success('로그인에 성공했습니다.')
      return response
    } catch (error: any) {
      toast.error(error.message || '로그인에 실패했습니다.')
      throw error
    } finally {
      isLoading.value = false
    }
  }

  const loginWithGoogle = async (code: string) => {
    try {
      isLoading.value = true
      const result = await authApi.loginWithGoogle(code)

      if (result.authenticated && result.auth) {
        const { auth } = result
        // 토큰 저장
        accessToken.value = auth.accessToken
        refreshToken.value = auth.refreshToken
        user.value = auth.user
        // 로컬 스토리지에 저장
        localStorage.setItem('access_token', auth.accessToken)
        localStorage.setItem('refresh_token', auth.refreshToken)
        localStorage.setItem('user', JSON.stringify(auth.user))
        toast.success('Google 로그인에 성공했습니다.')
        return { authenticated: true }
      }

      if (result.needSignup && result.signupToken && result.profile) {
        pendingGoogleSignup.value = {
          signupToken: result.signupToken,
          profile: result.profile
        }
        return { authenticated: false, needSignup: true }
      }

      throw new Error('알 수 없는 로그인 응답입니다.')
    } catch (error: any) {
      toast.error(error.message || 'Google 로그인에 실패했습니다.')
      throw error
    } finally {
      isLoading.value = false
    }
  }

  const logout = async () => {
    try {
      if (accessToken.value) {
        await authApi.logout(accessToken.value)
      }
    } catch (error) {
      console.error('로그아웃 API 호출 실패:', error)
    } finally {
      // 상태 초기화
      clearAuth()
      toast.success('로그아웃되었습니다.')
    }
  }

  const refreshAccessToken = async () => {
    try {
      if (!refreshToken.value) {
        throw new Error('리프레시 토큰이 없습니다.')
      }

      const response = await authApi.refreshToken(refreshToken.value)
      applyAuthResponse(response)
      return response
    } catch (error) {
      // 리프레시 실패 시 로그아웃
      clearAuth()
      throw error
    }
  }

  const initializeAuth = async () => {
    try {
      // 로컬 스토리지에서 사용자 정보 복원
      const storedUser = localStorage.getItem('user')
      if (storedUser) {
        user.value = JSON.parse(storedUser)
      }

      // 토큰이 있는 경우 사용자 정보 확인
      if (accessToken.value) {
        const userInfo = await authApi.getCurrentUser(accessToken.value)
        user.value = userInfo
        localStorage.setItem('user', JSON.stringify(userInfo))
      }
    } catch (error) {
      console.error('인증 초기화 실패:', error)
      clearAuth()
    }
  }

  const clearAuth = () => {
    user.value = null
    accessToken.value = null
    refreshToken.value = null
    
    // 로컬 스토리지 정리
    localStorage.removeItem('access_token')
    localStorage.removeItem('refresh_token')
    localStorage.removeItem('user')
  }

  const updateUser = (updatedUser: User) => {
    user.value = updatedUser
    localStorage.setItem('user', JSON.stringify(updatedUser))
  }

  const applyAuthResponse = (response: AuthResponse) => {
    accessToken.value = response.accessToken
    refreshToken.value = response.refreshToken
    user.value = response.user
    localStorage.setItem('access_token', response.accessToken)
    localStorage.setItem('refresh_token', response.refreshToken)
    localStorage.setItem('user', JSON.stringify(response.user))
  }

  return {
    // 상태
    user: readonly(user),
    accessToken: readonly(accessToken),
    refreshToken: readonly(refreshToken),
    isLoading: readonly(isLoading),
    pendingGoogleSignup,
    
    // 계산된 속성
    isAuthenticated,
    hasToken,
    
    // 액션
    login,
    loginWithGoogle,
    logout,
    refreshAccessToken,
    initializeAuth,
    clearAuth,
    updateUser,
    applyAuthResponse
  }
})
