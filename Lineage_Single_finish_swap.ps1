#Requires -Version 5.1
<#
  D:\Lineage_Single -> D:\Lineage_Single_NEW swap.
  .\Lineage_Single_finish_swap.ps1 -KillCursor
  .\Lineage_Single_finish_swap.ps1 -DeferToReboot
#>
param(
    [switch]$KillCursor,
    [switch]$DeferToReboot
)

$ErrorActionPreference = 'Stop'
$old = 'D:\Lineage_Single'
$new = 'D:\Lineage_Single_NEW'

if (-not (Test-Path -LiteralPath $new)) {
    throw "Missing folder: $new"
}

function Register-RebootSwap {
    Add-Type @"
using System;
using System.Runtime.InteropServices;
public class Kernel32Move {
  [DllImport("kernel32.dll", SetLastError=true, CharSet=CharSet.Unicode)]
  public static extern bool MoveFileEx(string lpExistingFileName, string lpNewFileName, int dwFlags);
}
"@
    $DELAY = 4
    if (-not (Test-Path -LiteralPath $old)) {
        throw "Missing old folder: $old"
    }
    $ok1 = [Kernel32Move]::MoveFileEx($old, $null, $DELAY)
    if (-not $ok1) {
        $err = [Runtime.InteropServices.Marshal]::GetLastWin32Error()
        throw "MoveFileEx delete pending failed (Win32 $err). Try Admin PowerShell."
    }
    $ok2 = [Kernel32Move]::MoveFileEx($new, $old, $DELAY)
    if (-not $ok2) {
        $err = [Runtime.InteropServices.Marshal]::GetLastWin32Error()
        throw "MoveFileEx rename pending failed (Win32 $err)."
    }
    Write-Host ""
    Write-Host "OK: After reboot, D:\Lineage_Single will be the new install."
    Write-Host "You can use Cursor now; reboot once."
}

if ($DeferToReboot) {
    Register-RebootSwap
    exit 0
}

if ($KillCursor) {
    Get-Process -Name 'Cursor' -ErrorAction SilentlyContinue | Stop-Process -Force
    Start-Sleep -Seconds 4
}

if (Test-Path -LiteralPath $old) {
    try {
        Remove-Item -LiteralPath $old -Recurse -Force -ErrorAction Stop
    }
    catch {
        Write-Host ""
        Write-Host "[FAIL] D:\Lineage_Single is locked by another program."
        Write-Host "  Option A: Close Cursor fully, then run:"
        Write-Host "    powershell -File `"$PSCommandPath`" -KillCursor"
        Write-Host "  Option B: Defer to reboot:"
        Write-Host "    powershell -File `"$PSCommandPath`" -DeferToReboot"
        Write-Host ""
        throw $_.Exception.Message
    }
}

Rename-Item -LiteralPath $new -NewName 'Lineage_Single'
Write-Host "Done: D:\Lineage_Single is now the new extracted files."