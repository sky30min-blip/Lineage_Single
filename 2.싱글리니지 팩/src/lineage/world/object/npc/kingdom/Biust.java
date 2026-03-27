package lineage.world.object.npc.kingdom;

import lineage.bean.database.Npc;
import lineage.share.Lineage;
import lineage.world.controller.KingdomController;

public class Biust extends KingdomSoldierShop {

	public Biust(Npc n){
		super(n);
		kingdom = KingdomController.find(Lineage.KINGDOM_ADEN);
		html = "biust";
		soldier_name = "성십자단";
	}

}
