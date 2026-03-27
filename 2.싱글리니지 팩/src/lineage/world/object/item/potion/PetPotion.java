package lineage.world.object.item.potion;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.PetInstance;
import lineage.world.object.magic.AbsoluteBarrier;

public class PetPotion extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new PetPotion();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		
		if(cha instanceof PcInstance){
			ChattingController.toChatting(cha, "펫 전용 물약 입니다", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		
		if (!isClick(cha))
			return;
		
		if (cha.isBuffDecayPotion()){
			ChattingController.toChatting(cha, "디케이포션:물약사용불가", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		// 앱솔상태 해제.
		if (cha.isBuffAbsoluteBarrier())
			BuffController.remove(cha, AbsoluteBarrier.class);
		
	
		
		// 패킷처리
		cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, getItem().getEffect()), true);
		
		// 물약 회복량
		int hp = Util.random(getItem().getSmallDmg(), getItem().getBigDmg());

		// 스탯에 따른 hp회복량 증가
		hp += (int) Math.round(hp * (cha.getTotalHpPotion() * 0.01));
		
		// 체력 상승
		cha.setNowHp((int) (cha.getNowHp() + (cha.isBuffPolluteWater() ? (Math.round(hp * 0.5)) : hp)));
		
		if (getItem() != null && !getItem().getType2().equalsIgnoreCase("무한 물약"))
			// 아이템 수량 갱신
			cha.getInventory().count(this, getCount() - 1, true);
	}

}
