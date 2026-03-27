package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.share.Lineage;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class FinalBurn {

	static public void init(Character cha, Skill skill, int object_id) {
		// 초기화
		object o = cha.findInsideList(object_id);
		// 처리
		if (o != null && SkillController.isMagic(cha, skill, true)) {
			int dmg = EnergyBolt.toBuff(cha, o, skill, Lineage.GFX_MODE_SPELL_DIRECTION, skill.getCastGfx(), cha.getNowHp() + cha.getNowMp());
			if (dmg > 0) {
				cha.setNowHp(1);
				cha.setNowMp(1);
			}
		}
	}

}
