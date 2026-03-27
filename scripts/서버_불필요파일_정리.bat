@echo off
chcp 65001 >nul
REM 스크립트 위치: Lineage_Single\scripts → 상위\2.싱글리니지 팩
cd /d "%~dp0"
set "SERVER=%~dp0..\2.싱글리니지 팩"
echo ========================================
echo 서버 불필요 파일 정리 (용량 확보)
echo ========================================
echo.
echo [삭제해도 되는 것]
echo  1. bin\     - IDE 컴파일 결과 (~6MB), 서버는 server.jar만 사용
echo  2. build\   - Ant 빌드 임시 (~5.5MB), 다음 빌드 시 다시 생성
echo  3. log\     - 오래된 로그 (~9MB), 필요시 백업 후 삭제
echo  4. apache-ant-1.10.14\ - Ant 도구 (~44MB), compile.bat만 쓰면 불필요
echo.
set /p choice="bin 폴더 삭제? (y/n): "
if /i "%choice%"=="y" (
    if exist "%SERVER%\bin" (
        rd /s /q "%SERVER%\bin"
        echo bin 삭제 완료.
    )
)
set /p choice="build 폴더 삭제? (y/n): "
if /i "%choice%"=="y" (
    if exist "%SERVER%\build" (
        rd /s /q "%SERVER%\build"
        echo build 삭제 완료.
    )
)
set /p choice="log 폴더 내용 삭제? (y/n): "
if /i "%choice%"=="y" (
    if exist "%SERVER%\log" (
        del /s /q "%SERVER%\log\*.*" 2>nul
        echo log 정리 완료.
    )
)
set /p choice="apache-ant-1.10.14 폴더 삭제? (y/n, Ant로 빌드 안 하면 y): "
if /i "%choice%"=="y" (
    if exist "%SERVER%\apache-ant-1.10.14" (
        rd /s /q "%SERVER%\apache-ant-1.10.14"
        echo apache-ant 삭제 완료.
    )
)
echo.
echo 완료. 자세한 설명은 docs\서버폴더_필수_불필요_정리.md 참고.
pause
