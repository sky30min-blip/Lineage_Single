package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class MaanBirthDelay extends Magic {
	
	public MaanBirthDelay(Skill skill) {
		super(null, skill);
	}

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time) {
		if (bi == null)
			bi = new MaanBirthDelay(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {

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
		ChattingController.toChatting(o, String.format("\\fY%s 종료", getSkill().getName()), Lineage.CHATTING_MODE_MESSAGE);
	}

	@Override
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min)
			ChattingController.toChatting(o, String.format("\\fY%s: %d초 후 종료", getSkill().getName(), getTime()), Lineage.CHATTING_MODE_MESSAGE);
	}

	static public void init(object o, int time) {
		BuffController.append(o, MaanBirthDelay.clone(BuffController.getPool(MaanBirthDelay.class), SkillDatabase.find(626), time));
	}

	static public void init(Character cha, Skill skill) {
		BuffController.append(cha, MaanBirthDelay.clone(BuffController.getPool(MaanBirthDelay.class), skill, skill.getBuffDuration()));
	}
}
