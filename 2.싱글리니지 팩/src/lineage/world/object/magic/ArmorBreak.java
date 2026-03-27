package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class ArmorBreak extends Magic {

	public ArmorBreak(Skill skill) {
		super(null, skill);
	}

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time) {
		if (bi == null)
			bi = new ArmorBreak(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		if (o instanceof Character) {
			Character cha = (Character) o;
			cha.setBuffArmorBreak(true);
		}
	}

	@Override
	public void toBuffStop(object o) {
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o) {
		if (o instanceof Character) {
			Character cha = (Character) o;
			cha.setBuffArmorBreak(false);

		}
	}

	static public void init(Character cha, Skill skill, int object_id) {

		object o = null;

		if (object_id == cha.getObjectId())
			o = cha;
		else
			o = cha.findInsideList(object_id);

		if (o != null) {

			if (SkillController.isMagic(cha, skill, true)) {
				if (SkillController.isFigure(cha, o, skill, true, false)) {
					onBuff(o, skill, skill.getBuffDuration());
					return;
				}
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 280));

			}
		}
	}

	static public void onBuff(object o, Skill skill, int time) {
		o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
		BuffController.append(o, ArmorBreak.clone(BuffController.getPool(ArmorBreak.class), skill, time));
		ChattingController.toChatting(o, "일정시간동안 입는 피해가 크게 증가합니다.", Lineage.CHATTING_MODE_MESSAGE);
	}

}
