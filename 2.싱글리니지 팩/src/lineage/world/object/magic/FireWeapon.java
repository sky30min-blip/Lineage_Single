package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_BuffElf;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class FireWeapon extends Magic {

	public FireWeapon(Skill skill){
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new FireWeapon(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		o.setBuffFireWeapon(true);
		if (o instanceof Character) {
			Character target = (Character) o;
			target.setDynamicAddDmg(target.getDynamicAddDmg() + 2);
			target.setDynamicAddHit(target.getDynamicAddHit() + 4);
			toBuffUpdate(o);
			ChattingController.toChatting(o, "\\fY근거리 대미지+2, 근거리 명중+4", Lineage.CHATTING_MODE_MESSAGE);
		}	
	}
	
	@Override
	public void toBuffUpdate(object o) {
		o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
		o.toSender(S_BuffElf.clone(BasePacketPooling.getPool(S_BuffElf.class), 147, skill.getBuffDuration()));
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		o.setBuffFireWeapon(false);
		if (o instanceof Character) {
			Character target = (Character) o;
			target.setDynamicAddDmg(target.getDynamicAddDmg() - 2);
			target.setDynamicAddHit(target.getDynamicAddHit() - 4);
			ChattingController.toChatting(o, "\\fY파이어 웨폰 종료", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	static public void init(Character cha, Skill skill, long object_id){	
		if(cha != null){
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			
			if(SkillController.isMagic(cha, skill, true) && SkillController.isFigure(cha, cha, skill, false, false)){
				// 버닝 웨폰
				if (BuffController.find(cha, SkillDatabase.find(132)) != null)
					return;
				
				BuffController.append(cha, FireWeapon.clone(BuffController.getPool(FireWeapon.class), skill, skill.getBuffDuration()));
			}
		}
	}
}
