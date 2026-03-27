from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path
import re
from typing import Dict, Iterable, List, Optional, Sequence, Tuple


RECORD_SIZE = 0x1C
NAME_SIZE = 20


@dataclass(frozen=True)
class PakEntry:
    offset: int
    filename: str
    size: int


def _to_u8_list(data: bytes) -> List[int]:
    return [b & 0xFF for b in data]


def _to_int32_le(data: Sequence[int], idx: int) -> int:
    if idx + 4 > len(data):
        return 0
    return (
        (data[idx] & 0xFF)
        | ((data[idx + 1] & 0xFF) << 8)
        | ((data[idx + 2] & 0xFF) << 16)
        | ((data[idx + 3] & 0xFF) << 24)
    )


def _sub_4032E0(a1: Sequence[int], a2: Sequence[int]) -> List[int]:
    out = [0] * 8
    num2 = 0
    idx = 0
    while num2 < 0x10:
        num3 = a1[idx] & 0xFF
        num4 = num3 >> 4
        num5 = num3 % 0x10
        for i in range(8):
            num7 = num2 * 0x80 + i
            out[i] = out[i] | (a2[num7 + (num4 * 8)] | a2[num7 + ((0x10 + num5) * 8)])
        num2 += 2
        idx += 1
    return [x & 0xFF for x in out]


def _sub_403450(a1: Sequence[int]) -> List[int]:
    return [
        ((a1[3] << 7) | (((a1[0] & 0xF9) | ((a1[0] >> 2) & 6)) >> 1)) & 0xFF,
        ((((a1[0] & 1) | (a1[0] << 2)) << 3) | (((a1[1] >> 2) | (a1[1] & 0x87)) >> 3)) & 0xFF,
        ((a1[2] >> 7) | (((a1[1] & 0x1F) | ((a1[1] & 0xF8) << 2)) << 1)) & 0xFF,
        ((a1[1] << 7) | (((a1[2] & 0xF9) | ((a1[2] >> 2) & 6)) >> 1)) & 0xFF,
        ((((a1[2] & 1) | (a1[2] << 2)) << 3) | (((a1[3] >> 2) | (a1[3] & 0x87)) >> 3)) & 0xFF,
        ((a1[0] >> 7) | (((a1[3] & 0x1F) | ((a1[3] & 0xF8) << 2)) << 1)) & 0xFF,
    ]


def _sub_403520(a1: Sequence[int], map4: Sequence[int]) -> List[int]:
    return [
        map4[((a1[0] & 0xFF) * 0x10) | ((a1[1] & 0xFF) >> 4)] & 0xFF,
        map4[0x1000 + ((a1[2] & 0xFF) | (((a1[1] & 0xFF) % 0x10) * 0x100))] & 0xFF,
        map4[0x2000 + (((a1[3] & 0xFF) * 0x10) | ((a1[4] & 0xFF) >> 4))] & 0xFF,
        map4[0x3000 + ((a1[5] & 0xFF) | (((a1[4] & 0xFF) % 0x10) * 0x100))] & 0xFF,
    ]


def _sub_4035A0(a1: Sequence[int], map3: Sequence[int]) -> List[int]:
    out = [0, 0, 0, 0]
    for i in range(4):
        idx = ((i * 0x100) + (a1[i] & 0xFF)) * 4
        out[0] |= map3[idx]
        out[1] |= map3[idx + 1]
        out[2] |= map3[idx + 2]
        out[3] |= map3[idx + 3]
    return [x & 0xFF for x in out]


def _sub_4033B0(a1: Sequence[int], a2: int, map4: Sequence[int], map5: Sequence[int], map3: Sequence[int]) -> List[int]:
    buf = _sub_403450(a1)
    idx = a2 * 6
    x = [
        (buf[0] ^ map5[idx]) & 0xFF,
        (buf[1] ^ map5[idx + 1]) & 0xFF,
        (buf[2] ^ map5[idx + 2]) & 0xFF,
        (buf[3] ^ map5[idx + 3]) & 0xFF,
        (buf[4] ^ map5[idx + 4]) & 0xFF,
        (buf[5] ^ map5[idx + 5]) & 0xFF,
    ]
    return _sub_4035A0(_sub_403520(x, map4), map3)


