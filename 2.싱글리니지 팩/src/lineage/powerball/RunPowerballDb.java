package lineage.powerball;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 파워볼 DB 스크립트 적용용. mysql.conf 읽어서 l1jdb에 powerball_*.sql 실행.
 * 실행: 서버 폴더( mysql.conf 있는 곳)에서
 *   java -cp "server.jar;lib/*" lineage.powerball.RunPowerballDb
 */
public class RunPowerballDb {

    public static void main(String[] args) {
        String dir = System.getProperty("user.dir");
        Path confPath = Paths.get(dir, "mysql.conf");
        if (!Files.exists(confPath)) {
            System.err.println("mysql.conf 없음: " + confPath);
            return;
        }

        String url = null, id = null, pw = null, driver = null;
        try (BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(confPath.toFile()), StandardCharsets.UTF_8))) {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.startsWith("#")) continue;
                int pos = line.indexOf("=");
                if (pos <= 0) continue;
                String key = line.substring(0, pos).trim();
                String value = line.substring(pos + 1).trim();
                if (key.equalsIgnoreCase("Url")) url = value;
                else if (key.equalsIgnoreCase("Id")) id = value;
                else if (key.equalsIgnoreCase("Pw")) pw = value;
                else if (key.equalsIgnoreCase("Driver")) driver = value;
            }
        } catch (Exception e) {
            System.err.println("mysql.conf 읽기 실패: " + e.getMessage());
            return;
        }

        if (url == null || id == null || pw == null) {
            System.err.println("mysql.conf에 Url, Id, Pw 필요");
            return;
        }

        try {
            if (driver != null && !driver.isEmpty()) {
                Class.forName(driver);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("드라이버 로드 실패: " + driver + " - " + e.getMessage());
            return;
        }

        String[] files = { "db/powerball_tables.sql", "db/powerball_npc.sql", "db/powerball_shop.sql", "db/powerball_claimed.sql" };
        try (Connection con = DriverManager.getConnection(url, id, pw)) {
            for (String f : files) {
                Path path = Paths.get(dir, f);
                if (!Files.exists(path)) {
                    System.out.println("[건너뜀] 없음: " + path);
                    continue;
                }
                String sql = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
                List<String> statements = splitStatements(sql);
                int run = 0;
                for (String st : statements) {
                    String s = stripCommentLines(st.trim());
                    if (s.isEmpty() || s.toLowerCase().startsWith("use ")) continue;
                    try (Statement stmt = con.createStatement()) {
                        stmt.execute(s);
                        run++;
                    } catch (Exception e) {
                        String msg = e.getMessage() != null ? e.getMessage() : "";
                        if (msg.contains("Duplicate") || msg.contains("already exists")) {
                            System.out.println("[이미 있음] " + s.substring(0, Math.min(50, s.length())) + "...");
                        } else {
                            System.err.println("[실패] " + msg);
                            System.err.println("  SQL: " + s.substring(0, Math.min(80, s.length())) + "...");
                        }
                    }
                }
                System.out.println("[적용] " + f + " (실행 " + run + "건)");
            }
            System.out.println("파워볼 DB 적용 완료.");
        } catch (Exception e) {
            System.err.println("DB 연결/실행 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static List<String> splitStatements(String sql) {
        List<String> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inString = false;
        char quote = 0;
        for (int i = 0; i < sql.length(); i++) {
            char c = sql.charAt(i);
            if (inString) {
                sb.append(c);
                if (c == quote && (i == 0 || sql.charAt(i - 1) != '\\')) inString = false;
                continue;
            }
            if (c == '\'' || c == '"' || c == '`') {
                inString = true;
                quote = c;
                sb.append(c);
                continue;
            }
            if (c == ';') {
                list.add(sb.toString().trim());
                sb.setLength(0);
                continue;
            }
            sb.append(c);
        }
        if (sb.length() > 0) list.add(sb.toString().trim());
        return list;
    }

    /** 문장 앞쪽 -- 주석 제거 (JDBC 실행용) */
    private static String stripCommentLines(String s) {
        StringBuilder sb = new StringBuilder();
        for (String line : s.split("\\r?\\n")) {
            String t = line.trim();
            if (!t.startsWith("--")) sb.append(line).append("\n");
        }
        return sb.toString().trim();
    }
}
