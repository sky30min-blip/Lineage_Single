package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.share.Lineage;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.BackgroundInstance;

public class Magic implements BuffInterface {

	protected Character cha; // 시전자
	protected Skill skill; // 마법종류
	protected int time_end; // 버프 종료될 시간값.
	protected BackgroundInstance effect; // 스턴과 같은 이팩트
	protected int damage; // 커스:포이즌의 대미지

	public Magic(Character cha, Skill skill) {
		this.cha = cha;
		this.skill = skill;
	}

	@Override
	public Skill getSkill() {
		return skill;
	}

	@Override
	public void setSkill(Skill skill) {
		this.skill = skill;
	}

	@Override
	public void setTime(int time) {
		time_end = time;
	}

	@Override
	public int getTime() {
		return time_end;
	}
	
	@Override
	public void setEffect(BackgroundInstance effect) {
		this.effect = effect;
		
	}

	@Override
	public BackgroundInstance getEffect() {
		return effect;
	}

	@Override
	public void setCharacter(Character cha) {
		this.cha = cha;
	}

	@Override
	public Character getCharacter() {
		return cha;
	}

	@Override
	public boolean isBuff(long time) {
		if (time_end > 0) {
			if (this instanceof Exp_Potion) {
				if (cha.getMap() != Lineage.teamBattleMap)
				--time_end;
			} else {
				--time_end;
			}
		}
		return time_end == 0 ? false : true;
	}

	@Override
	public void close() {
		cha = null;
		skill = null;
		time_end = 0;
	}

	@Override
	public void toBuffStart(object o) {
	}

	@Override
	public void toBuffUpdate(object o) {
	}

	@Override
	public void toBuff(object o) {
	}

	@Override
	public void toBuffStop(object o) {
	}

	@Override
	public void toBuffEnd(object o) {
	}

	@Override
	public boolean equal(BuffInterface bi) {
		// 헤이스트와 그레이트 헤이스트는 같은 스킬로 간주.
		if (this instanceof Haste && bi instanceof Haste)
			return true;
		// 스킬 uid가 같다면 같은 스킬로 간주.
		return getSkill().getUid() == bi.getSkill().getUid();
	}

	@Override
	public void setTime(int time, boolean restart) {
	}

	@Override
	public void setDamage(int damage) {
		this.damage = damage;		
	}

	@Override
	public int getDamage() {
		return damage;
	}

}
