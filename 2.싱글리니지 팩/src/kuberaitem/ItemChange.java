package kuberaitem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class ItemChange extends ItemInstance {
	public ItemInstance itema;

	public List<String> itemList = new ArrayList<String>();
	
	private List<String> allowedChangeItems = Arrays.asList("무관의 양손검", "뇌신검", "미스릴 단검", "살천의 활", "강철 마나의 지팡이", "파괴의 크로우", "파괴의 이도류");

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new ItemChange();
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
            list.add(String.format("%s", item.getItem().getName()));


            String currentItemName = item.getItem().getName();
            if (allowedChangeItems.contains(currentItemName)) {
                itemList.addAll(allowedChangeItems);
                list.addAll(allowedChangeItems);
            }

			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "itemchange", null, list));
			
			
		}
	}


	public void toClick(Character cha, ClientBasePacket cbp) {
	    PcInstance pc = (PcInstance) cha;
	    ItemInstance item = cha.getInventory().value(cbp.readD());


	    if (item.isEquipped()) {
	        ChattingController.toChatting(pc, "[알림] 착용 중에는 사용 할 수 없습니다.", 20);
	        return;
	    }


	    String currentItemName = item.getItem().getName();
	    boolean isAllowedItem = false;


	    if (allowedChangeItems.contains(currentItemName)) {
	        isAllowedItem = true;
	    }

	    if (!isAllowedItem) {
	        ChattingController.toChatting(pc, "[알림] 해당 아이템은 변경이 불가능합니다", 20);
	        return;
	    }


	    itema = item;
	    if (cha instanceof PcInstance) {
	        showHtml((PcInstance) cha);
	    }
	}
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
	
	    List<String> actionKeywords = Arrays.asList("kicheck-1", "kicheck-2", "kicheck-3", "kicheck-4", "kicheck-5", "kicheck-6" , "kicheck-7");


	    if (actionKeywords.contains(action)) {
	        int actionIndex = actionKeywords.indexOf(action);

	        String checkItem = itemList.get(actionIndex);
	        ItemInstance item = itema;

	        if (item == null) {
	        
	            return;
	        }

	        if (item.getItem().getName().equalsIgnoreCase(checkItem)) {
	            ChattingController.toChatting(pc, "[알림] 같은 아이템으로는 변경이 불가능합니다", 20);
	            return;
	        }

	        if (item.isEquipped()) {
	            ChattingController.toChatting(pc, "[알림] 착용 중에는 사용 할 수 없습니다.", 20);
	            return;
	        }

	        if (checkItem == null) {
	            itema = null;
	            return;
	        }

	        if (checkItem != null) {
	            ItemInstance ii = ItemDatabase.newInstance(ItemDatabase.find(checkItem));

	            ii.setCount(1);
	            ii.setEnLevel(item.getEnLevel());
	            ii.setDefinite(true);
	            ii.setBless(item.getBless());
	            pc.getInventory().count(this, getCount() - 1, true);
	            pc.getInventory().count(item, item.getCount() - item.getCount(), true);
	            pc.toGiveItem(null, ii, ii.getCount());
	            pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 13533), true);
	            S_Html htmlPacket = new S_Html(this, "", null, null);
	            pc.toSender(htmlPacket);

	            itemList.clear();
	            itema = null;
	        }
	    }
	}
}