package kuberaitem;

import lineage.bean.database.Skill;
import lineage.database.SkillDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.magic.BurningSpirit;
import lineage.world.object.magic.DoubleBreak;
import lineage.world.object.magic.DressDexterity;
import lineage.world.object.magic.DressEvasion;
import lineage.world.object.magic.DressMighty;
import lineage.world.object.magic.EnchantVenom;
import lineage.world.object.magic.Heal;
import lineage.world.object.magic.ImmuneToHarm;
import lineage.world.object.magic.InvisiBility;
import lineage.world.object.magic.ShadowArmor;
import lineage.world.object.magic.ShadowFang;
import lineage.world.object.magic.UncannyDodge;
import lineage.world.object.magic.movingacceleratic;

public class SelfSpell extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new SelfSpell();
		return item;
	}

	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha != null && getItem() != null && getItem().getSmallDmg() > 0) {
			Skill skill = SkillDatabase.find(getItem().getSmallDmg());	
			
			if(cha.getClassType() !=  Lineage.LINEAGE_CLASS_DARKELF ){
				ChattingController.toChatting(cha, "다크엘프만 사용 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			if (this.isInvis()){
				this.setInvis(false);
				this.setBuffInvisiBility(false);
				BuffController.remove(this, InvisiBility.class);
				
			}
			if (skill != null) {
					if (SkillController.isDelay(cha, skill)) {
						int uid = skill.getUid();
						
						if (uid == 634) {
							BurningSpirit.init(cha, skill );
						}
						if (uid == 635) {
							InvisiBility.init2(cha, skill );
						}
						if (uid == 656) {
							if(cha.getInventory().isAden("흑요석", 1, true)){
								EnchantVenom.init(cha, skill );
							}else{
								ChattingController.toChatting(cha, "마법재료가 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
							}
			
						}
						if (uid == 654) {
							ShadowArmor.init(cha, skill );
						}
						if (uid == 638) {
							movingacceleratic.init(cha, skill );
						}
						if (uid == 640) {
							DoubleBreak.init(cha, skill );
						}
						if (uid == 641) {
							
							if(cha.getInventory().isAden("흑요석", 1, true)){
								UncannyDodge.init(cha, skill );
							}else{
								ChattingController.toChatting(cha, "마법재료가 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
							}
				
						}
						if (uid == 642) {
							
							if(cha.getInventory().isAden("흑요석", 1, true)){
								ShadowFang.init(cha, skill);
							}else{
								ChattingController.toChatting(cha, "마법재료가 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
							}
					
						}
						if (uid == 643) {
							
							DressMighty.init(cha, skill );
					
						}
						if (uid == 644) {
							
							DressDexterity.init(cha, skill );
				
					}
						if (uid == 645) {
							
							DressEvasion.init(cha, skill );
				
					}
					}

			}
		}
	}
	

}
