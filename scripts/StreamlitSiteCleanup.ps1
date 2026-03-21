# pip uninstall 후에도 streamlit 패키지 폴더·메타만 남아 pip≠import 가 되는 경우 강제 삭제.
# Dot-source 후: Invoke-StreamlitSiteCleanup -PythonExe $pyLauncher
function Invoke-StreamlitSiteCleanup {
    param(
        [Parameter(Mandatory = $true)]
        [string] $PythonExe
    )
    $pure = (& $PythonExe -c "import sysconfig; print(sysconfig.get_path('purelib'))").Trim()
    $plat = (& $PythonExe -c "import sysconfig; print(sysconfig.get_path('platlib'))").Trim()
    $roots = @($pure, $plat) | Where-Object { $_ -and $_.Length -gt 0 } | Select-Object -Unique

    foreach ($root in $roots) {
        if (-not (Test-Path -LiteralPath $root)) { continue }
        $pkg = Join-Path $root 'streamlit'
        if (Test-Path -LiteralPath $pkg) {
            Write-Host "  [nuke] Remove-Item: $pkg"
            Remove-Item -LiteralPath $pkg -Recurse -Force -ErrorAction SilentlyContinue
        }
        Get-ChildItem -LiteralPath $root -Directory -ErrorAction SilentlyContinue |
            Where-Object { $_.Name -like 'streamlit-*.dist-info' -or $_.Name -like 'streamlit*.egg-info' } |
            ForEach-Object {
                Write-Host "  [nuke] Remove-Item: $($_.FullName)"
                Remove-Item -LiteralPath $_.FullName -Recurse -Force -ErrorAction SilentlyContinue
            }
    }
}
