package lineage.world.object.item.potion;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.magic.HastePotionMagic;

public class HastePotion extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new HastePotion();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if( !isClick(cha) )
			return;
		
		if(cha.getMap() == 5143){
			ChattingController.toChatting(cha, "[알림] 인형경주중엔 사용 하실 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		
		// 이팩트 표현
		cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, getItem().getEffect()), true);
		
		// 버프 처리
		HastePotionMagic.init(cha, getItem().getDuration(), false);
		// 아이템 수량 갱신
		cha.getInventory().count(this, getCount()-1, true);
	}
}
