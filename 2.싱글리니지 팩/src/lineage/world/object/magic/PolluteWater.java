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

public class PolluteWater extends Magic {

	public PolluteWater(Skill skill) {
		super(null, skill);
	}

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time) {
		if (bi == null)
			bi = new PolluteWater(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		o.setBuffPolluteWater(true);
	}

	@Override
	public void toBuffStop(object o) {
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o) {
		o.setBuffPolluteWater(false);
		if (o instanceof PcInstance)
			ChattingController.toChatting(o, "\\fY폴루트 워터 종료", Lineage.CHATTING_MODE_MESSAGE);
	}

	static public void init(Character cha, Skill skill, long object_id) {
		// 초기화
		object o = cha.findInsideList(object_id);
		// 처리
		if (o != null) {
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			
			if (SkillController.isMagic(cha, skill, true) && SkillController.isFigure(cha, o, skill, true, false) && Util.isDistance(cha, o, 5)) {
				o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
				BuffController.append(o, PolluteWater.clone(BuffController.getPool(PolluteWater.class), skill, skill.getBuffDuration()));
				ChattingController.toChatting(o, "폴루트 워터: 힐 계열 마법, 회복용 물약 효과 1/2 감소", Lineage.CHATTING_MODE_MESSAGE);
			} else {
				if (!Util.isDistance(cha, o, 5))
					ChattingController.toChatting(cha, "\\fY대상이 너무 멀리 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}

}
