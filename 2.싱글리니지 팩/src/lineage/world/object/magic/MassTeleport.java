package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.Clan;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_ObjectAction;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ClanController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.instance.PcInstance;

public class MassTeleport {

	static public void init(Character cha, Skill skill, ClientBasePacket cbp){
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		
		if((cha.getMap() == 0 || cha.getMap() == 4) && SkillController.isMagic(cha, skill, true)){
			if( Teleport.onBuff(cha, cbp, 1, false, false)){
				int x = cha.getX();
				int y = cha.getY();
				int map = cha.getMap();
				// 이동
				cha.toTeleport(cha.getHomeX(), cha.getHomeY(), cha.getHomeMap(), true);
				// 혈맹원 이동
				if(cha instanceof PcInstance){
					Clan c = ClanController.find( (PcInstance)cha );
					if(c != null){
						for(PcInstance use : c.getList()){
							if(Util.isDistance(x, y, map, use.getX(), use.getY(), use.getMap(), 2) && !use.isFishing() && !use.isLock())
								use.toTeleport(cha.getHomeX(), cha.getHomeY(), cha.getHomeMap(), true);
						}
					}
				}
			}
			return;
		}

		Teleport.unLock(cha, true);
	}
	
}
