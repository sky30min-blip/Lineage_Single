package lineage.world.object.magic;

import all_night.Lineage_Balance;
import lineage.bean.database.Skill;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.BuffController;
import lineage.world.controller.CharacterController;
import lineage.world.controller.DamageController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class Heal {

	static public void init(Character cha, Skill skill, long object_id) {
		// 초기화
		object o = null;
		// 타겟 찾기
		if (Lineage.is_heal_target) {
			if (object_id == cha.getObjectId())
				o = cha;
			else
				o = cha.findInsideList(object_id);
		} else {
			o = cha;
		}
		
		// 처리
		if (o != null) {
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			if (SkillController.isMagic(cha, skill, true) && SkillController.isFigure(cha, o, skill, false, false)) {
				// 적용
				onBuff(cha, o, skill, skill.getCastGfx(), 0);
			}
		}
	}

	/**
	 * 체력회복 공통 처리 구간. : 마법주문서 (힐) 도 이걸 이용함. : HelperNovice 에서도 이걸 이용함.
	 */
	static public void onBuff(Character cha, object o, Skill skill, int effect, int alpha_dmg) {
		if (o.isLockHigh())
			return;
		if (!Util.isAreaAttack(cha, o) && !Util.isAreaAttack(o, cha))
			return;
		// 이팩트
		if (effect > 0 && o != null)
			o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, effect), o instanceof PcInstance);
		// 처리
		int dmg = getHealDamage(cha, o, skill);

		if (skill != null && !Lineage_Balance.is_heal_damage && cha instanceof PcInstance && o instanceof PcInstance && (skill.getUid() == 49 || skill.getUid() == 133)) {
			if (o.lastHealTime > System.currentTimeMillis())
				dmg *= Lineage_Balance.heal_reduction;
			else
				o.lastHealTime = (long) (System.currentTimeMillis() + Lineage_Balance.heal_time);
		}
		
		// 힐올과 블레싱 조절.
		if (skill != null && (skill.getUid() == 49 || skill.getUid() == 133)) {
			if (skill.getUid() == 49) {
				dmg = (int) (dmg * Lineage_Balance.heal_all_rate);
			} else if (skill.getUid() == 133) {
				dmg = (int) (dmg * Lineage_Balance.blessing_rate);
			}
		}
		
		if (dmg > 0)
			o.setNowHp(o.getNowHp() + dmg);
	}

	public static int getHealDamage(Character cha, object o, Skill skill) {
		double healDmg = Util.random(skill.getMindmg(), skill.getMaxdmg());

		healDmg *= (cha.getTotalInt() * 0.05) + (CharacterController.toStatInt(cha, "magicBonus") * 0.5);

		//  라우풀값에 따라 대미지 영향주기.
		healDmg *= cha.getLawful() * 0.00001;

		if (o instanceof MonsterInstance && !(o.getSummon() != null && o.getSummon().getMasterObjectId() == cha.getObjectId())) {
			MonsterInstance mon = (MonsterInstance) o;
			if (mon.getMonster().isUndead() && mon.getMonster().isTurnUndead()) {
				healDmg = SkillController.getMrDamage(cha, o, healDmg, false);
				DamageController.toDamage(cha, mon, (int) Math.round(healDmg), Lineage.ATTACK_TYPE_MAGIC);
				// 패킷 처리
				if (SpriteFrameDatabase.findGfxMode(mon.getGfx(), mon.getGfxMode() + Lineage.GFX_MODE_DAMAGE))
					mon.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), mon, Lineage.GFX_MODE_DAMAGE), false);	
				return 0;
			}
		}
		
		if (o.isBuffWaterLife()) {
			healDmg *= 2;
			BuffController.remove(o, WaterLife.class);
		}
		
		if (o.isBuffPolluteWater())
			healDmg *= 0.5;
		
		return (int) Math.round(healDmg);
	}

}
