import { mount } from '@vue/test-utils'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import LoginView from './LoginView.vue'

const assignMock = vi.fn()

vi.stubGlobal('window', Object.assign(window, { location: { assign: assignMock } }))

vi.mock('vue-toastification', () => ({
  useToast: () => ({ success: vi.fn(), error: vi.fn() })
}))

// default mock env
vi.mock('@/utils/env', () => ({
  getEnv: () => ({
    VITE_GOOGLE_CLIENT_ID: 'client123',
    VITE_GOOGLE_REDIRECT_URI: 'http://localhost:3000/auth/callback',
    VITE_GOOGLE_AUTH_URI: 'https://accounts.google.com/o/oauth2/v2/auth'
  })
}))

describe('LoginView - Google button', () => {
  beforeEach(() => {
    assignMock.mockClear()
  })

  it('navigates to Google auth URL when clicked', async () => {
    const wrapper = mount(LoginView)
    await wrapper.find('button').trigger('click')
    expect(assignMock).toHaveBeenCalled()
    const calledUrl = (assignMock.mock.calls[0] as any[])[0] as string
    expect(calledUrl).toContain('client_id=client123')
    expect(calledUrl).toContain('response_type=code')
    expect(calledUrl).toContain('redirect_uri=http')
    wrapper.unmount()
  })
})

// missing client id case
describe('LoginView - missing client id', () => {
  it('shows error when client id is missing', async () => {
    assignMock.mockClear()
    vi.resetModules()
    vi.doMock('@/utils/env', () => ({
      getEnv: () => ({
        VITE_GOOGLE_CLIENT_ID: '',
        VITE_GOOGLE_REDIRECT_URI: 'http://localhost:3000/auth/callback',
        VITE_GOOGLE_AUTH_URI: 'https://accounts.google.com/o/oauth2/v2/auth'
      })
    }))
    const { default: Comp } = await import('./LoginView.vue')
    const wrapper = mount(Comp)
    await wrapper.find('button').trigger('click')
    expect(assignMock).not.toHaveBeenCalled()
    wrapper.unmount()
  })
})
