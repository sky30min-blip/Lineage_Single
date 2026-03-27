package kuberaitem;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_InventoryBress;
import lineage.network.packet.server.S_InventoryCount;
import lineage.network.packet.server.S_InventoryEquipped;
import lineage.network.packet.server.S_InventoryStatus;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;

public class BlessRemoves extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new BlessRemoves();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if(cha.getInventory() != null){
			ItemInstance item = cha.getInventory().value(cbp.readD());
			if(item!=null && item.getBless()==0 && (item.getItem().getType1().equalsIgnoreCase("armor") ||item.getItem().getType1().equalsIgnoreCase("weapon")) ) {
				//

				item.setBless(1);
				
				ChattingController.toChatting(cha, String.format("아이템에 깃든 축복이 사라집니다."), 20);
				//
				cha.getInventory().count(this, getCount()-1, true);
				//
				if(Lineage.server_version<=144){
					cha.toSender(S_InventoryEquipped.clone(BasePacketPooling.getPool(S_InventoryEquipped.class), item));
					cha.toSender(S_InventoryCount.clone(BasePacketPooling.getPool(S_InventoryCount.class), item));
				}else{
					
					cha.toSender(S_InventoryBress.clone(BasePacketPooling.getPool(S_InventoryBress.class), item));
					cha.toSender(S_InventoryStatus.clone(BasePacketPooling.getPool(S_InventoryStatus.class), item));
				}
			}else{
				ChattingController.toChatting(cha, String.format("조건을 만족하지 못했습니다(축복이 부여된 무기,방어구,장신구에만 사용이 가능합니다)"), 20);
			}
		}
	}

}
