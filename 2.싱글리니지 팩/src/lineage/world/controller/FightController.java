package lineage.world.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import lineage.bean.database.Item;
import lineage.bean.database.Monster;
import lineage.bean.database.Npc;
import lineage.database.AccountDatabase;
import lineage.database.DatabaseConnection;
import lineage.database.ItemDatabase;
import lineage.database.MonsterDatabase;
import lineage.database.ServerDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_Inventory;
import lineage.network.packet.server.S_LetterNotice;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectAttack;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectHitratio;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.thread.AiThread;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.ShopInstance;
import lineage.world.object.item.RaceTicket;
import lineage.world.object.npc.background.BattleRoyalTeamLine;


public final class FightController {
	static private STATUS 현재상태;
	// 나비켓 race_log 테이블의 type
	static private String raceType = "fight";
	static private String html = "fight";
	static private String cmdString = "투견";
	
	static private enum STATUS {
		휴식, 다음진행대기, 준비, 진행중, 결과,
	}
	
	static private String[] 투기몬스터종류;
	static private List<MonsterInstance> 투기몬스터목록;
	
	static private final int 다음진행대기시간 = 30;	// 초단위
	static private final int 대기시간멘트 = 20;			// 초단위
	static private final int 시체처리대기시간 = 10;		// 초단위
	
	static private object 투기장도우미NPC;
	static private String 투기장도우미이름 = "투견 도우미";
	// 기본 위치
//	static private final int 투기장도우미_X = 33531;
//	static private final int 투기장도우미_Y = 32861;
	static private final int 투기장도우미_X = 33443;
	static private final int 투기장도우미_Y = 32826;
	static private final int 투기장도우미_MAP = 4;
	
	static private object 투기장표판매원NPC;
	static private String 투기장표판매원이름 = "투견 표판매원";
	// 기본 위치
//	static private final int 투기장표판매원_X = 33536;
//	static private final int 투기장표판매원_Y = 32864;
	static private final int 투기장표판매원_X = 33442;
	static private final int 투기장표판매원_Y = 32822;
	static private final int 투기장표판매원_MAP = 4;
	
	static private int now_uid;
	static private int now_buyCount;

	static private final String ticketName = "레이스 표";
	static private final int 표판매갯수_1 = 1;
	static private final int 표판매갯수_2 = 1000;
	static private final int 표판매갯수_3 = 5000;
	static private final int 표판매갯수_4 = 10000;

	static private int game_time;

	static private long 표판매_우승견;
	static private long 운영자_설정_우승견;
	
	// 투견장 울타리 리스트
	public static List<BattleRoyalTeamLine> 울타리_리스트;
	// 울타리 사용여부
	// true: 사용   /  false: 사용안함
	static private final boolean 울타리사용여부 = false;
	// 투견장 울타리
	static private final int 울타리_X1 = 33445;
	static private final int 울타리_Y1 = 32831;
	
	static private final int 울타리_X2 = 33455;
	static private final int 울타리_Y2 = 32788;
	
	static private final int 울타리_MAP = 4;	
		
	// 기본위치
//	static private final int 홀_X = 33529;
//	static private final int 홀_Y = 32864; 
//	static private final int 짝_X = 33529;
//	static private final int 짝_Y = 32866;
	
	static private final int 홀_X = 33441;
	static private final int 홀_Y = 32827;
	static private final int 짝_X = 33441;
	static private final int 짝_Y = 32829;
	
	static private object 투기장버프사NPC;
	static private String 투기장버프사이름 = "버프사";
	// 기본 위치
//	static private final int 투기장버프사_X = 33527;
//	static private final int 투기장버프사_Y = 32861;
	static private final int 투기장버프사_X = 33443;
	static private final int 투기장버프사_Y = 32829;
	static private final int 투기장버프사_MAP = 4;
	static private int 투기장버프사_스폰횟수 = 0;
	static private final int 투기장버프사_최대스폰횟수 = 6;
	static private int 투기장버프사_카운트 = 0;
	
	static public void init() {
		TimeLine.start("FightController..");

		현재상태 = STATUS.휴식;

		투기몬스터종류 = new String[] { "늑대","진돗개","아기진돗개", "도베르만", "세퍼드", "비글", "허스키", "콜리", "여우", "호랑이", "누렁이"};
	//	투기몬스터종류 = new String[] { "데스나이트", "바포메트", "감시자 리퍼", "베레스", "네크로멘서", "오우거", "다크엘프"};

//		투기몬스터목록 = new ArrayList<MonsterInstance>();
//		투기몬스터목록.add(투기장몬스터.clone(null, MonsterDatabase.find("늑대")));
//		투기몬스터목록.add(투기장몬스터.clone(null, MonsterDatabase.find("늑대")));
//
//		투기장도우미NPC = new 투기장도우미();
//		투기장도우미NPC.setObjectId(ServerDatabase.nextEtcObjId());
//		투기장도우미NPC.setName(투기장도우미이름);
//		투기장도우미NPC.setGfx(1837);
//		투기장도우미NPC.setHomeX(투기장도우미_X);
//		투기장도우미NPC.setHomeY(투기장도우미_Y);
//		투기장도우미NPC.setHomeMap(투기장도우미_MAP);
//		투기장도우미NPC.setHeading(5);
//		투기장도우미NPC.setTitle("");
//		투기장도우미NPC.toTeleport(투기장도우미NPC.getHomeX(), 투기장도우미NPC.getHomeY(), 투기장도우미NPC.getHomeMap(), false);
//
//		투기장표판매원NPC = new 투기장표판매원(null);
//		투기장표판매원NPC.setObjectId(ServerDatabase.nextEtcObjId());
//		투기장표판매원NPC.setName(투기장표판매원이름);
//		투기장표판매원NPC.setGfx(1296);
//		투기장표판매원NPC.setHomeX(투기장표판매원_X);
//		투기장표판매원NPC.setHomeY(투기장표판매원_Y);
//		투기장표판매원NPC.setHomeMap(투기장표판매원_MAP);
//		투기장표판매원NPC.setHeading(5);
//		투기장표판매원NPC.setTitle("");
//		투기장표판매원NPC.toTeleport(투기장표판매원NPC.getHomeX(), 투기장표판매원NPC.getHomeY(), 투기장표판매원NPC.getHomeMap(), false);
//		
//		투기장버프사NPC = new 투기장버프사();
//		투기장버프사NPC.setObjectId(ServerDatabase.nextEtcObjId());
//		투기장버프사NPC.setName(투기장버프사이름);
//		투기장버프사NPC.setGfx(782);
//		투기장버프사NPC.setHomeX(투기장버프사_X);
//		투기장버프사NPC.setHomeY(투기장버프사_Y);
//		투기장버프사NPC.setHomeMap(투기장버프사_MAP);
//		투기장버프사NPC.setHeading(3);
//		투기장버프사NPC.setTitle("");
//		
		// 마지막 진행경기 uid 동기화.
		now_uid = getLastUid() + 1;
		
		if (울타리사용여부) {
			spawnFence();
		}
		
		TimeLine.end();
	}
	
