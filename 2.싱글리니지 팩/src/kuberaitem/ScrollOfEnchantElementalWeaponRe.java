package kuberaitem;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
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

public class ScrollOfEnchantElementalWeaponRe extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new ScrollOfEnchantElementalWeaponRe();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if(cha.getInventory() != null){
			ItemInstance weapon = cha.getInventory().value(cbp.readD());
			if(weapon!=null && weapon.getItem().isEnchant() && weapon instanceof ItemWeaponInstance) {
				//

				if(weapon.getEnWind()==0 && weapon.getEnEarth()==0 && weapon.getEnWater()==0 && weapon.getEnFire()==0){
					ChattingController.toChatting(cha, String.format("현재 속성이 0 단 입니다."), 20);					
					return;
				}
				if(weapon.getEnWind()>0){
					weapon.setEnWind(weapon.getEnWind() - 1);
					ChattingController.toChatting(cha, String.format("무기에 깃든 속성이 사라집니다."), 20);
				}
				if(weapon.getEnEarth()>0){						
					weapon.setEnEarth(weapon.getEnEarth() - 1);
					ChattingController.toChatting(cha, String.format("무기에 깃든 속성이 사라집니다."), 20);
						
				}if(weapon.getEnWater()>0){
					weapon.setEnWater(weapon.getEnWater() - 1);
					ChattingController.toChatting(cha, String.format("무기에 깃든 속성이 사라집니다."), 20);
					
				}if(weapon.getEnFire()>0){
					weapon.setEnFire(weapon.getEnFire() - 1);
					ChattingController.toChatting(cha, String.format("무기에 깃든 속성이 사라집니다."), 20);				
					
				}
				
				
				//
				cha.getInventory().count(this, getCount()-1, true);
				//
				if(Lineage.server_version<=144){
					cha.toSender(S_InventoryEquipped.clone(BasePacketPooling.getPool(S_InventoryEquipped.class), weapon));
					cha.toSender(S_InventoryCount.clone(BasePacketPooling.getPool(S_InventoryCount.class), weapon));
				}else{
					cha.toSender(S_InventoryStatus.clone(BasePacketPooling.getPool(S_InventoryStatus.class), weapon));
				}
			}
		}
	}

}
