package lineage.world.object.item.all_night;

import lineage.database.SkillDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.ImmuneToHarm;

public class SelfImmuneToHarm extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new SelfImmuneToHarm();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha instanceof PcInstance) {
			if (cha.getGm() > 0 || SkillController.find(cha, 68, false) != null) {
				if (SkillController.isDelay(cha, SkillDatabase.find(68)))
					ImmuneToHarm.init(cha, SkillDatabase.find(68), (int) cha.getObjectId());
			} else {
				ChattingController.toChatting(cha, "이뮨 투 함을 배워야 사용 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}
}
