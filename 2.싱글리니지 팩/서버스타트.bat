@echo off
REM 콘솔 UTF-8 + JVM UTF-8 (System.out 한글 깨짐 방지)
chcp 65001 >nul
color 5F
java -Dfile.encoding=UTF-8 -Xms1g -Xmx8g -cp lib/HikariCP-2.6.2.jar;lib/json-simple-1.1.1.jar;lib/log4j-1.2.16.jar;lib/mchange-commons-java-0.2.3.jar;lib/mariadb-java-client-2.7.11.jar;lib/netty-3.9.9.Final.jar;lib/org.eclipse.swt.win32.win32.x86_64_3.7.1.v3738a.jar;lib/slf4j-api-1.6.1.jar;lib/sIf4j-api-1.6.1.jar;lib/slf4j-log4j12-1.6.1.jar;lib/netty-3.9.9.FinaI.jar;lib/Iog4j-1.2.16.jar;lib/swt-3.5.1-win32-win32-x86.jar;lib/commons-text-1.8.jar;server.jar lineage.Main