package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.bean.lineage.Party;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_BuffElf;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.PartyController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class EyeOfStorm extends Magic {
	
	public EyeOfStorm(Skill skill){
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new EyeOfStorm(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
		
	@Override
	public void toBuffStart(object o){
		Character cha = (Character) o;
		cha.setBuffEyeOfStorm(true);
		cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() + 3);
		cha.setDynamicAddHitBow(cha.getDynamicAddHitBow() + 2);
		toBuffUpdate(o);
	}
	
	@Override
	public void toBuffUpdate(object o) {
		o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
		o.toSender(S_BuffElf.clone(BasePacketPooling.getPool(S_BuffElf.class), 155, skill.getBuffDuration()));
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		Character cha = (Character) o;
		o.setBuffEyeOfStorm(false);
		cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() - 3);
		cha.setDynamicAddHitBow(cha.getDynamicAddHitBow() - 2);
		ChattingController.toChatting(o, "\\fY아이 오브 스톰 종료", Lineage.CHATTING_MODE_MESSAGE);
	}
	
	@Override
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min)
			ChattingController.toChatting(o, "\\fY아이 오브 스톰: " + getTime() + "초 후 종료", Lineage.CHATTING_MODE_MESSAGE);
	}
	
	static public void init(Character cha, Skill skill){
		if (cha.getInventory().getSlot(Lineage.SLOT_WEAPON) == null || !cha.getInventory().getSlot(Lineage.SLOT_WEAPON).getItem().getType2().equalsIgnoreCase("bow")) {
			ChattingController.toChatting(cha, "\\fY활을 착용해야 사용 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		
		if(SkillController.isMagic(cha, skill, true) && cha instanceof PcInstance){
			PcInstance pc = (PcInstance)cha;
			Party p = PartyController.find(pc);
			if(p == null){
				onBuff(pc, skill, skill.getBuffDuration());
			}else{
				for(PcInstance use : p.getList()){
					if(Util.isDistance(cha, use, Lineage.SEARCH_LOCATIONRANGE))
						onBuff(use, skill, skill.getBuffDuration());
				}
			}
		}
	}
	
	static public void onBuff(PcInstance pc, Skill skill, int time){
		// 중복되지않게 다른 버프 제거.
		// 윈드샷
		BuffController.remove(pc, WindShot.class);	
		BuffController.remove(pc, StormShot.class);	
		// 스톰샷 적용되어 있을경우 우선순위 적용

		
		BuffController.append(pc, EyeOfStorm.clone(BuffController.getPool(EyeOfStorm.class), skill, time));
		ChattingController.toChatting(pc, "아이 오브 스톰: 원거리 대미지+3, 원거리 명중+2", Lineage.CHATTING_MODE_MESSAGE);
	}

}
