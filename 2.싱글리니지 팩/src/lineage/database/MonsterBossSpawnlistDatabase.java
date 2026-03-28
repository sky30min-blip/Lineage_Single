package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import lineage.bean.database.Boss;
import lineage.bean.database.BossSpawn;
import lineage.bean.database.Monster;
import lineage.share.TimeLine;

public final class MonsterBossSpawnlistDatabase {
	static private List<Boss> list;
	static private List<BossSpawn> spawnList;

	static private boolean metaHasColumn(ResultSetMetaData md, String col) throws SQLException {
		for (int i = 1; i <= md.getColumnCount(); i++) {
			if (col.equalsIgnoreCase(md.getColumnLabel(i)))
				return true;
		}
		return false;
	}

	/** spawn_time 컬럼 파싱. 비어 있으면 길이 0 배열 → 시간 매칭 없음(스폰 안 됨). */
	static private int[][] parseBossSpawnTimes(String spawn_time) {
		if (spawn_time == null || spawn_time.trim().length() == 0)
			return new int[0][2];
		ArrayList<int[]> out = new ArrayList<int[]>();
		StringTokenizer stt = new StringTokenizer(spawn_time, ",");
		while (stt.hasMoreTokens()) {
			String boss_time = stt.nextToken().trim();
			if (boss_time.isEmpty())
				continue;
			int colon = boss_time.indexOf(':');
			if (colon <= 0 || colon >= boss_time.length() - 1)
				continue;
			try {
				int boss_h = Integer.parseInt(boss_time.substring(0, colon).trim());
				int boss_m = Integer.parseInt(boss_time.substring(colon + 1).trim());
				if (boss_h >= 0 && boss_h < 24 && boss_m >= 0 && boss_m < 60)
					out.add(new int[] { boss_h, boss_m });
			} catch (Exception ignored) {
			}
		}
		int[][] time = new int[out.size()][2];
		for (int i = 0; i < out.size(); i++) {
			time[i][0] = out.get(i)[0];
			time[i][1] = out.get(i)[1];
		}
		return time;
	}

