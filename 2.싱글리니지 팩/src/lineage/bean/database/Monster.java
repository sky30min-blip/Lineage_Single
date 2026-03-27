package lineage.bean.database;

import java.util.ArrayList;
import java.util.List;

public class Monster {
	private String name;
	private String nameId;
	private int nameIdNumber;
	private int gfx;
	private int gfxMode;
	private boolean isBoss;
	private String bossClass;
	private int level;
	private int hp;
	private int mp;
	private int ticHp;
	private int ticMp;
	private int str;
	private int dex;
	private int con;
	private int Int;
	private int wis;
	private int cha;
	private int mr;
	private int ac;
	private int exp;
	private int lawful;
	private String size;
	private String family;
	private int atkType;
	private int atkRange;
	private int arrowGfx;
	private boolean atkInvis;
	private boolean atkPoly;
	private boolean pickup;
	private boolean revival;
	private boolean toughskin;
	private boolean adenDrop;
	private boolean taming;
	private boolean haste;
	private boolean bravery;
	private int resistanceEarth;
	private int resistanceFire;
	private int resistanceWind;
	private int resistanceWater;
	private boolean isUndead;
	private boolean isTurnUndead;
	private int msgAtkTime;
	private int msgDieTime;
	private int msgSpawnTime;
	private int msgEscapeTime;
	private int msgWalkTime;
	private List<String> msgAtk = new ArrayList<String>();
	private List<String> msgDie = new ArrayList<String>();
	private List<String> msgSpawn = new ArrayList<String>();
	private List<String> msgEscape = new ArrayList<String>();
	private List<String> msgWalk = new ArrayList<String>();
	private List<Drop> list = new ArrayList<Drop>();
	public List<MonsterSkill> list_skill = new ArrayList<MonsterSkill>();
	private String faust;
	private int chance;
	private int effect;

	public boolean isHaste() {
		return haste;
	}

	public void setHaste(boolean haste) {
		this.haste = haste;
	}

	public boolean isBravery() {
		return bravery;
	}

	public void setBravery(boolean bravery) {
		this.bravery = bravery;
	}

	public int getNameIdNumber() {
		return nameIdNumber;
	}

