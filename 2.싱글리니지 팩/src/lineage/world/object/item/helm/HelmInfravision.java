package lineage.world.object.item.helm;

import lineage.bean.lineage.Inventory;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Ability;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemInstance;

public class HelmInfravision extends ItemArmorInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new HelmInfravision();
		return item;
	}
	
	@Override
	public void toEquipped(Character cha, Inventory inv){
		super.toEquipped(cha, inv);
		cha.toSender(S_Ability.clone(BasePacketPooling.getPool(S_Ability.class), 4, equipped));
	}

}
