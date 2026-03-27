package lineage.world.object.npc;

import lineage.bean.database.Item;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.controller.CraftController;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class ManaStoneSynthesis extends object {
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maanNpc"));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (pc.getInventory() != null) {
			int rnd = Util.random(0, 100);
			
			if (action.equalsIgnoreCase("탄생의 마안")) {
				
					createItem(pc, "지룡의 마안", "수룡의 마안", "탄생의 마안", Lineage.탄생의마안_확률, Lineage.탄생의마안_제작_아덴, Lineage.탄생의마안_제작_아덴_수량);
			
			} else if (action.equalsIgnoreCase("형상의 마안")) {
		
					createItem(pc, "탄생의 마안", "풍룡의 마안", "형상의 마안", Lineage.형상의마안_확률, Lineage.형상의마안_제작_아덴, Lineage.형상의마안_제작_아덴_수량);
			
			} else if (action.equalsIgnoreCase("생명의 마안")) {
		
					createItem(pc, "형상의 마안", "화룡의 마안", "생명의 마안", Lineage.생명의마안_확률, Lineage.생명의마안_제작_아덴, Lineage.생명의마안_제작_아덴_수량);
				

			}else if (action.equalsIgnoreCase("화룡의 마안")) {
				
				// 재료 확인
				ItemInstance item1 = pc.getInventory().find("화룡 비늘");
				ItemInstance item2 = pc.getInventory().find("신성한 조각");
				ItemInstance item3 = pc.getInventory().find("고대의 훈장");
				ItemInstance item4 = pc.getInventory().find("아데나");
				
				if (item1 != null &&  item1.getCount() >= 30 && item2 != null &&  item2.getCount() >= 300 && item3 != null &&  item3.getCount() >= 300 && item4.getCount() >= 10000000) {
					// 확률
					int success_probability = 50;

					// 확률 계산
					if (rnd < success_probability) {
						// 제작 지급
						pc.getInventory().count(item1, item1.getCount() - 30, true);
						pc.getInventory().count(item2, item2.getCount() - 300, true);
						pc.getInventory().count(item3, item3.getCount() - 300, true);
						pc.getInventory().count(item4, item4.getCount() - 10000000, true);
						CraftController.toCraft(this, pc, ItemDatabase.find("화룡의 마안"), 1, true);
					} else{
						pc.getInventory().count(item1, item1.getCount() - 30, true);
						pc.getInventory().count(item2, item2.getCount() - 300, true);
						pc.getInventory().count(item3, item3.getCount() - 300, true);
						pc.getInventory().count(item4, item4.getCount() - 10000000, true);
						ChattingController.toChatting(pc, String.format("%s 제작에 실패하였습니다.", action), 20);
					
					
					}
				
				} else {
					
					ChattingController.toChatting(pc, "[알림] 재료가 부족합니다.", 20);
				}
			}else if (action.equalsIgnoreCase("수룡의 마안")) {
				
				// 재료 확인
				ItemInstance item1 = pc.getInventory().find("수룡 비늘");
				ItemInstance item2 = pc.getInventory().find("신성한 조각");
				ItemInstance item3 = pc.getInventory().find("고대의 훈장");
				ItemInstance item4 = pc.getInventory().find("아데나");
				
				
				if (item1 != null &&  item1.getCount() >= 30 && item2 != null &&  item2.getCount() >= 300 && item3 != null &&  item3.getCount() >= 300 && item4.getCount() >= 10000000) {
					// 확률
					int success_probability = 50;

					// 확률 계산
					if (rnd < success_probability) {
						// 제작 지급
						pc.getInventory().count(item1, item1.getCount() - 30, true);
						pc.getInventory().count(item2, item2.getCount() - 300, true);
						pc.getInventory().count(item3, item3.getCount() - 300, true);
						pc.getInventory().count(item4, item4.getCount() - 10000000, true);
						CraftController.toCraft(this, pc, ItemDatabase.find("수룡의 마안"), 1, true);
					} else{
						pc.getInventory().count(item1, item1.getCount() - 30, true);
						pc.getInventory().count(item2, item2.getCount() - 300, true);
						pc.getInventory().count(item3, item3.getCount() - 300, true);
						pc.getInventory().count(item4, item4.getCount() - 10000000, true);
						ChattingController.toChatting(pc, String.format("%s 제작에 실패하였습니다.", action), 20);
					
				
					}
			
				} else {
					ChattingController.toChatting(pc, "[알림] 재료가 부족합니다.", 20);
				}
			}else if (action.equalsIgnoreCase("풍룡의 마안")) {
				
				// 재료 확인
				ItemInstance item1 = pc.getInventory().find("풍룡 비늘");
				ItemInstance item2 = pc.getInventory().find("신성한 조각");
				ItemInstance item3 = pc.getInventory().find("고대의 훈장");
				ItemInstance item4 = pc.getInventory().find("아데나");
				
				
				if (item1 != null &&  item1.getCount() >= 30 && item2 != null &&  item2.getCount() >= 300 && item3 != null &&  item3.getCount() >= 300 && item4.getCount() >= 10000000) {
					// 확률
					int success_probability = 50;

					// 확률 계산
					if (rnd < success_probability) {
						// 제작 지급
						pc.getInventory().count(item1, item1.getCount() - 30, true);
						pc.getInventory().count(item2, item2.getCount() - 300, true);
						pc.getInventory().count(item3, item3.getCount() - 300, true);
						pc.getInventory().count(item4, item4.getCount() - 10000000, true);
						CraftController.toCraft(this, pc, ItemDatabase.find("풍룡의 마안"), 1, true);
					} else{
						pc.getInventory().count(item1, item1.getCount() - 30, true);
						pc.getInventory().count(item2, item2.getCount() - 300, true);
						pc.getInventory().count(item3, item3.getCount() - 300, true);
						pc.getInventory().count(item4, item4.getCount() - 10000000, true);
						ChattingController.toChatting(pc, String.format("%s 제작에 실패하였습니다.", action), 20);
				
					}
					
		
				} else {
					ChattingController.toChatting(pc, "[알림] 재료가 부족합니다.", 20);
				}
			}else if (action.equalsIgnoreCase("지룡의 마안")) {
				
				// 재료 확인
				ItemInstance item1 = pc.getInventory().find("지룡 비늘");
				ItemInstance item2 = pc.getInventory().find("신성한 조각");
				ItemInstance item3 = pc.getInventory().find("고대의 훈장");
				ItemInstance item4 = pc.getInventory().find("아데나");
				
				
				if (item1 != null &&  item1.getCount() >= 30 && item2 != null &&  item2.getCount() >= 300 && item3 != null &&  item3.getCount() >= 300 && item4.getCount() >= 10000000) {
					// 확률
					int success_probability = 50;

					// 확률 계산
					if (rnd < success_probability) {
						// 제작 지급
						pc.getInventory().count(item1, item1.getCount() - 30, true);
						pc.getInventory().count(item2, item2.getCount() - 300, true);
						pc.getInventory().count(item3, item3.getCount() - 300, true);
						pc.getInventory().count(item4, item4.getCount() - 10000000, true);
						CraftController.toCraft(this, pc, ItemDatabase.find("지룡의 마안"), 1, true);
					} else{
						pc.getInventory().count(item1, item1.getCount() - 30, true);
						pc.getInventory().count(item2, item2.getCount() - 300, true);
						pc.getInventory().count(item3, item3.getCount() - 300, true);
						pc.getInventory().count(item4, item4.getCount() - 10000000, true);
						ChattingController.toChatting(pc, String.format("%s 제작에 실패하였습니다.", action), 20);
					
				
					}
	
				} else {
					ChattingController.toChatting(pc, "[알림] 재료가 부족합니다.", 20);
				}
			}
		}
	}

	public void createItem(PcInstance pc, String name1, String name2, String newItemName, double percent, String aden, long adenCount) {
		if (pc.getInventory() != null) {
			// 재료
			ItemInstance item1 = null;
			// 재료
			ItemInstance item2 = null;
			// 재료
			ItemInstance item3 = null;
			
			if (adenCount > 0) {
				for (ItemInstance i : pc.getInventory().getList()) {
					if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase(name1) && !i.isEquipped())
						item1 = i;
					if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase(name2) && !i.isEquipped())
						item2 = i;
					if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase(aden) && i.getCount() >= adenCount && !i.isEquipped())
						item3 = i;
					
					if (item1 != null && item2 != null && item3 != null)
						break;
				}
				
				if (item1 != null && item2 != null && item3 != null) {
					Item i = ItemDatabase.find(newItemName);
					
					if (i != null) {
						if (pc.getGm() > 0 || Math.random() < percent) {
							ItemInstance temp = ItemDatabase.newInstance(i);
							temp.setObjectId(ServerDatabase.nextItemObjId());
							temp.setBless(1);
							temp.setEnLevel(0);
							temp.setDefinite(true);
							pc.getInventory().append(temp, true);

							ChattingController.toChatting(pc, String.format("'%s' 제작에 성공하였습니다! ", newItemName), Lineage.CHATTING_MODE_MESSAGE);
						} else {
							ChattingController.toChatting(pc, String.format("'%s' 제작에 실패하였습니다. ", newItemName), Lineage.CHATTING_MODE_MESSAGE);
						}

						pc.getInventory().count(item1, item1.getCount() - 1, true);
						pc.getInventory().count(item2, item2.getCount() - 1, true);
						pc.getInventory().count(item3, item3.getCount() - adenCount, true);
					}
				} else {
					ChattingController.toChatting(pc, String.format("%s, %s, %s(%,d) 필요합니다.", name1, name2, aden, adenCount), Lineage.CHATTING_MODE_MESSAGE);
				}
			} else {
				for (ItemInstance i : pc.getInventory().getList()) {
					if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase(name1) && !i.isEquipped())
						item1 = i;
					if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase(name2) && !i.isEquipped())
						item2 = i;
					
					if (item1 != null && item2 != null)
						break;
				}
				
				if (item1 != null && item2 != null) {
					Item i = ItemDatabase.find(newItemName);
					
					if (i != null) {
						if (pc.getGm() > 0 || Math.random() < percent) {
							ItemInstance temp = ItemDatabase.newInstance(i);
							temp.setObjectId(ServerDatabase.nextItemObjId());
							temp.setBless(1);
							temp.setEnLevel(0);
							temp.setDefinite(true);
							pc.getInventory().append(temp, true);

							ChattingController.toChatting(pc, String.format("'%s' 제작에 성공하였습니다! ", newItemName), Lineage.CHATTING_MODE_MESSAGE);
						} else {
							ChattingController.toChatting(pc, String.format("'%s' 제작에 실패하였습니다. ", newItemName), Lineage.CHATTING_MODE_MESSAGE);
						}

						pc.getInventory().count(item1, item1.getCount() - 1, true);
						pc.getInventory().count(item2, item2.getCount() - 1, true);
					}
				} else {
					ChattingController.toChatting(pc, String.format("%s, %s 필요합니다.", name1, name2), Lineage.CHATTING_MODE_MESSAGE);
				}
			}
		}
	}
}
