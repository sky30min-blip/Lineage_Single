package lineage.world.object.npc.guard;

import lineage.bean.database.Npc;
import lineage.share.Lineage;
import lineage.world.controller.KingdomController;
import lineage.world.object.npc.kingdom.HeineGuard;

public class Heine_Guard extends HeineGuard {

	public Heine_Guard(Npc npc){
		super(npc, null);
		kingdom = KingdomController.find(Lineage.KINGDOM_HEINE);
		html = "Heine";
	}
}

