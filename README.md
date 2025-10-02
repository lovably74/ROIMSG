# ROIMSG - 멀티테넌시 메시징 시스템

## 📋 프로젝트 개요

ROIMSG는 멀티테넌시 단일 DB와 기능 단위 MSA 아키텍처를 기반으로 하는 간단한 메시징 시스템입니다. Google 계정 연동을 통한 간편한 회원가입 및 로그인을 제공하며, PC 및 모바일 웹 환경에 최적화된 반응형 UI를 지원합니다.

## 🎯 주요 기능

### 🔐 인증 및 사용자 관리
- Google OAuth 2.0 기반 간편 로그인
- 멀티테넌시 지원
- 사용자 프로필 관리
- 권한 기반 접근 제어
- KISA 보안 가이드라인 준수

### 💬 메시징 시스템
- 실시간 1:1 채팅
- 그룹 채팅 (1:N)
- 쪽지 시스템
- 파일 첨부 지원
- 실시간 알림

### 📝 게시판
- 게시글 작성/수정/삭제
- 댓글 및 답글 시스템
- 파일 첨부
- 태그 시스템
- 검색 및 필터링

### 📁 자료실
- 파일 업로드/다운로드
- 썸네일 미리보기
- PDF 미리보기
- 대용량 파일 지원
- 일괄 다운로드

### 📊 대시보드
- 사용자 정의 위젯
- 실시간 데이터 표시
- 드래그 앤 드롭 레이아웃

### 🌍 다국어 지원
- 한국어/영어 기본 지원
- 런타임 언어 전환
- JSON 언어팩 기반 동적 로딩
- 지역화 지원 (날짜, 시간, 숫자 형식)

### ♿ 웹 접근성
- KWCAG 2.2 AA 레벨 준수
- 키보드 접근성
- 스크린 리더 지원
- 색상 대비 최적화
- 반응형 디자인

## 🏗️ 기술 스택

### 프론트엔드
- **Vue.js 3** - 프론트엔드 프레임워크
- **Vite** - 빌드 도구
- **TypeScript** - 타입 안전성
- **Pinia** - 상태 관리
- **Vue Router** - 라우팅
- **Vuetify** - UI 컴포넌트 라이브러리
- **Vue I18n** - 다국어 지원
- **Vue A11y** - 접근성 지원

### 백엔드
- **Spring Boot 3** - 백엔드 프레임워크
- **Java 21** - 프로그래밍 언어
- **Spring Security** - 보안
- **Spring Data JPA** - 데이터 접근
- **Spring Cloud Gateway** - API Gateway
- **WebSocket** - 실시간 통신

### 보안 및 인증
- **JWT** - 토큰 기반 인증
- **OAuth 2.0** - Google 계정 연동
- **AES-256** - 데이터 암호화
- **OWASP ZAP** - 보안 스캐닝
- **SonarQube** - 정적 보안 분석

### 데이터베이스
- **PostgreSQL** - 메인 데이터베이스
- **Redis** - 캐시 및 세션 저장소

### 인프라
- **Docker** - 컨테이너화
- **Kubernetes** - 오케스트레이션
- **Nginx** - 리버스 프록시
- **Prometheus** - 모니터링
- **Grafana** - 대시보드

## 📁 프로젝트 구조

```
ROIMSG/
├── backend/                 # 백엔드 서비스들
│   ├── api-gateway/         # API Gateway
│   ├── auth-service/        # 인증 서비스
│   ├── user-service/        # 사용자 관리 서비스
│   ├── message-service/     # 메시징 서비스
│   ├── board-service/       # 게시판 서비스
│   ├── file-service/        # 파일 관리 서비스
│   └── dashboard-service/   # 대시보드 서비스
├── frontend/                # 프론트엔드 애플리케이션들
│   ├── web-app/            # 웹 애플리케이션
│   └── mobile-app/         # 모바일 웹 애플리케이션
├── shared/                  # 공통 라이브러리
│   ├── types/              # 타입 정의
│   ├── utils/              # 유틸리티 함수
│   └── config/             # 공통 설정
├── docs/                    # 문서
│   ├── PRD-v1.1.md         # 제품 요구사항 정의서
│   └── Implementation-Checklist.md  # 구현 체크리스트
├── scripts/                 # 유틸리티 스크립트
├── infrastructure/          # 인프라 설정
│   ├── docker/             # Docker 설정
│   ├── kubernetes/         # K8s 매니페스트
│   └── terraform/          # 인프라 코드
└── package.json            # 루트 패키지 설정
```

## 🚀 빠른 시작

### 사전 요구사항

- **Node.js** 18.0.0 이상
- **Java** 21 이상
- **PostgreSQL** 15 이상
- **Redis** 7.0 이상
- **Git**

### 설치 및 실행

1. **저장소 클론**
   ```bash
   git clone https://github.com/roimsg/roimsg.git
   cd roimsg
   ```

