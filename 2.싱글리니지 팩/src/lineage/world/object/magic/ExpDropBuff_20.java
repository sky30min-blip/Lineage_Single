package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_BuffEva;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class ExpDropBuff_20 extends Magic {
	public ExpDropBuff_20(Skill skill) {
		super(null, skill);
	}

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time) {
		if (bi == null)
			bi = new ExpDropBuff_20(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		if (o instanceof PcInstance) {
			PcInstance pc = (PcInstance) o;		
			pc.setDynamicExp(pc.getDynamicExp() + 0.5);

			pc.toSender(S_BuffEva.clone(BasePacketPooling.getPool(S_BuffEva.class), pc, getTime()));
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
		if (o instanceof PcInstance) {
			PcInstance pc = (PcInstance) o;	
			pc.setDynamicExp(pc.getDynamicExp() - 0.5);
	
			ChattingController.toChatting(o, String.format("\\fY%s 종료", getSkill().getName()), Lineage.CHATTING_MODE_MESSAGE);
			pc.toSender(S_BuffEva.clone(BasePacketPooling.getPool(S_BuffEva.class), pc, 0));
		}
	}

	@Override
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min)
			ChattingController.toChatting(o, String.format("\\fY%s: %d초 후 종료", getSkill().getName(), getTime()), Lineage.CHATTING_MODE_MESSAGE);
	}

	static public void init(object o, int time) {
		BuffController.append(o, ExpDropBuff_20.clone(BuffController.getPool(ExpDropBuff_20.class), SkillDatabase.find(702), time));
	}

	static public void init(Character cha, Skill skill) {
		BuffController.remove(cha, ExpDropBuff_10.class);
		BuffController.remove(cha, ExpDropBuff_50.class);
		
		if (skill.getCastGfx() > 0)
			cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill.getCastGfx()), true);
		BuffController.append(cha, ExpDropBuff_20.clone(BuffController.getPool(ExpDropBuff_20.class), skill, skill.getBuffDuration()));
		ChattingController.toChatting(cha, "드래곤의 루비: 경험치+50%", Lineage.CHATTING_MODE_MESSAGE);
	}
}
