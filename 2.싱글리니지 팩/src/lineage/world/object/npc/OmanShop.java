package lineage.world.object.npc;

import lineage.bean.database.Npc;
import lineage.network.packet.ClientBasePacket;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.ShopInstance;

public class OmanShop extends ShopInstance {

	public OmanShop(Npc npc) {
		super(npc);
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){

		super.toTalk(pc, "buy", null, null);
	}
}
