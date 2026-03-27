package lineage.world.object.item.all_night;

import lineage.bean.database.Exp;
import lineage.bean.database.Item;
import lineage.database.ExpDatabase;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class ExpSaveMarble extends ItemInstance {
	
	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new ExpSaveMarble();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (isClickState(cha)) {
			if (Lineage.exp_marble_save_count < 1) {
				ChattingController.toChatting(cha, "현재 아이템 사용이 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}

			if (Lineage.exp_marble_save_count <= cha.getExp_marble_save_count()) {
				ChattingController.toChatting(cha, String.format("\\fR[경험치 저장 횟수: \\fY%d회\\fR] 1일 %d번 저장 가능합니다.", cha.getExp_marble_save_count(), Lineage.exp_marble_save_count), Lineage.CHATTING_MODE_MESSAGE);
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
						double cha_exp = cha.getExp() - e.getBonus();
						double exp = e1.getExp() * Lineage.exp_marble_percent;

						if (cha_exp < exp) {
							ChattingController.toChatting(cha, String.format("\\fR경험치가 %.0f%% 이상 있어야 가능합니다.", Lineage.exp_marble_percent * 100), Lineage.CHATTING_MODE_MESSAGE);
							return;
						}
						
						Item i = ItemDatabase.find(String.format("경험치 구슬 [%s]", temp));

						if (i != null) {
							cha.setExp_marble_save_count(cha.getExp_marble_save_count() + 1);
							cha.getInventory().count(this, getCount() - 1, true);
							cha.setExp(cha.getExp() - exp);

							int bless = 1;
							int en = 0;
							long count = 1;
							ItemInstance item = cha.getInventory().find(i.getName(), bless, i.isPiles());

							if (item != null && (item.getBless() != bless || item.getEnLevel() != en))
								item = null;

							if (item == null) {
								// 겹칠수 있는 아이템이 존재하지 않을경우.
								if (i.isPiles()) {
									item = ItemDatabase.newInstance(i);
									item.setObjectId(ServerDatabase.nextItemObjId());
									item.setBless(bless);
									item.setEnLevel(en);
									item.setCount(count);
									item.setDefinite(true);
									cha.getInventory().append(item, true);
								} else {
									for (int idx = 0; idx < count; idx++) {
										item = ItemDatabase.newInstance(i);
										item.setObjectId(ServerDatabase.nextItemObjId());
										item.setBless(bless);
										item.setEnLevel(en);
										item.setDefinite(true);
										cha.getInventory().append(item, true);
									}
								}
							} else {
								// 겹치는 아이템이 존재할 경우.
								cha.getInventory().count(item, item.getCount() + count, true);
							}

							ChattingController.toChatting(cha, String.format("\\fR[경험치 저장 횟수: \\fY%d회\\fR] %s 획득", cha.getExp_marble_save_count(), i.getName()), Lineage.CHATTING_MODE_MESSAGE);
						}
					}
				} else {
					ChattingController.toChatting(cha, String.format("\\fR%d레벨 이상 %d레벨 이하일 경우 사용 가능합니다.", minLevel, maxLevel), Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
			} catch (Exception ex) {
				lineage.share.System.printf("%s : toClick(Character cha, ClientBasePacket cbp)\r\n", ExpSaveMarble.class.toString());
				lineage.share.System.printf("캐릭터: %s\r\n", cha.getName());
				lineage.share.System.println(ex);
			}
		}
	}
}
