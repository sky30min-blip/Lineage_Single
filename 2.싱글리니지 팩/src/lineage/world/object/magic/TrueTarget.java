package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.Clan;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_BuffTrueTarget;
import lineage.network.packet.server.S_ObjectAction;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ClanController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class TrueTarget {

	static public void init(Character cha, Skill skill, int object_id, int x, int y, String msg){
		// 초기화
		object o = null;
		// 타겟 찾기
		if(object_id == cha.getObjectId())
			o = cha;
		else
			o = cha.findInsideList( object_id );
		if(o != null){
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			
			if(SkillController.isMagic(cha, skill, true)){
				cha.toSender(S_BuffTrueTarget.clone(BasePacketPooling.getPool(S_BuffTrueTarget.class), o, msg));
				// 혈맹원들에게 전송
				Clan c = ClanController.find(cha.getClanId());
				if(c != null){
					for(PcInstance pc : c.getList()){
						if(Util.isDistance(cha, pc, Lineage.SEARCH_LOCATIONRANGE) && pc.getObjectId() != cha.getObjectId())
							pc.toSender(S_BuffTrueTarget.clone(BasePacketPooling.getPool(S_BuffTrueTarget.class), o, msg));
					}
				}
			}
		}
	}
	
}
