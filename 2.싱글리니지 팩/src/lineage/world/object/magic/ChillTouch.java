package lineage.world.object.magic;

import lineage.bean.database.MonsterSkill;
import lineage.bean.database.Skill;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAttack;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.DamageController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class ChillTouch {

	static public void init(Character cha, Skill skill, int object_id) {
		// 타겟 찾기
		object o = cha.findInsideList(object_id);
		if (SkillController.isMagic(cha, skill, true) && SkillController.isFigure(cha, o, skill, false, false))
			toBuff(cha, skill, o, Lineage.GFX_MODE_SPELL_DIRECTION, skill.getCastGfx(), 0);
	}

	/**
	 * 몬스터용
	 * 
	 * @param mi
	 * @param ms
	 * @param o
	 */
	static public void init(MonsterInstance mi, MonsterSkill ms, object o, int action, int effect) {
		if (SkillController.isMagic(mi, ms, true) && SkillController.isFigure(mi, o, ms, false, false))
			toBuff(mi, ms.getSkill(), o, action, effect, (int) Util.random(ms.getMindmg(), ms.getMaxdmg()));
	}

	/**
	 * 중복코드 방지용.
	 * 
	 * @param cha
	 * @param skill
	 * @param o
	 * @param action
	 * @param effect
	 */
	static public void toBuff(Character cha, Skill skill, object o, int action, int effect, int alpha_dmg) {
		int hp = 0;
		// 데미지 처리
		int dmg = SkillController.getDamage(cha, o, o, skill, alpha_dmg, skill.getElement());

		if (o.getNowHp() - dmg <= 0)
			hp = o.getNowHp();
		else
			hp = dmg;

		DamageController.toDamage(cha, o, dmg, Lineage.ATTACK_TYPE_MAGIC);

		// hp 처리
		cha.setNowHp(cha.getNowHp() + hp);

		// 패킷 처리
		if (action > 0) {
			cha.setHeading(Util.calcheading(cha, o.getX(), o.getY()));
			cha.toSender(S_ObjectAttack.clone(BasePacketPooling.getPool(S_ObjectAttack.class), cha, o, action, dmg, effect, false, false, 0, 0), cha instanceof PcInstance);
		}
	}
}
