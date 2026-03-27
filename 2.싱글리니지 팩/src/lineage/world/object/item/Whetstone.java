package lineage.world.object.item;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_InventoryStatus;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class Whetstone extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new Whetstone();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		cha.getInventory().count(this, getCount()-1, true);

		ItemInstance item = cha.getInventory().value(cbp.readD());
		if(item!=null && item.getDurability()>0){
			item.setDurability( item.getDurability()-1 );
			if(Lineage.server_version >= 160)
				cha.toSender(S_InventoryStatus.clone(BasePacketPooling.getPool(S_InventoryStatus.class), item));

			if(item.getDurability()==0)
				// %0의 상태가 좋아졌습니다.
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 464, item.toString()));
			else
				// %0%s 이제 새 것처럼 되었습니다.
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 463, item.toString()));
		}
	}

}
