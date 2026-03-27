package kuberaitem;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectGfx;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.ArmorBreak;

public class ItemArmorBreak extends ItemInstance {
	private Skill skill;

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new ItemArmorBreak();
		item.setSkill(SkillDatabase.find(646));
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

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {


		PcInstance pc = (PcInstance) cha;
		if (pc.getClassType() != Lineage.LINEAGE_CLASS_DARKELF) {
		    ChattingController.toChatting(pc, "다크엘프만 사용 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
		    return;
		}

		int objId = cbp.readD();
		object target = cha.getObjectId() == objId ? cha : cha.findInsideList(objId);

		if (target == null) {
		    ChattingController.toChatting(cha, "대상을 찾을 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
		    return;
		}
		if(cha == target){
			   ChattingController.toChatting(cha, "자기 자신에게는 시전 할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			    return;
		}
	
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);

		if (!Util.isDistance(cha, target, 3)) {
		    ChattingController.toChatting(cha, "상대방이 너무 멀리 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (SkillController.isDelay(cha, skill) && SkillController.isMagic(cha, skill, false)) {
		    try {
		        ArmorBreak.onBuff( target, skill, skill.getBuffDuration());
		    } catch (Exception e) {
		        ChattingController.toChatting(cha, "스킬을 적용하는 도중 오류가 발생했습니다.", Lineage.CHATTING_MODE_MESSAGE);
		        // Log the error
		        e.printStackTrace();
		    }
		}
	}
}