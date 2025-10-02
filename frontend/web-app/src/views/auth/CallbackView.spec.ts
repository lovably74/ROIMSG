import { mount } from '@vue/test-utils'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import CallbackView from './CallbackView.vue'
import { createPinia, setActivePinia } from 'pinia'

vi.mock('vue-toastification', () => ({
  useToast: () => ({ success: vi.fn(), error: vi.fn() })
}))

const pushMock = vi.fn()
const replaceMock = vi.fn()

vi.mock('vue-router', async (orig) => {
  const actual: any = await (orig as any)()
  return {
    ...actual,
    useRoute: () => ({ query: { code: 'test_code' } }),
    useRouter: () => ({ push: pushMock, replace: replaceMock })
  }
})

// Mock authApi via store usage
vi.mock('@/utils/api', () => ({
  authApi: {
    loginWithGoogle: vi.fn(async () => ({
      accessToken: 'at',
      refreshToken: 'rt',
      expiresIn: 3600,
      user: { id: 'u1', tenantId: 't1', email: 'u1@example.com', name: 'U1' }
    }))
  }
}))

// Pinia store uses browser storage; stub it
const storage: Record<string, string> = {}
vi.stubGlobal('localStorage', {
  getItem: (k: string) => storage[k] ?? null,
  setItem: (k: string, v: string) => { storage[k] = v },
  removeItem: (k: string) => { delete storage[k] },
  clear: () => { Object.keys(storage).forEach(k => delete storage[k]) },
  key: (i: number) => Object.keys(storage)[i] ?? null,
  get length() { return Object.keys(storage).length }
} as any)

// Happy path test
describe('CallbackView - success', () => {
  beforeEach(() => {
    pushMock.mockClear()
    replaceMock.mockClear()
    setActivePinia(createPinia())
  })

  it('exchanges code and navigates to /dashboard on success', async () => {
    const wrapper = mount(CallbackView)
    // wait for onMounted promises
    await new Promise((r) => setTimeout(r, 0))
    expect(pushMock).toHaveBeenCalledWith('/dashboard')
    wrapper.unmount()
  })
})

// Error path test
describe('CallbackView - failure', () => {
  beforeEach(() => {
    pushMock.mockClear()
    replaceMock.mockClear()
    setActivePinia(createPinia())
  })

  it('navigates to /auth/login on error', async () => {
    const { authApi } = await import('@/utils/api')
    ;(authApi.loginWithGoogle as any) = vi.fn(async () => { throw new Error('bad') })

    const wrapper = mount(CallbackView)
    await new Promise((r) => setTimeout(r, 0))
    expect(pushMock).toHaveBeenCalledWith('/auth/login')
    wrapper.unmount()
  })
})