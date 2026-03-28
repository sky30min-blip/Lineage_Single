package lineage.world.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ConcurrentHashMap;

import lineage.database.DatabaseConnection;

/**
 * 웹 GM 툴의 gm_event_settings 테이블 캐시. 4초마다 DB에서 다시 읽는다.
 * 행이 없으면 해당 키는 "기본 활성"으로 간주한다.
 */
public final class GmEventSettings {

	public static final String HELL = "hell";
	public static final String TREASURE = "treasure";
	public static final String WORLDBOSS = "worldboss";
	public static final String ICEDUNGEON = "icedungeon";
	public static final String TIMEEVENT = "timeevent";
	public static final String DEVIL = "devil";
	public static final String DIMENSION = "dimension";
	public static final String DOLLRACE = "dollrace";

	private static class Row {
		boolean enabled = true;
		int minLevel;
		int playTimeSeconds;
		String monsterName = "";
		String bonusDropItem = "";
		int bonusDropCount = 1;
	}

	private static final ConcurrentHashMap<String, Row> CACHE = new ConcurrentHashMap<String, Row>();
	private static volatile long lastReloadMs;

	public static void tickReload() {
		long now = System.currentTimeMillis();
		if (now - lastReloadMs < 4000L)
			return;
		synchronized (GmEventSettings.class) {
			if (now - lastReloadMs < 4000L)
				return;
			lastReloadMs = now;
			doReload();
		}
	}

	private static void doReload() {
		CACHE.clear();
		try (Connection con = DatabaseConnection.getLineage();
				PreparedStatement ps = con.prepareStatement(
						"SELECT event_key, enabled, min_level, play_time_seconds, monster_name, bonus_drop_item, bonus_drop_count FROM gm_event_settings")) {
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Row r = new Row();
				r.enabled = rs.getInt("enabled") != 0;
				r.minLevel = rs.getInt("min_level");
				r.playTimeSeconds = rs.getInt("play_time_seconds");
				String mn = rs.getString("monster_name");
				r.monsterName = mn == null ? "" : mn.trim();
				String bd = rs.getString("bonus_drop_item");
				r.bonusDropItem = bd == null ? "" : bd.trim();
				r.bonusDropCount = rs.getInt("bonus_drop_count");
				String key = rs.getString("event_key");
				if (key != null && !key.isEmpty())
					CACHE.put(key.trim().toLowerCase(), r);
			}
		} catch (Exception e) {
			// 테이블 없음 등
		}
	}

	private static Row row(String key) {
		tickReload();
		if (key == null)
			return null;
		return CACHE.get(key.toLowerCase());
	}

	public static boolean isEnabled(String key) {
		Row r = row(key);
		return r == null || r.enabled;
	}

	public static int getMinLevel(String key, int lineageDefault) {
		Row r = row(key);
		if (r == null || r.minLevel <= 0)
			return lineageDefault;
		return r.minLevel;
	}

	public static int getPlayTimeSeconds(String key, int lineageDefault) {
		Row r = row(key);
		if (r == null || r.playTimeSeconds <= 0)
			return lineageDefault;
		return r.playTimeSeconds;
	}

	public static String getMonsterName(String key, String defaultName) {
		Row r = row(key);
		if (r == null || r.monsterName == null || r.monsterName.isEmpty())
			return defaultName;
		return r.monsterName;
	}

	public static String getBonusDropItem(String key) {
		Row r = row(key);
		if (r == null || r.bonusDropItem == null)
			return "";
		return r.bonusDropItem;
	}

	/** bonus_drop_item 이 비어 있으면 fallbackCount 그대로 사용 */
	public static int getBonusDropCount(String key, int fallbackCount) {
		Row r = row(key);
		if (r == null || r.bonusDropItem == null || r.bonusDropItem.isEmpty())
			return fallbackCount;
		if (r.bonusDropCount > 0)
			return r.bonusDropCount;
		return fallbackCount;
	}
}
