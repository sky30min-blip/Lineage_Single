package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.share.Lineage;
import lineage.world.controller.LocationController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;

public class TeleportToMother {

	static public void init(Character cha, Skill skill){
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		
		if(SkillController.isMagic(cha, skill, true)){
			if(LocationController.isTeleportVerrYedHoraeZone(cha, true))
				cha.toTeleport(33051, 32339, 4, true);
			return;
		}
		
		Teleport.unLock(cha, true);
	}
}
