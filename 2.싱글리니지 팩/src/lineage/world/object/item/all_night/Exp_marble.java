package lineage.world.object.item.all_night;

import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.npc.background.FishExp;

public class Exp_marble extends ItemInstance {
	
	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new Exp_marble();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha.getInventory() != null && !cha.isWorldDelete() && !cha.isLock() && !cha.isDead()) {
			if (cha.getResetBaseStat() <= 0 && cha.getResetLevelStat() <= 0) {
				
				if (getItem() != null && getItem().getName().equalsIgnoreCase("경험치 지급단")){
					
					FishExp e = new FishExp();
					double exp = Util.random(Lineage.exp_marble_min, Lineage.exp_marble_max);
					
					cha.toExp(e, exp);
					
					cha.getInventory().count(this, getCount() - 1, true);
				}
		
				if (getItem() != null && getItem().getName().equalsIgnoreCase("중급 경험치 지급단")){
					
					FishExp e = new FishExp();
					double exp = Util.random(Lineage.exp_marble_min*100, Lineage.exp_marble_max*100);
					
					cha.toExp(e, exp);
					
					cha.getInventory().count(this, getCount() - 1, true);
				}
				if (getItem() != null && getItem().getName().equalsIgnoreCase("상급 경험치 지급단")){
					
					FishExp e = new FishExp();
					double exp = Util.random(Lineage.exp_marble_min*500, Lineage.exp_marble_max*500);
					
					cha.toExp(e, exp);
					
					cha.getInventory().count(this, getCount() - 1, true);
				}
				
				
			} else {
				ChattingController.toChatting(cha, "스탯 능력치를 올리신 후 사용가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}

}
