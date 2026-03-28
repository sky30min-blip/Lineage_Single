@echo off
REM Same as 접속_로컬.bat but ASCII filename (double-click if Korean .bat name breaks).
setlocal
set "REPO=%~dp0..\"
cd /d "%REPO%"
set "PSX=%SystemRoot%\System32\WindowsPowerShell\v1.0\powershell.exe"
if not exist "%PSX%" set "PSX=powershell.exe"
"%PSX%" -NoLogo -NoProfile -ExecutionPolicy Bypass -File "%REPO%tools\connect_local.ps1"
if errorlevel 1 ( echo. & echo [ERROR] Exit %ERRORLEVEL% & pause & exit /b %ERRORLEVEL% )
exit /b 0
