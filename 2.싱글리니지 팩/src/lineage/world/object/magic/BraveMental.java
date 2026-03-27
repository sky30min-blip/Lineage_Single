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
import lineage.world.object.instance.PcInstance;

public class BraveMental extends Magic {

	public BraveMental(Skill skill){
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new BraveMental(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		o.setBuffBraveMental(true);
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		if(o.isWorldDelete())
			return;
		o.setBuffBraveMental(false);
		ChattingController.toChatting(o, "\\fY브레이브 멘탈 종료", Lineage.CHATTING_MODE_MESSAGE);
	}
	
	@Override
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min)
			ChattingController.toChatting(o, "\\fY브레이브 멘탈: " + getTime() + "초 후 종료", Lineage.CHATTING_MODE_MESSAGE);
	}
	
	static public void init(Character cha, Skill skill){
		// 패킷
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		
		// 시전가능 확인
		if(SkillController.isMagic(cha, skill, true) && cha instanceof PcInstance){		
			// 처리.
			onBuff(cha, skill);
		}
	}
	static public void init2(Character cha, Skill skill){

		// 시전가능 확인
		if(SkillController.isMagic(cha, skill, true) && cha instanceof PcInstance){		
			// 처리.
			onBuff(cha, skill);
		}
	}
	/**
	 * 중복코드 방지용.
	 * @param cha
	 * @param pc
	 * @param skill
	 */
	static public void onBuff(object o, Skill skill) {
		o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
		BuffController.append(o, BraveMental.clone(BuffController.getPool(BraveMental.class), skill, skill.getBuffDuration()));

		ChattingController.toChatting(o, "브레이브 멘탈: 일정 확률로 근거리 대미지 1.8배", Lineage.CHATTING_MODE_MESSAGE);
	}

}
