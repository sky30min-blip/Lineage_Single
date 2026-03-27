package lineage.world.object.npc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import lineage.bean.lineage.Swap;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class ItemSwap extends object {
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		int idx = 0;

		if (pc.getSwap() == null)
			pc.setSwap(new HashMap<String, Swap[]>());
		if (pc.swapIdx == null)
			pc.swapIdx = new String[10];

		List<String> swap = new ArrayList<String>();
		swap.clear();

		Set<String> key = pc.getSwap().keySet();
		for (Iterator<String> iterator = key.iterator(); iterator.hasNext();) {
			String keyName = (String) iterator.next();
			swap.add(keyName);
			pc.swapIdx[idx] = keyName;
			idx++;
		}

		for (int i = 0; i < 10; i++)
			swap.add(" ");
		
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "itemswap", null, swap));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if (pc != null && !pc.isWorldDelete() && !pc.isLock() && !pc.isDead() && !pc.isFishing()) {
			// 현재 장비 저장.
			if (action.equalsIgnoreCase("save swap")) {
				if (pc.getSwap().size() >= 10) {
					ChattingController.toChatting(pc, "\\fY장비 스왑 등록은 최대 10개까지 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
				
				ChattingController.toChatting(pc, "\\fY[장비 스왑 등록] 명칭을 입력하시기 바랍니다. ex) 물방셋", Lineage.CHATTING_MODE_MESSAGE);
				pc.isInsertSwap = true;
			} else if (action.contains("index_")) {
				int idx = Integer.valueOf(action.replace("index_", ""));
				pc.selectSwap = pc.swapIdx[idx];
				Swap[] swapList = pc.getSwap(pc.selectSwap);

				if (swapList != null) {
					List<String> swap = new ArrayList<String>();
					swap.clear();
					
					swap.add(pc.selectSwap);
					swap.add(String.format("무기: %s", swapList[Lineage.SLOT_WEAPON] == null ? "등록된 아이템 없음" : Util.getItemNameToString(pc, swapList[Lineage.SLOT_WEAPON])));
					swap.add(String.format("투구: %s", swapList[Lineage.SLOT_HELM] == null ? "등록된 아이템 없음" : Util.getItemNameToString(pc, swapList[Lineage.SLOT_HELM])));
					swap.add(String.format("목걸이: %s", swapList[Lineage.SLOT_NECKLACE] == null ? "등록된 아이템 없음" : Util.getItemNameToString(pc, swapList[Lineage.SLOT_NECKLACE])));
					swap.add(String.format("귀걸이: %s", swapList[Lineage.SLOT_EARRING] == null ? "등록된 아이템 없음" : Util.getItemNameToString(pc, swapList[Lineage.SLOT_EARRING])));
					swap.add(String.format("셔츠: %s", swapList[Lineage.SLOT_SHIRT] == null ? "등록된 아이템 없음" : Util.getItemNameToString(pc, swapList[Lineage.SLOT_SHIRT])));
					swap.add(String.format("갑옷: %s", swapList[Lineage.SLOT_ARMOR] == null ? "등록된 아이템 없음" : Util.getItemNameToString(pc, swapList[Lineage.SLOT_ARMOR])));
					swap.add(String.format("망토: %s", swapList[Lineage.SLOT_CLOAK] == null ? "등록된 아이템 없음" : Util.getItemNameToString(pc, swapList[Lineage.SLOT_CLOAK])));
					swap.add(String.format("장갑: %s", swapList[Lineage.SLOT_GLOVE] == null ? "등록된 아이템 없음" : Util.getItemNameToString(pc, swapList[Lineage.SLOT_GLOVE])));
					swap.add(String.format("방패: %s", swapList[Lineage.SLOT_SHIELD] == null ? "등록된 아이템 없음" : Util.getItemNameToString(pc, swapList[Lineage.SLOT_SHIELD])));
					swap.add(String.format("반지: %s", swapList[Lineage.SLOT_RING_LEFT] == null ? "등록된 아이템 없음" : Util.getItemNameToString(pc, swapList[Lineage.SLOT_RING_LEFT])));
					swap.add(String.format("반지: %s", swapList[Lineage.SLOT_RING_RIGHT] == null ? "등록된 아이템 없음" : Util.getItemNameToString(pc, swapList[Lineage.SLOT_RING_RIGHT])));
					swap.add(String.format("벨트: %s", swapList[Lineage.SLOT_BELT] == null ? "등록된 아이템 없음" : Util.getItemNameToString(pc, swapList[Lineage.SLOT_BELT])));
					swap.add(String.format("부츠: %s", swapList[Lineage.SLOT_BOOTS] == null ? "등록된 아이템 없음" : Util.getItemNameToString(pc, swapList[Lineage.SLOT_BOOTS])));
					swap.add(String.format("가더: %s", swapList[Lineage.SLOT_GUARDER] == null ? "등록된 아이템 없음" : Util.getItemNameToString(pc, swapList[Lineage.SLOT_GUARDER])));
					swap.add(String.format("화살: %s", swapList[Lineage.SLOT_ARROW] == null ? "등록된 아이템 없음" : Util.getItemNameToString(pc, swapList[Lineage.SLOT_ARROW])));
					
					for (int i = 0; i < 5; i++)
						swap.add(" ");
					
					equipItem(pc);
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "swaplist", null, swap));					
				}
			} else if (action.equalsIgnoreCase("equip item")) {
				equipItem(pc);
			} else if (action.equalsIgnoreCase("remove swap")) {
				pc.removeSwap(pc.selectSwap);
				ChattingController.toChatting(pc, String.format("[%s] 장비 스왑 삭제 완료.", pc.selectSwap), Lineage.CHATTING_MODE_MESSAGE);
				this.toTalk(pc, null);
			} else if (action.equalsIgnoreCase("back")) {
				this.toTalk(pc, null);
			}
		}
	}
	
	public void equipItem(PcInstance pc){
		Swap[] swapList = pc.getSwap(pc.selectSwap);
		
		if (swapList != null) {					
			for (int i = 0; i < swapList.length; i++) {
				if (swapList[i] == null) {
					if (pc.getInventory().getSlot(i) != null) {
						pc.getInventory().getSlot(i).toClick(pc, null);
					}
					continue;
				}
				
				ItemInstance item = pc.getInventory().find(swapList[i].getItem(), swapList[i].getEnLevel(), swapList[i].getBless());	
				if (item != null && !item.isEquipped())
					item.toClick(pc, null);
			}
		}

		this.toTalk(pc, null);
		ChattingController.toChatting(pc, String.format("[%s] 장비 착용 완료.", pc.selectSwap), Lineage.CHATTING_MODE_MESSAGE);
	}
}
