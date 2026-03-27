package lineage.world.object.npc.kingdom;

import lineage.bean.database.Npc;
import lineage.share.Lineage;
import lineage.world.controller.KingdomController;

public class Halt extends KingdomSoldierShop {

	public Halt(Npc n){
		super(n);
		kingdom = KingdomController.find(Lineage.KINGDOM_WINDAWOOD);
		html = "halt";
		soldier_name = "'폭풍의 칼날'단";
	}

}
