# 작업 디렉터리 고정 + 전체화면 최적화 비활성
# ($ServerHost 는 일부 환경에서 예약/충돌로 param 기본값 파싱 오류 발생)
param(
    [Parameter(Mandatory = $true)]
    [string] $ClientDir,
    [string] $GameIp = "127.0.0.1",
    [int] $Port = 0,
    # 과거 패치: 레지 16BITCOLOR/640x480/RUNASADMIN 등이 Win10/11에서 Lin.bin 크래시 유발 가능
    [switch] $AggressiveCompat,
    # true면 Lin.bin.exe 대신 lin.exe 우선 (일부 클라에서 안정적)
    [switch] $PreferLinExe,
    # true면 레지스트리/호환 플래그 건드리지 않고 실행만 (문제 분리용)
    [switch] $NoRegistryTweaks
)
$ErrorActionPreference = 'Stop'
$Client = $ClientDir.TrimEnd('\')

function Find-ClientExe {
    param([string] $Root, [bool] $PreferLin)
    $candidates = if ($PreferLin) {
        @('lin.exe', 'Lin.bin.exe', 'local.bin.exe', 'Lin.bin')
    } else {
        @('Lin.bin.exe', 'lin.exe', 'local.bin.exe', 'Lin.bin')
    }
    foreach ($n in $candidates) {
        $p = Join-Path $Root $n
        if (Test-Path -LiteralPath $p) { return $p }
    }
    return $null
}

$Exe = Find-ClientExe -Root $Client -PreferLin:$PreferLinExe
if (-not $Exe) {
    Write-Host "Client exe not found under: $Client" -ForegroundColor Red
    Write-Host "Expected one of: Lin.bin.exe, lin.exe, local.bin.exe, Lin.bin" -ForegroundColor Yellow
    if (Test-Path -LiteralPath $Client) {
        Write-Host "Folder exists. First files (check .gitignore: *.exe may be missing from git clone):" -ForegroundColor Yellow
        Get-ChildItem -LiteralPath $Client -File -ErrorAction SilentlyContinue | Select-Object -First 25 -ExpandProperty Name | ForEach-Object { Write-Host "  $_" }
    } else {
        Write-Host "Folder does not exist." -ForegroundColor Yellow
    }
    Write-Host "Tip: set LINEAGE_USE_LIN_EXE=1 if Lin.bin.exe crashes but lin.exe works." -ForegroundColor Cyan
    exit 1
}

$scriptDir = if ($PSScriptRoot) { $PSScriptRoot } else { Split-Path -Parent $MyInvocation.MyCommand.Path }
$root = if ((Split-Path -Leaf $scriptDir) -eq 'tools') { Split-Path -Parent $scriptDir } else { $scriptDir }
if ($Port -le 0) {
    $Port = 2000
    $conf = $null
    foreach ($d in Get-ChildItem -LiteralPath $root -Directory -ErrorAction SilentlyContinue) {
        $cand = Join-Path $d.FullName 'socket.conf'
        if (Test-Path -LiteralPath $cand) {
            $conf = $cand
            break
        }
    }
    if ($conf) {
        foreach ($line in Get-Content -LiteralPath $conf -ErrorAction SilentlyContinue) {
            if ($line -match '^\s*#') { continue }
            if ($line -match '^\s*[Pp]ort\s*=\s*(\d+)') {
                $Port = [int]$Matches[1]
                break
            }
        }
    }
}

$regPath = "HKCU:\Software\Microsoft\Windows NT\CurrentVersion\AppCompatFlags\Layers"
if ($NoRegistryTweaks) {
    Write-Host "Compat: skipped (-NoRegistryTweaks)"
} elseif ($AggressiveCompat) {
    $env:__COMPAT_LAYER = 'Win7RTM DisableFullscreenOptimizations HighDPIAware'
    $regCompat = "~ WIN7RTM 16BITCOLOR 640X480 DISABLEDXMAXIMIZEDWINDOWEDMODE RUNASADMIN HIGHDPIAWARE"
    if (-not (Test-Path $regPath)) {
        New-Item -Path $regPath -Force | Out-Null
    }
    Set-ItemProperty -LiteralPath $regPath -Name $Exe -Value $regCompat -Type String -Force
    Write-Host "Registry compat (aggressive) set for: $Exe"
} else {
    # 기본: 가벼운 설정만 (Access Violation 완화 목적)
    $env:__COMPAT_LAYER = 'DisableFullscreenOptimizations HighDPIAware'
    try {
        if (Test-Path $regPath) {
            Remove-ItemProperty -LiteralPath $regPath -Name $Exe -ErrorAction SilentlyContinue
        }
    } catch { }
    Write-Host "Compat: light (__COMPAT_LAYER only, no heavy registry flags)"
}
# CMD가 UTF-8이 아닐 때 한글 Write-Host 가 깨지므로 여기서는 ASCII만 사용
Write-Host "Client: $Exe"
Write-Host "Connect: $GameIp  Port: $Port"
Write-Host "Compat: $env:__COMPAT_LAYER"

# Test-NetConnection 은 종종 10초 이상 걸려 접속 배치가 멈춘 것처럼 보임 → 짧은 소켓 검사만 사용
function Test-TcpPortQuick {
    param([string]$TcpHost, [int]$PortNum, [int]$TimeoutMs = 1200)
    $c = New-Object System.Net.Sockets.TcpClient
    try {
        $ar = $c.BeginConnect($TcpHost, $PortNum, $null, $null)
        if (-not $ar.AsyncWaitHandle.WaitOne($TimeoutMs, $false)) { return $false }
        $c.EndConnect($ar)
        return $c.Connected
    } catch {
        return $false
    } finally {
        try { $c.Close() } catch { }
    }
}
try {
    if (-not (Test-TcpPortQuick -TcpHost $GameIp -PortNum $Port)) {
        Write-Host "WARN: TCP $GameIp`:$Port not open. Start the game server and check socket.conf port." -ForegroundColor Yellow
    }
} catch { }

# Start client asynchronously (no wait for exit)
$psi = New-Object System.Diagnostics.ProcessStartInfo
$psi.FileName = $Exe
$psi.Arguments = "$GameIp $Port"
$psi.WorkingDirectory = $Client
$psi.UseShellExecute = $true

Write-Host "Starting client process..."
$p = [System.Diagnostics.Process]::Start($psi)

if ($null -eq $p) {
    Write-Host "Failed to start process" -ForegroundColor Red
    exit 1
}

Write-Host "Client started successfully (PID: $($p.Id))"
exit 0
