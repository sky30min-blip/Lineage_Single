package lineage.world.object.npc.shop;

import lineage.bean.database.Npc;
import lineage.share.Lineage;
import lineage.world.controller.KingdomController;
import lineage.world.object.instance.SlimeraceInstance;

public class Aaman extends SlimeraceInstance {
	
	public Aaman(Npc npc){
		super(npc);
		kingdom = KingdomController.find(Lineage.KINGDOM_ORCISH);
	}

}
