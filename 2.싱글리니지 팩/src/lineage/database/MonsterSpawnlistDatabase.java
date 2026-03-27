package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import all_night.Lineage_Balance;
import lineage.bean.database.Monster;
import lineage.bean.database.MonsterGroup;
import lineage.bean.database.MonsterSpawnlist;
import lineage.bean.lineage.Map;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectGfx;
import lineage.network.packet.server.S_ObjectName;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.thread.AiThread;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.BossController;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.monster.Antharas;
import lineage.world.object.monster.ArachnevilElder;
import lineage.world.object.monster.Balthazar;
import lineage.world.object.monster.Baphomet;
import lineage.world.object.monster.Blob;
import lineage.world.object.monster.BombFlower;
import lineage.world.object.monster.Deer;
import lineage.world.object.monster.Demon;
import lineage.world.object.monster.Doppelganger;
import lineage.world.object.monster.Duck;
import lineage.world.object.monster.Elder;
import lineage.world.object.monster.Faust_Ghost;
import lineage.world.object.monster.FloatingEye;
import lineage.world.object.monster.Gremlin;
import lineage.world.object.monster.Grimreaper;
import lineage.world.object.monster.Harphy;
import lineage.world.object.monster.Hen;
import lineage.world.object.monster.Ifrit;
import lineage.world.object.monster.Kaspar;
import lineage.world.object.monster.Knight;
import lineage.world.object.monster.Kouts;
import lineage.world.object.monster.Milkcow;
import lineage.world.object.monster.Nancy;
import lineage.world.object.monster.Necromancer;
import lineage.world.object.monster.Oman_Monster;
import lineage.world.object.monster.Perez;
import lineage.world.object.monster.Phoenix;
import lineage.world.object.monster.Pig;
import lineage.world.object.monster.Sema;
import lineage.world.object.monster.Slime;
import lineage.world.object.monster.Spartoi;
import lineage.world.object.monster.StoneGolem;
import lineage.world.object.monster.Unicorn;
import lineage.world.object.monster.Wolf;
import lineage.world.object.monster.event.JackLantern;
import lineage.world.object.monster.quest.AtubaOrc;
import lineage.world.object.monster.quest.BetrayedOrcChief;
import lineage.world.object.monster.quest.BetrayerOfUndead;
import lineage.world.object.monster.quest.Bugbear;
import lineage.world.object.monster.quest.DarkElf;
import lineage.world.object.monster.quest.Darkmar;
import lineage.world.object.monster.quest.DudaMaraOrc;
import lineage.world.object.monster.quest.GandiOrc;
import lineage.world.object.monster.quest.MutantGiantQueenAnt;
import lineage.world.object.monster.quest.NerugaOrc;
import lineage.world.object.monster.quest.OrcZombie;
import lineage.world.object.monster.quest.Ramia;
import lineage.world.object.monster.quest.RovaOrc;
import lineage.world.object.monster.quest.Skeleton;
import lineage.world.object.monster.quest.Zombie;

public final class MonsterSpawnlistDatabase {

	static private List<MonsterInstance> pool;
	static public List<MonsterInstance> list;
	static private List<Monster> temp;

