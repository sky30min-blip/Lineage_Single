package lineage.world.object.item.all_night;

import lineage.bean.database.CharacterMarble;
import lineage.database.CharacterMarbleDatabase;
import lineage.network.LineageServer;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Disconnect;
import lineage.network.packet.server.S_MessageYesNo;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.PcMarketController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.PcShopInstance;

public class CharacterSaveMarble extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new CharacterSaveMarble();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (isClickState(cha)) {
			cha.setCharacterMarble(this);
			CharacterMarble cm = CharacterMarbleDatabase.getData(getObjectId());
			
			if (cm == null) {			
				String msg = "캐릭터가 창고에 보관되고 게임이 종료됩니다.";
				cha.toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 784, msg));
			} else {
				String msg = String.format("캐릭터를 슬롯에 등록하시겠습니까?", cm);
				cha.toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 785, msg));
			}
		}
	}
	
	public void toAsk(PcInstance pc, boolean yes) {
		if (pc.getClient() != null && isClickState(pc)) {
			if (yes) {
				if (!CharacterMarbleDatabase.checkInventory(pc, this)) {
					ChattingController.toChatting(pc, "\\fY인벤토리에 아이템이 존재합니다.", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
				
				PcShopInstance pc_shop = PcMarketController.getShop(pc.getObjectId());
				
				
				if (pc_shop != null) {
					ChattingController.toChatting(pc, "\\fY무인상점에 판매중인 아이템이 존재합니다.", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
				if (!CharacterMarbleDatabase.checkSellPcTrade(pc.getObjectId())) {
					ChattingController.toChatting(pc, "\\fY해당 캐릭터로 판매중인 현금 거래가 존재합니다.", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
				
				if (!CharacterMarbleDatabase.checkBuyPcTrade(pc.getObjectId())) {
					ChattingController.toChatting(pc, "\\fY해당 캐릭터로 구매중인 현금 거래가 존재합니다.", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
				
				if (!CharacterMarbleDatabase.checkWarehouse(pc.getClient().getAccountUid())) {
					ChattingController.toChatting(pc, "\\fY창고가 꽉 찼습니다.", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
				
				long item_objId = getObjectId();
				
				CharacterMarbleDatabase.insertDB(pc, this);
				pc.getInventory().count(this, getCount() - 1, true);
				
				// 사용자 강제종료 시키기.
				pc.toSender(S_Disconnect.clone(BasePacketPooling.getPool(S_Disconnect.class), 0x0A));
				LineageServer.close(pc.getClient());
				
				CharacterMarbleDatabase.removeItem(item_objId);
			}
		}
	}
	
	public void toAsk2(PcInstance pc, boolean yes) {		
		if (pc.getClient() != null && isClickState(pc)) {
			if (yes) {
				if (!CharacterMarbleDatabase.checkCharacterCount(pc)) {
					ChattingController.toChatting(pc, "\\fY캐릭터 슬롯이 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}

				if (CharacterMarbleDatabase.changeDB(pc, this)) {
					ChattingController.toChatting(pc, "\\fY캐릭터가 슬롯에 등록되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(pc, "\\fY로그인 창으로 나간 후 다시 로그인 하십시오.", Lineage.CHATTING_MODE_MESSAGE);
				}
				
				pc.getInventory().count(this, getCount() - 1, true);
			}
		}
	}
}
