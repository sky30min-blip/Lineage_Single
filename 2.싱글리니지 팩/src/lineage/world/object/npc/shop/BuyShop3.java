package lineage.world.object.npc.shop;

import lineage.bean.database.Npc;
import lineage.database.NpcSpawnlistDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.controller.KingdomController;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.ShopInstance;

public class BuyShop3 extends ShopInstance {
	
	public BuyShop3(Npc npc){
		super(npc);
		kingdom = KingdomController.find(Lineage.KINGDOM_GIRAN);
	}
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "yadoshop2"));
		
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		
		pc.setTempShop(null);

		if (action.equalsIgnoreCase("a")) {
			
			object shop = NpcSpawnlistDatabase.무기상인;
			
				if (shop != null){
					shop.toTalk(pc, null);
					pc.setTempShop(shop);
				}
			
		}
		if (action.equalsIgnoreCase("b")) {
			object shop = NpcSpawnlistDatabase.방어구상인;
			
			if (shop != null){
				shop.toTalk(pc, null);
				pc.setTempShop(shop);
			}
		}
		if (action.equalsIgnoreCase("c")) {
			object shop = NpcSpawnlistDatabase.악세상인;
			
			if (shop != null){
				shop.toTalk(pc, null);
				pc.setTempShop(shop);
			}
		}
		if (action.equalsIgnoreCase("d")) {
			object shop = NpcSpawnlistDatabase.마법서상인;
			
			if (shop != null){
				shop.toTalk(pc, null);
				pc.setTempShop(shop);
			}
		}
		if (action.equalsIgnoreCase("e")) {
			object shop = NpcSpawnlistDatabase.룬상인;
			
			if (shop != null){
				shop.toTalk(pc, null);
				pc.setTempShop(shop);
			}
		}
		if (action.equalsIgnoreCase("f")) {
			object shop = NpcSpawnlistDatabase.주문서상인;
			
			if (shop != null){
				shop.toTalk(pc, null);
				pc.setTempShop(shop);
			}
		}
		
//		super.toTalk(pc, "buy", null, null);
	}

}
