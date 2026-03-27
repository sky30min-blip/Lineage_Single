package lineage.world.object.npc.guard;

import lineage.bean.database.Npc;
import lineage.share.Lineage;
import lineage.world.controller.KingdomController;
import lineage.world.object.npc.kingdom.GirancastleGuard;

public class Girancastle_Guard extends GirancastleGuard {

	public Girancastle_Guard(Npc npc){
		super(npc, null);
		kingdom = KingdomController.find(Lineage.KINGDOM_GIRAN);
		html = "Heine";
	}
}

