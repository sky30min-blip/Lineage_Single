package lineage.world.object.npc.background;

import lineage.bean.event.DeleteObject;
import lineage.thread.EventThread;
import lineage.world.World;
import lineage.world.controller.CharacterController;
import lineage.world.object.instance.BackgroundInstance;

public class MagicFirewood extends BackgroundInstance {

	static synchronized public BackgroundInstance clone(BackgroundInstance bi) {
		if (bi == null)
			bi = new MagicFirewood();
		return bi;
	}

	private long end_time;

	@Override
	public void close() {
		super.close();
		//
		end_time = 0;
		//
		CharacterController.toWorldOut(this);
	}

	@Override
	public void toTeleport(int x, int y, int map, boolean effect) {
		// 장작 유지시간 3분
		end_time = System.currentTimeMillis() + (1000 * 60 * 30);
		// totimer를 호출하기위해 등록.
		CharacterController.toWorldJoin(this);
		//
		super.toTeleport(x, y, map, effect);
		
	}

	@Override
	public void toTimer(long time) {
		//
		if (time < end_time || isWorldDelete())
			return;
		//
		World.remove(this);
		clearList(true);
	//	System.out.println("매직파이어우드 타이머 쓰레드");
		EventThread.append(DeleteObject.clone(EventThread.getPool(DeleteObject.class), this));
	}
}
