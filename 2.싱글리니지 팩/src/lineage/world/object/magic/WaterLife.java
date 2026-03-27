package lineage.world.object.magic;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.bean.lineage.Clan;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.ClanController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class WaterLife extends Magic {

	public WaterLife(Skill skill){
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new WaterLife(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		o.setBuffWaterLife( true );
		toBuffUpdate(o);
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		o.setBuffWaterLife( false );
		ChattingController.toChatting(o, "\\fY워터 라이프 종료", Lineage.CHATTING_MODE_MESSAGE);
	}
	
	@Override
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min)
			ChattingController.toChatting(o, "\\fY워터 라이프: " + getTime() + "초 후 종료", Lineage.CHATTING_MODE_MESSAGE);
	}
	
	static public void init(Character cha, Skill skill, long object_id) {
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
				o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
				BuffController.append(o, WaterLife.clone(BuffController.getPool(WaterLife.class), skill, skill.getBuffDuration()));
				
				ChattingController.toChatting(cha, "워터 라이프: 힐 계열 마법의 효과 2배 1회 적용", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}
	static public void init2(Character cha, Skill skill, long object_id) {
		// 초기화


		// 처리


			if(SkillController.isMagic(cha, skill, true)) {
				
				if (  cha.getClanId() > 0) {
					PcInstance elf = (PcInstance) cha;
					List<object> list_temp = new ArrayList<object>();
					list_temp.add(elf);
					// 혈맹원 추출.
					Clan c = ClanController.find(elf);
					if (c != null) {
						for (PcInstance pc : c.getList()) {
							if (!list_temp.contains(pc) && Util.isDistance(cha, pc, 8))
								list_temp.add(pc);
						}
					}
					cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
					
					// 처리.
					for (object o : list_temp){
						o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
						BuffController.append(o, WaterLife.clone(BuffController.getPool(WaterLife.class), skill, skill.getBuffDuration()));
					}
						
				
					
					ChattingController.toChatting(cha, "워터 라이프: 힐 계열 마법의 효과 2배 1회 적용", Lineage.CHATTING_MODE_MESSAGE);
				}
				if( cha.getClanId() < 1){
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
							o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
							BuffController.append(o, WaterLife.clone(BuffController.getPool(WaterLife.class), skill, skill.getBuffDuration()));
							
							ChattingController.toChatting(cha, "워터 라이프: 힐 계열 마법의 효과 2배 1회 적용", Lineage.CHATTING_MODE_MESSAGE);
						}
					}
				}
		
			
			
		}
	}
	
	
	
}
