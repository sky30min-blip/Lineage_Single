package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.Party;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.PartyController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.instance.PcInstance;

public class NaturesBlessing {

	static public void init(Character cha, Skill skill){
		if(cha instanceof PcInstance){
			PcInstance pc = (PcInstance)cha;
			
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			
			if(SkillController.isMagic(cha, skill, true)){
				Party p = PartyController.find(pc);
				if(p == null){
					Heal.onBuff(cha, pc, skill, skill.getCastGfx(), 0);	
				}else{
					for(PcInstance use : p.getList()){
						if(Util.isDistance(pc, use, Lineage.SEARCH_LOCATIONRANGE) && SkillController.isFigure(cha, use, skill, false, false))
							Heal.onBuff(cha, use, skill, skill.getCastGfx(), 0);
					}
				}
			}
		}
	}

}
