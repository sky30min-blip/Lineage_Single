@echo off
chcp 65001 >nul
set "ROOT=%~dp0..\"
set "CLIENT="

for /d %%D in ("%ROOT%3.*") do set "CLIENT=%%~fD"

if not defined CLIENT (
  echo [오류] "%ROOT% 아래에서 3. 로 시작하는 클라이언트 폴더를 찾지 못했습니다.
  pause
  exit /b 1
)

cd /d "%CLIENT%"
if exist "게임접속.bat" (
  call "게임접속.bat" %*
) else (
  echo [오류] "%CLIENT%\게임접속.bat 이 없습니다.
  pause
  exit /b 1
)
