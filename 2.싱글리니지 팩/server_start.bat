@echo off
cd /d "%~dp0"
for %%F in (*.bat) do (
  if /i not "%%~nxF"=="server_start.bat" (
    findstr /m /c:"server.jar lineage.Main" "%%F" >nul 2>&1
    if not errorlevel 1 (
      call "%%F"
      exit /b 0
    )
  )
)
echo [ERROR] Server start script not found.
pause
exit /b 1
