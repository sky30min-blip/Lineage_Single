package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class MaanLife extends Magic {

	public MaanLife(Skill skill) {
		super(null, skill);
	}

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time) {
		if (bi == null)
			bi = new MaanLife(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		if (o instanceof Character) {
			Character cha = (Character) o;
			cha.setBuffMaanLife(true);
			// 근거리 대미지+2
			cha.setDynamicAddDmg(cha.getDynamicAddDmg() + 2);
			// 원거리 대미지+2
			cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() + 2);
			// 마법 치명타+2%
			cha.setDynamicMagicCritical(cha.getDynamicMagicCritical() + 2);
			// 스턴 내성+5%
			cha.setDynamicStunResist(cha.getDynamicStunResist() + 0.05);
			// 정령 내성+5%
			cha.setDynamicElfResist(cha.getDynamicElfResist() + 0.05);
		}
	}

	@Override
	public void toBuffUpdate(object o) {

	}

	@Override
	public void toBuffStop(object o) {
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o) {
		if (o.isWorldDelete())
			return;
		if (o instanceof Character) {
			Character cha = (Character) o;
			cha.setBuffMaanLife(false);
			// 근거리 대미지-2
			cha.setDynamicAddDmg(cha.getDynamicAddDmg() - 2);
			// 원거리 대미지-2
			cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() - 2);
			// 마법 치명타-2%
			cha.setDynamicMagicCritical(cha.getDynamicMagicCritical() - 2);
			// 스턴 내성-5%
			cha.setDynamicStunResist(cha.getDynamicStunResist() - 0.05);
			// 정령 내성-5%
			cha.setDynamicElfResist(cha.getDynamicElfResist() - 0.05);

			ChattingController.toChatting(cha, "\\fY생명의 마안 종료", Lineage.CHATTING_MODE_MESSAGE);
		}
	}

	@Override
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min)
			ChattingController.toChatting(o, "\\fY생명의 마안: " + getTime() + "초 후 종료", Lineage.CHATTING_MODE_MESSAGE);
	}

	static public void init(object o, int time) {
		BuffController.append(o, MaanLife.clone(BuffController.getPool(MaanLife.class), SkillDatabase.find(621), time));
	}

	static public void init(Character cha, Skill skill) {
		BuffController.remove(cha, MaanWatar.class);
		BuffController.remove(cha, MaanWind.class);
		BuffController.remove(cha, MaanEarth.class);
		BuffController.remove(cha, MaanFire.class);
		BuffController.remove(cha, MaanBirth.class);
		BuffController.remove(cha, MaanShape.class);

		cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill.getCastGfx()), true);
		BuffController.append(cha, MaanLife.clone(BuffController.getPool(MaanLife.class), skill, skill.getBuffDuration()));
		ChattingController.toChatting(cha, "생명의 마안: 일정 확률로 물리 회피 상승, 받는 마법 대미지 50% 감소, 근거리/원거리 대미지+2, 마법 치명타+2%, 스턴/정령 내성+5%", Lineage.CHATTING_MODE_MESSAGE);
	}
}
