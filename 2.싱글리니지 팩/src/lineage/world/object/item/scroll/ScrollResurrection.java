package lineage.world.object.item.scroll;

import lineage.bean.lineage.Kingdom;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.KingdomController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;

public class ScrollResurrection extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new ScrollResurrection();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		object o = cha.findInsideList(cbp.readD());
		
		if(o != null && o.getGm() == 0) {
			Kingdom k = KingdomController.findKingdomLocation(o);
			
			if (k != null && k.isWar()) {
				ChattingController.toChatting(cha, "공성 중에 깃발 내에서 부활이 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			o.toRevival(cha);
		
			// 아이템 수량 갱신
			cha.getInventory().count(this, getCount()-1, true);
		}	
	}
}
