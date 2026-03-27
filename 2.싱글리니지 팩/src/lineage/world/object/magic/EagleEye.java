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

public class EagleEye extends Magic {
	
	public EagleEye(Skill skill){
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new EagleEye(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
	
	@Override
	public void toBuffStart(object o) {
		if (o instanceof Character) {
			Character cha = (Character) o;
			cha.setDynamicBowCritical(cha.getDynamicBowCritical() + 2);
		}		
	}
	
	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		if (o instanceof Character) {
			Character cha = (Character) o;
			cha.setDynamicBowCritical(cha.getDynamicBowCritical() - 2);
			ChattingController.toChatting(cha, "\\fY이글 아이 종료", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	@Override
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min)
			ChattingController.toChatting(o, "\\fY이글 아이: " + getTime() + "초 후 종료", Lineage.CHATTING_MODE_MESSAGE);
	}
	
	static public void init(Character cha, Skill skill){
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		
		if (cha.getInventory().getSlot(Lineage.SLOT_WEAPON) == null || !cha.getInventory().getSlot(Lineage.SLOT_WEAPON).getItem().getType2().equalsIgnoreCase("bow")) {
			ChattingController.toChatting(cha, "\\fY활을 착용해야 사용 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		
		if(SkillController.isMagic(cha, skill, true)) {
			cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill.getCastGfx()), true);
			// 처리.
			BuffController.append(cha, EagleEye.clone(BuffController.getPool(EagleEye.class), skill, skill.getBuffDuration()));
			ChattingController.toChatting(cha, "이글 아이: 원거리 치명타+2%", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
}
