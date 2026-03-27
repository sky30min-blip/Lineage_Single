package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectSpeed;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class HolyWalk extends Magic {

	public HolyWalk(Skill skill){
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new HolyWalk(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
		
	@Override
	public void toBuffStart(object o){
		o.setBrave(true);
		if(Lineage.server_version > 200)
			o.toSender(S_ObjectSpeed.clone(BasePacketPooling.getPool(S_ObjectSpeed.class), o, S_ObjectSpeed.BRAVE, Lineage.holywalk_frame ? 4 : 0x01, getTime()), true);
		else
			o.toSender(S_ObjectSpeed.clone(BasePacketPooling.getPool(S_ObjectSpeed.class), o, S_ObjectSpeed.BRAVE, Lineage.holywalk_frame ? 0x03 : 0x01, getTime()), true);
	}

	@Override
	public void toBuffUpdate(object o) {
		o.setBrave(true);
		if(Lineage.server_version > 200)
			o.toSender(S_ObjectSpeed.clone(BasePacketPooling.getPool(S_ObjectSpeed.class), o, S_ObjectSpeed.BRAVE, Lineage.holywalk_frame ? 4 : 0x01, getTime()), true);
		else
			o.toSender(S_ObjectSpeed.clone(BasePacketPooling.getPool(S_ObjectSpeed.class), o, S_ObjectSpeed.BRAVE, Lineage.holywalk_frame ? 0x03 : 0x01, getTime()), true);
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		if(o.isWorldDelete())
			return;
		o.setBrave(false);
		o.toSender(S_ObjectSpeed.clone(BasePacketPooling.getPool(S_ObjectSpeed.class), o, 1, 0, 0), true);
		ChattingController.toChatting(o, "\\fY홀리 워크 종료", Lineage.CHATTING_MODE_MESSAGE);
	}
	
	@Override
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min)
			ChattingController.toChatting(o, "\\fY홀리 워크: " + getTime() + "초 후 종료", Lineage.CHATTING_MODE_MESSAGE);
		
		if (getTime() == 1)
			o.speedCheck = System.currentTimeMillis() + 2000;
	}
	
	static public void init(Character cha, Skill skill){
		
		if(cha.getMap() == 807){
			ChattingController.toChatting(cha, "여기서는 사용 하실 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		if(cha.getMap() == 5143) {
			ChattingController.toChatting(cha, String.format("[알림] 인형레이스중엔 사용이 불가능합니다"), Lineage.CHATTING_MODE_MESSAGE);
		}
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		
		if(SkillController.isMagic(cha, skill, true)){
			// 패킷 처리
			cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill.getCastGfx()), true);
			// 처리.
			if(cha.getSpeed() != 2){
				// 마법사 용기 제거
				BuffController.remove(cha, Bravery.class);
				// 슬로우 상태가 아닐경우
				BuffController.append(cha, HolyWalk.clone(BuffController.getPool(HolyWalk.class), skill, skill.getBuffDuration()));
				ChattingController.toChatting(cha, "홀리 워크: 이동속도 향상", Lineage.CHATTING_MODE_MESSAGE);
			}else{
				// 슬로우 상태일경우 슬로우 제거.
				BuffController.remove(cha, Slow.class);
			}
		}
	}
	
	static public void init(Character cha, int time){
		// 적용
		if (cha.getClassType() == Lineage.LINEAGE_CLASS_WIZARD)
			BuffController.append(cha, HolyWalk.clone(BuffController.getPool(HolyWalk.class), SkillDatabase.find(7, 3), time));
	}

}
