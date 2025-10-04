# 구현 완료 기능 정리

## 백엔드

- API Gateway: JWT 인증/테넌트 헤더 전파, Redis 기반 Rate Limiting, 보안 헤더 적용 (`backend/api-gateway/src/main/java/com/roimsg/gateway/filter/JwtAuthenticationFilter.java`, `RateLimitingFilter.java`, `SecurityHeadersFilter.java`, `backend/api-gateway/src/main/java/com/roimsg/gateway/config/RedisConfig.java`)
- 인증 서비스: Google OAuth 코드 교환, 이메일/비밀번호 로그인, 리프레시 토큰 재발급, 로그아웃, 토큰 검증 및 현재 사용자 조회 (`backend/auth-service/src/main/java/com/roimsg/auth/controller/AuthController.java`, `backend/auth-service/src/main/java/com/roimsg/auth/service/AuthService.java`)
- 사용자 서비스: 프로필 조회/수정, 테넌트 경계 검증 (`backend/user-service/src/main/java/com/roimsg/user/controller/UserController.java`, `backend/user-service/src/main/java/com/roimsg/user/repository/UserRepository.java`)
- 메시지 서비스: 1:1 메시지 전송 및 대화 내역 조회 (`backend/message-service/src/main/java/com/roimsg/message/controller/MessageController.java`, `backend/message-service/src/main/java/com/roimsg/message/repository/MessageRepository.java`)
- 게시판 서비스: 게시글 목록/작성, JWT 기반 테넌트 필터링 (`backend/board-service/src/main/java/com/roimsg/board/controller/PostController.java`, `backend/board-service/src/main/java/com/roimsg/board/entity/Post.java`)
- 파일 서비스: 다중 파트 업로드, 유형/용량 검증, 테넌트별 저장소 관리, 안전한 다운로드 응답 (`backend/file-service/src/main/java/com/roimsg/file/controller/FileController.java`, `backend/file-service/src/main/java/com/roimsg/file/entity/FileResource.java`)
- 대시보드 서비스: 사용자/게시글/메시지/파일 집계 요약 API (`backend/dashboard-service/src/main/java/com/roimsg/dashboard/controller/SummaryController.java`)
- 통합 테스트: 핵심 REST 플로우에 대한 통합 테스트 작성 (`backend/*/src/test/java/com/roimsg/*/*.java`)

## 프론트엔드

- Vue 3 SPA 라우팅/레이아웃/가드 구성 및 주요 화면 라우트 정의 (`frontend/web-app/src/router/index.ts`)
- Pinia 인증 스토어: 로그인(이메일/Google), 토큰 갱신, 로그아웃, 사용자 상태 초기화 (`frontend/web-app/src/stores/auth.ts`)
- 인증 전용 뷰: 로그인, 회원가입, OAuth 콜백 화면 (`frontend/web-app/src/views/auth/*.vue`)
- 대시보드/메시지/게시판/파일/사용자/프로필/설정 레이아웃과 뷰 골격 구성 (`frontend/web-app/src/layouts/*.vue`, `frontend/web-app/src/views/**/*.vue`)

## 공유 모듈 및 스크립트

- 공통 TypeScript 타입/유틸 제공 (`shared/src/index.ts`)
- 개발 환경 진단 스크립트: 필수(로컬)·선택(테스트/운영) 의존성 구분 검증 (`scripts/check-environment.ps1`)
- 데이터베이스 초기화 스크립트 및 SQL 시드 (`scripts/setup-database.js`, `scripts/sql/*.sql`)

## 인프라 및 문서

- 환경별 배포 전략 문서화: 로컬(무도커), 테스트(Docker Compose), 운영(Kubernetes/매니지드) 분리 (`infrastructure/README.md`, `infrastructure/test/`, `infrastructure/prod/`)
- Implementation Order/Checklist에 환경별 배포 항목 반영 (`docs/Implementation-Order.md`, `docs/Implementation-Checklist.md`)
