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

public class AbsoluteBarrier extends Magic {

	public AbsoluteBarrier(Skill skill){
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new AbsoluteBarrier(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		o.setBuffAbsoluteBarrier(true);
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		o.setBuffAbsoluteBarrier(false);
	}
	
	static public void init(Character cha, Skill skill){
		if (cha.getMap() == Lineage.teamBattleMap || cha.getMap() == Lineage.BattleRoyalMap) {
			ChattingController.toChatting(cha, "이곳에선 해당 스킬을 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		// 처리
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		
		if(SkillController.isMagic(cha, skill, true)){
			cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill.getCastGfx()), true);
			BuffController.append(cha, AbsoluteBarrier.clone(BuffController.getPool(AbsoluteBarrier.class), skill, skill.getBuffDuration()));
		}
	}
	
	static public void onBuff(object o, Skill skill, int time){
		if (o.getMap() == Lineage.teamBattleMap || o.getMap() == Lineage.BattleRoyalMap) {
			ChattingController.toChatting(o, "이곳에선 해당 스킬을 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
		BuffController.append(o, AbsoluteBarrier.clone(BuffController.getPool(AbsoluteBarrier.class), skill, time));
	}
	
}
