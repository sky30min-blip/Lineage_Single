package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class AreaOfSilence {

	static public void init(Character cha, Skill skill){
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		
		if(SkillController.isMagic(cha, skill, true)){
			// 투망상태 해제
			Detection.onBuff(cha);
			// 이팩트
			cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill.getCastGfx()), true);
			// 주변이 완전한 정적에 묻힙니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 715));
			// 처리
			Silence.onBuff(cha, skill, skill.getBuffDuration());
			
			for(object o : cha.getInsideList()){
				if(o instanceof Character && SkillController.isFigure(cha, o, skill, true, false))
					Silence.onBuff(o, skill, skill.getBuffDuration());
			}
		}
	}
	
}
