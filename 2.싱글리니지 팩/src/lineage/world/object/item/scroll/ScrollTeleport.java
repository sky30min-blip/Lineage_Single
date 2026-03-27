package lineage.world.object.item.scroll;

import lineage.bean.database.ItemTeleport;
import lineage.bean.lineage.Kingdom;
import lineage.database.ItemTeleportDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.KingdomController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class ScrollTeleport extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new ScrollTeleport();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (isLvCheck(cha)) {
			try {
			    // 초기화
			    String type2 = getItem().getType2();
			    int underscoreIndex = type2.indexOf("_");
			    
			    if (underscoreIndex != -1) {
			        int uid = Integer.valueOf(type2.substring(underscoreIndex + 1));
			        ItemTeleport it = ItemTeleportDatabase.find(uid);
			        Kingdom k = KingdomController.find(4);

			        if (it != null) {
			            // 텔레포트
			            // if_remove = 1 경우에 인벤토리에서 제거
			            if (uid == 28 && k != null && k.getClanId() > 0) {
			                if (k.getClanId() == cha.getClanId()) {
			                    ItemTeleportDatabase.toTeleport(it, cha, true);
			                    cha.getInventory().count(this, getCount() - 1, true);
			                } else {
			                    ChattingController.toChatting(cha, "성혈맹이 아닐 경우 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			                }
			            } else {
			                if (ItemTeleportDatabase.toTeleport(it, cha, true) && it.isRemove()) {
			                    cha.getInventory().count(this, getCount() - 1, true);
			                }
			            }
			        }
			    } else {
			    
			    }
			} catch (Exception e) {
			
			}
	}
	}
}
