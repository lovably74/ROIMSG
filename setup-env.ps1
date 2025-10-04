# ROIMSG 환경 설정 스크립트
# PowerShell 스크립트로 환경 변수와 필요한 디렉토리를 설정합니다.

Write-Host "ROIMSG 환경 설정을 시작합니다..." -ForegroundColor Green

# .env 파일 생성
$envContent = @"
# ROIMSG 환경 변수 설정
# 실제 사용 시 필요한 값들을 설정하세요

# ===========================================
# 데이터베이스 설정
# ===========================================
DB_HOST=localhost
DB_PORT=5432
DB_NAME=ROIMSG
DB_USER=roit
DB_PASSWORD=fhdlxpzm1*

# ===========================================
# Google OAuth 2.0 설정
# ===========================================
GOOGLE_CLIENT_ID=your_google_client_id_here
GOOGLE_CLIENT_SECRET=your_google_client_secret_here
GOOGLE_REDIRECT_URI=http://localhost:3000/auth/callback

# ===========================================
# JWT 설정
# ===========================================
JWT_SECRET=roimsg_super_secret_jwt_key_2024_change_this_in_production
JWT_EXPIRATION=86400
JWT_REFRESH_EXPIRATION=604800

# ===========================================
# Redis 설정
# ===========================================
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=
REDIS_DATABASE=0

# ===========================================
# 서버 설정
# ===========================================
NODE_ENV=development
PORT=3000
HOST=0.0.0.0

# API Gateway 설정
API_GATEWAY_PORT=8080
API_GATEWAY_HOST=0.0.0.0

# ===========================================
# 파일 업로드 설정
# ===========================================
MAX_FILE_SIZE=10485760
UPLOAD_PATH=./uploads
ALLOWED_FILE_TYPES=jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx,ppt,pptx,txt

# ===========================================
# 로깅 설정
# ===========================================
LOG_LEVEL=info
LOG_FORMAT=json
LOG_FILE=./logs/roimsg.log

# ===========================================
# 보안 설정
# ===========================================
CORS_ORIGIN=http://localhost:3000
CORS_CREDENTIALS=true
RATE_LIMIT_WINDOW_MS=900000
RATE_LIMIT_MAX_REQUESTS=100

# ===========================================
# 모니터링 설정
# ===========================================
METRICS_ENABLED=true
METRICS_PORT=9090
PROMETHEUS_ENABLED=false

# ===========================================
# 개발 설정
# ===========================================
DEBUG_MODE=true
HOT_RELOAD=true
API_DOCS_ENABLED=true

# ===========================================
# 멀티테넌시 설정
# ===========================================
DEFAULT_TENANT_ID=550e8400-e29b-41d4-a716-446655440000
TENANT_HEADER=X-Tenant-Id

# ===========================================
# WebSocket 설정
# ===========================================
WS_PORT=3001
WS_HOST=0.0.0.0

# ===========================================
# 캐시 설정
# ===========================================
CACHE_TTL=3600
CACHE_PREFIX=roimsg:

# ===========================================
# 세션 설정
# ===========================================
SESSION_SECRET=roimsg_session_secret_2024_change_this_in_production
SESSION_MAX_AGE=86400000
SESSION_SECURE=false
SESSION_HTTP_ONLY=true
SESSION_SAME_SITE=lax

# ===========================================
# 보안 설정
# ===========================================
# 암호화 키 (32자리 이상)
ENCRYPTION_KEY=roimsg_32_char_encryption_key_2024
JWT_REFRESH_SECRET=roimsg_refresh_token_secret_2024

# 보안 헤더
CSP_POLICY=default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'
HSTS_MAX_AGE=31536000
X_FRAME_OPTIONS=DENY
X_CONTENT_TYPE_OPTIONS=nosniff

# 파일 업로드 보안
ANTIVIRUS_ENABLED=false
VIRUS_SCAN_API_URL=
MAX_FILE_SIZE_MB=10
ALLOWED_FILE_EXTENSIONS=jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx,ppt,pptx,txt

# ===========================================
# 접근성 설정
# ===========================================
# 웹 접근성 준수 레벨 (AA 또는 AAA)
ACCESSIBILITY_LEVEL=AA
# 색상 대비 최소 비율
MIN_COLOR_CONTRAST=4.5
# 애니메이션 감소 설정
REDUCE_MOTION=false

# ===========================================
# 다국어 설정
# ===========================================
# 기본 언어
DEFAULT_LANGUAGE=ko
# 지원 언어 목록
SUPPORTED_LANGUAGES=ko,en
# 언어팩 경로
I18N_LOCALE_PATH=./locales
# 날짜/시간 형식
DATE_FORMAT=YYYY-MM-DD
TIME_FORMAT=HH:mm:ss
TIMEZONE=Asia/Seoul

# ===========================================
# 보안 모니터링
# ===========================================
# 보안 이벤트 로깅
SECURITY_LOGGING_ENABLED=true
SECURITY_LOG_LEVEL=warn
# 실시간 위협 탐지
THREAT_DETECTION_ENABLED=false
# 로그인 시도 제한
MAX_LOGIN_ATTEMPTS=5
LOGIN_LOCKOUT_DURATION=900
# 세션 타임아웃 (초)
SESSION_TIMEOUT=3600
# 동시 세션 제한
MAX_CONCURRENT_SESSIONS=3
"@

# .env 파일 생성
$envContent | Out-File -FilePath ".env" -Encoding UTF8
Write-Host ".env 파일이 생성되었습니다." -ForegroundColor Green

# 필요한 디렉토리 생성
$directories = @("uploads", "logs", "temp")
foreach ($dir in $directories) {
    if (!(Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir -Force
        Write-Host "$dir 디렉토리가 생성되었습니다." -ForegroundColor Green
    } else {
        Write-Host "$dir 디렉토리가 이미 존재합니다." -ForegroundColor Yellow
    }
}

Write-Host "환경 설정이 완료되었습니다!" -ForegroundColor Green
Write-Host "Google OAuth 설정을 위해 .env 파일의 GOOGLE_CLIENT_ID와 GOOGLE_CLIENT_SECRET을 설정해주세요." -ForegroundColor Yellow

