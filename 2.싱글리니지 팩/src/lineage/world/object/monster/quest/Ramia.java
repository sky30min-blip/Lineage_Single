package lineage.world.object.monster.quest;

import lineage.bean.database.Item;
import lineage.bean.database.Monster;
import lineage.bean.lineage.Quest;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.QuestController;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.RobotInstance;

public class Ramia extends MonsterInstance {

	private Item quest_item;
	
	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m){
		if(mi == null)
			mi = new Ramia();
		return MonsterInstance.clone(mi, m);
	}
	
	@Override
	protected void toAiDead(long time){
		if(getMap() == 22){
			// 라미아(보라)
			if(getGfx() == 1600){
				if(quest_item == null)
					quest_item = ItemDatabase.find("라미아의 비늘");
				if(getAttackListSize()==1){
					object o = getAttackList(0);
					if(o instanceof PcInstance && !(o instanceof RobotInstance)) {
						PcInstance pc = (PcInstance)o;
						Quest q = QuestController.find(pc, Lineage.QUEST_KNIGHT_LV30);
						if(q!=null && q.getQuestStep()==3 && pc.getInventory().findDbNameId(quest_item.getNameIdNumber())==null){
							ItemInstance ii = ItemDatabase.newInstance(quest_item);
							ii.setObjectId(ServerDatabase.nextItemObjId());
							pc.getInventory().append(ii, true);
							// \f1%0%s 당신에게 %1%o 주었습니다.
							pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 143, getName(), ii.getName()));
						}
					}
				}
			}
		}
		super.toAiDead(time);
	}

	@Override
	public void readDrop(int map){
		// 게라드 퀘스트 맵에 잇는 녀석들은 퀘스트 아이템 등록.
		if(getMap() != 22){
//			super.readDrop(map);
		}else{
			// 라미아(녹색)
			if(getGfx() == 1597){
				Item item = ItemDatabase.find("라미아 방 열쇠");
				if(item != null){
					if(Util.random(1,10000) <= 2000*Lineage.rate_drop){
						ItemInstance ii = ItemDatabase.newInstance(item);
						if(ii != null)
							inv.append(ii, true);
					}
				}
			}
		}
	}
	
}
