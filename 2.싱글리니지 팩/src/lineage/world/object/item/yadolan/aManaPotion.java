package lineage.world.object.item.yadolan;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.util.Util;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class aManaPotion extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new aManaPotion();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if( !isClick(cha) )
			return;

		// 이팩트 표현
		cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, getItem().getEffect()), true);
		// 메세지 표현
		cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 818));
		// 마나 상승
		cha.setNowMp( cha.getNowMp()+Util.random(getItem().getSmallDmg(), getItem().getBigDmg()) );
		// 아이템 수량 갱신
		cha.getInventory().count(this, getCount()-1, true);
	}
	
}
