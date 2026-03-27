package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class AdvanceSpirit extends Magic {

	public AdvanceSpirit(Skill skill) {
		super(null, skill);
	}

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time) {
		if (bi == null)
			bi = new AdvanceSpirit(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		if (o instanceof Character) {
			Character c = (Character) o;
			c.setBuffAdvanceSpiritHp((int) (c.getMaxHp() * 0.2));
			c.setBuffAdvanceSpiritMp((int) (c.getMaxMp() * 0.2));
			c.setDynamicHp(c.getDynamicHp() + c.getBuffAdvanceSpiritHp());
			c.setDynamicMp(c.getDynamicMp() + c.getBuffAdvanceSpiritMp());
			c.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), c));
		}
	}

	@Override
	public void toBuffStop(object o) {
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o) {
		if (o instanceof Character) {
			Character c = (Character) o;
			c.setDynamicHp(c.getDynamicHp() - c.getBuffAdvanceSpiritHp());
			c.setDynamicMp(c.getDynamicMp() - c.getBuffAdvanceSpiritMp());
			ChattingController.toChatting(c, "\\fY어드밴스 스피릿 종료", Lineage.CHATTING_MODE_MESSAGE);
			c.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), c));
		}
	}

	@Override
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min)
			ChattingController.toChatting(o, "\\fY어드밴스 스피릿: " + getTime() + "초 후 종료", Lineage.CHATTING_MODE_MESSAGE);
	}

	static public void init(Character cha, int time) {
		BuffController.append(cha, AdvanceSpirit.clone(BuffController.getPool(AdvanceSpirit.class), SkillDatabase.find(9, 2), time));
	}

	static public void init(Character cha, Skill skill, int object_id) {
		// 초기화
		object o = null;
		// 타겟 찾기
		if (Lineage.is_advance_spirit_target) {
			if (object_id == cha.getObjectId())
				o = cha;
			else
				o = cha.findInsideList(object_id);
		} else {
			o = cha;
		}
		
		// 처리
		if (o != null) {
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);

			if (SkillController.isMagic(cha, skill, true) && Util.isAreaAttack(cha, o) && Util.isAreaAttack(o, cha)) {
				o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
				
				BuffController.append(o, AdvanceSpirit.clone(BuffController.getPool(AdvanceSpirit.class), skill, skill.getBuffDuration()));
				ChattingController.toChatting(o, "어드밴스 스피릿: 최대 HP+20%, 최대 MP+20%", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}

	/*
	 * 마법주문서에서 사용중
	 */
	static public void onBuff(object o, Skill skill) {
		o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);

		BuffController.append(o, AdvanceSpirit.clone(BuffController.getPool(AdvanceSpirit.class), skill, skill.getBuffDuration()));
	}

}
