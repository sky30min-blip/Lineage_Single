package lineage.world.object.npc;

import java.util.ArrayList;
import java.util.List;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_SoundEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.potion.HealingPotion;

public class AutoPotion extends object {

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		showHtml(pc);
	}
	
	public void showHtml(PcInstance pc){
		if (pc.getInventory() != null) {
			if (pc.autoPotionIdx == null)
				pc.autoPotionIdx = new String[20];
			
			checkPotion(pc);
			
			List<String> autoPotion = new ArrayList<String>();
			autoPotion.clear();

			autoPotion.add(pc.isAutoPotion ? "ON" : "OFF");
			autoPotion.add(pc.autoPotionPercent < 1 ? "설정 X" : String.format("%d%% 이하 물약 복용", pc.autoPotionPercent));
			
			for (int i = 0; i < Lineage.auto_hunt_potion_hp_list.size(); i++) {
				autoPotion.add(String.format("%d%%", Lineage.auto_hunt_potion_hp_list.get(i)));
				
				if (i > 6) {
					break;
				}
			}
			
			for (int i = 0; i < 7 - Lineage.auto_hunt_potion_hp_list.size(); i++) {
				autoPotion.add(" ");
			}
			
			autoPotion.add(pc.autoPotionName == null || pc.autoPotionName.length() < 2 ? "설정 X" : pc.autoPotionName);
			
			// 인벤토리에서 물약종류를 선택.
			int idx = 0;
			for (ItemInstance potion : pc.getInventory().getList()) {
				if (potion != null && potion.getItem() != null && potion instanceof HealingPotion) {
					autoPotion.add(String.format("%s (%s)", potion.getItem().getName(), Util.changePrice(potion.getCount())));
					pc.autoPotionIdx[idx] = potion.getItem().getName();
					idx++;
				}
			}
			
			for (int i = 0; i < pc.autoPotionIdx.length; i++) {
				if (idx == 0 && i == 0) {
					autoPotion.add("인벤토리에 물약이 존재하지 않습니다.");
				} else {
					autoPotion.add(" ");
				}
			}

			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "autopotion", null, autoPotion));
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if (pc.getInventory() != null) {
			if (action.equalsIgnoreCase("on"))
				pc.isAutoPotion = true;
			
			if (action.equalsIgnoreCase("off"))
				pc.isAutoPotion = false;
			
			if (action.contains("percent-")) {
				action = action.replace("percent-", "");
				
				if (action.equalsIgnoreCase("1")) {
					if (Lineage.auto_hunt_potion_hp_list.size() > 0) {
						pc.autoPotionPercent = Lineage.auto_hunt_potion_hp_list.get(0);
					}
				} else if (action.equalsIgnoreCase("2")) {
					if (Lineage.auto_hunt_potion_hp_list.size() > 1) {
						pc.autoPotionPercent = Lineage.auto_hunt_potion_hp_list.get(1);
					}
				} else if (action.equalsIgnoreCase("3")) {
					if (Lineage.auto_hunt_potion_hp_list.size() > 2) {
						pc.autoPotionPercent = Lineage.auto_hunt_potion_hp_list.get(2);
					}
				} else if (action.equalsIgnoreCase("4")) {
					if (Lineage.auto_hunt_potion_hp_list.size() > 3) {
						pc.autoPotionPercent = Lineage.auto_hunt_potion_hp_list.get(3);
					}
				} else if (action.equalsIgnoreCase("5")) {
					if (Lineage.auto_hunt_potion_hp_list.size() > 4) {
						pc.autoPotionPercent = Lineage.auto_hunt_potion_hp_list.get(4);
					}
				} else if (action.equalsIgnoreCase("6")) {
					if (Lineage.auto_hunt_potion_hp_list.size() > 5) {
						pc.autoPotionPercent = Lineage.auto_hunt_potion_hp_list.get(5);
					}
				} else if (action.equalsIgnoreCase("7")) {
					if (Lineage.auto_hunt_potion_hp_list.size() > 6) {
						pc.autoPotionPercent = Lineage.auto_hunt_potion_hp_list.get(6);
					}
				}
			}
			if (pc.isSound()) {
				pc.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 7788));
			}
			if (action.contains("potion-")) {
				try {
					int idx = Integer.valueOf(action.replace("potion-", "").trim());
					pc.autoPotionName = pc.autoPotionIdx[idx];
				} catch (Exception e) {
					ChattingController.toChatting(pc, "[자동 물약] 물약 설정이 잘못되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
			}
			
			showHtml(pc);
		}
	}
	
	public void checkPotion(PcInstance pc) {
		// 인벤토리에서 설정한 물약 찾기.
		boolean isPotion = false;
		for (ItemInstance potion : pc.getInventory().getList()) {
			if (potion != null && potion.getItem() != null && potion instanceof HealingPotion && potion.getItem().getName().equalsIgnoreCase(pc.autoPotionName)) {
				isPotion = true;
				break;
			}
		}
		
		// 설정한 물약이 인벤토리에 존재하지 않으면 설정 초기화.
		if (!isPotion)
			pc.autoPotionName = null;
	}
}
