package lineage.world.object.magic;

import lineage.bean.database.MonsterSkill;
import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class Silence extends Magic {

	public Silence(Skill skill) {
		super(null, skill);
	}

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time) {
		if (bi == null)
			bi = new Silence(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		o.setBuffSilence(true);
		// 공격당한거 알리기.
		o.toDamage(cha, 0, Lineage.ATTACK_TYPE_MAGIC);
		ChattingController.toChatting(o, "\\fY채팅 & 마법 사용 불가", Lineage.CHATTING_MODE_MESSAGE);
	}

	@Override
	public void toBuffUpdate(object o) {
		if (o instanceof Character)
			ChattingController.toChatting(o, "\\fY채팅 & 마법 사용 불가", Lineage.CHATTING_MODE_MESSAGE);
	}

	@Override
	public void toBuffStop(object o) {
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o) {
		o.setBuffSilence(false);
		ChattingController.toChatting(o, "\\fY사일런스 종료", Lineage.CHATTING_MODE_MESSAGE);
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
			// 투망상태 해제
			Detection.onBuff(cha);
			// 모션
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);

			if (SkillController.isMagic(cha, skill, true) && SkillController.isFigure(cha, o, skill, true, false))
				onBuff(o, skill, skill.getBuffDuration());
		}
	}

	static public void onBuff(object o, Skill skill, int time) {
		// 이팩트
		o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
		// 적용
		BuffController.append(o, Silence.clone(BuffController.getPool(Silence.class), skill, time));
	}

	static public void init2(Character cha, Skill skill, int object_id) {
		// 초기화
		object o = null;
		// 타겟 찾기
		if (object_id == cha.getObjectId())
			o = cha;
		else
			o = cha.findInsideList(object_id);
		// 처리
		if (o != null) {

			
				// 투망상태 해제
				Detection.onBuff(cha);
				// 처리

				onBuff(o, skill, skill.getBuffDuration());
		
			
		
		}
	}

	/**
	 * 몬스터용. 
	 * 2019-12-02 
	 * by connector12@nate.com
	 */
	static public void init(Character cha, object o, MonsterSkill ms, int action) {
		// 처리
		if (o != null) {
			// 투망상태 해제
			Detection.onBuff(cha);

			if (action > 0)
				// 모션
				cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, action), true);

			if (SkillController.isMagic(cha, ms, true) && SkillController.isFigure(cha, o, ms, false, false)) {
				// 이팩트
				o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, ms.getCastGfx() < 1 ? ms.getSkill().getCastGfx() : ms.getCastGfx()), true);
				// 적용
				BuffController.append(o, Silence.clone(BuffController.getPool(Silence.class), ms.getSkill(), ms.getBuffDuration() < 1 ? ms.getSkill().getBuffDuration() : ms.getBuffDuration()));
			}
		}
	}
}