	/**
	 * 투기장 초기화.
	 * 2019-10-29
	 * by connector12@nate.com
	 */
	static private void initFighter() {
		String temp = null;
		표판매_우승견 = 0;
		운영자_설정_우승견 = 0;
		투기장버프사_스폰횟수 = 0;
		투기장버프사_카운트 = 0;
		
		for (int i = 0; i < 2; ++i) {
			Monster m = null;

			while (true) {
				m = MonsterDatabase.find(투기몬스터종류[Util.random(0, 투기몬스터종류.length - 1)]);

				if (m == null)
					continue;

				if (i == 0)
					temp = m.getName();
				
				if (temp == null)
					continue;

				if (i > 0 && temp.equalsIgnoreCase(m.getName()))
					continue;

				break;
			}

			투기장몬스터 monster = (투기장몬스터) 투기몬스터목록.get(i);
			monster.idx = i;
			monster.rate = Lineage.fight_rate;
			monster.승률 = Util.random(40, 55);
			monster.buyCnt = 0;

			try {
				monster.toReset(false);
				monster.setDead(false);
				//monster.setMonster(m);
				monster.name = m.getName();
				monster.setGfx(m.getGfx());
				monster.setGfxMode(m.getGfxMode());
				monster.setClassGfx(m.getGfx());
				monster.setClassGfxMode(m.getGfxMode());
				monster.setName(m.getName());
				monster.setLevel(5);
				monster.setAc(0);
				monster.setExp(0);
				monster.setMaxHp(150);
				monster.setNowHp(150);
				monster.setMaxMp(0);
				monster.setNowMp(0);
				monster.setLawful(Lineage.NEUTRAL);
				monster.setStr(0);
				monster.setDex(0);
				monster.setInt(0);
				monster.setDynamicStr(0);
				monster.setDynamicDex(0);
				monster.setDynamicInt(0);
				monster.setEarthress(0);
				monster.setFireress(0);
				monster.setWindress(0);
				monster.setWaterress(0);
				monster.setDynamicAddDmg(0);
				monster.setAiStatus(Lineage.AI_STATUS_WALK);
				monster.setSpeed(1);
				monster.setBrave(true);
			} catch (Exception e) {
				i -= 1;
				continue;
			}

//			if (i == 0) {
//				monster.setHeading(4);
//				monster.toTeleport(홀_X, 홀_Y, 투기장도우미_MAP, false);
//				monster.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), monster, Lineage.doll_teleport_effect), false);
//			} else {
//				monster.setHeading(0);
//				monster.toTeleport(짝_X, 짝_Y, 투기장도우미_MAP, false);
//				monster.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), monster, Lineage.doll_teleport_effect), false);
//			}
		}	
	}
	
	static public boolean isCommand(String cmd) {
		if (cmd.equalsIgnoreCase(Lineage.command + cmdString))
			return true;
		
		return false;
	}
	
	static public Object toCommand(object o, String cmd, StringTokenizer st) {
		if (cmd.equalsIgnoreCase(Lineage.command + cmdString)) {
			toFightCommand(o, cmd, st);
			return true;
		}
		return null;
	}
	
