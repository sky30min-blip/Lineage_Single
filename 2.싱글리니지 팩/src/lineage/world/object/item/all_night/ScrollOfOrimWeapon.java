package lineage.world.object.item.all_night;

import all_night.Lineage_Balance;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.Enchant;

public class ScrollOfOrimWeapon extends Enchant {
	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new ScrollOfOrimWeapon();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha.getInventory() != null) {
			ItemInstance weapon = cha.getInventory().value(cbp.readD());

			if (weapon != null && weapon.getItem().isEnchant() && weapon instanceof ItemWeaponInstance) {
				if (cha instanceof PcInstance) {
					
					if (weapon.getEnLevel() < Lineage_Balance.orim_weapon_min_en) {
						ChattingController.toChatting(cha, String.format("+%d 이상 무기에 사용 가능합니다.", Lineage_Balance.orim_weapon_min_en), Lineage.CHATTING_MODE_MESSAGE);
						return;
					}

					int en = toEnchant(cha, weapon, this);

					weapon.toEnchant((PcInstance) cha, en);

					if (en != -127)
						cha.getInventory().count(this, getCount() - 1, true);
				}
			} else {
				if (weapon instanceof ItemWeaponInstance && !weapon.getItem().isEnchant())
					ChattingController.toChatting(cha, "인챈트가 불가능한 무기입니다.", Lineage.CHATTING_MODE_MESSAGE);
				else
					ChattingController.toChatting(cha, "무기에 사용 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}
}
