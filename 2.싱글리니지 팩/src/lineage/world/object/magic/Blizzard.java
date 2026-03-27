package lineage.world.object.magic;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.MonsterSkill;
import lineage.bean.database.Skill;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectAttackMagic;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.DamageController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class Blizzard {

	/**
	 * 사용자 가 사용하는 함수.
	 * @param cha
	 * @param skill
	 */
	static public void init(Character cha, Skill skill){
		toBuff(cha, skill, Lineage.GFX_MODE_SPELL_DIRECTION, skill.getRange(), skill.getCastGfx(), 0);
	}
	
	/**
	 * 몬스터가 사용하는 함수.
	 * @param mi
	 * @param skill
	 * @param action
	 */
	static public void init(MonsterInstance mi, MonsterSkill ms, int action, int effect){
		toBuff(mi, ms.getSkill(), action, ms.getRange(), effect, Util.random(ms.getMindmg(), ms.getMaxdmg()));
	}
	
	/**
	 * 중복코드 방지 함수.
	 * @param mi
	 * @param skill
	 * @param action
	 */
	static private void toBuff(Character cha, Skill skill, int action, int area, int effect, double alpha_dmg){
		List<object> list = new ArrayList<object>();
		int x = 0;
		int y = 0;
		if(SkillController.isMagic(cha, skill, true)) {
			// 주변 객체 추출 및 데미지 처리.
			for(object o : cha.getInsideList()){
				if(Util.isDistance(cha, o, area) && o instanceof Character){
					// 데미지 처리
					int dmg = SkillController.getDamage(cha, cha, o, skill, alpha_dmg, skill.getElement());
					DamageController.toDamage(cha, o, dmg, Lineage.ATTACK_TYPE_MAGIC);
					if(dmg > 0){
						if(x==0 && y==0){
							x = o.getX();
							y = o.getY();
						}
						list.add(o);
					}
				}
			}
			// 패킷 처리.
			if(x!=0 && y!=0)
				cha.setHeading( Util.calcheading(cha, x, y) );
			cha.toSender(S_ObjectAttackMagic.clone(BasePacketPooling.getPool(S_ObjectAttackMagic.class), cha, null, list, true, action, 0, effect, x, y), cha instanceof PcInstance);
		} else {
			// 모션 취하기.
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_DIRECTION), true);
		}
	}

}
