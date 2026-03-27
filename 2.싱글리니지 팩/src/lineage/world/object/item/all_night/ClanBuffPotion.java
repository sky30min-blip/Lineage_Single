package lineage.world.object.item.all_night;

import lineage.database.SkillDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.BraveAvatar;
import lineage.world.object.magic.GlowingWeapon;
import lineage.world.object.magic.ShiningShield;

public class ClanBuffPotion extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new ClanBuffPotion();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha != null && cha instanceof PcInstance) {
			if (cha.getClanId() > 0) {
				PcInstance pc = (PcInstance) cha;

				GlowingWeapon.onBuff(pc, SkillDatabase.find(100));
				ShiningShield.onBuff(pc, SkillDatabase.find(101));
				BraveAvatar.onBuff(pc, SkillDatabase.find(307));
				// 아이템 수량 갱신
				pc.getInventory().count(this, getCount() - 1, true);
			} else {
				ChattingController.toChatting(cha, "혈맹 가입 후 사용 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}
}
