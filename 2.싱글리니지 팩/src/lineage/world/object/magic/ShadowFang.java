package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemWeaponInstance;

public class ShadowFang extends Magic {
	
	public ShadowFang(Skill skill){
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new ShadowFang(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
		
	@Override
	public void toBuffStart(object o){
		o.setBuffShadowFang(true);
		if (o instanceof Character) {
			Character cha = (Character) o;
			cha.setDynamicAddDmg(cha.getDynamicAddDmg() + 5);


			cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
		}
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		o.setBuffShadowFang(false);
		if (o.isWorldDelete())
			return;
		if (o instanceof Character) {
			Character cha = (Character) o;
			cha.setDynamicAddDmg(cha.getDynamicAddDmg() - 5);

			cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
			ChattingController.toChatting(o, "\\fY쉐도우 팽 종료", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min)
			ChattingController.toChatting(o, "\\fY쉐도우 팽: " + getTime() + "초 후 종료", Lineage.CHATTING_MODE_MESSAGE);
	}
	static public void init(Character cha, Skill skill, int object_id){
		// 타겟 찾기
		object o = cha.getInventory().value(object_id);
		// 처리
		if(o != null){
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			
			if(SkillController.isMagic(cha, skill, true) && o instanceof ItemWeaponInstance){
				// 버프 등록
				BuffController.append(o, ShadowFang.clone(BuffController.getPool(ShadowFang.class), skill, skill.getBuffDuration()));
				
				// 패킷 처리
				cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill.getCastGfx()), true);
			}
		}
	}

	static public void init(Character cha, Skill skill){

		// 처리

			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			
			if (SkillController.isMagic(cha, skill, true)){
				// 버프 등록
				BuffController.append(cha, ShadowFang.clone(BuffController.getPool(ShadowFang.class), skill, skill.getBuffDuration()));
				
				// 패킷 처리
				cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill.getCastGfx()), true);
				
				ChattingController.toChatting(cha, "쉐도우 팽: 근거리 대미지 +5",
						Lineage.CHATTING_MODE_MESSAGE);
			
		}
	}
}
