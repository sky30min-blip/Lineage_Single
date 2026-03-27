package lineage.world.object.item.all_night;

import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.magic.ExpDropBuff_10;
import lineage.world.object.magic.ExpDropBuff_20;
import lineage.world.object.magic.ExpDropBuff_50;

public class Exp_drop_potion extends ItemInstance {
	
	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new Exp_drop_potion();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha.getInventory() != null && getItem() != null) {
			
			if (getItem().getType2().equalsIgnoreCase("드래곤의 사파이어") || getItem().getType2().equalsIgnoreCase("드래곤의 사파이어")) {
				if (checkBuff(cha, 701))
					ExpDropBuff_10.init(cha, SkillDatabase.find(701));
				else
					return;
			} else if (getItem().getType2().equalsIgnoreCase("드래곤의 루비") || getItem().getType2().equalsIgnoreCase("무한 드래곤의 루비")) {
				if (checkBuff(cha, 702))
					ExpDropBuff_20.init(cha, SkillDatabase.find(702));
				else
					return;
			} else if (getItem().getType2().equalsIgnoreCase("드래곤의 다이아몬드") || getItem().getType2().equalsIgnoreCase("무한 드래곤의 다이아몬드")) {
				if (checkBuff(cha, 703))
					ExpDropBuff_50.init(cha, SkillDatabase.find(703));
				else
					return;
			}
			
			if (!getItem().getType2().contains("무한"))
				cha.getInventory().count(this, getCount()-1, true);
		}
	}
	
	public boolean checkBuff(Character cha, int uid) {
		BuffInterface b = BuffController.find(cha, SkillDatabase.find(uid));
		if (b != null && b.getTime() > 0) {
			if (b.getTime() / 3600 > 0) {
				ChattingController.toChatting(cha, String.format("%s: %d시간 %d분 %d초 후 사용 가능합니다.", b.getSkill().getName(), b.getTime() / 3600, b.getTime() % 3600 / 60, b.getTime() % 3600 % 60), Lineage.CHATTING_MODE_MESSAGE);
			} else if (b.getTime() % 3600 / 60 > 0) {
				ChattingController.toChatting(cha, String.format("%s: %d분 %d초 후 사용 가능합니다.", b.getSkill().getName(), b.getTime() % 3600 / 60, b.getTime() % 3600 % 60), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				ChattingController.toChatting(cha, String.format("%s: %d초 후 사용 가능합니다.", b.getSkill().getName(), b.getTime() % 3600 % 60), Lineage.CHATTING_MODE_MESSAGE);
			}
			return false;
		}
		return true;
	}
}
