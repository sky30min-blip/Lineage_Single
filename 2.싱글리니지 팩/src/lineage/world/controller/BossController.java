package lineage.world.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import lineage.bean.database.Boss;
import lineage.bean.database.Monster;
import lineage.bean.lineage.Map;
import lineage.database.MonsterBossSpawnlistDatabase;
import lineage.database.MonsterDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.plugin.PluginController;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.thread.AiThread;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.instance.MonsterInstance;

public final class BossController {
	// 현재 스폰된 보스 리스트
	static public List<MonsterInstance> list;
	static private Calendar calendar;

	static public void init() {
		TimeLine.start("BossController..");

		list = new ArrayList<MonsterInstance>();
		calendar = Calendar.getInstance();

		TimeLine.end();
	}

	static public void toTimer(long time) {
		for (MonsterInstance boss : getBossList()) {
			if (boss != null && boss.getMonster() != null) {
				if (Lineage.boss_live_time > 0) {
					if (--boss.bossLiveTime < 1) {
						toDeadBoss(boss);
						// World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class),
						// String.format("%s 소멸되었습니다.",
						// Util.getStringWord(boss.getMonster().getName(), "이",
						// "가"))));
						toWorldOut(boss);
						boss.toAiThreadDelete();
						World.removeMonster(boss);
						World.remove(boss);
					}
				}
			}
		}

		List<Boss> spawnList = MonsterBossSpawnlistDatabase.getList();

		if (spawnList.size() > 0) {
			// 버그 방지
			for (MonsterInstance boss : getBossList()) {
				if (boss == null || boss.getX() == 0 || boss.getY() == 0) {
					toWorldOut(boss);
					World.removeMonster(boss);
					World.remove(boss);
				}
			}

			// 현재 시간.
			calendar.setTimeInMillis(time);
			Date date = calendar.getTime();
			int day = date.getDay();
			int hour = date.getHours();
			int min = date.getMinutes();
			//
			PluginController.init(BossController.class, "toTimer", time, spawnList, hour, min);
			// 스폰할 보스 루프.
			for (Boss b : spawnList) {
				// 버그 방지
				if (b.getSpawn().size() <= 0)
					continue;

				if (b.isSpawnTime(day, hour, min) && !isSpawn(b)) {
					// 객체 생성.
					MonsterInstance mi = MonsterSpawnlistDatabase.newInstance(b.getMon());

					if (mi != null) {
						appendBossList(mi);

						// 정보 갱신.
						mi.setBoss(true);

						// 좌표 구분 추출.
						StringTokenizer stt = new StringTokenizer(b.getSpawn().get(Util.random(0, b.getSpawn().size() - 1)), ",");

						if (stt.hasMoreTokens()) {
							mi.setHomeX(Integer.valueOf(stt.nextToken().trim()));
							mi.setHomeY(Integer.valueOf(stt.nextToken().trim()));
							mi.setHomeMap(Integer.valueOf(stt.nextToken().trim()));
						}

						// 랜덤 좌표 스폰
						if (mi.getHomeX() == 0 || mi.getHomeY() == 0) {
							Map m = World.get_map(mi.getHomeMap());
							boolean stop = false;

							if (m != null) {
								int lx = Util.random(m.locX1, m.locX2);
								int ly = Util.random(m.locY1, m.locY2);
								int count = 0;
								// 랜덤 좌표 스폰
								do {
									if (count++ > 300) {
										stop = true;
										break;
									}

									lx = Util.random(m.locX1, m.locX2);
									ly = Util.random(m.locY1, m.locY2);

								} while (!World.isThroughObject(lx, ly + 1, mi.getHomeMap(), 0) || !World.isThroughObject(lx, ly - 1, mi.getHomeMap(), 4) || !World.isThroughObject(lx - 1, ly, mi.getHomeMap(), 2)
										|| !World.isThroughObject(lx + 1, ly, mi.getHomeMap(), 6) || !World.isThroughObject(lx - 1, ly + 1, mi.getHomeMap(), 1)
										|| !World.isThroughObject(lx + 1, ly - 1, mi.getHomeMap(), 5) || !World.isThroughObject(lx + 1, ly + 1, mi.getHomeMap(), 7)
										|| !World.isThroughObject(lx - 1, ly - 1, mi.getHomeMap(), 3));

								mi.setHomeX(lx);
								mi.setHomeY(ly);
							} else {
								stop = true;
							}

							if (stop) {
								toWorldOut(mi);
								mi = null;
								continue;
							}
						}

						for (String group_monster : b.getGroup_monster()) {
							Monster monster = MonsterDatabase.find(group_monster.substring(0, group_monster.indexOf("(")));

							if (monster != null) {
								int count = Integer.valueOf(group_monster.substring(group_monster.indexOf("(") + 1, group_monster.indexOf(")")));

								for (int i = 0; i < count; i++) {
									MonsterInstance mon = MonsterSpawnlistDatabase.newInstance(monster);

									if (mon.getMonster().isBoss()) {
										mon.setBoss(true);
										appendBossList(mon);
									}

									if (mon.getMonster().isHaste() || mon.getMonster().isBravery()) {
										if (mon.getMonster().isHaste())
											mon.setSpeed(1);
										if (mon.getMonster().isBravery())
											mon.setBrave(true);
									}

									mon.setGroupMaster(mi);

									mon.setHomeX(mi.getHomeX());
									mon.setHomeY(mi.getHomeY());
									mon.setHomeMap(mi.getHomeMap());
									mon.toTeleport(mon.getHomeX(), mon.getHomeY(), mon.getHomeMap(), false);
									AiThread.append(mon);
								}
							}
						}

						if (mi.getMonster().isHaste() || mi.getMonster().isBravery()) {
							if (mi.getMonster().isHaste())
								mi.setSpeed(1);
							if (mi.getMonster().isBravery())
								mi.setBrave(true);
						}

						if (mi.getMonster().getName().equalsIgnoreCase("카스파")) {
							mi.setGroupMaster(mi);
							mi.setBoss(true);
							appendBossList(mi);

							List<Monster> list = new ArrayList<Monster>();
							list.clear();

							Monster ma = MonsterDatabase.find("세마");
							Monster mb = MonsterDatabase.find("발터자르");
							Monster mc = MonsterDatabase.find("메르키오르");

							MonsterInstance m1 = MonsterSpawnlistDatabase.newInstance(ma);
							if (m1 != null) {
								appendBossList(m1);
								Map mapmap = World.get_map(mi.getHomeMap());
								// 스폰
								MonsterSpawnlistDatabase.toSpawnMonster(m1, mapmap, true, mi.getHomeX() - 1, mi.getHomeY() - 1, mapmap.mapid, 0, 0, 0, false, true);
							}

							MonsterInstance m2 = MonsterSpawnlistDatabase.newInstance(mb);
							if (m2 != null) {
								appendBossList(m2);
								Map mapmap = World.get_map(mi.getHomeMap());
								// 스폰
								MonsterSpawnlistDatabase.toSpawnMonster(m2, mapmap, true, mi.getHomeX() - 1, mi.getHomeY() - 1, mapmap.mapid, 0, 0, 0, false, true);
							}

							MonsterInstance m3 = MonsterSpawnlistDatabase.newInstance(mc);
							if (m3 != null) {
								appendBossList(m3);
								Map mapmap = World.get_map(mi.getHomeMap());
								// 스폰
								MonsterSpawnlistDatabase.toSpawnMonster(m3, mapmap, true, mi.getHomeX() - 1, mi.getHomeY() - 1, mapmap.mapid, 0, 0, 0, false, true);
							}
						}

						if (mi.getMonster().getName().equalsIgnoreCase("커츠")) {
							mi.setGroupMaster(mi);
							mi.setBoss(true);
							appendBossList(mi);

							List<Monster> list = new ArrayList<Monster>();
							list.clear();

							Monster ma = MonsterDatabase.find("해적선");
							Monster mb = MonsterDatabase.find("흑기사");
							Monster mc = MonsterDatabase.find("흑기사");
							Monster md = MonsterDatabase.find("흑기사");
							Monster me = MonsterDatabase.find("흑기사");
							Monster mf = MonsterDatabase.find("흑기사");

							MonsterInstance m1 = MonsterSpawnlistDatabase.newInstance(ma);
							if (m1 != null) {
								appendBossList(m1);
								Map mapmap = World.get_map(mi.getHomeMap());
								// 스폰
								MonsterSpawnlistDatabase.toSpawnMonster(m1, mapmap, true, 32624, 32982, 0, 0, 0, 0, false, true);
							}
							
							MonsterInstance m2 = MonsterSpawnlistDatabase.newInstance(mb);
							if (m2 != null) {
								appendBossList(m2);
								Map mapmap = World.get_map(mi.getHomeMap());
								// 스폰
								MonsterSpawnlistDatabase.toSpawnMonster(m2, mapmap, true, mi.getHomeX() - 1, mi.getHomeY() - 1, mapmap.mapid, 0, 0, 0, false, true);
							}

							MonsterInstance m3 = MonsterSpawnlistDatabase.newInstance(mc);
							if (m3 != null) {
								appendBossList(m3);
								Map mapmap = World.get_map(mi.getHomeMap());
								// 스폰
								MonsterSpawnlistDatabase.toSpawnMonster(m3, mapmap, true, mi.getHomeX() - 1, mi.getHomeY() - 1, mapmap.mapid, 0, 0, 0, false, true);
							}
							MonsterInstance m4 = MonsterSpawnlistDatabase.newInstance(md);
							if (m4 != null) {
								appendBossList(m4);
								Map mapmap = World.get_map(mi.getHomeMap());
								// 스폰
								MonsterSpawnlistDatabase.toSpawnMonster(m4, mapmap, true, mi.getHomeX() - 1, mi.getHomeY() - 1, mapmap.mapid, 0, 0, 0, false, true);
							}

							MonsterInstance m5 = MonsterSpawnlistDatabase.newInstance(me);
							if (m5 != null) {
								appendBossList(m5);
								Map mapmap = World.get_map(mi.getHomeMap());
								// 스폰
								MonsterSpawnlistDatabase.toSpawnMonster(m5, mapmap, true, mi.getHomeX() - 1, mi.getHomeY() - 1, mapmap.mapid, 0, 0, 0, false, true);
							}
							
							MonsterInstance m6 = MonsterSpawnlistDatabase.newInstance(mf);
							if (m6 != null) {
								appendBossList(m6);
								Map mapmap = World.get_map(mi.getHomeMap());
								// 스폰
								MonsterSpawnlistDatabase.toSpawnMonster(m6, mapmap, true, mi.getHomeX() - 1, mi.getHomeY() - 1, mapmap.mapid, 0, 0, 0, false, true);
							}
						}
						
						if (mi.getMonster().getName().equalsIgnoreCase("드레이크1") || mi.getMonster().getName().equalsIgnoreCase("드레이크2") || mi.getMonster().getName().equalsIgnoreCase("드레이크3")) {
							mi.setGroupMaster(mi);
							mi.setBoss(true);
							appendBossList(mi);

							List<Monster> list = new ArrayList<Monster>();
							list.clear();

							Monster ma = MonsterDatabase.find("드레이크");
							Monster mb = MonsterDatabase.find("드레이크");

							MonsterInstance m1 = MonsterSpawnlistDatabase.newInstance(ma);
							if (m1 != null && Util.random(0, 90) <= Lineage.Double_Drake) {
								appendBossList(m1);
								Map mapmap = World.get_map(mi.getHomeMap());
								// 스폰
								MonsterSpawnlistDatabase.toSpawnMonster(m1, mapmap, true, mi.getHomeX() - 1, mi.getHomeY() - 1, mapmap.mapid, 0, 0, 0, false, true);
							}
							
							MonsterInstance m2 = MonsterSpawnlistDatabase.newInstance(mb);
							if (m2 != null && Util.random(0, 90) <= Lineage.Double_Drake) {
								appendBossList(m2);
								Map mapmap = World.get_map(mi.getHomeMap());
								// 스폰
								MonsterSpawnlistDatabase.toSpawnMonster(m2, mapmap, true, mi.getHomeX() - 1, mi.getHomeY() - 1, mapmap.mapid, 0, 0, 0, false, true);
							}
						}
						mi.toTeleport(mi.getHomeX(), mi.getHomeY(), mi.getHomeMap(), false);
						b.setLastTime(System.currentTimeMillis());
						if (hour != 0)
							// 보스가 스폰된 후 정해진 시간.
							mi.bossLiveTime = Lineage.boss_live_time;
						// 인공지능쓰레드에 등록.
						AiThread.append(mi);

						if (b.is스폰알림여부()) {
							String displayName = mi.getMonster().getName();
							if (displayName.equalsIgnoreCase("카스파")) {
								displayName = "카스파 패밀리";
							}
							if (displayName.equalsIgnoreCase("커츠")) {
								displayName = "커츠 군단";
							}
							String line = String.format("[%s]가 소환되었습니다.", displayName);
							World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), line));
						}
					}
				}
			}
		}
	}

	/**
	 * 현재 스폰된 상태인지 확인해주는 함수.
	 * 
	 * @param b
	 * @return
	 */
	static public boolean isSpawn(Boss b) {
		synchronized (list) {
			for (MonsterInstance mi : list) {
				if (mi.getMonster().getName().equalsIgnoreCase(b.getMon().getName()))
					return true;
			}
			return false;
		}
	}

	/**
	 * 보스몬스터 이름으로 스폰된 상태인지 확인하는 함수. 2017-10-07 by all-night
	 */
	static public boolean isSpawn(String boss, int map) {
		synchronized (list) {
			for (MonsterInstance mi : list) {
				if (mi.getMonster().getName().equalsIgnoreCase(boss) && mi.getMap() == map)
					return true;
			}
			return false;
		}
	}

	/**
	 * boss변수가 true인 객체들은 월드에서 사라질때 이 함수가 호출됨.
	 * 
	 * @param mi
	 */
	static public void toWorldOut(MonsterInstance mi) {
		synchronized (list) {
			list.remove(mi);
		}
	}

	static public List<MonsterInstance> getBossList() {
		synchronized (list) {
			return new ArrayList<MonsterInstance>(list);
		}
	}

	static public void appendBossList(MonsterInstance mi) {
		synchronized (list) {
			if (!list.contains(mi)) {
				mi.bossLiveTime = Lineage.boss_live_time;
				list.add(mi);
			}
		}
	}

	static public void appendBossList(MonsterInstance mi, int time) {
		synchronized (list) {
			if (!list.contains(mi)) {
				mi.bossLiveTime = Lineage.boss_live_time;
				list.add(mi);
			}
		}
	}

	static private void toDeadBoss(MonsterInstance boss) {
		if (boss != null) {
			String name = boss.getMonster().getName();

			if (name.equalsIgnoreCase("세마") || name.equalsIgnoreCase("메르키오르") || name.equalsIgnoreCase("발터자르")) {
			} else {
				if (name.equalsIgnoreCase("카스파")) {
					name = "카스파 패밀리";

					World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), "" + name + " 소멸 되었습니다"));
					boss.toAiThreadDelete();
				}
			}
		}
	}
}