package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.SkillController;
import lineage.world.controller.SummonController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.SummonInstance;

public class CreateZombie {

	static public void init(Character cha, Skill skill, int object_id){
		// 타겟 찾기
		object o = cha.findInsideList( object_id );
		// 처리
		if(o != null){
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			
			if(SkillController.isMagic(cha, skill, true) && o instanceof MonsterInstance && !(o instanceof SummonInstance)){
				MonsterInstance mi = (MonsterInstance)o;
				if(mi.isDead() && SummonController.isAppend(null, cha, SummonController.TYPE.TAME)){
					// 이팩트 처리.
					cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), mi, skill.getCastGfx()));
					// 테이밍 처리.
					SummonController.toCreateZombie(cha, mi, skill.getBuffDuration());
				}else{
					cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 280));
				}
			}
		}
	}

}
