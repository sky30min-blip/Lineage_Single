package lineage.world.object.item.yadolan;

import lineage.bean.database.Skill;
import lineage.database.SkillDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.magic.BraveAvatar;
import lineage.world.object.magic.BraveMental;
import lineage.world.object.magic.CounterBarrier;
import lineage.world.object.magic.GlowingWeapon;
import lineage.world.object.magic.Heal;
import lineage.world.object.magic.ImmuneToHarm;
import lineage.world.object.magic.ReductionArmor;
import lineage.world.object.magic.ShiningShield;
import lineage.world.object.magic.SolidCarriage;
import lineage.world.object.magic.StormShot;

public class LordBuff extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new LordBuff();
		return item;
	}

	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha != null && getItem() != null ) {
			
			if(cha.getClassType() != Lineage.LINEAGE_CLASS_ROYAL){
				ChattingController.toChatting(cha, "\\fY당신의 클래스는 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			Skill s15 = SkillDatabase.find(309);
			Skill s16 = SkillDatabase.find(307);
			Skill s17 = SkillDatabase.find(100);
			Skill s18 = SkillDatabase.find(101);
			
			if (s15 != null && SkillController.find(cha, s15.getUid()) != null && SkillController.isHpMpCheck(cha, s15.getHpConsume(), s15.getMpConsume()) ) {

	
					BraveMental.init2(cha, s15);
		
	
			}
			if (s16 != null && SkillController.find(cha, s16.getUid()) != null && SkillController.isHpMpCheck(cha, s16.getHpConsume(), s16.getMpConsume()) ) {

		
					BraveAvatar.init2(cha, s16);
		
			
			}
			if (s17 != null && SkillController.find(cha, s17.getUid()) != null && SkillController.isHpMpCheck(cha, s17.getHpConsume(), s17.getMpConsume())) {

		
					GlowingWeapon.init2(cha, s17);
				
			
			}
			if (s18 != null && SkillController.find(cha, s18.getUid()) != null  && SkillController.isHpMpCheck(cha, s18.getHpConsume(), s18.getMpConsume())) {

		
					ShiningShield.init2(cha, s18);
					
			
			}
		}
		}
	

}
