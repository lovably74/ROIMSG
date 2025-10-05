import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios'
import { useAuthStore } from '@/stores/auth'
import { useToast } from 'vue-toastification'
import type { LoginRequest, AuthResponse, RefreshTokenRequest, User, GoogleLoginResponse } from '@/types/auth'

// API 기본 설정
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

// Axios 인스턴스 생성
const api: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 요청 인터셉터
api.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    
    // 액세스 토큰이 있으면 헤더에 추가
    if (authStore.accessToken) {
      config.headers.Authorization = `Bearer ${authStore.accessToken}`
    }

    // 테넌트 ID 헤더 추가
    if (authStore.user?.tenant_id) {
      config.headers['X-Tenant-Id'] = authStore.user.tenant_id
    }

    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 응답 인터셉터
api.interceptors.response.use(
  (response: AxiosResponse) => {
    return response
  },
  async (error) => {
    const authStore = useAuthStore()
    const toast = useToast()

    // 401 에러 처리 (토큰 만료)
    if (error.response?.status === 401) {
      try {
        // 리프레시 토큰으로 액세스 토큰 갱신
        await authStore.refreshAccessToken()
        
        // 원래 요청 재시도
        const originalRequest = error.config
        originalRequest.headers.Authorization = `Bearer ${authStore.accessToken}`
        return api(originalRequest)
      } catch (refreshError) {
        // 리프레시 실패 시 로그아웃
        authStore.clearAuth()
        toast.error('세션이 만료되었습니다. 다시 로그인해주세요.')
        window.location.href = '/auth/login'
      }
    }

    // 403 에러 처리 (권한 없음)
    if (error.response?.status === 403) {
      toast.error('접근 권한이 없습니다.')
    }

    // 500 에러 처리 (서버 오류)
    if (error.response?.status >= 500) {
      toast.error('서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.')
    }

    return Promise.reject(error)
  }
)

// 인증 API
export const authApi = {
  // 로그인
  login: async (credentials: LoginRequest): Promise<AuthResponse> => {
    const response = await api.post('/api/auth/login', credentials)
    const d = response.data
    return {
      accessToken: d.access_token,
      refreshToken: d.refresh_token,
      tokenType: d.token_type || 'Bearer',
      expiresIn: d.expires_in,
      user: d.user
    }
  },

  // Google OAuth 로그인 (회원 존재 시 토큰, 미존재 시 사전가입 정보 반환)
  loginWithGoogle: async (code: string): Promise<GoogleLoginResponse> => {
    const response = await api.post('/api/auth/login/google', null, {
      params: { code }
    })
    const d = response.data
    if (d.authenticated) {
      const a = d.auth
      return {
        authenticated: true,
        needSignup: false,
        auth: {
          accessToken: a.access_token,
          refreshToken: a.refresh_token,
          tokenType: a.token_type || 'Bearer',
          expiresIn: a.expires_in,
          user: a.user
        }
      }
    }
    return {
      authenticated: false,
      needSignup: true,
      signupToken: d.signupToken,
      profile: d.profile
    }
  },

  // 토큰 갱신
  refreshToken: async (refreshToken: string): Promise<AuthResponse> => {
    const response = await api.post('/api/auth/refresh', {
      refreshToken
    })
    const d = response.data
    return {
      accessToken: d.access_token,
      refreshToken: d.refresh_token,
      tokenType: d.token_type || 'Bearer',
      expiresIn: d.expires_in,
      user: d.user
    }
  },

  // 로그아웃
  logout: async (accessToken: string): Promise<void> => {
    await api.post('/api/auth/logout', null, {
      headers: {
        Authorization: `Bearer ${accessToken}`
      }
    })
  },

  // 현재 사용자 정보 조회
  getCurrentUser: async (accessToken: string): Promise<User> => {
    const response = await api.get('/api/auth/me', {
      headers: {
        Authorization: `Bearer ${accessToken}`
      }
    })
    return response.data
  },

  // 토큰 유효성 검증
  validateToken: async (accessToken: string): Promise<boolean> => {
    try {
      const response = await api.get('/api/auth/validate', {
        headers: {
          Authorization: `Bearer ${accessToken}`
        }
      })
      return response.data
    } catch (error) {
      return false
    }
  },

  // Google 회원가입
  signupWithGoogle: async (payload: { signupToken: string; agreePrivacy: boolean; agreeTerms?: boolean; name?: string }): Promise<AuthResponse> => {
    const response = await api.post('/api/auth/signup/google', payload)
    const d = response.data
    return {
      accessToken: d.access_token,
      refreshToken: d.refresh_token,
      tokenType: d.token_type || 'Bearer',
      expiresIn: d.expires_in,
      user: d.user
    }
  }
}

// 사용자 API
export const userApi = {
  // 사용자 목록 조회
  getUsers: async (params?: any) => {
    const response = await api.get('/api/users', { params })
    return response.data
  },

  // 사용자 상세 조회
  getUser: async (id: string) => {
    const response = await api.get(`/api/users/${id}`)
    return response.data
  },

  // 사용자 정보 수정
  updateUser: async (id: string, data: Partial<User>) => {
    const response = await api.put(`/api/users/${id}`, data)
    return response.data
  },

  // 사용자 삭제
  deleteUser: async (id: string) => {
    await api.delete(`/api/users/${id}`)
  }
}

// 메시지 API
export const messageApi = {
  // 메시지 목록 조회
  getMessages: async (params?: any) => {
    const response = await api.get('/api/messages', { params })
    return response.data
  },

  // 메시지 전송
  sendMessage: async (data: any) => {
    const response = await api.post('/api/messages', data)
    return response.data
  },

  // 메시지 삭제
  deleteMessage: async (id: string) => {
    await api.delete(`/api/messages/${id}`)
  }
}

// 게시판 API
export const boardApi = {
  // 게시글 목록 조회
  getPosts: async (params?: any) => {
    const response = await api.get('/api/boards', { params })
    return response.data
  },

  // 게시글 상세 조회
  getPost: async (id: string) => {
    const response = await api.get(`/api/boards/${id}`)
    return response.data
  },

  // 게시글 작성
  createPost: async (data: any) => {
    const response = await api.post('/api/boards', data)
    return response.data
  },

  // 게시글 수정
  updatePost: async (id: string, data: any) => {
    const response = await api.put(`/api/boards/${id}`, data)
    return response.data
  },

  // 게시글 삭제
  deletePost: async (id: string) => {
    await api.delete(`/api/boards/${id}`)
  }
}

// 파일 API
export const fileApi = {
  // 파일 목록 조회
  getFiles: async (params?: any) => {
    const response = await api.get('/api/files', { params })
    return response.data
  },

  // 파일 업로드
  uploadFile: async (file: File, onProgress?: (progress: number) => void) => {
    const formData = new FormData()
    formData.append('file', file)

    const response = await api.post('/api/files/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      onUploadProgress: (progressEvent) => {
        if (onProgress && progressEvent.total) {
          const progress = Math.round((progressEvent.loaded * 100) / progressEvent.total)
          onProgress(progress)
        }
      }
    })
    return response.data
  },

  // 파일 다운로드
  downloadFile: async (id: string) => {
    const response = await api.get(`/api/files/${id}/download`, {
      responseType: 'blob'
    })
    return response.data
  },

  // 파일 삭제
  deleteFile: async (id: string) => {
    await api.delete(`/api/files/${id}`)
  }
}

export default api
