package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class Eruption {

	/**
	 * 사용자 용
	 * @param cha
	 * @param skill
	 * @param object_id
	 */
	static public void init(Character cha, Skill skill, int object_id){
		// 타겟 찾기
		object o = cha.findInsideList( object_id );
		if(o!=null && Util.isDistance(cha, o, 8) && SkillController.isMagic(cha, skill, true))
			EnergyBolt.toBuff(cha, o, skill, Lineage.GFX_MODE_SPELL_DIRECTION, skill.getCastGfx(), 0);
	}

}
