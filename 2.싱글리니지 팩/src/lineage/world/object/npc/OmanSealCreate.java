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

public class OmanSealCreate extends object {
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "orimCreate"));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (pc.getInventory() != null) {
	
			if (action.equalsIgnoreCase("오만의 탑 1층 지배 부적")) {
				createItem(pc, "오만의탑 1층 이동 부적", 1, "오만의탑 1층 이동 주문서", 500, "오만의 탑 1층 지배 부적", 1, 1, 10);

			} 
	
			if (action.equalsIgnoreCase("오만의 탑 2층 지배 부적")) {
				createItem(pc, "오만의탑 2층 이동 부적", 1, "오만의탑 2층 이동 주문서", 500, "오만의 탑 2층 지배 부적", 1, 1, 10);

			}
	
			if (action.equalsIgnoreCase("오만의 탑 3층 지배 부적")) {
				createItem(pc, "오만의탑 3층 이동 부적", 1, "오만의탑 3층 이동 주문서", 500, "오만의 탑 3층 지배 부적", 1, 1, 10);

			}
		
			if (action.equalsIgnoreCase("오만의 탑 4층 지배 부적")) {
				createItem(pc, "오만의탑 4층 이동 부적", 1, "오만의탑 4층 이동 주문서", 500, "오만의 탑 4층 지배 부적", 1, 1, 10);

			}
			
			if (action.equalsIgnoreCase("오만의 탑 5층 지배 부적")) {
				createItem(pc, "오만의탑 5층 이동 부적", 1, "오만의탑 5층 이동 주문서", 500, "오만의 탑 5층 지배 부적", 1, 1, 10);

			} 
			
			if (action.equalsIgnoreCase("오만의 탑 6층 지배 부적")) {
				createItem(pc, "오만의탑 6층 이동 부적", 1, "오만의탑 6층 이동 주문서", 500, "오만의 탑 6층 지배 부적", 1, 1, 10);

			} 
			
			if (action.equalsIgnoreCase("오만의 탑 7층 지배 부적")) {
				createItem(pc, "오만의탑 7층 이동 부적", 1, "오만의탑 7층 이동 주문서", 500, "오만의 탑 7층 지배 부적", 1, 1, 10);

			} 
			
			if (action.equalsIgnoreCase("오만의 탑 8층 지배 부적")) {
				createItem(pc, "오만의탑 8층 이동 부적", 1, "오만의탑 8층 이동 주문서", 500, "오만의 탑 8층 지배 부적", 1, 1, 10);

			} 
			
			if (action.equalsIgnoreCase("오만의 탑 9층 지배 부적")) {
				createItem(pc, "오만의탑 9층 이동 부적", 1, "오만의탑 9층 이동 주문서", 500, "오만의 탑 9층 지배 부적", 1, 1, 10);

			} 
			 
			if (action.equalsIgnoreCase("오만의 탑 10층 지배 부적")) {
				createItem(pc, "오만의탑 10층 이동 부적", 1, "오만의탑 10층 이동 주문서", 500, "오만의 탑 10층 지배 부적", 1, 1, 10);

			}
		}
	}


	
	public void createItem(PcInstance pc, String name1, long count, String name2, long count2, String newItemName, int bless, long createCount, double percent) {
	    if (pc.getInventory() != null) {
	        // 재료
	        ItemInstance item1 = null;
	        // 재료
	        ItemInstance item2 = null;

	        for (ItemInstance i : pc.getInventory().getList()) {
	            if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase(name1) && !i.isEquipped() && i.getCount() >= count)
	                item1 = i;
	            if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase(name2) && !i.isEquipped() && i.getCount() >= count2)
	                item2 = i;

	            if (item1 != null && item2 != null)
	                break;
	        }

	        if (item1 != null && item2 != null) {
	            Item i = ItemDatabase.find(newItemName);

	            if (i != null) {
	                if (pc.getGm() > 0 || Math.random() < (percent * 0.01)) {
	                    ItemInstance temp = pc.getInventory().find(i.getName(), bless, i.isPiles());

	                    if (temp != null && (temp.getBless() != bless || temp.getEnLevel() != 0))
	                        temp = null;

	                    if (temp == null) {
	                        // 겹칠수 있는 아이템이 존재하지 않을경우.
	                        if (i.isPiles()) {
	                            temp = ItemDatabase.newInstance(i);
	                            temp.setObjectId(ServerDatabase.nextItemObjId());
	                            temp.setBless(bless);
	                            temp.setEnLevel(0);
	                            temp.setCount(createCount);
	                            temp.setDefinite(true);
	                            pc.getInventory().append(temp, true);
	                        } else {
	                            for (int idx = 0; idx < createCount; idx++) {
	                                temp = ItemDatabase.newInstance(i);
	                                temp.setObjectId(ServerDatabase.nextItemObjId());
	                                temp.setBless(bless);
	                                temp.setEnLevel(0);
	                                temp.setDefinite(true);
	                                pc.getInventory().append(temp, true);
	                            }
	                        }
	                        // 제작 성공 시 item1과 item2 모두 소모
	                        pc.getInventory().count(item1, item1.getCount() - count, true);
	                        pc.getInventory().count(item2, item2.getCount() - count2, true);
	                        ChattingController.toChatting(pc, String.format("'%s' 제작에 성공하였습니다! ", newItemName), Lineage.CHATTING_MODE_MESSAGE);
	                    } 
	                } else {
	               	  // 제작 실패 시 item2만 소모
                        pc.getInventory().count(item2, item2.getCount() - count2, true);
	                    ChattingController.toChatting(pc, String.format("'%s' 제작에 실패하였습니다. ", newItemName), Lineage.CHATTING_MODE_MESSAGE);
	                }
	            }
	        } else {
	            ChattingController.toChatting(pc, String.format("%s(%,d), %s(%,d) 필요합니다.", name1, count, name2, count2), Lineage.CHATTING_MODE_MESSAGE);
	        }
	    }
	}
}
