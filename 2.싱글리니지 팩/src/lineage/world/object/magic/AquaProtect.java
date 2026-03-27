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

public class AquaProtect extends Magic {

	public AquaProtect(Skill skill) {
		super(null, skill);
	}

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time) {
		if (bi == null)
			bi = new AquaProtect(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		if (o instanceof Character) {
			Character cha = (Character) o;
			cha.setDynamicEr(cha.getDynamicEr() + 15);
		}
		toBuffUpdate(o);
	}

	@Override
	public void toBuffStop(object o) {
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o) {
		if (o instanceof Character) {
			Character cha = (Character) o;
			cha.setDynamicEr(cha.getDynamicEr() - 15);
			ChattingController.toChatting(cha, "\\fY아쿠아 프로텍트 종료", Lineage.CHATTING_MODE_MESSAGE);
		}
	}

	@Override
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min)
			ChattingController.toChatting(o, "\\fY아쿠아 프로텍트: " + getTime() + "초 후 종료", Lineage.CHATTING_MODE_MESSAGE);
	}

	static public void init(Character cha, Skill skill, long object_id) {
		// 초기화
		object o = null;
		// 타겟 찾기
		if (object_id == cha.getObjectId())
			o = cha;
		else
			o = cha.findInsideList(object_id);
		// 처리
		if (o != null) {
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			
			if (SkillController.isMagic(cha, skill, true) && Util.isDistance(cha, o, 8)) {
				o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
				BuffController.append(o, AquaProtect.clone(BuffController.getPool(AquaProtect.class), skill, skill.getBuffDuration()));
				ChattingController.toChatting(o, "아쿠아 프로텍트: ER(원거리 회피)+15", Lineage.CHATTING_MODE_MESSAGE);
			} else {
				if (!Util.isDistance(cha, o, 8))
					ChattingController.toChatting(cha, "\\fY대상이 너무 멀리 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}

}
