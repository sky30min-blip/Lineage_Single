package lineage.bean.database;


public class SkillRobot extends Skill {
	private String type;
	private double probability;
	private String weaponType;
	private int attribute;
	private String target;
	private int level;

	public SkillRobot(Skill s) {
		setUid(s.getUid());
		setName(s.getName());
		setSkillLevel(s.getSkillLevel());
		setSkillNumber(s.getSkillNumber());
		setMpConsume(s.getMpConsume());
		setHpConsume(s.getHpConsume());
		setItemConsume(s.getItemConsume());
		setItemConsumeCount(s.getItemConsumeCount());
		setBuffDuration(s.getBuffDuration());
		setMindmg(s.getMindmg());
		setMaxdmg(s.getMaxdmg());
		setId(s.getId());
		setCastGfx(s.getCastGfx());
		setDistance(s.getDistance());
		setRange(s.getRange());
		setLawfulConsume(s.getLawfulConsume());
		setDelay(s.getDelay());
		setLock(s.getLock());
		setPrice(s.getPrice());
		setElement(s.getElement());
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}
	
	public String getWeaponType() {
		return weaponType;
	}

	public void setWeaponType(String weaponType) {
		this.weaponType = weaponType;
	}
	
	public int getAttribute() {
		return attribute;
	}
	
	public void setAttribute(int attribute) {
		this.attribute = attribute;
	}
	
	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
}
