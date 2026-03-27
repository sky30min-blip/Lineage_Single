package lineage.world.object.item.quest;

import lineage.network.packet.ClientBasePacket;
import lineage.util.Util;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class RedKey extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new RedKey();
		return item;
	}
	
	public RedKey(){
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		// 파고가 있는 수련동굴로 이동.
		switch(Util.random(0, 3)) {
			case 0:
				cha.toPotal(32731, 32807, 87);
				break;
			case 1:
				cha.toPotal(32731, 32803, 87);
				break;
			case 2:
				cha.toPotal(32727, 32803, 87);
				break;
			case 3:
				cha.toPotal(32727, 32807, 87);
				break;
		}
	}

}
