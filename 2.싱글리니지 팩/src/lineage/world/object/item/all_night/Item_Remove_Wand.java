package lineage.world.object.item.all_night;

import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.share.Log;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class Item_Remove_Wand extends ItemInstance {
	
	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new Item_Remove_Wand();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		ItemInstance item = cha.getInventory().value(cbp.readD());
		
		if (!isRemove(item.getItem().getName())) {
			ChattingController.toChatting(cha, "제거할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		} else if (item.isEquipped()) {
			ChattingController.toChatting(cha, "사용중인 아이템은 제거할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		
		if (cha.getInventory() != null && !(item instanceof Item_Remove_Wand)  && isRemove(item.getItem().getName())) {
			if(item != null){
				ChattingController.toChatting(cha, String.format("%s 제거", item.toStringDB()), Lineage.CHATTING_MODE_MESSAGE);
				
				Log.appendItem(cha, "type|아이템 삭제", String.format("item_name|%s", item.toStringDB()), String.format("item_objid|%d", item.getObjectId()), String.format("count|%d", item.getCount()));
				
				cha.getInventory().count(item, item.getCount() - item.getCount(), true);
			}
		}
	}
	
	/**
	 * 제거 해도 되는 아이템인지 확인하는 함수.
	 * 2017-10-13
	 * by all-night
	 */
	public boolean isRemove(String itemName) {
		for (String name : Lineage.no_remove_item) {
			if (name.equalsIgnoreCase(itemName))
				return false;
		}
		return true;
	}
}
