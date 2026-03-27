package lineage.world.object.item.scroll;

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

public class ScrollOfEnchantElementalWeapon extends ItemInstance {

    static synchronized public ItemInstance clone(ItemInstance item){
        if(item == null)
            item = new ScrollOfEnchantElementalWeapon();
        return item;
    }
    
    @Override
    public void toClick(Character cha, ClientBasePacket cbp){
        if(cha.getInventory() != null){
            ItemInstance weapon = cha.getInventory().value(cbp.readD());
            if(weapon!=null && weapon.getItem().isEnchant() && weapon instanceof ItemWeaponInstance) {
            	
            	if(weapon.isEquipped()){
            	     ChattingController.toChatting(cha, String.format("무기를 착용상태에서는 불가능합니다."), 20);
                     return;
            	}
                if(weapon.getEnEarth()>= Lineage.danlevel || weapon.getEnWater()>= Lineage.danlevel || weapon.getEnFire()>= Lineage.danlevel || weapon.getEnWind()>= Lineage.danlevel){
                    ChattingController.toChatting(cha, String.format("더이상 강화가 불가능합니다.."), 20);
                    return;
                }
                //
                if(getItem().getNameIdNumber() == 5725){
                    if(weapon.getEnEarth()>0 || weapon.getEnWater()>0 || weapon.getEnFire()>0){
                        ChattingController.toChatting(cha, String.format("무기에 다른 속성을 부여 할 수 없습니다."), 20);
                        return;
                    }
                    int chance=0;
                    switch(weapon.getEnWind()){                        
                        case 0: chance=Lineage.dan1;
                            break;
                        case 1: chance=Lineage.dan2;
                            break;
                        case 2: chance=Lineage.dan3;
                            break;
                        case 3: chance=Lineage.dan4;
                            break;
                        case 4: chance=Lineage.dan5;
                            break;
                    }
                    
                    if(chance >= Util.random(1,100)){
                        weapon.setEnWind(weapon.getEnWind() + 1);
                        ChattingController.toChatting(cha, String.format("무기에 속성 부여 성공."), 20);
                    }else{
                   //   cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, 752), true);
                    	ChattingController.toChatting(cha, String.format("무기에 속성 부여 실패."), 20);
                    }
                }if(getItem().getNameIdNumber() == 5726){
                    if(weapon.getEnWind()>0 || weapon.getEnWater()>0 || weapon.getEnFire()>0){
                        ChattingController.toChatting(cha, String.format("무기에 다른 속성을 부여 할 수 없습니다."), 20);
                        return;
                    }
                    int chance=0;
                    switch(weapon.getEnEarth()){
                        
                    case 0: chance=Lineage.dan1;
                    break;
                case 1: chance=Lineage.dan2;
                    break;
                case 2: chance=Lineage.dan3;
                    break;
                case 3: chance=Lineage.dan4;
                    break;
                case 4: chance=Lineage.dan5;
                    break;
                    }
                    if(chance >= Util.random(1,100)){
                        weapon.setEnEarth(weapon.getEnEarth() + 1);
                        ChattingController.toChatting(cha, String.format("무기에 속성 부여 성공."), 20);
                    }else{
                      //  cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, 752), true);
                    	ChattingController.toChatting(cha, String.format("무기에 속성 부여 실패."), 20);

                    }
                }if(getItem().getNameIdNumber() == 5727){
                    if(weapon.getEnWind()>0 || weapon.getEnEarth()>0 || weapon.getEnFire()>0){
                        ChattingController.toChatting(cha, String.format("무기에 다른 속성을 부여 할 수 없습니다."), 20);
                        return;
                    }
                    int chance=0;
                    switch(weapon.getEnWater()){
                        
                    case 0: chance=Lineage.dan1;
                    break;
                case 1: chance=Lineage.dan2;
                    break;
                case 2: chance=Lineage.dan3;
                    break;
                case 3: chance=Lineage.dan4;
                    break;
                case 4: chance=Lineage.dan5;
                    break;
                    }
                    if(chance >= Util.random(1,100)){
                        weapon.setEnWater(weapon.getEnWater() + 1);
                        ChattingController.toChatting(cha, String.format("무기에 속성 부여 성공."), 20);
                    }else{
                  //      cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, 752), true);
                    	ChattingController.toChatting(cha, String.format("무기에 속성 부여 실패."), 20);

                    }
                }if(getItem().getNameIdNumber() == 5728){
                    if(weapon.getEnWind()>0 || weapon.getEnEarth()>0 || weapon.getEnWater()>0){
                        ChattingController.toChatting(cha, String.format("무기에 다른 속성을 부여 할 수 없습니다."), 20);
                        return;
                    }
                    int chance=0;
                    switch(weapon.getEnFire()){
                        
                    case 0: chance=Lineage.dan1;
                    break;
                case 1: chance=Lineage.dan2;
                    break;
                case 2: chance=Lineage.dan3;
                    break;
                case 3: chance=Lineage.dan4;
                    break;
                case 4: chance=Lineage.dan5;
                    break;
                    }
                    if(chance >= Util.random(1,100)){
                        weapon.setEnFire(weapon.getEnFire() + 1);
                        ChattingController.toChatting(cha, String.format("무기에 속성 부여 성공."), 20);
                    }else{
                  //      cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, 752), true);
                    	ChattingController.toChatting(cha, String.format("무기에 속성 부여 실패."), 20);

                    }
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

 

 

 