package lineage.world.object.magic;

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

public class Meditation extends Magic {

	public Meditation(Skill skill) {
		super(null, skill);
	}

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time) {
		if (bi == null)
			bi = new Meditation(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		o.setBuffMeditation(true);
		if (o instanceof Character) {
			Character cha = (Character) o;
			cha.setDynamicTicMp(cha.getDynamicTicMp() + 5);
			cha.setBuffMeditaitonLevel(0);
		}
	}

	@Override
	public void toBuffStop(object o) {
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o) {
		o.setBuffMeditation(false);

		if (o instanceof Character) {
			Character cha = (Character) o;
			cha.setDynamicTicMp(cha.getDynamicTicMp() - 5);
			cha.setBuffMeditaitonLevel(0);
			ChattingController.toChatting(cha, "\\fY메디테이션 종료", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	@Override
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min)
			ChattingController.toChatting(o, "\\fY메디테이션: " + getTime() + "초 후 종료", Lineage.CHATTING_MODE_MESSAGE);
	}

	static public void init(Character cha, Skill skill) {
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		
		if (SkillController.isMagic(cha, skill, true))
			onBuff(cha, skill, skill.getBuffDuration());
	}

	static public void onBuff(object o, Skill skill, int time) {
		o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
		BuffController.append(o, Meditation.clone(BuffController.getPool(Meditation.class), skill, time));
		ChattingController.toChatting(o, "메디테이션: MP회복+5 유지되는 시간동안 회복량 점차 증가", Lineage.CHATTING_MODE_MESSAGE);
	}

}
