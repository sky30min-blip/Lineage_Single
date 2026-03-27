# GM툴 + 서버 파워볼 오버레이를 백업 폴더에서 복사합니다. (이후 Ant 빌드 및 호환 패치 필요할 수 있음)
param(
  [Parameter(Mandatory = $false)]
  [string] $BackupDir = "D:\Lineage_GM_Powerball_backup_20260326_105713"
)

$ErrorActionPreference = "Stop"
$Root = Split-Path $PSScriptRoot -Parent

$Pack = Get-ChildItem $Root -Directory | Where-Object { $_.Name -match '^2\.' } | Select-Object -First 1
if (-not $Pack) { throw "2.싱글리니지 팩 폴더를 찾을 수 없습니다: $Root" }

if (-not (Test-Path $BackupDir)) { throw "백업 없음: $BackupDir" }

Write-Host "Backup: $BackupDir"
Write-Host "Pack:   $($Pack.FullName)"

Copy-Item -Path (Join-Path $BackupDir "server_overlay\src\*") -Destination (Join-Path $Pack.FullName "src") -Recurse -Force
Copy-Item -Path (Join-Path $BackupDir "server_overlay\db") -Destination (Join-Path $Pack.FullName "db\gm_powerball") -Recurse -Force
$GmDest = Join-Path $Root "gm_tool"
if (Test-Path $GmDest) { Remove-Item $GmDest -Recurse -Force }
Copy-Item -Path (Join-Path $BackupDir "gm_tool") -Destination $GmDest -Recurse -Force

$cfg = Join-Path $GmDest "config.py"
if (Test-Path $cfg) {
  (Get-Content $cfg -Raw -Encoding UTF8) -replace "'database':\s*'l1jdb'", "'database': 'lin200'" | Set-Content $cfg -Encoding UTF8 -NoNewline
}

Write-Host "복사 완료."
Write-Host "Ant 빌드: Set-Location '$($Pack.FullName)'; & 'D:\Lineage_Single\_tools\apache-ant-1.10.14\bin\ant.bat' -noinput all"
