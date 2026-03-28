@echo off
REM 실제 실행은 PowerShell (start/call/따옴표 버그 회피)
chcp 65001 >nul
cd /d "%~dp0"
powershell.exe -NoLogo -NoProfile -ExecutionPolicy Bypass -File "%~dp0run-gm-tool.ps1"
if errorlevel 1 (
    echo.
    pause
    exit /b 1
)
exit /b 0
