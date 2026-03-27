package lineage.world.object.item.scroll;

import lineage.bean.database.Definite;
import lineage.database.DefiniteDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_InventoryBress;
import lineage.network.packet.server.S_InventoryEquipped;
import lineage.network.packet.server.S_InventoryIdentify;
import lineage.network.packet.server.S_InventoryStatus;
import lineage.share.Lineage;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class ScrollLabeledKernodwel extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new ScrollLabeledKernodwel();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		ItemInstance item = cha.getInventory().value(cbp.readD());
		if(item != null){
			if(!item.isDefinite()){
				item.setDefinite(true);
				cha.toSender(S_InventoryBress.clone(BasePacketPooling.getPool(S_InventoryBress.class), item));
				if(Lineage.server_version <= 144){
					cha.toSender(S_InventoryEquipped.clone(BasePacketPooling.getPool(S_InventoryEquipped.class), item));
				}else{
					cha.toSender(S_InventoryStatus.clone(BasePacketPooling.getPool(S_InventoryStatus.class), item));
				}
			}
			Definite d = DefiniteDatabase.find(item.getItem().getNameIdNumber());
			if(d != null)
				cha.toSender(S_InventoryIdentify.clone(BasePacketPooling.getPool(S_InventoryIdentify.class), item, d.getType(), d.getMessage()));
		}

		cha.getInventory().count(this, getCount()-1, true);
	}

}
