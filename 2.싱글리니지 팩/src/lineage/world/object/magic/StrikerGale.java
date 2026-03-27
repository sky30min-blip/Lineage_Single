package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class StrikerGale extends Magic {

	public StrikerGale(Skill skill) {
		super(null, skill);
	}

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time) {
		if (bi == null)
			bi = new StrikerGale(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		o.setBuffStrikerGale(true);
	}

	@Override
	public void toBuffStop(object o) {
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o) {
		o.setBuffStrikerGale(false);
		if (o instanceof PcInstance)
			ChattingController.toChatting(o, "\\fY스트라이커 게일 종료", Lineage.CHATTING_MODE_MESSAGE);
	}

	static public void init(Character cha, Skill skill, long object_id) {
		// 초기화
		object o = cha.findInsideList(object_id);
		// 처리
		if (o != null) {
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			
			if (SkillController.isMagic(cha, skill, true) && SkillController.isFigure(cha, o, skill, true, false) && Util.isDistance(cha, o, 8)) {
				o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
				BuffController.append(o, StrikerGale.clone(BuffController.getPool(StrikerGale.class), skill, skill.getBuffDuration()));
				ChattingController.toChatting(o, "스트라이커 게일: ER(원거리 회피) 1/3 감소", Lineage.CHATTING_MODE_MESSAGE);
			} else {
				if (!Util.isDistance(cha, o, 6))
					ChattingController.toChatting(cha, "\\fY대상이 너무 멀리 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}

}
