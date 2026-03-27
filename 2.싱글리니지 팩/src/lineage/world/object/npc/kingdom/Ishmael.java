package lineage.world.object.npc.kingdom;

import lineage.bean.database.Npc;
import lineage.share.Lineage;
import lineage.world.controller.KingdomController;

public class Ishmael extends KingdomChamberlain {
	
	public Ishmael(Npc npc){
		super(npc);
		kingdom = KingdomController.find(Lineage.KINGDOM_KENT);
		html = "ishmael";
	}
	
}
