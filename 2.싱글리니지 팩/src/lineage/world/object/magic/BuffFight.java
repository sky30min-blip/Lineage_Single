package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_CharacterSpMr;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class BuffFight extends Magic {

	public BuffFight(Skill skill) {
		super(null, skill);
	}

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time) {
		if (bi == null)
			bi = new BuffFight(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		if (o instanceof Character) {
			Character target = (Character) o;
			target.setDynamicAddDmg(target.getDynamicAddDmg() + 2);
			target.setDynamicAddDmgBow(target.getDynamicAddDmgBow() + 2);
			target.setDynamicSp(target.getDynamicSp() + 2);
			target.setDynamicHp(target.getDynamicHp() + 50);

			target.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), target));
			target.toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), target));

			toBuffUpdate(o);
		}
	}

	@Override
	public void toBuffStop(object o) {
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o) {
		if (o instanceof Character) {
			Character target = (Character) o;
			target.setDynamicAddDmg(target.getDynamicAddDmg() - 2);
			target.setDynamicAddDmgBow(target.getDynamicAddDmgBow() - 2);
			target.setDynamicSp(target.getDynamicSp() - 2);
			target.setDynamicHp(target.getDynamicHp() - 50);

			target.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), target));
			target.toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), target));

			ChattingController.toChatting(o, "\\fY전투 강화 물약 종료", Lineage.CHATTING_MODE_MESSAGE);
		}
	}

	@Override
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min)
			ChattingController.toChatting(o, "\\fY전투 강화 물약: " + getTime() + "초 후 종료", Lineage.CHATTING_MODE_MESSAGE);
	}

	static public void init(object o, int time) {
		BuffController.append(o, BuffFight.clone(BuffController.getPool(BuffFight.class), SkillDatabase.find(601), time));
	}

	static public void onBuff(object o, Skill skill) {
		o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
		// 버프 등록
		BuffController.append(o, BuffFight.clone(BuffController.getPool(BuffFight.class), skill, skill.getBuffDuration()));
		ChattingController.toChatting(o, "전투 강화 물약: 추가 타격+2, SP+2, HP+50", Lineage.CHATTING_MODE_MESSAGE);
	}

}
