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

public class ReductionArmor extends Magic {
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new ReductionArmor(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
	
	public ReductionArmor(Skill skill){
		super(null, skill);
	}
	
	@Override
	public void toBuffStart(object o) {
		if (o instanceof Character) {
			Character cha = (Character) o;		
			cha.setReductionAromr(1 + (cha.getLevel() - 50 < 5 ? 0 : ((cha.getLevel() - 50) / 5)));
			cha.setDynamicReduction(cha.getDynamicReduction() + cha.getReductionAromr());
		}
	}

	@Override
	public void toBuffStop(object o) {
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o) {
		if (o instanceof Character) {
			Character cha = (Character) o;		
			cha.setDynamicReduction(cha.getDynamicReduction() - cha.getReductionAromr());
		}
		ChattingController.toChatting(o, "리덕션 아머 종료", Lineage.CHATTING_MODE_MESSAGE);
	}
	
	@Override
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min)
			ChattingController.toChatting(o, "\\fY리덕션 아머: " + getTime() + "초 후 종료", Lineage.CHATTING_MODE_MESSAGE);
	}

	/**
	 * 
	 * @param cha
	 * @param skill
	 * @param object_id
	 * @param x
	 * @param y
	 */
	static public void init(Character cha, Skill skill) {
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		
		if(SkillController.isMagic(cha, skill, true)) {
			cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill.getCastGfx()), true);
			BuffController.append(cha, ReductionArmor.clone(BuffController.getPool(ReductionArmor.class), skill, skill.getBuffDuration()));
			ChattingController.toChatting(cha, "리덕션 아머: 대미지 감소+1, 50레벨 이후 5레벨 당 대미지 감소+1씩 증가", Lineage.CHATTING_MODE_MESSAGE);
		}
	}

}
