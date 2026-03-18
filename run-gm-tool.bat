@echo off
set "PHP_EXE=C:\Users\User\Downloads\php-8.5.1-nts-Win32-vs17-x64\php.exe"
set "PORT=8765"
set "STREAM_PORT=8501"
set "GM_ROOT=%~dp0"
if "%GM_ROOT:~-1%"=="\" set "GM_ROOT=%GM_ROOT:~0,-1%"

if not exist "%PHP_EXE%" (
    echo [ERROR] PHP not found: %PHP_EXE%
    pause
    exit /b 1
)

echo [1/2] Streamlit (계정/아이템지급/NPC 등) 시작 중...
start "GM Streamlit" cmd /c "cd /d %~dp0 && py -m streamlit run app.py --server.port=8501 --server.headless=true & pause"
echo      Streamlit 창이 뜰 때까지 8초 대기...
ping -n 9 127.0.0.1 >nul

echo [2/2] PHP (웹) 시작 중...
start "" "%PHP_EXE%" -S localhost:%PORT% -t "%GM_ROOT%"
ping -n 3 127.0.0.1 >nul
start "" "http://localhost:%PORT%/"
echo.
echo GM Tool: http://localhost:%PORT%/
echo Streamlit: http://localhost:%STREAM_PORT%/ (메뉴 클릭 시 새 탭에서 열림)
echo 연결 안 되면 Streamlit 창이 완전히 뜬 뒤(5~10초) 다시 메뉴 클릭하세요.
ping -n 2 127.0.0.1 >nul
exit
