package lineage.world.object.item.all_night;

import all_night.Lineage_Balance;
import lineage.bean.database.Item;
import lineage.database.ItemDatabase;
import lineage.database.ItemDropMessageDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.item.MagicDoll;

public class DollAwaken extends ItemInstance {
	
	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new DollAwaken();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		ItemInstance target = cha.getInventory().value(cbp.readD());
		int chance= getItem().getSmallDmg();
		
		if(target == null || target.getItem() == null)
			return;
		
		if (target instanceof MagicDoll) {
			String targetName = target.getItem().getName();
			if (!targetName.equalsIgnoreCase("마법인형: 발라카스") && !targetName.equalsIgnoreCase("마법인형: 린드비오르") && 
				!targetName.equalsIgnoreCase("마법인형: 파푸리온") && !targetName.equalsIgnoreCase("마법인형: 안타라스")) {
				ChattingController.toChatting(cha, "해당 인형은 각성 할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			if (Util.random(1,100) < chance) {
				// 성공
				String name = null;
				
				switch (target.getItem().getName()) {
				case "마법인형: 발라카스":
					name = "마법인형[5]: 각성 발라카스";
					break;
				case "마법인형: 린드비오르":
					name = "마법인형[5]: 각성 린드비오르";
					break;
				case "마법인형: 파푸리온":
					name = "마법인형[5]: 각성 파푸리온";
					break;
				case "마법인형: 안타라스":
					name = "마법인형[5]: 각성 안타라스";
					break;
				}
				
				Item i = ItemDatabase.find(name);
				if (i != null) {
					ItemInstance temp = ItemDatabase.newInstance(i);
					temp.setObjectId(ServerDatabase.nextItemObjId());
					temp.setDefinite(true);
					cha.getInventory().append(temp, true);
					
					// 주문서 제거.
					cha.getInventory().count(this, getCount()-1, true);
					// 인형 삭제.
					cha.getInventory().count(target, target.getCount()-1, true);
					ChattingController.toChatting(cha, "\\fR인형 각성에 성공하였습니다!", Lineage.CHATTING_MODE_MESSAGE);
					
					ItemDropMessageDatabase.sendMessageMagicDoll2(cha, name);
				}
			} else {
				// 실패
				ChattingController.toChatting(cha, "\\fY인형 각성에 실패하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
				// 주문서 제거.
				cha.getInventory().count(this, getCount()-1, true);
				// 인형 삭제.
			//	cha.getInventory().count(target, target.getCount()-1, true);
			}
		} else {
			ChattingController.toChatting(cha, "인형에만 사용가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
}
