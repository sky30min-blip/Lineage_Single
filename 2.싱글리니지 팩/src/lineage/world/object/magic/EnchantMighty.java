package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_BuffStr;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class EnchantMighty extends Magic {

	public EnchantMighty(Skill skill) {
		super(null, skill);
	}

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time) {
		if (bi == null)
			bi = new EnchantMighty(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		if (o instanceof Character) {
			Character cha = (Character) o;
			cha.setDynamicStr(cha.getDynamicStr() + 5);
		}
		toBuffUpdate(o);
	}

	@Override
	public void toBuffUpdate(object o) {
		if (o instanceof Character) {
			Character cha = (Character) o;
			if (Lineage.server_version > 144)
				cha.toSender(S_BuffStr.clone(BasePacketPooling.getPool(S_BuffStr.class), cha, getTime(), 5));
			else
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
		if (o instanceof Character) {
			Character cha = (Character) o;
			cha.setDynamicStr(cha.getDynamicStr() - 5);
			ChattingController.toChatting(cha, "\\fY피지컬 인챈트: STR 종료", Lineage.CHATTING_MODE_MESSAGE);
			if (Lineage.server_version > 144)
				cha.toSender(S_BuffStr.clone(BasePacketPooling.getPool(S_BuffStr.class), cha, 0, 5));
			else
				cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
		}
	}
	
	@Override
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min)
			ChattingController.toChatting(o, "\\fY피지컬 인챈트 STR: " + getTime() + "초 후 종료", Lineage.CHATTING_MODE_MESSAGE);
	}

	static public void init(Character cha, Skill skill, int object_id) {
		// 초기화
		object o = null;
		// 타겟 찾기
		if (object_id == cha.getObjectId())
			o = cha;
		else
			o = cha.findInsideList(object_id);
		// 처리
		if (o != null) {
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			
			if (SkillController.isMagic(cha, skill, true)) {
				if (SkillController.isFigure(cha, o, skill, false, SkillController.isClan(cha, o))) {
					onBuff(o, skill);
				} else {
					// \f1마법이 실패했습니다.
					cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 280));
				}
			}
		}
	}

	static public void init(Character cha, int time) {
		BuffController.append(cha, EnchantMighty.clone(BuffController.getPool(EnchantMighty.class), SkillDatabase.find(6, 1), time));
	}

	static public void onBuff(object o, Skill skill) {
		//
		o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
		//
		BuffController.remove(o, DressMighty.class);
		//
		BuffController.append(o, EnchantMighty.clone(BuffController.getPool(EnchantMighty.class), skill, skill.getBuffDuration()));
		
		ChattingController.toChatting(o, "피지컬 인챈트 STR: STR+5", Lineage.CHATTING_MODE_MESSAGE);
	}

}
