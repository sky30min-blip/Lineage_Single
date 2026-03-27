package lineage.world.object.item;

import lineage.world.object.instance.ItemInstance;

public class RaceTicket extends ItemInstance {

	private int RaceUid;				// 레이스가 진행된던 uid 값.
	private int RacerIdx;				// 레이서에 고유 idx 값.
	private String RacerName;		// 레이서의 이름.
	private String RacerType;			// 구분용. 슬경인지 개경인지
	private String db;					// 디비에 기록될때 사용되는 값.
	
	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new RaceTicket();
		return item;
	}

	@Override
	public void close(){
		super.close();
		RaceUid = RacerIdx = 0;
		RacerName = db = RacerType = null;
	}

	public int getRaceUid() {
		return RaceUid;
	}

	public void setRaceUid(int RaceUid) {
		this.RaceUid = RaceUid;
	}

	public int getRacerIdx() {
		return RacerIdx;
	}

	public void setRacerIdx(int RacerIdx) {
		this.RacerIdx = RacerIdx;
	}

	public String getRacerName() {
		return RacerName;
	}

	public void setRacerName(String RacerName) {
		this.RacerName = RacerName;
	}
	
	public String getRacerType() {
		return RacerType;
	}
	
	public void setRaceType(String RaceType) {
		this.RacerType = RaceType;
	}
	
	@Override
	public String getName() {
		// uid-idx name
		return String.format("%d-%d %s", RaceUid, RacerIdx, RacerName);
	}
	
	@Override
	public String getRaceTicket(){
		if(db == null)
			db = String.format("%s %s", getName(), RacerType);
		return db;
	}
	
	@Override
	public void setRaceTicket(String ticket){
		db = ticket;

		int pos = db.indexOf("-");
		int pos2 = db.indexOf(" ");
		int pos3 = db.lastIndexOf(" ");
		RaceUid = Integer.valueOf(db.substring(0, pos));
		RacerIdx = Integer.valueOf(db.substring(pos+1, pos2));
		try {
			RacerName = db.substring(pos2+1, pos3);
			RacerType = db.substring(pos3).trim();
		} catch (Exception e) {
			RacerName = db.substring(pos2).trim();
			RacerType = "slime";
			db += " slime";
		}
	}
	
}
