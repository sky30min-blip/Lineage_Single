# -*- coding: utf-8 -*-
"""Java 파일에서 미사용 import 라인 제거 (단순 이름이 본문에 없는 경우만)"""
import re
import os

SRC = "src"

def simple_name(imp):
    # import xxx.yyy.ZZZ; or import xxx.yyy.ZZZ.*; -> ZZZ
    m = re.match(r'\s*import\s+(?:static\s+)?[\w.]+\.(\w+)(?:\s*\.\*)?\s*;\s*', imp)
    return m.group(1) if m else None

def is_used_simple_name(body, name):
    # 단순 이름이 식별자로 쓰였는지 (주석/문자열 제외는 어렵으므로 전체에서 검색)
    # \bname\b 로 단어 경계 검색. 단, import 구문 제거된 본문에서
    return re.search(r'\b' + re.escape(name) + r'\b', body) is not None

def process_file(path):
    with open(path, 'r', encoding='utf-8', errors='replace') as f:
        content = f.read()
    lines = content.split('\n')
    new_lines = []
    in_imports = True
    import_block = []
    rest = []
    i = 0
    while i < len(lines):
        line = lines[i]
        # package 또는 import 구간
        if in_imports and (line.strip().startswith('package ') or re.match(r'\s*import\s+', line)):
            import_block.append(line)
            i += 1
            continue
        elif in_imports and line.strip() == '':
            import_block.append(line)
            i += 1
            continue
        else:
            in_imports = False
            rest.append(line)
            i += 1
    rest_text = '\n'.join(rest)
    # import 블록에서 제거할 것 결정
    kept = []
    for line in import_block:
        stripped = line.strip()
        if not re.match(r'import\s+', stripped):
            kept.append(line)
            continue
        name = simple_name(line)
        if not name:
            kept.append(line)
            continue
        # static import .* 는 보수적으로 유지
        if '.*' in stripped:
            kept.append(line)
            continue
        if is_used_simple_name(rest_text, name):
            kept.append(line)
        else:
            pass  # 제거
    new_content = '\n'.join(kept) + '\n' + rest_text
    if new_content != content:
        with open(path, 'w', encoding='utf-8', newline='\n') as f:
            f.write(new_content)
        return True
    return False

count = 0
for root, dirs, files in os.walk(SRC):
    for f in files:
        if f.endswith('.java'):
            path = os.path.join(root, f)
            if process_file(path):
                count += 1
                print(path)
print("Done. Modified", count, "files.")
