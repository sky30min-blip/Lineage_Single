package lineage.network.packet.client;

import kuberaitem.darkelf_potion;
import lineage.database.PolyDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_CharacterSpMr;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.world.controller.AgitController;
import lineage.world.controller.CharacterController;
import lineage.world.controller.TeamBattleController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.ClanController;
import lineage.world.controller.FishingController;
import lineage.world.controller.GiranClanLordController;
import lineage.world.controller.PartyController;
import lineage.world.controller.SummonController;
import lineage.world.controller.TradeController;
import lineage.world.controller.WeddingController;
import lineage.world.controller.DollRaceController;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.all_night.StatClear;
import lineage.world.object.magic.ShapeChange;
import lineage.world.object.npc.Cash_Market_telepoter;
import lineage.world.object.npc.GoddessAgata;
import lineage.world.object.npc.Market_telepoter;

public class C_Ask extends ClientBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length) {
		if (bp == null)
			bp = new C_Ask(data, length);
		else
			((C_Ask) bp).clone(data, length);
		return bp;
	}

	public C_Ask(byte[] data, int length) {
		clone(data, length);
	}

	@Override
	public BasePacket init(PcInstance pc) {
		// 버그 방지.
		if (!isRead(3) || pc == null || pc.isWorldDelete())
			return this;

		int type = readH(); // 구분
		int yn = readC(); // 승낙 여부
		switch (type) {
		case 97: // 가입
			ClanController.toJoinFinal(pc, yn == 1);
			break;
		case 180: // 변신
			String name = readS();
			if (name != null && name.length() > 0) {
				if (name.equalsIgnoreCase("랭커 변신")) {
					name = PolyDatabase.toRankPolyMorph(pc, name, false);
					
					// 버그방지
					if (name.equalsIgnoreCase("랭커 변신"))
						return this;
				}
				
				if (name.equalsIgnoreCase("빠른 변신")) {
					// 버그방지
					if (pc.getQuickPolymorph() == null || pc.getQuickPolymorph().equalsIgnoreCase("") || pc.getQuickPolymorph().length() < 1) {
						return this;
					} else {
						name = pc.getQuickPolymorph();
					}
				}
				
				if (pc.isTempPoly())
					ShapeChange.init(pc, pc, PolyDatabase.getPolyName(name), 1800, pc.getTempPolyScroll().getBless());
				else
					ShapeChange.init(pc, pc, PolyDatabase.getPolyName(name), 7200, 1);
			}
			break;
		case 217: // 전쟁 선포 혈전 부분
			ClanController.toWarFinal(pc, yn == 1);
			break;
		case 221: // 항복 요청
			ClanController.toWarSubmissionFinal(pc, yn == 1);
			break;
		case 252: // 트레이드
			TradeController.toTrade(pc, yn == 1);
			break;
		case 321: // 부활 여부
			if (yn == 1)
				pc.toRevivalFinal(null);
			break;
		case 325: // 펫 이름 바꾸기
			if (pc.getSummon().getTempSi() == null || !pc.getSummon().getTempSi().getName().startsWith("$")) {
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 326));
			} else {
				SummonController.updateName(pc.getSummon().getTempSi(), readS());
			}
			break;
		case 422: // 파티
			PartyController.toParty(pc, yn == 1);
			break;
		case 769: // 혈맹파티
			PartyController.toClanParty(pc, yn == 1);
			break;
		case 479: // 새로운 스탯 추가
			if (pc.toLvStat(false)) {
				String space = "        ";
				String stat = readS();
				if (stat.equalsIgnoreCase("str")) {
					if (pc.getStr() + pc.getLvStr() < Lineage.stat_str) {
						if (pc.getResetBaseStat() > 0) {
							if (pc.getStr() < 20) {
								pc.setStr(pc.getStr() + 1);
								pc.setResetBaseStat(pc.getResetBaseStat() - 1);
								CharacterController.toResetBaseStat(pc);
							} else {
								ChattingController.toChatting(pc, "Str의 기초 능력치 최대값은 20 입니다.", Lineage.CHATTING_MODE_MESSAGE);
								return this;
							}
						} else {
							if (pc.getResetLevelStat() > 0) {
								CharacterController.toResetLevelStat(pc);
								pc.setResetLevelStat(pc.getResetLevelStat() - 1);
							} else {
								pc.setLevelUpStat(pc.getLevelUpStat() - 1);
							}
							
							pc.setLvStr(pc.getLvStr() + 1);
						}
						ChattingController.toChatting(pc, String.format("%s\\fSStr:%d  \\fVDex:%d  Con:%d  Int:%d  Wis:%d  Cha:%d", space, pc.getStr() + pc.getLvStr(), pc.getDex() + pc.getLvDex(),
								pc.getCon() + pc.getLvCon(), pc.getInt() + pc.getLvInt(), pc.getWis() + pc.getLvWis(), pc.getCha() + pc.getLvCha()), Lineage.CHATTING_MODE_MESSAGE);
					} else {
						ChattingController.toChatting(pc, String.format("[Str] 능력치의 최대값은 %d 입니다. 다른 능력치를 선택해 주세요.", Lineage.stat_str), Lineage.CHATTING_MODE_MESSAGE);
					}
				} else if (stat.equalsIgnoreCase("dex")) {
					if (pc.getDex() + pc.getLvDex() < Lineage.stat_dex) {
						if (pc.getResetBaseStat() > 0) {
							if (pc.getDex() < 18) {
								pc.setDex(pc.getDex() + 1);
								pc.setResetBaseStat(pc.getResetBaseStat() - 1);
								CharacterController.toResetBaseStat(pc);
							} else {
								ChattingController.toChatting(pc, "Dex의 기초 능력치 최대값은 18 입니다.", Lineage.CHATTING_MODE_MESSAGE);
								return this;
							}
						} else {							
							if (pc.getResetLevelStat() > 0) {
								CharacterController.toResetLevelStat(pc);
								pc.setResetLevelStat(pc.getResetLevelStat() - 1);
							} else {
								pc.setLevelUpStat(pc.getLevelUpStat() - 1);
							}
							
							pc.setLvDex(pc.getLvDex() + 1);
						}
						ChattingController.toChatting(pc, String.format("%s\\fVStr:%d  \\fSDex:%d  \\fVCon:%d  Int:%d  Wis:%d  Cha:%d", space, pc.getStr() + pc.getLvStr(), pc.getDex() + pc.getLvDex(),
								pc.getCon() + pc.getLvCon(), pc.getInt() + pc.getLvInt(), pc.getWis() + pc.getLvWis(), pc.getCha() + pc.getLvCha()), Lineage.CHATTING_MODE_MESSAGE);
					} else {
						ChattingController.toChatting(pc, String.format("[Dex] 능력치의 최대값은 %d 입니다. 다른 능력치를 선택해 주세요.", Lineage.stat_dex), Lineage.CHATTING_MODE_MESSAGE);
					}
				} else if (stat.equalsIgnoreCase("con")) {
					if (pc.getCon() + pc.getLvCon() < Lineage.stat_con) {
						if (pc.getResetBaseStat() > 0) {
							if (pc.getCon() < 18) {
								pc.setCon(pc.getCon() + 1);
								pc.setResetBaseStat(pc.getResetBaseStat() - 1);
								CharacterController.toResetBaseStat(pc);
							} else {
								ChattingController.toChatting(pc, "Con의 기초 능력치 최대값은 18 입니다.", Lineage.CHATTING_MODE_MESSAGE);
								return this;
							}
						} else {
							if (pc.getResetLevelStat() > 0) {
								CharacterController.toResetLevelStat(pc);
								pc.setResetLevelStat(pc.getResetLevelStat() - 1);
							} else {
								pc.setLevelUpStat(pc.getLevelUpStat() - 1);
							}
							
							pc.setLvCon(pc.getLvCon() + 1);
						}
						ChattingController.toChatting(pc, String.format("%s\\fVStr:%d  Dex:%d  \\fSCon:%d  \\fVInt:%d  Wis:%d  Cha:%d", space, pc.getStr() + pc.getLvStr(), pc.getDex() + pc.getLvDex(),
								pc.getCon() + pc.getLvCon(), pc.getInt() + pc.getLvInt(), pc.getWis() + pc.getLvWis(), pc.getCha() + pc.getLvCha()), Lineage.CHATTING_MODE_MESSAGE);
					} else {
						ChattingController.toChatting(pc, String.format("[Con] 능력치의 최대값은 %d 입니다. 다른 능력치를 선택해 주세요.", Lineage.stat_con), Lineage.CHATTING_MODE_MESSAGE);
					}
				} else if (stat.equalsIgnoreCase("int")) {
					if (pc.getInt() + pc.getLvInt() < Lineage.stat_int) {
						if (pc.getResetBaseStat() > 0) {
							if (pc.getInt() < 18) {
								pc.setInt(pc.getInt() + 1);
								pc.setResetBaseStat(pc.getResetBaseStat() - 1);
								CharacterController.toResetBaseStat(pc);
							} else {
								ChattingController.toChatting(pc, "Int의 기초 능력치 최대값은 18 입니다.", Lineage.CHATTING_MODE_MESSAGE);
								return this;
							}
						} else {
							if (pc.getResetLevelStat() > 0) {
								CharacterController.toResetLevelStat(pc);
								pc.setResetLevelStat(pc.getResetLevelStat() - 1);
							} else {
								pc.setLevelUpStat(pc.getLevelUpStat() - 1);
							}
							
							pc.setLvInt(pc.getLvInt() + 1);
						}
						ChattingController.toChatting(pc, String.format("%s\\fVStr:%d  Dex:%d  Con:%d  \\fSInt:%d  \\fVWis:%d  Cha:%d", space, pc.getStr() + pc.getLvStr(), pc.getDex() + pc.getLvDex(),
								pc.getCon() + pc.getLvCon(), pc.getInt() + pc.getLvInt(), pc.getWis() + pc.getLvWis(), pc.getCha() + pc.getLvCha()), Lineage.CHATTING_MODE_MESSAGE);
					} else {
						ChattingController.toChatting(pc, String.format("[Int] 능력치의 최대값은 %d 입니다. 다른 능력치를 선택해 주세요.", Lineage.stat_int), Lineage.CHATTING_MODE_MESSAGE);
					}
				} else if (stat.equalsIgnoreCase("wis")) {
					if (pc.getWis() + pc.getLvWis() < Lineage.stat_wis) {
						if (pc.getResetBaseStat() > 0) {
							if (pc.getWis() < 18) {
								pc.setWis(pc.getWis() + 1);
								pc.setResetBaseStat(pc.getResetBaseStat() - 1);
								CharacterController.toResetBaseStat(pc);
							} else {
								ChattingController.toChatting(pc, "Wis의 기초 능력치 최대값은 18 입니다.", Lineage.CHATTING_MODE_MESSAGE);
								return this;
							}
						} else {
							if (pc.getResetLevelStat() > 0) {
								CharacterController.toResetLevelStat(pc);
								pc.setResetLevelStat(pc.getResetLevelStat() - 1);
							} else {
								pc.setLevelUpStat(pc.getLevelUpStat() - 1);
							}
							
							pc.setLvWis(pc.getLvWis() + 1);
						}
						ChattingController.toChatting(pc, String.format("%s\\fVStr:%d  Dex:%d  Con:%d  Int:%d  \\fSWis:%d  \\fVCha:%d", space, pc.getStr() + pc.getLvStr(), pc.getDex() + pc.getLvDex(),
								pc.getCon() + pc.getLvCon(), pc.getInt() + pc.getLvInt(), pc.getWis() + pc.getLvWis(), pc.getCha() + pc.getLvCha()), Lineage.CHATTING_MODE_MESSAGE);
					} else {
						ChattingController.toChatting(pc, String.format("[Wis] 능력치의 최대값은 %d 입니다. 다른 능력치를 선택해 주세요.", Lineage.stat_wis), Lineage.CHATTING_MODE_MESSAGE);
					}
				} else if (stat.equalsIgnoreCase("cha")) {
					if (pc.getCha() + pc.getLvCha() < Lineage.stat_cha) {
						if (pc.getResetBaseStat() > 0) {
							if (pc.getCha() < 18) {
								pc.setCha(pc.getCha() + 1);
								pc.setResetBaseStat(pc.getResetBaseStat() - 1);
								CharacterController.toResetBaseStat(pc);
							} else {
								ChattingController.toChatting(pc, "Cha의 기초 능력치 최대값은 18 입니다.", Lineage.CHATTING_MODE_MESSAGE);
								return this;
							}
						} else {							
							if (pc.getResetLevelStat() > 0) {
								CharacterController.toResetLevelStat(pc);
								pc.setResetLevelStat(pc.getResetLevelStat() - 1);
							} else {
								pc.setLevelUpStat(pc.getLevelUpStat() - 1);
							}
							
							pc.setLvCha(pc.getLvCha() + 1);
						}
						ChattingController.toChatting(pc, String.format("%s\\fVStr:%d  Dex:%d  Con:%d  Int:%d  Wis:%d  \\fSCha:%d", space, pc.getStr() + pc.getLvStr(), pc.getDex() + pc.getLvDex(),
								pc.getCon() + pc.getLvCon(), pc.getInt() + pc.getLvInt(), pc.getWis() + pc.getLvWis(), pc.getCha() + pc.getLvCha()), Lineage.CHATTING_MODE_MESSAGE);
					} else {
						ChattingController.toChatting(pc, String.format("[Cha] 능력치의 최대값은 %d 입니다. 다른 능력치를 선택해 주세요.", Lineage.stat_cha), Lineage.CHATTING_MODE_MESSAGE);
					}
				}
				
				pc.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), pc));
				pc.toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), pc));
			}
			break;
		case 512: // 아지트 집 이름 변경.
			AgitController.toNameChange(pc, readS());
			break;
		case 653: // 이혼
			WeddingController.toDivorceFinal(pc, yn == 1);
			break;
		case 654: // 청혼.
			WeddingController.toProposeFinal(pc, yn == 1);
			break;
		case 729: // 콜 클렌
			ClanController.toCallClan(pc, yn == 1);
			break;
		case 770: // 스탯 초기화를 하시겠습니까? (y/N)
			StatClear stat = new StatClear();
			stat.toAsk(pc, yn == 1);
			break;
		case 771: // 변신이 해제됩니다. 계속 하시겠습니까? (y/N)
			FishingController.toAsk(pc, yn == 1);
			break;
		case 772: // %0 혈맹에 가입하시겠습니까? (y/N)
			GiranClanLordController.toAsk(pc, yn == 1);
			break;
		case 773: // 팀대전에 참여하시겠습니까? (y/N)
			TeamBattleController.toAsk(pc, yn == 1);
			break;
		case 774: 
			darkelf_potion dlf = new darkelf_potion();
			dlf.toAsk(pc, yn == 1);
			

			break;
		case 775: // %0아데나가 필요합니다. 경험치를 복구하시겠습니까? (y/N)
			GoddessAgata agata = new GoddessAgata();
			agata.toAsk(pc, yn == 1);
			break;
