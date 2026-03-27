package lineage.bean.lineage;

public class Auction {
	private int agitId;
	private String type;
	private String loc;
	private int size;
	private boolean sell;
	private String agent;
	private String bidder;
	private int price;
	private int defaultPrice;
	private long day;
	public int getAgitId() {
		return agitId;
	}
	public void setAgitId(int agitId) {
		this.agitId = agitId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getLoc() {
		return loc;
	}
	public void setLoc(String loc) {
		this.loc = loc;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public boolean isSell() {
		return sell;
	}
	public void setSell(boolean sell) {
		this.sell = sell;
	}
	public String getAgent() {
		return agent;
	}
	public void setAgent(String agent) {
		this.agent = agent;
	}
	public String getBidder() {
		return bidder;
	}
	public void setBidder(String bidder) {
		this.bidder = bidder;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getDefaultPrice() {
		return defaultPrice;
	}
	public void setDefaultPrice(int defaultPrice) {
		this.defaultPrice = defaultPrice;
	}
	public long getDay() {
		return day;
	}
	public void setDay(long day) {
		this.day = day;
	}
}