	public void setNameIdNumber(int nameIdNumber) {
		this.nameIdNumber = nameIdNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNameId() {
		return nameId;
	}

	public void setNameId(String nameId) {
		this.nameId = nameId;
	}

	public int getGfx() {
		return gfx;
	}

	public void setGfx(int gfx) {
		this.gfx = gfx;
	}

	public int getGfxMode() {
		return gfxMode;
	}

	public void setGfxMode(int gfxMode) {
		this.gfxMode = gfxMode;
	}

	public boolean isBoss() {
		return isBoss;
	}

	public void setBoss(boolean isBoss) {
		this.isBoss = isBoss;
	}

	public String getBossClass() {
		return bossClass;
	}

	public void setBossClass(String bossClass) {
		this.bossClass = bossClass;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getMp() {
		return mp;
	}

	public void setMp(int mp) {
		this.mp = mp;
	}

	public int getTicHp() {
		return ticHp;
	}

	public void setTicHp(int ticHp) {
		this.ticHp = ticHp;
	}

	public int getTicMp() {
		return ticMp;
	}

	public void setTicMp(int ticMp) {
		this.ticMp = ticMp;
	}

	public int getStr() {
		return str;
	}

	public void setStr(int str) {
		this.str = str;
	}

	public int getDex() {
		return dex;
	}

	public void setDex(int dex) {
		this.dex = dex;
	}

	public int getCon() {
		return con;
	}

	public void setCon(int con) {
		this.con = con;
	}

	public int getInt() {
		return Int;
	}

	public void setInt(int i) {
		Int = i;
	}

	public int getWis() {
		return wis;
	}

	public void setWis(int wis) {
		this.wis = wis;
	}

	public int getCha() {
		return cha;
	}

	public void setCha(int cha) {
		this.cha = cha;
	}

	public int getMr() {
		return mr;
	}

	public void setMr(int mr) {
		this.mr = mr;
	}

	public int getAc() {
		return ac;
	}

	public void setAc(int ac) {
		this.ac = ac;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getLawful() {
		return lawful;
	}

	public void setLawful(int lawful) {
		this.lawful = lawful;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}

	public int getAtkType() {
		return atkType;
	}

	public void setAtkType(int atkType) {
		this.atkType = atkType;
	}

	public int getAtkRange() {
		return atkRange;
	}

	public void setAtkRange(int atkRange) {
		this.atkRange = atkRange;
	}

	public boolean isAtkInvis() {
		return atkInvis;
	}

	public void setAtkInvis(boolean atkInvis) {
		this.atkInvis = atkInvis;
	}

	public boolean isAtkPoly() {
		return atkPoly;
	}

	public void setAtkPoly(boolean atkPoly) {
		this.atkPoly = atkPoly;
	}

	public boolean isPickup() {
		return pickup;
	}

	public void setPickup(boolean pickup) {
		this.pickup = pickup;
	}

	public boolean isRevival() {
		return revival;
	}

	public void setRevival(boolean revival) {
		this.revival = revival;
	}

	public boolean isToughskin() {
		return toughskin;
	}

	public void setToughskin(boolean toughskin) {
		this.toughskin = toughskin;
	}

	public boolean isAdenDrop() {
		return adenDrop;
	}

	public void setAdenDrop(boolean adenDrop) {
		this.adenDrop = adenDrop;
	}

	public boolean isTaming() {
		return taming;
	}

	public void setTaming(boolean taming) {
		this.taming = taming;
	}

	public int getResistanceEarth() {
		return resistanceEarth;
	}

	public void setResistanceEarth(int resistanceEarth) {
		this.resistanceEarth = resistanceEarth;
	}

	public int getResistanceFire() {
		return resistanceFire;
	}

	public void setResistanceFire(int resistanceFire) {
		this.resistanceFire = resistanceFire;
	}

	public int getResistanceWind() {
		return resistanceWind;
	}

	public void setResistanceWind(int resistanceWind) {
		this.resistanceWind = resistanceWind;
	}

	public int getResistanceWater() {
		return resistanceWater;
	}

	public void setResistanceWater(int resistanceWater) {
		this.resistanceWater = resistanceWater;
	}

	public boolean isUndead() {
		return isUndead;
	}

	public void setUndead(boolean isUndead) {
		this.isUndead = isUndead;
	}

	public boolean isTurnUndead() {
		return isTurnUndead;
	}

	public void setTurnUndead(boolean isTurnUndead) {
		this.isTurnUndead = isTurnUndead;
	}

	public List<String> getMsgAtk() {
		return msgAtk;
	}

	public List<String> getMsgDie() {
		return msgDie;
	}

	public List<String> getMsgSpawn() {
		return msgSpawn;
	}

	public List<String> getMsgEscape() {
		return msgEscape;
	}

	public List<String> getMsgWalk() {
		return msgWalk;
	}

	public List<Drop> getDropList() {
		synchronized (list) {
			return list;
		}
	}

	public int getArrowGfx() {
		return arrowGfx;
	}

	public void setArrowGfx(int arrowGfx) {
		this.arrowGfx = arrowGfx;
	}

	public List<MonsterSkill> getSkillList() {
		return new ArrayList<MonsterSkill>(list_skill);
	}

	public int getMsgAtkTime() {
		return msgAtkTime;
	}

	public void setMsgAtkTime(int msgAtkTime) {
		this.msgAtkTime = msgAtkTime;
	}

	public int getMsgDieTime() {
		return msgDieTime;
	}

	public void setMsgDieTime(int msgDieTime) {
		this.msgDieTime = msgDieTime;
	}

	public int getMsgSpawnTime() {
		return msgSpawnTime;
	}

	public void setMsgSpawnTime(int msgSpawnTime) {
		this.msgSpawnTime = msgSpawnTime;
	}

	public int getMsgEscapeTime() {
		return msgEscapeTime;
	}

	public void setMsgEscapeTime(int msgEscapeTime) {
		this.msgEscapeTime = msgEscapeTime;
	}

	public int getMsgWalkTime() {
		return msgWalkTime;
	}

	public void setMsgWalkTime(int msgWalkTime) {
		this.msgWalkTime = msgWalkTime;
	}
	
	public String getFaust() {
		return this.faust;
	}

	public void setFaust(final String faust) {
		this.faust = faust;
	}

	public int getChance() {
		return this.chance;
	}

	public void setChance(final int chance) {
		this.chance = chance;
	}

	public int getEffect() {
		return this.effect;
	}

	public void setEffect(final int effect) {
		this.effect = effect;
	}
}
