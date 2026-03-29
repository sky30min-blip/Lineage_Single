# -*- coding: utf-8 -*-
"""Windows javac @file uses system encoding (CP949), not UTF-8."""
import glob
import os
import subprocess
import sys
from pathlib import Path

PACK = Path(__file__).resolve().parent / "2.싱글리니지 팩"
JAVAC = Path(r"D:\jdk8\bin\javac.exe")
JAR = Path(r"D:\jdk8\bin\jar.exe")
ARGFILE = Path(__file__).resolve().parent / "lin_javac_args.txt"


def main() -> int:
    if not (PACK / "src" / "lineage" / "share" / "Lineage.java").is_file():
        print("PACK not found:", PACK)
        return 1
    # cwd=PACK 이므로 상대 경로만 넣으면 @파일에 한글(폴더명) 불필요. 파일명 한글은 CP949로 기록(윈도우 javac @인자).
    java_files = sorted((PACK / "src").rglob("*.java"))
    rel_lines = [str(p.relative_to(PACK)).replace("/", os.sep) for p in java_files]
    ARGFILE.write_text("\n".join(rel_lines), encoding="cp949")
    jars = ";".join(glob.glob(str(PACK / "lib" / "*.jar")))
    build = PACK / "build"
    build.mkdir(exist_ok=True)
    cmd = [
        str(JAVAC),
        "-nowarn",
        "-encoding",
        "UTF-8",
        "-d",
        str(build),
        "-cp",
        jars,
        "-J-Xmx2048m",
        "@" + str(ARGFILE),
    ]
    r = subprocess.run(cmd, cwd=str(PACK))
    if r.returncode != 0:
        return r.returncode
    manifest = PACK / "src" / "META-INF" / "MANIFEST.MF"
    server_jar = PACK / "server.jar"
    cmd2 = [str(JAR), "cfm", str(server_jar), str(manifest), "-C", str(build), "."]
    return subprocess.run(cmd2, cwd=str(PACK)).returncode


if __name__ == "__main__":
    sys.exit(main())
