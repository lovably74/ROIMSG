<template>
  <v-app>
    <!-- 로딩 스피너 -->
    <v-overlay
      v-model="loading"
      class="align-center justify-center"
      persistent
    >
      <v-progress-circular
        color="primary"
        indeterminate
        size="64"
      />
    </v-overlay>

    <!-- 메인 레이아웃 -->
    <v-app-bar
      v-if="!isAuthPage"
      app
      color="primary"
      dark
      elevation="2"
    >
      <v-app-bar-nav-icon
        @click="drawer = !drawer"
        class="d-md-none"
      />
      
      <v-toolbar-title class="text-h6 font-weight-bold">
        ROIMSG
      </v-toolbar-title>

      <v-spacer />

      <!-- 사용자 메뉴 -->
      <v-menu
        v-if="isAuthenticated"
        offset-y
        left
      >
        <template #activator="{ props }">
          <v-btn
            icon
            v-bind="props"
            class="mr-2"
          >
            <v-avatar size="32">
              <v-img
                v-if="user?.profileImageUrl"
                :src="user.profileImageUrl"
                :alt="user.name"
              />
              <v-icon v-else>mdi-account</v-icon>
            </v-avatar>
          </v-btn>
        </template>

        <v-list>
          <v-list-item>
            <v-list-item-title>{{ user?.name }}</v-list-item-title>
            <v-list-item-subtitle>{{ user?.email }}</v-list-item-subtitle>
          </v-list-item>
          <v-divider />
          <v-list-item @click="goToProfile" prepend-icon="mdi-account">
            <v-list-item-title>프로필</v-list-item-title>
          </v-list-item>
          <v-list-item @click="goToSettings" prepend-icon="mdi-cog">
            <v-list-item-title>설정</v-list-item-title>
          </v-list-item>
          <v-divider />
          <v-list-item @click="logout" prepend-icon="mdi-logout">
            <v-list-item-title>로그아웃</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>

      <!-- 언어 선택 -->
      <v-menu offset-y>
        <template #activator="{ props }">
          <v-btn
            icon
            v-bind="props"
            class="mr-2"
          >
            <v-icon>mdi-translate</v-icon>
          </v-btn>
        </template>
        <v-list>
          <v-list-item @click="changeLanguage('ko')">
            <v-list-item-title>한국어</v-list-item-title>
          </v-list-item>
          <v-list-item @click="changeLanguage('en')">
            <v-list-item-title>English</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
    </v-app-bar>

    <!-- 네비게이션 드로어 -->
    <v-navigation-drawer
      v-if="!isAuthPage"
      v-model="drawer"
      app
      :permanent="mdAndUp"
      :temporary="smAndDown"
      width="280"
    >
      <v-list nav>
        <v-list-item
          v-for="item in navigationItems"
          :key="item.title"
          :to="item.to"
          :prepend-icon="item.icon"
          :title="item.title"
          :subtitle="item.subtitle"
          class="mb-1"
        />
      </v-list>

      <template #append>
        <v-divider />
        <v-list>
          <v-list-item
            prepend-icon="mdi-information"
            title="버전 정보"
            subtitle="v1.0.0"
          />
        </v-list>
      </template>
    </v-navigation-drawer>

    <!-- 메인 컨텐츠 -->
    <v-main>
      <router-view />
    </v-main>

    <!-- 푸터 -->
    <v-footer
      v-if="!isAuthPage"
      app
      color="grey-lighten-5"
      class="text-center"
    >
      <v-row justify="center" no-gutters>
        <v-col
          v-for="link in footerLinks"
          :key="link.text"
          class="text-center mt-4 mb-4"
          cols="auto"
        >
          <v-btn
            :href="link.href"
            color="grey"
            variant="text"
            class="mx-2"
            target="_blank"
            rel="noopener noreferrer"
          >
            {{ link.text }}
          </v-btn>
        </v-col>
        <v-col cols="12">
          <div>
            {{ new Date().getFullYear() }} — <strong>ROIMSG</strong>
          </div>
        </v-col>
      </v-row>
    </v-footer>
  </v-app>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'
import { useDisplay } from 'vuetify'

const route = useRoute()
const router = useRouter()
const { locale } = useI18n()
const { mdAndUp, smAndDown } = useDisplay()
const authStore = useAuthStore()

// 반응형 데이터
const drawer = ref(mdAndUp.value)
const loading = ref(false)

// 계산된 속성
const isAuthenticated = computed(() => authStore.isAuthenticated)
const user = computed(() => authStore.user)
const isAuthPage = computed(() => route.path.startsWith('/auth'))

// 네비게이션 아이템
const navigationItems = computed(() => [
  {
    title: '대시보드',
    subtitle: 'Dashboard',
    icon: 'mdi-view-dashboard',
    to: '/dashboard'
  },
  {
    title: '메시지',
    subtitle: 'Messages',
    icon: 'mdi-message',
    to: '/messages'
  },
  {
    title: '게시판',
    subtitle: 'Board',
    icon: 'mdi-bulletin-board',
    to: '/board'
  },
  {
    title: '자료실',
    subtitle: 'Files',
    icon: 'mdi-folder',
    to: '/files'
  },
  {
    title: '사용자',
    subtitle: 'Users',
    icon: 'mdi-account-group',
    to: '/users'
  }
])

// 푸터 링크
const footerLinks = [
  { text: '개인정보처리방침', href: '/privacy' },
  { text: '이용약관', href: '/terms' },
  { text: '지원', href: '/support' },
  { text: 'GitHub', href: 'https://github.com/lovably74/ROIMSG' }
]

// 메서드
const goToProfile = () => {
  router.push('/profile')
}

const goToSettings = () => {
  router.push('/settings')
}

const logout = async () => {
  try {
    loading.value = true
    await authStore.logout()
    router.push('/auth/login')
  } catch (error) {
    console.error('로그아웃 실패:', error)
  } finally {
    loading.value = false
  }
}

const changeLanguage = (lang: string) => {
  locale.value = lang
  localStorage.setItem('locale', lang)
}

// 화면 크기 변경 감지
watch(mdAndUp, (newValue) => {
  drawer.value = newValue
})

// 라우트 변경 감지
watch(route, () => {
  if (!mdAndUp.value) {
    drawer.value = false
  }
})
</script>

<style scoped>
.v-navigation-drawer {
  border-right: 1px solid rgba(0, 0, 0, 0.12);
}

.v-app-bar {
  border-bottom: 1px solid rgba(0, 0, 0, 0.12);
}

.v-footer {
  border-top: 1px solid rgba(0, 0, 0, 0.12);
}
</style>