	static public void toFightCommand(object o, String cmd, StringTokenizer st) {
		try {
			String type = st.nextToken();

			if (o.getGm() > 0) {
				if (type.equalsIgnoreCase("시작")) {
					시작(o, st);
				} else if (type.equalsIgnoreCase("종료")) {
					종료(o);
				} else if (type.equalsIgnoreCase("정보")) {
					정보(o, st);
				} else if (type.equalsIgnoreCase("조작")) {
					조작(o, st);
				} else {
					투견족보(o);
				}
			} else {
				투견족보(o);
			}
		} catch (Exception e) {
			투견족보(o);
			
			if (o.getGm() > 0) {
				ChattingController.toChatting(o, Lineage.command + cmdString + " 시작 대기시간(초)", Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(o, Lineage.command + cmdString + " 종료", Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(o, Lineage.command + cmdString + " 정보 회차", Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(o, Lineage.command + cmdString + " 조작 홀/짝", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}
	
	static public void 투견족보(object o) {
		if (!Lineage.is_fight)
			return;
		
		String[] arrayOfString = new String[4];
		arrayOfString[0] = new StringBuilder().append(투기장도우미이름).toString();
		arrayOfString[1] = "* 투 견 족 보 *";
		StringBuilder str = new StringBuilder();

		int limit = 38;
		List<String> temp = new ArrayList<String>();
		Connection con = null;
		PreparedStatement stt = null;
		ResultSet rs = null;
		
		try {
			con = DatabaseConnection.getLineage();
			stt = con.prepareStatement("SELECT * FROM race_log WHERE type=? ORDER BY uid DESC LIMIT " + limit);
			stt.setString(1, raceType);
			rs = stt.executeQuery();
			while (rs.next()) {
				if (rs.getInt("uid") < 10)
					temp.add(String.format("  %d회차:[%s] ", rs.getInt("uid"), rs.getInt("race_idx") == 0 ? "홀" : "짝"));
				else if (rs.getInt("uid") < 100)
					temp.add(String.format(" %d회차:[%s] ", rs.getInt("uid"), rs.getInt("race_idx") == 0 ? "홀" : "짝"));
				else
					temp.add(String.format("%d회차:[%s] ", rs.getInt("uid"), rs.getInt("race_idx") == 0 ? "홀" : "짝"));
			}
		} catch (Exception e) {
		} finally {
			DatabaseConnection.close(con, stt, rs);
		}

		Collections.reverse(temp);
		String[] temp1 = new String[temp.size()];

		try {
			for (int i = 0; i < temp.size(); i++) {
				if (i < 19)
					temp1[i] = temp.get(i) + "\r";
				else
					temp1[i - 19] = temp1[i - 19] + temp.get(i);
			}
			
			for (int i = 0; i < temp1.length; i++) {
				temp1[i] = temp1[i] + "\n";
				str.append(temp1[i]);
			}
		} catch (Exception e) {
		}
		arrayOfString[2] = str.toString();
		o.toSender(S_LetterNotice.clone(BasePacketPooling.getPool(S_LetterNotice.class), arrayOfString));
	}
	
	static public void 시작(object o, StringTokenizer st) {
		try {
			if (현재상태 == STATUS.휴식 || 현재상태 == STATUS.다음진행대기 || 현재상태 == STATUS.준비) {
				Lineage.is_fight = true;
				int count = 다음진행대기시간;

				if (st.hasMoreTokens())
					count = Integer.valueOf(st.nextToken());
				
				if (현재상태 == STATUS.휴식) {
					현재상태 = STATUS.다음진행대기;
					clearFighter();
					initFighter();
				}

				game_time = count;
				ChattingController.toChatting(o, String.format("[%s] %d초 후 시작 설정 완료.", cmdString, count), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				ChattingController.toChatting(o, String.format("[%s] 이미 진행중 입니다.", cmdString), Lineage.CHATTING_MODE_MESSAGE);
			}	
		} catch (Exception e) {
		}
	}
	
	static public void 종료(object o) {
		if (현재상태 == STATUS.휴식 || 현재상태 == STATUS.다음진행대기 || 현재상태 == STATUS.준비) {
			Lineage.is_fight = false;
			현재상태 = STATUS.휴식;
			game_time = 0;
			now_buyCount = 0;
			clearFighter();
			ChattingController.toChatting(o, String.format("[%s] 종료 완료.", cmdString), Lineage.CHATTING_MODE_MESSAGE);
		} else {
			ChattingController.toChatting(o, String.format("[%s] 진행 중이므로 종료할 수 없습니다.", cmdString), Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	static public void 정보(object o, StringTokenizer st) {
		try {
			int uid = 0;

			if (st.hasMoreTokens())
				uid = Integer.valueOf(st.nextToken());

			int price = 0;
			double rate = 0.0D;
			long buyCnt = 0;
			long sellCnt = 0;
			double buyAden = 0.0D;
			double sellAden = 0.0D;

			if (uid == 0) {
				buyCnt = getBuyCnt(uid);
				sellCnt = getSellCnt(uid);
				buyAden = getTotalPrice(true);
				sellAden = getTotalPrice(false);

				String[] arrayOfString = new String[4];
				arrayOfString[0] = new StringBuilder().append(투기장도우미이름).toString();
				arrayOfString[1] = "* 투 견 정 보 *";
				StringBuilder str = new StringBuilder();

				str.append(String.format("    현재[%d]회차 투견 정보\n", now_uid));
				str.append(String.format("전체 표 판매수: %,d장\n", now_buyCount));

				for (MonsterInstance mi : 투기몬스터목록) {
					투기장몬스터 mon = (투기장몬스터) mi;
					str.append(String.format("[%s] 표 판매수: %,d장\n", mi.getName(), mon.buyCnt));
				}
				
				str.append("\n\n");

				str.append("      전체회차 투견 정보\n");
				str.append(String.format("표 판매수: %,d장\n", buyCnt));
				str.append(String.format("표 매입수: %,d장\n", sellCnt));
				str.append(String.format("표 판매액: %,d(%s)\n", (long) buyAden, Lineage.fight_aden));
				str.append(String.format("표 매입액: %,d(%s)\n", (long) sellAden, Lineage.fight_aden));

				arrayOfString[2] = str.toString();
				o.toSender(S_LetterNotice.clone(BasePacketPooling.getPool(S_LetterNotice.class), arrayOfString));
			} else if (uid < now_uid) {
				price = getPrice(uid);
				rate = getRate(uid);
				buyCnt = getBuyCnt(uid);
				sellCnt = getSellCnt(uid);

				String[] arrayOfString = new String[4];
				arrayOfString[0] = new StringBuilder().append(투기장도우미이름).toString();
				arrayOfString[1] = "* 투 견 정 보 *";
				StringBuilder str = new StringBuilder();

				str.append(String.format("      %d회차 투견 정보\n", uid));
				str.append(String.format("표가격: %,d\n", price));
				str.append(String.format("배당: %.2f\n", rate));
				str.append(String.format("표 판매수: %,d장\n", buyCnt));
				str.append(String.format("표 매입수: %,d장\n", sellCnt));
				str.append(String.format("표 판매액: %,d(%s)\n", (long) (price * buyCnt), Lineage.fight_aden));
				str.append(String.format("표 매입액: %,d(%s)\n", (long) (rate * price) * sellCnt, Lineage.fight_aden));

				arrayOfString[2] = str.toString();
				o.toSender(S_LetterNotice.clone(BasePacketPooling.getPool(S_LetterNotice.class), arrayOfString));
			}
		} catch (Exception e) {
		}
	}
	
	static public void 조작(object o, StringTokenizer st) {
		try {
			if(현재상태 == STATUS.결과 || (현재상태 == STATUS.다음진행대기 && game_time == 0)) {
				ChattingController.toChatting(o, String.format("[%s] 진행 중이므로 조작할 수 없습니다.", cmdString), Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			String cmd = st.nextToken();

			if (cmd.equalsIgnoreCase("홀") || cmd.equalsIgnoreCase("짝") || cmd.equalsIgnoreCase("작")) {
				int idx = 0;
				
				switch (cmd) {
				case "작":
				case "짝":
					idx = 1;
					break;
				}
				
				MonsterInstance monster = 투기몬스터목록.get(idx);
				운영자_설정_우승견 = monster.getObjectId();
				ChattingController.toChatting(o, String.format("[%s] %s 설정 완료.", cmdString, monster.getName()), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				ChattingController.toChatting(o, Lineage.command + cmdString + " 조작 홀/짝", Lineage.CHATTING_MODE_MESSAGE);
			}
		} catch (Exception e) {
			ChattingController.toChatting(o, Lineage.command + cmdString + " 조작 홀/짝", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	static public void toTimer(long time) {
		if (Lineage.is_fight) {
			switch (현재상태) {
			case 휴식:
				현재상태 = STATUS.다음진행대기;
				break;
			case 다음진행대기:
				if (game_time == 0) {
					game_time = 다음진행대기시간;
					initFighter();
				}

				if (game_time > 60 && --game_time % 60 == 0)
					toMessage(투기장도우미NPC, String.format("경기시작 %d분 전!", game_time / 60));

				if (game_time <= 60)
					현재상태 = STATUS.준비;
				break;
			case 준비:
				// 1분 대기
				if (game_time % 60 == 0)
					toMessage(투기장도우미NPC, String.format("경기시작 %d분 전!", game_time / 60));
				
				// 10초마다 알림
				if (game_time < 60 && game_time % 10 == 0)
					toMessage(투기장도우미NPC, String.format("경기시작 %d초 전!", game_time));

				// 매초마다 글 표시.
				if (--game_time <= 대기시간멘트)
					toMessage(투기장도우미NPC, String.format("경기시작 %d초 전!", game_time));

				//
				if (game_time <= 1) {
					toMessage(투기장도우미NPC, String.format("%d회차 경기가 시작되었습니다!", now_uid));
					현재상태 = STATUS.진행중;
					game_time = 0;

					int idx = 투기몬스터목록.size() - 1;
					투기몬스터목록.get(0).addAttackList(투기몬스터목록.get(idx));
					투기몬스터목록.get(idx).addAttackList(투기몬스터목록.get(0));

					투기장몬스터 top1 = (투기장몬스터) 투기몬스터목록.get(0);
					투기장몬스터 top2 = (투기장몬스터) 투기몬스터목록.get(idx);
					if (top1.buyCnt > 0 && top2.buyCnt > 0) {
						if (top1.buyCnt > top2.buyCnt)
							표판매_우승견 = top2.getObjectId();
						else if (top1.buyCnt < top2.buyCnt)
							표판매_우승견 = top1.getObjectId();
					}
				}
				break;
			case 진행중:
				// 진행중.
				for (MonsterInstance mi : 투기몬스터목록) {
					if (mi.isDead()) {
						현재상태 = STATUS.결과;
						game_time = 0;
						break;
					}
				}			
				spawnBuff();
				break;
			case 결과:
				// 대기
				if (game_time == 0) {
					game_time = 시체처리대기시간;
					for (MonsterInstance mi : 투기몬스터목록) {
						if (!mi.isDead()) {
							// 이긴 몬스터에게 이팩트.
							mi.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), mi, Util.random(2026, 2027)), false);
							toMessage(투기장도우미NPC, String.format("[%s] 승리 하였습니다!!", mi.getName()));
							// 디비 로그 갱신.
							insertDB((투기장몬스터) mi);
						}
					}
					
					투기장버프사_카운트 = 0;
					투기장버프사NPC.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), 투기장버프사NPC, Lineage.doll_teleport_effect + 1), false);
					World.remove(투기장버프사NPC);
					투기장버프사NPC.clearList(true);
				}

				//
				if (--game_time < 1) {
					현재상태 = STATUS.다음진행대기;
					game_time = 0;
					now_buyCount = 0;
					clearFighter();
				}
				break;
			}
		}
	}
	
	static private void clearFighter() {
		for (MonsterInstance mi : 투기몬스터목록) {
			mi.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), mi, Lineage.doll_teleport_effect + 1), false);
			mi.toReset(true);
			World.remove(mi);
			mi.clearList(true);
		}
		
		투기장버프사_카운트 = 0;
		World.remove(투기장버프사NPC);
		투기장버프사NPC.clearList(true);
	}
	
	static private void toMessage(object o, String msg) {
		ChattingController.toChatting(o, msg, Lineage.CHATTING_MODE_SHOUT);
	}
	
	/**
	 * 투기장 몬스터인지 확인.
	 * 2019-10-29
	 * by connector12@nate.com
	 */
	static public boolean isFightMonster(object o) {
		if (o instanceof 투기장몬스터)
			return true;
		
		return false;
	}
	
	static private class 투기장몬스터 extends MonsterInstance {
		private long buyCnt; // 경기전 누적 판매량
		private int idx; // 투견 번호.
		private double rate; // 배당
		private int 승률;
		public String name;

		static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m) {
			if (mi == null)
				mi = new 투기장몬스터();
			mi.setObjectId(ServerDatabase.nextEtcObjId());
			mi.setMonster(m);
			mi.setAiStatus(Lineage.AI_STATUS_WALK);
			AiThread.append(mi);
			return mi;
		}

		public 투기장몬스터() {
		}

		@Override
		public void close() {
			super.close();
		}

		@Override
		protected void toAiWalk(long time) {
		}

		@Override
		protected void toAiCorpse(long time) {
		}

		@Override
		public int getAtkRange() {
			return 2;
		}
		
		@Override
		public void readDrop(int spawnMap) {
		}
		
		@Override
		public void readDrop(int spawnMap, PcInstance pc) {		
		}
		
		@Override
		public void toTimer(long time) {		
		}

		@Override
		public void setNowHp(int nowHp) {
			super.setNowHp(nowHp);
			//
			if (worldDelete || Lineage.server_version <= 144)
				return;
			//
			for (object o : getInsideList()) {
				if (o instanceof PcInstance) {
					o.toSender(S_ObjectHitratio.clone(BasePacketPooling.getPool(S_ObjectHitratio.class), this, true));
				}
			}
		}

		@Override
		public String getName() {		
			if (idx == 0)
				return String.format("%s# %s", "홀", name);
			else
				return String.format("%s# %s", "짝", name);
		}
		
		@Override
		public boolean isAttack(object o, boolean walk) {
			if (o == null)
				return false;
			if (o.isDead())
				return false;	
			if (o instanceof 투기장몬스터)
				return true;
			
			return false;
		}

		@Override
		public void toAiAttack(long time) {
			// 공격자 확인.
			object o = findDangerousObject();

			// 객체를 찾지못했다면 무시.
			if (o == null)
				return;

			boolean blind = isBuffCurseBlind() && !Util.isDistance(this, o, 2);
			// 객체 거리 확인
			if (Util.isDistance(this, o, getAtkRange()) && Util.isAreaAttack(this, o) && Util.isAreaAttack(o, this) && !blind) {
				// 객체 공격
				if (Util.isDistance(this, o, getAtkRange())) {
					// 물리공격 범위내로 잇을경우 처리.
					toAttack(o, 0, 0, getAtkRange() > 2, gfxMode + Lineage.GFX_MODE_ATTACK, 0, false);
				} else {
					// 객체에게 접근.
					ai_time = SpriteFrameDatabase.getGfxFrameTime(this, gfx, gfxMode + Lineage.GFX_MODE_WALK);
					toMoving(o, o.getX(), o.getY(), 0, true);
				}
			} else {
				ai_time = SpriteFrameDatabase.getGfxFrameTime(this, gfx, gfxMode + Lineage.GFX_MODE_WALK);
				// 객체 이동
				if (blind) {
					if (Util.random(0, 2) == 0)
						toMoving(null, o.getX() + Util.random(0, 1), o.getY() + Util.random(0, 1), 0, true);
				} else {
					if (!World.isNotAttackTile(o.getX(), o.getY(), o.getMap()))
						toMoving(o, o.getX(), o.getY(), 0, true);
				}
			}
		}
		
		@Override
		public void toAttack(object o, int x, int y, boolean bow, int gfxMode, int alpha_dmg, boolean isTriple) {
			int dmg = 0;
			ai_time = 500;
			
			// 일반적인 공격mode와 다를경우 프레임값 재 설정.
//			if (this.gfxMode + Lineage.GFX_MODE_ATTACK != gfxMode)
//				ai_time = SpriteFrameDatabase.getGfxFrameTime(this, gfx, gfxMode);

			int effect = bow ? mon.getArrowGfx() != 0 ? mon.getArrowGfx() : 66 : 0;
			
			// 데미지 설정.
			dmg = Util.random(1, 5);
			
			if (표판매_우승견 > 0) {
				if (getObjectId() == 표판매_우승견 && Util.random(1, 100) < 80)
					dmg = Util.random(5, 7);
			}
			
			if (운영자_설정_우승견 > 0) {
				if (getObjectId() == 운영자_설정_우승견 && Util.random(1, 100) < 80)
					dmg = Util.random(5, 7);
			}
			
			if (Util.random(1, 100) < 승률)
				dmg += Util.random(0, 1);
			
			dmg += getDynamicAddDmg();
			
			if (dmg <= 0)
				dmg = 0;
			
			DamageController.toDamage(this, o, dmg, Lineage.ATTACK_TYPE_WEAPON);

			toSender(S_ObjectAttack.clone(BasePacketPooling.getPool(S_ObjectAttack.class), this, o, gfxMode, 0, effect, bow, true, 0, 0), false);
		}
	}
	
	static private class 투기장도우미 extends object {

	}
	
	static private class 투기장버프사 extends object {

	}

	static private class 투기장표판매원 extends ShopInstance {
		public 투기장표판매원(Npc npc) {
			super(npc);
		}

		@Override
		public void toTalk(PcInstance pc, ClientBasePacket cbp) {
			if (!Lineage.is_fight) {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, html + "2"));
			} else {
				if (현재상태 == STATUS.다음진행대기 || 현재상태 == STATUS.준비)
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, html + "1"));
				else if (현재상태 == STATUS.결과)
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, html + "5"));
				else
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, html + "3", null, getRacerStatus()));
			}
		}

		@Override
		public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
			if (action.equalsIgnoreCase("status"))
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, html + "4", null, getRacerStatus()));
			else if (action.equalsIgnoreCase("buy")) {
				if (현재상태 == STATUS.다음진행대기 || 현재상태 == STATUS.준비)
					pc.toSender(S_TicketBuy.clone(BasePacketPooling.getPool(S_TicketBuy.class), this));
			} else if (action.equalsIgnoreCase("sell")) {
				if (현재상태 == STATUS.다음진행대기 || 현재상태 == STATUS.준비 || 현재상태 == STATUS.결과)
					pc.toSender(S_TicketSell.clone(BasePacketPooling.getPool(S_TicketSell.class), this, pc));

			}
		}

		@Override
		public void toDwarfAndShop(PcInstance pc, ClientBasePacket cbp) {
			switch (cbp.readC()) {
			case 0: { // 상점 구입
				if (현재상태 != STATUS.다음진행대기 && 현재상태 != STATUS.준비)
					return;
				long count = cbp.readH();
				if (count > 0 && count <= 100) {
					for (int j = 0; j < count; ++j) {
						long idx = cbp.readD();
						long item_idx = idx % 2;
						long item_count = cbp.readD();

						if (idx <= 1)
							item_count = item_count * 표판매갯수_1;
						else if (idx <= 3)
							item_count = item_count * 표판매갯수_2;
						else if (idx <= 5)
							item_count = item_count * 표판매갯수_3;
						else
							item_count = item_count * 표판매갯수_4;

						long price = Lineage.fight_ticket_price * item_count;

						if (item_count > 0) {
							투기장몬스터 select_dog = null;
							for (MonsterInstance mi : 투기몬스터목록) {
								투기장몬스터 monster = (투기장몬스터) mi;
								if (monster.idx == item_idx)
									select_dog = monster;
							}

							if (select_dog == null)
								continue;

							if (checkTicketCount(pc, now_uid, select_dog.idx, item_count)) {
								if (pc.getInventory().isAden(Lineage.fight_aden, price, true)) {
									RaceTicket ticket = pc.getInventory().findRaceTicket(now_uid, select_dog.idx, raceType);
									if (ticket == null) {
										ticket = (RaceTicket) ItemDatabase.newInstance(ItemDatabase.find(ticketName));
										ticket.setObjectId(ServerDatabase.nextItemObjId());
										ticket.setCount(item_count);
						
										ticket.setRaceTicket(RacerTicketName(select_dog.idx) + " " + raceType);
										pc.getInventory().append(ticket, true);
									} else {
										pc.getInventory().count(ticket, ticket.getCount() + item_count, true);
									}
//									//싸울의뢰
//									AccountDatabase.buyTickets((int) pc.getObjectId(), ticket.getCount());
									// 표 카운팅.
									now_buyCount += item_count;
									select_dog.buyCnt += item_count;
								} else {
									ChattingController.toChatting(pc, String.format("%s 충분치 않습니다.", getStringWord(Lineage.fight_aden, "이", "가")), Lineage.CHATTING_MODE_MESSAGE);
								}
							} else {
								ChattingController.toChatting(pc, String.format("한판당 최대 %d장 구매 가능합니다.", Lineage.fight_max_ticket), Lineage.CHATTING_MODE_MESSAGE);
							}
						}
					}
				}
				break;
			}
			case 1: { // 상점 판매
				if (현재상태 != STATUS.다음진행대기 && 현재상태 != STATUS.준비 && 현재상태 != STATUS.결과)
					return;
				Connection con = null;
				int count = cbp.readH();
				if (count > 0 && count <= Lineage.inventory_max) {
					try {
						con = DatabaseConnection.getLineage();

						for (int i = 0; i < count; ++i) {
							long inv_id = cbp.readD();
							long item_count = cbp.readD();
							ItemInstance temp = pc.getInventory().value(inv_id);
							if (temp != null && !temp.isEquipped() && item_count > 0 && temp.getCount() >= item_count) {
								// 가격 체크
								long target_price = getPrice(con, temp);
								if (target_price > 0) {
									ItemInstance aden = pc.getInventory().find(Lineage.fight_aden, true);
									if (aden == null) {
										aden = ItemDatabase.newInstance(ItemDatabase.find(Lineage.fight_aden));
										aden.setObjectId(ServerDatabase.nextItemObjId());
										aden.setCount(0);
										pc.getInventory().append(aden, true);
									}
									pc.getInventory().count(aden, aden.getCount() + (target_price * item_count), true);
									//
									if (temp instanceof RaceTicket)
										updateSell(((RaceTicket) temp).getRaceUid(), item_count);
								}
								pc.getInventory().count(temp, temp.getCount() - item_count, true);
							}
						}
					} catch (Exception e) {
					} finally {
						DatabaseConnection.close(con);
					}
				}
				break;
			}
			}
		}
		
		@Override
		public int getPrice(Connection con, ItemInstance item) {
			if (item instanceof RaceTicket) {
				RaceTicket ticket = (RaceTicket) item;
				PreparedStatement st = null;
				ResultSet rs = null;
				try {
					// 로그 참고로 목록 만들기.
					st = con.prepareStatement("SELECT * FROM race_log WHERE uid=? AND race_idx=? AND type=?");
					st.setInt(1, ticket.getRaceUid());
					st.setInt(2, ticket.getRacerIdx());
					st.setString(3, ticket.getRacerType());
					rs = st.executeQuery();
					if (rs.next())
						return rs.getInt("sell_price");
				} catch (Exception e) {
					lineage.share.System.println(투기장표판매원.class + " : getPrice(Connection con, ItemInstance item)");
					lineage.share.System.println(e);
				} finally {
					DatabaseConnection.close(st, rs);
				}
			}
			// 당첨 안된거 0원
			return 0;
		}
		
		/**
		 * 한판당 최대 표 구매수 확인.
		 * 2019-11-21
		 * by connector12@nate.com
		 */
		public boolean checkTicketCount(PcInstance pc, int uid, int idx, long count) {
			if (pc.getInventory() != null) {
				try {
					long tempCount = 0;
					for (ItemInstance ticket : pc.getInventory().getList()) {
						if (ticket instanceof RaceTicket) {
							if (((RaceTicket) ticket).getRaceUid() == uid && ((RaceTicket) ticket).getRacerIdx() == idx) {
								tempCount += ticket.getCount();
							}
						}
					}
					
					if (tempCount + count <= Lineage.fight_max_ticket)
						return true;
					else
						return false;
				} catch(Exception e) {
					return false;
				}
			}			
			return false;
		}
	}
	
	static private boolean isSellAdd(ItemInstance item) {
		if (item instanceof RaceTicket) {
			RaceTicket rt = (RaceTicket) item;
			if(rt.getRacerType().equalsIgnoreCase(raceType)) {
				// 결과가 나온 티켓인지 확인.
				if(getLastUid() >= rt.getRaceUid())
					return true;
			}
		}
		return false;
	}
	
	static private String RacerTicketName(int idx) {
		for (MonsterInstance mi : 투기몬스터목록) {
			투기장몬스터 monster = (투기장몬스터) mi;
			if (monster.idx != idx)
				continue;

			return String.format("%d-%d %s", now_uid, idx, monster.name);
		}

		return "";
	}
	
	static private List<String> getRacerStatus() {
		List<String> list_html = new ArrayList<String>();
		if (현재상태 == STATUS.다음진행대기 || 현재상태 == STATUS.준비 || 현재상태 == STATUS.진행중) {
			for (MonsterInstance mi : 투기몬스터목록) {
				투기장몬스터 monster = (투기장몬스터) mi;

				list_html.add(mi.getName());
				list_html.add("좋음");
				list_html.add(String.format("%d%%", monster.승률));
			}
		} else {
			list_html.add("");
		}
		
		return list_html;
	}
	
	static private class S_TicketBuy extends S_Inventory {
		static synchronized public BasePacket clone(BasePacket bp, object shop, Object... opt) {
			if (bp == null)
				bp = new S_TicketBuy(shop, opt);
			else
				((S_TicketBuy) bp).toClone(shop, opt);
			return bp;
		}

		public S_TicketBuy(object shop, Object... opt) {
			toClone(shop, opt);
		}

		public void toClone(object shop, Object... opt) {
			clear();

			Item i = ItemDatabase.find(ticketName);
			if (i != null) {
				writeC(Opcodes.S_OPCODE_SHOPBUY);
				writeD(shop.getObjectId());

				writeH(투기몬스터목록.size() * 4);

				int idx = 0;
				for (MonsterInstance mi : 투기몬스터목록) {
					투기장몬스터 monster = (투기장몬스터) mi;
					writeD(monster.idx + idx);
					writeH(i.getInvGfx());
					writeD(Lineage.fight_ticket_price * 표판매갯수_1);
					writeS(String.format("%s %,d장", monster.getName(), 표판매갯수_1));
					toEtc(i, 0);
				}

				idx += 투기몬스터목록.size();
				for (MonsterInstance mi : 투기몬스터목록) {
					투기장몬스터 monster = (투기장몬스터) mi;
					writeD(monster.idx + idx);
					writeH(i.getInvGfx());
					writeD(Lineage.fight_ticket_price * 표판매갯수_2);
					writeS(String.format("%s %,d장", monster.getName(), 표판매갯수_2));
					toEtc(i, 0);
				}

				idx += 투기몬스터목록.size();
				for (MonsterInstance mi : 투기몬스터목록) {
					투기장몬스터 monster = (투기장몬스터) mi;
					writeD(monster.idx + idx);
					writeH(i.getInvGfx());
					writeD(Lineage.fight_ticket_price * 표판매갯수_3);
					writeS(String.format("%s %,d장", monster.getName(), 표판매갯수_3));
					toEtc(i, 0);
				}

				idx += 투기몬스터목록.size();
				for (MonsterInstance mi : 투기몬스터목록) {
					투기장몬스터 monster = (투기장몬스터) mi;
					writeD(monster.idx + idx);
					writeH(i.getInvGfx());
					writeD(Lineage.fight_ticket_price * 표판매갯수_4);
					writeS(String.format("%s %,d장", monster.getName(), 표판매갯수_4));
					toEtc(i, 0);
				}

				writeH(0x07);
			}
		}
	}

	static private class S_TicketSell extends S_Inventory {
		static synchronized public BasePacket clone(BasePacket bp, object shop, Object... opt) {
			if (bp == null)
				bp = new S_TicketSell(shop, opt);
			else
				((S_TicketSell) bp).toClone(shop, opt);
			return bp;
		}

		public S_TicketSell(object shop, Object... opt) {
			toClone(shop, opt);
		}

		public void toClone(object shop, Object... opt) {
			clear();
			PcInstance pc = (PcInstance) opt[0];
			List<ItemInstance> sell_list = new ArrayList<ItemInstance>();
			List<ItemInstance> remove_list = new ArrayList<ItemInstance>();
			pc.getInventory().findDbName(ticketName, sell_list);
			for (ItemInstance ii : sell_list)
				if (!isSellAdd(ii))
					remove_list.add(ii);
			sell_list.removeAll(remove_list);

			writeC(Opcodes.S_OPCODE_SHOPSELL);
			writeD(shop.getObjectId());

			writeH(sell_list.size());
			Connection con = null;
			try {
				con = DatabaseConnection.getLineage();
				for (ItemInstance ii : sell_list) {
					writeD(ii.getObjectId());
					writeD(((투기장표판매원) shop).getPrice(con, ii));
				}
			} catch (Exception e) {
			} finally {
				DatabaseConnection.close(con);
			}

			writeH(0x07);
		}
	}
	
	/**
	 * 마지막 투기장 uid 추출.
	 * 2019-10-29
	 * by connector12@nate.com
	 */
	static public int getLastUid() {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT MAX(uid) FROM race_log WHERE type=?");
			st.setString(1, raceType);
			rs = st.executeQuery();
			if (rs.next())
				return rs.getInt(1);
		} catch (Exception e) {
			lineage.share.System.printf("%s : getLastUid()\r\n", FightController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return 1;
	}
	
	/**
	 * 투기장 종료시 이긴 몬스터의 정보 저장.
	 * 2019-10-29
	 * by connector12@nate.com
	 */
	static private void insertDB(투기장몬스터 monster) {
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("INSERT INTO race_log SET type=?, race_idx=?, uid=?, price=?, rate=?, cnt_buy=?, sell_price=?");
			st.setString(1, raceType);
			st.setInt(2, monster.idx);
			st.setInt(3, now_uid++);
			st.setInt(4, Lineage.fight_ticket_price);
			st.setString(5, String.format("%.2f", monster.rate));
			st.setLong(6, now_buyCount);
			st.setInt(7, (int) (Lineage.fight_rate * Lineage.fight_ticket_price));
			st.execute();
		} catch (Exception e) {
			lineage.share.System.printf("%s : insertDB()\r\n", FightController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	
	static public void updateSell(int uid, long count) {
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE race_log SET cnt_sell=cnt_sell+? WHERE uid=? AND type=?");
			st.setLong(1, count);
			st.setInt(2, uid);
			st.setString(3, raceType);
			st.execute();
		} catch (Exception e) {
			lineage.share.System.printf("%s : updateSell(int uid, long count)\r\n", FightController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	
	static public long getBuyCnt(int uid) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			if (uid == 0) {
				st = con.prepareStatement("SELECT SUM(cnt_buy) FROM race_log WHERE type=?");
				st.setString(1, raceType);
			} else {
				st = con.prepareStatement("SELECT SUM(cnt_buy) FROM race_log WHERE uid=? AND type=?");
				st.setInt(1, uid);
				st.setString(2, raceType);
			}
			rs = st.executeQuery();
			if (rs.next())
				return rs.getLong(1);
		} catch (Exception e) {
			lineage.share.System.printf("%s : getBuyCnt(int uid)\r\n", FightController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return 0;
	}
	
	static public long getSellCnt(int uid) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			if (uid == 0) {
				st = con.prepareStatement("SELECT SUM(cnt_sell) FROM race_log WHERE type=?");
				st.setString(1, raceType);
			} else {
				st = con.prepareStatement("SELECT SUM(cnt_sell) FROM race_log WHERE uid=? AND type=?");
				st.setInt(1, uid);
				st.setString(2, raceType);
			}
			rs = st.executeQuery();
			if (rs.next())
				return rs.getLong(1);
		} catch (Exception e) {
			lineage.share.System.printf("%s : getSellCnt(int uid)\r\n", FightController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return 0;
	}
	
	static public double getTotalPrice(boolean buy) {
		double value = 0D;
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM race_log WHERE type=?");
			st.setString(1, raceType);
			rs = st.executeQuery();
			while (rs.next()) {
				if (buy)
					value += rs.getInt("price") * rs.getInt("cnt_buy");
				else
					value += rs.getDouble("rate") * rs.getInt("price") * rs.getInt("cnt_sell");
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : getTotalPrice(boolean buy)\r\n", FightController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return value;
	}
	
	static public int getPrice(int uid) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT price FROM race_log WHERE uid=? AND type=?");
			st.setInt(1, uid);
			st.setString(2, raceType);
			rs = st.executeQuery();
			if (rs.next())
				return rs.getInt(1);
		} catch (Exception e) {
			lineage.share.System.printf("%s : getPrice(int uid)\r\n", FightController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return 0;
	}
	
	static public double getRate(int uid) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT rate FROM race_log WHERE uid=? AND type=?");
			st.setInt(1, uid);
			st.setString(2, raceType);
			rs = st.executeQuery();
			if (rs.next())
				return rs.getDouble(1);
		} catch (Exception e) {
			lineage.share.System.printf("%s : getRate(int uid)\r\n", FightController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return 0;
	}
	
	/**
	 * 한글 조사 연결 (을/를,이/가,은/는,로/으로)
	 * 1. 종성에 받침이 있는 경우 '을/이/은/으로/과'
	 * 2. 종성에 받침이 없는 경우 '를/가/는/로/와'
	 * 3. '로/으로'의 경우 종성의 받침이 'ㄹ' 인경우 '로'
	 * 참고 1 : http://gun0912.tistory.com/65 (소스 참고)
	 * 참고 2 : http://www.klgoodnews.org/board/bbs/board.php?bo_table=korean&wr_id=247 (조사 원리 참고)
	 * 
	 * 2019-05-22
	 * by connector12@nate.com
	 * @param name
	 * @param firstValue
	 * @param secondValue
	 * @return
	 */
	public static String getStringWord(String str, String firstVal, String secondVal) {
		try {
			char laststr = str.charAt(str.length() - 1);
			// 한글의 제일 처음과 끝의 범위밖일 경우는 오류
			if (laststr < 0xAC00 || laststr > 0xD7A3) {
				return str;
			}

			int lastCharIndex = (laststr - 0xAC00) % 28;

			// 종성인덱스가 0이상일 경우는 받침이 있는경우이며 그렇지 않은경우는 받침이 없는 경우
			if (lastCharIndex > 0) {
				// 받침이 있는경우
				// 조사가 '로'인경우 'ㄹ'받침으로 끝나는 경우는 '로' 나머지 경우는 '으로'
				if (firstVal.equals("으로") && lastCharIndex == 8) {
					str += secondVal;
				} else {
					str += firstVal;
				}
			} else {
				// 받침이 없는 경우
				str += secondVal;
			}
		} catch (Exception e) {
		}
		return str;
	}
	
	/**
	 * 울타리 스폰.
	 * 2019-10-30
	 * by connector12@nate.com
	 */
	static public void spawnFence() {
		울타리_리스트 = new ArrayList<BattleRoyalTeamLine>();
		int count = (((울타리_X2 - 울타리_X1)) + ((울타리_Y2 - 울타리_Y1))) * 2;

		// 울타리 생성
		for (int i = 0; i < count; i++)
			울타리_리스트.add(new lineage.world.object.npc.background.BattleRoyalTeamLine());
		
		// 울타리 스폰
		int x1 = 울타리_X1;
		int y1 = 울타리_Y1;
		int x2 = 울타리_X2;
		int y2 = 울타리_Y2;

		int step = 0;
		for (BattleRoyalTeamLine fence : 울타리_리스트) {
			if (step == 0) {
				fence.setGfx(11204);
				fence.setObjectId(ServerDatabase.nextEtcObjId());
				fence.toTeleport(x1, y1++, 울타리_MAP, false);
				
				if (y1 > 울타리_Y2) {
					step++;
					y1 = 울타리_Y1;
				}
			} else if (step == 1) {
				fence.setGfx(11204);
				fence.setObjectId(ServerDatabase.nextEtcObjId());
				fence.toTeleport(x2, y1++, 울타리_MAP, false);
				
				if (y1 > 울타리_Y2) {
					step++;
					y1 = 울타리_Y1;
				}
			} else if (step == 2) {
				fence.setGfx(11204);
				fence.setObjectId(ServerDatabase.nextEtcObjId());
				fence.toTeleport(++x1, y1, 울타리_MAP, false);
							
				if (x1 >= 울타리_X2 - 1) {
					step++;
					x1 = 울타리_X1;
				}
			} else if (step == 3) {
				fence.setGfx(11204);
				fence.setObjectId(ServerDatabase.nextEtcObjId());
				fence.toTeleport(++x1, y2, 울타리_MAP, false);
				
				if (x1 >= 울타리_X2 - 1) {
					step++;
					x1 = 울타리_X1;
				}
			}
		}
	}
	
	static public void spawnBuff() {
		if (현재상태 == STATUS.진행중) {
			if (Util.random(1, 100) < 10 && 투기장버프사_스폰횟수 < 투기장버프사_최대스폰횟수 && 투기장버프사_카운트 == 0) {
				투기장버프사_카운트 = 5;
				투기장버프사_스폰횟수++;
				투기장버프사NPC.toTeleport(투기장버프사NPC.getHomeX(), 투기장버프사NPC.getHomeY(), 투기장버프사NPC.getHomeMap(), false);
				투기장버프사NPC.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), 투기장버프사NPC, Lineage.doll_teleport_effect), false);
				
				투기장몬스터 mon1 = (투기장몬스터) 투기몬스터목록.get(0);
				투기장몬스터 mon2 = (투기장몬스터) 투기몬스터목록.get(1);
							
				if (Util.random(1, 100) < 10) {
					int effect = 830;
					// 힐
					mon1.setNowHp(mon1.getNowHp() + 20);
					mon1.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), mon1, effect), false);
					mon2.setNowHp(mon1.getNowHp() + 20);
					mon2.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), mon2, effect), false);
					투기장버프사NPC.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), 투기장버프사NPC, 832), false);
				} else if (Util.random(1, 100) < 30) {
					int effect = 830;
					// 힐
					if (운영자_설정_우승견 > 0) {
						if (mon1.getObjectId() == 운영자_설정_우승견) {
							mon1.setNowHp(mon1.getNowHp() + 20);
							mon1.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), mon1, effect), false);
						} else if (mon2.getObjectId() == 운영자_설정_우승견) {
							mon2.setNowHp(mon1.getNowHp() + 20);
							mon2.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), mon2, effect), false);
						}
					} else {
						if (Util.random(1, 100) <= 50) {
							mon1.setNowHp(mon1.getNowHp() + 20);
							mon1.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), mon1, effect), false);
						} else {
							mon2.setNowHp(mon1.getNowHp() + 20);
							mon2.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), mon2, effect), false);
						}
					}
				} else if (Util.random(1, 100) < 40) {
					int effect = 172;			
					// 디버프
					if (운영자_설정_우승견 > 0) {
						if (mon1.getObjectId() != 운영자_설정_우승견) {
							mon1.setDynamicAddDmg(-2);
							mon1.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), mon1, effect), false);
						} else if (mon2.getObjectId() != 운영자_설정_우승견) {
							mon2.setDynamicAddDmg(-2);
							mon2.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), mon2, effect), false);
						}
					} else {
						if (Util.random(1, 100) <= 50) {
							mon1.setDynamicAddDmg(-2);
							mon1.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), mon1, effect), false);
						} else {
							mon2.setDynamicAddDmg(-2);
							mon2.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), mon2, effect), false);
						}
					}
				} else {
					int effect = 2176;
					// 버프
					if (운영자_설정_우승견 > 0) {
						if (mon1.getObjectId() == 운영자_설정_우승견) {
							mon1.setDynamicAddDmg(2);
							mon1.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), mon1, effect), false);
						} else if (mon2.getObjectId() == 운영자_설정_우승견) {
							mon2.setDynamicAddDmg(2);
							mon2.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), mon2, effect), false);
						}
					} else {
						if (Util.random(1, 100) <= 50) {
							mon1.setDynamicAddDmg(2);
							mon1.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), mon1, effect), false);
						} else {
							mon2.setDynamicAddDmg(2);
							mon2.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), mon2, effect), false);
						}
					}
				}
				
				투기장버프사NPC.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), 투기장버프사NPC, 19), false);
			}
			
			if (투기장버프사_카운트 > 0 && --투기장버프사_카운트 < 1) {
				투기장버프사_카운트 = 0;
				투기장버프사NPC.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), 투기장버프사NPC, Lineage.doll_teleport_effect + 1), false);	
				World.remove(투기장버프사NPC);
				투기장버프사NPC.clearList(true);
			}
		}
	}
}
