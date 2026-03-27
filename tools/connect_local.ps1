# Repo root = parent of tools/
$ErrorActionPreference = 'Stop'
$toolsDir = if ($PSScriptRoot) { $PSScriptRoot } else { Split-Path -Parent $MyInvocation.MyCommand.Path }
$repoRoot = Split-Path -Parent $toolsDir

function Find-ClientFolderUnderRepo {
    param([string]$Root)
    if (-not (Test-Path -LiteralPath $Root)) { return $null }
    $exeNames = @('Lin.bin.exe', 'lin.exe', 'local.bin.exe', 'Lin.bin')
    foreach ($d in Get-ChildItem -LiteralPath $Root -Directory -ErrorAction SilentlyContinue) {
        foreach ($n in $exeNames) {
            if (Test-Path -LiteralPath (Join-Path $d.FullName $n)) { return $d.FullName }
        }
    }
    return $null
}

# No non-ASCII in this file: Windows PowerShell 5.1 loads .ps1 as system ANSI unless UTF-8 BOM.
$defaultClientDir = Find-ClientFolderUnderRepo -Root $repoRoot
$clientDir = $null
if ($env:LINEAGE_CLIENT_DIR -and (Test-Path -LiteralPath $env:LINEAGE_CLIENT_DIR)) {
    $clientDir = $env:LINEAGE_CLIENT_DIR.Trim().TrimEnd('\').TrimEnd('/')
} elseif ($defaultClientDir) {
    $clientDir = $defaultClientDir
}
$launcher = Join-Path $toolsDir 'launch_lineage_local.ps1'

if (-not (Test-Path -LiteralPath $launcher)) {
    Write-Host "Missing: $launcher" -ForegroundColor Red
    exit 1
}

if (-not $clientDir) {
    Write-Host "Client folder not found." -ForegroundColor Red
    Write-Host "  Looked under: $repoRoot for a subfolder containing Lin.bin.exe or lin.exe" -ForegroundColor Yellow
    Write-Host "  Or set LINEAGE_CLIENT_DIR to that folder." -ForegroundColor Yellow
    exit 1
}

$launchArgs = @{ ClientDir = $clientDir }
if ($env:LINEAGE_USE_LIN_EXE -eq '1') { $launchArgs.PreferLinExe = $true }
if ($env:LINEAGE_LAUNCH_AGGRESSIVE_COMPAT -eq '1') { $launchArgs.AggressiveCompat = $true }
if ($env:LINEAGE_NO_REGISTRY -eq '1') { $launchArgs.NoRegistryTweaks = $true }
& $launcher @launchArgs
exit $LASTEXITCODE
