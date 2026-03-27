package lineage.world.object.magic;

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
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;

public class CurseParalyze extends Magic {
	
	public CurseParalyze(Skill skill){
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new CurseParalyze(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
		
	@Override
	public void toBuffStart(object o){
		o.setBuffCurseParalyze(true);
		if(o.getCurseParalyzeCounter()<10)
			// \f1몸이 서서히 마비되어 갑니다.
			o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 212));
		// 공격당한거 알리기.
		o.toDamage(cha, 0, Lineage.ATTACK_TYPE_MAGIC);
	}
	
	@Override
	public void toBuff(object o) {
		if(o.getCurseParalyzeCounter()>=10){
			if(skill.getLock().equalsIgnoreCase("none") || skill.getLock().equalsIgnoreCase("low")){
				if(!o.isLockLow()){
					o.setLockLow(true);
					o.toSender(S_ObjectPoisonLock.clone(BasePacketPooling.getPool(S_ObjectPoisonLock.class), o), true);
					o.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x02));
				}
			}else if(skill.getLock().equalsIgnoreCase("high")){
				if(!o.isLockHigh()){
					o.setLockHigh(true);
					o.toSender(S_ObjectPoisonLock.clone(BasePacketPooling.getPool(S_ObjectPoisonLock.class), o), true);
					o.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x02));
				}
			}
		}else{
			// 완벽하게 굳기까지 대기중..
			o.setCurseParalyzeCounter(o.getCurseParalyzeCounter() + 1);
		}
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		if(o.isWorldDelete() || !o.isLock())
			return;
		o.setCurseParalyzeCounter( 0 );
		o.setBuffCurseParalyze(false);
		if(skill.getLock().equalsIgnoreCase("none") || skill.getLock().equalsIgnoreCase("low")){
			o.setLockLow(false);
		}else if(skill.getLock().equalsIgnoreCase("high")){
			o.setLockHigh(false);
		}
		o.toSender(S_ObjectPoisonLock.clone(BasePacketPooling.getPool(S_ObjectPoisonLock.class), o), true);
		o.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x00));
	}
	
	/**
	 * 사용자 시전
	 * @param cha
	 * @param skill
	 * @param object_id
	 */
	static public void init(Character cha, Skill skill, int object_id){
		// 타겟 찾기
		object o = cha.findInsideList( object_id );
		// 처리
		if(o != null){
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			
			if(SkillController.isMagic(cha, skill, true))
				onBuff(cha, o, skill, 0);
		}
	}
	
	/**
	 * 월드 접속할때 해당 객체 버프 적용하기 위한것
	 * @param cha
	 * @param time
	 */
	static public void init(Character cha, int time){
		cha.setCurseParalyzeCounter(10);
		BuffController.append(cha, CurseParalyze.clone(BuffController.getPool(CurseParalyze.class), SkillDatabase.find(5, 0), time));
	}

	/**
	 * 몬스터용
	 * @param mi
	 * @param o
	 * @param ms
	 * @param action
	 * @param effect
	 * @param check		: hp/mp 감소 체크할지 여부.
	 */
	static public void init(MonsterInstance mi, object o, MonsterSkill ms, int action, int effect, boolean check){
		if(action != -1)
			mi.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), mi, action), false);
		if(check && !SkillController.isMagic(mi, ms, true))
			return;
		if(SkillController.isFigure(mi, o, ms.getSkill(), false, false) && !o.isLock()){
			if(effect > 0)
				o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, effect), true);
			// option값을 참고로해서 굳기까지 딜레이걸 대기시간을 세팅할지 여부 처리함.
			try {
				if(ms.getOption()!=null && ms.getOption().length()>0)
					o.setCurseParalyzeCounter( Integer.valueOf(ms.getOption()) );
			} catch (Exception e) {
				o.setCurseParalyzeCounter( 0 );
			}
			// 처리.
			BuffController.append(o, CurseParalyze.clone(BuffController.getPool(CurseParalyze.class), ms.getSkill(), ms.getBuffDuration() > 0 ? ms.getBuffDuration() : ms.getSkill().getBuffDuration() + 10));
		}
	}
	
	/**
	 * 중복코드 방지용.
	 * @param cha
	 * @param o
	 * @param skill
	 */
	static public void onBuff(Character cha, object o, Skill skill, int counter){
		// 투망상태 해제
		Detection.onBuff(cha);
		// 처리
		if(SkillController.isFigure(cha, o, skill, true, false) && !o.isLock()){
			o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
			o.setCurseParalyzeCounter(counter);
			BuffController.append(o, CurseParalyze.clone(BuffController.getPool(CurseParalyze.class), skill, skill.getBuffDuration() + 10));
			return;
		}
		// \f1마법이 실패했습니다.
		cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 280));
	}
}
