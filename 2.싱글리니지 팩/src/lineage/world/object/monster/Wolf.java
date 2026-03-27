package lineage.world.object.monster;

import lineage.bean.database.Monster;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.world.controller.SummonController;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;

public class Wolf extends MonsterInstance {
	
	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m){
		if(mi == null)
			mi = new Wolf();
		return MonsterInstance.clone(mi, m);
	}
	
	@Override
	public void toGiveItem(object o, ItemInstance item, long count){
		// 고기를 1개씩만 줬을경우 펫길들이기로 판단.
		if(count==1 && item.getItem().getNameIdNumber()==23 && Lineage.pet_tame_is){
			// 확률
			if( SummonController.isTame(this, true) ){
				// 하향
				o.getInventory().count(item, item.getCount()-count, true);
				// 길들이기
				if(SummonController.toPet(o, this));
					return;
			}
			// 길들이기가 실패했습니다.
			o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 324));
		}
		
		super.toGiveItem(o, item, count);
	}
	
}
