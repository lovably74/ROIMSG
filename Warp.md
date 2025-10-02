# Warp.md — ROIMSG 개발 가이드 (Windows/PowerShell)

이 문서는 Warp(또는 PowerShell)에서 ROIMSG 모노레포를 빠르게 개발/실행/테스트하기 위한 실전 명령 모음입니다. 프론트엔드는 Vue 3 + Vite, 백엔드는 Spring Boot 3(Java 21) 기반의 다수 서비스로 구성되어 있습니다.

프로젝트 루트: H:\\ROIAPP\\ROIMSG

---

## 1) 필수 요구사항
- Node.js >= 18, npm >= 9
- Java 21 (권장: Microsoft OpenJDK, Temurin 등)
- Maven 3.9+ (mvn 명령 사용 가능해야 함)
- PostgreSQL 15+, Redis 7+
- Git

버전 확인(예):
```powershell
node -v
npm -v
java -version
mvn -v
psql --version  # (선택) PostgreSQL CLI 설치 시
redis-server --version  # (선택)
```

---

## 2) 저장소 의존성 설치
루트(package.json, workspaces 있음):
```powershell
# 락파일(package-lock.json)이 있으므로 가능하면 ci 사용
npm ci
# 또는
npm install
```
프론트엔드(web-app) 전용 설치가 필요할 경우:
```powershell
pushd .\frontend\web-app
npm ci
popd
```

---

## 3) 환경 변수(.env) 준비
레포에 .env/.env.example 파일이 없을 수 있습니다. 다음 키들을 참고하여 루트 또는 서비스별 .env를 작성하세요(값은 예시이며 보안상 실제 값은 별도로 관리). 
```env
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=ROIMSG
DB_USER=your_db_user
DB_PASSWORD={{DB_PASSWORD}}

# Google OAuth
GOOGLE_CLIENT_ID={{GOOGLE_CLIENT_ID}}
GOOGLE_CLIENT_SECRET={{GOOGLE_CLIENT_SECRET}}

# JWT
JWT_SECRET={{JWT_SECRET}}
JWT_EXPIRATION=86400

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
```
PowerShell에서 예시 파일 복사(존재하는 경우):
```powershell
Copy-Item .env.example .env -ErrorAction SilentlyContinue
```

---

## 4) 데이터베이스 초기화(선택)
루트 스크립트 존재: scripts\setup-database.js 및 SQL 스크립트(scripts\sql\*.sql)
```powershell
node .\scripts\setup-database.js
```
수동으로 실행하려면 psql 등으로 순서대로 적용하세요:
- scripts\sql\01-create-tables.sql
- scripts\sql\02-create-indexes.sql
- scripts\sql\03-insert-sample-data.sql

---

## 5) 개발 서버 실행(권장 워크플로우)
루트 npm 스크립트:
```jsonc
// package.json (루트)
{
  "scripts": {
    "dev": "concurrently \"npm run dev:backend\" \"npm run dev:frontend\"",
    "dev:frontend": "npm run dev --workspace=frontend/web-app"
  }
}
```
프론트엔드(web-app)는 정상 동작합니다:
```powershell
npm run dev:frontend  # http://localhost:3000
```
백엔드는 Maven(Spring Boot) 기반으로 각 서비스에서 직접 실행하는 것을 권장합니다:
```powershell
# API Gateway
pushd .\backend\api-gateway
mvn spring-boot:run
popd

# Auth Service
pushd .\backend\auth-service
mvn spring-boot:run
popd
```
참고 URL:
- Web App: http://localhost:3000
- API Gateway: http://localhost:8080
- Swagger UI(예): http://localhost:8080/swagger-ui.html

주의: 루트의 "dev:backend" 스크립트는 npm workspace를 가정하지만, backend/* 는 Maven 프로젝트이므로 직접 mvn으로 기동하는 방식을 사용하세요.

---

## 6) 테스트
프론트엔드(web-app):
```powershell
pushd .\frontend\web-app
npm run test           # vitest
npm run test:coverage  # 커버리지
popd
```
백엔드(각 서비스 디렉터리에서):
```powershell
mvn test
```
루트(workspaces 위임) 테스트는 프론트엔드에만 적용될 가능성이 큽니다:
```powershell
npm test
```

---

## 7) 빌드/린트/형식
프론트엔드(web-app):
```powershell
pushd .\frontend\web-app
npm run build     # vite build -> dist
npm run lint      # eslint
npm run format    # prettier (src/)
popd
```
백엔드(각 서비스):
```powershell
mvn package  # 대상 JAR 생성
```
루트 빌드:
```powershell
# 프론트엔드 위주로 동작할 가능성 높음
npm run build
```

---

## 8) 데이터베이스/인프라 관련(참고)
- 현재 레포에서 docker-compose.* 또는 Dockerfile이 확인되지 않았습니다. 컨테이너 기반 실행을 원하시면 별도 정의가 필요합니다.
- Kubernetes 매니페스트/인프라 폴더는 README 설명과 실제 파일이 상이할 수 있으니(폴더 미존재 가능) 필요 시 추가하세요.

---

## 9) 자주 쓰는 Git/Warp 명령
```powershell
# 최신 변경 가져오기
git pull --rebase --autostash

# 새 브랜치 생성 후 작업
git switch -c feature/my-change

# 변경 확인
git status
git diff

# 커밋 및 푸시
git add -A
git commit -m "feat: ..."
git push -u origin feature/my-change
```

---

## 10) 문제 해결(Troubleshooting)
- Node/npm 버전 불일치: nvm-windows 등으로 Node 18 이상 사용 권장.
- Java 21 미설치: JAVA_HOME 설정 및 java -version 확인.
- Maven 인식 안 됨: PATH에 Maven bin 추가 또는 mvnw 사용(래퍼 추가 시).
- 포트 충돌: 3000(웹), 8080(API) 사용; 이미 사용 중이면 설정 변경.
- DB 연결 실패: DB_HOST/PORT/USER/PASSWORD 확인, 방화벽/로컬 서비스 상태 점검.
- Redis 필요 기능: 로컬 Redis 실행 또는 연결 정보 수정.

---

## 11) 레포 구조 요약(발견된 항목)
- README.md (루트)
- package.json (루트, npm workspaces)
- frontend/web-app (Vue 3 + Vite, 스크립트 다수)
- backend/api-gateway (Spring Boot 3, Maven)
- backend/auth-service (Spring Boot 3, Maven)
- scripts/setup-database.js 및 scripts/sql/*.sql

추가 서비스(예: user-service 등)는 README에 명시되어 있으나 현재 파일은 확인되지 않았습니다. 필요 시 생성/연동하세요.

---

문의/개선 제안은 GitHub Issues 또는 팀 채널에 남겨주세요. 즐거운 개발 되세요!