//		case 777: // 시장으로 이동하시겠습니까? (y/N)
//			Market_telepoter market = new Market_telepoter();
//			market.toAsk(pc, yn == 1);
//			break;
		case 778: // 자동낚시를 시작하시겠습니까? (y/N)
			FishingController.startAutoFishing(pc, yn == 1);
			break;
		case 779: // 인형경주에 참여하시겠습니까? (y/N)
			DollRaceController.toAsk(pc, yn == 1);
			break;
		case 782: // '%s'님의 장비 확인에 %s(%,d)가 소모됩니다.
			pc.pcItemCheck(yn == 1);
			break;
		case 783: // '%s'로 클래스를 변경하시겠습니까? 게임이 종료됩니다.
			if (pc.getClassChangeScroll() != null && pc.getClassChangeType() != null) {
				pc.getClassChangeScroll().toAsk(pc, yn == 1);
			}
			break;
		case 784: // 캐릭터가 창고에 보관되고 게임이 종료됩니다.
			if (pc.getCharacterMarble() != null) {
				pc.getCharacterMarble().toAsk(pc, yn == 1);
			}
			
			pc.setCharacterMarble(null);
			break;
		case 785: // 캐릭터를 슬롯에 등록하시겠습니까?
			if (pc.getCharacterMarble() != null) {
				pc.getCharacterMarble().toAsk2(pc, yn == 1);
			}
			
			pc.setCharacterMarble(null);
			break;

		}

		return this;
	}
}
