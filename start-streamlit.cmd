@echo off
REM run-gm-tool.bat에서 호출. %1 = 포트 (예: 8501)
setlocal
cd /d "%~dp0"
py -m streamlit run app.py --server.port %~1 --server.headless=true
pause
