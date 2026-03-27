package lineage.world.object.npc.kingdom;

import lineage.bean.database.Npc;
import lineage.share.Lineage;
import lineage.world.controller.KingdomController;

public class Hunt extends KingdomSoldierShop {

	public Hunt(Npc n){
		super(n);
		kingdom = KingdomController.find(Lineage.KINGDOM_KENT);
		html = "hunt";
		soldier_name = "'청상어'단";
	}
	
	//
}
