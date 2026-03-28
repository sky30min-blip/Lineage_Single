package lineage.network.packet.client;

import kuberaitem.DeadRecovery;
import kuberaitem.ItemChange;
import kuberaitem.RandomDollOption;
import lineage.database.BackgroundDatabase;
import lineage.database.NpcSpawnlistDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.CommandController;
import lineage.world.controller.PcMarketController;
import lineage.world.controller.PcTradeController;
import lineage.world.controller.RankController;
import lineage.world.controller.RobotClanController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.BoardInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.RankBoardInstance;
import lineage.world.object.item.all_night.EnchantRecovery;
import lineage.world.object.item.all_night.ClassChangeTicket;
import lineage.world.object.item.yadolan.HuntingZoneTeleportationBook;
import lineage.world.object.npc.RobotClan;

public class C_ObjectTalkAction extends ClientBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length) {
		if (bp == null)
			bp = new C_ObjectTalkAction(data, length);
		else
			((C_ObjectTalkAction) bp).clone(data, length);
		return bp;
	}

	public C_ObjectTalkAction(byte[] data, int length) {
		clone(data, length);
	}

	@Override
	public BasePacket init(PcInstance pc) {
		// 버그 방지.
		if (pc == null || pc.isWorldDelete() || !isRead(4))
			return this;
		try {
			int objId = readD();
			String action = readS();
			String type = readS();
			object o = pc.findInsideList(objId);

			

			// 자동사냥
			if (action != null && action.contains("autohunt-")) {
				pc.toTalk(pc, action, type, this);
				return this;
			}

			// 랭킹게시판
			if (action.contains("rankcheck-")) {

				BoardInstance b = BackgroundDatabase.getRankBoard();

				b.toClick(pc, this);

				return this;
			}

			// 출석체크
			if (action.contains("playcheck-")) {
				if (!Lineage.attendance_check_enabled) {
					ChattingController.toChatting(pc, "출석체크는 현재 비활성화되어 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
					return this;
				}
				if (pc.getDaycount() > Lineage.lastday) {
					ChattingController.toChatting(pc, "출석체크를 전부 완료하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
				}else{
					NpcSpawnlistDatabase.playcheck.toTalk(pc, action, type, this);
					return this;
				}
			
				return this;
			}
		
			try {
			    int selectedAction = Integer.parseInt(action);

				
			    if (selectedAction >= 0 && selectedAction <= 100) {
		
					if (pc.isAutoSellDeleting) {
						if (pc.isAutoSellList.get(selectedAction) != null) {
							String valueAtIndex = pc.isAutoSellList.get(selectedAction);
							pc.isAutoSellList.remove(valueAtIndex);
							NpcSpawnlistDatabase.AutoSellItem.toTalk(pc, null);
							
							ChattingController.toChatting(pc, String.format("\\fY [자동판매 알림] '%s' 자동판매 목록에서 제외되었습니다", valueAtIndex), Lineage.CHATTING_MODE_MESSAGE);
			

							return this;

						} else {
							ChattingController.toChatting(pc, "[자동판매 알림] 존재하지 않는 아이템입니다 ", 20);
						}
						pc.isAutoSellDeleting = false;
						return this;
					}
				}

			} catch (NumberFormatException e) {

			}

			if (objId == NpcSpawnlistDatabase.AutoSellItem.getObjectId()) {
				NpcSpawnlistDatabase.AutoSellItem.toTalk(pc, action, type, this);
				return this;
			}
			// 보스 시간표
			if (action.contains("bossList-")) {
				NpcSpawnlistDatabase.bosstime.toTalk(pc, action, type, this);
				return this;
			}

			// 퀘스트
			if (action.contains("kquest-")) {

				if (pc.getQuestChapter() >= Lineage.lastquest) {
					ChattingController.toChatting(pc, "퀘스트를 전부 완료했습니다.", Lineage.CHATTING_MODE_MESSAGE);
				} else {
					NpcSpawnlistDatabase.quest.toTalk(pc, action, type, this);
					return this;
				}

				return this;
			}
			// 랜덤퀘스트
			if (action.contains("kquest2-")) {

				NpcSpawnlistDatabase.quest2.toTalk(pc, action, type, this);

				return this;
			}

			// 아이템쪽
			if (action.contains("ChangeOptions")) {
				RandomDollOption doll = pc.getInventory().is부여주문서(pc, objId);
				if (doll != null) {
					doll.toTalk(pc, action, type, this);
					return this;
				} else {
					ChattingController.toChatting(pc, "[알림] 인형 랜덤옵션 부여 주문서가 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
				return this;
			}
			if (action.contains("kicheck-")) {
				ItemChange kitemc = pc.getInventory().is아이템변경주문서(pc, objId);
				if (kitemc != null) {
					kitemc.toTalk(pc, action, type, this);
					return this;
				} else {
					ChattingController.toChatting(pc, "[알림] 주문서가 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
				return this;
			}
			// 시세 검색
			if (objId == PcMarketController.marketPriceNPC.getObjectId()) {
				PcMarketController.marketPriceNPC.toTalk(pc, action, type, this);
				return this;
			}
			if (objId == NpcSpawnlistDatabase.marketNpc.getObjectId()) {
				NpcSpawnlistDatabase.marketNpc.toTalk(pc, action, type, this);
				return this;
			}

			// 장비 스왑
			if (objId == NpcSpawnlistDatabase.itemSwap.getObjectId()) {
				NpcSpawnlistDatabase.itemSwap.toTalk(pc, action, type, this);
				return this;
			}

			// 자동 물약
			if (objId == NpcSpawnlistDatabase.autoPotion.getObjectId()) {
				NpcSpawnlistDatabase.autoPotion.toTalk(pc, action, type, this);
				return this;
			}

			
			RobotClan ci = RobotClanController.find무인혈맹(objId);
			// 무인혈맹
			if (ci != null) {
				ci.toTalk(pc, action, type, this);
				return this;
			}

			if (o != null && (pc.getGm() > 0 || !pc.isTransparent())) {
				o.toTalk(pc, action, type, this);
				return this;
			}

			if (pc.getInventory() != null) {
				EnchantRecovery enchant = pc.getInventory().is인첸트복구주문서(pc, objId);
				if (enchant != null) {
					pc.isAutoSellAdding = false;
			    	pc.isAutoSellDeleting = false;
					enchant.toTalk(pc, action, type, this);
					return this;
				}
	
				ClassChangeTicket classChange = pc.getInventory().is클래스변경주문서(pc, objId);
				if (classChange != null) {
					pc.isAutoSellAdding = false;
			    	pc.isAutoSellDeleting = false;
					classChange.toTalk(pc, action, type, this);
					return this;
				}

				HuntingZoneTeleportationBook book = pc.getInventory().istellbook(pc, objId);
				if (book != null) {
					pc.isAutoSellAdding = false;
			    	pc.isAutoSellDeleting = false;
					book.toTalk(pc, action, type, this);
					return this;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			// Handle exception
		}

		return this;
	}
}
