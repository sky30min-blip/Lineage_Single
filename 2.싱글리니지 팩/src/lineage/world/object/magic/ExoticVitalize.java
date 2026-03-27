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

public class ExoticVitalize extends Magic {

	public ExoticVitalize(Skill skill){
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new ExoticVitalize(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		o.setBuffExoticVitalize(true);
		toBuffUpdate(o);
	}
	
	@Override
	public void toBuffUpdate(object o) {
		o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		o.setBuffExoticVitalize( false );
		ChattingController.toChatting(o, "\\fY엑조틱 바이탈라이즈 종료", Lineage.CHATTING_MODE_MESSAGE);
	}
	
	@Override
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min)
			ChattingController.toChatting(o, "\\fY엑조틱 바이탈라이즈: " + getTime() + "초 후 종료", Lineage.CHATTING_MODE_MESSAGE);
	}
	
	static public void init(Character cha, Skill skill, long object_id) {
		// 처리
		if (cha != null) {
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			
			if(SkillController.isMagic(cha, skill, true)) {
				BuffController.append(cha, ExoticVitalize.clone(BuffController.getPool(ExoticVitalize.class), skill, skill.getBuffDuration()));
				ChattingController.toChatting(cha, "엑조틱 바이탈라이즈: 무게 50% 초과해도 HP/MP 자연 회복", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}
	
	static public void init(Character cha, Skill skill) {
		// 처리
		if (cha != null) {
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			
			if(SkillController.isMagic(cha, skill, true)) {
				BuffController.append(cha, ExoticVitalize.clone(BuffController.getPool(ExoticVitalize.class), skill, skill.getBuffDuration()));
				ChattingController.toChatting(cha, "엑조틱 바이탈라이즈: 무게 50% 초과해도 HP/MP 자연 회복", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}
}
