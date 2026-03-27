package lineage.bean.lineage;

public class Wedding {

	private long manObjectId; // 남편 아이디
	private long girlObjectId; // 부인 아이디
	private String manName; // 남편 이름
	private String girlName; // 부인 이름
	private long dateTime; // 청혼 후 결혼한 시간

	public long getManObjectId() {
		return manObjectId;
	}

	public void setManObjectId(long manObjectId) {
		this.manObjectId = manObjectId;
	}

	public long getGirlObjectId() {
		return girlObjectId;
	}

	public void setGirlObjectId(long girlObjectId) {
		this.girlObjectId = girlObjectId;
	}

	public String getManName() {
		return manName;
	}

	public void setManName(String manName) {
		this.manName = manName;
	}

	public String getGirlName() {
		return girlName;
	}

	public void setGirlName(String girlName) {
		this.girlName = girlName;
	}

	public long getDateTime() {
		return dateTime;
	}

	public void setDateTime(long dateTime) {
		this.dateTime = dateTime;
	}
}
