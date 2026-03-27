package lineage.world.object.instance;

import java.sql.Connection;

import lineage.world.World;
import lineage.world.controller.InventoryController;

public class RobotInstance extends PcInstance {

	public RobotInstance() {
		super(null);
	}

	@Override
	public void toTimer(long time) {

	}

	@Override
	public void toWorldJoin() {
		InventoryController.toWorldJoin(this);

		toTeleport(getX(), getY(), getMap(), false);
	}

	@Override
	public void toWorldOut() {
		clearList(true);
		World.remove(this);

		InventoryController.toWorldOut(this);
	}

	@Override
	public void toSave(Connection con) {

	}

}
