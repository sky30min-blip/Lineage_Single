package lineage.world.object.item.all_night;

import lineage.bean.database.Exp;
import lineage.database.ExpDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.World;
import lineage.world.controller.CharacterController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.RankController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class Exp_support extends ItemInstance {
	
	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new Exp_support();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
//		if (cha.getInventory() != null && !cha.isWorldDelete() && !cha.isLock() && !cha.isDead()) {
//			int maxLevel = RankController.rank_top_level - Lineage.exp_support_level_gap;
//			
//			if (Lineage.is_exp_support && cha.getLevel() < maxLevel && cha.getLevel() < Lineage.exp_support_max_level) {
//				if (cha.getResetBaseStat() <= 0 && cha.getResetLevelStat() <= 0 && cha.getLevelUpStat() <= 0) {
//					Exp e = ExpDatabase.find(cha.getLevel());
//					cha.setExp(e.getBonus() + 0.01);
//				} else {
//					ChattingController.toChatting(cha, "스탯 능력치를 올리신 후 사용가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
//				}
//			} else {
//				if (Lineage.is_exp_support) {
//					if (cha.getLevel() >= Lineage.exp_support_max_level)
//						ChattingController.toChatting(cha, String.format("레벨업 지원은 %d레벨까지 사용 가능합니다.", Lineage.exp_support_max_level - 1), Lineage.CHATTING_MODE_MESSAGE);
//					else if (maxLevel > 0 && cha.getLevel() >= maxLevel)
//						ChattingController.toChatting(cha, String.format("레벨업 지원은 %d레벨까지 사용 가능합니다.", maxLevel - 1), Lineage.CHATTING_MODE_MESSAGE);
//				} else {
//					ChattingController.toChatting(cha, "레벨업 지원은 현재 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
//				}
//			}
//		}
		

		if (cha.getInventory() != null && !cha.isWorldDelete() && !cha.isLock() && !cha.isDead()) {
			int maxLevel = Lineage.exp_support_level_gap == 99 ? Lineage.exp_support_max_level : RankController.rank_top_level - Lineage.exp_support_level_gap;
			
			if (Lineage.open_wait) {
				ChattingController.toChatting(cha, "[오픈대기] 오픈대기에는 레벨업 하실수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}			
			
			if (Lineage.is_exp_support) {
				if (cha.getLevel() < maxLevel) {
					if (cha.getResetBaseStat() <= 0 && cha.getResetLevelStat() <= 0 ) {
	

						 int level = Lineage.exp_support_max_level;
						 double tempExp = 0;
						 double exp = 0;
						
						 
						 if (tempExp >= 99)
							 tempExp = 99;
						 
						 tempExp *= 0.01;
						 
						Exp e = ExpDatabase.find(level - 1);
						exp = e.getBonus();
						tempExp *= ExpDatabase.find(level).getExp();

						if (cha.getExp() > exp + tempExp) {
							if (e.getLevel() < cha.getLevel()) {							
								for (int i = cha.getLevel(); i > e.getLevel(); i--) {
									// hp & mp 하향.
									cha.setMaxHp( cha.getMaxHp()-CharacterController.toStatusUP(cha, true) );
									cha.setMaxMp( cha.getMaxMp()-CharacterController.toStatusUP(cha, false) );
								}
				
								cha.setLevel(e.getLevel());
								cha.setExp(exp + tempExp);
							} else {
								cha.setExp(exp + tempExp);
							}
						} else {
							cha.setExp(exp + tempExp);
						}
					} else {
						ChattingController.toChatting(cha, "\\fY스탯 능력치를 올리신 후 사용가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
					}
				} else {
					if (Lineage.exp_support_level_gap == 99 && cha.getLevel() >= Lineage.exp_support_max_level) {
						ChattingController.toChatting(cha, String.format("\\fY레벨업 지원은 %d레벨까지 사용 가능합니다.", Lineage.exp_support_max_level ), Lineage.CHATTING_MODE_MESSAGE);
					} else if (maxLevel > 0 && cha.getLevel() >= maxLevel) {
						ChattingController.toChatting(cha, String.format("\\fY레벨업 지원은 %d레벨까지 사용 가능합니다.", maxLevel ), Lineage.CHATTING_MODE_MESSAGE);
					}
				}
			} else {
				ChattingController.toChatting(cha, "\\fY레벨업 지원은 현재 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	
	}

}
