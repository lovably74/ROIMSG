# ROIMSG 환경설정 관리 파일
# 이 파일은 프로젝트의 환경설정 상태를 추적합니다.

# Google OAuth 설정 완료: ✅
# - 클라이언트 ID: 386472545089-nms9v0b856h10q9dp380gn4t9oukbtfg.apps.googleusercontent.com
# - 클라이언트 시크릿: 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

# 백엔드 설정 파일들:
# - backend/auth-service/src/main/resources/application.yml ✅ (업데이트 완료)
# - backend/api-gateway/src/main/resources/application.yml ✅ (업데이트 완료)
# - setup-env.ps1 ✅ (업데이트 완료)

# 프론트엔드 설정 파일들:
# - frontend/web-app/.env.example ✅ (업데이트 완료)
# - frontend/web-app/.env.local ⚠️ (사용자가 직접 생성해야 함)

# 데이터베이스 설정:
# - 아직 설정되지 않음 (PostgreSQL 서비스 시작 필요)

# 다음 단계:
# 1. 프론트엔드 .env.local 파일 생성 (아래 내용 참조)
# 2. 데이터베이스 설정 (scripts/setup-database.js 실행)
# 3. 서비스 재시작 및 테스트

# 프론트엔드 .env.local 파일 내용:
# VITE_API_BASE_URL=http://localhost:8080
# VITE_GOOGLE_CLIENT_ID=386472545089-nms9v0b856h10q9dp380gn4t9oukbtfg.apps.googleusercontent.com
# VITE_GOOGLE_REDIRECT_URI=http://localhost:3000/auth/callback
# VITE_GOOGLE_AUTH_URI=https://accounts.google.com/o/oauth2/v2/auth
