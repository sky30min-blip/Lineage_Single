package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_BuffShield;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class ShadowArmor extends Magic {

	public ShadowArmor(Skill skill) {
		super(null, skill);
	}

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time) {
		if (bi == null)
			bi = new ShadowArmor(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		if (o instanceof Character) {
			Character cha = (Character) o;
			cha.setDynamicAc((int) (cha.getDynamicAc() + skill.getMaxdmg()));
			cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
			if (Lineage.server_version > 144)
				cha.toSender(S_BuffShield.clone(BasePacketPooling.getPool(S_BuffShield.class), getTime(),
						(int) skill.getMaxdmg()));
		}
	}

	@Override
	public void toBuffUpdate(object o) {
		if (Lineage.server_version > 144)
			o.toSender(S_BuffShield.clone(BasePacketPooling.getPool(S_BuffShield.class), getTime(),
					(int) skill.getMaxdmg()));
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
			cha.setDynamicAc((int) (cha.getDynamicAc() - skill.getMaxdmg()));
			cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
			if (Lineage.server_version > 144)
				cha.toSender(
						S_BuffShield.clone(BasePacketPooling.getPool(S_BuffShield.class), 0, (int) skill.getMaxdmg()));
			ChattingController.toChatting(o, "\\fY쉐도우 아머 종료", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	@Override
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min)
			ChattingController.toChatting(o, "\\fY쉐도우 아머: " + getTime() + "초 후 종료", Lineage.CHATTING_MODE_MESSAGE);
	}
	static public void init(Character cha, Skill skill) {
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha,
				Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);

		if (SkillController.isMagic(cha, skill, true))
			onBuff(cha, skill);
	}

	static public void init(Character cha, int time) {
		BuffController.append(cha, ShadowArmor.clone(BuffController.getPool(ShadowArmor.class), SkillDatabase.find(654), time));
	}

	static public void onBuff(Character cha, Skill skill) {
		cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill.getCastGfx()),
				true);

		// 쉴드 제거
		BuffController.remove(cha, Shield.class);
		// 어스스킨 제거
		BuffController.remove(cha, EarthSkin.class);
		// 브레스오브어스 제거
		BuffController.remove(cha, EarthGuardian.class);
		// 아이언스킨 제거
		BuffController.remove(cha, IronSkin.class);
		// 쉐도우아머 적용
		BuffController.append(cha,
				ShadowArmor.clone(BuffController.getPool(ShadowArmor.class), skill, skill.getBuffDuration()));
		
		ChattingController.toChatting(cha, "쉐도우 아머 :  AC + 7", Lineage.CHATTING_MODE_MESSAGE);
	}

}
