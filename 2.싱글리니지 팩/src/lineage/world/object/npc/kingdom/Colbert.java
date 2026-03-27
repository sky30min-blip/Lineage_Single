package lineage.world.object.npc.kingdom;

import lineage.bean.database.Npc;
import lineage.share.Lineage;
import lineage.world.controller.KingdomController;

public class Colbert extends KingdomSoldierShop {

	public Colbert(Npc n){
		super(n);
		kingdom = KingdomController.find(Lineage.KINGDOM_GIRAN);
		html = "colbert";
		soldier_name = "'황금성'단";
	}

}
