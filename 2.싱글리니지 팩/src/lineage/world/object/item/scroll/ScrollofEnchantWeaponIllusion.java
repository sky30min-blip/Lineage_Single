package lineage.world.object.item.scroll;

import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.EventController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemIllusionInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.Enchant;

public class ScrollofEnchantWeaponIllusion extends Enchant {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new ScrollofEnchantWeaponIllusion();
		// 환상아이템 관리목록에 등록.
		EventController.appendIllusion((ItemIllusionInstance)item);
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if(cha.getInventory() != null){
			ItemInstance weapon = cha.getInventory().value(cbp.readD());
			if(weapon!=null && weapon.getItem().isEnchant() && weapon instanceof ItemWeaponInstance && EventController.containsIllusion((ItemWeaponInstance)weapon)){
				if(cha instanceof PcInstance)
					weapon.toEnchant((PcInstance)cha, toEnchant(cha, weapon, this));
				cha.getInventory().count(this, getCount()-1, true);
			}
		}
	}
	
}
