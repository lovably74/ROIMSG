/**
 * 인증 관련 타입 정의
 */

export interface User {
  id: string
  tenant_id: string
  google_id?: string
  email: string
  name: string
  profile_image_url?: string
  phone_number?: string
  address?: string
  custom_profile_image_url?: string
  is_active: boolean
  last_login_at?: string
  created_at: string
  updated_at: string
}

export interface LoginRequest {
  email: string
  password: string
  rememberMe?: boolean
}

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
  user: User
}

export interface GoogleProfile {
  email: string
  name: string
  picture?: string
}

export interface GoogleLoginResponse {
  authenticated: boolean
  needSignup?: boolean
  auth?: AuthResponse
  signupToken?: string
  profile?: GoogleProfile
}

export interface RefreshTokenRequest {
  refreshToken: string
}

export interface ApiError {
  error: string
  message: string
  statusCode?: number
}

export interface PaginationParams {
  page?: number
  limit?: number
  sortBy?: string
  sortOrder?: 'asc' | 'desc'
}

export interface PaginatedResponse<T> {
  data: T[]
  total: number
  page: number
  limit: number
  totalPages: number
}
