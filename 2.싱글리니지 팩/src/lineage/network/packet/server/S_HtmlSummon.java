package lineage.network.packet.server;

import lineage.bean.database.Exp;
import lineage.database.ExpDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.share.Lineage;
import lineage.world.object.instance.PetInstance;
import lineage.world.object.instance.SummonInstance;

public class S_HtmlSummon extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, SummonInstance si) {
		if (bp == null)
			bp = new S_HtmlSummon(si);
		else
			((S_HtmlSummon) bp).clone(si);
		return bp;
	}

	public S_HtmlSummon(SummonInstance si) {
		clone(si);
	}

	public void clone(SummonInstance si) {
		clear();

		// 초기화
		String type = "moncom";
		Integer count = 9;
		String s_mode = "$471";
		String f_mode = "0";
		String exp = "792";
		String lawful = null;

		int additionalDamage = 0;
		int dynamicSp = 0;
		int dynamicAddHit = 0;
		int dynamicAddAc = 0;

		switch (si.getSummonMode()) {
		case AggressiveMode:
			s_mode = "$469"; // 공격 태세
			break;
		case DefensiveMode:
			s_mode = "$470"; // 방어 태세
			break;
		case Deploy:
			s_mode = "$476"; // 산개
			break;
		case Alert:
			s_mode = "$472"; // 경계
			break;
		case ItemPickUp:
			s_mode = "$613"; // 수집
			break;
		case Rest:
			s_mode = "$471"; // 휴식
			break;
		}
		if (si instanceof PetInstance) {
			PetInstance pi = (PetInstance) si;
			type = "anicom";
			count = 16;
			switch (pi.getFoodMode()) {
			case Veryhungry:
				f_mode = "$608";
				break;
			case Littlehungry:
				f_mode = "$609";
				break;
			case NeitherHungryNorFull:
				f_mode = "$610";
				break;
			case LittleFull:
				f_mode = "$611";
				break;
			case VeryFull:
				f_mode = "$612";
				break;
			}
			Exp e = ExpDatabase.find(pi.getLevel());
			double a = e.getBonus() - e.getExp();
			double b = pi.getExp() - a;
			exp = String.valueOf((int) ((b / e.getExp()) * 100));
			lawful = String.valueOf(pi.getLawful() - Lineage.NEUTRAL);
		}

		// 처리.
		writeC(Opcodes.S_OPCODE_SHOWHTML); //
		writeD(si.getObjectId()); //
		writeS(type); //
		if (Lineage.server_version > 144)
			writeC(0x00); // ?
		writeH(count); // 문자열 갯수
		writeS(s_mode); //
		writeS(String.valueOf(si.getNowHp())); // 현재 hp
		writeS(String.valueOf(si.getTotalHp())); // 최대 hp
		writeS(String.valueOf(si.getNowMp())); // 현재 mp
		writeS(String.valueOf(si.getTotalMp())); // 최대 mp
		writeS(String.valueOf(si.getLevel())); // 레벨
		writeS(si.getName()); //
		writeS(f_mode); //
		writeS(exp); // 790, 792, 경험치
		writeS(lawful); // 라우풀

		// 쿠베라 펫장비
		additionalDamage= si.getLevel() / 8; // 8레벨당 추가타격 +1
		dynamicSp = si.getLevel() / 12; // 12레벨당 마법추가타격 +1

		// 공격성공은 공통으로 10레벨당 +1씩 오르게 됩니다.
		dynamicAddHit = si.getLevel() / 10;


		if (si.getPetWeapon() != null) {

			switch (si.getPetWeapon().toLowerCase()) {
			case "강철의 이빨":
			case "승리의 이빨":
			case "황금의 이빨":
				additionalDamage += 1;
				break;
			case "파멸의 이빨":
				additionalDamage += 2;
				break;
			}

			switch (si.getPetWeapon().toLowerCase()) {
			case "투견의 이빨":
				dynamicAddHit += 3;
				break;
			case "사냥개의 이빨":
				dynamicAddHit += 5;
				break;
			case "승리의 이빨":
				dynamicAddHit += 2;
				break;
			case "신마의 이빨":
				dynamicAddHit += 3;
				break;
			case "파멸의 이빨":
				dynamicAddHit += -3;
				break;
			case "황금의 이빨":
				dynamicAddHit += 1;
				break;
			}

			switch (si.getPetWeapon().toLowerCase()) {
			case "신마의 이빨":
				dynamicSp += 2;
				break;
			case "황금의 이빨":
				dynamicSp += 1;
				break;
			}

		
		

		}
		int totalAdditionalDamage = additionalDamage > 0 ? si.getDynamicAddDmg() + additionalDamage : 0;
		int totalDynamicHit = si.getDynamicAddHit() + dynamicAddHit;
		totalDynamicHit = totalDynamicHit > 0 ? totalDynamicHit : 0;
		int totalDynamicSp = si.getDynamicSp() + dynamicSp;
		totalDynamicSp = totalDynamicSp > 0 ? totalDynamicSp : 0;

		writeS(String.valueOf(totalAdditionalDamage));
		writeS(String.valueOf(totalDynamicHit));
		writeS(String.valueOf(totalDynamicSp));
		
		
		if (si.getPetArmor() != null) {

			switch (si.getPetArmor().toLowerCase()) {
			case "레더 펫아머":
				dynamicAddAc += 4;
				break;
			case "강철 펫아머":
				dynamicAddAc += 8;
				break;
			case "스켈 펫아머":
				dynamicAddAc += 7;
				break;
			case "미스릴 펫아머":
				dynamicAddAc += 12;
				break;
			case "크로스 펫아머":
				dynamicAddAc += 13;
				break;
			case "체인 펫아머":
				dynamicAddAc += 20;
				break;
			}

			

		
		

		}
		// 방어력
		writeS(String.valueOf(si.getAc()+dynamicAddAc));

		// 착용중인 펫 무기
		if (si.getPetWeapon() != null) {

			writeS(si.getPetWeapon());
		} else {
			writeS("비어있음");
		}
		// 착용중인 펫 방어구
		if (si.getPetArmor() != null) {

			writeS(si.getPetArmor());
		} else {
			writeS("비어있음");
		}

	}
}
