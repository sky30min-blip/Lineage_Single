package lineage.bean.database;

public final class Ip {
	private String ip;
	private Integer count;
	private Long time;
	private Boolean block;
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	public Boolean getBlock() {
		return block;
	}
	public void setBlock(Boolean block) {
		this.block = block;
	}
}
