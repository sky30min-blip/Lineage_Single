@echo off
chcp 65001 >nul
title 리니지 GM 툴
cd /d "%~dp0"
REM GM 툴 메인(index.html)만 연다. 몬스터 스폰 관리 등 모든 메뉴는 메인 화면에서 선택.
start "" "index.html"
exit
