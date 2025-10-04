/**
 * 인증 관련 타입 정의
 */

export interface User {
  id: string
  tenantId: string
  googleId?: string
  email: string
  name: string
  profileImageUrl?: string
  phoneNumber?: string
  address?: string
  customProfileImageUrl?: string
  isActive: boolean
  lastLoginAt?: string
  createdAt: string
  updatedAt: string
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
