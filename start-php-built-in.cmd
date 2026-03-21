@echo off
REM run-gm-tool.bat에서 호출. %1=php.exe 경로 %2=포트 — 문서 루트는 이 파일이 있는 폴더(gm_tool)
setlocal
cd /d "%~dp0"
if "%~2"=="" (
    echo [ERROR] 인자 부족: php경로 포트
    pause
    exit /b 1
)
"%~1" -S "127.0.0.1:%~2" -t "%CD%"
pause
