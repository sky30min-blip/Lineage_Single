package lineage.bean.database;

public class MonsterSkill extends Skill {
	private String monster;		// 몬스터
	private int actionNumber;	// 취할 액션 번호
	private String type;		// 마법 종류
	private int chance;			// 100분율의 확률값.
	private String msg;			// 마법 시전시 표현할 멘트.
	private Skill skill;		// 버프종류 마법때문에 필요하게된 변수.
	private String option;		// 별도로 함께 사용될 옵션정보 변수.
	public String getMonster() {
		return monster;
	}
	public void setMonster(String monster) {
		this.monster = monster;
	}
	public int getActionNumber() {
		return actionNumber;
	}
	public void setActionNumber(int actionNumber) {
		this.actionNumber = actionNumber;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getChance() {
		return chance;
	}
	public void setChance(int chance) {
		this.chance = chance;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Skill getSkill() {
		return skill;
	}
	public void setSkill(Skill skill) {
		this.skill = skill;
	}
	public String getOption() {
		return option;
	}
	public void setOption(String option) {
		this.option = option;
	}
	
}
