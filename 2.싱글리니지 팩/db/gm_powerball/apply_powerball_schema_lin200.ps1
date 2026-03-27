# lin200 DB에 파워볼 스키마 적용 (powerball_bets, powerball_results 등)
# 사용법: 이 폴더에서
#   .\apply_powerball_schema_lin200.ps1
# 비밀번호가 mysql.conf와 다르면:
#   .\apply_powerball_schema_lin200.ps1 -Password "실제비밀번호"
param(
  [string] $DbHost = "127.0.0.1",
  [string] $User = "root",
  [string] $Password = "",
  [string] $Database = "lin200"
)

$ErrorActionPreference = "Stop"
$Here = $PSScriptRoot

function Find-MysqlExe {
  $candidates = @(
    "${env:ProgramFiles}\MariaDB 12.2\bin\mysql.exe",
    "${env:ProgramFiles}\MariaDB 11.4\bin\mysql.exe",
    "${env:ProgramFiles}\MariaDB 10.11\bin\mysql.exe",
    "${env:ProgramFiles}\MySQL\MySQL Server 8.4\bin\mysql.exe",
    "${env:ProgramFiles}\MySQL\MySQL Server 8.0\bin\mysql.exe"
  )
  foreach ($p in $candidates) {
    if (Test-Path -LiteralPath $p) { return $p }
  }
  $found = Get-ChildItem "${env:ProgramFiles}\MariaDB*" -ErrorAction SilentlyContinue |
    ForEach-Object { Join-Path $_.FullName "bin\mysql.exe" } |
    Where-Object { Test-Path -LiteralPath $_ } |
    Select-Object -First 1
  if ($found) { return $found }
  return $null
}

function Read-PasswordFromMysqlConf {
  $packRoot = Split-Path (Split-Path $Here -Parent) -Parent
  $conf = Join-Path $packRoot "mysql.conf"
  if (-not (Test-Path -LiteralPath $conf)) { return $null }
  $lines = Get-Content -LiteralPath $conf -Encoding Default
  foreach ($line in $lines) {
    if ($line -match '^\s*pw\s*=\s*(.+)\s*$') { return $Matches[1].Trim() }
  }
  return $null
}

$mysql = Find-MysqlExe
if (-not $mysql) {
  throw "mysql.exe를 찾을 수 없습니다. MariaDB/MySQL 클라이언트를 설치하거나 PATH에 추가하세요."
}

if ([string]::IsNullOrEmpty($Password)) {
  $pwFromConf = Read-PasswordFromMysqlConf
  if ($null -ne $pwFromConf) { $Password = $pwFromConf }
}

$mysqlArgs = @(
  "-u$User",
  "-p$Password",
  "-h$DbHost",
  "--protocol=TCP",
  "--default-character-set=utf8mb4",
  $Database
)

function Invoke-SqlPipe([string] $relativePath) {
  $path = Join-Path $Here $relativePath
  if (-not (Test-Path -LiteralPath $path)) { throw "파일 없음: $path" }
  Write-Host ">> $relativePath"
  Get-Content -LiteralPath $path -Raw -Encoding UTF8 | & $mysql @mysqlArgs 2>&1
  if ($LASTEXITCODE -ne 0) { throw "SQL 실패 (exit $LASTEXITCODE): $relativePath" }
}

$ordered = @(
  "powerball_tables.sql",
  "powerball_reward_tables.sql",
  "powerball_claimed.sql",
  "powerball_shop.sql",
  "powerball_npc.sql",
  "powerball_display_npc.sql"
)

Write-Host "mysql: $mysql"
Write-Host "대상: ${User}@${DbHost} / $Database"
foreach ($f in $ordered) {
  try {
    Invoke-SqlPipe $f
  } catch {
    if ($f -eq "powerball_claimed.sql") {
      Write-Warning "powerball_claimed.sql 건너뜀 (이미 claimed 컬럼이 있을 수 있음): $_"
    } else {
      throw
    }
  }
}

Write-Host ""
Write-Host "테이블 확인:"
& $mysql "-u$User" "-p$Password" "-h$DbHost" "--protocol=TCP" $Database "-e" "SHOW TABLES LIKE 'powerball_%';" 2>&1
Write-Host "완료."
