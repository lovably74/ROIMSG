# Google OAuth 로그인 문제 해결 가이드

## 🔍 문제 원인

Google 계정 로그인 오류의 주요 원인은 다음과 같습니다:

### 1. 환경 변수 미설정 ⚠️
- `GOOGLE_CLIENT_ID`: Google OAuth 클라이언트 ID 미설정
- `GOOGLE_CLIENT_SECRET`: Google OAuth 클라이언트 시크릿 미설정
- `VITE_GOOGLE_CLIENT_ID`: 프론트엔드용 클라이언트 ID 미설정

### 2. Google Console 설정 미완료
- OAuth 2.0 클라이언트 ID 생성 필요
- 승인된 리디렉션 URI 설정 필요

## 🛠️ 해결 단계

### 1단계: Google Console에서 OAuth 설정

1. [Google Cloud Console](https://console.cloud.google.com/)에 접속
2. 프로젝트 선택 또는 새 프로젝트 생성
3. "API 및 서비스" → "사용자 인증 정보" 이동
4. "+ 사용자 인증 정보 만들기" → "OAuth 2.0 클라이언트 ID" 선택
5. 애플리케이션 유형: "웹 애플리케이션" 선택
6. 승인된 리디렉션 URI 추가:
   - `http://localhost:3000/auth/callback` (로컬 개발용)
   - `https://yourdomain.com/auth/callback` (운영용)

### 2단계: 환경 변수 설정

생성된 `.env` 파일들에 실제 Google OAuth 정보를 입력하세요:

#### 루트 `.env` 파일 (백엔드용):
```env
GOOGLE_CLIENT_ID=YOUR_ACTUAL_GOOGLE_CLIENT_ID
GOOGLE_CLIENT_SECRET=YOUR_ACTUAL_GOOGLE_CLIENT_SECRET
GOOGLE_REDIRECT_URI=http://localhost:3000/auth/callback
```

#### `frontend/web-app/.env` 파일 (프론트엔드용):
```env
VITE_GOOGLE_CLIENT_ID=YOUR_ACTUAL_GOOGLE_CLIENT_ID
VITE_GOOGLE_REDIRECT_URI=http://localhost:3000/auth/callback
```

### 3단계: 애플리케이션 재시작

환경 변수 변경 후 서버들을 재시작하세요:

1. 백엔드 서비스들 재시작
2. 프론트엔드 개발 서버 재시작

## 🔧 추가 개선사항

### 개선된 오류 처리
- 상세한 오류 메시지 제공
- 환경 변수 누락 시 명확한 안내
- Google API 응답 로깅 추가

### 보안 강화
- OAuth 상태 파라미터 검증 (권장)
- CSRF 보호 (권장)
- HTTPS 사용 (운영 환경)

## 🧪 테스트 방법

1. `.env` 파일에 올바른 Google OAuth 정보 입력
2. 애플리케이션 재시작
3. 브라우저에서 `http://localhost:3000/auth/login` 접속
4. "Google 계정으로 로그인" 버튼 클릭
5. Google 인증 페이지에서 로그인 진행
6. `http://localhost:3000/auth/callback`으로 리디렉션 확인

## 🚨 일반적인 문제들

### 1. "redirect_uri_mismatch" 오류
- Google Console의 리디렉션 URI와 코드의 URI가 다름
- 해결: Google Console에서 정확한 URI 추가

### 2. "invalid_client" 오류
- 잘못된 클라이언트 ID 또는 시크릿
- 해결: Google Console에서 정보 재확인

### 3. "unauthorized_client" 오류
- OAuth 동의 화면 설정 필요
- 해결: Google Console에서 OAuth 동의 화면 구성

## 📞 추가 도움

문제가 지속되는 경우:
1. 브라우저 개발자 도구에서 네트워크 탭 확인
2. 백엔드 콘솔 로그 확인
3. Google Console 설정 재검토