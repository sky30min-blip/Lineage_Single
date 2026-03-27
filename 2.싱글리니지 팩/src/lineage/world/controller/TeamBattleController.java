package lineage.world.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lineage.bean.database.Item;
import lineage.bean.database.ItemTeambattle;
import lineage.bean.database.TeamBattlePoly;
import lineage.bean.database.TeamBattleTime;
import lineage.bean.lineage.Clan;
import lineage.bean.lineage.Kingdom;
import lineage.database.ItemDatabase;
import lineage.database.PolyDatabase;
import lineage.database.ServerDatabase;
import lineage.database.SkillDatabase;
import lineage.database.TeamBattleDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ClanWar;
import lineage.network.packet.server.S_KingdomAgent;
import lineage.network.packet.server.S_MessageYesNo;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.network.packet.server.S_ObjectHitratio;
import lineage.network.packet.server.S_ObjectName;
import lineage.network.packet.server.S_ObjectPoly;
import lineage.network.packet.server.S_ObjectTitle;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.instance.BackgroundInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.ShapeChange;

public final class TeamBattleController {
	static private Calendar calendar;
	static private List<PcInstance> joinList;
	static private Map<Long, TeamBattlePoly> polyList;
	static public int A_TeamScore = 0;
	static public int B_TeamScore = 0;
	static private List<BackgroundInstance> line;
	static public Clan A_Team;
	static public Clan B_Team;
	static public boolean askTeamBattle = false;
	static public boolean startTeamBattle = false;
	static public boolean startx = false;
	static boolean joinEnd;
	
	static public void init() {
		TimeLine.start("TeamBattleController..");
		calendar = Calendar.getInstance();
		joinList = new ArrayList<PcInstance>();
		polyList = new HashMap<Long, TeamBattlePoly>();
		line = new ArrayList<BackgroundInstance>();
		TimeLine.end();
	}

