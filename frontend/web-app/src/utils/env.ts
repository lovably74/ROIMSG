export function getEnv() {
  const env = (import.meta as any).env || {}
  return {
    VITE_GOOGLE_CLIENT_ID: env.VITE_GOOGLE_CLIENT_ID || '',
    VITE_GOOGLE_REDIRECT_URI: env.VITE_GOOGLE_REDIRECT_URI || 'http://localhost:3000/auth/callback',
    VITE_GOOGLE_AUTH_URI: env.VITE_GOOGLE_AUTH_URI || 'https://accounts.google.com/o/oauth2/v2/auth'
  }
}