	static public void init(Connection con) {
		TimeLine.start("MonsterSpawnlistDatabase..");

		// 몬스터 스폰 처리.
		pool = new ArrayList<MonsterInstance>();
		list = new ArrayList<MonsterInstance>();
		temp = new ArrayList<Monster>();

		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM monster_spawnlist");
			rs = st.executeQuery();
			while (rs.next()) {
				Monster m = MonsterDatabase.find(rs.getString("monster"));

				if (m != null) {

					if (temp.size() < 1) {
						temp.add(m);
					} else {
						boolean result = true;

						for (int i = 0; i < temp.size(); i++) {
							if (temp.get(i).getName().equalsIgnoreCase(m.getName())) {
								result = false;
								break;
							}
						}

						if (result)
							temp.add(m);
					}
					MonsterSpawnlist ms = new MonsterSpawnlist();
					ms.setUid(rs.getInt("uid"));
					ms.setName(rs.getString("name"));
					ms.setMonster(m);
					ms.setRandom(rs.getString("random").equalsIgnoreCase("true"));
					ms.setCount(rs.getInt("count"));
					ms.setLocSize(rs.getInt("loc_size"));
					ms.setX(rs.getInt("spawn_x"));
					ms.setY(rs.getInt("spawn_y"));
					StringTokenizer stt = new StringTokenizer(rs.getString("spawn_map"), "|");
					while (stt.hasMoreTokens()) {
						String map = stt.nextToken();
						if (map.length() > 0)
							ms.getMap().add(Integer.valueOf(map));
					}
					ms.setReSpawn(rs.getInt("re_spawn_min") * 1000);

					if (rs.getInt("re_spawn_max") < rs.getInt("re_spawn_min"))
						ms.setReSpawnMax(rs.getInt("re_spawn_min") * 1000);
					else
						ms.setReSpawnMax(rs.getInt("re_spawn_max") * 1000);
					ms.setGroup(rs.getString("groups").equalsIgnoreCase("true"));
					if (ms.isGroup()) {
						Monster g1 = MonsterDatabase.find(rs.getString("monster_1"));
						Monster g2 = MonsterDatabase.find(rs.getString("monster_2"));
						Monster g3 = MonsterDatabase.find(rs.getString("monster_3"));
						Monster g4 = MonsterDatabase.find(rs.getString("monster_4"));
						if (g1 != null)
							ms.getListGroup().add(new MonsterGroup(g1, rs.getInt("monster_1_count")));
						if (g2 != null)
							ms.getListGroup().add(new MonsterGroup(g2, rs.getInt("monster_2_count")));
						if (g3 != null)
							ms.getListGroup().add(new MonsterGroup(g3, rs.getInt("monster_3_count")));
						if (g4 != null)
							ms.getListGroup().add(new MonsterGroup(g4, rs.getInt("monster_4_count")));
					}
					toSpawnMonster(ms, null);
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", MonsterSpawnlistDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		TimeLine.end();
	}

	static public void close() {
		synchronized (list) {
			for (MonsterInstance mi : World.getMonsterList()) {
				mi.setDead(true);
				World.remove(mi);
				mi.clearList(true);
				mi.setAiStatus(-2);

			}
		}
	}

	/**
	 * 중복코드 방지용 : gui 기능에서 사용중 lineage.gui.dialog.MonsterSpawn.step4()
	 */
	static public void toSpawnMonster(MonsterSpawnlist ms, Map map) {
		// 버그 방지
		if (ms == null || ms.getMap().size() <= 0)
			return;
		// 맵 확인.
		if (map == null) {
			if (ms.getMap().size() > 1)
				map = World.get_map(ms.getMap().get(Util.random(0, ms.getMap().size() - 1)));
			else
				map = World.get_map(ms.getMap().get(0));
		}
		if (map == null)
			return;
		// 스폰처리.
		for (int i = 0; i < ms.getCount(); ++i) {
			MonsterInstance mi = newInstance(ms.getMonster());
			if (mi == null)
				return;

			if (i == 0)
				mi.setMonsterSpawnlist(ms);
			mi.setDatabaseKey(Integer.valueOf(ms.getUid()));
			if (ms.isGroup()) {
				mi.setGroupMaster(mi);
				// 마스터 스폰.
				toSpawnMonster(mi, map, false, ms.getX(), ms.getY(), map.mapid, ms.getLocSize(), ms.getReSpawn(), ms.getReSpawnMax(), true, true);
				// 관리객체 생성.
				for (MonsterGroup mg : ms.getListGroup()) {
					for (int j = mg.getCount(); j > 0; --j) {
						MonsterInstance a = newInstance(mg.getMonster());
						if (a != null) {
							// 스폰
							toSpawnMonster(a, map, false, ms.getX(), ms.getY(), map.mapid, ms.getLocSize(), ms.getReSpawn(), ms.getReSpawnMax(), true, true);
							// 마스터관리 목록에 등록.
							mi.getGroupList().add(a);
							// 관리하고있는 마스터가 누군지 지정.
							a.setGroupMaster(mi);
						}
					}
				}
			} else {
				toSpawnMonster(mi, map, ms.isRandom(), ms.getX(), ms.getY(), map.mapid, ms.getLocSize(), ms.getReSpawn(), ms.getReSpawnMax(), true, true);
			}
		}
	}

	/**
	 * 파우스트의 악령 및 기타 몬스터 이벤트성 소환시 사용 2017-10-07 by all-night
	 */
	static public boolean toSpawnMonster(Monster monster, int x, int y, int map, int heading, boolean boss, MonsterInstance mon) {
		// 버그 방지
		if (monster == null || map < 0)
			return false;

		// 스폰처리.
		MonsterInstance mi = newInstance(monster);

		if (mi == null)
			return false;

		if (mon instanceof Oman_Monster || mon instanceof Grimreaper)
			mon.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), mon, 4784), true);
		else if (mon instanceof Faust_Ghost)
			mon.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), mon, 5634), true);

		// 기존 몬스터 제거후 스폰대기
		mon.toAiThreadRespawn();

		if (mi.getMonster().isHaste() || mi.getMonster().isBravery()) {
			if (mi.getMonster().isHaste())
				mi.setSpeed(1);
			if (mi.getMonster().isBravery())
				mi.setBrave(true);
		}

		mi.setHeading(heading);
		mi.setReSpawnTime(0);
		mi.setHomeX(x);
		mi.setHomeY(y);
		mi.setHomeMap(map);
		mi.toTeleport(x, y, map, false);
		// mi.readDrop(map);
		AiThread.append(mi);

		if (boss) {
			mi.setBoss(true);
			BossController.appendBossList(mi);

			if (mon instanceof Faust_Ghost && Lineage_Balance.faust_spawn_msg) {
				World.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 780, "\\fY" + Util.getMapName(mi) + " " + mi.getMonster().getName()));
			} else if (mon instanceof Oman_Monster && Lineage_Balance.grimreaper_spawn_msg) {
				World.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 780, "\\fY" + Util.getMapName(mi) + " " + mi.getMonster().getName()));
			} else if (mon instanceof Grimreaper && Lineage_Balance.oman_spawn_msg) {
				World.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 780, "\\fY" + Util.getMapName(mi) + " " + mi.getMonster().getName()));
			}
		}

		return true;
	}

	static public boolean toSpawnMonster2(Monster monster, int x, int y, int map, int heading, boolean boss, MonsterInstance mon) {
		// 버그 방지
		if (monster == null || map < 0)
			return false;

		// 스폰처리.
		MonsterInstance mi = newInstance(monster);

		if (mi == null)
			return false;
		

		
		if (mi.getMonster().isHaste() || mi.getMonster().isBravery()) {
			if (mi.getMonster().isHaste())
				mi.setSpeed(1);
			if (mi.getMonster().isBravery())
				mi.setBrave(true);
		}
		
		mi.setHeading(heading);
		mi.setReSpawnTime(0);
		mi.setHomeX(x);
		mi.setHomeY(y);
		mi.setHomeMap(map);
		mi.toTeleport(x, y, map, false);
//		mi.readDrop(map);
		AiThread.append(mi);
		
		if (boss) {
			mi.setBoss(true);
			BossController.appendBossList(mi);
			
			if (mon instanceof Faust_Ghost && Lineage_Balance.faust_spawn_msg) {
				World.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 780, "\\fY" + Util.getMapName(mi) + " " + mi.getMonster().getName()));
			} else if (mon instanceof Oman_Monster && Lineage_Balance.grimreaper_spawn_msg) {
				World.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 780, "\\fY" + Util.getMapName(mi) + " " + mi.getMonster().getName()));
			} else if (mon instanceof Grimreaper && Lineage_Balance.oman_spawn_msg) {
				World.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 780, "\\fY" + Util.getMapName(mi) + " " + mi.getMonster().getName()));
			}		
		}
		
		return true;
	}
	
	/**
	 * 중복코드 방지용 : gui 기능에서 사용중 lineage.gui.dialog.MonsterSpawn.step4()
	 */
	static public void toSpawnMonster(MonsterSpawnlist ms, Map map, boolean isBoss) {
		// 버그 방지
		if (ms == null || ms.getMap().size() <= 0)
			return;
		// 맵 확인.
		if (map == null) {
			if (ms.getMap().size() > 1)
				map = World.get_map(ms.getMap().get(Util.random(0, ms.getMap().size() - 1)));
			else
				map = World.get_map(ms.getMap().get(0));
		}
		if (map == null)
			return;
		// 스폰처리.
		for (int i = 0; i < ms.getCount(); ++i) {
			MonsterInstance mi = newInstance(ms.getMonster());
			if (mi == null)
				return;
			if (mi.getMonster().isHaste() || mi.getMonster().isBravery()) {
				if (mi.getMonster().isHaste())
					mi.setSpeed(1);
				if (mi.getMonster().isBravery())
					mi.setBrave(true);
			}
			if (i == 0)
				mi.setMonsterSpawnlist(ms);

			mi.setDatabaseKey(Integer.valueOf(ms.getUid()));
			if (ms.isGroup()) {
				mi.setGroupMaster(mi);
				// 마스터 스폰.
				toSpawnMonster(mi, map, ms.isRandom(), ms.getX(), ms.getY(), map.mapid, ms.getLocSize(), ms.getReSpawn(), ms.getReSpawnMax(), true, true);
				// 관리객체 생성.
				for (MonsterGroup mg : ms.getListGroup()) {
					for (int j = mg.getCount(); j > 0; --j) {
						MonsterInstance a = newInstance(mg.getMonster());
						if (a != null) {
							// 스폰
							toSpawnMonster(mi, map, ms.isRandom(), ms.getX(), ms.getY(), map.mapid, ms.getLocSize(), ms.getReSpawn(), ms.getReSpawnMax(), true, true);
							// 마스터관리 목록에 등록.
							mi.getGroupList().add(a);
							// 관리하고있는 마스터가 누군지 지정.
							a.setGroupMaster(mi);
						}
					}
				}
			} else {
				toSpawnMonster(mi, map, ms.isRandom(), ms.getX(), ms.getY(), map.mapid, ms.getLocSize(), ms.getReSpawn(), ms.getReSpawnMax(), true, true);
			}
		}
	}

	/**
	 * 중복 코드를 막기위해 함수로 따로 뺌.
	 */
	static public void toSpawnMonster(MonsterInstance mi, Map m, boolean random, int x, int y, int map, int loc, int respawn, int respawnMax, boolean drop, boolean ai) {
		if (mi == null)
			return;

		int roop_cnt = 0;
		int lx = x;
		int ly = y;
		if (random) {
			// 랜덤 좌표 스폰
			do {
				if (x > 0) {
					lx = Util.random(x - loc, x + loc);
					ly = Util.random(y - loc, y + loc);
				} else {
					lx = Util.random(m.locX1, m.locX2);
					ly = Util.random(m.locY1, m.locY2);
				}
				if (roop_cnt++ > 500) {
					lx = x;
					ly = y;
					break;
				}
			} while (!World.isThroughObject(lx, ly + 1, map, 0) || !World.isThroughObject(lx, ly - 1, map, 4) || !World.isThroughObject(lx - 1, ly, map, 2) || !World.isThroughObject(lx + 1, ly, map, 6)
					|| !World.isThroughObject(lx - 1, ly + 1, map, 1) || !World.isThroughObject(lx + 1, ly - 1, map, 5) || !World.isThroughObject(lx + 1, ly + 1, map, 7) || !World.isThroughObject(lx - 1, ly - 1, map, 3)
					|| World.isNotMovingTile(lx, ly, map));
		} else {
			// 좌표 스폰
			lx = x;
			ly = y;
			if (loc > 1 && x > 0) {
				while (!World.isThroughObject(lx, ly + 1, map, 0) || !World.isThroughObject(lx, ly - 1, map, 4) || !World.isThroughObject(lx - 1, ly, map, 2) || !World.isThroughObject(lx + 1, ly, map, 6)
						|| !World.isThroughObject(lx - 1, ly + 1, map, 1) || !World.isThroughObject(lx + 1, ly - 1, map, 5) || !World.isThroughObject(lx + 1, ly + 1, map, 7)
						|| !World.isThroughObject(lx - 1, ly - 1, map, 3) || World.isNotMovingTile(lx, ly, map)) {
					lx = Util.random(x - loc, x + loc);
					ly = Util.random(y - loc, y + loc);
					if (roop_cnt++ > 500) {
						lx = x;
						ly = y;
						break;
					}
				}
			}
		}

		if (mi.getMonster().isHaste() || mi.getMonster().isBravery()) {
			if (mi.getMonster().isHaste())
				mi.setSpeed(1);
			if (mi.getMonster().isBravery())
				mi.setBrave(true);
		}

		mi.setReSpawnTime(Util.random(respawn, respawnMax));
		mi.setHomeX(lx);
		mi.setHomeY(ly);
		mi.setHomeLoc(loc);
		mi.setHomeMap(map);
		mi.toTeleport(lx, ly, map, false);

		if (mi.getInventory() != null) {
			for (ItemInstance ii : mi.getInventory().getList()) {
				ItemDatabase.setPool(ii);
			}
			mi.getInventory().clearList();
		}

		if (ai)
			AiThread.append(mi);

		if (respawn > 0)
			World.appendMonster(mi);
	}

	static public MonsterInstance newInstance(Monster m) {
		MonsterInstance mi = null;

		if (m != null) {

			switch (m.getNameIdNumber()) {
       	   	case 936: //토끼
            case 256: //개구리
	        case 930: //사슴
			   mi = Deer.clone(getPool(Deer.class), m);
			   break;
			case 952:	// 오리
			   mi = Duck.clone(getPool(Duck.class), m);
			   break;
			case 927:	// 돼지
			   mi = Pig.clone(getPool(Pig.class), m);
			   break;
			case 928:	// 암닭
			   mi = Hen.clone(getPool(Hen.class), m);
			   break;
			case 929:	// 젖소
			   mi = Milkcow.clone(getPool(Milkcow.class), m);
			   break;
			case 331:	// 네크로맨서[완]
			   mi = Necromancer.clone(getPool(Necromancer.class), m);
			   break;
			case 335:	// 발터자르
			   mi = Balthazar.clone(getPool(Balthazar.class), m);
			   break;
			case 336:	// 카스파
			   mi = Kaspar.clone(getPool(Kaspar.class), m);
			   break;
			case 337:	// 메르키오르
			   mi = Nancy.clone(getPool(Nancy.class), m);
			   break;
			case 338:	// 세마
			   mi = Sema.clone(getPool(Sema.class), m);
			   break;
			case 371:	// 데스나이트[완]
			   mi = Knight.clone(getPool(Knight.class), m);
			   break;
			case 306:	// 바포메트[완]
				mi = Baphomet.clone(getPool(Baphomet.class), m);
				break;
			case 945:	// 베레스[완]
			   mi = Perez.clone(getPool(Perez.class), m);
			   break;
			case 274:	// 커츠[완]
			   mi = Kouts.clone(getPool(Kouts.class), m);
			   break;
			case 1175:	// 데몬[완]
			   mi = Demon.clone(getPool(Demon.class), m);
			   break;
			case 992:	// 흑장로[완]
			   mi = Elder.clone(getPool(Elder.class), m);
			   break; 
			case 1569:	// 피닉스[완]
			   mi = Phoenix.clone(getPool(Phoenix.class), m);
			   break;
			case 1567:	// 이프리트[완]
			   mi = Ifrit.clone(getPool(Ifrit.class), m);
			   break;
			case 1116:	// 안타라스
				mi = Antharas.clone(getPool(Antharas.class), m);
				break;
			case 6: // 괴물 눈
				mi = FloatingEye.clone(getPool(FloatingEye.class), m);
				break;
			case 7: // 해골
				mi = Skeleton.clone(getPool(Skeleton.class), m);
				break;
			case 8: // 슬라임
				mi = Slime.clone(getPool(Slime.class), m);
				break;
			case 56: // 돌골렘
				mi = StoneGolem.clone(getPool(StoneGolem.class), m);
				break;
			case 57: // 좀비
				mi = Zombie.clone(getPool(Zombie.class), m);
				break;
			case 268: // 늑대
			case 904: // 세인트버나드
			case 905: // 도베르만
			case 4072: // 아기진돗개
			case 4073: // 진돗개
			case 4079: // 아기 캥거루
			case 4078: // 공포의판다곰
			case 4080: // 불꽃의 캥거루
			case 4077: // 아기 판다곰
			case 906: // 콜리
			case 907: // 세퍼드
			case 908: // 비글
			case 1397: // 여우
			case 1495: // 곰
			case 1788: // 허스키
			case 2563: // 열혈토끼
			case 2701: // 고양이
			case 3041: // 호랑이
			case 3508: // 라쿤
				mi = Wolf.clone(getPool(Wolf.class), m);
				break;
			case 318: // 스파토이
				mi = Spartoi.clone(getPool(Spartoi.class), m);
				break;
			case 319: // 웅골리언트
				mi = ArachnevilElder.clone(getPool(ArachnevilElder.class), m);
				break;
			case 325: // 버그베어
				mi = Bugbear.clone(getPool(Bugbear.class), m);
				break;
			case 494: // 아투바 오크
				mi = AtubaOrc.clone(getPool(AtubaOrc.class), m);
				break;
			case 495: // 네루가 오크
				mi = NerugaOrc.clone(getPool(NerugaOrc.class), m);
				break;
			case 496: // 간디 오크
				mi = GandiOrc.clone(getPool(GandiOrc.class), m);
				break;
			case 497: // 로바 오크
				mi = RovaOrc.clone(getPool(RovaOrc.class), m);
				break;
			case 498: // 두다-마라 오크
				mi = DudaMaraOrc.clone(getPool(DudaMaraOrc.class), m);
				break;
			case 758: // 브롭
				mi = Blob.clone(getPool(Blob.class), m);
				break;
			case 1041: // 오크좀비
				mi = OrcZombie.clone(getPool(OrcZombie.class), m);
				break;
			case 1042: // 다크엘프
				mi = DarkElf.clone(getPool(DarkElf.class), m);
				break;
			case 1046: // 그렘린
				mi = Gremlin.clone(getPool(Gremlin.class), m);
				break;

			case 1428: // 라미아
				mi = Ramia.clone(getPool(Ramia.class), m);
				break;
			case 1554: // 도펠갱어
				mi = Doppelganger.clone(getPool(Doppelganger.class), m);
				break;
			case 1571: // 폭탄꽃
				mi = BombFlower.clone(getPool(BombFlower.class), m);
				break;
			case 1176: // 그리폰
			case 959: // 하피
				mi = Harphy.clone(getPool(Harphy.class), m);
				break;
			case 2017: // 다크마르
				mi = Darkmar.clone(getPool(Darkmar.class), m);
				break;
			case 2020: // 언데드의 배신자
				mi = BetrayerOfUndead.clone(getPool(BetrayerOfUndead.class), m);
				break;
			case 2063: // 잭-O-랜턴
			case 2064: // 잭-0-랜턴
				mi = JackLantern.clone(getPool(JackLantern.class), m);
				break;
			case 2073: // 변종 거대 여왕 개미
				mi = MutantGiantQueenAnt.clone(getPool(MutantGiantQueenAnt.class), m);
				break;
			case 2219: // 배신당한 오크대장
				mi = BetrayedOrcChief.clone(getPool(BetrayedOrcChief.class), m);
				break;
			case 2488:
				mi = Unicorn.clone(getPool(Unicorn.class), m);
				break;
			case 7444: // 파우스트의 악령
				mi = Faust_Ghost.clone(getPool(Faust_Ghost.class), m);
				break;
			case 19883: // 오만의 탑 몬스터
			case 19884:
			case 19885:
			case 19886:
			case 19887:
			case 19889:
			case 19890:
			case 19891:
			case 19892:
			case 19893:
			case 19895:
			case 19896:
			case 19897:
			case 19898:
			case 19899:
			case 19900:
			case 19902:
			case 19903:
			case 19904:
			case 19905:
			case 19906:
			case 19908:
			case 19909:
			case 19910:
			case 19911:
			case 19912:
			case 19913:
			case 19915:
			case 19916:
			case 19917:
			case 19918:
			case 19920:
			case 19921:
			case 19922:
			case 19923:
			case 19925:
			case 19926:
			case 19927:
			case 19928:
			case 19930:
			case 19931:
			case 19932:
			case 19933:
			case 19935:
			case 19936:
			case 19937:
			case 19938:
			case 19940:
			case 19941:
			case 19942:
			case 19943:
			case 19944:
			case 19945:
			case 19946:
			case 19947:
			case 19948:
				mi = Oman_Monster.clone(getPool(Oman_Monster.class), m);
				break;
			case 12410: // 감시자 리퍼
				mi = Grimreaper.clone(getPool(Grimreaper.class), m);
				break;

			default:
				mi = MonsterInstance.clone(getPool(MonsterInstance.class), m);
				break;
			}
			mi.setObjectId(ServerDatabase.nextEtcObjId());
			mi.setGfx(m.getGfx());
			mi.setGfxMode(m.getGfxMode());
			mi.setClassGfx(m.getGfx());
			mi.setClassGfxMode(m.getGfxMode());
			mi.setName(m.getNameId());
			mi.setLevel(m.getLevel());
			mi.setExp(m.getExp());
			mi.setMaxHp(m.getHp());
			mi.setMaxMp(m.getMp());
			mi.setNowHp(m.getHp());
			mi.setNowMp(m.getMp());
			mi.setLawful(m.getLawful());
			mi.setStr(mi.getLevel() / 2 < 25 ? 25 : mi.getLevel() / 2);
			mi.setDex(mi.getLevel() / 2 < 25 ? 25 : mi.getLevel() / 2);
			mi.setInt(mi.getLevel() / 2 < 25 ? 25 : mi.getLevel() / 2);
			mi.setCon(m.getCon());
			mi.setWis(m.getWis());
			mi.setCha(m.getCha());
			mi.setEarthress(m.getResistanceEarth());
			mi.setFireress(m.getResistanceFire());
			mi.setWindress(m.getResistanceWind());
			mi.setWaterress(m.getResistanceWater());
			mi.setAiStatus(Lineage.AI_STATUS_WALK);
		}

		return mi;
	}

	static public void changeMonsterRenew(MonsterInstance mi, Monster m) {

		newInstance(mi, m);
		mi.setMonster(m);
		mi.clearExpList();
		mi.setGfxMode(0);
		mi.toSender(S_ObjectGfx.clone(BasePacketPooling.getPool(S_ObjectGfx.class), mi), false);
		mi.toSender(S_ObjectName.clone(BasePacketPooling.getPool(S_ObjectName.class), mi), false);
	}

	static public void changeMonster(MonsterInstance mi, Monster m) {

		newInstance(mi, m);
		mi.setMonster(m);
		mi.readDrop();
		mi.clearExpList();
		mi.setGfxMode(0);
		mi.toSender(S_ObjectGfx.clone(BasePacketPooling.getPool(S_ObjectGfx.class), mi), false);
		mi.toSender(S_ObjectName.clone(BasePacketPooling.getPool(S_ObjectName.class), mi), false);
	}

	/**
	 * 파우스트
	 * 
	 * @param mi
	 * @param m
	 */
	static public void changeMonsterBoss(MonsterInstance mi, Monster m) {

		newInstance(mi, m);
		mi.setMonster(m);
		mi.readDrop();
		mi.clearExpList();
		mi.setGfxMode(0);
		mi.setBoss(true);
		BossController.appendBossList(mi);
		if (Lineage_Balance.faust_spawn_msg) {
			World.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 780, "\\fY" + Util.getMapName(mi) + " " + mi.getMonster().getName()));
		}
		mi.toSender(S_ObjectGfx.clone(BasePacketPooling.getPool(S_ObjectGfx.class), mi), false);
		mi.toSender(S_ObjectName.clone(BasePacketPooling.getPool(S_ObjectName.class), mi), false);
	}

	static private MonsterInstance newInstance(MonsterInstance mi, Monster m) {
		mi.setObjectId(mi.getObjectId() == 0 ? ServerDatabase.nextNpcObjId() : mi.getObjectId());
		mi.setGfx(m.getGfx());
		mi.setGfxMode(m.getGfxMode());
		mi.setClassGfx(m.getGfx());
		mi.setClassGfxMode(m.getGfxMode());
		mi.setName(m.getNameId());
		mi.setLevel(m.getLevel());
		mi.setExp(m.getExp());
		mi.setMaxHp(m.getHp());
		mi.setMaxMp(m.getMp());
		mi.setNowHp(m.getHp());
		mi.setNowMp(m.getMp());
		mi.setLawful(m.getLawful());
		mi.setEarthress(m.getResistanceEarth());
		mi.setFireress(m.getResistanceFire());
		mi.setWindress(m.getResistanceWind());
		mi.setWaterress(m.getResistanceWater());

		return mi;
	}

	static private MonsterInstance getPool(Class<?> c) {
		synchronized (pool) {
			MonsterInstance mon = null;
			for (MonsterInstance mi : pool) {
				if (mi.getClass().equals(c)) {
					mon = mi;
					break;
				}
			}
			if (mon != null)
				pool.remove(mon);
			return mon;
		}
	}

	static public void setPool(MonsterInstance mi) {
		mi.close();
		synchronized (pool) {
			if (!pool.contains(mi))
				pool.add(mi);
		}

		// lineage.share.System.println(MonsterSpawnlistDatabase.class.toString()+"
		// : pool.add("+pool.size()+")");
	}

	static public int getPoolSize() {
		return pool.size();
	}

	static public void insert(Connection con, String name, String monster, boolean random, int count, int loc_size, int x, int y, int map, int re_spawn, boolean groups, 
			String monster_1, int monster_1_count, String monster_2, int monster_2_count, 
			String monster_3, int monster_3_count, String monster_4, int monster_4_count
		){
		PreparedStatement st = null;
		int uid = getUid(con);
		
		try {
			st = con.prepareStatement("INSERT INTO monster_spawnlist SET uid=?, name=?, monster=?, random=?, count=?, loc_size=?, spawn_x=?, spawn_y=?, spawn_map=?, re_spawn_min=?, re_spawn_max=?, groups=?, monster_1=?, monster_1_count=?, monster_2=?, monster_2_count=?, monster_3=?, monster_3_count=?, monster_4=?, monster_4_count=?");
			st.setInt(1, uid);
			st.setString(2, name);
			st.setString(3, monster);
			st.setString(4, String.valueOf(random));
			st.setInt(5, count);
			st.setInt(6, loc_size);
			st.setInt(7, x);
			st.setInt(8, y);
			st.setInt(9, map);
			st.setInt(10, re_spawn);
			st.setInt(11, re_spawn);
			st.setString(12, String.valueOf(groups));
			st.setString(13, monster_1);
			st.setInt(14, monster_1_count);
			st.setString(15, monster_2);
			st.setInt(16, monster_2_count);
			st.setString(17, monster_3);
			st.setInt(18, monster_3_count);
			st.setString(19, monster_4);
			st.setInt(20, monster_4_count);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : insert()\r\n", MonsterSpawnlistDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}

	static public int getUid(Connection con) {
		PreparedStatement st = null;
		ResultSet rs = null;
		int uid = 0;

		try {
			st = con.prepareStatement("SELECT * FROM monster_spawnlist");
			rs = st.executeQuery();
			while (rs.next()) {
				int temp = rs.getInt("uid");

				if (uid < temp)
					uid = temp;
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : getUid(Connection con)\r\n", MonsterSpawnlistDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		return uid + 1;
	}

	/**
	 * 
	 * 어느 맵에 스폰되어있는지 확인해주는 함수
	 * 
	 * @param map
	 */

	static public MonsterInstance find(String name) {
		synchronized (list) {
			for (MonsterInstance m : list) {
				if (m.getMonster().getName().equalsIgnoreCase(name))
					return m;
			}
			return null;
		}
	}

	public static void reload(int mapId) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;

		for (MonsterInstance mon : World.getMonsterList()) {
			if (mon.getMap() == mapId && !mon.isBoss()) {
				World.removeMonster(mon);
				mon.toAiThreadDelete();
				mon.close();
			}
		}

		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM monster_spawnlist WHERE spawn_map=?");
			st.setInt(1, mapId);
			rs = st.executeQuery();

			while (rs.next()) {
				Monster m = MonsterDatabase.find(rs.getString("monster"));

				if (m != null) {

					if (temp.size() < 1) {
						temp.add(m);
					} else {
						boolean result = true;

						for (int i = 0; i < temp.size(); i++) {
							if (temp.get(i).getName().equalsIgnoreCase(m.getName())) {
								result = false;
								break;
							}
						}

						if (result)
							temp.add(m);
					}
					MonsterSpawnlist ms = new MonsterSpawnlist();
					ms.setUid(rs.getInt("uid"));
					ms.setName(rs.getString("name"));
					ms.setMonster(m);
					ms.setRandom(rs.getString("random").equalsIgnoreCase("true"));
					ms.setCount(rs.getInt("count"));
					ms.setLocSize(rs.getInt("loc_size"));
					ms.setX(rs.getInt("spawn_x"));
					ms.setY(rs.getInt("spawn_y"));
					StringTokenizer stt = new StringTokenizer(rs.getString("spawn_map"), "|");
					while (stt.hasMoreTokens()) {
						String map = stt.nextToken();
						if (map.length() > 0)
							ms.getMap().add(Integer.valueOf(map));
					}
					ms.setReSpawn(rs.getInt("re_spawn_min") * 1000);
					if (rs.getInt("re_spawn_max") < rs.getInt("re_spawn_min"))
						ms.setReSpawnMax(rs.getInt("re_spawn_min") * 1000);
					else
						ms.setReSpawnMax(rs.getInt("re_spawn_max") * 1000);
					ms.setGroup(rs.getString("groups").equalsIgnoreCase("true"));
					if (ms.isGroup()) {
						Monster g1 = MonsterDatabase.find(rs.getString("monster_1"));
						Monster g2 = MonsterDatabase.find(rs.getString("monster_2"));
						Monster g3 = MonsterDatabase.find(rs.getString("monster_3"));
						Monster g4 = MonsterDatabase.find(rs.getString("monster_4"));
						if (g1 != null)
							ms.getListGroup().add(new MonsterGroup(g1, rs.getInt("monster_1_count")));
						if (g2 != null)
							ms.getListGroup().add(new MonsterGroup(g2, rs.getInt("monster_2_count")));
						if (g3 != null)
							ms.getListGroup().add(new MonsterGroup(g3, rs.getInt("monster_3_count")));
						if (g4 != null)
							ms.getListGroup().add(new MonsterGroup(g4, rs.getInt("monster_4_count")));
					}
					toSpawnMonster(ms, null);
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : reload(int map)\r\n", MonsterSpawnlistDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}

	public static void reload() {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;

		for (MonsterInstance mon : World.getMonsterList()) {
			if (!mon.isBoss()) {
				World.removeMonster(mon);
				mon.toAiThreadDelete();
				mon.close();
			}
		}

		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM monster_spawnlist");
			rs = st.executeQuery();

			while (rs.next()) {
				Monster m = MonsterDatabase.find(rs.getString("monster"));

				if (m != null) {

					if (temp.size() < 1) {
						temp.add(m);
					} else {
						boolean result = true;

						for (int i = 0; i < temp.size(); i++) {
							if (temp.get(i).getName().equalsIgnoreCase(m.getName())) {
								result = false;
								break;
							}
						}

						if (result)
							temp.add(m);
					}
					MonsterSpawnlist ms = new MonsterSpawnlist();
					ms.setUid(rs.getInt("uid"));
					ms.setName(rs.getString("name"));
					ms.setMonster(m);
					ms.setRandom(rs.getString("random").equalsIgnoreCase("true"));
					ms.setCount(rs.getInt("count"));
					ms.setLocSize(rs.getInt("loc_size"));
					ms.setX(rs.getInt("spawn_x"));
					ms.setY(rs.getInt("spawn_y"));
					StringTokenizer stt = new StringTokenizer(rs.getString("spawn_map"), "|");
					while (stt.hasMoreTokens()) {
						String map = stt.nextToken();
						if (map.length() > 0)
							ms.getMap().add(Integer.valueOf(map));
					}

					ms.setReSpawn(rs.getInt("re_spawn_min") * 1000);

					if (rs.getInt("re_spawn_max") < rs.getInt("re_spawn_min"))
						ms.setReSpawnMax(rs.getInt("re_spawn_min") * 1000);
					else
						ms.setReSpawnMax(rs.getInt("re_spawn_max") * 1000);
					ms.setGroup(rs.getString("groups").equalsIgnoreCase("true"));
					if (ms.isGroup()) {
						Monster g1 = MonsterDatabase.find(rs.getString("monster_1"));
						Monster g2 = MonsterDatabase.find(rs.getString("monster_2"));
						Monster g3 = MonsterDatabase.find(rs.getString("monster_3"));
						Monster g4 = MonsterDatabase.find(rs.getString("monster_4"));
						if (g1 != null)
							ms.getListGroup().add(new MonsterGroup(g1, rs.getInt("monster_1_count")));
						if (g2 != null)
							ms.getListGroup().add(new MonsterGroup(g2, rs.getInt("monster_2_count")));
						if (g3 != null)
							ms.getListGroup().add(new MonsterGroup(g3, rs.getInt("monster_3_count")));
						if (g4 != null)
							ms.getListGroup().add(new MonsterGroup(g4, rs.getInt("monster_4_count")));
					}
					toSpawnMonster(ms, null);
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : reload(int map)\r\n", MonsterSpawnlistDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}
}