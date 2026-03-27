package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class FullHeal {
	
	static public void init(Character cha, Skill skill, long object_id){
		// 초기화
		object o = null;
		// 타겟 찾기
		if (object_id == cha.getObjectId()) {
			o = cha;
		} else {
			o = cha.findInsideList( object_id );
		}
		// 처리
		if(o != null){
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			
			if(SkillController.isMagic(cha, skill, true) && SkillController.isFigure(cha, o, skill, false, false) && Util.isDistance(cha, o, 10)){
				// 적용
				Heal.onBuff(cha, o, skill, skill.getCastGfx(), 0);
			} else {
				if (!Util.isDistance(cha, o, 10))
					ChattingController.toChatting(cha, "\\fY대상이 너무 멀리 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}

}
