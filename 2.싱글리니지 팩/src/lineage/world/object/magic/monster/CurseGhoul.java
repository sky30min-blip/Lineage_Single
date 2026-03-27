package lineage.world.object.magic.monster;

import lineage.bean.database.MonsterSkill;
import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectLock;
import lineage.network.packet.server.S_ObjectPoisonLock;
import lineage.world.controller.BuffController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.magic.Magic;

public class CurseGhoul extends Magic {
	
	public CurseGhoul(Skill skill){
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new CurseGhoul(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
		
	@Override
	public void toBuffStart(object o){
		o.setBuffCurseGhoul(true);
		// 버프가 시전되면 한번만 호출되는 함수임.
		// 그렇기 때문에 이미 굳어버린 상태에서 같은 메세지를 또 뿌릴 필요가 없기때문에 10 이하일때만 표현하도록 함.
		if(o.getCurseParalyzeCounter()<10){
			// \f1몸이 서서히 마비되어 갑니다.
			o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 212));
			o.toSender(S_ObjectPoisonLock.clone(BasePacketPooling.getPool(S_ObjectPoisonLock.class), o), true);
		}
	}
	
	@Override
	public void toBuff(object o) {
		if(o.getCurseParalyzeCounter()>=10){
			if(!o.isLockLow()){
				o.setLockLow(true);
				o.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x02));
			}
		}else{
			// 완벽하게 굳기까지 대기중..
			o.setCurseParalyzeCounter( o.getCurseParalyzeCounter()+1 );
		}
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		if(o.isWorldDelete())
			return;
		o.setCurseParalyzeCounter( 0 );
		o.setBuffCurseGhoul(false);
		o.setLockLow(false);
		o.toSender(S_ObjectPoisonLock.clone(BasePacketPooling.getPool(S_ObjectPoisonLock.class), o), true);
		o.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x00));
	}
	
	/**
	 * 월드 접속할때 해당 객체 버프 적용하기 위한것
	 * @param cha
	 * @param time
	 */
	static public void init(Character cha, int time){
		cha.setCurseParalyzeCounter(0);
		BuffController.append(cha, CurseGhoul.clone(BuffController.getPool(CurseGhoul.class), SkillDatabase.find(300), time));
	}

	static public void init(MonsterInstance mi, object o, MonsterSkill ms, int action, int effect, boolean check){
		if(action != -1)
			mi.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), mi, action), false);
		if(check && !SkillController.isMagic(mi, ms, true) && !SkillController.isFigure(mi, o, ms, true, false))
			return;
		if(SkillController.isFigure(mi, o, ms.getSkill(), true, false) && !o.isLock()){
			if(effect > 0)
				o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, effect), true);
			BuffController.append(o, CurseGhoul.clone(BuffController.getPool(CurseGhoul.class), ms.getSkill(), ms.getBuffDuration() > 0 ? ms.getBuffDuration() : ms.getSkill().getBuffDuration() + 10));
		}
	}
	
}
