package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class Disease extends Magic {

	public Disease(Skill skill) {
		super(null, skill);
	}

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time) {
		if (bi == null)
			bi = new Disease(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		o.setBuffDisease(true);
		if (o instanceof Character) {
			Character cha = (Character) o;
			cha.setDynamicAc(cha.getDynamicAc() - 12);
			cha.setDynamicAddHit(cha.getDynamicAddHit() - 6);
			cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
			// 공격당한거 알리기.
			o.toDamage(cha, 0, Lineage.ATTACK_TYPE_MAGIC);
		}
	}

	@Override
	public void toBuffStop(object o) {
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o) {
		o.setBuffDisease(false);
		if (o instanceof Character) {
			Character cha = (Character) o;
			cha.setDynamicAc(cha.getDynamicAc() + 12);
			cha.setDynamicAddHit(cha.getDynamicAddHit() + 6);
			ChattingController.toChatting(cha, "\\fY디지즈 종료", Lineage.CHATTING_MODE_MESSAGE);
			cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
		}
	}

	static public void init(Character cha, Skill skill, int object_id) {
		// 초기화
		object o = null;
		// 타겟 찾기
		if (object_id == cha.getObjectId())
			o = cha;
		else
			o = cha.findInsideList(object_id);
		// 처리
		if (o != null) {
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha,
					Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);

			if (SkillController.isMagic(cha, skill, true)) {
				// 투망상태 해제
				Detection.onBuff(cha);
				// 처리
				if (SkillController.isFigure(cha, o, skill, true, false)) {
					onBuff(o, skill, skill.getBuffDuration());
					return;
				}
				// \f1마법이 실패했습니다.
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 280));
			}
		}

	}

	static public void onBuff(object o, Skill skill, int time) {
		o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
		BuffController.append(o, Disease.clone(BuffController.getPool(Disease.class), skill, time));
		ChattingController.toChatting(o, "디지즈: AC+12, 근거리 명중-6", Lineage.CHATTING_MODE_MESSAGE);
	}

}
