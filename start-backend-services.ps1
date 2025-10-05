# ROIMSG 백엔드 서비스 시작 스크립트
Write-Host "ROIMSG 백엔드 서비스들을 시작합니다..." -ForegroundColor Green

# API Gateway 시작
Write-Host "1. API Gateway 시작 중..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd backend/api-gateway; mvn spring-boot:run" -WindowStyle Normal

Start-Sleep -Seconds 3

# Auth Service 시작
Write-Host "2. Auth Service 시작 중..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd backend/auth-service; mvn spring-boot:run" -WindowStyle Normal

Start-Sleep -Seconds 3

# User Service 시작
Write-Host "3. User Service 시작 중..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd backend/user-service; mvn spring-boot:run" -WindowStyle Normal

Start-Sleep -Seconds 3

# Message Service 시작
Write-Host "4. Message Service 시작 중..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd backend/message-service; mvn spring-boot:run" -WindowStyle Normal

Start-Sleep -Seconds 3

# Board Service 시작
Write-Host "5. Board Service 시작 중..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd backend/board-service; mvn spring-boot:run" -WindowStyle Normal

Start-Sleep -Seconds 3

# File Service 시작
Write-Host "6. File Service 시작 중..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd backend/file-service; mvn spring-boot:run" -WindowStyle Normal

Start-Sleep -Seconds 3

# Dashboard Service 시작
Write-Host "7. Dashboard Service 시작 중..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd backend/dashboard-service; mvn spring-boot:run" -WindowStyle Normal

Write-Host "모든 백엔드 서비스가 시작되었습니다!" -ForegroundColor Green
Write-Host "API Gateway: http://localhost:8080" -ForegroundColor Cyan
Write-Host "Swagger UI: http://localhost:8080/swagger-ui.html" -ForegroundColor Cyan
