package lineage.world.object.npc.guard;

import lineage.bean.database.Npc;
import lineage.share.Lineage;
import lineage.world.controller.KingdomController;
import lineage.world.object.npc.kingdom.DwarfcastleGuard;

public class Dwarfcastle_Guard extends DwarfcastleGuard {

	public Dwarfcastle_Guard(Npc npc){
		super(npc, null);
		kingdom = KingdomController.find(Lineage.KINGDOM_ABYSS);
		html = "Dwarfcastle";
	}
}

