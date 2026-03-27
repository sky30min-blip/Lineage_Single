package lineage.world.object.npc.shop;

import lineage.bean.database.Npc;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.KingdomController;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.ShopInstance;

public class OrcSanta extends ShopInstance {
	
	public OrcSanta(Npc npc){
		super(npc);
		kingdom = KingdomController.find(Lineage.KINGDOM_GIRAN);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		super.toTalk(pc, "buy", null, null);
	}
}