	static public void toTimer(long time) {
		calendar.setTimeInMillis(time);
		Date date = calendar.getTime();
		int hour = date.getHours();
		int min = date.getMinutes();
		int sec = date.getSeconds();

		for (TeamBattleTime tempTime : Lineage.team_battle_time) {
			// %d분전 시작 메세지
			if (tempTime.getHour() == hour && tempTime.getMin() - Lineage.team_battle_world_message == min && sec == 0) {
				spawnLine();
				World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), String.format("[알림] %d분 후 팀대전이 시작됩니다.", Lineage.team_battle_world_message)));
			}

			// 1분전 y/N 보내기
			if (tempTime.getHour() == hour && tempTime.getMin() - 1 == min && sec == 0) {
				A_Team = ClanController.find(Lineage.teamBattle_A_team);
				B_Team = ClanController.find(Lineage.teamBattle_B_team);
				askTeamBattle = true;
				joinEnd = false;
				toAskTeamBattle("1분");
			}


			// 30초전 y/N 보내기
			if (tempTime.getHour() == hour && tempTime.getMin() - 1 == min && sec == 30){
				A_Team = ClanController.find(Lineage.teamBattle_A_team);
				B_Team = ClanController.find(Lineage.teamBattle_B_team);
				askTeamBattle = true;

				joinEnd = false;
				toAskTeamBattle("30초");
			}


			// 5초전 입장 종료
			if (tempTime.getHour() == hour && tempTime.getMin() - 1 == min && sec == 55 && !joinEnd) {
				joinEnd = true;
				World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), "[알림] 팀대전의 입장이 마감되었습니다."));
			}

			// 참여자에게 알림
			if (tempTime.getHour() == hour && tempTime.getMin() - 1 == min && sec > 54 && sec <= 59) {
				for (PcInstance pc : getJoinList()) {
					if (!pc.isWorldDelete())
						ChattingController.toChatting(pc, String.format("\\fR[알림] %d초 후 팀대전이 시작됩니다.", (60 - sec)), Lineage.CHATTING_MODE_MESSAGE);
				}
			}

			// 팀대전 시작
			if (tempTime.getHour() == hour && tempTime.getMin() == min && !startTeamBattle && askTeamBattle) {
				askTeamBattle = false;
				startTeamBattle = true;
				startTeamBattle();
			}

			if (startTeamBattle) {
				if (A_TeamScore < 1)
					// B팀 승리
					endTeamBattle(B_Team.getUid());
				else if (B_TeamScore < 1)
					// A팀 승리
					endTeamBattle(A_Team.getUid());
			}
		}
	}

	// 팀 나누는 라인 스폰
	static public void spawnLine() {
		int y = 32789;
		
		for (int i = 0; i < 18; i++)
			line.add(new lineage.world.object.npc.background.BattleRoyalTeamLine());

		for (BackgroundInstance teamLine : line) {
			teamLine.setGfx(10467);
			teamLine.setObjectId(ServerDatabase.nextEtcObjId());
			teamLine.toTeleport(32738, y, Lineage.teamBattleMap, false);
			y++;
		}
	}

	// 팀대전에 참여하시겠습니까? (y/n)
	static public void toAskTeamBattle(String time) {
		for (PcInstance pc : World.getPcList()) {
			if (!pc.isWorldDelete() && !checkList(pc))
				pc.toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 773, time));
		}
	}

	// YES / NO 대답을 했을시
	static public void toAsk(PcInstance pc, boolean yes) {
		if (yes && !pc.isWorldDelete() && !pc.isDead() && !pc.isLock() && !startTeamBattle && pc.getMap() != Lineage.teamBattleMap && !joinEnd && askTeamBattle && !pc.isFishing()) {
			if (Lineage.teamBattle_max_pc > 0 && getJoinListSize() > Lineage.teamBattle_max_pc) {
				ChattingController.toChatting(pc, String.format("팀대전 입장 인원(%d명)을 초과하여 입장이 불가능합니다.", Lineage.teamBattle_max_pc), Lineage.CHATTING_MODE_MESSAGE);
				return;
			}

			if (pc.getLevel() >= Lineage.teamBattle_level)
				
				appendList(pc);
	
			else
				ChattingController.toChatting(pc, String.format("팀대전은 %d레벨 이상 입장 가능합니다.", Lineage.teamBattle_level), Lineage.CHATTING_MODE_MESSAGE);
		} else {
			if (yes) {
				if (pc.isDead() || pc.isLock())
					ChattingController.toChatting(pc, "[알림] 현재 상태에서는 입장이 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
				if (pc.isFishing())
					ChattingController.toChatting(pc, "[알림] 낚시중엔 입장이 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
				if (startTeamBattle)
					ChattingController.toChatting(pc, "[알림] 팀대전이 이미 시작되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
				if (joinEnd)
					ChattingController.toChatting(pc, "[알림] 팀대전 입장시간이 아닙니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}

	// 팀대전 참여자 추가
	static public void appendList(PcInstance pc) {
		synchronized (joinList) {
			if (!joinList.contains(pc)) {
				joinList.add(pc);
				pc.isAutoHunt = false;
			}
		}

		PartyController.close(pc);

		pc.setTempName(pc.getName());
		pc.setTempTitle(pc.getTitle());
		pc.setTempClanName(pc.getClanName());
		pc.setTempClanId(pc.getClanId());
		pc.setTempClanGrade(pc.getClanGrade());
		// 인원수가 같을땐 랜덤으로 팀배정
		if (A_TeamScore == B_TeamScore) {
			if (Math.random() < Math.random()) {
				A_TeamScore += 1;
				pc.setBattleTeam(A_Team.getUid());
				pc.setName(A_Team.getName());
				pc.setTitle("");
				pc.setClanName(A_Team.getName());
				pc.setClanId(A_Team.getUid());
				pc.setClanGrade(0);
				pc.toPotal(Util.random(32732, 32735), Util.random(32791, 32803), Lineage.teamBattleMap);
			} else {
				B_TeamScore += 1;
				pc.setBattleTeam(B_Team.getUid());
				pc.setName(B_Team.getName());
				pc.setTitle("");
				pc.setClanName(B_Team.getName());
				pc.setClanId(B_Team.getUid());
				pc.setClanGrade(0);
				pc.toPotal(Util.random(32741, 32744), Util.random(32791, 32803), Lineage.teamBattleMap);
			}
		} else if (A_TeamScore > B_TeamScore) {
			B_TeamScore += 1;
			pc.setBattleTeam(B_Team.getUid());
			pc.setName(B_Team.getName());
			pc.setTitle("");
			pc.setClanName(B_Team.getName());
			pc.setClanId(B_Team.getUid());
			pc.setClanGrade(0);
			pc.toPotal(Util.random(32741, 32744), Util.random(32791, 32803), Lineage.teamBattleMap);
		} else {
			A_TeamScore += 1;
			pc.setBattleTeam(A_Team.getUid());
			pc.setName(A_Team.getName());
			pc.setTitle("");
			pc.setClanName(A_Team.getName());
			pc.setClanId(A_Team.getUid());
			pc.setClanGrade(0);
			pc.toPotal(Util.random(32732, 32735), Util.random(32791, 32803), Lineage.teamBattleMap);
		}

		pc.toSender(S_ObjectName.clone(BasePacketPooling.getPool(S_ObjectName.class), pc));
		pc.toSender(S_ObjectTitle.clone(BasePacketPooling.getPool(S_ObjectTitle.class), pc), true);

		Clan clan = ClanController.find(pc.getClanId());
		clan.appendList(pc);

		pc.toSender(S_KingdomAgent.clone(BasePacketPooling.getPool(S_KingdomAgent.class), 4, 0));

		// 변신중인 유저는 임시로 변신정보 담았다가 팀대전 종료 후 복구
		if (BuffController.find(pc, SkillDatabase.find(208)) != null && PolyDatabase.getPolyGfx(pc.getGfx()) != null) {
			TeamBattlePoly poly = new TeamBattlePoly();

			poly.setObjId(pc.getObjectId());
			poly.setGfx(pc.getGfx());
			poly.setPoly(PolyDatabase.getPolyGfx(pc.getGfx()));
			poly.setPolyTime(BuffController.find(pc, SkillDatabase.find(208)).getTime());

			if (!polyList.containsKey(pc.getObjectId())) {
				polyList.put(pc.getObjectId(), poly);

				BuffController.remove(pc, ShapeChange.class);

				if (poly.getPolyTime() < 0)
					ChattingController.toChatting(pc, "변신 효과가 있는 세트 아이템을 착용하고 있을 경우 팀대전이 종료 후 변신 효과가 복구되지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
		//ShapeChange.init(pc, pc, PolyDatabase.getName(PolyDatabase.toRankPolyMorph(pc, null, true)), -1, 1);
		ShapeChange.init(pc, pc, PolyDatabase.getName(PolyDatabase.teamBattlePoly(pc, pc.getBattleTeam())), -1, 1);
	}

	// 팀대전 시작
	static public void startTeamBattle() {

		for (BackgroundInstance teamLine : line) {
			teamLine.clearList(true);
			World.remove(teamLine);
		}

		if (A_TeamScore <= 2 || B_TeamScore <= 2 ){
			startx = true;
			endTeamBattle(0);
		}else {
			for (PcInstance pc : getJoinList())
				hpUpdate(pc, true);

			A_Team.toSender(S_ClanWar.clone(BasePacketPooling.getPool(S_ClanWar.class), 1, A_Team.getName(), B_Team.getName()));
			B_Team.toSender(S_ClanWar.clone(BasePacketPooling.getPool(S_ClanWar.class), 1, B_Team.getName(), A_Team.getName()));

			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), "[알림] 팀대전이 시작되었습니다."));
		}
	}

	static public void hpUpdate(PcInstance pc) {
		if (pc.getBattleTeam() == A_Team.getUid())
			A_Team.toSender(S_ObjectHitratio.clone(BasePacketPooling.getPool(S_ObjectHitratio.class), pc, true));
		else if (pc.getBattleTeam() == B_Team.getUid())
			B_Team.toSender(S_ObjectHitratio.clone(BasePacketPooling.getPool(S_ObjectHitratio.class), pc, true));
	}

	static public void hpUpdate(PcInstance pc, boolean visual) {		
		if (pc.getBattleTeam() == A_Team.getUid())
			A_Team.toSender(S_ObjectHitratio.clone(BasePacketPooling.getPool(S_ObjectHitratio.class), pc, visual));
		else if (pc.getBattleTeam() == B_Team.getUid())
			B_Team.toSender(S_ObjectHitratio.clone(BasePacketPooling.getPool(S_ObjectHitratio.class), pc, visual));
	}

	// 팀대전 종료
	static public void endTeamBattle(int team) {
		String winTeam = team == A_Team.getUid() ? A_Team.getName() : B_Team.getName();
		if(!startx){
			
		if (team > 0) {
			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), String.format("[알림] %s이 팀대전에서 승리하였습니다.", winTeam)));

			A_Team.toSender(S_ClanWar.clone(BasePacketPooling.getPool(S_ClanWar.class), 3, A_Team.getName(), B_Team.getName()));
			B_Team.toSender(S_ClanWar.clone(BasePacketPooling.getPool(S_ClanWar.class), 3, B_Team.getName(), A_Team.getName()));
		} else
			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), "[알림] 팀대전이 무승부로 종료되었습니다."));

		try {
			List<ItemTeambattle> teamItemList = new ArrayList<ItemTeambattle>();
			List<ItemTeambattle> winTeamItemList = new ArrayList<ItemTeambattle>();
			List<ItemTeambattle> loseTeamItemList = new ArrayList<ItemTeambattle>();
			
			TeamBattleDatabase.find(teamItemList, "공통");
			TeamBattleDatabase.find(winTeamItemList, "승리");
			TeamBattleDatabase.find(loseTeamItemList, "패배");
			
			synchronized (joinList) {
				for (PcInstance pc : joinList) {
					if (pc == null || pc.getMap() != Lineage.teamBattleMap || pc.getBattleTeam() == 0)
						continue;
					
					try {
						hpUpdate(pc, false);
						
						if (team > 0)
							ChattingController.toChatting(pc, String.format("[알림] 팀대전에서 %s 하였습니다.", pc.getBattleTeam() == team ? "승리" : "패배"), Lineage.CHATTING_MODE_MESSAGE);
						
						// 공통 보상 지급
						if (teamItemList.size() > 0) {
							for (ItemTeambattle ib : teamItemList) {
								if (ib.getItemCountMin() > 0) {
									Item i = ItemDatabase.find(ib.getItem());

									if (i != null) {
										ItemInstance temp = pc.getInventory().find(i.getName(), ib.getItemBless(), i.isPiles());
										int count = Util.random(ib.getItemCountMin(), ib.getItemCountMax());

										if (temp != null && (temp.getBless() != ib.getItemBless() || temp.getEnLevel() != ib.getItemEnchant()))
											temp = null;

										if (temp == null) {
											// 겹칠수 있는 아이템이 존재하지 않을경우.
											if (i.isPiles()) {
												temp = ItemDatabase.newInstance(i);
												temp.setObjectId(ServerDatabase.nextItemObjId());
												temp.setBless(ib.getItemBless());
												temp.setEnLevel(ib.getItemEnchant());
												temp.setCount(count);
												temp.setDefinite(true);
												pc.getInventory().append(temp, true);
											} else {
												for (int idx = 0; idx < count; idx++) {
													temp = ItemDatabase.newInstance(i);
													temp.setObjectId(ServerDatabase.nextItemObjId());
													temp.setBless(ib.getItemBless());
													temp.setEnLevel(ib.getItemEnchant());
													temp.setDefinite(true);
													pc.getInventory().append(temp, true);
												}
											}
										} else {
											// 겹치는 아이템이 존재할 경우.
											pc.getInventory().count(temp, temp.getCount() + count, true);
										}

										// 알림.
										ChattingController.toChatting(pc, String.format("%s(%d)을 보상 받았습니다. ", i.getName(), count), Lineage.CHATTING_MODE_MESSAGE);
										
									}
								}
							}
						}
		
							if (team > 0) {
								// 이긴팀 보상
								if (pc.getBattleTeam() == team) {
									if (winTeamItemList.size() > 0) {
										for (ItemTeambattle ib : winTeamItemList) {
											if (ib.getItemCountMin() > 0) {
												Item i = ItemDatabase.find(ib.getItem());

												if (i != null) {
													ItemInstance temp = pc.getInventory().find(i.getName(), ib.getItemBless(), i.isPiles());
													int count = Util.random(ib.getItemCountMin(), ib.getItemCountMax());

													if (temp != null && (temp.getBless() != ib.getItemBless() || temp.getEnLevel() != ib.getItemEnchant()))
														temp = null;

													if (temp == null) {
														// 겹칠수 있는 아이템이 존재하지 않을경우.
														if (i.isPiles()) {
															temp = ItemDatabase.newInstance(i);
															temp.setObjectId(ServerDatabase.nextItemObjId());
															temp.setBless(ib.getItemBless());
															temp.setEnLevel(ib.getItemEnchant());
															temp.setCount(count);
															temp.setDefinite(true);
															pc.getInventory().append(temp, true);
														} else {
															for (int idx = 0; idx < count; idx++) {
																temp = ItemDatabase.newInstance(i);
																temp.setObjectId(ServerDatabase.nextItemObjId());
																temp.setBless(ib.getItemBless());
																temp.setEnLevel(ib.getItemEnchant());
																temp.setDefinite(true);
																pc.getInventory().append(temp, true);
															}
														}
													} else {
														// 겹치는 아이템이 존재할 경우.
														pc.getInventory().count(temp, temp.getCount() + count, true);
													}

													// 알림.
													ChattingController.toChatting(pc, String.format("%s(%d) 을 보상 받았습니다. ", i.getName(), count), Lineage.CHATTING_MODE_MESSAGE);
													pc.toPotal(Util.random(33433, 33436), Util.random(32814, 32817), 4);
													
												}
											}
										}
									}
								} else {
									// 진팀 보상
									if (loseTeamItemList.size() > 0) {
										for (ItemTeambattle ib : loseTeamItemList) {
											if (ib.getItemCountMin() > 0) {
												Item i = ItemDatabase.find(ib.getItem());

												if (i != null) {
													ItemInstance temp = pc.getInventory().find(i.getName(), ib.getItemBless(), i.isPiles());
													int count = Util.random(ib.getItemCountMin(), ib.getItemCountMax());

													if (temp != null && (temp.getBless() != ib.getItemBless() || temp.getEnLevel() != ib.getItemEnchant()))
														temp = null;

													if (temp == null) {
														// 겹칠수 있는 아이템이 존재하지 않을경우.
														if (i.isPiles()) {
															temp = ItemDatabase.newInstance(i);
															temp.setObjectId(ServerDatabase.nextItemObjId());
															temp.setBless(ib.getItemBless());
															temp.setEnLevel(ib.getItemEnchant());
															temp.setCount(count);
															temp.setDefinite(true);
															pc.getInventory().append(temp, true);
														} else {
															for (int idx = 0; idx < count; idx++) {
																temp = ItemDatabase.newInstance(i);
																temp.setObjectId(ServerDatabase.nextItemObjId());
																temp.setBless(ib.getItemBless());
																temp.setEnLevel(ib.getItemEnchant());
																temp.setDefinite(true);
																pc.getInventory().append(temp, true);
															}
														}
													} else {
														// 겹치는 아이템이 존재할 경우.
														pc.getInventory().count(temp, temp.getCount() + count, true);
													}

													// 알림.
													ChattingController.toChatting(pc, String.format("%s(%d) 을 보상 받았습니다. ", i.getName(), count), Lineage.CHATTING_MODE_MESSAGE);
													pc.toPotal(Util.random(33433, 33436), Util.random(32814, 32817), 4);
												}
											}
										}
									}
								}
							}
						
						

						// 참여자 원상복귀
						if (pc.getClanId() > 0) {
							Clan clan = ClanController.find(pc.getClanId());
							
							if (clan != null)
								clan.removeList(pc);
						}
						
						if (pc.getTempName() != null)
							pc.setName(pc.getTempName());
						if (pc.getTempClanName() != null)
							pc.setClanName(pc.getTempClanName());
						if (pc.getTempTitle() != null)
							pc.setTitle(pc.getTempTitle());
						pc.setClanId(pc.getTempClanId());
						pc.setClanGrade(pc.getTempClanGrade());	
						pc.setBattleTeam(0);
						pc.setTransparent(false);
						pc.setTeamBattleDead(false);
						
						removePoly(pc);
								
						Kingdom kingdom = KingdomController.find(4);
						if (kingdom != null && kingdom.getClanId() > 0)
							pc.toSender(S_KingdomAgent.clone(BasePacketPooling.getPool(S_KingdomAgent.class), 4, kingdom.getAgentId()));
						
						int[] loc = Lineage.getHomeXY();
						pc.toTeleport(loc[0], loc[1], loc[2], true);
					} catch (Exception e) {
						lineage.share.System.println("팀대전 종료 에러 캐릭터 오브젝트id: " + pc.getObjectId());
						lineage.share.System.println(e);
					}
				}
			}
		} catch (Exception e) {}	
		}else{
			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), "[알림] 팀대전이 인원이 부족하여 종료되었습니다."));
		}

		// 변수 초기화
		startTeamBattle = false;
		startx = false;
		joinList.clear();
		polyList.clear();
		line.clear();
		A_TeamScore = 0;
		B_TeamScore = 0;
	}

	// 팀대전 참여자 제거
	static public void removeList(PcInstance pc) {
		synchronized (joinList) {
			if (joinList.contains(pc)) {
				joinList.remove(pc);
				if (!pc.isTeamBattleDead()) {
					if (pc.getBattleTeam() == A_Team.getUid())
						A_TeamScore -= 1;
					else if (pc.getBattleTeam() == B_Team.getUid())
						B_TeamScore -= 1;
				}
			}
		}

		hpUpdate(pc, false);

		if (pc.getClanId() > 0) {
			Clan clan = ClanController.find(pc.getClanId());
			clan.removeList(pc);
		}

		if (pc.getTempName() != null)
			pc.setName(pc.getTempName());
		if (pc.getTempTitle() != null)
			pc.setTitle(pc.getTempTitle());
		if (pc.getTempClanName() != null)
			pc.setClanName(pc.getTempClanName());
		pc.setClanId(pc.getTempClanId());
		pc.setClanGrade(pc.getTempClanGrade());
		pc.setBattleTeam(0);
		pc.setTransparent(false);

		Kingdom kingdom = KingdomController.find(4);
		if (kingdom.getClanId() > 0)
			pc.toSender(S_KingdomAgent.clone(BasePacketPooling.getPool(S_KingdomAgent.class), 4, kingdom.getAgentId()));

		removePoly(pc);

		int[] loc = Lineage.getHomeXY();
		pc.toTeleport(loc[0], loc[1], loc[2], true);
	}
	
	/**
	 * 팀대전 입장전 변신정보를 추출하여 복구
	 * 2017-10-11
	 * by all-night
	 */
	static public void removePoly(PcInstance pc) {
		synchronized (polyList) {
			if (polyList.containsKey(pc.getObjectId())) {
				TeamBattlePoly poly = polyList.get(pc.getObjectId());
				
				if (poly != null) {
					if (poly.getPolyTime() > 0) {
						ShapeChange.init(pc, pc, poly.getPoly(), poly.getPolyTime(), 1);
					} else {
						BuffController.remove(pc, ShapeChange.class);
						
						pc.setGfx(pc.getClassGfx());
						if (pc.getInventory() != null && pc.getInventory().getSlot(Lineage.SLOT_WEAPON) != null)
							pc.setGfxMode(pc.getClassGfxMode() + pc.getInventory().getSlot(Lineage.SLOT_WEAPON).getItem().getGfxMode());
						else
							pc.setGfxMode(pc.getClassGfxMode());
					}
					
					polyList.remove(pc.getObjectId());
				}
			} else {
				BuffController.remove(pc, ShapeChange.class);
				
				pc.setGfx(pc.getClassGfx());
				if (pc.getInventory() != null && pc.getInventory().getSlot(Lineage.SLOT_WEAPON) != null)
					pc.setGfxMode(pc.getClassGfxMode() + pc.getInventory().getSlot(Lineage.SLOT_WEAPON).getItem().getGfxMode());
				else
					pc.setGfxMode(pc.getClassGfxMode());
				pc.toSender(S_ObjectPoly.clone(BasePacketPooling.getPool(S_ObjectPoly.class), pc), true);
			}
		}
	}
	
	static public void setDead(PcInstance pc) {
		pc.setTeamBattleDead(true);
		TeamBattleController.hpUpdate(pc, false);
		pc.setName("");
								
		if (pc.getBattleTeam() == A_Team.getUid()) {
			A_TeamScore -= 1;
			pc.toSender(S_ClanWar.clone(BasePacketPooling.getPool(S_ClanWar.class), 1, A_Team.getName(), B_Team.getName()));
		} else if (pc.getBattleTeam() == B_Team.getUid()) {
			B_TeamScore -= 1;
			pc.toSender(S_ClanWar.clone(BasePacketPooling.getPool(S_ClanWar.class), 1, B_Team.getName(), A_Team.getName()));
		}
		
		Clan clan = ClanController.find(pc.getClanId());
		clan.removeList(pc);
		pc.setClanId(0);
		pc.setClanName("");

		if (A_TeamScore > 0 && B_TeamScore > 0) {
			pc.setTransparent(true);
			
			if (pc.getBattleTeam() == A_Team.getUid())
				pc.toTeleport(Util.random(32736, 32741), 32808, Lineage.teamBattleMap, false);
			else if (pc.getBattleTeam() == B_Team.getUid())
				pc.toTeleport(Util.random(32736, 32741), 32786, Lineage.teamBattleMap, false);
		}
	}

	// 팀대전 참여자 확인
	static public boolean checkList(PcInstance pc) {
		synchronized (joinList) {
			if (joinList.contains(pc)) {
				return true;
			} else {
				return false;
			}
		}
	}

	static public List<PcInstance> getJoinList() {
		synchronized (joinList) {
			return joinList;
		}
	}
	
	static public int getJoinListSize() {
		synchronized (joinList) {
			return joinList.size();
		}
	}
}
