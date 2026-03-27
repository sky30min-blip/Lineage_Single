package lineage.world.object.item.all_night;

import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class MaxMPIncreasePotion extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new MaxMPIncreasePotion();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha instanceof PcInstance) {
			if (cha.isAddMp) {
				ChattingController.toChatting(cha, "이미 효과가 적용 중입니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			cha.isAddMp = true;
			cha.setDynamicMp(cha.getDynamicMp() + Util.random(getItem().getSmallDmg(), getItem().getBigDmg()));
				
			// 아이템 수량 갱신
			cha.getInventory().count(this, getCount()-1, true);
		}
	}
}
