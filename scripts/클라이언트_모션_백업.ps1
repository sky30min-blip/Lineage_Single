# ============================================================
# 클라이언트 모션 관련 파일 백업 스크립트
# (아크네 모션 가져오기 전에 반드시 실행 권장)
# ============================================================
# 모션(서있기, 칼질 등)은 클라이언트에만 있습니다.
# 서버 파일은 이 작업과 무관하지만, 서버 전체 백업도 권장합니다.
# ============================================================

$ErrorActionPreference = "Stop"
$BaseDir = "d:\Lineage_Single"
$ClientRoot = Join-Path $BaseDir "3.싱글리니지 클라이언트"
$BackupRoot = Join-Path $BaseDir "3.Single_Client_Backup_Motion"
$Timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$BackupPath = "${BackupRoot}_${Timestamp}"

# 백업할 항목: 모션/스프라이트 관련
$ItemsToBackup = @(
    @{ Name = "Sprite.pak"; Path = "Sprite.pak" },
    @{ Name = "Sprite.idx"; Path = "Sprite.idx" },
    @{ Name = "sprite"; Path = "sprite" }
)

Write-Host "============================================" -ForegroundColor Cyan
Write-Host " 클라이언트 모션 백업 (아크네 모션 작업 전)" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

if (-not (Test-Path $ClientRoot)) {
    Write-Host "오류: 클라이언트 경로를 찾을 수 없습니다. $ClientRoot" -ForegroundColor Red
    exit 1
}

New-Item -ItemType Directory -Path $BackupPath -Force | Out-Null
Write-Host "백업 경로: $BackupPath" -ForegroundColor Yellow
Write-Host ""

foreach ($item in $ItemsToBackup) {
    $src = Join-Path $ClientRoot $item.Path
    $dest = Join-Path $BackupPath $item.Name
    if (Test-Path $src) {
        Write-Host "백업 중: $($item.Path) ..." -ForegroundColor Gray
        if (Test-Path $dest) { Remove-Item $dest -Recurse -Force }
        Copy-Item -Path $src -Destination $dest -Recurse -Force
        Write-Host "  -> 완료" -ForegroundColor Green
    } else {
        Write-Host "건너뜀 (없음): $($item.Path)" -ForegroundColor DarkGray
    }
}

Write-Host ""
Write-Host "백업이 완료되었습니다." -ForegroundColor Green
Write-Host "복원이 필요하면 아래 폴더에서 복사해 덮어쓰세요:" -ForegroundColor White
Write-Host "  $BackupPath" -ForegroundColor White
Write-Host ""
