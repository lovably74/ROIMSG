import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import { aliases, mdi } from 'vuetify/iconsets/mdi'
import { ko, en } from 'vuetify/locale'

// 테마 정의
const lightTheme = {
  dark: false,
  colors: {
    primary: '#0ea5e9',      // Ocean Blue Primary
    secondary: '#0284c7',    // Ocean Blue Secondary
    accent: '#38bdf8',       // Ocean Blue Accent
    success: '#10b981',
    warning: '#f59e0b',
    error: '#ef4444',
    info: '#3b82f6',
    background: '#f8fafc',   // Gray-50
    surface: '#ffffff',
    'on-primary': '#ffffff',
    'on-secondary': '#ffffff',
    'on-surface': '#0f172a', // Gray-900
    'on-background': '#0f172a'
  }
}

const darkTheme = {
  dark: true,
  colors: {
    primary: '#38bdf8',      // Ocean Blue Accent (다크모드용)
    secondary: '#0ea5e9',    // Ocean Blue Primary (다크모드용)
    accent: '#0284c7',       // Ocean Blue Secondary (다크모드용)
    success: '#10b981',
    warning: '#f59e0b',
    error: '#ef4444',
    info: '#3b82f6',
    background: '#0f172a',   // Gray-900
    surface: '#1e293b',      // Gray-800
    'on-primary': '#0f172a',
    'on-secondary': '#0f172a',
    'on-surface': '#f8fafc',
    'on-background': '#f8fafc'
  }
}

// Vuetify 인스턴스 생성
export default createVuetify({
  components,
  directives,
  icons: {
    defaultSet: 'mdi',
    aliases,
    sets: {
      mdi
    }
  },
  locale: {
    locale: 'ko',
    fallback: 'en',
    messages: { ko, en }
  },
  theme: {
    defaultTheme: 'light',
    themes: {
      light: lightTheme,
      dark: darkTheme
    },
    variations: {
      colors: ['primary', 'secondary', 'accent'],
      lighten: 5,
      darken: 5
    }
  },
  defaults: {
    VBtn: {
      style: 'text-transform: none;',
      elevation: 2
    },
    VCard: {
      elevation: 2,
      rounded: 'lg'
    },
    VTextField: {
      variant: 'outlined',
      density: 'comfortable'
    },
    VSelect: {
      variant: 'outlined',
      density: 'comfortable'
    },
    VTextarea: {
      variant: 'outlined',
      density: 'comfortable'
    },
    VDialog: {
      maxWidth: '600px'
    },
    VMenu: {
      elevation: 8
    },
    VTooltip: {
      location: 'top'
    },
    VSnackbar: {
      timeout: 3000,
      location: 'top right'
    },
    VAlert: {
      variant: 'tonal'
    },
    VProgressCircular: {
      indeterminate: true
    },
    VProgressLinear: {
      indeterminate: true
    }
  },
  display: {
    mobileBreakpoint: 'sm',
    thresholds: {
      xs: 0,
      sm: 600,
      md: 960,
      lg: 1280,
      xl: 1920
    }
  }
})
