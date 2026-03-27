package lineage.bean.lineage;

import java.util.ArrayList;
import java.util.List;

public class Rank {

	private Integer num;				// 순번
	private String type;				// 종류가 군주인지 기사인지 구분용.
	private Long time;					// 정보를 기록한 시간.
	private List<String> list;			// 랭킹 목록 문자열로 정의.	ex)55|abcd
	
	public Rank() {
		list = new ArrayList<String>();
	}
	
	public void close() {
		list.clear();
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public List<String> getList() {
		return list;
	}

}