	static public void init(Connection con) {
		TimeLine.start("MonsterBossSpawnlistDatabase..");

		list = new ArrayList<Boss>();
		spawnList = new ArrayList<BossSpawn>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM monster_spawnlist_boss");
			rs = st.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			boolean hasSpawnEnabled = metaHasColumn(rsmd, "spawn_enabled");
			while (rs.next()) {
				String name = rs.getString("name");
				String monster = rs.getString("monster");
				if (hasSpawnEnabled && rs.getInt("spawn_enabled") == 0)
					continue;
				String spawn = rs.getString("spawn_x_y_map");
				String spawn_time = rs.getString("spawn_time");
				String spawn_day = rs.getString("spawn_day");
				String group_monster = rs.getString("group_monster");
				Monster mon = MonsterDatabase.find(monster);
				boolean isMsg = rs.getInt("스폰알림여부") == 1;

				if (mon != null) {
					Boss b = new Boss();
					b.setName(name);
					b.setMon(mon);
					b.set스폰알림여부(isMsg);
					
					// 좌표 구분 추출.
					StringTokenizer stt = new StringTokenizer(spawn == null ? "" : spawn, "&");

					while (stt.hasMoreTokens()) {
						String map = stt.nextToken();

						if (map.length() > 0)
							b.getSpawn().add(map.trim());
					}

					// 요일 구분 추출.
					stt = new StringTokenizer(spawn_day == null ? "" : spawn_day, ",");

					while (stt.hasMoreTokens()) {
						String day = stt.nextToken();

						if (day.length() > 0) {
							switch (day.trim()) {
							case "일":
							case "일요일":
								b.getSpawn_day().add(0);
								break;
							case "월":
							case "월요일":
								b.getSpawn_day().add(1);
								break;
							case "화":
							case "화요일":
								b.getSpawn_day().add(2);
								break;
							case "수":
							case "수요일":
								b.getSpawn_day().add(3);
								break;
							case "목":
							case "목요일":
								b.getSpawn_day().add(4);
								break;
							case "금":
							case "금요일":
								b.getSpawn_day().add(5);
								break;
							case "토":
							case "토요일":
								b.getSpawn_day().add(6);
								break;
							}
						}
					}

					b.setTime(parseBossSpawnTimes(spawn_time));

					// 그룹 구분 추출.
					stt = new StringTokenizer(group_monster == null ? "" : group_monster, ",");

					while (stt.hasMoreTokens()) {
						String group = stt.nextToken();

						if (group.length() > 0)
							b.getGroup_monster().add(group.trim());
					}

					list.add(b);

					BossSpawn bossSpawn = new BossSpawn();
					bossSpawn.setMonster(rs.getString("monster"));
					bossSpawn.setSpawnDay(rs.getString("spawn_day"));
					bossSpawn.setSpawnTime(rs.getString("spawn_time"));
					spawnList.add(bossSpawn);
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", MonsterBossSpawnlistDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		TimeLine.end();
	}

	static public void reload() {
		TimeLine.start("monster_spawnlist_boss 테이블 리로드 완료 - ");

		synchronized (list) {
			List<Boss> tempList = new ArrayList<Boss>();

			for (Boss b : list)
				tempList.add(b);

			list.clear();
			spawnList.clear();

			PreparedStatement st = null;
			ResultSet rs = null;
			Connection con = null;
			try {
				con = DatabaseConnection.getLineage();
				st = con.prepareStatement("SELECT * FROM monster_spawnlist_boss");
				rs = st.executeQuery();
				ResultSetMetaData rsmd = rs.getMetaData();
				boolean hasSpawnEnabled = metaHasColumn(rsmd, "spawn_enabled");
				while (rs.next()) {
					String name = rs.getString("name");
					String monster = rs.getString("monster");
					if (hasSpawnEnabled && rs.getInt("spawn_enabled") == 0)
						continue;
					String spawn = rs.getString("spawn_x_y_map");
					String spawn_time = rs.getString("spawn_time");
					String spawn_day = rs.getString("spawn_day");
					String group_monster = rs.getString("group_monster");
					Monster mon = MonsterDatabase.find(monster);
					boolean isMsg = rs.getInt("스폰알림여부") == 1;
					
					if (mon != null) {
						Boss b = new Boss();
						b.setName(name);
						b.setMon(mon);
						b.set스폰알림여부(isMsg);
						
						// 좌표 구분 추출.
						StringTokenizer stt = new StringTokenizer(spawn == null ? "" : spawn, "&");

						while (stt.hasMoreTokens()) {
							String map = stt.nextToken();

							if (map.length() > 0)
								b.getSpawn().add(map.trim());
						}

						// 요일 구분 추출.
						stt = new StringTokenizer(spawn_day == null ? "" : spawn_day, ",");

						while (stt.hasMoreTokens()) {
							String day = stt.nextToken();

							if (day.length() > 0) {
								switch (day.trim()) {
								case "일":
								case "일요일":
									b.getSpawn_day().add(0);
									break;
								case "월":
								case "월요일":
									b.getSpawn_day().add(1);
									break;
								case "화":
								case "화요일":
									b.getSpawn_day().add(2);
									break;
								case "수":
								case "수요일":
									b.getSpawn_day().add(3);
									break;
								case "목":
								case "목요일":
									b.getSpawn_day().add(4);
									break;
								case "금":
								case "금요일":
									b.getSpawn_day().add(5);
									break;
								case "토":
								case "토요일":
									b.getSpawn_day().add(6);
									break;
								}
							}
						}

						b.setTime(parseBossSpawnTimes(spawn_time));

						for (Boss tempBoss : tempList) {
							if (tempBoss.getMon().getName().equalsIgnoreCase(b.getMon().getName()))
								b.setLastTime(tempBoss.getLastTime());
						}

						// 그룹 구분 추출.
						stt = new StringTokenizer(group_monster == null ? "" : group_monster, ",");

						while (stt.hasMoreTokens()) {
							String group = stt.nextToken();

							if (group.length() > 0)
								b.getGroup_monster().add(group.trim());
						}

						list.add(b);

						BossSpawn bossSpawn = new BossSpawn();
						bossSpawn.setMonster(rs.getString("monster"));
						bossSpawn.setSpawnDay(rs.getString("spawn_day"));
						bossSpawn.setSpawnTime(rs.getString("spawn_time"));
						spawnList.add(bossSpawn);
					}
				}

				tempList.clear();
			} catch (Exception e) {
				lineage.share.System.printf("%s : reload()\r\n", MonsterBossSpawnlistDatabase.class.toString());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con, st, rs);
			}
		}

		TimeLine.end();
	}

	static public List<Boss> getList() {
		synchronized (list) {
			return new ArrayList<Boss>(list);
		}
	}

	static public List<BossSpawn> getSpawnList() {
		synchronized (list) {
			return spawnList;
		}
	}

	static public int getSize() {
		return list.size();
	}
}
