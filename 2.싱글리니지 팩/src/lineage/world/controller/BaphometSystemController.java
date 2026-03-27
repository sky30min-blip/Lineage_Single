package lineage.world.controller;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_CharacterSpMr;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.object.instance.PcInstance;

public final class BaphometSystemController {

	public static void setBaphomet(PcInstance pc) {
		//라우풀 일때
		if (pc.getLawful() >= 75536) {
			// 10000 ~ 19999
			if (pc.getLawful() >= 75536 && pc.getLawful() <= 85535 && (!pc.isBaphomet() || pc.getBaphometLevel() != 1)) {
				if (pc.isBaphomet())
					removeBaphomet(pc);
				pc.setBaphomet(true);
				pc.setBaphometLevel(1);
				appendBaphomet(pc);
				//pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 10881), true);
				ChattingController.toChatting(pc, "\\fS라우풀 1단계: AC-3, 마법방어+1", Lineage.CHATTING_MODE_MESSAGE);
			}
			// 20000 ~ 29999
			if (pc.getLawful() >= 85536 && pc.getLawful() <= 95535 && (!pc.isBaphomet() || pc.getBaphometLevel() != 2)) {
				if (pc.isBaphomet())
					removeBaphomet(pc);
				pc.setBaphomet(true);
				pc.setBaphometLevel(2);
				appendBaphomet(pc);
				//pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 10881), true);
				ChattingController.toChatting(pc, "\\fS라우풀 2단계: AC-6, 마법방어+3", Lineage.CHATTING_MODE_MESSAGE);
			}
			// 30000 ~ 32767
			if (pc.getLawful() >= 95536 && (!pc.isBaphomet() || pc.getBaphometLevel() != 3)) {
				if (pc.isBaphomet())
					removeBaphomet(pc);
				pc.setBaphomet(true);
				pc.setBaphometLevel(3);
				appendBaphomet(pc);
				//pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 10881), true);
				ChattingController.toChatting(pc, "\\fS라우풀 3단계: AC-9, 마법방어+6", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
		//카오틱 일때
		if (pc.getLawful() <= 55536) {
			// -10000 ~ -19999
			if (pc.getLawful() >= 45537 && pc.getLawful() <= 55536 && (!pc.isBaphomet() || pc.getBaphometLevel() != -1)) {
				if (pc.isBaphomet())
					removeBaphomet(pc);
				pc.setBaphomet(true);
				pc.setBaphometLevel(-1);
				appendBaphomet(pc);
				//pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 10879), true);
				ChattingController.toChatting(pc, "\\fY카오틱 1단계: 추가 대미지+1, 주술력+1", Lineage.CHATTING_MODE_MESSAGE);
			}
			// -20000 ~ -29999
			if (pc.getLawful() >= 35537 && pc.getLawful() <= 45536 && (!pc.isBaphomet() || pc.getBaphometLevel() != -2)) {
				if (pc.isBaphomet())
					removeBaphomet(pc);
				pc.setBaphomet(true);
				pc.setBaphometLevel(-2);
				appendBaphomet(pc);
				//pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 10879), true);
				ChattingController.toChatting(pc, "\\fY카오틱 2단계: 추가 대미지+3, 주술력+2", Lineage.CHATTING_MODE_MESSAGE);
			}
			// -30000 ~ -32768
			if (pc.getLawful() <= 35536 && (!pc.isBaphomet() || pc.getBaphometLevel() != -3)) {
				if (pc.isBaphomet())
					removeBaphomet(pc);
				pc.setBaphomet(true);
				pc.setBaphometLevel(-3);
				appendBaphomet(pc);
				//pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 10879), true);
				ChattingController.toChatting(pc, "\\fY카오틱 3단계: 추가 대미지+5, 주술력+3", Lineage.CHATTING_MODE_MESSAGE);
			}
		}	
		if (pc.getLawful() >= 55537 && pc.getLawful() <= 75535 && pc.isBaphomet()) {
			pc.setBaphomet(false);
			removeBaphomet(pc);
			ChattingController.toChatting(pc, "\\fW라우풀 효과 종료", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	// 효과 제거
	public static void removeBaphomet(PcInstance pc) {
		// -1 ~ -3 카오틱 
		// 1 ~ 3 라우풀
		switch (pc.getBaphometLevel()) {
		case -1:
			pc.setDynamicAddDmg(pc.getDynamicAddDmg() - 1);
			pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() - 1);
			pc.setDynamicSp(pc.getDynamicSp() - 1);
			break;
		case -2:
			pc.setDynamicAddDmg(pc.getDynamicAddDmg() - 3);
			pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() - 3);
			pc.setDynamicSp(pc.getDynamicSp() - 2);
			break;
		case -3:
			pc.setDynamicAddDmg(pc.getDynamicAddDmg() - 5);
			pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() - 5);
			pc.setDynamicSp(pc.getDynamicSp() - 3);
			break;
		case 1:
			pc.setDynamicAc(pc.getDynamicAc() - 3);
			pc.setDynamicMr(pc.getDynamicMr() - 1);
			break;
		case 2:
			pc.setDynamicAc(pc.getDynamicAc() - 6);
			pc.setDynamicMr(pc.getDynamicMr() - 3);
			break;
		case 3:
			pc.setDynamicAc(pc.getDynamicAc() - 9);
			pc.setDynamicMr(pc.getDynamicMr() - 6);
			break;
		}
		
		pc.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), pc));
		pc.toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), pc));
	}
	// 효과 부여
	public static void appendBaphomet(PcInstance pc) {
		// -1 ~ -3 카오틱 
		// 1 ~ 3 라우풀
		switch (pc.getBaphometLevel()) {
		case -1:
			pc.setDynamicAddDmg(pc.getDynamicAddDmg() + 1);
			pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() + 1);
			pc.setDynamicSp(pc.getDynamicSp() + 1);
			break;
		case -2:
			pc.setDynamicAddDmg(pc.getDynamicAddDmg() + 3);
			pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() + 3);
			pc.setDynamicSp(pc.getDynamicSp() + 2);
			break;
		case -3:
			pc.setDynamicAddDmg(pc.getDynamicAddDmg() + 5);
			pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() + 5);
			pc.setDynamicSp(pc.getDynamicSp() + 3);
			break;
		case 1:
			pc.setDynamicAc(pc.getDynamicAc() + 3);
			pc.setDynamicMr(pc.getDynamicMr() + 1);
			break;
		case 2:
			pc.setDynamicAc(pc.getDynamicAc() + 6);
			pc.setDynamicMr(pc.getDynamicMr() + 3);
			break;
		case 3:
			pc.setDynamicAc(pc.getDynamicAc() + 9);
			pc.setDynamicMr(pc.getDynamicMr() + 6);
			break;
		}
		
		pc.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), pc));
		pc.toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), pc));
	}
}
