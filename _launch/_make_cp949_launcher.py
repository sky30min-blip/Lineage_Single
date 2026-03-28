# -*- coding: utf-8 -*-
from pathlib import Path

# CP949 + no UTF-8 BOM so cmd parses @echo off on line 1. ASCII only in body.
BAT = """@echo off
chcp 65001 >nul
echo ========================================
echo   Hodu server launch
echo ========================================
echo.

echo [1/2] Checking Docker MariaDB...
docker ps -a | findstr l1j-db | findstr "Up " >nul 2>&1
if errorlevel 1 (
    echo       Starting l1j-db...
    docker start l1j-db
    timeout /t 3 /nobreak >nul
) else (
    echo       l1j-db is running.
)
echo.

echo [2/2] Starting game server...
pushd "%~dp0.."
for /d %%D in ("2.*") do (
    if exist "%%~fD\\server_start.bat" (
        pushd "%%~fD"
        call server_start.bat
        popd
        pause
        popd
        exit /b 0
    )
)
popd
echo ERROR: Server folder 2.x or server_start.bat not found.
pause
exit /b 1
"""


def main() -> None:
    root = Path(__file__).resolve().parent
    out = root / "서버_가동.bat"
    data = BAT.encode("cp949", errors="strict").replace(b"\n", b"\r\n")
    out.write_bytes(data)
    head = out.read_bytes()[:16]
    assert head.startswith(b"@echo off\r\n"), head
    assert not out.read_bytes().startswith(b"\xef\xbb\xbf")
    print("OK", out)


if __name__ == "__main__":
    main()