def _sub_403340(a1: int, a2: Sequence[int], map4: Sequence[int], map5: Sequence[int], map3: Sequence[int]) -> List[int]:
    right4 = [a2[4], a2[5], a2[6], a2[7]]
    b2 = _sub_4033B0(right4, a1, map4, map5, map3)
    return [
        a2[4],
        a2[5],
        a2[6],
        a2[7],
        (b2[0] ^ a2[0]) & 0xFF,
        (b2[1] ^ a2[1]) & 0xFF,
        (b2[2] ^ a2[2]) & 0xFF,
        (b2[3] ^ a2[3]) & 0xFF,
    ]


def _sub_403220(src8: Sequence[int], map1: Sequence[int], map2: Sequence[int], map3: Sequence[int], map4: Sequence[int], map5: Sequence[int]) -> List[int]:
    rounds: List[List[int]] = [None] * 0x11  # type: ignore[assignment]
    rounds[0] = _sub_4032E0(src8, map1)
    idx = 0
    num2 = 15
    while num2 >= 0:
        rounds[idx + 1] = _sub_403340(num2, rounds[idx], map4, map5, map3)
        num2 -= 1
        idx += 1
    r16 = rounds[0x10]
    buf = [r16[4], r16[5], r16[6], r16[7], r16[0], r16[1], r16[2], r16[3]]
    return _sub_4032E0(buf, map2)


def _decode(src: Sequence[int], index: int, maps: Tuple[Sequence[int], Sequence[int], Sequence[int], Sequence[int], Sequence[int]]) -> List[int]:
    map1, map2, map3, map4, map5 = maps
    if len(src) <= index:
        return []
    dst = [0] * (len(src) - index)
    full = len(dst) // 8
    d_idx = 0
    for _ in range(full):
        block = list(src[index + d_idx : index + d_idx + 8])
        dec = _sub_403220(block, map1, map2, map3, map4, map5)
        dst[d_idx : d_idx + 8] = dec
        d_idx += 8
    remain = len(dst) % 8
    if remain > 0:
        num4 = len(dst) - remain
        dst[num4:] = src[index + num4 : index + num4 + remain]
    return [x & 0xFF for x in dst]


def _load_maps(map_dir: Path) -> Tuple[List[int], List[int], List[int], List[int], List[int]]:
    maps = []
    for i in range(1, 6):
        p = map_dir / f"Map{i}"
        if not p.exists():
            raise FileNotFoundError(f"Pak decode key 파일 누락: {p}")
        maps.append(_to_u8_list(p.read_bytes()))
    return maps[0], maps[1], maps[2], maps[3], maps[4]


def _parse_records(buf: Sequence[int]) -> List[PakEntry]:
    entries: List[PakEntry] = []
    for off in range(0, len(buf) - RECORD_SIZE + 1, RECORD_SIZE):
        offset = _to_int32_le(buf, off)
        name_bytes = bytes(buf[off + 4 : off + 4 + NAME_SIZE])
        size = _to_int32_le(buf, off + 0x18)
        if offset == 0 and size == 0 and name_bytes.strip(b"\x00") == b"":
            continue
        name = name_bytes.split(b"\x00", 1)[0].decode("latin1", errors="ignore").strip()
        if not name:
            continue
        if not _is_sane_name(name):
            continue
        if offset < 0 or size <= 0:
            continue
        entries.append(PakEntry(offset=offset, filename=name, size=size))
    entries.sort(key=lambda e: e.offset)
    return entries


