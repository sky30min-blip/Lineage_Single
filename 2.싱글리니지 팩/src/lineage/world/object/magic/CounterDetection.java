package lineage.world.object.magic;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Skill;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectAttackMagic;
import lineage.share.Lineage;
import lineage.world.controller.DamageController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class CounterDetection {

	static public void init(Character cha, Skill skill){
		if(SkillController.isMagic(cha, skill, true)){
			List<object> list = new ArrayList<object>();
			Detection.onBuff(cha);
			// 주변객체 데미지 및 인비지 처리.
			for(object o : cha.getInsideList()){
				if(o.isInvis()){
					// 데미지 처리
					int dmg = SkillController.getDamage(cha, o, o, skill, 0, skill.getElement());
					DamageController.toDamage(cha, o, dmg, Lineage.ATTACK_TYPE_MAGIC);
					if(dmg > 0)
						list.add(o);
					// 인비지 제거.
					Detection.onBuff(o);
				}
			}
			// 패킷 처리.
			cha.toSender(S_ObjectAttackMagic.clone(BasePacketPooling.getPool(S_ObjectAttackMagic.class), cha, cha, list, false, Lineage.GFX_MODE_SPELL_NO_DIRECTION, 0, skill.getCastGfx(), cha.getX(), cha.getY()), cha instanceof PcInstance);
		}else{
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		}
	}
	
}
