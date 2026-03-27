package lineage.bean.lineage;

public class InnKey {
	private long key;		// 방 고유 값
	private int count;		// 발급된 갯수.
	private String type;	// 룸인지 홀인지 구분용
	private long time;		// 방을 생성한 시간값.
	
	public void close(){
		time = key = 0;
	}

	public long getKey() {
		return key;
	}

	public void setKey(long key) {
		this.key = key;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	
}
