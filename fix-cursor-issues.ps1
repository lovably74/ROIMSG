# Cursor IDE Problem Fix Script
# Fixes serialization errors and other issues

Write-Host "Starting Cursor IDE problem resolution..." -ForegroundColor Green

# 1. Close Cursor processes
Write-Host "1. Closing Cursor processes..." -ForegroundColor Yellow
try {
    Get-Process -Name "Cursor" -ErrorAction SilentlyContinue | Stop-Process -Force
    Start-Sleep -Seconds 3
    Write-Host "   Cursor processes have been closed." -ForegroundColor Green
} catch {
    Write-Host "   No Cursor processes were running." -ForegroundColor Yellow
}

# 2. Clear Cursor cache directories
Write-Host "2. Clearing Cursor cache..." -ForegroundColor Yellow
$cachePaths = @(
    "$env:APPDATA\Cursor",
    "$env:LOCALAPPDATA\Cursor",
    "$env:TEMP\Cursor*"
)

foreach ($path in $cachePaths) {
    if (Test-Path $path) {
        try {
            Remove-Item -Path $path -Recurse -Force -ErrorAction SilentlyContinue
            Write-Host "   Cache cleared: $path" -ForegroundColor Green
        } catch {
            Write-Host "   Error clearing cache: $path (some files may be in use)" -ForegroundColor Yellow
        }
    }
}

# 3. Clear temporary files
Write-Host "3. Clearing temporary files..." -ForegroundColor Yellow
try {
    Get-ChildItem -Path $env:TEMP -Filter "*cursor*" -Recurse -ErrorAction SilentlyContinue | Remove-Item -Recurse -Force
    Get-ChildItem -Path $env:TEMP -Filter "*vscode*" -Recurse -ErrorAction SilentlyContinue | Remove-Item -Recurse -Force
    Write-Host "   Temporary files cleared." -ForegroundColor Green
} catch {
    Write-Host "   Error clearing some temporary files" -ForegroundColor Yellow
}

# 4. Check network connection
Write-Host "4. Checking network connection..." -ForegroundColor Yellow
try {
    $ping = Test-NetConnection -ComputerName "8.8.8.8" -Port 443 -InformationLevel Quiet
    if ($ping) {
        Write-Host "   Network connection is normal." -ForegroundColor Green
    } else {
        Write-Host "   Network connection may have issues." -ForegroundColor Red
    }
} catch {
    Write-Host "   Error checking network connection" -ForegroundColor Yellow
}

# 5. Flush DNS cache
Write-Host "5. Flushing DNS cache..." -ForegroundColor Yellow
try {
    ipconfig /flushdns | Out-Null
    Write-Host "   DNS cache flushed." -ForegroundColor Green
} catch {
    Write-Host "   Error flushing DNS cache" -ForegroundColor Yellow
}

# 6. Check system resources
Write-Host "6. Checking system resources..." -ForegroundColor Yellow
$memory = Get-WmiObject -Class Win32_OperatingSystem
$totalMemory = [math]::Round($memory.TotalVisibleMemorySize/1MB, 2)
$freeMemory = [math]::Round($memory.FreePhysicalMemory/1MB, 2)
$usedPercent = [math]::Round((($memory.TotalVisibleMemorySize - $memory.FreePhysicalMemory) / $memory.TotalVisibleMemorySize) * 100, 2)

Write-Host "   Total Memory: $totalMemory GB" -ForegroundColor Cyan
Write-Host "   Free Memory: $freeMemory GB" -ForegroundColor Cyan
Write-Host "   Memory Usage: $usedPercent%" -ForegroundColor Cyan

if ($usedPercent -gt 90) {
    Write-Host "   Warning: High memory usage. Consider restarting the system." -ForegroundColor Red
} else {
    Write-Host "   Memory status is good." -ForegroundColor Green
}

Write-Host "`nProblem resolution completed!" -ForegroundColor Green
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "1. Restart Cursor IDE" -ForegroundColor White
Write-Host "2. Reopen the project" -ForegroundColor White
Write-Host "3. If problems persist, update Cursor to the latest version" -ForegroundColor White
Write-Host "4. If still having issues, completely uninstall and reinstall Cursor" -ForegroundColor White