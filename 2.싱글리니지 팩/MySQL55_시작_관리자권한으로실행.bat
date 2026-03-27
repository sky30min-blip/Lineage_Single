@echo off
chcp 65001 >nul
echo MySQL55 서비스를 시작합니다. (실패하면 이 파일을 "관리자 권한으로 실행" 하세요.)
net start MySQL55
if errorlevel 1 (
  echo.
  echo [실패] 서비스 이름이 다를 수 있습니다. services.msc 에서 MySQL 항목을 확인하세요.
)
echo.
pause
