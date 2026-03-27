#Requires -RunAsAdministrator
<#
  MySQL 설치 및 lin200 DB 임포트 스크립트
  관리자 권한 PowerShell에서 실행: .\tools\Install-MySQL-And-Import.ps1
#>

$ErrorActionPreference = "Stop"
$repoRoot = Split-Path -Parent $PSScriptRoot
$sqlPath = Join-Path $repoRoot "2.싱글리니지 서버\db\20260222.sql"
if (-not (Test-Path $sqlPath)) {
    $sqlPath = (Get-ChildItem -LiteralPath $repoRoot -Recurse -Filter "20260222.sql" -ErrorAction SilentlyContinue | Select-Object -First 1).FullName
}
if (-not $sqlPath) { throw "20260222.sql 파일을 찾을 수 없습니다." }

Write-Host "=== STEP 1: Chocolatey 확인 ===" -ForegroundColor Cyan
if (-not (Get-Command choco -ErrorAction SilentlyContinue)) {
    Write-Host "Chocolatey 설치 중..."
    Set-ExecutionPolicy Bypass -Scope Process -Force
    [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
    iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
    $env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")
}
choco --version

Write-Host "`n=== STEP 2: MySQL 설치 ===" -ForegroundColor Cyan
choco install mysql -y
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")

Write-Host "`n=== STEP 3: MySQL 서비스 시작 ===" -ForegroundColor Cyan
Start-Service MySQL
Get-Service MySQL

$mysqlExe = (Get-ChildItem "C:\Program Files\MySQL" -Recurse -Filter "mysql.exe" -ErrorAction SilentlyContinue | Select-Object -First 1).FullName
if (-not $mysqlExe) { $mysqlExe = "mysql" }

Write-Host "`n=== STEP 4: root 비밀번호 설정 (1307) ===" -ForegroundColor Cyan
& $mysqlExe -u root -e "ALTER USER 'root'@'localhost' IDENTIFIED BY '1307'; FLUSH PRIVILEGES;" 2>$null
if ($LASTEXITCODE -ne 0) {
    Write-Host "최초 접속 시 비밀번호 없을 수 있음. 한 번 실행 후 다시 시도하세요." -ForegroundColor Yellow
}

Write-Host "`n=== STEP 5: lin200 DB 생성 및 스크립트 임포트 ===" -ForegroundColor Cyan
& $mysqlExe -u root -p1307 -e "CREATE DATABASE IF NOT EXISTS lin200 CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"
Get-Content $sqlPath -Raw -Encoding UTF8 | & $mysqlExe -u root -p1307 lin200

Write-Host "`n=== STEP 6: 테이블 확인 ===" -ForegroundColor Cyan
$tables = & $mysqlExe -u root -p1307 -e "USE lin200; SHOW TABLES;" 2>$null
$tables
$count = ($tables | Measure-Object -Line).Lines - 1
Write-Host "`n테이블 개수: $count" -ForegroundColor Green
Write-Host "완료." -ForegroundColor Green