2. **의존성 설치**
   ```bash
   npm install
   ```

3. **데이터베이스 설정**
   ```bash
   # PostgreSQL 데이터베이스 생성
   createdb ROIMSG
   
   # 데이터베이스 설정 스크립트 실행
   npm run setup:db
   ```

4. **환경 변수 설정**
   ```bash
   # .env 파일 생성 및 설정
   cp .env.example .env
   # .env 파일을 편집하여 필요한 설정값 입력
   ```

5. **개발 서버 실행**
   ```bash
   # 모든 서비스 동시 실행
   npm run dev
   
   # 또는 개별 실행
   npm run dev:backend  # 백엔드만
   npm run dev:frontend # 프론트엔드만
   ```

6. **애플리케이션 접속**
   - 웹 애플리케이션: http://localhost:3000
   - API Gateway: http://localhost:8080
   - API 문서: http://localhost:8080/swagger-ui.html

## 🔧 개발 환경 설정

### 로컬 개발 환경 (Windows)

1. **PostgreSQL 설정**
   ```sql
   -- 관리자 계정으로 로그인
   CREATE USER roit WITH PASSWORD 'fhdlxpzm1*';
   CREATE DATABASE ROIMSG OWNER roit;
   GRANT ALL PRIVILEGES ON DATABASE ROIMSG TO roit;
   ```

2. **Google OAuth 설정**
   - [Google Developers Console](https://console.developers.google.com/)에서 프로젝트 생성
   - OAuth 2.0 클라이언트 ID 발급
   - 리다이렉트 URI 설정: `http://localhost:3000/auth/callback`

3. **환경 변수 설정**
   ```env
   # Database
   DB_HOST=localhost
   DB_PORT=5432
   DB_NAME=ROIMSG
   DB_USER=roit
   DB_PASSWORD=fhdlxpzm1*
   
   # Google OAuth
   GOOGLE_CLIENT_ID=your_google_client_id
   GOOGLE_CLIENT_SECRET=your_google_client_secret
   
   # JWT
   JWT_SECRET=your_jwt_secret_key
   JWT_EXPIRATION=86400
   
   # Redis
   REDIS_HOST=localhost
   REDIS_PORT=6379
   ```

## 📚 문서

- [제품 요구사항 정의서 (PRD)](./docs/PRD-v1.1.md)
- [구현 체크리스트](./docs/Implementation-Checklist.md)
- [UI 스타일 가이드](./docs/UI-Style-Guide.md)
- [API 문서](http://localhost:8080/swagger-ui.html)
- [개발 가이드](./docs/development-guide.md)
- [배포 가이드](./docs/deployment-guide.md)

## 🧪 테스트

```bash
# 모든 테스트 실행
npm test

# 백엔드 테스트만
npm run test:backend

# 프론트엔드 테스트만
npm run test:frontend

# E2E 테스트
npm run test:e2e
```

## 🚀 배포

### Docker를 사용한 배포

```bash
# Docker 이미지 빌드
docker-compose build

# 서비스 실행
docker-compose up -d

# 로그 확인
docker-compose logs -f
```

### Kubernetes를 사용한 배포

```bash
# 네임스페이스 생성
kubectl create namespace roimsg

# 매니페스트 적용
kubectl apply -f infrastructure/kubernetes/

# 서비스 상태 확인
kubectl get pods -n roimsg
```

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 [LICENSE](LICENSE) 파일을 참조하세요.

## 📞 지원

- **이슈 리포트**: [GitHub Issues](https://github.com/roimsg/roimsg/issues)
- **문서**: [Wiki](https://github.com/roimsg/roimsg/wiki)
- **이메일**: support@roimsg.com

## 🎯 핵심 특징

1. **멀티테넌시 지원** - 단일 DB에서 테넌트별 데이터 분리
2. **Google OAuth 연동** - 간편한 로그인 및 사용자 관리
3. **실시간 메시징** - WebSocket 기반 실시간 통신
4. **MSA 아키텍처** - 확장 가능한 마이크로서비스 구조
5. **반응형 UI** - PC/모바일 최적화된 사용자 인터페이스
6. **보안 강화** - KISA 보안 가이드라인 및 제로트러스트 준수
7. **웹 접근성** - KWCAG 2.2 AA 레벨 준수
8. **다국어 지원** - 한국어/영어 런타임 전환 지원

## 🗺️ 로드맵

- [ ] **v1.0** - 기본 메시징 기능
- [ ] **v1.1** - 게시판 및 자료실
- [ ] **v1.2** - 대시보드 및 위젯
- [ ] **v1.3** - 보안 강화 및 접근성 개선
- [ ] **v2.0** - 모바일 앱 지원
- [ ] **v2.1** - 고급 검색 기능
- [ ] **v3.0** - AI 기반 기능

---

**ROIMSG Development Team**  
*멀티테넌시 메시징 시스템을 통한 효율적인 커뮤니케이션*
