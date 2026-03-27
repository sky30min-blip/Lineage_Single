package lineage.world.object.npc.teleporter;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.TeleportInstance;

public class MarketTeleporter extends TeleportInstance {

	public MarketTeleporter(Npc npc) {
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		switch (getNpc().getNameIdNumber()) {
		case 8572:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "grtztele"));
			break;
		case 8573:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gltztele"));
			break;
		case 8574:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "sktztele"));
			break;
		case 8575:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ortztele"));
			break;
		}
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
	    switch (getNpc().getNameIdNumber()) {
	        case 8572:
	            if (action.equalsIgnoreCase("teleportURL")) {
	                pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "grtztele1"));
	            } else if (action.equalsIgnoreCase("teleport giran-giran-trade-zone")) {
	                pc.toPotal(32724, 32841, 350);
	            }
	            break;

	        case 8573:
	            if (action.equalsIgnoreCase("teleportURL")) {
	                pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gltztele1"));
	            } else if (action.equalsIgnoreCase("teleport gludio-gludin-trade-zone")) {
	                pc.toPotal(32786, 32818, 340);
	            }
	            break;

	        case 8574:
	            if (action.equalsIgnoreCase("teleportURL")) {
	                pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "sktztele1"));
	            } else if (action.equalsIgnoreCase("teleport silver-silver-trade-zone")) {
	                pc.toPotal(32733, 32792, 370);
	            }
	            break;

	        case 8575:
	            if (action.equalsIgnoreCase("teleportURL")) {
	                pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ortztele1"));
	            } else if (action.equalsIgnoreCase("teleport oren-oren-trade-zone")) {
	                pc.toPotal(32733, 32807, 360);
	            }
	            break;
	    }
	}
}
