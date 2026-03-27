package lineage.world.object.npc.kingdom;

import lineage.bean.database.Npc;
import lineage.share.Lineage;
import lineage.world.controller.KingdomController;

public class SeghemAtuba extends KingdomChamberlain {
	
	public SeghemAtuba(Npc npc){
		super(npc);
		kingdom = KingdomController.find(Lineage.KINGDOM_ORCISH);
		html = "seghem";
	}

}
