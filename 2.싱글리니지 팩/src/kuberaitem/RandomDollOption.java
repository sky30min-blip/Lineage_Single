package kuberaitem;

import java.util.ArrayList;
import java.util.List;

import all_night.Lineage_Balance;
import lineage.bean.database.EnchantLostItem;
import lineage.database.EnchantLostItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_InventoryBress;
import lineage.network.packet.server.S_InventoryStatus;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.controller.TebeController;
import lineage.world.controller.WantedController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.instance.PcInstance;

public class RandomDollOption extends ItemInstance {
	public ItemInstance itema;
	public int percentage;

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new RandomDollOption();
		return item;
	}
	public void showHtml(PcInstance pc) {
	
		
		if (pc.getInventory() != null) {
			ItemInstance item = itema;
			if (item == null )
				return;
			
			// 착용 중
			if(item.isEquipped()){
				ChattingController.toChatting(cha, "[알림] 착용 중에는 사용 할 수 없습니다.", 20);
				return;
			}
			
			List<String> list = new ArrayList<String>();
			
			list.add(String.format("%d", percentage));
			
			list.add(String.format("%s", item.getItem().getName()));
			
			if(item.getInvDolloptionA() > 0){
				list.add(String.format("스턴 내성: %d", item.getInvDolloptionA()));
			}
			if(item.getInvDolloptionB() > 0){
				list.add(String.format("스턴 적중: %d", item.getInvDolloptionB()));
			}
			if(item.getInvDolloptionC() > 0){
				list.add(String.format("마법적중: %d", item.getInvDolloptionC()));
			}
			if(item.getInvDolloptionD() > 0){
				list.add(String.format("근/원거리 대미지: %d", item.getInvDolloptionD()));
			}
			if(item.getInvDolloptionE() > 0){
				list.add(String.format("근/원거리 명중: %d", item.getInvDolloptionE()));
			}
			
			if(item.getInvDolloptionA() > 0){
				list.add(String.format("스턴 내성: 10"));
			}
			if(item.getInvDolloptionB() > 0){
				list.add(String.format("스턴 적중: 10"));
			}
			if(item.getInvDolloptionC() > 0){
				list.add(String.format("마법적중: 10"));
			}
			if(item.getInvDolloptionD() > 0){
				list.add(String.format("근/원거리 대미지: 3"));
			}
			if(item.getInvDolloptionE() > 0){
				list.add(String.format("근/원거리 명중: 3"));
			}
			
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "dollrandom", null, list));
			
			
		}
	}


	public void toClick(Character cha, ClientBasePacket cbp) {
		PcInstance pc = (PcInstance) cha;
		ItemInstance item = cha.getInventory().value(cbp.readD());
		percentage = getItem().getSmallDmg();
		boolean check = item.getItem().getName().contains("마법인형: ");
		
		
		
		if(item.isEquipped()){
			ChattingController.toChatting(pc, "[알림] 착용 중에는 사용 할 수 없습니다.", 20);
			return;
		}

		if (!check) {
			ChattingController.toChatting(pc, "[알림]] 인형에만 사용 할 수 있습니다.", 20);
		}
		itema = item;
		if (cha instanceof PcInstance) {
			showHtml((PcInstance) cha);
		}
		
		
	}
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (action.equalsIgnoreCase("ChangeOptions")) {

			if (pc.getInventory() != null) {
				ItemInstance item = itema;
				int chance= getItem().getSmallDmg();
				
				if (item == null ){
					ChattingController.toChatting(pc, "[알림] 인형을 다시 선택해주세요 ", 20);
					return;
				}
				
	
				// 착용 중
				if(item.isEquipped()){
					ChattingController.toChatting(pc, "[알림] 착용 중에는 사용 할 수 없습니다.", 20);
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
						ChattingController.toChatting(pc,
								String.format("[알림]] %s : 스턴 내성 +%d의 옵션이 부여되었습니다.", item.toStringDB(), item.getInvDolloptionA()),
								20);
					} else if (item.getInvDolloptionB() > 0) {
						ChattingController.toChatting(pc,
								String.format("[알림]] %s : 스턴 적중 +%d의 옵션이 부여되었습니다.", item.toStringDB(), item.getInvDolloptionB()),
								20);
					} else if (item.getInvDolloptionC() > 0) {
						ChattingController.toChatting(pc,
								String.format("[알림]] %s : 마법 적중 +%d의 옵션이 부여되었습니다.", item.toStringDB(), item.getInvDolloptionC()),
								20);
					} else if (item.getInvDolloptionD() > 0) {
						ChattingController.toChatting(pc,
								String.format("[알림]] %s : 근거리,원거리 대미지 +%d의 옵션이 부여되었습니다.", item.toStringDB(), item.getInvDolloptionD()),
								20);
					} else if (item.getInvDolloptionE() > 0) {
						ChattingController.toChatting(pc,
								String.format("[알림]] %s : 근거리,원거리 명중 +%d의 옵션이 부여되었습니다.", item.toStringDB(), item.getInvDolloptionE()),
								20);
					}
					pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 16213), true);
					// 패킷
					pc.toSender(S_InventoryBress.clone(BasePacketPooling.getPool(S_InventoryBress.class), item));
					pc.toSender(S_InventoryStatus.clone(BasePacketPooling.getPool(S_InventoryStatus.class), item));
					
					
					}else{
						ChattingController.toChatting(pc, "랜덤 옵션 부여에 실패 하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
					}
			
					// 수량 감소
					pc.getInventory().count(this, getCount() - 1, true);
					
					showHtml(pc);
					
				} else {
					ChattingController.toChatting(pc, "[알림]] 인형에만 사용 할 수 있습니다.", 20);
					return;
				}
			}
		}

	}
}
