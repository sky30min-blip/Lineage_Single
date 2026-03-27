package lineage.world.object.magic;

import lineage.bean.database.MonsterSkill;
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
import lineage.world.object.instance.MonsterInstance;

public class DecayPotion extends Magic {

	public DecayPotion(Skill skill){
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new DecayPotion(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		o.setBuffDecayPotion(true);
		// 공격당한거 알리기.
		o.toDamage(cha, 0, Lineage.ATTACK_TYPE_MAGIC);
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		o.setBuffDecayPotion(false);
		ChattingController.toChatting(o, "\\fY디케이 포션 종료", Lineage.CHATTING_MODE_MESSAGE);
	}
	
	static public void init(Character cha, Skill skill, int object_id){
		// 초기화
		object o = null;
		// 타겟 찾기
		if(object_id == cha.getObjectId())
			o = cha;
		else
			o = cha.findInsideList( object_id );
		// 처리
		if(o != null){
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			
			if(SkillController.isMagic(cha, skill, true)) {
				if(SkillController.isFigure(cha, o, skill, true, false)) {
					o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
					BuffController.append(o, DecayPotion.clone(BuffController.getPool(DecayPotion.class), skill, skill.getBuffDuration()));
					ChattingController.toChatting(o, "디케이 포션: 회복용 물약 사용 불가", Lineage.CHATTING_MODE_MESSAGE);
				}
				// 투망상태 해제
				Detection.onBuff(cha);
			}
		}
	}
	
	static public void init(MonsterInstance mi, object o, MonsterSkill ms){					
		// 처리
		if(o != null){
			if(SkillController.isMagic(mi, ms, true)) {
				// 공격당한거 알리기.
				o.toDamage(mi, 0, Lineage.ATTACK_TYPE_MAGIC, Cancellation.class);
				
				if(SkillController.isFigure(mi, o, ms.getSkill(), false, false)) {
					o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, 7781), true);
					BuffController.append(o, DecayPotion.clone(BuffController.getPool(DecayPotion.class), ms.getSkill(), ms.getSkill().getBuffDuration()));
					ChattingController.toChatting(o, "디케이 포션: 회복용 물약 사용 불가", Lineage.CHATTING_MODE_MESSAGE);
				}		
			}
		}
	}
		
}
