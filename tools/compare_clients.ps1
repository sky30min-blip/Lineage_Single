[Console]::OutputEncoding = [System.Text.UTF8Encoding]::new($false)
$OutputEncoding = [System.Text.UTF8Encoding]::new($false)

function Resolve-FirstDir($base, $pattern) {
    $d = Get-ChildItem -LiteralPath $base -Directory -ErrorAction SilentlyContinue | Where-Object { $_.Name -like $pattern } | Select-Object -First 1
    if ($d) { return $d.FullName }
    return $null
}

$clients = @(
    (Resolve-FirstDir 'C:\Users\User\Downloads' '*아크네*클라이언트*1*'),
    (Resolve-FirstDir 'C:\Users\User\Downloads' '*2.0*로마서버*클라이언트*1*'),
    (Resolve-FirstDir 'D:\Lineage_Single' '3.*클라이언트*'),
    (Resolve-FirstDir 'D:\' 'zena2.0*')
)

$reportLines = New-Object System.Collections.Generic.List[string]
function Add-ReportLine([string]$line) { $script:reportLines.Add($line) }

Write-Host ('=' * 80)
Write-Host '리니지 클라이언트 비교 분석'
Write-Host ('=' * 80)
Write-Host ''
Add-ReportLine ('=' * 80)
Add-ReportLine '리니지 클라이언트 비교 분석'
Add-ReportLine ('=' * 80)
Add-ReportLine ''

foreach ($client in $clients) {
    if ([string]::IsNullOrWhiteSpace($client)) {
        Write-Host '[미탐지 대상]' -ForegroundColor Red
        Write-Host '  경로 없음' -ForegroundColor Red
        Write-Host ''
        Add-ReportLine '[미탐지 대상]'
        Add-ReportLine '  경로 없음'
        Add-ReportLine ''
        continue
    }

    $name = Split-Path -Leaf $client
    Write-Host "[$name]" -ForegroundColor Cyan
    Add-ReportLine "[$name]"

    if (-not (Test-Path -LiteralPath $client)) {
        Write-Host '  경로 없음' -ForegroundColor Red
        Write-Host ''
        Add-ReportLine '  경로 없음'
        Add-ReportLine ''
        continue
    }

    $linbin = Join-Path $client 'Lin.bin.exe'
    if (Test-Path -LiteralPath $linbin) {
        $fileInfo = Get-Item -LiteralPath $linbin
        $version = [System.Diagnostics.FileVersionInfo]::GetVersionInfo($linbin)
        Write-Host '  Lin.bin.exe:'
        Write-Host "    - 크기: $([math]::Round($fileInfo.Length / 1MB, 2)) MB"
        Write-Host "    - 수정일: $($fileInfo.LastWriteTime)"
        Write-Host "    - 버전: $($version.FileVersion)"
        Add-ReportLine '  Lin.bin.exe:'
        Add-ReportLine "    - 크기: $([math]::Round($fileInfo.Length / 1MB, 2)) MB"
        Add-ReportLine "    - 수정일: $($fileInfo.LastWriteTime)"
        Add-ReportLine "    - 버전: $($version.FileVersion)"
    }

    $dlls = @('gg.dll','D3D8.dll','DDraw.dll','d3d9.dll')
    $foundDlls = @()
    foreach ($dll in $dlls) {
        if (Test-Path -LiteralPath (Join-Path $client $dll)) { $foundDlls += $dll }
    }
    if ($foundDlls.Count -gt 0) {
        Write-Host "  특수 DLL: $($foundDlls -join ', ')" -ForegroundColor Yellow
        Add-ReportLine "  특수 DLL: $($foundDlls -join ', ')"
    }

    $sprPath = Join-Path $client 'sprite'
    if (Test-Path -LiteralPath $sprPath) {
        $sprFiles = Get-ChildItem -LiteralPath $sprPath -Filter '*.spr' -ErrorAction SilentlyContinue
        Write-Host "  스프라이트 파일 수: $($sprFiles.Count)개"
        Add-ReportLine "  스프라이트 파일 수: $($sprFiles.Count)개"

        $charSpr = @('PRKNIGHT.spr','ELFKNIGHT.spr')
        foreach ($spr in $charSpr) {
            $sprFile = Join-Path $sprPath $spr
            if (Test-Path -LiteralPath $sprFile) {
                $size = (Get-Item -LiteralPath $sprFile).Length
                Write-Host "    - $spr : $([math]::Round($size / 1KB, 0)) KB"
                Add-ReportLine "    - $spr : $([math]::Round($size / 1KB, 0)) KB"
            }
        }
    }

    $exeFiles = Get-ChildItem -LiteralPath $client -Filter '*.exe' -ErrorAction SilentlyContinue | Where-Object { $_.Name -ne 'Lin.bin.exe' }
    if ($exeFiles.Count -gt 0) {
        Write-Host "  추가 실행파일: $($exeFiles.Name -join ', ')" -ForegroundColor Green
        Add-ReportLine "  추가 실행파일: $($exeFiles.Name -join ', ')"
    }

    Write-Host '  .spr 파일 재귀 검색 중...' -ForegroundColor Gray
    Add-ReportLine '  .spr 파일 재귀 검색 중...'
    $allSpr = Get-ChildItem -LiteralPath $client -Filter '*.spr' -Recurse -ErrorAction SilentlyContinue
    if ($allSpr.Count -gt 0) {
        Write-Host "  전체 .spr 파일: $($allSpr.Count)개 발견" -ForegroundColor Yellow
        Add-ReportLine "  전체 .spr 파일: $($allSpr.Count)개 발견"

        $motionFiles = @('PRKNIGHT','ELFKNIGHT','KNIGHT','PRINCE','ELF','WIZARD')
        foreach ($motionName in $motionFiles) {
            $found = $allSpr | Where-Object { $_.Name -like "*$motionName*.spr" }
            if ($found) {
                foreach ($f in $found) {
                    Write-Host "    - $($f.Name): $([math]::Round($f.Length / 1KB, 0)) KB" -ForegroundColor Cyan
                    Add-ReportLine "    - $($f.Name): $([math]::Round($f.Length / 1KB, 0)) KB"
                }
            }
        }
    }

    $pakFiles = Get-ChildItem -LiteralPath $client -Filter '*.pak' -ErrorAction SilentlyContinue
    if ($pakFiles.Count -gt 0) {
        Write-Host "  패키지 파일(.pak): $($pakFiles.Count)개"
        Add-ReportLine "  패키지 파일(.pak): $($pakFiles.Count)개"
        foreach ($pak in $pakFiles | Select-Object -First 5) {
            Write-Host "    - $($pak.Name): $([math]::Round($pak.Length / 1MB, 2)) MB"
            Add-ReportLine "    - $($pak.Name): $([math]::Round($pak.Length / 1MB, 2)) MB"
        }
    }

    $dataPath = Join-Path $client 'data'
    if (Test-Path -LiteralPath $dataPath) {
        $dataSize = (Get-ChildItem -LiteralPath $dataPath -Recurse -File -ErrorAction SilentlyContinue | Measure-Object -Property Length -Sum).Sum
        Write-Host "  data 폴더 크기: $([math]::Round($dataSize / 1MB, 0)) MB" -ForegroundColor Magenta
        Add-ReportLine "  data 폴더 크기: $([math]::Round($dataSize / 1MB, 0)) MB"
    }

    Write-Host ''
    Add-ReportLine ''
}

Write-Host ('=' * 80)
Write-Host '분석 완료'
Write-Host ('=' * 80)
Add-ReportLine ('=' * 80)
Add-ReportLine '분석 완료'
Add-ReportLine ('=' * 80)

$outFile = 'D:\Lineage_Single\tools\compare_clients_result.txt'
$reportLines | Set-Content -Path $outFile -Encoding utf8
Write-Host "결과 저장: $outFile" -ForegroundColor Green
