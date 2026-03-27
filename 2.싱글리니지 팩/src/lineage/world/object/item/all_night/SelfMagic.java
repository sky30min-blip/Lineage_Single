package lineage.world.object.item.all_night;

import lineage.bean.database.Skill;
import lineage.database.SkillDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.magic.AquaProtect;
import lineage.world.object.magic.Berserks;
import lineage.world.object.magic.Heal;
import lineage.world.object.magic.ImmuneToHarm;
import lineage.world.object.magic.NaturesTouch;
import lineage.world.object.magic.PolluteWater;
import lineage.world.object.magic.StormShot;
import lineage.world.object.magic.WaterLife;

public class SelfMagic extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new SelfMagic();
		return item;
	}

	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha != null && getItem() != null && getItem().getSmallDmg() > 0) {
			Skill skill = SkillDatabase.find(getItem().getSmallDmg());			
			
			if (skill != null) {
				if (SkillController.find(cha, skill.getUid()) != null) {
					if (SkillController.isDelay(cha, skill)) {
						int uid = skill.getUid();
						
						if (isHeal(uid)) {
							Heal.init(cha, skill, cha.getObjectId());
						} else if (uid == 68) {
							// 이뮨 투 함
							ImmuneToHarm.init(cha, skill, (int) cha.getObjectId());
						}else if (uid == 135) {
							// 스톰샷
							StormShot.init(cha, skill, (int) cha.getObjectId());
						}else if (uid == 128) {
			
							NaturesTouch.init(cha, skill, (int) cha.getObjectId());
						}else if (uid == 118) {
			
							AquaProtect.init(cha, skill, (int) cha.getObjectId());
						}else if (uid == 132) {
			
							WaterLife.init2(cha, skill, (int) cha.getObjectId());
						}else if (uid == 23) {
			
							Berserks.init(cha, skill, (int) cha.getObjectId());
						}
					}
				} else {
					ChattingController.toChatting(cha, String.format("'%s' 을(를) 배우지 않았습니다.", skill.getName()), Lineage.CHATTING_MODE_MESSAGE);
				}
			}
		}
	}
	
	public boolean isHeal(int uid) {
		switch (uid) {
		case 1: // 힐
		case 19: // 익스트라 힐
		case 35: // 그레이터 힐
		case 57: // 풀 힐
			return true;
		}
		return false;
	}
}
