package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_SoundEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class Light extends Magic {

	public Light(Skill skill){
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new Light(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
	
	@Override
	public void toBuffStart(object o){
		toBuffUpdate(o);
	}

	@Override
	public void toBuffUpdate(object o) {
		if(o.getLight() < 14)
			o.setLight(14);
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		if(o.getLight() <= 14)
			o.setLight(0);
	}

	static public void init(Character cha, Skill skill){
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		
		if(SkillController.isMagic(cha, skill, true))
			onBuff(cha, skill);
	}

	static public void init(Character cha, int time){
		BuffController.append(cha, Light.clone(BuffController.getPool(Light.class), SkillDatabase.find(1, 1), time));
	}
	
	/**
	 * 중복 코드 방지용.
	 *  : 마법주문서 (라이트) 에서 사용중.
	 * @param cha
	 * @param skill
	 */
	static public void onBuff(Character cha, Skill skill){
		cha.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), skill.getCastGfx()), true);
		BuffController.append(cha, Light.clone(BuffController.getPool(Light.class), skill, skill.getBuffDuration()));
	}
	
}
