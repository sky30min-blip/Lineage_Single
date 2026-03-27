package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * GM 웹 툴용: 서버 가동 여부(heartbeat).<br>
 * - 가동 중: {@link #touchOnlineIfDue()} 로 주기 갱신<br>
 * - 정상 종료: {@link #markOffline(Connection)} 및 {@code gm_robot_live} 비우기(RobotController)<br>
 * 툴에서는 {@code online=1} 이어도 {@code updated_at} 이 수십 초 이상 지났으면 크래시/강제 종료로 간주해 오프라인 UI 처리 권장.
 */
public final class GmServerStatusDatabase {

	private static final AtomicLong lastTouchMs = new AtomicLong(0L);
	private static final long TOUCH_INTERVAL_MS = 5000L;
	private static final AtomicBoolean hookRegistered = new AtomicBoolean(false);

	private GmServerStatusDatabase() {
	}

	public static void ensureTable(Connection con) throws Exception {
		if (con == null)
			return;
		try (PreparedStatement ps = con.prepareStatement(
				"CREATE TABLE IF NOT EXISTS `gm_server_status` ("
						+ "`id` TINYINT NOT NULL PRIMARY KEY DEFAULT 1,"
						+ "`online` TINYINT NOT NULL DEFAULT 0,"
						+ "`updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
						+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='GM툴: 서버 가동/heartbeat'")) {
			ps.executeUpdate();
		}
		try (PreparedStatement ins = con.prepareStatement(
				"INSERT IGNORE INTO `gm_server_status` (`id`,`online`) VALUES (1,0)")) {
			ins.executeUpdate();
		}
	}

	/** 서버 로딩 완료 직후 1회 (이미 열린 커넥션 사용 가능) */
	public static void markOnlineNow(Connection con) {
		if (con == null)
			return;
		try {
			ensureTable(con);
			try (PreparedStatement ps = con.prepareStatement(
					"UPDATE `gm_server_status` SET `online`=1, `updated_at`=NOW() WHERE `id`=1")) {
				ps.executeUpdate();
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : markOnlineNow\r\n", GmServerStatusDatabase.class.getName());
			lineage.share.System.println(e);
		}
	}

	/** TimeThread 등에서 주기 호출 (내부 스로틀) */
	public static void touchOnlineIfDue() {
		long now = System.currentTimeMillis();
		if (now - lastTouchMs.get() < TOUCH_INTERVAL_MS)
			return;
		lastTouchMs.set(now);
		Connection con = null;
		try {
			con = DatabaseConnection.getLineage();
			ensureTable(con);
			try (PreparedStatement ps = con.prepareStatement(
					"UPDATE `gm_server_status` SET `online`=1, `updated_at`=NOW() WHERE `id`=1")) {
				ps.executeUpdate();
			}
		} catch (Exception e) {
			// 테이블/DB 일시 오류는 무시 (다음 틱에 재시도)
		} finally {
			DatabaseConnection.close(con);
		}
	}

	public static void markOffline(Connection con) {
		if (con == null)
			return;
		try {
			ensureTable(con);
			try (PreparedStatement ps = con.prepareStatement(
					"UPDATE `gm_server_status` SET `online`=0, `updated_at`=NOW() WHERE `id`=1")) {
				ps.executeUpdate();
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : markOffline\r\n", GmServerStatusDatabase.class.getName());
			lineage.share.System.println(e);
		}
	}

	/**
	 * JVM 종료 훅·크래시 대비: 별도 커넥션으로 오프라인 표기 + 라이브 봇 스냅샷 삭제.<br>
	 * 정상 종료 절차가 이미 실행된 경우에도 치명적이지 않게 동작.
	 */
	public static void markOfflineStandalone() {
		Connection con = null;
		try {
			con = DatabaseConnection.getLineage();
			markOffline(con);
			try (PreparedStatement del = con.prepareStatement("DELETE FROM `gm_robot_live`")) {
				del.executeUpdate();
			}
		} catch (Exception e) {
			// 종료 시점 DB 끊김 등
		} finally {
			DatabaseConnection.close(con);
		}
	}

	public static void registerJvmShutdownHook() {
		if (!hookRegistered.compareAndSet(false, true))
			return;
		Runtime.getRuntime().addShutdownHook(new Thread(GmServerStatusDatabase::markOfflineStandalone, "gm-server-status-offline"));
	}
}
