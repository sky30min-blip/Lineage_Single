package lineage.world.object.npc.shop;

import lineage.bean.database.Npc;
import lineage.share.Lineage;
import lineage.world.controller.KingdomController;
import lineage.world.object.instance.SlimeraceInstance;

public class Gora extends SlimeraceInstance {
	
	public Gora(Npc npc){
		super(npc);
		kingdom = KingdomController.find(Lineage.KINGDOM_ORCISH);
	}

}
