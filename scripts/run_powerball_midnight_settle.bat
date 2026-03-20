@echo off
REM 파워볼 일일 포상 자동 정산 — 작업 스케줄러에서 이 배치를 실행하세요.
cd /d "%~dp0.."
if not exist logs mkdir logs
echo ===== %date% %time% =====>> logs\powerball_midnight.log
where py >nul 2>&1 && (
  py -3 scripts\powerball_midnight_settle.py %*>> logs\powerball_midnight.log 2>&1
  exit /b %ERRORLEVEL%
)
python scripts\powerball_midnight_settle.py %*>> logs\powerball_midnight.log 2>&1
exit /b %ERRORLEVEL%
