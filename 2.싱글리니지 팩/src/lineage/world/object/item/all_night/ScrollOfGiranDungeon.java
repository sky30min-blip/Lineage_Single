package lineage.world.object.item.all_night;

import lineage.database.AccountDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class ScrollOfGiranDungeon extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new ScrollOfGiranDungeon();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha.getInventory() != null) {
			PcInstance pc = (PcInstance) cha;	
			if (Lineage.giran_dungeon_scroll_count > 0 && pc.getGiran_dungeon_count() >= Lineage.giran_dungeon_scroll_count) {
				ChattingController.toChatting(cha, String.format("초기화 주문서는 하루 %d번 사용가능합니다.", Lineage.giran_dungeon_scroll_count), Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			pc.setGiran_dungeon_time(Lineage.giran_dungeon_time);
			pc.setGiran_dungeon_count( pc.getGiran_dungeon_count()+1);
			AccountDatabase.updateGiran(1,pc.getAccountUid());
			// 알림
			ChattingController.toChatting(cha, "기란감옥 이용시간이 초기화 되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
			// 아이템 수량 갱신
			cha.getInventory().count(this, getCount() - 1, true);
		}
	}
}
