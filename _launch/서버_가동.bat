@echo off
chcp 65001 >nul
echo ========================================
echo   호두서버 가동
echo ========================================
echo.

REM 1. Docker MariaDB 실행 확인 및 시작
echo [1/2] DB (Docker MariaDB) 확인 중...
docker ps -a | findstr l1j-db | findstr "Up " >nul 2>&1
if errorlevel 1 (
    echo       l1j-db가 꺼져 있음. 시작합니다...
    docker start l1j-db
    timeout /t 3 /nobreak >nul
) else (
    echo       l1j-db 실행 중입니다.
)
echo.

REM 2. 서버 폴더로 이동 후 서버 실행 (저장소 루트 = 상위 폴더)
echo [2/2] 게임 서버 시작 중...
set "REPO=%~dp0..\"
cd /d "%REPO%"
for /f "delims=" %%D in ('dir /b /ad 2^>nul ^| findstr "^2."') do (
    cd "%%D"
    if exist "서버스타트.bat" goto :run_server
    cd ..
)
echo 오류: 서버 폴더(2.싱글리니지 팩)를 찾을 수 없습니다.
pause
exit /b 1

:run_server
call 서버스타트.bat
pause
