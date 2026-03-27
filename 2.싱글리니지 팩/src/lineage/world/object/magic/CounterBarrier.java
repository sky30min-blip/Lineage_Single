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

public class CounterBarrier extends Magic {

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time) {
		if (bi == null)
			bi = new CounterBarrier(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	public CounterBarrier(Skill skill) {
		super(null, skill);
	}

	@Override
	public void toBuffStart(object o) {
		if (o instanceof Character) {
			Character cha = (Character) o;
			cha.setBuffCounterBarrier(true);
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
			cha.setBuffCounterBarrier(false);
			ChattingController.toChatting(o, "\\fY카운터 배리어 종료", Lineage.CHATTING_MODE_MESSAGE);
		}
	}

	@Override
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min)
			ChattingController.toChatting(o, "\\fY카운터 배리어: " + getTime() + "초 후 종료", Lineage.CHATTING_MODE_MESSAGE);
	}

	/**
	 * 
	 * @param cha
	 * @param skill
	 * @param object_id
	 * @param x
	 * @param y
	 */
	static public void init(Character cha, Skill skill) {
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		
		if (cha.getInventory().getSlot(Lineage.SLOT_WEAPON) != null && cha.getInventory().getSlot(Lineage.SLOT_WEAPON).getItem().getType2().equalsIgnoreCase("tohandsword")
				&& SkillController.isMagic(cha, skill, true)) {
			cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill.getCastGfx()), true);
			BuffController.append(cha, CounterBarrier.clone(BuffController.getPool(CounterBarrier.class), skill, skill.getBuffDuration()));
			ChattingController.toChatting(cha, "카운터 배리어: 일정 확률로 상대방의 근거리 물리 공격을 회피하고 일정 대미지 반사, 무기 장착 해제시 종료", Lineage.CHATTING_MODE_MESSAGE);
		} else {
			if (cha.getInventory().getSlot(Lineage.SLOT_WEAPON) == null)
				ChattingController.toChatting(cha, "\\fY무기를 착용해야 사용가능합니다.", Lineage.CHATTING_MODE_MESSAGE);

			if (!cha.getInventory().getSlot(Lineage.SLOT_WEAPON).getItem().getType2().equalsIgnoreCase("tohandsword"))
				ChattingController.toChatting(cha, "\\fY양손무기를 착용해야 사용가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
		}
	}

	static public void init(Character cha, MonsterSkill ms, int action) {
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, action), true);

		if (SkillController.isMagic(cha, ms.getSkill(), true)) {
			cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, ms.getCastGfx()), true);
			BuffController.append(cha, CounterBarrier.clone(BuffController.getPool(CounterBarrier.class), ms.getSkill(), ms.getSkill().getBuffDuration()));
		}
	}
}
