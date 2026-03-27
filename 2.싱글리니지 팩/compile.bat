@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"

set "CP="
for %%f in (lib\*.jar) do set "CP=!CP!;%%f"
set "CP=!CP:~1!"

if not exist build mkdir build

echo Building source list...
powershell -NoProfile -Command "$root = (Get-Location).Path + [char]92; Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName.Replace($root, '').Replace([char]92, [char]92) } | Set-Content -Path sources_build.txt -Encoding Default"
if errorlevel 1 (
    echo Source list failed. Using sources.txt...
    copy /y sources.txt sources_build.txt >nul
)

echo Compiling...
"%JAVA_HOME%\bin\javac.exe" -nowarn -encoding UTF-8 -d build -cp "%CP%" -J-Xmx2048m @sources_build.txt 2>nul
if errorlevel 1 (
    echo Build failed.
    exit /b 1
)

echo Creating server.jar...
"%JAVA_HOME%\bin\jar.exe" cfm server.jar src\META-INF\MANIFEST.MF -C build .
echo Done.
exit /b 0
