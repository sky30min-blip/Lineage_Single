@echo off
chcp 65001 >nul
echo ============================================================
echo  MariaDB (Windows 설치본) 서비스 등록 및 시작
echo  - 반드시 "관리자 권한으로 실행" 하세요.
echo  - Docker 등이 3306을 쓰는 경우, my.ini 포트는 3307 로 맞춰 두었습니다.
echo    (리니지 서버 mysql.conf 는 그대로 3306 이면 Docker 쪽 DB에 붙습니다.)
echo ============================================================
set "MYSQLD=C:\Program Files\MariaDB 12.2\bin\mysqld.exe"
set "INI=C:\Program Files\MariaDB 12.2\data\my.ini"
if not exist "%MYSQLD%" (
  echo MariaDB 가 설치되어 있지 않습니다. winget install MariaDB.Server 로 설치하세요.
  pause
  exit /b 1
)
"%MYSQLD%" --install "MariaDB122" --defaults-file="%INI%"
if errorlevel 1 (
  echo 서비스 등록 실패. 이미 등록됐을 수 있습니다. services.msc 에서 MariaDB122 확인.
) else (
  echo 서비스 등록됨.
)
net start MariaDB122
if errorlevel 1 (
  echo 서비스 시작 실패. 로그는 "C:\Program Files\MariaDB 12.2\data\*.err" 를 확인하세요.
) else (
  echo MariaDB122 시작됨. 포트는 my.ini 의 port 값입니다 ^(현재 3307^).
)
echo.
echo 클라이언트 예: "C:\Program Files\MariaDB 12.2\bin\mysql.exe" -u root -h 127.0.0.1 -P 3307 -p
pause
