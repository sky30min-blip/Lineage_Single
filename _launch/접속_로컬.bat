@echo off
REM ASCII-only: cmd.exe parses this file as CP949. Korean paths live in tools\connect_local.ps1 (UTF-8).
REM Optional before run:
REM   set LINEAGE_CLIENT_DIR=D:\path\to\client   (override default client folder name)
REM   set LINEAGE_USE_LIN_EXE=1          (use lin.exe first)
REM   set LINEAGE_LAUNCH_AGGRESSIVE_COMPAT=1   (old heavy registry compat)
REM   set LINEAGE_NO_REGISTRY=1          (no compat registry / env tweaks)
setlocal
set "REPO=%~dp0..\"
cd /d "%REPO%"

set "PSX=%SystemRoot%\System32\WindowsPowerShell\v1.0\powershell.exe"
if not exist "%PSX%" set "PSX=powershell.exe"

echo.
echo [Lineage] Running launcher (PowerShell)...
echo   Script: %REPO%tools\connect_local.ps1
echo.

"%PSX%" -NoLogo -NoProfile -ExecutionPolicy Bypass -File "%REPO%tools\connect_local.ps1"
set "EC=%ERRORLEVEL%"

if not "%EC%"=="0" (
    echo.
    echo [ERROR] Exit code %EC%. Read PowerShell lines above.
    echo Check: client folder ^(default: 3.* under repo^) or LINEAGE_CLIENT_DIR, plus Lin.bin.exe or lin.exe
    echo Note: *.exe is gitignored - place client binaries locally after clone.
    pause
    exit /b %EC%
)

echo.
echo [OK] Launcher exit code 0. Game may be behind other windows - check taskbar.
echo      Server must be on (port 2000). Test: powershell Test-NetConnection 127.0.0.1 -Port 2000
echo.
echo Closing in 12 seconds... (key to skip)
timeout /t 12
exit /b 0
