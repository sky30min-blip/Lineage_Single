package lineage.world.object.magic;

import all_night.Lineage_Balance;
import lineage.bean.database.MonsterSkill;
import lineage.bean.database.Skill;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAttack;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.DamageController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class EnergyBolt {

	/**
	 * 사용자 용
	 * 
	 * @param cha
	 * @param skill
	 * @param object_id
	 */
	static public void init(Character cha, Skill skill, int object_id) {
		// 타겟 찾기
		object o = cha.findInsideList(object_id);

		if (!Util.isDistance(cha, o, skill.getDistance())) {
			ChattingController.toChatting(cha, "\\fY대상이 너무 멀리있습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}

		if (o != null && Util.isDistance(cha, o, skill.getDistance()) && SkillController.isMagic(cha, skill, true))
			toBuff(cha, o, skill, Lineage.GFX_MODE_SPELL_DIRECTION, skill.getCastGfx(), 0);
	}

	/**
	 * 몬스터용
	 * 
	 * @param cha
	 * @param o
	 * @param ms
	 * @param action
	 */
	static public void init(Character cha, object o, MonsterSkill ms, int action, int effect) {
		if (o != null && SkillController.isMagic(cha, ms, true) && Util.isDistance(cha, o, ms.getDistance() > 0 ? ms.getDistance() : ms.getSkill().getDistance())){
			toBuff(cha, o, ms.getSkill(), action, effect,100);
		}
		
		
	}

	/**
	 * 중복코드 방지용
	 * 
	 * @param cha
	 * @param o
	 * @param skill
	 * @param action
	 * @param effect
	 */
	static public int toBuff(Character cha, object o, Skill skill, int action, int effect, double alpha_dmg) {
		// 데미지 처리
		double dmg = 0;
		
		if (skill != null) {
			dmg = SkillController.getDamage(cha, o, o, skill, alpha_dmg, skill.getElement());
		} else {
			// 공격가능한 존인지, 장거리공격이 가능한지 확인.
			if (World.isAttack(cha, o) && Util.isAreaAttack(cha, o) && Util.isAreaAttack(o, cha)) {
				dmg = alpha_dmg;
				
				if (o.isBuffCounterMagic()) {
					BuffController.remove(o, CounterMagic.class);
					dmg = 0;
				}
			}
		}
				
		// 디스인티그레이트의 중복 대미지 시간 설정
		if (skill != null && !Lineage_Balance.is_this_inti_greate_damage && cha instanceof PcInstance && o instanceof PcInstance && skill.getUid() == 77) {
			if (o.lastDamageThisTime > System.currentTimeMillis())
				dmg *= Lineage_Balance.this_inti_greate_reduction;
			else
				o.lastDamageThisTime = (long) (System.currentTimeMillis() + Lineage_Balance.this_inti_greate_time);
		}
		
		if (skill != null && !SkillController.isFigure(cha, o, skill, false, false))
			dmg = 0;
		
		DamageController.toDamage(cha, o, (int) Math.round(dmg), Lineage.ATTACK_TYPE_MAGIC);
		
		if (action > 0)
			// 패킷 처리
			cha.setHeading(Util.calcheading(cha, o.getX(), o.getY()));

		// 마법 크리티컬시 이팩트
		if (cha.isCriticalMagicEffect()) {
			// 콜 라이트닝
			if (skill.getUid() == 34)
				effect = 11737;
			// 콘 오브 콜드
			else if (skill.getUid() == 38)
				effect = 11742;
			// 선 버스트
			else if (skill.getUid() == 46)
				effect = 11760;
			// 디스인티그레이트
			else if (skill.getUid() == 77)
				effect = 11748;
		}



		cha.toSender(S_ObjectAttack.clone(BasePacketPooling.getPool(S_ObjectAttack.class), cha, o, action,
				(int) Math.round(dmg), effect, false, false, 0, 0), cha instanceof PcInstance);

		return (int) Math.round(dmg);
	}
}
