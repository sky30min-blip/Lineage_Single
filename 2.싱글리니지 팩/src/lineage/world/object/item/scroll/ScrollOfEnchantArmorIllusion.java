package lineage.world.object.item.scroll;

import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.EventController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemIllusionInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.Enchant;

public class ScrollOfEnchantArmorIllusion extends Enchant {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new ScrollOfEnchantArmorIllusion();
		// 환상아이템 관리목록에 등록.
		EventController.appendIllusion((ItemIllusionInstance)item);
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if(cha.getInventory() != null){
			ItemInstance armor = cha.getInventory().value(cbp.readD());
			if(armor!=null && armor.getItem().isEnchant() && armor instanceof ItemArmorInstance && EventController.containsIllusion((ItemArmorInstance)armor)){
				if(cha instanceof PcInstance)
					armor.toEnchant((PcInstance)cha, toEnchant(cha, armor, this));
				cha.getInventory().count(this, getCount()-1, true);
			}
		}
	}

}
