package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class DecreaseWeight extends Magic {
	
	public DecreaseWeight(Skill skill){
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new DecreaseWeight(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
	
	@Override
	public void toBuffStart(object o){
		o.setBuffDecreaseWeight(true);
		if(o instanceof Character)
			o.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), (Character)o));
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		if(o.isWorldDelete())
			return;
		o.setBuffDecreaseWeight(false);
		if(o instanceof Character) {
			o.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), (Character)o));
			ChattingController.toChatting(o, "\\fY디크리즈 웨이트 종료", Lineage.CHATTING_MODE_MESSAGE);
			o.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), (Character)o));
		}
	}
	
	static public void init(Character cha, Skill skill){
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		
		if(SkillController.isMagic(cha, skill, true))
			onBuff(cha, skill);
	}
	
	@Override
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min)
			ChattingController.toChatting(o, "\\fY디크리즈 웨이트: " + getTime() + "초 후 종료", Lineage.CHATTING_MODE_MESSAGE);
	}
	
	/**
	 * 중복코드 방지용
	 *  : 마법주문서 (디크리즈 웨이트) 에서도 사용중.
	 * @param cha
	 * @param skill
	 */
	static public void onBuff(Character cha, Skill skill){
		cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill.getCastGfx()), true);
		BuffController.append(cha, DecreaseWeight.clone(BuffController.getPool(DecreaseWeight.class), skill, skill.getBuffDuration()));
		ChattingController.toChatting(cha, "디크리즈 웨이트: 최대무게+180", Lineage.CHATTING_MODE_MESSAGE);
	}
	
	/*
	 * 운영자 올버프에서 사용중
	 * */
	static public void onBuff(object o, Skill skill) {
		o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
		
		BuffController.append(o, DecreaseWeight.clone(BuffController.getPool(DecreaseWeight.class), skill, skill.getBuffDuration()));
	}
	
}
