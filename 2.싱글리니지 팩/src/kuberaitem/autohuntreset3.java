package kuberaitem;

import lineage.database.AccountDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class autohuntreset3 extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new autohuntreset3();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		PcInstance pc = (PcInstance)cha;
		
		if (cha.getInventory() != null) {
			
	
			if (Lineage.auto_scroll_count > 0 && pc.getAuto_count() >= Lineage.auto_scroll_count) {
				ChattingController.toChatting(cha, String.format("초기화는 하루 %d번 사용가능합니다.", Lineage.auto_scroll_count), Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			pc.auto_hunt_account_time = pc.auto_hunt_account_time+86400;
			pc.setAuto_count( pc.getAuto_count()+1);
			AccountDatabase.updateauto(1,pc.getAccountUid());

			
			ChattingController.toChatting(cha, "자동사냥 계정자동사냥 계정 시간 초기화 시간이 초기화 되었습니다..", Lineage.CHATTING_MODE_MESSAGE);
			cha.getInventory().count(this, getCount()-1, true);
		}
	}
}
