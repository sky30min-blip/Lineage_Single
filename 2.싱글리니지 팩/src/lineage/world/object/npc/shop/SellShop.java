package lineage.world.object.npc.shop;

import lineage.bean.database.Npc;
import lineage.network.packet.ClientBasePacket;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.ShopInstance;

public class SellShop extends ShopInstance {
	
	public SellShop(Npc npc){
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		super.toTalk(pc, "sell", null, null);
	}

}
