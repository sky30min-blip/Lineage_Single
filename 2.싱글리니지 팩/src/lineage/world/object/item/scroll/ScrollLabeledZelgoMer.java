package lineage.world.object.item.scroll;

import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.Enchant;

public class ScrollLabeledZelgoMer extends Enchant {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new ScrollLabeledZelgoMer();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha.getInventory() != null) {
			ItemInstance armor = cha.getInventory().value(cbp.readD());
			if (armor.getItem().getType2().equals("belt") || armor.getItem().getType2().equals("ring") || armor.getItem().getType2().equals("necklace") || armor.getItem().getType2().equals("earing")) {
				ChattingController.toChatting(cha, "장신구 주문서만 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			if (armor.getItem().getName().equalsIgnoreCase("수호성의 파워 글로브") || armor.getItem().getName().equalsIgnoreCase("수호성의 활 골무")) {
				if (armor.isEquipped()) {
					ChattingController.toChatting(cha, "해당 아이템은 착용해제 후 인챈트 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
			}
			
			if (armor.getItem().getName().equalsIgnoreCase("전장의 가호")){
				ChattingController.toChatting(cha, "해당아이템에는 사용 불가능 합니다", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			if (armor != null && armor.getItem().isEnchant() && armor instanceof ItemArmorInstance) {
				if (cha instanceof PcInstance) {
					int en = toEnchant(cha, armor, this);
					
					armor.toEnchant((PcInstance) cha, en);
					
					if (en != -127)
						cha.getInventory().count(this, getCount()-1, true);
				}
			} else {
				if (armor instanceof ItemArmorInstance && !armor.getItem().isEnchant())
					ChattingController.toChatting(cha, "인챈트가 불가능한 방어구 입니다.", Lineage.CHATTING_MODE_MESSAGE);
				else
					ChattingController.toChatting(cha, "방어구에 사용 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}
}
