package lineage.world.object.item.all_night;

import lineage.database.SkillDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.AdvanceSpirit;
import lineage.world.object.magic.BlessWeapon;
import lineage.world.object.magic.BlessedArmor;
import lineage.world.object.magic.DecreaseWeight;
import lineage.world.object.magic.EarthSkin;
import lineage.world.object.magic.EnchantDexterity;
import lineage.world.object.magic.EnchantMighty;
import lineage.world.object.magic.Haste;

public class Buff_potion extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new Buff_potion();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if(cha instanceof PcInstance){
			
			if(cha.getMap() == 807){
				ChattingController.toChatting(cha, "여기서는 사용 하실 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			if(cha.getMap() == 5143){
				ChattingController.toChatting(cha, "[알림] 인형경주중엔 사용 하실 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			PcInstance pc = (PcInstance)cha;
			
			Haste.onBuff(pc, SkillDatabase.find(6, 2), 1800);
			DecreaseWeight.onBuff(pc, SkillDatabase.find(2, 5));
			
			if (pc.getInventory() != null && pc.getInventory().getSlot(Lineage.SLOT_ARMOR) != null)
				BlessedArmor.onBuff(pc, pc.getInventory().getSlot(Lineage.SLOT_ARMOR), SkillDatabase.find(3, 4), SkillDatabase.find(3, 4).getBuffDuration());
			
			EnchantDexterity.onBuff(pc, SkillDatabase.find(4, 1));
			EnchantMighty.onBuff(pc, SkillDatabase.find(6, 1));
			
			if (pc.getInventory() != null && pc.getInventory().getSlot(Lineage.SLOT_WEAPON) != null)
				BlessWeapon.onBuff(pc.getInventory().getSlot(Lineage.SLOT_WEAPON), SkillDatabase.find(6, 7));
			
	

/*			EnchantDexterity.onBuff(pc, SkillDatabase.find(4, 1));
			EnchantMighty.onBuff(pc, SkillDatabase.find(6, 1));
			AdvanceSpirit.onBuff(pc, SkillDatabase.find(9, 2));
			BlessWeapon.init(cha, SkillDatabase.find(6, 7), cha.getObjectId(), false, false);
			GlowingWeapon.onBuff(pc, SkillDatabase.find(15, 1));
			ShiningShield.onBuff(pc, SkillDatabase.find(15, 2));*/

			// 아이템 수량 갱신
			if (!getItem().getNameId().contains("무한 버프 물약"))
				cha.getInventory().count(this, getCount()-1, true);
		
			}
	}

}
