package lineage.world.object.item.cloak;

import lineage.bean.lineage.Inventory;
import lineage.network.packet.ClientBasePacket;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemInstance;

public class CloakInvisibility extends ItemArmorInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new CloakInvisibility();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		// 3단착용 구현.
		if(cha.isInvis() && !cha.isBuffInvisiBility() && equipped){
			cha.setInvis(false);
			cha.setTransparent(false);
		}else{
			super.toClick(cha, cbp);
		}
	}
	
	@Override
	public void toEquipped(Character cha, Inventory inv){
		super.toEquipped(cha, inv);

		if(equipped){
			cha.setInvis(equipped);
		}else{
			cha.setTransparent(equipped);
			// 인비지블리티 마법시전상태 확인 : 시전상태가 아닐경우 투망해제.
			if(!cha.isBuffInvisiBility())
				cha.setInvis(equipped);
		}
	}
}
