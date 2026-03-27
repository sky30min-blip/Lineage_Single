package kuberaitem;

import lineage.bean.database.Skill;

import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectGfx;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.EnergyBolt;

public class ItemFinal extends ItemInstance {

	private Skill skill;

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new ItemFinal();
		item.setSkill(SkillDatabase.find(648));
		return item;
	}

	@Override
	public Skill getSkill() {
		return skill;
	}

	@Override
	public void setSkill(Skill skill) {
		this.skill = skill;
	}

	// @Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha instanceof PcInstance) {
			PcInstance pc = (PcInstance) cha;
			if(cha.getClassType() !=  Lineage.LINEAGE_CLASS_DARKELF ){
				ChattingController.toChatting(cha, "다크엘프만 사용 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
		}

		if (cha.getClassType() == Lineage.LINEAGE_CLASS_DARKELF || isClassCheck(cha)) {

			int object_id = cbp.readD();
			object o = null;

			if (object_id == cha.getObjectId())
				o = cha;
			else
				o = cha.findInsideList(object_id);

			if (o != null) {
				// cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class),
				// cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
				int dmg = EnergyBolt.toBuff(cha, o, skill, Lineage.GFX_MODE_SPELL_DIRECTION, skill.getCastGfx(),
						(cha.getNowHp() + cha.getNowMp()) / 2);
				if (dmg > 0) {
					cha.setNowHp(1);
					cha.setNowMp(1);
				}
			}
		}
	}
}