package lineage.bean.event;

import lineage.database.BackgroundDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.database.NpcSpawnlistDatabase;
import lineage.world.controller.MagicDollController;
import lineage.world.controller.RobotController;
import lineage.world.controller.SummonController;
import lineage.world.object.object;
import lineage.world.object.instance.BackgroundInstance;
import lineage.world.object.instance.MagicDollInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.NpcInstance;
import lineage.world.object.instance.PcRobotInstance;
import lineage.world.object.instance.PetInstance;
import lineage.world.object.instance.SoldierInstance;
import lineage.world.object.instance.SummonInstance;

public class DeleteObject implements Event {

	private object o;

	static synchronized public Event clone(Event e, object o){
		if(e == null)
			e = new DeleteObject();
		((DeleteObject)e).setObject(o);
		return e;
	}

	public void setObject(object o) {
		this.o = o;
	}
	
	@Override
	public void init() {
		//
		if(o == null)
			return;
		//
		try {
			if(o instanceof PetInstance){
				SummonController.setPetPool((PetInstance)o);
			}else if(o instanceof SoldierInstance){
				SummonController.setSoldierPool((SoldierInstance)o);
			}else if(o instanceof SummonInstance){
				SummonController.setSummonPool((SummonInstance)o);
			}else if(o instanceof MonsterInstance){
				MonsterSpawnlistDatabase.setPool((MonsterInstance)o);
			}else if(o instanceof NpcInstance){
				NpcSpawnlistDatabase.setPool((NpcInstance)o);
			}else if(o instanceof BackgroundInstance){
				BackgroundDatabase.setPool((BackgroundInstance)o);
			}else if(o instanceof MagicDollInstance){
				MagicDollController.setPoolMd((MagicDollInstance)o);
			}else if(o instanceof PcRobotInstance){
				RobotController.setPool((PcRobotInstance)o);
			}else{
				lineage.share.System.printf("%s : 객체를 재사용 하지 못함. 개발자에게 알리세요!\r\n", o.toString());
			}
		} catch (Exception e) {
			lineage.share.System.printf("lineage.bean.event.DeleteObject.init()\r\n : %s\r\n", o.toString());
		}
	}

	@Override
	public void close() {
		// 할거 없음..
	}

}
