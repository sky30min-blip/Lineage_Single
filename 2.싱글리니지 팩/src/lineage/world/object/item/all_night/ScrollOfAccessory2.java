package lineage.world.object.item.all_night;

import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.Enchant;

public class ScrollOfAccessory2 extends Enchant {
	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new ScrollOfAccessory2();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha.getInventory() != null) {
			ItemInstance item = cha.getInventory().value(cbp.readD());
			if (item.isEquipped()) {
				ChattingController.toChatting(cha, "해당 아이템은 착용해제 후 인챈트 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			if(item.getEnLevel() > 8){
				ChattingController.toChatting(cha, "8이상 인챈이 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			if(!item.getName().contains("룸티스")){
				ChattingController.toChatting(cha, "해당주문서로 인챈트가 불가능한 장신구입니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}

			if ( item.getItem().isEnchant() && item.getItem().getType2().equals("earring") ) {
				if (item != null) {
					if (item.isEquipped()) {
						ChattingController.toChatting(cha, "착용중인 장신구에 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
						return;
					}
					


					if (cha instanceof PcInstance) {
						int en = toEnchant(cha, item, this);

						item.toEnchant((PcInstance) cha, en);

						if (en != -127)
							cha.getInventory().count(this, getCount() - 1, true);
					}
				}
			} else {
				if (!item.getItem().isEnchant())
					ChattingController.toChatting(cha, "인챈트가 불가능한 장신구입니다.", Lineage.CHATTING_MODE_MESSAGE);
				else
					ChattingController.toChatting(cha, "룸티스에만 사용 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}
}
