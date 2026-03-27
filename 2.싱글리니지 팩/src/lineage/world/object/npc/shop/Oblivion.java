package lineage.world.object.npc.shop;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_ShopBuy;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.ShopInstance;

public class Oblivion extends ShopInstance {

	public Oblivion(Npc npc) {
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "oblivion1"));

	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (action.equalsIgnoreCase("buy")) {
			pc.toSender(S_ShopBuy.clone(BasePacketPooling.getPool(S_ShopBuy.class), this));
		} else if (pc.getInventory().isAden(20000, true)) {
			if (action.equalsIgnoreCase("oblivionT")) {
				pc.toTeleport(33427, 33483, 4, true);
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, null));
			}
		} else {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "oblivion9"));
		}
	}
}