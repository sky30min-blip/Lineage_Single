# map_backup_20260316_215927\653~656 -> 3.x 클라이언트\map\653~656
$ErrorActionPreference = 'Stop'
$root = 'D:\Lineage_Single'
$client = Get-ChildItem -LiteralPath $root -Directory |
    Where-Object { $_.Name -like '3.*' -and $_.Name -notlike '*Single_Client_Backup_Motion*' } |
    Select-Object -First 1
if (-not $client) { throw '3.x client folder not found under D:\Lineage_Single' }

$backup = Join-Path $client.FullName 'map_backup_20260316_215927'
$map = Join-Path $client.FullName 'map'
if (-not (Test-Path -LiteralPath $map)) {
    New-Item -ItemType Directory -Path $map -Force | Out-Null
}

Write-Host "Client: $($client.FullName)"
Write-Host "Backup: $backup"
Write-Host "Dest map: $map"

foreach ($id in @(653, 654, 655, 656)) {
    $src = Join-Path $backup ([string]$id)
    $dst = Join-Path $map ([string]$id)
    if (-not (Test-Path -LiteralPath $src)) {
        Write-Warning "Missing source: $src"
        continue
    }
    New-Item -ItemType Directory -Path $dst -Force | Out-Null
    & robocopy $src $dst /E /NFL /NDL /NJH /NJS /nc /ns /np
    $code = $LASTEXITCODE
    if ($code -ge 8) {
        throw "robocopy failed for map $id (exit $code)"
    }
    Write-Host "OK map $id -> $dst"
}
Write-Host 'Done.'
