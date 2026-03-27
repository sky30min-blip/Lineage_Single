# 리니지 서버 빌드 (Ant)
$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot
if (Get-Command ant -ErrorAction SilentlyContinue) {
    ant
} else {
    Write-Host "Ant not in PATH. Trying ant.bat..."
    & "$env:ANT_HOME\bin\ant.bat" 2>$null
    if (-not $?) { Write-Host "Run: ant (from 2.싱글리니지 팩 folder) or set ANT_HOME"; exit 1 }
}
