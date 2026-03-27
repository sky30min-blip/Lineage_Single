package lineage.world.object.magic;

import lineage.bean.database.Item;
import lineage.bean.database.Skill;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;

public class PurifyStone {

	static public void init(Character cha, Skill skill, int object_id){
		// 타겟 찾기
		object o = cha.getInventory().value(object_id);
		// 처리
		if(o != null){
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			if(SkillController.isMagic(cha, skill, true) && o instanceof ItemInstance){
				// 초기화
				ItemInstance item = (ItemInstance)o;
				int rate = 0;
				Item stone = null;
				// 이팩트 처리.
				cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill.getCastGfx()), true);
				// 처리
				if(item.getItem().getNameIdNumber() == 2476){
					// 강암석
					rate = 35;
					stone = ItemDatabase.find(2477);
				}else if(item.getItem().getNameIdNumber() == 2477){
					// 현암석
					rate = 15;
					stone = ItemDatabase.find(2478);
				}else if(item.getItem().getNameIdNumber() == 2474){
					// 흑마석
					rate = 80;
					stone = ItemDatabase.find(2475);
				}else if(item.getItem().getNameIdNumber() == 2475){
					// 흑요석
					rate = 50;
					stone = ItemDatabase.find(2476);
				}
				if(stone!=null){
					if(Util.random(0, 100) <= rate){
						ItemInstance ii = ItemDatabase.newInstance(stone);
						cha.getInventory().append(ii, ii.getCount());
						// 메모리 재사용.
						ItemDatabase.setPool(ii);
					}else{
						cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 280));
					}
					// 재료 제거.
					cha.getInventory().count(item, item.getCount()-1, true);
				}else{
					cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 280));
				}
			}
		}
	}
	
}
