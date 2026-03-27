package lineage.world.object.item;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class Firework extends ItemInstance {
	
	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new Firework();
		return item;
	}
	
	public Firework(){
		setQuantity(1);
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		// 모션 보이기.
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, 17), item.getAction1()==0);
		// 이팩트 자신과 주변사용자에게 보이기.
		cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, item.getEffect()), true);
		// 아이템 수량 갱신
		cha.getInventory().count(this, getCount()-1, true);
	}

}
