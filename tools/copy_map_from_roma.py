import shutil
import os
from pathlib import Path
from datetime import datetime

_REPO = Path(__file__).resolve().parent.parent
# 소스 경로 (구버전 맵)
source_dir = r"C:\Users\User\Downloads\2.0 로마서버 클라이언트 1\map"

# 타겟 경로 (현재 서버 클라이언트)
_client = _REPO / "3.싱글리니지 클라이언트"
target_dir = str(_client / "map")

# 백업 먼저 (날짜/시간 포함)
backup_name = f"map_backup_{datetime.now().strftime('%Y%m%d_%H%M%S')}"
backup_dir = str(_client / backup_name)

print("=" * 60)
print("리니지 맵 파일 복사 작업 시작")
print("=" * 60)

# 1. 백업
if os.path.exists(target_dir):
    print(f"\n[1단계] 기존 맵 폴더 백업 중...")
    shutil.copytree(target_dir, backup_dir)
    print(f"[OK] 백업 완료: {backup_dir}")
else:
    print(f"\n[1단계] 타겟 폴더가 없어 생성합니다: {target_dir}")
    os.makedirs(target_dir, exist_ok=True)

# 2. 타겟 기존 내용 삭제 후, 소스 맵 폴더 전체 복사 (.map + .s32 등)
print(f"\n[2단계] 타겟 맵 폴더 비우는 중...")
target_path = Path(target_dir)
for item in target_path.iterdir():
    if item.is_file():
        item.unlink()
    else:
        shutil.rmtree(item)
print(f"[OK] 타겟 비움 완료")

print(f"\n[3단계] 로마 맵 폴더 전체 복사 중...")
print(f"소스: {source_dir}")
print(f"타겟: {target_dir}\n")

def copy_all(src: Path, dst: Path):
    """src 폴더 전체를 dst로 복사"""
    count = 0
    for item in src.rglob("*"):
        if item.is_file():
            rel = item.relative_to(src)
            out = dst / rel
            out.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(item, out)
            count += 1
            if count <= 15 or count % 500 == 0:
                print(f"  복사: {rel}")
    return count

copied_count = copy_all(Path(source_dir), Path(target_dir))

print("\n" + "=" * 60)
print(f"[OK] 맵 폴더 복사 완료! (총 {copied_count}개 파일)")
print("=" * 60)
print(f"\n백업 위치: {backup_dir}")
print(f"새 맵 위치: {target_dir}")
