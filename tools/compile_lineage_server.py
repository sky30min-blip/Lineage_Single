# -*- coding: utf-8 -*-
"""Build server.jar for 2.싱글리니지 팩 (JDK 8). Run: python tools/compile_lineage_server.py"""
import os
import pathlib
import subprocess
import sys

ROOT = pathlib.Path(__file__).resolve().parent.parent
PACK = ROOT / "2.싱글리니지 팩"
JAVA_HOME = pathlib.Path(os.environ.get("JAVA_HOME", r"D:\jdk8"))
JAVAC = JAVA_HOME / "bin" / "javac.exe"
JAR = JAVA_HOME / "bin" / "jar.exe"


def main() -> int:
    if not PACK.is_dir():
        print("Pack folder not found:", PACK, file=sys.stderr)
        return 1
    if not JAVAC.is_file():
        print("javac not found:", JAVAC, file=sys.stderr)
        return 1

    jars = ";".join(
        p.relative_to(PACK).as_posix().replace("/", os.sep)
        for p in sorted((PACK / "lib").glob("*.jar"))
    )
    srcs = sorted((PACK / "src").rglob("*.java"))
    if not srcs:
        print("No .java under", PACK / "src", file=sys.stderr)
        return 1

    # 한글 파일명은 Python pathlib로 나열 시 깨질 수 있어 compile.bat과 같이 PowerShell로 목록 생성
    argf = PACK / "sources_build_py.txt"
    ps = (
        "$root = (Get-Location).Path.TrimEnd('\\') + '\\'; "
        "Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { "
        "$_.FullName.Substring($root.Length) } | Set-Content -Path 'sources_build_py.txt' -Encoding Default"
    )
    subprocess.run(
        ["powershell", "-NoProfile", "-Command", ps],
        cwd=str(PACK),
        check=True,
    )

    build = PACK / "build"
    build.mkdir(exist_ok=True)

    cmd = [
        str(JAVAC),
        "-nowarn",
        "-encoding",
        "UTF-8",
        "-d",
        "build",
        "-cp",
        jars,
        "-J-Xmx2048m",
        "@" + str(argf.name),
    ]
    print("Compiling", len(srcs), "Java files...")
    r = subprocess.run(cmd, cwd=str(PACK))
    if r.returncode != 0:
        return r.returncode

    out_jar = PACK / "server.jar"
    mf = PACK / "src" / "META-INF" / "MANIFEST.MF"
    subprocess.run(
        [str(JAR), "cfm", "server.jar", str(mf.relative_to(PACK)), "-C", "build", "."],
        check=True,
        cwd=str(PACK),
    )
    print("Done:", out_jar)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
