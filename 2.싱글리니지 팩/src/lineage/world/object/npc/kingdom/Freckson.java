package lineage.world.object.npc.kingdom;

import lineage.bean.database.Npc;
import lineage.share.Lineage;
import lineage.world.controller.KingdomController;

public class Freckson extends KingdomSoldierShop {

	public Freckson(Npc n){
		super(n);
		kingdom = KingdomController.find(Lineage.KINGDOM_ABYSS);
		html = "freckson";
		soldier_name = "강철의 문";
	}

}
