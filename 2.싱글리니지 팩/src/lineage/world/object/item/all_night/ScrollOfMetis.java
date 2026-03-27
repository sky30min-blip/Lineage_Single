package lineage.world.object.item.all_night;

import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.Enchant;

public class ScrollOfMetis extends Enchant {
	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new ScrollOfMetis();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha.getInventory() != null) {
			ItemInstance item = cha.getInventory().value(cbp.readD());

			if (item != null && item.getItem().isEnchant() && (item instanceof ItemArmorInstance || item instanceof ItemWeaponInstance)) {
				if (cha instanceof PcInstance) {
					if (item.getEnLevel() < item.getItem().getSafeEnchant()) {
						ChattingController.toChatting(cha, "안전 인챈트보다 높은 아이템에 사용 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
						return;
					}
					
					int en = toEnchant(cha, item, this);
					
					item.toEnchant((PcInstance) cha, en);
					
					if (en != -127)
						cha.getInventory().count(this, getCount()-1, true);
				}
			} else {
				if ((item instanceof ItemArmorInstance || item instanceof ItemWeaponInstance) && !item.getItem().isEnchant())
					ChattingController.toChatting(cha, "인챈트가 불가능한 아아템 입니다.", Lineage.CHATTING_MODE_MESSAGE);
				else
					ChattingController.toChatting(cha, "인챈트가 가능한 아이템에 사용 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
			}		
		}
	}
}
