package lineage.bean;

import java.util.ArrayList;
import java.util.List;

public class LogConnect implements LogInterface {
	// 케릭 생성 날자, 키용도로도 사용
	private long date;
	// 로그내용
	private List<String> list;
	
	public LogConnect(){
		list = new ArrayList<String>();
	}
	
	@Override
	public void close(){
		date = 0;
		synchronized (list) {
			list.clear();
		}
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public void append(String data) {
		synchronized (list) {
			list.add(data);
		}
	}
	
	public List<String> getList() {
		List<String> r_list = new ArrayList<String>();
		synchronized (list) {
			r_list.addAll( list );
		}
		return r_list;
	}
}
