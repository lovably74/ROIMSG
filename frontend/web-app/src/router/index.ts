import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// 라우트 정의
const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/auth',
    component: () => import('@/layouts/AuthLayout.vue'),
    children: [
      {
        path: 'login',
        name: 'Login',
        component: () => import('@/views/auth/LoginView.vue'),
        meta: { requiresGuest: true }
      },
      {
        path: 'register',
        name: 'Register',
        component: () => import('@/views/auth/RegisterView.vue'),
        meta: { requiresGuest: true }
      },
      {
        path: 'callback',
        name: 'AuthCallback',
        component: () => import('@/views/auth/CallbackView.vue'),
        meta: { requiresGuest: true }
      }
    ]
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/DashboardView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/messages',
    component: () => import('@/layouts/MessageLayout.vue'),
    children: [
      {
        path: '',
        name: 'Messages',
        component: () => import('@/views/messages/MessageListView.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: ':id',
        name: 'MessageDetail',
        component: () => import('@/views/messages/MessageDetailView.vue'),
        meta: { requiresAuth: true }
      }
    ]
  },
  {
    path: '/board',
    component: () => import('@/layouts/BoardLayout.vue'),
    children: [
      {
        path: '',
        name: 'Board',
        component: () => import('@/views/board/BoardListView.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'create',
        name: 'BoardCreate',
        component: () => import('@/views/board/BoardCreateView.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: ':id',
        name: 'BoardDetail',
        component: () => import('@/views/board/BoardDetailView.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: ':id/edit',
        name: 'BoardEdit',
        component: () => import('@/views/board/BoardEditView.vue'),
        meta: { requiresAuth: true }
      }
    ]
  },
  {
    path: '/files',
    component: () => import('@/layouts/FileLayout.vue'),
    children: [
      {
        path: '',
        name: 'Files',
        component: () => import('@/views/files/FileListView.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'upload',
        name: 'FileUpload',
        component: () => import('@/views/files/FileUploadView.vue'),
        meta: { requiresAuth: true }
      }
    ]
  },
  {
    path: '/users',
    name: 'Users',
    component: () => import('@/views/UsersView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/ProfileView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('@/views/SettingsView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFoundView.vue')
  }
]

// 라우터 생성
const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

// 네비게이션 가드
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()
  
  // 인증 상태 초기화 (토큰이 있는 경우)
  if (!authStore.isAuthenticated && authStore.hasToken()) {
    try {
      await authStore.initializeAuth()
    } catch (error) {
      console.error('인증 초기화 실패:', error)
      authStore.clearAuth()
    }
  }

  // 인증이 필요한 페이지
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/auth/login')
    return
  }

  // 게스트만 접근 가능한 페이지
  if (to.meta.requiresGuest && authStore.isAuthenticated) {
    next('/dashboard')
    return
  }

  next()
})

// 라우트 변경 후 처리
router.afterEach((to, from) => {
  // 페이지 제목 설정
  const title = to.meta.title as string
  if (title) {
    document.title = `${title} - ROIMSG`
  } else {
    document.title = 'ROIMSG - 멀티테넌시 메시징 시스템'
  }

  // 페이지 뷰 추적 (Google Analytics 등)
  if (typeof gtag !== 'undefined') {
    gtag('config', 'GA_MEASUREMENT_ID', {
      page_path: to.fullPath
    })
  }
})

export default router
