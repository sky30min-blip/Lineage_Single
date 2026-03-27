package lineage.world.object.npc.kingdom;

import lineage.bean.database.Npc;
import lineage.share.Lineage;
import lineage.world.controller.KingdomController;

public class Orville extends KingdomChamberlain {
	
	public Orville(Npc npc){
		super(npc);
		kingdom = KingdomController.find(Lineage.KINGDOM_GIRAN);
		html = "orville";
	}

}
