package lineage.world.object.item.all_night;

import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.Character;
import lineage.world.object.instance.PcInstance;

public class AutoAttackItem extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new AutoAttackItem();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if(cha.getInventory() != null){
			PcInstance pc = (PcInstance)cha;
			
			if (pc.isAutoHunt) {
				ChattingController.toChatting(pc, "자동사냥중엔 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			if (pc.isDead()) {
				ChattingController.toChatting(pc, "죽은 상태에선 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			} else if (pc.isLock()) {
				ChattingController.toChatting(pc, "기절 상태에선 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			if (pc.isAutoAttack) {
				pc.isAutoAttack = false;
				pc.resetAutoAttack();
			} else {
				pc.isAutoAttack = true;
			}
			
			ChattingController.toChatting(pc, String.format("[자동칼질: %s]", pc.isAutoAttack ? "활성화" : "비활성화"), Lineage.CHATTING_MODE_MESSAGE);
		}
	}
}
