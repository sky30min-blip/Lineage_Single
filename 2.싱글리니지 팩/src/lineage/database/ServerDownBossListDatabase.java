package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import lineage.bean.database.Boss;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.thread.AiThread;
import lineage.world.controller.BossController;
import lineage.world.object.instance.MonsterInstance;

public final class ServerDownBossListDatabase {

	static public void init(Connection con) {
		TimeLine.start("ServerDownBossListDatabase..");
		List<Boss> list = MonsterBossSpawnlistDatabase.getList();

		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = con.prepareStatement("SELECT * FROM server_down_bosslist");
			rs = st.executeQuery();
			
			while (rs.next()) {
				if (Lineage.boss_live_time > 0 && rs.getInt("live_time") < 1)
					continue;
				
				if (!BossController.isSpawn(rs.getString("monster"), rs.getInt("map"))) {					
					// 객체 생성.
					MonsterInstance mi = MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find(rs.getString("monster")));
					
					if (rs.getInt("now_hp") > 0 && mi != null) {
						BossController.appendBossList(mi, rs.getInt("live_time"));
						
						if (mi.getMonster().isHaste() || mi.getMonster().isBravery()) {
							if (mi.getMonster().isHaste())
								mi.setSpeed(1);
							if (mi.getMonster().isBravery())
								mi.setBrave(true);
						}
						
						// 정보 갱신.
						mi.setBoss(true);	
						mi.setNowHp(rs.getInt("now_hp"));
						mi.setNowMp(rs.getInt("now_mp"));
						mi.setHomeX(rs.getInt("home_x"));
						mi.setHomeY(rs.getInt("home_y"));
						mi.setHomeMap(rs.getInt("home_map"));
						mi.setHeading(rs.getInt("heading"));
						mi.toTeleport(rs.getInt("x"), rs.getInt("y"), rs.getInt("map"), false);
						// 인공지능쓰레드에 등록.
						AiThread.append(mi);
					}
				}

				for (Boss b : list) {
					if (b.getSpawn().size() <= 0)
						continue;

					if (b.getMon().getName().equalsIgnoreCase(rs.getString("monster")))
						b.setLastTime(System.currentTimeMillis());
				}
			}

		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", ServerDownBossListDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		TimeLine.end();
	}
	
	static public void save(Connection con) {
		PreparedStatement st = null;

		try {
			st = con.prepareStatement("DELETE FROM server_down_bosslist");
			st.executeUpdate();
			st.close();

			for (MonsterInstance boss : BossController.getBossList()) {
				if (Lineage.boss_live_time > 0 && boss.bossLiveTime < 1)
					continue;
				
				if (!boss.isWorldDelete() && !boss.isDead() && boss.getNowHp() > 0 && boss.getX() > 0 && boss.getY() > 0) {
					st = con.prepareStatement("INSERT INTO server_down_bosslist SET objId=?, monster=?, now_hp=?, now_mp=?, home_x=?, home_y=?, home_map=?, x=?, y=?, map=?, heading=?, live_time=?");
					st.setLong(1, boss.getObjectId());
					st.setString(2, boss.getMonster().getName());
					st.setInt(3, boss.getNowHp());
					st.setInt(4, boss.getNowMp());
					st.setInt(5, boss.getHomeX());
					st.setInt(6, boss.getHomeY());
					st.setInt(7, boss.getHomeMap());
					st.setInt(8, boss.getX());
					st.setInt(9, boss.getY());
					st.setInt(10, boss.getMap());
					st.setInt(11, boss.getHeading());
					st.setInt(12, boss.bossLiveTime);
					st.executeUpdate();
					st.close();
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : save(Connection con)\r\n", ServerDownBossListDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}

	static public void close(Connection con) {
		PreparedStatement st = null;

		try {
			st = con.prepareStatement("DELETE FROM server_down_bosslist");
			st.executeUpdate();
			st.close();

			for (MonsterInstance boss : BossController.getBossList()) {
				if (Lineage.boss_live_time > 0 && boss.bossLiveTime < 1)
					continue;
				
				if (!boss.isWorldDelete() && !boss.isDead() && boss.getNowHp() > 0 && boss.getX() > 0 && boss.getY() > 0) {
					st = con.prepareStatement("INSERT INTO server_down_bosslist SET objId=?, monster=?, now_hp=?, now_mp=?, home_x=?, home_y=?, home_map=?, x=?, y=?, map=?, heading=?, live_time=?");
					st.setLong(1, boss.getObjectId());
					st.setString(2, boss.getMonster().getName());
					st.setInt(3, boss.getNowHp());
					st.setInt(4, boss.getNowMp());
					st.setInt(5, boss.getHomeX());
					st.setInt(6, boss.getHomeY());
					st.setInt(7, boss.getHomeMap());
					st.setInt(8, boss.getX());
					st.setInt(9, boss.getY());
					st.setInt(10, boss.getMap());
					st.setInt(11, boss.getHeading());
					st.setInt(12, boss.bossLiveTime);
					st.executeUpdate();
					st.close();
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : close(Connection con)\r\n", ServerDownBossListDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}
}
