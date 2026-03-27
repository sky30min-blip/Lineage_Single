package lineage.world.object.item.scroll;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_ObjectLock;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.LocationController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class ScrollReturnSilentCavern extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new ScrollReturnSilentCavern();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if(cha.getLevel() < 13){
			cha.getInventory().count(this, getCount()-1, true);
			
			if(LocationController.isTeleportVerrYedHoraeZone(cha, true)){
				LocationController.toSilenceCave(cha);
				cha.toPotal(cha.getHomeX(), cha.getHomeY(), cha.getHomeMap());
			}
		}else{
			ChattingController.toChatting(cha, "이 아이템은 12레벨 이하일 때만 사용할 수 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
			cha.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x09));
		}
	}

}
