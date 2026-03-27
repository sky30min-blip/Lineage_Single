package lineage.world.object.item.all_night;

import lineage.bean.lineage.Buff;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.magic.MaanBirth;
import lineage.world.object.magic.MaanBirthDelay;
import lineage.world.object.magic.MaanEarth;
import lineage.world.object.magic.MaanEarthDelay;
import lineage.world.object.magic.MaanFire;
import lineage.world.object.magic.MaanFireDelay;
import lineage.world.object.magic.MaanLife;
import lineage.world.object.magic.MaanLifeDelay;
import lineage.world.object.magic.MaanShape;
import lineage.world.object.magic.MaanShapeDelay;
import lineage.world.object.magic.MaanWatar;
import lineage.world.object.magic.MaanWatarDelay;
import lineage.world.object.magic.MaanWind;
import lineage.world.object.magic.MaanWindDelay;

public class BuffMaan extends ItemInstance {
	
	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new BuffMaan();
		return item;
	}
	
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha.getInventory() != null) {		
			if (getItem().getName().contains("수룡")) {
				if (checkBuff(cha)) {
					MaanWatar.init(cha, SkillDatabase.find(615));
					MaanWatarDelay.init(cha, SkillDatabase.find(622));
				}
			} else if (getItem().getName().contains("풍룡")) {
				if (checkBuff(cha)) {
					MaanWind.init(cha, SkillDatabase.find(616));
					MaanWindDelay.init(cha, SkillDatabase.find(623));
				}
			} else if (getItem().getName().contains("지룡")) {
				if (checkBuff(cha)) {
					MaanEarth.init(cha, SkillDatabase.find(617));
					MaanEarthDelay.init(cha, SkillDatabase.find(624));
				}
			} else if (getItem().getName().contains("화룡")) {
				if (checkBuff(cha)) {
					MaanFire.init(cha, SkillDatabase.find(618));
					MaanFireDelay.init(cha, SkillDatabase.find(625));
				}
			} else if (getItem().getName().contains("탄생")) {
				if (checkBuff(cha)) {
					MaanBirth.init(cha, SkillDatabase.find(619));
					MaanBirthDelay.init(cha, SkillDatabase.find(626));
				}
			} else if (getItem().getName().contains("형상")) {
				if (checkBuff(cha)) {
					MaanShape.init(cha, SkillDatabase.find(620));
					MaanShapeDelay.init(cha, SkillDatabase.find(627));
				}
			} else if (getItem().getName().contains("생명")) {
				if (checkBuff(cha)) {
					MaanLife.init(cha, SkillDatabase.find(621));
					MaanLifeDelay.init(cha, SkillDatabase.find(628));
				}
			}
			
//			if (getItem().getName().contains("수룡")) {
//				if (checkDelay(cha, 622) && checkBuff(cha)) {
//					MaanWatar.init(cha, SkillDatabase.find(615));
//					MaanWatarDelay.init(cha, SkillDatabase.find(622));
//				}
//			} else if (getItem().getName().contains("풍룡")) {
//				if (checkDelay(cha, 623) && checkBuff(cha)) {
//					MaanWind.init(cha, SkillDatabase.find(616));
//					MaanWindDelay.init(cha, SkillDatabase.find(623));
//				}
//			} else if (getItem().getName().contains("지룡")) {
//				if (checkDelay(cha, 624) && checkBuff(cha)) {
//					MaanEarth.init(cha, SkillDatabase.find(617));
//					MaanEarthDelay.init(cha, SkillDatabase.find(624));
//				}
//			} else if (getItem().getName().contains("화룡")) {
//				if (checkDelay(cha, 625) && checkBuff(cha)) {
//					MaanFire.init(cha, SkillDatabase.find(618));
//					MaanFireDelay.init(cha, SkillDatabase.find(625));
//				}
//			} else if (getItem().getName().contains("탄생")) {
//				if (checkDelay(cha, 626) && checkBuff(cha)) {
//					MaanBirth.init(cha, SkillDatabase.find(619));
//					MaanBirthDelay.init(cha, SkillDatabase.find(626));
//				}
//			} else if (getItem().getName().contains("형상")) {
//				if (checkDelay(cha, 627) && checkBuff(cha)) {
//					MaanShape.init(cha, SkillDatabase.find(620));
//					MaanShapeDelay.init(cha, SkillDatabase.find(627));
//				}
//			} else if (getItem().getName().contains("생명")) {
//				if (checkDelay(cha, 628) && checkBuff(cha)) {
//					MaanLife.init(cha, SkillDatabase.find(621));
//					MaanLifeDelay.init(cha, SkillDatabase.find(628));
//				}
//			}
		}
	}
	
	public boolean checkDelay(Character cha, int uid) {
		BuffInterface b = BuffController.find(cha, SkillDatabase.find(uid));
		if (b != null && b.getTime() > 0) {
			if (b.getTime() / 3600 > 0) {
				ChattingController.toChatting(cha, String.format("%s: %d시간 %d분 %d초 후 사용 가능합니다.", getItem().getName(), b.getTime() / 3600, b.getTime() % 3600 / 60, b.getTime() % 3600 % 60), Lineage.CHATTING_MODE_MESSAGE);
			} else if (b.getTime() % 3600 / 60 > 0) {
				ChattingController.toChatting(cha, String.format("%s: %d분 %d초 후 사용 가능합니다.", getItem().getName(), b.getTime() % 3600 / 60, b.getTime() % 3600 % 60), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				ChattingController.toChatting(cha, String.format("%s: %d초 후 사용 가능합니다.", getItem().getName(), b.getTime() % 3600 % 60), Lineage.CHATTING_MODE_MESSAGE);
			}
			return false;
		}
		return true;
	}
	
	public boolean checkBuff(Character cha) {
		Buff buff = BuffController.find(cha);
		
		if (buff != null) {
			for (BuffInterface b : buff.getList()) {
				if (b != null && b.getTime() > 0) {
					if (b.getSkill().getUid() >= 622 && b.getSkill().getUid() <= 628) {
						if (b.getTime() / 3600 > 0) {
							ChattingController.toChatting(cha, String.format("%s: %d시간 %d분 %d초가 남았습니다.", b.getSkill().getName(), b.getTime() / 3600, b.getTime() % 3600 / 60, b.getTime() % 3600 % 60), Lineage.CHATTING_MODE_MESSAGE);
						} else if (b.getTime() % 3600 / 60 > 0) {
							ChattingController.toChatting(cha, String.format("%s: %d분 %d초가 남았습니다.", b.getSkill().getName(), b.getTime() % 3600 / 60, b.getTime() % 3600 % 60), Lineage.CHATTING_MODE_MESSAGE);
						} else {
							ChattingController.toChatting(cha, String.format("%s: %d초가 남았습니다.", b.getSkill().getName(), b.getTime() % 3600 % 60), Lineage.CHATTING_MODE_MESSAGE);
						}
						return false;
					}					
				}
			}
		}	
		return true;
	}
}
