package lineage.bean.lineage;

import lineage.util.Util;

public class Board {
	private int uid;
	private String type;
	private String accountId;
	private String name;
	private String subject;
	private String memo;
	private long days;
	
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getDays() {
		return days;
	}
	public void setDays(long days) {
		this.days = days;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String toStringDays(){
		int y = Util.getYear(days)-100;
		int m = Util.getMonth(days);
		int d = Util.getDate(days);
		
		StringBuffer sb = new StringBuffer();
		if(y<10)
			sb.append("0");
		sb.append(y);
		if(m<10)
			sb.append("0");
		sb.append(m);
		if(d<10)
			sb.append("0");
		sb.append(d);
		
		return sb.toString();
	}
}
