package kuberaitem;

import lineage.bean.database.Skill;
import lineage.database.SkillDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.CommandController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
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

public class monsterClean extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new monsterClean();
		return item;
	}

	public void toClick(Character cha, ClientBasePacket cbp) {
		
		
		PcInstance pc = (PcInstance) cha;
		
		CommandController.toClearMonster(pc);
	}
	

}
