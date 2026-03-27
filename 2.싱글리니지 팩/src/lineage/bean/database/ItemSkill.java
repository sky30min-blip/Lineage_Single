package lineage.bean.database;

public class ItemSkill {
	private String name;
	private String item;
	private int skillUid;
	private int enLevel;
	private int defaultProbability;
	private int addEnchantProbability;
	private boolean setInt;
	private boolean effectTarget; // true: target, false: me
	private double rateDmg;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public int getSkillUid() {
		return skillUid;
	}

	public void setSkillUid(int skillUid) {
		this.skillUid = skillUid;
	}
	
	public int getEnLevel() {
		return enLevel;
	}

	public void setEnLevel(int enLevel) {
		this.enLevel = enLevel;
	}

	public int getDefaultProbability() {
		return defaultProbability;
	}

	public void setDefaultProbability(int defaultProbability) {
		this.defaultProbability = defaultProbability;
	}

	public int getAddEnchantProbability() {
		return addEnchantProbability;
	}

	public void setAddEnchantProbability(int addEnchantProbability) {
		this.addEnchantProbability = addEnchantProbability;
	}

	public boolean isSetInt() {
		return setInt;
	}

	public void setSetInt(boolean setInt) {
		this.setInt = setInt;
	}

	public boolean isEffectTarget() {
		return effectTarget;
	}

	public void setEffectTarget(boolean effectTarget) {
		this.effectTarget = effectTarget;
	}
	
	public double getRateDmg() {
		return rateDmg;
	}

	public void setRateDmg(double rateDmg) {
		this.rateDmg = rateDmg;
	}
}
