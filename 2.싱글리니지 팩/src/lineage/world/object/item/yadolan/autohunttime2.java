package lineage.world.object.item.yadolan;

import lineage.database.AccountDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class autohunttime2 extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new autohunttime2();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		PcInstance pc = (PcInstance)cha;
		
		if (cha.getInventory() != null) {
			
	
			
			pc.auto_hunt_account_time =  pc.auto_hunt_account_time + 3600;

			
			ChattingController.toChatting(cha, "자동사냥  시간이 충전 되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
			cha.getInventory().count(this, getCount()-1, true);
		}
	}
}
