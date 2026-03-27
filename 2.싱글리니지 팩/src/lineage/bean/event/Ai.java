package lineage.bean.event;

import lineage.world.object.object;

public class Ai implements Event {

	private object o;
	private long time;
	private int uid;

	/**
	 * 풀링에서 객체를 꺼냈는데 해당 객체가 null일수 있음. 그래서 이곳에서 동적으로 생성.
	 * 
	 * @param e
	 * @param c
	 * @return
	 */
	static synchronized public Event clone(Event e, object o, long time, int uid) {
		if (e == null)
			e = new Ai();
		((Ai) e).o = o;
		((Ai) e).time = time;
		((Ai) e).uid = uid;
		return e;
	}

	@Override
	public void init() {
		try {
			o.thread_uid = uid;
			o.toAi(time);
		} catch (Exception e) {
			//lineage.share.System.printf("lineage.bean.event.Ai.init()\r\n : %s - %d - %s - 맵번호: %d\r\n", e, o.getAiStatus(), o.getName(), o.getMap());
			// 에러낫을경우 해당 객체 제거처리.
			o.toAiThreadDelete();
		}
	}

	@Override
	public void close() {
		// 할거 없음..
	}

}
