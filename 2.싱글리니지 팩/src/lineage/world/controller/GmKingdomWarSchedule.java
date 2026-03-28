package lineage.world.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import lineage.database.DatabaseConnection;

/**
 * GM 툴·DB 기반 공성 자동 시작 스케줄 (성별 요일·시각·진행분). {@link KingdomController#toTimer} 에서 사용.
 * 요일 비트는 {@link java.util.Date#getDay()} 와 동일: 0=일요일 … 6=토요일.
 * 1차(weekdays+start_hour/min)와 2차(weekdays_2+start_hour_2/min_2)로 주 2회·서로 다른 시각 설정 가능.
 */
public final class GmKingdomWarSchedule {

	private static class Row {
		boolean enabled;
		int weekdays;
		int startHour;
		int startMin;
		int durationMinutes;
		int weekdays2;
		int startHour2;
		int startMin2;
	}

	private static final ConcurrentHashMap<Integer, Row> CACHE = new ConcurrentHashMap<Integer, Row>();
	private static volatile long lastReloadMs;
	private static volatile boolean tableEnsured;

	private GmKingdomWarSchedule() {
	}

	private static void ensureTable(Connection con) {
		if (tableEnsured)
			return;
		synchronized (GmKingdomWarSchedule.class) {
			if (tableEnsured)
				return;
			try (PreparedStatement st = con.prepareStatement(
					"CREATE TABLE IF NOT EXISTS gm_kingdom_war_schedule ("
							+ "kingdom_uid TINYINT UNSIGNED NOT NULL PRIMARY KEY,"
							+ "enabled TINYINT NOT NULL DEFAULT 0,"
							+ "weekdays TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '비트=Date.getDay 0=일..6=토',"
							+ "start_hour TINYINT UNSIGNED NOT NULL DEFAULT 20,"
							+ "start_min TINYINT UNSIGNED NOT NULL DEFAULT 0,"
							+ "duration_minutes SMALLINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '0=kingdom_war_time',"
							+ "weekdays_2 TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '2차 요일 비트 0=미사용',"
							+ "start_hour_2 TINYINT UNSIGNED NOT NULL DEFAULT 20,"
							+ "start_min_2 TINYINT UNSIGNED NOT NULL DEFAULT 0,"
							+ "updated_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3)"
							+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4")) {
				st.executeUpdate();
			} catch (Exception e) {
				lineage.share.System.println("[GM] gm_kingdom_war_schedule 테이블 생성 실패: " + e);
				return;
			}
			tableEnsured = true;
		}
	}

	/** 기존 DB에 2차 스케줄 컬럼 추가 */
	private static void migrateSecondSlotColumns(Connection con) {
		try (Statement st = con.createStatement()) {
			st.executeUpdate(
					"ALTER TABLE gm_kingdom_war_schedule ADD COLUMN weekdays_2 TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '2차 요일 비트'");
		} catch (Exception e) {
			if (!isDuplicateColumn(e))
				lineage.share.System.println("[GM] gm_kingdom_war_schedule weekdays_2: " + e);
		}
		try (Statement st = con.createStatement()) {
			st.executeUpdate(
					"ALTER TABLE gm_kingdom_war_schedule ADD COLUMN start_hour_2 TINYINT UNSIGNED NOT NULL DEFAULT 20");
		} catch (Exception e) {
			if (!isDuplicateColumn(e))
				lineage.share.System.println("[GM] gm_kingdom_war_schedule start_hour_2: " + e);
		}
		try (Statement st = con.createStatement()) {
			st.executeUpdate(
					"ALTER TABLE gm_kingdom_war_schedule ADD COLUMN start_min_2 TINYINT UNSIGNED NOT NULL DEFAULT 0");
		} catch (Exception e) {
			if (!isDuplicateColumn(e))
				lineage.share.System.println("[GM] gm_kingdom_war_schedule start_min_2: " + e);
		}
	}

	private static boolean isDuplicateColumn(Exception e) {
		String m = String.valueOf(e.getMessage());
		return m.contains("Duplicate column") || m.contains("1060");
	}

	public static void tickReload() {
		long now = System.currentTimeMillis();
		if (now - lastReloadMs < 4000L)
			return;
		synchronized (GmKingdomWarSchedule.class) {
			if (now - lastReloadMs < 4000L)
				return;
			lastReloadMs = now;
			doReload();
		}
	}

	private static void doReload() {
		CACHE.clear();
		try (Connection con = DatabaseConnection.getLineage()) {
			ensureTable(con);
			migrateSecondSlotColumns(con);
			try (PreparedStatement st = con.prepareStatement(
					"SELECT kingdom_uid, enabled, weekdays, start_hour, start_min, duration_minutes, "
							+ "weekdays_2, start_hour_2, start_min_2 FROM gm_kingdom_war_schedule");
					ResultSet rs = st.executeQuery()) {
				while (rs.next()) {
					Row r = new Row();
					r.enabled = rs.getInt("enabled") != 0;
					r.weekdays = rs.getInt("weekdays") & 0x7F;
					r.startHour = Math.max(0, Math.min(23, rs.getInt("start_hour")));
					r.startMin = Math.max(0, Math.min(59, rs.getInt("start_min")));
					r.durationMinutes = Math.max(0, rs.getInt("duration_minutes"));
					r.weekdays2 = rs.getInt("weekdays_2") & 0x7F;
					r.startHour2 = Math.max(0, Math.min(23, rs.getInt("start_hour_2")));
					r.startMin2 = Math.max(0, Math.min(59, rs.getInt("start_min_2")));
					int uid = rs.getInt("kingdom_uid");
					if (uid > 0 && uid < 256)
						CACHE.put(uid, r);
				}
			}
		} catch (Exception e) {
			// 테이블 없음 등
		}
	}

	/** 해당 성에 DB 자동 스케줄이 켜져 있으면 lineage.conf 기란용 giran_kingdom_war_day_list 자동 시작 생략 */
	public static boolean dbScheduleEnabledFor(int kingdomUid) {
		tickReload();
		Row r = CACHE.get(kingdomUid);
		if (r == null || !r.enabled)
			return false;
		return r.weekdays != 0 || r.weekdays2 != 0;
	}

	/** 0이면 Lineage.kingdom_war_time 사용 */
	public static int getDurationMinutes(int kingdomUid) {
		tickReload();
		Row r = CACHE.get(kingdomUid);
		if (r == null)
			return 0;
		return r.durationMinutes;
	}

	private static boolean matchesSlot(long timeMs, int startHour, int startMin, int weekdaysMask) {
		if (weekdaysMask == 0)
			return false;
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"), Locale.KOREA);
		cal.setTimeInMillis(timeMs);
		if (cal.get(Calendar.SECOND) != 0)
			return false;
		if (cal.get(Calendar.HOUR_OF_DAY) != startHour || cal.get(Calendar.MINUTE) != startMin)
			return false;
		int calDow = cal.get(Calendar.DAY_OF_WEEK);
		int javaDateDay = calDow == Calendar.SUNDAY ? 0 : calDow - 1;
		int bit = 1 << javaDateDay;
		return (weekdaysMask & bit) != 0;
	}

	/**
	 * 한국시간 기준 1차 또는 2차 슬롯의 시각(0초)·요일이 맞으면 true.
	 */
	public static boolean shouldAutoStartNow(int kingdomUid, long timeMs) {
		tickReload();
		Row r = CACHE.get(kingdomUid);
		if (r == null || !r.enabled)
			return false;
		if (matchesSlot(timeMs, r.startHour, r.startMin, r.weekdays))
			return true;
		if (matchesSlot(timeMs, r.startHour2, r.startMin2, r.weekdays2))
			return true;
		return false;
	}
}
