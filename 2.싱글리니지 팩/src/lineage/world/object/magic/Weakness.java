package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class Weakness extends Magic {

	public Weakness(Skill skill){
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new Weakness(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
		
	@Override
	public void toBuffStart(object o){
		if (o instanceof Character) {
			Character target = (Character) o;
			target.setDynamicAddDmg(target.getDynamicAddDmg() - 5);
			target.setDynamicAddHit(target.getDynamicAddHit() - 1);
			o.setBuffWeakness(true);
			// 공격당한거 알리기.
			o.toDamage(cha, 0, Lineage.ATTACK_TYPE_MAGIC);
		}
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		if (o instanceof Character) {
			Character target = (Character) o;
			target.setDynamicAddDmg(target.getDynamicAddDmg() + 5);
			target.setDynamicAddHit(target.getDynamicAddHit() + 1);
			o.setBuffWeakness(false);
			ChattingController.toChatting(o, "\\fY위크니스 종료", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	static public void init(Character cha, Skill skill, int object_id){
		// 초기화
		object o = cha.findInsideList( object_id );
		// 처리
		if(o != null){
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			
			if(SkillController.isMagic(cha, skill, true)){
				// 투망상태 해제
				Detection.onBuff(cha);
				// 처리
				if(SkillController.isFigure(cha, o, skill, true, false)){
					o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
					BuffController.append(o, Weakness.clone(BuffController.getPool(Weakness.class), skill, skill.getBuffDuration()));
					ChattingController.toChatting(o, "위크니스: 근거리 대미지-5, 근거리 명중-1", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
				// \f1마법이 실패했습니다.
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 280));
			}
		}
	}

}
