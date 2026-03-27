package lineage.world.object.item.yadolan;

import lineage.gui.GuiMain;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_InventoryAdd;
import lineage.network.packet.server.S_InventoryDelete;
import lineage.network.packet.server.S_InventoryStatus;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.share.Log;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class Seal_enchant extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new Seal_enchant();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		int chance= 0;
		long time = System.currentTimeMillis();
		String timeString = Util.getLocaleString(time, true);
		
		if (!isClick(cha))
			return;

		//
		ItemInstance target = cha.getInventory().value(cbp.readD());
		if (target == null)
			return;

		if (!target.getItem().getName().equalsIgnoreCase("전장의 가호")) {
			ChattingController.toChatting(cha, "전장의 가호 이외의 아이템은 인첸트 할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}

		if (target.getEnLevel() >= 8) {
			ChattingController.toChatting(cha, "더 이상 인첸트 할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		
		switch(target.getEnLevel()){
		 case 0: chance = Lineage.runup0;
			break;
		 case 1: chance = Lineage.runup1;
			break;
		 case 2: chance = Lineage.runup2;
			break;
		 case 3: chance = Lineage.runup3;
			break;
		 case 4: chance = Lineage.runup4;
			break;
		 case 5: chance = Lineage.runup5;
			break;
		 case 6: chance = Lineage.runup6;
			break;
		 case 7: chance = Lineage.runup7;
			break;
		}
		
		if (Util.random(1, 100) < chance) {
			target.setEnLevel(target.getEnLevel() + 1);
		
		 
			ChattingController.toChatting(cha, "데우스의 문장 강화에 성공하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
			cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, 13532), true);
			cha.toSender(S_InventoryStatus.clone(BasePacketPooling.getPool(S_InventoryStatus.class), target));
			String log = String.format("[%s] [인첸트 성공]\t [캐릭터: %s]\t [아이템: %s]\t [주문서: %s]\t [인첸증가: %d]", timeString, cha.getName(), Util.getItemNameToString(target, getCount()), Util.getItemNameToString(this, getCount()), 1);
			GuiMain.display.asyncExec(new Runnable() {
				public void run() {
					GuiMain.getViewComposite().getEnchantComposite().toLog(log);
				}
			});
		} else {
	
			ChattingController.toChatting(cha, "데우스의 문장 강화에 실패하여 증발 하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
			
			Log.appendItem(cha, "type|인첸트실패", String.format("item_name|%s", target.getName()), String.format("item_objid|%d", target.getObjectId()), String.format("scroll_name|%s", toStringDB()),
					String.format("scroll_objid|%d", getObjectId()), String.format("scroll_bress|%d", getBless()));
			
			String log = String.format("[%s] [인첸트 실패]\t [캐릭터: %s]\t [아이템: %s]\t [주문서: %s]", timeString, cha.getName(), Util.getItemNameToString(target, getCount()), Util.getItemNameToString(this, getCount()));
			
			cha.getInventory().count(target, target.getCount()-1, true);
			GuiMain.display.asyncExec(new Runnable() {
				public void run() {
					GuiMain.getViewComposite().getEnchantComposite().toLog(log);
				}
			});
		}

		// 아이템 수량 갱신
		cha.getInventory().count(this, getCount() - 1, true);
	}

}
