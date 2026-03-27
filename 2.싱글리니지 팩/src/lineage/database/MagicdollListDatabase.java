package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.MagicdollList;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_CharacterSpMr;
import lineage.network.packet.server.S_CharacterStat;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.controller.ChattingController;
import lineage.world.object.instance.PcInstance;

public class MagicdollListDatabase {

	static private List<MagicdollList> list;

	static public void init(Connection con) {
		TimeLine.start("MagicdollListDatabase..");

		list = new ArrayList<MagicdollList>();

		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM magicdoll_list");
			rs = st.executeQuery();
			while (rs.next()) {
				MagicdollList mdl = new MagicdollList();
				mdl.setItemName(rs.getString("item_name"));
				mdl.setMaterialName(rs.getString("material_name"));
				mdl.setMaterialCount(rs.getInt("material_count"));
				mdl.setDollName(rs.getString("doll_name"));
				mdl.setDollGfx(rs.getInt("doll_gfx"));
				mdl.setDollBuffType(rs.getString("doll_buff_type"));
				mdl.setDollBuffEffect(rs.getInt("doll_buff_effect"));
				mdl.setDollContinuous(rs.getInt("doll_continuous"));

				list.add(mdl);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", MagicdollListDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		TimeLine.end();
	}

	static public MagicdollList find(String name) {
		for (MagicdollList mdl : list) {
			if (mdl.getItemName().equalsIgnoreCase(name))
				return mdl;
		}
		return null;
	}

	/**
	 * 버프 타입별 버프 이팩트값 찾아서 리턴함.
	 * 
	 * @param type
	 * @return
	 */
	static public int getBuffEffect(String type) {
		for (MagicdollList mdl : list) {
			if (mdl.getDollBuffType().equalsIgnoreCase(type))
				return mdl.getDollBuffEffect();
		}
		return 0;
	}

	/**
	 * 매직인형 착용 및 해제시 옵션 처리 함수.
	 * 
	 * @param pc
	 * @param mdl
	 * @param enabled
	 */
	static public void toOption(PcInstance pc, MagicdollList mdl, boolean enabled) {
		if (mdl == null)
			return;
		if (enabled) {
			// 1단계 마법인형
			if (mdl.getDollBuffType().equalsIgnoreCase("돌 골렘")) {
				pc.setMagicdollStoneGolem(enabled);
				pc.setDynamicReduction(pc.getDynamicReduction() + 1);
				ChattingController.toChatting(pc, "돌 골렘: 대미지 감소+1 ", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("늑대인간")) {
				pc.setMagicdollWerewolf(enabled);
				ChattingController.toChatting(pc, "늑대인간: 근거리 공격 시 일정 확률로 추가 대미지+15 ", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("버그베어")) {
				pc.setMagicdollBugBear(enabled);
				ChattingController.toChatting(pc, "버그베어: 소지 무게 증가+500 ", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("크러스트시안")) {
				pc.setMagicdollHermitCrab(enabled);
				ChattingController.toChatting(pc, "크러스트시안: 원거리 공격 시 일정 확률로 추가 대미지+15 ", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("에티")) {
				pc.setMagicdollYeti(enabled);
				pc.setDynamicAc(pc.getDynamicAc() + 3);
				pc.setDynamicMagicCritical(pc.getDynamicMagicCritical() + 1);
				ChattingController.toChatting(pc, "에티: AC-3, 마법 치명타+1% ", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("목각")) {
				pc.setMagicdollBasicWood(enabled);
				pc.setDynamicHp(pc.getDynamicHp() + 50);
				ChattingController.toChatting(pc, "목각: 최대 HP+50 ", Lineage.CHATTING_MODE_MESSAGE);
				pc.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), pc));
				// 2단계 마법인형
			} else if (mdl.getDollBuffType().equalsIgnoreCase("서큐버스")) {
				pc.setMagicdollsuccubus(enabled);
				pc.setMagicdollTimeMpTic(64);
				pc.setMagicdollMpTic(15);
				ChattingController.toChatting(pc, "서큐버스: 64초당 MP 회복+15 ", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("장로")) {
				pc.setMagicdollElder(enabled);
				pc.setMagicdollTimeMpTic(64);
				pc.setMagicdollMpTic(15);
				ChattingController.toChatting(pc, "장로: 64초당 MP 회복+15", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("코카트리스")) {
				pc.setMagicdollCockatrice(enabled);
				pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() + 1);
				pc.setDynamicAddHitBow(pc.getDynamicAddHitBow() + 1);
				ChattingController.toChatting(pc, "코카트리스: 원거리 대미지+1, 원거리 명중+1", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("눈사람")) {
				pc.setMagicdollSnowMan(enabled);
				pc.setDynamicAddDmg(pc.getDynamicAddDmg() + 1);
				pc.setDynamicAddHit(pc.getDynamicAddHit() + 1);
				ChattingController.toChatting(pc, "눈사람: 근거리 대미지+1, 근거리 명중+1", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("인어")) {
				pc.setMagicdollMermaid(enabled);
				pc.setDynamicExp(pc.getDynamicExp() + 0.05);
				ChattingController.toChatting(pc, "인어: 경험치 보너스+5%", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("라바 골렘")) {
				pc.setMagicdollLavaGolem(enabled);
				pc.setDynamicAddDmg(pc.getDynamicAddDmg() + 1);
				pc.setDynamicReduction(pc.getDynamicReduction() + 1);
				ChattingController.toChatting(pc, "라바 골렘: 근거리 대미지+1, 대미지 감소+1", Lineage.CHATTING_MODE_MESSAGE);
				// 3단계 마법인형
			} else if (mdl.getDollBuffType().equalsIgnoreCase("자이언트")) {
				pc.setMagicdollGiant(enabled);
				pc.setDynamicExp(pc.getDynamicExp() + 0.1);
				pc.setDynamicReduction(pc.getDynamicReduction() + 1);
				ChattingController.toChatting(pc, "자이언트: 경험치 보너스+10%, 대미지 감소+1", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("흑장로")) {
				pc.setMagicdollBlackElder(enabled);
				pc.setMagicdollTimeMpTic(64);
				pc.setMagicdollMpTic(15);
				ChattingController.toChatting(pc, "흑장로: 64초당 MP 회복+15, 일정확률 콜 라이트닝 발동", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("서큐버스 퀸")) {
				pc.setMagicdollsuccubusQueen(enabled);
				pc.setMagicdollTimeMpTic(64);
				pc.setMagicdollMpTic(15);
				pc.setDynamicSp(pc.getDynamicSp() + 1);
				ChattingController.toChatting(pc, "서큐버스 퀸: 64초당 MP 회복+15, SP+1", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("드레이크")) {
				pc.setMagicdollDrake(enabled);
				pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() + 2);
				pc.setMagicdollTimeMpTic(64);
				pc.setMagicdollMpTic(6);
				ChattingController.toChatting(pc, "드레이크: 원거리 대미지+2, 64초당 MP 회복+6", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("킹 버그베어")) {
				pc.setMagicdollKingBugBear(enabled);
				pc.setDynamicStunResist(pc.getDynamicStunResist() + 0.08);
				pc.setMagicdollTimeMpTic(64);
				pc.setMagicdollMpTic(10);
				ChattingController.toChatting(pc, "킹 버그베어: 스턴 내성+8, 64초당 MP 회복+10", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("다이아몬드 골렘")) {
				pc.setMagicdollDiamondGolem(enabled);
				pc.setDynamicReduction(pc.getDynamicReduction() + 2);
				pc.setDynamicAddPvpReduction(pc.getDynamicAddPvpReduction() + 1);
				ChattingController.toChatting(pc, "다이아몬드 골렘: 대미지 감소+2, PvP 대미지 감소+1", Lineage.CHATTING_MODE_MESSAGE);
				// 4단계 마법인형
			} else if (mdl.getDollBuffType().equalsIgnoreCase("리치")) {
				pc.setMagicdollRich(enabled);
				pc.setDynamicSp(pc.getDynamicSp() + 2);
				pc.setDynamicHp(pc.getDynamicHp() + 80);
				ChattingController.toChatting(pc, "리치: SP+2, 최대 HP+80", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("사이클롭스")) {
				pc.setMagicdollCyclops(enabled);
				pc.setDynamicAddDmg(pc.getDynamicAddDmg() + 2);
				pc.setDynamicAddHit(pc.getDynamicAddHit() + 2);
				pc.setDynamicStunResist(pc.getDynamicStunResist() + 0.12);
				ChattingController.toChatting(pc, "사이클롭스: 근거리 대미지+2, 근거리 명중+2, 스턴 내성+12", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("나이트발드")) {
				pc.setMagicdollKnightVald(enabled);
				pc.setDynamicAddDmg(pc.getDynamicAddDmg() + 2);
				pc.setDynamicAddHit(pc.getDynamicAddHit() + 2);
				pc.setDynamicStunHit(pc.getDynamicStunHit() + 0.05);
				ChattingController.toChatting(pc, "나이트발드: 근거리 대미지+2, 근거리 명중+2, 스턴 명중+5", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("시어")) {
				pc.setMagicdollSeer(enabled);
				pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() + 5);
				pc.setMagicdollTimeHpTic(32);
				pc.setMagicdollHpTic(30);
				ChattingController.toChatting(pc, "시어: 원거리 대미지+5, 32초당 HP 회복+30", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("아이리스")) {
				pc.setMagicdollIris(enabled);
				pc.setDynamicAddPvpDmg(pc.getDynamicAddPvpDmg() + 5);
				pc.setDynamicReduction(pc.getDynamicReduction() + 3);
				ChattingController.toChatting(pc, "아이리스: PvP 대미지+5, 대미지 감소+3", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("뱀파이어")) {
				pc.setMagicdollVampire(enabled);
				pc.setDynamicAddPvpDmg(pc.getDynamicAddPvpDmg() + 3);
				pc.setMagicdollTimeHpTic(32);
				pc.setMagicdollHpTic(30);
				ChattingController.toChatting(pc, "뱀파이어: PvP 대미지+3, 32초당 HP 회복+30", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("머미로드")) {
				pc.setMagicdollMummylord(enabled);
				pc.setDynamicSp(pc.getDynamicSp() + 1);
				pc.setDynamicMagicCritical(pc.getDynamicMagicCritical() + 1);
				pc.setMagicdollTimeMpTic(64);
				pc.setMagicdollMpTic(15);
				ChattingController.toChatting(pc, "머미로드: SP+1, 마법 치명타+1%, 64초당 MP 회복+15", Lineage.CHATTING_MODE_MESSAGE);
				// 5단계 마법인형
			} else if (mdl.getDollBuffType().equalsIgnoreCase("데몬")) {
				pc.setMagicdollDemon(enabled);
				pc.setDynamicStunHit(pc.getDynamicStunHit() + 0.1);
				pc.setDynamicStunResist(pc.getDynamicStunResist() + 0.12);
				pc.setDynamicExp(pc.getDynamicExp() + 0.15);
				ChattingController.toChatting(pc, "데몬: 스턴 명중+10, 스턴 내성+12 , 경험치 보너스+15%", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("데스나이트")) {
				pc.setMagicdollDeathKnight(enabled);
				pc.setDynamicReduction(pc.getDynamicReduction() + 7);
				pc.setDynamicExp(pc.getDynamicExp() + 0.25);
				ChattingController.toChatting(pc, "데스나이트: 대미지 감소+7, 경험치 보너스+25%, 일정확률 헬파이어 발동", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("바란카")) {
				pc.setMagicdollBaranka(enabled);
				pc.setDynamicStunResist(pc.getDynamicStunResist() + 0.10);
				pc.setDynamicAddPvpDmg(pc.getDynamicAddPvpDmg() + 5);
				pc.setDynamicAddPvpReduction(pc.getDynamicAddPvpReduction() + 3);
				pc.setDynamicCritical(pc.getDynamicCritical() + 10);
				pc.setDynamicBowCritical(pc.getDynamicBowCritical() + 10);
				pc.setDynamicExp(pc.getDynamicExp() + 0.15);
				ChattingController.toChatting(pc, "바란카: 스턴 내성+10, 근거리/원거리 치명타+10% , 경험치 보너스+15%", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("타락")) {
				pc.setMagicdollTarak(enabled);
				pc.setDynamicStunResist(pc.getDynamicStunResist() + 0.1);
				pc.setDynamicSp(pc.getDynamicSp() + 3);
				pc.setDynamicMagicHit(pc.getDynamicMagicHit() + 5);
				pc.setDynamicExp(pc.getDynamicExp() + 0.15);
				ChattingController.toChatting(pc, "타락: 스턴 내성+10, SP+3, 마법 명중+5 , 경험치 보너스+15%", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("바포메트")) {
				pc.setMagicdollBaphomet(enabled);
				pc.setDynamicStunResist(pc.getDynamicStunResist() + 0.1);
				pc.setDynamicMagicCritical(pc.getDynamicMagicCritical() + 5);
				pc.setDynamicMagicDmg(pc.getDynamicMagicDmg() + 2);
				pc.setDynamicExp(pc.getDynamicExp() + 0.15);
				ChattingController.toChatting(pc, "바포메트: 스턴 내성+10, 마법 치명타+5%, 마법 대미지+2 , 경험치 보너스+15%", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("얼음여왕")) {
				pc.setMagicdollIceQueen(enabled);
				pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() + 5);
				pc.setDynamicAddHitBow(pc.getDynamicAddHitBow() + 5);
				pc.setDynamicStunResist(pc.getDynamicStunResist() + 0.1);
				pc.setDynamicExp(pc.getDynamicExp() + 0.15);
				ChattingController.toChatting(pc, "얼음여왕: 원거리 대미지+5, 원거리 명중+5, 스턴 내성+10 , 경험치 보너스+15%", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("커츠")) {
				pc.setMagicdollKouts(enabled);
				pc.setDynamicReduction(pc.getDynamicReduction() + 3);
				pc.setDynamicStunResist(pc.getDynamicStunResist() + 0.1);
				pc.setDynamicAddDmg(pc.getDynamicAddDmg() + 5);
				pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() + 5);
				pc.setDynamicExp(pc.getDynamicExp() + 0.15);
				ChattingController.toChatting(pc, "커츠: 추가 대미지+5, 대미지 감소+3, 스턴 내성+10 , 경험치 보너스+15%", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("안타라스")) {
				pc.setMagicdollAntaras(enabled);
				pc.setDynamicStunResist(pc.getDynamicStunResist() + 0.05);
				pc.setDynamicReduction(pc.getDynamicReduction() + 8);
				pc.setDynamicExp(pc.getDynamicExp() + 0.35);
				pc.setMagicdollTimeMpTic(64);
				pc.setMagicdollMpTic(20);
				ChattingController.toChatting(pc, "안타라스: 스턴 내성+5, 대미지 감소+8, 경험치 보너스+35%, 64초당 MP 회복+20", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("파푸리온")) {
				pc.setMagicdollPapoorion(enabled);
				pc.setDynamicSp(pc.getDynamicSp() + 8);
				pc.setDynamicMagicHit(pc.getDynamicMagicHit() + 8);
				pc.setDynamicStunResist(pc.getDynamicStunResist() + 0.08);
				pc.setDynamicExp(pc.getDynamicExp() + 0.20);
				pc.setMagicdollTimeMpTic(64);
				pc.setMagicdollMpTic(20);
				ChattingController.toChatting(pc, "파푸리온: SP+8, 마법 명중+8, 스턴 내성+8, 64초당 MP 회복+20 , 경험치 보너스+20%", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("린드비오르")) {
				pc.setMagicdollLindvior(enabled);
				pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() + 8);
				pc.setDynamicAddHitBow(pc.getDynamicAddHitBow() + 8);
				pc.setDynamicBowCritical(pc.getDynamicBowCritical() + 5);
				pc.setDynamicStunResist(pc.getDynamicStunResist() + 0.08);
				pc.setDynamicExp(pc.getDynamicExp() + 0.20);
				pc.setMagicdollTimeMpTic(64);
				pc.setMagicdollMpTic(15);
				ChattingController.toChatting(pc, "린드비오르: 원거리 대미지+8, 원거리 명중+8, 원거리 치명타+5%, 스턴 내성+8, 64초당 MP 회복+15 , 경험치 보너스+20%", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("발라카스")) {
				pc.setMagicdollValakas(enabled);
				pc.setDynamicAddDmg(pc.getDynamicAddDmg() + 8);
				pc.setDynamicAddHit(pc.getDynamicAddHit() + 8);
				pc.setDynamicCritical(pc.getDynamicCritical() + 5);
				pc.setDynamicStunHit(pc.getDynamicStunHit() + 0.1);
				pc.setDynamicExp(pc.getDynamicExp() + 0.20);
				pc.setMagicdollTimeMpTic(64);
				pc.setMagicdollMpTic(15);
				ChattingController.toChatting(pc, "발라카스: 근거리 대미지+8, 근거리 명중+8, 근거리 치명타+5%, 스턴 명중+10, 64초당 MP 회복+15 , 경험치 보너스+20%", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("각성 발라카스")) {
				pc.setMagicdollValakas(enabled);
				pc.setDynamicAddDmg(pc.getDynamicAddDmg() + 10);
				pc.setDynamicAddHit(pc.getDynamicAddHit() + 10);
				pc.setDynamicCritical(pc.getDynamicCritical() + 7);
				pc.setDynamicStunHit(pc.getDynamicStunHit() + 0.15);
				pc.setDynamicExp(pc.getDynamicExp() + 0.30);
				pc.setMagicdollTimeMpTic(64);
				pc.setMagicdollMpTic(15);
				ChattingController.toChatting(pc, "각성 발라카스: 근거리 대미지+10, 근거리 명중+10, 근거리 치명타+7%, 스턴 명중+15, 64초당 MP 회복+15 , 경험치 보너스+30%", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("각성 린드비오르")) {
				pc.setMagicdollLindvior(enabled);
				pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() + 10);
				pc.setDynamicAddHitBow(pc.getDynamicAddHitBow() + 10);
				pc.setDynamicBowCritical(pc.getDynamicBowCritical() + 10);
				pc.setDynamicStunResist(pc.getDynamicStunResist() + 0.1);
				pc.setDynamicExp(pc.getDynamicExp() + 0.30);
				pc.setMagicdollTimeMpTic(64);
				pc.setMagicdollMpTic(15);
				ChattingController.toChatting(pc, "각성 린드비오르: 원거리 대미지+10, 원거리 명중+10, 원거리 치명타+10%, 스턴 내성+10, 64초당 MP 회복+15 , 경험치 보너스+30%", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("각성 파푸리온")) {
				pc.setMagicdollPapoorion(enabled);
				pc.setDynamicSp(pc.getDynamicSp() + 10);
				pc.setDynamicMagicHit(pc.getDynamicMagicHit() + 10);
				pc.setDynamicStunResist(pc.getDynamicStunResist() + 0.1);
				pc.setDynamicExp(pc.getDynamicExp() + 0.30);
				pc.setMagicdollTimeMpTic(64);
				pc.setMagicdollMpTic(30);
				ChattingController.toChatting(pc, "각성 파푸리온: SP+10, 마법 명중+10, 스턴 내성+10, 64초당 MP 회복+30 , 경험치 보너스+30%", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("각성 안타라스")) {
				pc.setMagicdollAntaras(enabled);
				pc.setDynamicStunResist(pc.getDynamicStunResist() + 0.1);
				pc.setDynamicReduction(pc.getDynamicReduction() + 10);
				pc.setDynamicExp(pc.getDynamicExp() + 0.50);
				pc.setMagicdollTimeMpTic(64);
				pc.setMagicdollMpTic(20);
				ChattingController.toChatting(pc, "각성 안타라스: 스턴 내성+10, 대미지 감소+10, 경험치 보너스+50%, 64초당 MP 회복+20", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("군주")) {
				pc.setDynamicAddDmg(pc.getDynamicAddDmg() + 2);
				pc.setDynamicAddHit(pc.getDynamicAddHit() + 4);
				pc.setDynamicCritical(pc.getDynamicCritical() + 2);
				pc.setDynamicExp(pc.getDynamicExp() + 0.1);
				pc.setMagicdollTimeMpTic(64);
				pc.setMagicdollMpTic(5);
				ChattingController.toChatting(pc, "군주:근거리 대미지+2, 근거리 명중+4, 근거리 치명타+2%,  64초당 MP 회복+5, 경험치 보너스+10%", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("기사")) {
				pc.setDynamicAddDmg(pc.getDynamicAddDmg() + 2);
				pc.setDynamicAddHit(pc.getDynamicAddHit() + 4);
				pc.setDynamicCritical(pc.getDynamicCritical() + 2);
				pc.setDynamicExp(pc.getDynamicExp() + 0.1);
				pc.setMagicdollTimeMpTic(64);
				pc.setMagicdollMpTic(5);
				ChattingController.toChatting(pc, "기사: 근거리 대미지+2, 근거리 명중+4, 근거리 치명타+2%,  64초당 MP 회복+5, 경험치 보너스+10%", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("요정")) {
				pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() + 2);
				pc.setDynamicAddHitBow(pc.getDynamicAddHitBow() + 4);
				pc.setDynamicBowCritical(pc.getDynamicBowCritical() + 2);
				pc.setMagicdollTimeMpTic(64);
				pc.setMagicdollMpTic(5);
				pc.setDynamicExp(pc.getDynamicExp() + 0.1);
				ChattingController.toChatting(pc, "요정: 원거리 대미지+2, 원거리 명중+4, 원거리 치명타+2%, 64초당 MP 회복+5, 경험치 보너스+10%", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("마법사")) {
				pc.setDynamicSp(pc.getDynamicSp() + 3);
				pc.setDynamicMagicHit(pc.getDynamicMagicHit() + 2);
				pc.setMagicdollTimeMpTic(64);
				pc.setMagicdollMpTic(5);
				pc.setDynamicExp(pc.getDynamicExp() + 0.1);
				pc.setDynamicInt(pc.getDynamicInt() + 2);
				ChattingController.toChatting(pc, "마법사: SP+3, 마법 명중+2, 64초당 MP 회복+5, 경험치 보너스+10%, INT+2", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("진 군주")) {
				pc.setMagicdollAntaras(enabled);
				pc.setDynamicReduction(pc.getDynamicReduction() + 10);
				pc.setDynamicExp(pc.getDynamicExp() + 0.65);
				pc.setMagicdollTimeMpTic(64);
				pc.setMagicdollMpTic(15);
				pc.setDynamicAddPvpDmg(pc.getDynamicAddPvpDmg() + 10);
				ChattingController.toChatting(pc, "진 군주: 대미지 감소+10, 경험치 보너스+65%, 64초당 MP 회복+15, PvP 대미지+10", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("진 기사")) {
				pc.setDynamicAddDmg(pc.getDynamicAddDmg() + 6);
				pc.setDynamicAddHit(pc.getDynamicAddHit() + 8);
				pc.setDynamicCritical(pc.getDynamicCritical() + 5);
				pc.setDynamicStunHit(pc.getDynamicStunHit() + 0.1);
				pc.setMagicdollTimeMpTic(64);
				pc.setMagicdollMpTic(5);
				pc.setDynamicExp(pc.getDynamicExp() + 0.4);
				pc.setDynamicAddPvpDmg(pc.getDynamicAddPvpDmg() + 10);
				ChattingController.toChatting(pc, "진 기사: 근거리 대미지+6, 근거리 명중+8, 근거리 치명타+5%, 스턴 명중+10, 64초당 MP 회복+5, 경험치 보너스+40%, PvP 대미지+10", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("진 요정")) {
				pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() + 6);
				pc.setDynamicAddHitBow(pc.getDynamicAddHitBow() + 8);
				pc.setDynamicBowCritical(pc.getDynamicBowCritical() + 5);
				pc.setDynamicStunResist(pc.getDynamicStunResist() + 0.08);
				pc.setMagicdollTimeMpTic(64);
				pc.setMagicdollMpTic(5);
				pc.setDynamicExp(pc.getDynamicExp() + 0.4);
				pc.setDynamicAddPvpDmg(pc.getDynamicAddPvpDmg() + 10);
				ChattingController.toChatting(pc, "진 요정: 원거리 대미지+6, 원거리 명중+8, 원거리 치명타+5%, 스턴 내성+8, 64초당 MP 회복+5, 경험치 보너스+40%, PvP 대미지+10", Lineage.CHATTING_MODE_MESSAGE);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("진 마법사")) {
				pc.setDynamicSp(pc.getDynamicSp() + 8);
				pc.setDynamicMagicHit(pc.getDynamicMagicHit() + 8);
				pc.setDynamicStunResist(pc.getDynamicStunResist() + 0.08);
				pc.setMagicdollTimeMpTic(64);
				pc.setMagicdollMpTic(5);
				pc.setDynamicExp(pc.getDynamicExp() + 0.4);
				pc.setDynamicInt(pc.getDynamicInt() + 4);
				ChattingController.toChatting(pc, "진 마법사: SP+8, 마법 명중+8, 스턴 내성+8, 64초당 MP 회복+5, 경험치 보너스+40%, INT+4", Lineage.CHATTING_MODE_MESSAGE);
			}
		} else {
			// 1단계 마법인형
			if (mdl.getDollBuffType().equalsIgnoreCase("돌 골렘")) {
				pc.setMagicdollStoneGolem(enabled);
				pc.setDynamicReduction(pc.getDynamicReduction() - 1);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("늑대인간")) {
				pc.setMagicdollWerewolf(enabled);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("버그베어")) {
				pc.setMagicdollBugBear(enabled);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("크러스트시안")) {
				pc.setMagicdollHermitCrab(enabled);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("에티")) {
				pc.setMagicdollYeti(enabled);
				pc.setDynamicAc(pc.getDynamicAc() - 3);
				pc.setDynamicMagicCritical(pc.getDynamicMagicCritical() - 1);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("목각")) {
				pc.setMagicdollBasicWood(enabled);
				pc.setDynamicHp(pc.getDynamicHp() - 50);
				// 2단계 마법인형
			} else if (mdl.getDollBuffType().equalsIgnoreCase("서큐버스")) {
				pc.setMagicdollsuccubus(enabled);
				pc.setMagicdollTimeMpTic(0);
				pc.setMagicdollMpTic(0);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("장로")) {
				pc.setMagicdollElder(enabled);
				pc.setMagicdollTimeMpTic(0);
				pc.setMagicdollMpTic(0);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("코카트리스")) {
				pc.setMagicdollCockatrice(enabled);
				pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() - 1);
				pc.setDynamicAddHitBow(pc.getDynamicAddHitBow() - 1);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("눈사람")) {
				pc.setMagicdollSnowMan(enabled);
				pc.setDynamicAddDmg(pc.getDynamicAddDmg() - 1);
				pc.setDynamicAddHit(pc.getDynamicAddHit() - 1);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("인어")) {
				pc.setMagicdollMermaid(enabled);
				pc.setDynamicExp(pc.getDynamicExp() - 0.05);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("라바 골렘")) {
				pc.setMagicdollLavaGolem(enabled);
				pc.setDynamicAddDmg(pc.getDynamicAddDmg() - 1);
				pc.setDynamicReduction(pc.getDynamicReduction() - 1);
				// 3단계 마법인형
			} else if (mdl.getDollBuffType().equalsIgnoreCase("자이언트")) {
				pc.setMagicdollGiant(enabled);
				pc.setDynamicExp(pc.getDynamicExp() - 0.1);
				pc.setDynamicReduction(pc.getDynamicReduction() - 1);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("흑장로")) {
				pc.setMagicdollBlackElder(enabled);
				pc.setMagicdollTimeMpTic(0);
				pc.setMagicdollMpTic(0);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("서큐버스 퀸")) {
				pc.setMagicdollsuccubusQueen(enabled);
				pc.setMagicdollTimeMpTic(0);
				pc.setMagicdollMpTic(0);
				pc.setDynamicSp(pc.getDynamicSp() - 1);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("드레이크")) {
				pc.setMagicdollDrake(enabled);
				pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() - 2);
				pc.setMagicdollTimeMpTic(0);
				pc.setMagicdollMpTic(0);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("킹 버그베어")) {
				pc.setMagicdollKingBugBear(enabled);
				pc.setDynamicStunResist(pc.getDynamicStunResist() - 0.08);
				pc.setMagicdollTimeMpTic(0);
				pc.setMagicdollMpTic(0);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("다이아몬드 골렘")) {
				pc.setMagicdollDiamondGolem(enabled);
				pc.setDynamicReduction(pc.getDynamicReduction() - 2);
				pc.setDynamicAddPvpReduction(pc.getDynamicAddPvpReduction() - 1);
				// 4단계 마법인형
			} else if (mdl.getDollBuffType().equalsIgnoreCase("리치")) {
				pc.setMagicdollRich(enabled);
				pc.setDynamicSp(pc.getDynamicSp() - 2);
				pc.setDynamicHp(pc.getDynamicHp() - 80);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("사이클롭스")) {
				pc.setMagicdollCyclops(enabled);
				pc.setDynamicAddDmg(pc.getDynamicAddDmg() - 2);
				pc.setDynamicAddHit(pc.getDynamicAddHit() - 2);
				pc.setDynamicStunResist(pc.getDynamicStunResist() - 0.12);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("나이트발드")) {
				pc.setMagicdollKnightVald(enabled);
				pc.setDynamicAddDmg(pc.getDynamicAddDmg() - 2);
				pc.setDynamicAddHit(pc.getDynamicAddHit() - 2);
				pc.setDynamicStunHit(pc.getDynamicStunHit() - 0.05);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("시어")) {
				pc.setMagicdollSeer(enabled);
				pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() - 5);
				pc.setMagicdollTimeHpTic(0);
				pc.setMagicdollHpTic(0);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("아이리스")) {
				pc.setMagicdollIris(enabled);
				pc.setDynamicAddPvpDmg(pc.getDynamicAddPvpDmg() - 5);
				pc.setDynamicReduction(pc.getDynamicReduction() - 3);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("뱀파이어")) {
				pc.setMagicdollVampire(enabled);
				pc.setDynamicAddPvpDmg(pc.getDynamicAddPvpDmg() - 3);
				pc.setMagicdollTimeHpTic(0);
				pc.setMagicdollHpTic(0);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("머미로드")) {
				pc.setMagicdollMummylord(enabled);
				pc.setDynamicSp(pc.getDynamicSp() - 1);
				pc.setDynamicMagicCritical(pc.getDynamicMagicCritical() - 1);
				pc.setMagicdollTimeMpTic(0);
				pc.setMagicdollMpTic(0);
				// 5단계 마법인형
			} else if (mdl.getDollBuffType().equalsIgnoreCase("데몬")) {
				pc.setMagicdollDemon(enabled);
				pc.setDynamicStunHit(pc.getDynamicStunHit() - 0.1);
				pc.setDynamicStunResist(pc.getDynamicStunResist() - 0.12);
				pc.setDynamicExp(pc.getDynamicExp() - 0.15);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("데스나이트")) {
				pc.setMagicdollDeathKnight(enabled);
				pc.setDynamicReduction(pc.getDynamicReduction() - 7);
				pc.setDynamicExp(pc.getDynamicExp() - 0.25);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("바란카")) {
				pc.setMagicdollBaranka(enabled);
				pc.setDynamicStunResist(pc.getDynamicStunResist() - 0.10);
				pc.setDynamicAddPvpDmg(pc.getDynamicAddPvpDmg() - 5);
				pc.setDynamicAddPvpReduction(pc.getDynamicAddPvpReduction() - 3);
				pc.setDynamicCritical(pc.getDynamicCritical() - 10);
				pc.setDynamicBowCritical(pc.getDynamicBowCritical() - 10);
				pc.setDynamicExp(pc.getDynamicExp() - 0.15);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("타락")) {
				pc.setMagicdollTarak(enabled);
				pc.setDynamicStunResist(pc.getDynamicStunResist() - 0.1);
				pc.setDynamicSp(pc.getDynamicSp() - 3);
				pc.setDynamicMagicHit(pc.getDynamicMagicHit() - 5);
				pc.setDynamicExp(pc.getDynamicExp() - 0.15);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("바포메트")) {
				pc.setMagicdollBaphomet(enabled);
				pc.setDynamicStunResist(pc.getDynamicStunResist() - 0.1);
				pc.setDynamicMagicCritical(pc.getDynamicMagicCritical() - 5);
				pc.setDynamicMagicDmg(pc.getDynamicMagicDmg() - 2);
				pc.setDynamicExp(pc.getDynamicExp() - 0.15);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("얼음여왕")) {
				pc.setMagicdollIceQueen(enabled);
				pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() - 5);
				pc.setDynamicAddHitBow(pc.getDynamicAddHitBow() - 5);
				pc.setDynamicStunResist(pc.getDynamicStunResist() - 0.1);
				pc.setDynamicExp(pc.getDynamicExp() - 0.15);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("커츠")) {
				pc.setMagicdollKouts(enabled);
				pc.setDynamicReduction(pc.getDynamicReduction() - 3);
				pc.setDynamicStunResist(pc.getDynamicStunResist() - 0.1);
				pc.setDynamicAddDmg(pc.getDynamicAddDmg() - 5);
				pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() - 5);
				pc.setDynamicExp(pc.getDynamicExp() - 0.15);
			
			} else if (mdl.getDollBuffType().equalsIgnoreCase("안타라스")) {
				pc.setMagicdollAntaras(enabled);
				pc.setDynamicStunResist(pc.getDynamicStunResist() - 0.05);
				pc.setDynamicReduction(pc.getDynamicReduction() - 8);
				pc.setDynamicExp(pc.getDynamicExp() - 0.35);
				pc.setMagicdollTimeMpTic(0);
				pc.setMagicdollMpTic(0);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("파푸리온")) {
				pc.setMagicdollPapoorion(enabled);
				pc.setDynamicSp(pc.getDynamicSp() - 8);
				pc.setDynamicMagicHit(pc.getDynamicMagicHit() - 8);
				pc.setDynamicExp(pc.getDynamicExp() - 0.20);
				pc.setDynamicStunResist(pc.getDynamicStunResist() - 0.08);
				pc.setMagicdollTimeMpTic(0);
				pc.setMagicdollMpTic(0);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("린드비오르")) {
				pc.setMagicdollLindvior(enabled);
				pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() - 8);
				pc.setDynamicAddHitBow(pc.getDynamicAddHitBow() - 8);
				pc.setDynamicBowCritical(pc.getDynamicBowCritical() - 5);
				pc.setDynamicStunResist(pc.getDynamicStunResist() - 0.08);
				pc.setDynamicExp(pc.getDynamicExp() - 0.20);
				pc.setMagicdollTimeMpTic(0);
				pc.setMagicdollMpTic(0);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("발라카스")) {
				pc.setMagicdollValakas(enabled);
				pc.setDynamicAddDmg(pc.getDynamicAddDmg() - 8);
				pc.setDynamicAddHit(pc.getDynamicAddHit() - 8);
				pc.setDynamicCritical(pc.getDynamicCritical() - 5);
				pc.setDynamicStunHit(pc.getDynamicStunHit() - 0.1);
				pc.setDynamicExp(pc.getDynamicExp() - 0.20);
				pc.setMagicdollTimeMpTic(0);
				pc.setMagicdollMpTic(0);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("각성 발라카스")) {
				pc.setMagicdollValakas(enabled);
				pc.setDynamicAddDmg(pc.getDynamicAddDmg() - 10);
				pc.setDynamicAddHit(pc.getDynamicAddHit() - 10);
				pc.setDynamicCritical(pc.getDynamicCritical() - 7);
				pc.setDynamicStunHit(pc.getDynamicStunHit() - 0.15);
				pc.setDynamicExp(pc.getDynamicExp() - 0.30);
				pc.setMagicdollTimeMpTic(0);
				pc.setMagicdollMpTic(0);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("각성 린드비오르")) {
				pc.setMagicdollLindvior(enabled);
				pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() - 10);
				pc.setDynamicAddHitBow(pc.getDynamicAddHitBow() - 10);
				pc.setDynamicBowCritical(pc.getDynamicBowCritical() - 10);
				pc.setDynamicStunResist(pc.getDynamicStunResist() - 0.1);
				pc.setDynamicExp(pc.getDynamicExp() - 0.30);
				pc.setMagicdollTimeMpTic(0);
				pc.setMagicdollMpTic(0);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("각성 파푸리온")) {
				pc.setMagicdollPapoorion(enabled);
				pc.setDynamicSp(pc.getDynamicSp() - 10);
				pc.setDynamicMagicHit(pc.getDynamicMagicHit() - 10);
				pc.setDynamicStunResist(pc.getDynamicStunResist() - 0.1);
				pc.setDynamicExp(pc.getDynamicExp() - 0.30);
				pc.setMagicdollTimeMpTic(0);
				pc.setMagicdollMpTic(0);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("각성 안타라스")) {
				pc.setMagicdollAntaras(enabled);
				pc.setDynamicStunResist(pc.getDynamicStunResist() - 0.1);
				pc.setDynamicReduction(pc.getDynamicReduction() - 10);
				pc.setDynamicExp(pc.getDynamicExp() - 0.50);
				pc.setMagicdollTimeMpTic(0);
				pc.setMagicdollMpTic(0);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("군주")) {
				pc.setDynamicAddDmg(pc.getDynamicAddDmg() - 2);
				pc.setDynamicAddHit(pc.getDynamicAddHit() - 4);
				pc.setDynamicCritical(pc.getDynamicCritical() - 2);
				pc.setDynamicExp(pc.getDynamicExp() - 0.1);
				pc.setMagicdollTimeMpTic(0);
				pc.setMagicdollMpTic(0);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("기사")) {
				pc.setDynamicAddDmg(pc.getDynamicAddDmg() - 2);
				pc.setDynamicAddHit(pc.getDynamicAddHit() - 4);
				pc.setDynamicCritical(pc.getDynamicCritical() - 2);
				pc.setDynamicExp(pc.getDynamicExp() - 0.1);
				pc.setMagicdollTimeMpTic(0);
				pc.setMagicdollMpTic(0);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("요정")) {
				pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() - 2);
				pc.setDynamicAddHitBow(pc.getDynamicAddHitBow() - 4);
				pc.setDynamicBowCritical(pc.getDynamicBowCritical() - 2);
				pc.setMagicdollTimeMpTic(0);
				pc.setMagicdollMpTic(0);
				pc.setDynamicExp(pc.getDynamicExp() - 0.1);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("마법사")) {
				pc.setDynamicSp(pc.getDynamicSp() - 3);
				pc.setDynamicMagicHit(pc.getDynamicMagicHit() - 2);
				pc.setMagicdollTimeMpTic(0);
				pc.setMagicdollMpTic(0);
				pc.setDynamicExp(pc.getDynamicExp() - 0.1);
				pc.setDynamicInt(pc.getDynamicInt() - 2);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("진 군주")) {
				pc.setMagicdollAntaras(enabled);
				pc.setDynamicReduction(pc.getDynamicReduction() - 10);
				pc.setDynamicExp(pc.getDynamicExp() - 0.65);
				pc.setMagicdollTimeMpTic(0);
				pc.setMagicdollMpTic(0);
				pc.setDynamicAddPvpDmg(pc.getDynamicAddPvpDmg() - 10);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("진 기사")) {
				pc.setDynamicAddDmg(pc.getDynamicAddDmg() - 6);
				pc.setDynamicAddHit(pc.getDynamicAddHit() - 8);
				pc.setDynamicCritical(pc.getDynamicCritical() - 5);
				pc.setDynamicStunHit(pc.getDynamicStunHit() - 0.1);
				pc.setMagicdollTimeMpTic(0);
				pc.setMagicdollMpTic(0);
				pc.setDynamicExp(pc.getDynamicExp() - 0.4);
				pc.setDynamicAddPvpDmg(pc.getDynamicAddPvpDmg() - 10);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("진 요정")) {
				pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() - 6);
				pc.setDynamicAddHitBow(pc.getDynamicAddHitBow() - 8);
				pc.setDynamicBowCritical(pc.getDynamicBowCritical() - 5);
				pc.setDynamicStunResist(pc.getDynamicStunResist() - 0.08);
				pc.setMagicdollTimeMpTic(0);
				pc.setMagicdollMpTic(0);
				pc.setDynamicExp(pc.getDynamicExp() - 0.4);
				pc.setDynamicAddPvpDmg(pc.getDynamicAddPvpDmg() - 10);
			} else if (mdl.getDollBuffType().equalsIgnoreCase("진 마법사")) {
				pc.setDynamicSp(pc.getDynamicSp() - 8);
				pc.setDynamicMagicHit(pc.getDynamicMagicHit() - 8);
				pc.setDynamicStunResist(pc.getDynamicStunResist() - 0.08);
				pc.setMagicdollTimeMpTic(0);
				pc.setMagicdollMpTic(0);
				pc.setDynamicExp(pc.getDynamicExp() - 0.4);
				pc.setDynamicInt(pc.getDynamicInt() - 4);
			}
		}

		pc.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), pc));
		pc.toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), pc));
	}
}