def parse_idx_entries(idx_path: Path, map_dir: Path) -> List[PakEntry]:
    raw = _to_u8_list(idx_path.read_bytes())

    # 1) 정상 경로: 키(Map1~Map5)로 복호화
    try:
        dec = _decode(raw, 4, _load_maps(map_dir))
        out = _parse_records(dec)
        if out:
            return out
    except FileNotFoundError:
        # 키 파일이 없으면 아래 평문 파서로 폴백
        pass

    # 2) 폴백: idx가 평문일 수 있으므로 그대로 파싱
    plain = _parse_records(raw)
    if plain:
        return plain
    # 3) 변형 포맷 대응: 4바이트 헤더 스킵 후 파싱
    if len(raw) > 4:
        plain_skip = _parse_records(raw[4:])
        if plain_skip:
            return plain_skip
    return []


def _is_sane_name(name: str) -> bool:
    if len(name) > NAME_SIZE:
        return False
    return bool(re.fullmatch(r"[\w\-. ]+", name))


def _sniff_ext(buf: bytes) -> str:
    if buf.startswith(b"BM"):
        return ".bmp"
    if buf.startswith(b"\x89PNG\r\n\x1a\n"):
        return ".png"
    if buf.startswith(b"\xff\xd8\xff"):
        return ".jpg"
    if buf.startswith((b"GIF87a", b"GIF89a")):
        return ".gif"
    return ".bin"


def _inv_candidates_from_name(name: str) -> List[int]:
    stem = Path(name).stem
    nums: List[int] = []
    if stem.isdigit():
        nums.append(int(stem))
    else:
        m = re.findall(r"\d+", stem)
        if m:
            nums.append(int(m[-1]))
    out: List[int] = []
    for raw in nums:
        out.append(raw)
        if raw >= 1000000:
            out.append(raw - 1000000)
    # 중복 제거 + 음수 제거
    seen = set()
    final = []
    for n in out:
        if n < 0 or n in seen:
            continue
        seen.add(n)
        final.append(n)
    return final


def discover_sprite_pairs(search_root: Path, limit: int = 50) -> List[Tuple[Path, Path]]:
    if not search_root.exists():
        return []
    idx_files = list(search_root.rglob("*.idx"))
    pairs: List[Tuple[Path, Path]] = []
    for idx in idx_files:
        pak = idx.with_suffix(".pak")
        if pak.exists():
            pairs.append((idx, pak))
    # Sprite 이름 우선 정렬
    pairs.sort(key=lambda x: (0 if x[0].stem.lower() == "sprite" else 1, str(x[0]).lower()))
    return pairs[:limit]


def extract_item_icons_from_pak(
    idx_path: Path,
    pak_path: Path,
    map_dir: Path,
    inv_ids: Iterable[int],
    output_dir: Path,
) -> Dict[int, str]:
    want: set[int] = set()
    for v in inv_ids:
        try:
            n = int(str(v).strip())
        except Exception:
            # 비정상 값(예: 3¹) 무시
            continue
        if n >= 0:
            want.add(n)
    if not want:
        return {}
    output_dir.mkdir(parents=True, exist_ok=True)
    entries = parse_idx_entries(idx_path, map_dir)
    if not entries:
        return {}

    mapping: Dict[int, str] = {}
    with pak_path.open("rb") as fp:
        for entry in entries:
            if len(mapping) >= len(want):
                break
            cands = _inv_candidates_from_name(entry.filename)
            target_inv = next((n for n in cands if n in want and n not in mapping), None)
            if target_inv is None:
                continue

            fp.seek(entry.offset)
            blob = fp.read(entry.size)
            if not blob:
                continue
            ext = _sniff_ext(blob)
            # 이미지 포맷만 표시에 활용
            if ext not in {".bmp", ".png", ".jpg", ".gif"}:
                continue
            out_file = output_dir / f"{target_inv}{ext}"
            out_file.write_bytes(blob)
            mapping[target_inv] = str(out_file)
    return mapping
