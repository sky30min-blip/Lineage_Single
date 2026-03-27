@echo off
chcp 65001 >nul
cd /d "%~dp0"

REM ===== mysql.conf 의 jdbc 포트와 반드시 같게 맞추세요 =====
REM 3306 = Docker/WSL 등 / 3307 = Windows 서비스 MariaDB122
set "PORT=3306"

set "MYSQL=C:\Program Files\MariaDB 12.2\bin\mysql.exe"
if not exist "%MYSQL%" (
  echo [오류] "%MYSQL%" 파일이 없습니다.
  echo MariaDB 설치 경로의 bin\mysql.exe 로 위 set MYSQL= 줄을 수정하세요.
  pause
  exit /b 1
)

set "DB=lin200"
set "DUMP=%~dp0db\20260222.sql"
set "FIX=%~dp0db\fix_ban_word.sql"
REM Docker 등 환경에서 localhost 가 172.17.x 로 잡히며 인증 실패할 수 있어 TCP+127.0.0.1 고정
set "MYSQLOPTS=--protocol=TCP -h 127.0.0.1 -P %PORT%"

if not exist "%DUMP%" (
  echo [오류] 덤프 파일 없음: %DUMP%
  pause
  exit /b 1
)

echo ============================================================
echo  DB: %DB%   호스트: 127.0.0.1   포트: %PORT%
echo  1) 전체 스키마+데이터: db\20260222.sql  (수 분 걸릴 수 있음)
echo  2) 금칙어 테이블 보정: db\fix_ban_word.sql
echo ------------------------------------------------------------
echo  이미 lin200 에 테이블이 있으면 CREATE 가 충돌합니다.
echo  처음부터 다시 넣으려면 이 배치를 다음처럼 실행하세요:
echo    lin200_덤프임포트.bat /fresh
echo  ^(/fresh = lin200 DB 삭제 후 재생성. 캐릭터 등 데이터도 모두 지워짐^)
echo ============================================================
echo.

if /i "%~1"=="/fresh" goto DO_FRESH

echo root 비밀번호 입력 (입력 내용은 표시되지 않음).
echo.
"%MYSQL%" %MYSQLOPTS% -u root -p %DB% < "%DUMP%"
if errorlevel 1 (
  echo.
  echo [실패] Table already exists 등이면 이미 임포트된 상태입니다.
  echo        처음부터 다시:  lin200_덤프임포트.bat /fresh
  echo        그 외: 포트^(%PORT%^)·비밀번호·max_allowed_packet 확인.
  pause
  exit /b 1
)
goto AFTER_DUMP

:DO_FRESH
echo [/fresh] 기존 lin200 DB를 삭제하고 새로 만듭니다. (데이터 전부 삭제)
echo root 비밀번호 입력:
"%MYSQL%" %MYSQLOPTS% -u root -p -e "DROP DATABASE IF EXISTS lin200; CREATE DATABASE lin200 CHARACTER SET utf8 COLLATE utf8_general_ci;"
if errorlevel 1 (
  echo [실패] DB 초기화 오류.
  pause
  exit /b 1
)
echo root 비밀번호 다시 입력 (임포트용):
"%MYSQL%" %MYSQLOPTS% -u root -p %DB% < "%DUMP%"
if errorlevel 1 (
  echo [실패] 임포트 중 오류.
  pause
  exit /b 1
)

:AFTER_DUMP
echo.
echo [1/2] 20260222.sql 완료.
if exist "%FIX%" (
  echo root 비밀번호 입력 (fix_ban_word):
  "%MYSQL%" %MYSQLOPTS% -u root -p %DB% < "%FIX%"
  if errorlevel 1 (
    echo [경고] fix_ban_word.sql 실패. 수동 실행 가능: %FIX%
  ) else (
    echo [2/2] fix_ban_word.sql 완료.
  )
)

echo.
echo 끝. 서버를 다시 켜 보세요.
pause
