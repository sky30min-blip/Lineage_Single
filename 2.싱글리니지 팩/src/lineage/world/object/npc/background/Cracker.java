 package lineage.world.object.npc.background;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectHeading;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.BackgroundInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class Cracker extends BackgroundInstance {

	@Override
	public void toDamage(Character cha, int dmg, int type, Object... opt) {
		if (cha instanceof PcInstance) {
			PcInstance pc = (PcInstance) cha;
			ItemInstance weapon = cha.getInventory().getSlot(Lineage.SLOT_WEAPON);
			if ((weapon != null) && (type == Lineage.ATTACK_TYPE_WEAPON || type == Lineage.ATTACK_TYPE_BOW)) {
				if (cha.getWeapon() == null) {
					cha.setWeapon(weapon);
					cha.setMinDmg(dmg);
					cha.setMaxDmg(dmg);
				} else if (cha.getWeapon().getObjectId() != weapon.getObjectId() || cha.getLastHitTime() + (1000 * 3) < System.currentTimeMillis()) {
					cha.setWeapon(weapon);
					cha.setDmg(0);
					cha.setMinDmg(dmg);
					cha.setMaxDmg(dmg);
					cha.setHitCount(0);
				}
				cha.setLastHitTime(System.currentTimeMillis());
				cha.setDmg(cha.getDmg() + dmg);
				cha.setHitCount(cha.getHitCount() + 1);
				// 최소, 최대 데미지 저장
				if (cha.getMinDmg() > dmg)
					cha.setMinDmg(dmg);
				if (cha.getMaxDmg() < dmg)
					cha.setMaxDmg(dmg);
				// 평균 데미지
				int avgDmg = cha.getDmg() / cha.getHitCount();
				
				// DPS
				long time = System.currentTimeMillis();
				long dpsTime = time - cha.dps_attack_time;
				double attackSpeed = dpsTime * 0.001;
				int dps = (int) Math.round(avgDmg / attackSpeed);
				cha.dps_attack_time = time;
				
				if (pc.getLevel() < Lineage.cracker_exp_max_level)
					pc.toExp(this, Util.random(1, 2));

				// 리니지conf에서 true false 설정
				if (Lineage.view_cracker_damage && !pc.isDamageMassage()) {
					if (weapon.getEnLevel() > 0)
						ChattingController.toChatting(cha, String.format("[+%d %s] 대미지: [%d]  공격횟수: [%d회]", weapon.getEnLevel(), weapon.getItem().getName(), dmg, cha.getHitCount()), Lineage.CHATTING_MODE_MESSAGE);
					else
						ChattingController.toChatting(cha, String.format("[%s] 대미지: [%d]  공격횟수: [%d회]", weapon.getItem().getName(), dmg, cha.getHitCount()), Lineage.CHATTING_MODE_MESSAGE);

					ChattingController.toChatting(cha, String.format("최소대미지: [%d]  최대대미지: [%d]  평균대미지: [%d]", cha.getMinDmg(), cha.getMaxDmg(), avgDmg), Lineage.CHATTING_MODE_MESSAGE);
					
					if (Lineage.view_cracker_dps)
						ChattingController.toChatting(cha, String.format("초당 대미지(DPS): [%d]  공격속도: [%.3f초]", dps, attackSpeed), Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(cha, "ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ", Lineage.CHATTING_MODE_MESSAGE);
				}
			} else {
				if (pc.getLevel() < Lineage.cracker_exp_max_level)
					pc.toExp(this, Util.random(1, 2));
				
				if (Lineage.view_cracker_damage && !pc.isDamageMassage()) // 리니지conf에서 true false 설정
					ChattingController.toChatting(cha, String.format("대미지: [%d]", dmg), Lineage.CHATTING_MODE_MESSAGE);
			}
		}
		
		setHeading(++heading);
	}

	@Override
	public void setHeading(int heading) {
		if (Lineage.server_version <= 163) {
			if (heading > 2)
				heading = 0;
		}
		super.setHeading(heading);

		toSender(S_ObjectHeading.clone(BasePacketPooling.getPool(S_ObjectHeading.class), this), false);
	}

}
