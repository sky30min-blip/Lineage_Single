#Requires -RunAsAdministrator
<#
.SYNOPSIS
  파워볼 일일 포상 정산을 매일 KST 00:05에 실행하도록 Windows 작업 스케줄러에 등록합니다.

.DESCRIPTION
  - 실행 파일: run_powerball_midnight_settle.bat (이 스크립트와 같은 폴더)
  - 작업 이름: Lineage_PowerballReward (이미 있으면 /F 로 덮어씀)
  - 관리자 PowerShell에서 실행: 우클릭 → 관리자 권한으로 실행 후:
      cd D:\Lineage_Single\gm_tool\scripts
      .\register_powerball_midnight_task.ps1
#>
$ErrorActionPreference = "Stop"
$here = Split-Path -Parent $MyInvocation.MyCommand.Path
$bat = Join-Path $here "run_powerball_midnight_settle.bat"
if (-not (Test-Path -LiteralPath $bat)) {
    Write-Error "배치 파일이 없습니다: $bat"
}
# schtasks /TR 인자는 내부 따옴표 이스케이프
$tr = '"' + $bat + '"'
$taskName = "Lineage_PowerballReward"
Write-Host "작업 등록: $taskName"
Write-Host "실행: $bat"
Write-Host "시간: 매일 00:05 (로컬 PC 시계 = 이 PC가 한국 시간이면 KST 0시 직후)"
& schtasks /Create /TN $taskName /TR $tr /SC DAILY /ST 00:05 /RL LIMITED /F
if ($LASTEXITCODE -ne 0) {
    Write-Error "schtasks 실패 (exit $LASTEXITCODE). 관리자 권한으로 다시 실행해 보세요."
}
Write-Host "완료. 확인: taskschd.msc 에서 '$taskName' 검색"
Write-Host "로그: gm_tool\logs\powerball_midnight.log (배치 실행 시 append)"
