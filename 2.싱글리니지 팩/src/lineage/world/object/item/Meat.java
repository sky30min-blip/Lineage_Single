package lineage.world.object.item;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class Meat extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new Meat();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		updateFood(cha);
		// \f1%0%o 먹었습니다.
		cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 76, toString()));
		// 고기수량 갱신
		cha.getInventory().count(this, getCount()-1, true);
	}
	
	/**
	 * 고기먹을시 food값 처리해주는 함수.
	 * @param cha
	 */
	protected void updateFood(Character cha){
		/*if(cha.getFood() == 7 || cha.getFood() == 12 || cha.getFood() == 16 || cha.getFood() == 21 || cha.getFood() == 25){
			cha.setFood(cha.getFood() + 2);
		}else{
			cha.setFood(cha.getFood() + 1);
		}*/
		cha.setFood(cha.getFood() + (getItem().getNameIdNumber() == 72 ? 80 : getItem().getNameIdNumber() == 23 || getItem().getNameIdNumber() == 623 ? 200 : 1));
	}
}
