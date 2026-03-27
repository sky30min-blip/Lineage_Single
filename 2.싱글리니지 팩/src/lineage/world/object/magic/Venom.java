package lineage.world.object.magic;

import lineage.bean.database.MonsterSkill;
import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectPoisonLock;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;

public class Venom extends Magic {
	
	public Venom(Skill skill){
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, int level, Skill skill, int time){
		if(bi == null)
			bi = new Venom(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		bi.setDamage(level);
		return bi;
	}
	
	@Override
	public void toBuffStart(object o){
		o.setPoison(true);
		o.toSender(S_ObjectPoisonLock.clone(BasePacketPooling.getPool(S_ObjectPoisonLock.class), o), true);
	}

	@Override
	public void toBuff(object o){
		if(!o.isDead() && !o.isLockHigh())
			o.setNowHp(o.getNowHp() - Util.random((int) Math.round(getDamage() * 0.08), (int) Math.round(getDamage() * 0.12)));
		
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min)
			ChattingController.toChatting(o, "\\fY인첸트베놈: " + getTime() + "초 후 종료", Lineage.CHATTING_MODE_MESSAGE);
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}


	@Override
	public void toBuffEnd(object o){
		if(o.isWorldDelete())
			return;
		o.setPoison(false);
		o.toSender(S_ObjectPoisonLock.clone(BasePacketPooling.getPool(S_ObjectPoisonLock.class), o), true);
		
		ChattingController.toChatting(o, "\\fY인첸트베놈 종료", Lineage.CHATTING_MODE_MESSAGE);
	}

	static public void init(Character cha, Skill skill, int object_id){
		// 타겟 찾기
		object o = cha.findInsideList( object_id );
		// 처리
		if(o != null){
		
			
			if(SkillController.isMagic(cha, skill, true))
				onBuff(cha, o, skill);
		}
	}

	static public void init(Character cha, int time, int level){
		BuffController.append(cha, Venom.clone(BuffController.getPool(Venom.class), level, SkillDatabase.find(656), time));
	}
	

	/**
	 * 중복코드 방지용
	 * @param cha
	 * @param o
	 * @param skill
	 */
	static public void onBuff(Character cha, object o, Skill skill){
		// 투망상태 해제
		Detection.onBuff(cha);
		// 처리
		if(SkillController.isFigure(cha, o, skill, true, false)){
			o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
			BuffController.append(o, Venom.clone(BuffController.getPool(Venom.class), cha.getLevel(), skill, skill.getBuffDuration()));
			ChattingController.toChatting(cha, "인첸트베놈: 무기에 독이 스며듭니다",Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		// \f1마법이 실패했습니다.
		cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 280));
	}
}
