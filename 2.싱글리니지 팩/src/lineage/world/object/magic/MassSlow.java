package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class MassSlow {

	static public void init(Character cha, Skill skill, int object_id){
		// 초기화
		object o = null;
		// 타겟 찾기
		if(object_id == cha.getObjectId())
			o = cha;
		else
			o = cha.findInsideList( object_id );
		// 처리
		if(o != null){
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			
			if(SkillController.isMagic(cha, skill, true)){
				// 지정된 객체
				if(SkillController.isFigure(cha, o, skill, true, false))
					Slow.onBuff(o, skill, true);
				// 주변 객체
				for(object oo : o.getInsideList()){
					if(cha.getObjectId()!=oo.getObjectId() && Util.isDistance(o, oo, skill.getRange()) && SkillController.isFigure(cha, oo, skill, true, false) && o instanceof Character)
						Slow.onBuff(oo, skill, true);
				}
			}
		}
		
	}
	
}
