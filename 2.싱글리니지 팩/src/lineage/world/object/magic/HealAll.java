package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.Party;
import lineage.bean.lineage.Summon;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.PartyController;
import lineage.world.controller.SkillController;
import lineage.world.controller.SummonController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class HealAll {

	static public void init(Character cha, Skill skill){
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		
		if(SkillController.isMagic(cha, skill, true))
			onBuff(cha, skill);
	}
	
	static public void onBuff(object o, Skill skill){
		o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
		
		if( !(o instanceof Character) )
			return;
		
		// 파티원 추출.
		if(o instanceof PcInstance){
			Party p = PartyController.find((PcInstance)o);
			
			if(p != null){
				for(PcInstance pc : p.getList()){
					if(Util.isDistance(o, pc, Lineage.SEARCH_LOCATIONRANGE)  && SkillController.isFigure((Character) o, pc, skill, false, false) && o.getObjectId()!=pc.getObjectId())
						Heal.onBuff((Character)o, pc, skill, 8908, 0);
				}
			}
			
			Summon summon = SummonController.find(o);
			
			if(summon != null){
				for(object mon : summon.getList()){
					if(Util.isDistance(o, mon, Lineage.SEARCH_LOCATIONRANGE)  && SkillController.isFigure((Character) o, mon, skill, false, false) && o.getObjectId() != mon.getObjectId())
						Heal.onBuff((Character)o, mon, skill, 8908, 0);
				}
			}
		}	
	}
}
