package lineage.world.object.npc.shop;

import lineage.bean.database.Npc;
import lineage.bean.database.Shop;
import lineage.database.NpcSpawnlistDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.ShopInstance;

public class SellShop2 extends ShopInstance {
	


	public SellShop2(Npc npc) {
		super(npc);
	
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "yadoshop3"));
		
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {

		if (action.equalsIgnoreCase("c")) {
			NpcSpawnlistDatabase.sellShop.toTalk(pc, null);
		}
		if (action.equalsIgnoreCase("d")) {
			super.toTalk(pc, "sell", null, null);
		}

		
//		super.toTalk(pc, "buy", null, null);
	}

}
