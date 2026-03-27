package kuberaitem;

import all_night.Lineage_Balance;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_InventoryBress;
import lineage.network.packet.server.S_InventoryStatus;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;

public class RandomDollOptionGrantingOrderForm extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new RandomDollOptionGrantingOrderForm();
		return item;
	}
   //쿠베라
	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		
		if (cha.getInventory() != null) {
			ItemInstance item = cha.getInventory().value(cbp.readD());
			int chance= getItem().getSmallDmg();
			if (item == null )
				return;
			
			// 착용 중
			if(item.isEquipped()){
				ChattingController.toChatting(cha, "[알림] 착용 중에는 사용 할 수 없습니다.", 20);
				return;
			}
			
			boolean check = item.getItem().getName().contains("마법인형: ");
	
			
			if (check) {
			
				
				//a 스턴내성 1~10     랜덤
				//b 스턴적중 1~10     랜덤
				//c 마법적중 1~10     랜덤
				//d 근거리데미지 1~3 랜덤
				//e 원거리데미지 1~3 랜덤

				if (Util.random(1,100) < chance) {
					
					item.setInvDolloptionA(0);	
					item.setInvDolloptionB(0); 			
					item.setInvDolloptionC(0);
					item.setInvDolloptionD(0); 
					item.setInvDolloptionE(0); 
					
					// 랜덤 옵션 부여
				switch (Util.random(1, 5)) {
					case 1:
						item.setInvDolloptionA(Util.random(1, 10)); // 스턴내성
						break;
					case 2:
						item.setInvDolloptionB(Util.random(1, 10)); // 스턴적중
						break;
					case 3:
						item.setInvDolloptionC(Util.random(1, 10)); // 마법적중
						break;
					case 4:
						item.setInvDolloptionD(Util.random(1, 3)); // 근거리데미지
						break;
					case 5:
						item.setInvDolloptionE(Util.random(1, 3)); // 원거리데미지
						break;
					}
				
				// 알림
				if (item.getInvDolloptionA() > 0) {
					ChattingController.toChatting(cha,
							String.format("[알림]] %s : 스턴 내성 +%d의 옵션이 부여되었습니다.", item.toStringDB(), item.getInvDolloptionA()),
							20);
				} else if (item.getInvDolloptionB() > 0) {
					ChattingController.toChatting(cha,
							String.format("[알림]] %s : 스턴 적중 +%d의 옵션이 부여되었습니다.", item.toStringDB(), item.getInvDolloptionB()),
							20);
				} else if (item.getInvDolloptionC() > 0) {
					ChattingController.toChatting(cha,
							String.format("[알림]] %s : 마법 적중 +%d의 옵션이 부여되었습니다.", item.toStringDB(), item.getInvDolloptionC()),
							20);
				} else if (item.getInvDolloptionD() > 0) {
					ChattingController.toChatting(cha,
							String.format("[알림]] %s : 근거리,원거리 대미지 +%d의 옵션이 부여되었습니다.", item.toStringDB(), item.getInvDolloptionD()),
							20);
				} else if (item.getInvDolloptionE() > 0) {
					ChattingController.toChatting(cha,
							String.format("[알림]] %s : 근거리,원거리 명중 +%d의 옵션이 부여되었습니다.", item.toStringDB(), item.getInvDolloptionE()),
							20);
				}
				// 패킷
				cha.toSender(S_InventoryBress.clone(BasePacketPooling.getPool(S_InventoryBress.class), item));
				cha.toSender(S_InventoryStatus.clone(BasePacketPooling.getPool(S_InventoryStatus.class), item));
				
				
				}else{
					ChattingController.toChatting(cha, "랜덤 옵션 부여에 실패 하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
		
				// 수량 감소
				cha.getInventory().count(this, getCount() - 1, true);
				
		
			} else {
				ChattingController.toChatting(cha, "[알림]] 인형에만 사용 할 수 있습니다.", 20);
				return;
			}
		}
	}
}
