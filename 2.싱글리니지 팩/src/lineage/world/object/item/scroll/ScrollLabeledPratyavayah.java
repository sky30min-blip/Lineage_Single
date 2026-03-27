package lineage.world.object.item.scroll;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_InventoryBress;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class ScrollLabeledPratyavayah extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new ScrollLabeledPratyavayah();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		// \f1누군가가 도와주는 것같습니다.
		cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 155));
		ItemInstance item = null;
		if(bless == 0){
			// 축프라 처리. 원하는 아이템을 풀 수 있음.
			item = cha.getInventory().value(cbp.readD());
			if(item!=null && item.getBless()==2){
				item.setBless(1);
				if(Lineage.server_version>144)
					cha.toSender(S_InventoryBress.clone(BasePacketPooling.getPool(S_InventoryBress.class), item));
			}
		}else{
			// 걍 처리 착용중인 아이템 목록에서만 처리하면 됨.
			for(int i=0 ; i<=Lineage.SLOT_NONE ; ++i){
				item = cha.getInventory().getSlot(i);
				// 착용상태이며, 저주라면 보통으로 변경하기.
				if(item!=null && item.getBless()==2){
					item.setBless(1);
					if(Lineage.server_version>144)
						cha.toSender(S_InventoryBress.clone(BasePacketPooling.getPool(S_InventoryBress.class), item));
				}
			}
		}

		cha.getInventory().count(this, getCount()-1, true);
	}

}
