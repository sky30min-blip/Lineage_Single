package lineage.world.object.monster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lineage.bean.database.Item;
import lineage.bean.database.Monster;
import lineage.bean.lineage.Craft;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_InventoryCount;
import lineage.network.packet.server.S_InventoryEquipped;
import lineage.network.packet.server.S_InventoryStatus;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.CraftController;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class Blob extends Slime {
	
	private Map<Item, List<Craft>> list;			// 제작될아이템(item) 과 연결된 재료 목록
	private Map<String, Item> craft_list;			// 요청청 문자(action)와 연결될 제작될아이템(item)
	
	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m){
		if(mi == null)
			mi = new Blob();
		return MonsterInstance.clone(mi, m);
	}
	
	public Blob(){
		list = new HashMap<Item, List<Craft>>();
		craft_list = new HashMap<String, Item>();
		
		// 제작 처리 초기화.
		Item i = ItemDatabase.find("단검신");
		if(i != null){
			craft_list.put("1", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("페어리의 날개"), 1) );
			l.add( new Craft(ItemDatabase.find("미스릴"), 50) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("장검신");
		if(i != null){
			craft_list.put("2", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("페어리의 날개"), 3) );
			l.add( new Craft(ItemDatabase.find("미스릴"), 150) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("오리하루콘 검신");
		if(i != null){
			craft_list.put("3", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("페어리의 날개"), 3) );
			l.add( new Craft(ItemDatabase.find("오리하루콘"), 150) );
			l.add( new Craft(ItemDatabase.find("루비"), 3) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("미스릴 도금 뿔");
		if(i != null){
			craft_list.put("4", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("판의 뿔"), 2) );
			l.add( new Craft(ItemDatabase.find("미스릴"), 80) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("오리하루콘 도금 뿔");
		if(i != null){
			craft_list.put("5", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("판의 뿔"), 4) );
			l.add( new Craft(ItemDatabase.find("오리하루콘"), 80) );
			l.add( new Craft(ItemDatabase.find("루비"), 3) );
			list.put(i, l);
		}
	}
	
	@Override
	protected void toAiDead(long time){
		// 재료 아이템 확인해서 처리하기.
		for(Item i : craft_list.values()){
			List<Craft> l = list.get(i);
			
			if (i.getName().equalsIgnoreCase("단검신")) {
				boolean result1 = true;
				boolean result2 = true;
				int have_count = 0;
				
				for (Craft c : l) {
					// 초기화
					have_count = 0;
					List<ItemInstance> temp_list = new ArrayList<ItemInstance>();
					// 검색
					getInventory().findDbName(c.getItem().getName(), temp_list);
					for (ItemInstance ii : temp_list) {
						if (!ii.isEquipped())
							have_count += ii.getCount();
					}

					// 갯수 확인
					if (c.getItem().getName().equalsIgnoreCase("페어리의 날개") && have_count >= 3)
						result1 = false;
					if (c.getItem().getName().equalsIgnoreCase("미스릴") && have_count >= 150)
						result2 = false;
				}
				
				if (!result1 && !result2)
					continue;
			}

			// 재료 확인.
			while(CraftController.isCraft(this, l, false)){
				// 재료 제거.
				CraftController.toCraft(this, l);
				// 제작된 아이템 등록.
				CraftController.toCraft(this, this, i, 1, false);
			}
		}
		// 뒷처리.
		super.toAiDead(time);
	}
	
	@Override
	public void toAttack(object o, int x, int y, boolean bow, int gfxMode, int alpha_dmg, boolean isTriple){
		super.toAttack(o, x, y, bow, gfxMode, alpha_dmg, isTriple);

		if(o instanceof PcInstance){
			ItemInstance item = null;
			// 버전별로 손상시킬 장비 구분.
			if(Lineage.server_version <= 150){
				item = o.getInventory().getSlot(Lineage.SLOT_WEAPON);
			}else{
				switch(Util.random(0, 1)){
					case 0:
						item = o.getInventory().getSlot( Util.random(Lineage.SLOT_SHIRT, Lineage.SLOT_CLOAK) );
						break;
					case 1:
						item = o.getInventory().getSlot( Util.random(Lineage.SLOT_GLOVE, Lineage.SLOT_BOOTS) );
						break;
				}
			}
			// 
			if(item!=null && o.isBuffSoulOfFlame()==false){
				item.setDurability(item.getDurability() + 1);
				if(Lineage.server_version<=144){
					o.toSender(S_InventoryEquipped.clone(BasePacketPooling.getPool(S_InventoryEquipped.class), item));
					o.toSender(S_InventoryCount.clone(BasePacketPooling.getPool(S_InventoryCount.class), item));
				}else{
					o.toSender(S_InventoryStatus.clone(BasePacketPooling.getPool(S_InventoryStatus.class), item));
				}
				// \f1당신의 %0%s 손상되었습니다.
				o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 268, item.toString()));
			}
		}
	}
	
}
