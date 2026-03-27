package lineage.world.object.item.all_night;

import lineage.bean.database.Exp;
import lineage.database.ExpDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class ExpMarble extends ItemInstance {
	
	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new ExpMarble();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (isClickState(cha)) {
			if (Lineage.exp_marble_use_count < 1) {
				ChattingController.toChatting(cha, "현재 아이템 사용이 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}

			if (Lineage.exp_marble_use_count <= cha.getExp_marble_use_count()) {
				ChattingController.toChatting(cha, String.format("\\fR[경험치 사용 횟수: \\fY%d회\\fR] 1일 %d번 사용 가능합니다.", cha.getExp_marble_use_count(), Lineage.exp_marble_use_count), Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(cha, String.format("\\fR사용 횟수는 %s에 초기화 됩니다.", Lineage.exp_marble_time), Lineage.CHATTING_MODE_MESSAGE);
				return;
			}

			try {
				String name = getItem().getName();
				String temp = name.substring(name.indexOf("[") + 1, name.indexOf("]")).trim();
				int minLevel = Integer.valueOf(temp.substring(0, temp.indexOf("~")).trim());
				int maxLevel = Integer.valueOf(temp.substring(temp.indexOf("~") + 1).trim());

				if (minLevel <= cha.getLevel() && cha.getLevel() <= maxLevel) {
					Exp e = ExpDatabase.find(cha.getLevel() - 1);
					Exp e1 = ExpDatabase.find(cha.getLevel());
					if (e != null && e1 != null) {
						double gap = cha.getExp() - e.getBonus();
						double exp = e1.getExp() * Lineage.exp_marble_percent;

						if (e1.getBonus() <= cha.getExp() + 10) {
							ChattingController.toChatting(cha, "\\fR더이상 경험치를 획득할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
							return;
						}

						if (e1.getBonus() <= cha.getExp() + exp) {
							exp = e1.getExp() - gap - 10;
						}

						cha.setExp_marble_use_count(cha.getExp_marble_use_count() + 1);
						cha.getInventory().count(this, getCount() - 1, true);
						cha.setExp(cha.getExp() + exp);
						ChattingController.toChatting(cha, String.format("\\fR[경험치 사용 횟수: \\fY%d회\\fR]", cha.getExp_marble_use_count()), Lineage.CHATTING_MODE_MESSAGE);
					}
				} else {
					ChattingController.toChatting(cha, String.format("\\fR%d레벨 이상 %d레벨 이하일 경우 사용 가능합니다.", minLevel, maxLevel), Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
			} catch (Exception ex) {
				lineage.share.System.printf("%s : toClick(Character cha, ClientBasePacket cbp)\r\n", ExpMarble.class.toString());
				lineage.share.System.printf("캐릭터: %s\r\n", cha.getName());
				lineage.share.System.println(ex);
			}
		}
	}
}
