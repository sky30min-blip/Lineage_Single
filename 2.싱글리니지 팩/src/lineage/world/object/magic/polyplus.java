package lineage.world.object.magic;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.bean.lineage.Clan;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.ClanController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class polyplus extends Magic {

	public polyplus(Skill skill) {
		super(null, skill);
	}

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time) {
		if (bi == null)
			bi = new polyplus(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		//
		if (o instanceof Character) {
			Character cha = (Character) o;
			cha.setDynamicAddDmg(cha.getDynamicAddDmg() + 1);
			cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() + 1);
			cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
		}
	}

	@Override
	public void toBuffStop(object o) {
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o) {
		if (o.isWorldDelete())
			return;
		//
		if (o instanceof Character) {
			Character cha = (Character) o;
			cha.setDynamicAddDmg(cha.getDynamicAddDmg() - 1);
			cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() - 1);
			cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
		
		}
	}
	
	@Override
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min)
			ChattingController.toChatting(o, "\\fY신화변신 버프: " + getTime() + "초 후 종료", Lineage.CHATTING_MODE_MESSAGE);
	}

	static public void init(Character cha, Skill skill) {
		// 처리
		
		onBuff(cha, skill);
		
	}
	static public void init(Character cha, int time){
		BuffController.append(cha, polyplus.clone(BuffController.getPool(polyplus.class), SkillDatabase.find(634), time));
	}
	static public void onBuff(object o, Skill skill) {
		BuffController.append(o, polyplus.clone(BuffController.getPool(polyplus.class), skill, skill.getBuffDuration()));

	}
}
