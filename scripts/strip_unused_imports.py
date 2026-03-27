# -*- coding: utf-8 -*-
"""Remove unused single-type imports (ignore occurrences only in comments)."""
import glob
import os
import re
import sys


def strip_comments_for_scan(java: str) -> str:
    """Rough comment strip for identifier scanning (not a full parser)."""
    out = []
    i = 0
    n = len(java)
    while i < n:
        if i < n - 1 and java[i : i + 2] == "//":
            while i < n and java[i] != "\n":
                i += 1
            continue
        if i < n - 1 and java[i : i + 2] == "/*":
            i += 2
            while i < n - 1:
                if java[i : i + 2] == "*/":
                    i += 2
                    break
                i += 1
            continue
        if java[i] == '"':
            out.append(" ")
            i += 1
            while i < n:
                if java[i] == "\\" and i + 1 < n:
                    i += 2
                    continue
                if java[i] == '"':
                    i += 1
                    break
                i += 1
            continue
        if java[i] == "'":
            out.append(" ")
            i += 1
            while i < n:
                if java[i] == "\\" and i + 1 < n:
                    i += 2
                    continue
                if java[i] == "'":
                    i += 1
                    break
                i += 1
            continue
        out.append(java[i])
        i += 1
    return "".join(out)


def strip_imports_in_file(path: str) -> int:
    try:
        raw = open(path, encoding="utf-8", errors="replace").read()
    except OSError:
        return 0
    if "import static " in raw:
        return 0
    lines = raw.splitlines(keepends=True)
    import_blocks = []
    for i, L in enumerate(lines):
        s = L.strip()
        if not s.startswith("import ") or s.startswith("import static"):
            continue
        if "*" in s:
            continue
        m = re.match(r"import\s+([\w.]+)\s*;", s)
        if not m:
            continue
        full = m.group(1)
        simple = full.split(".")[-1]
        import_blocks.append((i, full, simple, L))

    if not import_blocks:
        return 0

    pkg = None
    for L in lines[:45]:
        if L.strip().startswith("package "):
            pkg = L.strip()[8:].strip().rstrip(";")
            break

    non_import = []
    for L in lines:
        s = L.strip()
        if s.startswith("import ") or s.startswith("package "):
            continue
        non_import.append(L)
    body = "".join(non_import)
    body = strip_comments_for_scan(body)

    to_drop = set()
    for i, full, simple, L in import_blocks:
        if full.startswith("java.lang."):
            continue
        imp_pkg = full.rsplit(".", 1)[0]
        if pkg and imp_pkg == pkg:
            continue
        if len(re.findall(r"\b" + re.escape(simple) + r"\b", body)) == 0:
            to_drop.add(i)

    if not to_drop:
        return 0
    new_lines = [L for i, L in enumerate(lines) if i not in to_drop]
    open(path, "w", encoding="utf-8", newline="\n").write("".join(new_lines))
    return len(to_drop)


def main():
    root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    candidates = glob.glob(os.path.join(root, "2.*", "src"))
    src = candidates[0] if candidates else None
    if not src or not os.path.isdir(src):
        print("2.*/src not found", root)
        sys.exit(1)
    total = 0
    nfiles = 0
    for dirpath, _, files in os.walk(src):
        for f in files:
            if not f.endswith(".java"):
                continue
            p = os.path.join(dirpath, f)
            n = strip_imports_in_file(p)
            if n:
                nfiles += 1
                total += n
                print(f"+{n}\t{p}")
    print(f"Done: {total} imports removed in {nfiles} files")


if __name__ == "__main__":
    main()
