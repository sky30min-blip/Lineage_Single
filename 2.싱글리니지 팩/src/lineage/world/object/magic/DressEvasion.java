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

public class DressEvasion extends Magic {

	public DressEvasion(Skill skill){
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new DressEvasion(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
	
	@Override
	public void toBuffStart(object o){
		if(o instanceof Character) {
			Character cha = (Character)o;
			cha.setDynamicEr( cha.getDynamicEr() + 18 );
		}
	}
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min)
			ChattingController.toChatting(o, "\\fY드레스 이베이젼: " + getTime() + "초 후 종료", Lineage.CHATTING_MODE_MESSAGE);
	}
	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		if(o.isWorldDelete())
			return;
		if(o instanceof Character) {
			Character cha = (Character)o;
			cha.setDynamicEr( cha.getDynamicEr() - 18 );
			ChattingController.toChatting(o, "\\fY드레스 이베이젼 종료", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	static public void init(Character cha, Skill skill) {
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		
		if(SkillController.isMagic(cha, skill, true)) {
			cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill.getCastGfx()), true);
			BuffController.append(cha, DressEvasion.clone(BuffController.getPool(DressEvasion.class), skill, skill.getBuffDuration()));
			ChattingController.toChatting(cha, "드레스 이베이젼 :  ER+18", Lineage.CHATTING_MODE_MESSAGE);
		}
	}

}
