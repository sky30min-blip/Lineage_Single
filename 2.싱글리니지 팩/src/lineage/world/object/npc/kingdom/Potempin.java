package lineage.world.object.npc.kingdom;

import lineage.bean.database.Npc;
import lineage.share.Lineage;
import lineage.world.controller.KingdomController;

public class Potempin extends KingdomChamberlain {
	
	public Potempin(Npc npc){
		super(npc);
		kingdom = KingdomController.find(Lineage.KINGDOM_ABYSS);
		html = "potempin";
	}
	

}
