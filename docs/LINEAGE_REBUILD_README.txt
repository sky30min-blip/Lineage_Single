[리니지 재구축 상태 - 2026-03-26]

1) GM툴 + 파워볼 백업
   D:\Lineage_GM_Powerball_backup_20260326_105713

2) 새 설치본
   이 폴더 = D:\Lineage_Single_NEW (압축 해제 완료)

3) 기존 D:\Lineage_Single 은 Cursor가 잠그면 삭제가 안 됩니다.

   방법 A: Cursor 강제 종료 후 교체
   powershell -NoProfile -ExecutionPolicy Bypass -File "D:\Lineage_Single_NEW\Lineage_Single_finish_swap.ps1" -KillCursor

   방법 B: 재부팅으로 교체 (에디터 안 꺼도 됨)
   powershell -NoProfile -ExecutionPolicy Bypass -File "D:\Lineage_Single_NEW\Lineage_Single_finish_swap.ps1" -DeferToReboot

4) 복원
   새 서버 빌드 후: scripts\apply_gm_powerball_from_backup.ps1
   -BackupDir "D:\Lineage_GM_Powerball_backup_20260326_105713"
