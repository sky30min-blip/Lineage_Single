package lineage.world.object.item.scroll;

import java.sql.Connection;

import lineage.database.CharactersDatabase;
import lineage.database.DatabaseConnection;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_ObjectName;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.ClanController;
import lineage.world.controller.PcMarketController;
import lineage.world.controller.PcTradeController;
import lineage.world.controller.RobotController;
import lineage.world.controller.WantedController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class ScrollChangeName extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if(item == null)
			item = new ScrollChangeName();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha.isWorldDelete() || cha.isDead() || cha == null || cha.isLock() || cha.getMap() == Lineage.teamBattleMap || cha.getMap() == Lineage.BattleRoyalMap)
			return;
		//
		cha.getInventory().changeName = this;
		//
		ChattingController.toChatting(cha, "변경할 캐릭명을 입력하여 주십시오.", Lineage.CHATTING_MODE_MESSAGE);	
	}

	@Override
	public void toClickFinal(Character cha, Object... opt) {
		if (cha instanceof PcInstance) {
			// 
			Connection con = null;
			try {
				con = DatabaseConnection.getLineage();
				//
				String name = (String)opt[0];
				name = name.replaceAll(" ", "").trim();
				
				if (name.getBytes("EUC-KR").length > 12) {
					ChattingController.toChatting(cha, "캐릭명은 6글자 이하로 가능합니다. 다시 시도하여 주십시오.", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
				//
				if(CharactersDatabase.isCharacterName(con, name) || RobotController.isName(name)) {
					ChattingController.toChatting(cha, "케릭명이 이미 존재합니다. 다시 시도하여 주십시오.", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
				//
				if(CharactersDatabase.isInvalidName(con, name)) {
					ChattingController.toChatting(cha, "사용할 수 없는 캐릭명입니다. 다시 시도하여 주십시오.", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
				
				ClanController.changeName((PcInstance) cha, cha.getName(), name);
				CharactersDatabase.updateCharacterName(cha.getName(), name);
				WantedController.changeName(cha.getName(), name);
				PcTradeController.changeName(cha.getObjectId(), name);
				PcMarketController.changeName(cha.getObjectId(), name);
				cha.setName(name);
				((PcInstance) cha).setTempName(name);
				cha.toSender(S_ObjectName.clone(BasePacketPooling.getPool(S_ObjectName.class), cha), true);
				ChattingController.toChatting(cha, "캐릭명이 변경되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(cha, "리스가 아닌 로그아웃 후 다시 접속하시기 바랍니다.", Lineage.CHATTING_MODE_MESSAGE);
			} catch (Exception e) {
			} finally {
				DatabaseConnection.close(con);
			}
			//
			cha.getInventory().count(this, getCount()-1, true);
		}
	}
}
