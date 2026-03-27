package lineage.network.packet;

import java.util.ArrayList;
import java.util.List;

import lineage.Main;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.util.Util;

public final class BasePacketPooling {

	// 동적 생성된 패킷처리용 객체 담을 용도.
	static private List<BasePacket> pool;
	
	static public void init(){
		TimeLine.start("BasePacketPooling..");
		
		pool = new ArrayList<BasePacket>();
		
		TimeLine.end();
	}
	
	/**
	 * 풀목록에서 찾아서 리턴.
	 * @param c
	 * @return
	 */
	static public BasePacket getPool(Class<?> c) {
		if (Lineage.pool_basePacket) {
			synchronized (pool) {
				BasePacket bp = find(c);
				// 꺼낸 객체 목록에서 제거.
				if (bp != null)
					pool.remove(bp);

				return bp;
			}
		}
		return null;
	}
	static public BasePacket getPool2(Class<?> c) {
	
			synchronized (pool) {
				BasePacket bp = find(c);
				// 꺼낸 객체 목록에서 제거.
				if (bp != null)
					pool.remove(bp);

				return bp;
			}
	
	}
	static public void setPool(BasePacket bp) {
		if (Lineage.pool_basePacket) {
			synchronized (pool) {
				if (Main.running && Util.isPoolAppend(pool) && pool.contains(bp) == false)
					pool.add(bp);
				else
					bp = null;
			}
		}
	}
	
	/**
	 * 해당하는 객체 찾아서 리턴.
	 * @param c
	 * @return
	 */
	static private BasePacket find(Class<?> c) {
		for (BasePacket b : pool) {
			if (b.getClass().equals(c))
				return b;
		}
		return null;
	}
	
	static public int getPoolSize() {
		return pool.size();
	}
	
}
