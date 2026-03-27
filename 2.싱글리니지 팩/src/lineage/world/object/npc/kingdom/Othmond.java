package lineage.world.object.npc.kingdom;

import lineage.bean.database.Npc;
import lineage.share.Lineage;
import lineage.world.controller.KingdomController;

public class Othmond extends KingdomChamberlain {
	
	public Othmond(Npc npc){
		super(npc);
		kingdom = KingdomController.find(Lineage.KINGDOM_WINDAWOOD);
		html = "othmond";
	}
	
}
