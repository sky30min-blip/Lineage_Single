# Streamlit pip 메타데이터와 실제 import 버전이 다를 때(예: 1.55 기록 / 1.31 로드) 복구용.
# 사용: 저장소 루트에서 .\gm_tool\scripts\repair_streamlit.ps1
#       GM 화면에 나온 python.exe 로 고정하려면:
#       .\gm_tool\scripts\repair_streamlit.ps1 -Python "C:\...\python.exe"
param(
    [string] $Python = ''
)

$ErrorActionPreference = 'Stop'

$ScriptsDir = if ($PSScriptRoot) { $PSScriptRoot } else { Split-Path -Parent $MyInvocation.MyCommand.Path }
$GmRoot = Split-Path -Parent $ScriptsDir
$RepoRoot = Split-Path -Parent $GmRoot
$Req = Join-Path $GmRoot 'requirements.txt'

if ($Python -and (Test-Path -LiteralPath $Python)) {
    $pyLauncher = $Python
} else {
    $pyLauncher = (Get-Command py -ErrorAction SilentlyContinue).Source
    if (-not $pyLauncher) { $pyLauncher = (Get-Command python -ErrorAction SilentlyContinue).Source }
}
if (-not $pyLauncher) {
    Write-Host "[ERROR] py/python not on PATH, or -Python path is invalid." -ForegroundColor Red
    exit 1
}
if (-not (Test-Path -LiteralPath $Req)) {
    Write-Host "[ERROR] requirements.txt not found: $Req" -ForegroundColor Red
    exit 1
}

Write-Host "Python: $(& $pyLauncher -c 'import sys; print(sys.executable)')"
Write-Host "pip uninstall streamlit (repeat)..."
$oldEap = $ErrorActionPreference
$ErrorActionPreference = 'Continue'
try {
    for ($i = 0; $i -lt 10; $i++) {
        & $pyLauncher -m pip uninstall streamlit -y 2>&1 | Out-Null
    }
} finally {
    $ErrorActionPreference = $oldEap
}

$cleanupScript = Join-Path $ScriptsDir 'StreamlitSiteCleanup.ps1'
if (Test-Path -LiteralPath $cleanupScript) {
    Write-Host "Force-delete streamlit package + dist-info under site-packages (if uninstall left files)..."
    . $cleanupScript
    Invoke-StreamlitSiteCleanup -PythonExe $pyLauncher
}

Write-Host "pip install --no-cache-dir -r gm_tool/requirements.txt ..."
& $pyLauncher -m pip install --no-cache-dir -r $Req

Write-Host "Verify pip/import/fragment..."
& $pyLauncher -c "import importlib.metadata as m,streamlit as s,sys; mv,iv=m.version('streamlit'),s.__version__; fr=getattr(s,'fragment',None) or getattr(s,'experimental_fragment',None); print('pip=%s  import=%s  fragment_ok=%s'%(mv,iv,callable(fr))); sys.exit(0 if mv==iv and callable(fr) else 1)"
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Verify failed. Wrong interpreter? Try py -0p and use -Python <full path to python.exe>." -ForegroundColor Red
    exit 1
}
Write-Host "Done. Stop every Streamlit/python server, then run run-gm-tool.ps1 or streamlit run again."
exit 0
