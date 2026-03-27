package lineage.world.object.npc;

import lineage.bean.database.Item;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class WeaponChange extends object {

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "weaponNpc"));
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if (pc.getInventory() != null) {			
			if (action.equalsIgnoreCase("고대 신의 창")) {
				createspear(pc);
			} else if (action.equalsIgnoreCase("진명황의 집행검")) {
				createTwoHandSword(pc);
			} else if (action.equalsIgnoreCase("가이아의 격노")) {
				createBow(pc);
			} else if (action.equalsIgnoreCase("수정 결정체 지팡이")) {
				createWand(pc);
			}
		}
	}
	
	public void createspear(PcInstance pc) {
		ItemInstance weapon1 = null;
		
		for (ItemInstance temp : pc.getInventory().getList()) {
			if (!temp.isEquipped() && temp.getEnLevel() >= 11 && temp.getItem().getName().equalsIgnoreCase("군터의 백드코빈" )) {
				weapon1 = temp;
				break;
			}
		}
		
		if (weapon1 != null) {
			ItemInstance weapon = null;
			Item i = ItemDatabase.find("고대 신의 창");
			
			if (i != null) {
				if (weapon1.isEquipped())
					weapon1.toClick(pc, null);
				
				pc.getInventory().remove(weapon1, true);
				
				weapon = ItemDatabase.newInstance(i);
				weapon.setObjectId(ServerDatabase.nextItemObjId());
				weapon.setBless(1);
				weapon.setEnLevel(0);
				weapon.setCount(1);
				weapon.setDefinite(true);
				pc.getInventory().append(weapon, true);
				
				ChattingController.toChatting(pc, String.format("무기 교환: %s 획득.", i.getName()), Lineage.CHATTING_MODE_MESSAGE);
			}							
		} else {
			ChattingController.toChatting(pc, "+11이상 군터의 백드코빈이 필요합니다.", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	public void createTwoHandSword(PcInstance pc) {
		ItemInstance weapon1 = null;
		
		for (ItemInstance temp : pc.getInventory().getList()) {
			if (!temp.isEquipped() && temp.getEnLevel() >= 11 && temp.getItem().getName().equalsIgnoreCase("나이트발드의 양손검")) {
				weapon1 = temp;
				break;
			}
		}
		
		if (weapon1 != null) {
			ItemInstance weapon = null;
			Item i = ItemDatabase.find("진명황의 집행검");
			
			if (i != null) {
				if (weapon1.isEquipped())
					weapon1.toClick(pc, null);
				
				pc.getInventory().remove(weapon1, true);
				
				weapon = ItemDatabase.newInstance(i);
				weapon.setObjectId(ServerDatabase.nextItemObjId());
				weapon.setBless(1);
				weapon.setEnLevel(0);
				weapon.setCount(1);
				weapon.setDefinite(true);
				pc.getInventory().append(weapon, true);
				
				ChattingController.toChatting(pc, String.format("무기 교환: %s 획득.", i.getName()), Lineage.CHATTING_MODE_MESSAGE);
			}							
		} else {
			ChattingController.toChatting(pc, "+11이상 나이트발드의 양손검이 필요합니다.", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	public void createBow(PcInstance pc) {
		ItemInstance weapon1 = null;
		
		for (ItemInstance temp : pc.getInventory().getList()) {
			if (!temp.isEquipped() && temp.getEnLevel() >= 11 && temp.getItem().getName().equalsIgnoreCase("악몽의 장궁")) {
				weapon1 = temp;
				break;
			}
		}
		
		if (weapon1 != null) {
			ItemInstance weapon = null;
			Item i = ItemDatabase.find("가이아의 격노");
			
			if (i != null) {
				if (weapon1.isEquipped())
					weapon1.toClick(pc, null);
				
				pc.getInventory().remove(weapon1, true);
				
				weapon = ItemDatabase.newInstance(i);
				weapon.setObjectId(ServerDatabase.nextItemObjId());
				weapon.setBless(1);
				weapon.setEnLevel(0);
				weapon.setCount(1);
				weapon.setDefinite(true);
				pc.getInventory().append(weapon, true);
				
				ChattingController.toChatting(pc, String.format("무기 교환: %s 획득.", i.getName()), Lineage.CHATTING_MODE_MESSAGE);
			}							
		} else {
			ChattingController.toChatting(pc, "+11이상 악몽의 장궁이 필요합니다.", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	public void createWand(PcInstance pc) {
		ItemInstance weapon1 = null;
		
		for (ItemInstance temp : pc.getInventory().getList()) {
			if (!temp.isEquipped() && temp.getEnLevel() >= 11 && temp.getItem().getName().equalsIgnoreCase("제로스의 지팡이")) {
				weapon1 = temp;
				break;
			}
		}
		
		if (weapon1 != null) {
			ItemInstance weapon = null;
			Item i = ItemDatabase.find("수정 결정체 지팡이");
			
			if (i != null) {
				if (weapon1.isEquipped())
					weapon1.toClick(pc, null);
				
				pc.getInventory().remove(weapon1, true);
				
				weapon = ItemDatabase.newInstance(i);
				weapon.setObjectId(ServerDatabase.nextItemObjId());
				weapon.setBless(1);
				weapon.setEnLevel(0);
				weapon.setCount(1);
				weapon.setDefinite(true);
				pc.getInventory().append(weapon, true);
				
				ChattingController.toChatting(pc, String.format("무기 교환: %s 획득.", i.getName()), Lineage.CHATTING_MODE_MESSAGE);
			}							
		} else {
			ChattingController.toChatting(pc, "+11이상 제로스의 지팡이가 필요합니다.", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
}
