package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_BuffStr;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class DressMighty extends Magic {

	public DressMighty(Skill skill){
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new DressMighty(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
	
	@Override
	public void toBuffStart(object o){
		if(o instanceof Character){
			Character cha = (Character)o;
			cha.setDynamicStr( (int) (cha.getDynamicStr() + skill.getMaxdmg()) );
		}
		toBuffUpdate(o);
	}
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min)
			ChattingController.toChatting(o, "\\fY드레스 마이티: " + getTime() + "초 후 종료", Lineage.CHATTING_MODE_MESSAGE);
	}
	@Override
	public void toBuffUpdate(object o){
		if(o instanceof Character){
			Character cha = (Character)o;
			if(Lineage.server_version>144)
				cha.toSender(S_BuffStr.clone(BasePacketPooling.getPool(S_BuffStr.class), cha, getTime(), (int) skill.getMaxdmg()));
			else
				cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
		}
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		if(o.isWorldDelete())
			return;
		if(o instanceof Character){
			Character cha = (Character)o;
			cha.setDynamicStr( (int) (cha.getDynamicStr() - skill.getMaxdmg()) );
			if(Lineage.server_version>144)
				cha.toSender(S_BuffStr.clone(BasePacketPooling.getPool(S_BuffStr.class), cha, 0, (int) skill.getMaxdmg()));
			else
				cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
			
			ChattingController.toChatting(o, "\\fY드레스 마이티 종료", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	static public void init(Character cha, Skill skill) {
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		
		if(SkillController.isMagic(cha, skill, true)) {
			//
			cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill.getCastGfx()), true);
			//
			BuffController.remove(cha, EnchantMighty.class);
			//
			BuffController.append(cha, DressMighty.clone(BuffController.getPool(DressMighty.class), skill, skill.getBuffDuration()));
			ChattingController.toChatting(cha, "드레스 마이티 :  STR+3", Lineage.CHATTING_MODE_MESSAGE);
		}
	}

}
