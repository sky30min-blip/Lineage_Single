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
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;

public class CursePoison extends Magic {
	
	public CursePoison(Skill skill){
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, int level, Skill skill, int time){
		if(bi == null)
			bi = new CursePoison(skill);
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
	}

	static public void init(Character cha, Skill skill, int object_id){
		// 타겟 찾기
		object o = cha.findInsideList( object_id );
		// 처리
		if(o != null){
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			
			if(SkillController.isMagic(cha, skill, true))
				onBuff(cha, o, skill);
		}
	}

	static public void init(Character cha, int time, int level){
		BuffController.append(cha, CursePoison.clone(BuffController.getPool(CursePoison.class), level, SkillDatabase.find(2, 2), time));
	}
	
	/**
	 * 몬스터 용
	 */
	static public void init(MonsterInstance mi, object o, MonsterSkill ms, int action, int effect, boolean check){
		if(action != -1)
			mi.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), mi, action), true);
		if(o.isPoison()){
			return;
		}
		if(check && !SkillController.isMagic(mi, ms, true))
			return;
		
		if(SkillController.isFigure(mi, o, ms.getSkill(), true, false)){
			if(effect != 0)
				o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, effect), true);
			BuffController.append(o, CursePoison.clone(BuffController.getPool(CursePoison.class), mi.getLevel(), ms.getSkill(), ms.getBuffDuration() > 0 ? ms.getBuffDuration() : ms.getSkill().getBuffDuration()));
		}
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
			BuffController.append(o, CursePoison.clone(BuffController.getPool(CursePoison.class), cha.getLevel(), skill, skill.getBuffDuration()));
			return;
		}
		// \f1마법이 실패했습니다.
		cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 280));
	}
}
