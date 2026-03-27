# GM 도구: Streamlit + PHP 내장 서버 (배치 start/call 파싱 이슈 회피)
$ErrorActionPreference = 'Stop'

$RepoRoot = if ($PSScriptRoot) { $PSScriptRoot } else { Split-Path -Parent $MyInvocation.MyCommand.Path }
$GmRoot = Join-Path $RepoRoot 'gm_tool'
$PhpExe = 'C:\Users\User\Downloads\php-8.5.1-nts-Win32-vs17-x64\php.exe'
$WebPort = 8765
$StreamPort = 8501

if (-not (Test-Path -LiteralPath (Join-Path $GmRoot 'app.py'))) {
    Write-Host "[ERROR] gm_tool or app.py missing: $GmRoot" -ForegroundColor Red
    exit 1
}
if (-not (Test-Path -LiteralPath $PhpExe)) {
    Write-Host "[ERROR] PHP not found: $PhpExe" -ForegroundColor Red
    Write-Host "        Edit PhpExe at the top of run-gm-tool.ps1 for your PC." -ForegroundColor Yellow
    exit 1
}

$pyLauncher = (Get-Command py -ErrorAction SilentlyContinue).Source
if (-not $pyLauncher) { $pyLauncher = (Get-Command python -ErrorAction SilentlyContinue).Source }
if (-not $pyLauncher) {
    Write-Host "[ERROR] Neither py nor python is on PATH." -ForegroundColor Red
    exit 1
}

$req = Join-Path $GmRoot 'requirements.txt'
if (Test-Path -LiteralPath $req) {
    Write-Host "[0/2] Reset Streamlit + pip install gm_tool/requirements.txt (fix pip vs import mismatch) ..."
    # 이전 설치 잔여물이 남으면 pip 메타는 1.55인데 import 는 1.31 처럼 불일치할 수 있음
    # pip 가 stderr 로 WARNING 을 내서 $ErrorActionPreference Stop 이면 여기서 중단됨
    $oldEap = $ErrorActionPreference
    $ErrorActionPreference = 'Continue'
    try {
        for ($i = 0; $i -lt 10; $i++) {
            & $pyLauncher -m pip uninstall streamlit -y 2>&1 | Out-Null
        }
    } finally {
        $ErrorActionPreference = $oldEap
    }
    $cleanupScript = Join-Path $GmRoot 'scripts\StreamlitSiteCleanup.ps1'
    if (Test-Path -LiteralPath $cleanupScript) {
        Write-Host "      Removing leftover streamlit folders under site-packages..."
        . $cleanupScript
        Invoke-StreamlitSiteCleanup -PythonExe $pyLauncher
    }
    & $pyLauncher -m pip install --no-cache-dir -q -r $req
    Write-Host "      Python: $(& $pyLauncher -c 'import sys; print(sys.executable)')"
    Write-Host ("      Streamlit(pip): " + (& $pyLauncher -c "import importlib.metadata as m; print(m.version('streamlit'))"))
    # 한 줄 검증: Python 문자열은 작은따옴표 (PowerShell 단일인용 안에서 큰따옴표가 깨지는 경우 방지)
    & $pyLauncher -c "import importlib.metadata as m,streamlit as s,sys; mv,iv=m.version('streamlit'),s.__version__; fr=getattr(s,'fragment',None) or getattr(s,'experimental_fragment',None); (print('      [WARN] verify fail: pip=%s import=%s fragment=%s'%(mv,iv,callable(fr))) or sys.exit(1)) if (mv!=iv or not callable(fr)) else (print('      Streamlit(import): %s, st.fragment: OK'%iv) or sys.exit(0))"
    if ($LASTEXITCODE -ne 0) {
        Write-Host "      Fix: run .\gm_tool\scripts\repair_streamlit.ps1 -Python <same python.exe>, stop all Streamlit, then re-run this script." -ForegroundColor Yellow
    }
}

Write-Host "[1/2] Starting Streamlit ($StreamPort) ..."
$stArgs = @(
    '-m', 'streamlit', 'run', 'app.py',
    "--server.port=$StreamPort",
    '--server.headless=true'
)
Start-Process -FilePath $pyLauncher -ArgumentList $stArgs -WorkingDirectory $GmRoot

Write-Host "      Waiting 8s..."
Start-Sleep -Seconds 8

Write-Host "[2/2] Starting PHP built-in server ($WebPort) ..."
Start-Process -FilePath $PhpExe -ArgumentList @(
    '-S', "127.0.0.1:$WebPort",
    '-t', $GmRoot
) -WorkingDirectory $GmRoot

Start-Sleep -Seconds 2
Start-Process "http://127.0.0.1:$WebPort/"

Write-Host ""
Write-Host "GM Tool:   http://127.0.0.1:$WebPort/"
Write-Host "Streamlit: http://127.0.0.1:$StreamPort/"
Write-Host "If the page fails to load, wait 5-10s for Streamlit then retry."
exit 0
