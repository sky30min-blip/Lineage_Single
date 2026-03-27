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

public class MaanEarth extends Magic {

	public MaanEarth(Skill skill) {
		super(null, skill);
	}

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time) {
		if (bi == null)
			bi = new MaanEarth(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		if (o instanceof Character) {
			o.setBuffMaanEarth(true);
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
			o.setBuffMaanEarth(false);
			ChattingController.toChatting(o, "\\fY지룡의 마안 종료", Lineage.CHATTING_MODE_MESSAGE);
		}
	}

	@Override
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min)
			ChattingController.toChatting(o, "\\fY지룡의 마안: " + getTime() + "초 후 종료", Lineage.CHATTING_MODE_MESSAGE);
	}

	static public void init(object o, int time) {
		BuffController.append(o, MaanEarth.clone(BuffController.getPool(MaanEarth.class), SkillDatabase.find(617), time));
	}

	static public void init(Character cha, Skill skill) {
		BuffController.remove(cha, MaanWatar.class);
		BuffController.remove(cha, MaanWind.class);
		BuffController.remove(cha, MaanFire.class);
		BuffController.remove(cha, MaanBirth.class);
		BuffController.remove(cha, MaanShape.class);
		BuffController.remove(cha, MaanLife.class);

		cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill.getCastGfx()), true);
		BuffController.append(cha, MaanEarth.clone(BuffController.getPool(MaanEarth.class), skill, skill.getBuffDuration()));
		ChattingController.toChatting(cha, "지룡의 마안: 일정 확률로 물리 공격에 대한 회피 상승", Lineage.CHATTING_MODE_MESSAGE);
	}

}
