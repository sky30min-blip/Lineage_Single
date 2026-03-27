package lineage.world.object.npc.guard;

import lineage.bean.database.Npc;
import lineage.share.Lineage;
import lineage.world.controller.KingdomController;
import lineage.world.object.npc.kingdom.WyndowoodcastleGuard;

public class Wyndowoodcastle_Guard extends WyndowoodcastleGuard {

	public Wyndowoodcastle_Guard(Npc npc){
		super(npc, null);
		kingdom = KingdomController.find(Lineage.KINGDOM_KENT);
		html = "Kentcastle";
	}
}

