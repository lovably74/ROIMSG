import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { createI18n } from 'vue-i18n'
import Toast from 'vue-toastification'
import 'vue-toastification/dist/index.css'

import App from './App.vue'
import router from './router'
import vuetify from './plugins/vuetify'
import { useAuthStore } from './stores/auth'

// 다국어 설정
import ko from './locales/ko.json'
import en from './locales/en.json'

// 스타일 임포트
import './styles/main.scss'

const i18n = createI18n({
  legacy: false,
  locale: 'ko',
  fallbackLocale: 'en',
  messages: {
    ko,
    en
  }
})

const app = createApp(App)
const pinia = createPinia()

// 플러그인 등록
app.use(pinia)
app.use(router)
app.use(vuetify)
app.use(i18n)
app.use(Toast, {
  position: 'top-right',
  timeout: 3000,
  closeOnClick: true,
  pauseOnFocusLoss: true,
  pauseOnHover: true,
  draggable: true,
  draggablePercent: 0.6,
  showCloseButtonOnHover: false,
  hideProgressBar: false,
  closeButton: 'button',
  icon: true,
  rtl: false
})

// 인증 상태 복원
const authStore = useAuthStore()
authStore.initializeAuth()

app.mount('#app')
