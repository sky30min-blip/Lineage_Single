package lineage.world.object.item.all_night;

import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class GoldBar extends ItemInstance {
	
	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new GoldBar();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if(cha.getInventory() != null){
			int adenCount = 0;
			ItemInstance aden = cha.getInventory().find("아데나", true);
			
			if (getItem().getName().equals("1억 아데나")) {
				adenCount = 100000000;
			} else if (getItem().getName().equals("10억 아데나")) {
				adenCount = 1000000000;
			} else {
				return;
			}
			
			if (aden != null && (aden.getCount() > Common.MAX_COUNT || aden.getCount() + adenCount > Common.MAX_COUNT)) {
				ChattingController.toChatting(cha, "20억 아데나를 초과 소지할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			if(aden == null){
				aden = ItemDatabase.newInstance(ItemDatabase.find("아데나"));
				aden.setObjectId(ServerDatabase.nextItemObjId());
				aden.setCount(0);
				cha.getInventory().append(aden, true);
			}
			cha.getInventory().count(aden, aden.getCount() + adenCount , true);
			cha.getInventory().count(this, getCount() - 1, true);
			ChattingController.toChatting(cha, String.format("아데나(%d) 를 획득하였습니다.", adenCount), Lineage.CHATTING_MODE_MESSAGE);
		}
	}
}
