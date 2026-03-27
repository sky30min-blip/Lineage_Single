package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class ElementalFalldown extends Magic {
	
	static synchronized public BuffInterface clone(BuffInterface bi, Character cha, Skill skill, int time){
		if(bi == null)
			bi = new ElementalFalldown(skill);
		bi.setCharacter(cha);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	public ElementalFalldown(Skill skill){
		super(null, skill);
	}
	
	@Override
	public void toBuffStart(object o) {
		if(o instanceof Character) {
			Character cha = (Character)o;
			switch(getCharacter().getAttribute()){
				case Lineage.ELEMENT_EARTH:
					cha.setDynamicEarthress( (int) (cha.getDynamicEarthress() - skill.getMaxdmg()) );
					break;
				case Lineage.ELEMENT_FIRE:
					cha.setDynamicFireress( (int) (cha.getDynamicFireress() - skill.getMaxdmg()) );
					break;
				case Lineage.ELEMENT_WIND:
					cha.setDynamicWindress( (int) (cha.getDynamicFireress() - skill.getMaxdmg()) );
					break;
				case Lineage.ELEMENT_WATER:
					cha.setDynamicWaterress( (int) (cha.getDynamicFireress() - skill.getMaxdmg()) );
					break;
			}
			cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
		}
	}

	@Override
	public void toBuffStop(object o) {
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o) {
		if(o.isWorldDelete())
			return;
		if(o instanceof Character) {
			Character cha = (Character)o;
			switch(getCharacter().getAttribute()){
				case Lineage.ELEMENT_EARTH:
					cha.setDynamicEarthress( (int) (cha.getDynamicEarthress() + skill.getMaxdmg()) );
					break;
				case Lineage.ELEMENT_FIRE:
					cha.setDynamicFireress( (int) (cha.getDynamicFireress() + skill.getMaxdmg()) );
					break;
				case Lineage.ELEMENT_WIND:
					cha.setDynamicWindress( (int) (cha.getDynamicFireress() + skill.getMaxdmg()) );
					break;
				case Lineage.ELEMENT_WATER:
					cha.setDynamicWaterress( (int) (cha.getDynamicFireress() + skill.getMaxdmg()) );
					break;
			}
			cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
		}
	}

	/**
	 * 
	 * @param cha
	 * @param skill
	 * @param object_id
	 * @param x
	 * @param y
	 */
	static public void init(Character cha, Skill skill, int object_id) {
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
			
			if(SkillController.isMagic(cha, skill, true) && SkillController.isFigure(cha, o, skill, false, false)) {
				// 버프 등록
				BuffController.append(o, ElementalFalldown.clone(BuffController.getPool(ElementalFalldown.class), cha, skill, skill.getBuffDuration()));
				// 패킷 처리
				o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
			}
		}
	}

}
