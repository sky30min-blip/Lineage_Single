package lineage.bean.event;

public interface Event {
	
	/**
	 * 처리 함수.
	 */
	public void init();
	
	/**
	 * 종료 함수.
	 */
	public void close();
	
}
