package lineage.world.object.magic;

import java.util.ArrayList;
import java.util.List;

import all_night.Lineage_Balance;
import lineage.bean.database.MonsterSkill;
import lineage.bean.database.Skill;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAttackMagic;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.DamageController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.SummonInstance;

public class Lightning {

	static private List<object> list = new ArrayList<object>();
	static private int x;
	static private int y;

	/**
	 * 사용자용 함수.
	 * 
	 * @param cha
	 * @param skill
	 * @param object_id
	 * @param x
	 * @param y
	 */
	static public void init(Character cha, Skill skill, int object_id, int x, int y) {
		synchronized (list) {
			list.clear();
			Lightning.x = x;
			Lightning.y = y;
		}

		object o = cha.findInsideList(object_id);
		if (o != null && Util.isDistance(cha, o, skill.getDistance()) && SkillController.isMagic(cha, skill, true)) {
			if (Lineage.server_version < 160) {
				Lightning.x = o.getX();
				Lightning.y = o.getY();
			}
			toBuff(cha, skill, o, Lineage.GFX_MODE_SPELL_DIRECTION, skill.getRange(), skill.getCastGfx(), 0);
		}
	}

	/**
	 * 몬스터용 함수.
	 * 
	 * @param mi
	 * @param skill
	 * @param o
	 * @param action
	 */
	static public void init(MonsterInstance mi, MonsterSkill ms, object o, int action, int effect) {
		synchronized (list) {
			list.clear();
			x = o.getX();
			y = o.getY();
		}
		if (SkillController.isMagic(mi, ms, true) && ms.getSkill() != null) {
			toBuff(mi, ms.getSkill(), o, action, ms.getRange() > 0 ? ms.getRange() : ms.getSkill().getRange(), effect, Util.random(ms.getMindmg(), ms.getMaxdmg()));
		}
	}

	/**
	 * 중복코드 방지용. : 마법주문서 (라이트닝) 에서도 사용중.
	 * 
	 * @param cha
	 * @param skill
	 * @param o
	 * @param action
	 */
	static public void toBuff(Character cha, Skill skill, object o, int action, int area, int effect, double alpha_dmg) {
		synchronized (list) {
			// 데미지 처리
			int dmg = SkillController.getDamage(cha, o, o, skill, alpha_dmg, skill.getElement());
			
			if (skill != null && !Lineage_Balance.is_meteor_strike_damage && cha instanceof PcInstance && o instanceof PcInstance && skill.getUid() == 74) {
				if (o.lastDamageMeteorStrike > System.currentTimeMillis())
					dmg *= Lineage_Balance.meteor_strike_reduction;
				else
					o.lastDamageMeteorStrike = (long) (System.currentTimeMillis() + Lineage_Balance.meteor_strike_time);
			}

			if (dmg > 0)
				list.add(o);
			
			DamageController.toDamage(cha, o, dmg, Lineage.ATTACK_TYPE_MAGIC);

			// 주변객체 데미지 처리
			for (object oo : cha.getInsideList()) {
				if (o.getObjectId() != oo.getObjectId() && Util.isDistance(o, oo, area) && o instanceof Character && oo instanceof Character) {
					if (cha instanceof PcInstance) {
						if (oo instanceof PcInstance)
							continue;

						if (oo instanceof SummonInstance) {
							SummonInstance sm = (SummonInstance) oo;

							if (sm.getSummon() != null && sm.getSummon().getMaster() != null && sm.getSummon().getMaster() instanceof PcInstance)
								continue;
						}
					}
					
					if (cha instanceof MonsterInstance && oo instanceof MonsterInstance) {
						if (oo instanceof SummonInstance) {
							SummonInstance sm = (SummonInstance) oo;
							
							if (sm.getSummon() != null && sm.getSummon().getMaster() != null && sm.getSummon().getMaster() instanceof MonsterInstance)
								continue;
						} else {
							continue;
						}
					}
					
					int oo_dmg = SkillController.getDamage(cha, oo, oo, skill, alpha_dmg, skill.getElement());
					
					if (skill != null && !Lineage_Balance.is_meteor_strike_damage && cha instanceof PcInstance && oo instanceof PcInstance && skill.getUid() == 74) {
						if (oo.lastDamageMeteorStrike > System.currentTimeMillis())
							oo_dmg *= Lineage_Balance.meteor_strike_reduction;
						else
							oo.lastDamageMeteorStrike = (long) (System.currentTimeMillis() + Lineage_Balance.meteor_strike_time);
					}
					
					DamageController.toDamage(cha, oo, oo_dmg, Lineage.ATTACK_TYPE_MAGIC);
					
					if (oo_dmg > 0)
						list.add(oo);
				}
			}

			// 패킷 처리.
			cha.setHeading(Util.calcheading(cha, x, y));
			cha.toSender(S_ObjectAttackMagic.clone(BasePacketPooling.getPool(S_ObjectAttackMagic.class), cha, o, list, false, action, dmg, effect, x, y), cha instanceof PcInstance);
		}
	}
}
