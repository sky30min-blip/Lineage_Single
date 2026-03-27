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

public class movingacceleratic extends Magic {

	public movingacceleratic(Skill skill){
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new movingacceleratic(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
		
	@Override
	public void toBuffStart(object o){
		o.setBrave(true);
		if(Lineage.server_version > 200)
			o.toSender(S_ObjectSpeed.clone(BasePacketPooling.getPool(S_ObjectSpeed.class), o, 1, 1, getTime()), true);
		else
			o.toSender(S_ObjectSpeed.clone(BasePacketPooling.getPool(S_ObjectSpeed.class), o, 1, 1, getTime()), true);
	}

	@Override
	public void toBuffUpdate(object o) {
		o.setBrave(true);
		if(Lineage.server_version > 200)
			o.toSender(S_ObjectSpeed.clone(BasePacketPooling.getPool(S_ObjectSpeed.class), o, 1, 1, getTime()), true);
		else
			o.toSender(S_ObjectSpeed.clone(BasePacketPooling.getPool(S_ObjectSpeed.class), o, 1, 1, getTime()), true);
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
		ChattingController.toChatting(o, "\\fY무빙 악셀레이션 종료", Lineage.CHATTING_MODE_MESSAGE);
	}
	
	@Override
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min)
			ChattingController.toChatting(o, "\\fY무빙 악셀레이션: " + getTime() + "초 후 종료", Lineage.CHATTING_MODE_MESSAGE);
		
		if (getTime() == 1)
			o.speedCheck = System.currentTimeMillis() + 2000;
	}
	
	static public void init(Character cha, Skill skill){
		
		if(cha.getMap() == 5143){
			ChattingController.toChatting(cha, "여기서는 사용 하실 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		if(cha.getMap() == 807){
			ChattingController.toChatting(cha, "여기서는 사용 하실 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		
		if(SkillController.isMagic(cha, skill, true)){
			// 패킷 처리
			cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill.getCastGfx()), true);
			// 처리.
			if(cha.getSpeed() != 2){
				// 용기 제거
				BuffController.remove(cha, Bravery.class);
				// 슬로우 상태가 아닐경우
				BuffController.append(cha, movingacceleratic.clone(BuffController.getPool(movingacceleratic.class), skill, skill.getBuffDuration()));
				ChattingController.toChatting(cha, "무빙 악셀레이션: 속도 향상", Lineage.CHATTING_MODE_MESSAGE);
			}else{
				// 슬로우 상태일경우 슬로우 제거.
				BuffController.remove(cha, Slow.class);
			}
		}
	}
	
	static public void init(Character cha, int time){
		// 적용
		if (cha.getClassType() == Lineage.LINEAGE_CLASS_DARKELF)
			BuffController.append(cha, Bravery.clone(BuffController.getPool(Bravery.class), SkillDatabase.find(638), time));
	}

}
