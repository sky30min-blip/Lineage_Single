package lineage.world.object.npc.background.door;

import lineage.world.controller.DogRaceController;

public class DogRaceDoor extends Door {

	@Override
	public void toTeleport(final int x, final int y, final int map, final boolean effect){
		super.toTeleport(x, y, map, effect);
		
		DogRaceController.appendDoor( this );
	}
	
}
