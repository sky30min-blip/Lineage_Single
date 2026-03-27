package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_BuffShield;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class EarthGuardian extends Magic {

	public EarthGuardian(Skill skill) {
		super(null, skill);
	}

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time) {
		if (bi == null)
			bi = new EarthGuardian(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		if (o instanceof Character) {
			Character cha = (Character) o;
			cha.setDynamicReduction(cha.getDynamicReduction() + 2);
//			cha.toSender(S_BuffShield.clone(BasePacketPooling.getPool(S_BuffShield.class), getTime(), 7));
		}
	}

	@Override
	public void toBuffUpdate(object o) {
		//o.toSender(S_BuffShield.clone(BasePacketPooling.getPool(S_BuffShield.class), getTime(), 6));
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
			cha.setDynamicReduction(cha.getDynamicReduction() - 2);
//			cha.toSender(S_BuffShield.clone(BasePacketPooling.getPool(S_BuffShield.class), 0, 7));
		}
	}
	
	@Override
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min);
	}

	static public void init(Character cha, Skill skill) {
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		
		if (SkillController.isMagic(cha, skill, true) && cha instanceof PcInstance) {
			PcInstance pc = (PcInstance) cha;
			onBuff(pc, skill, skill.getBuffDuration());
		}
	}

	static public void init(Character cha, int time) {
		BuffController.append(cha, EarthGuardian.clone(BuffController.getPool(EarthGuardian.class), SkillDatabase.find(19, 5), time));
	}

	static public void onBuff(PcInstance pc, Skill skill, int time) {
		pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, skill.getCastGfx()), true);
		BuffController.append(pc, EarthGuardian.clone(BuffController.getPool(EarthGuardian.class), skill, time));
	}
}
