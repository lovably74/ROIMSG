# Cursor Codex 문제 해결 스크립트
Write-Host "Cursor Codex 문제 해결을 시작합니다..." -ForegroundColor Green

# 1. Cursor 프로세스 완전 종료
Write-Host "1. Cursor 프로세스 종료 중..." -ForegroundColor Yellow
Get-Process -Name "Cursor" -ErrorAction SilentlyContinue | Stop-Process -Force
Start-Sleep -Seconds 3

# 2. Cursor 캐시 정리
Write-Host "2. Cursor 캐시 정리 중..." -ForegroundColor Yellow
$cursorCache = "$env:APPDATA\Cursor\CachedData"
if (Test-Path $cursorCache) {
    Remove-Item -Path $cursorCache -Recurse -Force -ErrorAction SilentlyContinue
}

# 3. MCP 서버 재설치
Write-Host "3. MCP 서버 재설치 중..." -ForegroundColor Yellow
npm uninstall -g @upstash/context7-mcp @modelcontextprotocol/server-filesystem
npm install -g @upstash/context7-mcp@latest @modelcontextprotocol/server-filesystem@latest

# 4. 환경 변수 설정
Write-Host "4. 환경 변수 설정 중..." -ForegroundColor Yellow
[Environment]::SetEnvironmentVariable("CURSOR_AI_ENABLED", "true", "User")
[Environment]::SetEnvironmentVariable("CURSOR_CODEX_ENABLED", "true", "User")

# 5. Cursor 재시작
Write-Host "5. Cursor 재시작 중..." -ForegroundColor Yellow
Start-Process "cursor" -ArgumentList "D:\project\ROIMSG"

Write-Host "완료! Cursor가 재시작되었습니다. codex c;o 기능을 테스트해보세요." -ForegroundColor Green
Write-Host "사용 가능한 단축키:" -ForegroundColor Cyan
Write-Host "  - Ctrl + K: AI 채팅 열기" -ForegroundColor White
Write-Host "  - Ctrl + I: 인라인 편집" -ForegroundColor White
Write-Host "  - Ctrl + Shift + P: 명령 팔레트" -ForegroundColor White

