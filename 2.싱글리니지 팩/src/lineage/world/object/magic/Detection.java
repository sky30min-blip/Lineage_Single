package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.world.controller.BuffController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class Detection extends Magic {

	public Detection(Skill skill) {
		super(null, skill);
	}

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time) {
		if (bi == null)
			bi = new Detection(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	static public void init(Character cha, Skill skill){
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, 19), true);
		
		if(SkillController.isMagic(cha, skill, true))
			onBuff(cha, skill);
	}
	
	/**
	 * 중복코드 방지용.
	 *  : 마법주문서 (디텍션) 에서도 사용중.
	 * @param cha
	 * @param skill
	 */
	static public void onBuff(Character cha, Skill skill){
		cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill.getCastGfx()), true);

		if(cha.isInvis())
			BuffController.append(cha, Detection.clone(BuffController.getPool(Detection.class), skill, 3));
		//
		onBuff(cha);
		for(object o : cha.getInsideList(true)) {
			onBuff(o);
			// 디텍션 시전된거 알리기.
			o.toMagic(cha, Detection.class);
		}
	}
	
	static public void onBuff(object o){
		// 운영자는 무시.
		if(o==null || o.getGm()>0)
			return;
		
		if( o.isInvis() ){
			o.setInvis(false);
			// 인비지마법 제거.
			BuffController.remove(o, InvisiBility.class);
		}
	}
}
