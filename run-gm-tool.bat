@echo off
set "PHP_EXE=C:\Users\User\Downloads\php-8.5.1-nts-Win32-vs17-x64\php.exe"
set "PORT=8765"
set "GM_ROOT=%~dp0"
if "%GM_ROOT:~-1%"=="\" set "GM_ROOT=%GM_ROOT:~0,-1%"

if not exist "%PHP_EXE%" (
    echo [ERROR] PHP not found: %PHP_EXE%
    echo Edit PHP_EXE in this bat file.
    pause
    exit /b 1
)

echo Starting GM Tool server on port %PORT%...
start "" "%PHP_EXE%" -S localhost:%PORT% -t "%GM_ROOT%"
ping -n 3 127.0.0.1 >nul
start "" "http://localhost:%PORT%/"
echo Browser opened. Close the server window to stop.
ping -n 2 127.0.0.1 >nul
exit
