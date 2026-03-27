package lineage.world.object.item.quest;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class ElvenTreasure extends ItemInstance {

	private List<Item> quest_item;
	
	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new ElvenTreasure();
		return item;
	}
	
	public ElvenTreasure(){
		quest_item = new ArrayList<Item>();
		quest_item.add(ItemDatabase.find("요정족 티셔츠"));
		quest_item.add(ItemDatabase.find("정령의 수정 (서먼 레서 엘리멘탈)"));
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		// 퀘스트 아이템 지급.
		for(Item i : quest_item){
			ItemInstance ii = ItemDatabase.newInstance(i);
			ii.setObjectId(ServerDatabase.nextItemObjId());
			cha.getInventory().append(ii, true);
		}
		// 제거
		cha.getInventory().count(this, 0, true);
	}

}
