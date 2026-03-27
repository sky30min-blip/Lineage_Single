package lineage.bean.lineage;

import lineage.bean.database.Skill;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.BackgroundInstance;


public interface BuffInterface {

	/**
	 * 사용된 스킬에 대한객체 연결 함수
	 * @return
	 */
	public Skill getSkill();
	
	public void setSkill(Skill skill);
	
	/**
	 * 버프 적용되는 시간 세팅 함수
	 * @param time
	 */
	public void setTime(int time);
	
	/**
	 * 버프 남은 시간 리턴하는 함수.
	 * @return
	 */
	public int getTime();
	
	/**
	 * 이팩트 세팅 함수
	 * 2017-10-11
	 * by all-night
	 */
	public void setEffect(BackgroundInstance effect);
	
	/**
	 * 이팩트 리턴 함수
	 * 2017-10-11
	 * by all-night
	 */
	public BackgroundInstance getEffect();
	
	public void setDamage(int damage);
	
	public int getDamage();
	
	/**
	 * 시전자 정보 갱신처리 함수.
	 * @param cha
	 */
	public void setCharacter(Character cha);
	
	/**
	 * 시전자 정보 리턴.
	 * @return
	 */
	public Character getCharacter();
	
	/**
	 * 버프를 계속 유지해도 되는지 여부 판단 리턴.
	 * @return
	 */
	public boolean isBuff(long time);
	
	/**
	 * 버프객체가 재사용을위해 풀로들어갈때 호출됨. 사용된 변수 초기화 용.
	 */
	public void close();
	
	/**
	 * 해당 아이템이 버프에 등록된 시점에 호출되는 메서드.
	 * @param cha
	 */
	public void toBuffStart(object o);

	/**
	 * 해당 아이템이 버프에 등록됫는데 이미 존재하여 업데이트만 이뤄졌을경우 호출되는 메서드.
	 * @param cha
	 */
	public void toBuffUpdate(object o);

	/**
	 * 해당 아이템이 버프에 등록되어 주기적으로 호출되는 메서드.
	 * @param cha
	 */
	public void toBuff(object o);

	/**
	 * 해당 아이템이 버프에 등록되어 처리되는동안 인위적으로 종료하려할때 호출되는 메서드.
	 * @param cha
	 */
	public void toBuffStop(object o);

	/**
	 * 해당 아이템이 버프에 등록되어 처리되는동안 시간값이 다됫을경우 종료를 위해 호출되는 메서드.
	 * @param cha
	 */
	public void toBuffEnd(object o);
	
	/**
	 * 현재 버프와 매개변수 버프가 동일한 버프인지 확인해주는 함수.
	 * 	: 헤이스트와 그레이트헤이스트는 같은 타입.
	 * @param bi
	 * @return
	 */
	public boolean equal(BuffInterface bi);
	
	/*
	 * 리스 후 버프 중첩 되는 버그 제거
	 * */
	public void setTime(int time, boolean restart);
	
}
