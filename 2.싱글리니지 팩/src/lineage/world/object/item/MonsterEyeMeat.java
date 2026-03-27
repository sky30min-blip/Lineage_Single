package lineage.world.object.item;

import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.magic.FloatingEyeMeat;

public class MonsterEyeMeat extends Meat {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new MonsterEyeMeat();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		updateFood(cha);
		// \f1이상하게도 감각이 예민해진것 같습니다.
		cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 152));
		// 버프 처리
		FloatingEyeMeat.init(cha, SkillDatabase.find(205));
		// 아이템 수량 갱신
		cha.getInventory().count(this, getCount()-1, true);
	}
}
