package lineage.world.object.npc.teleporter;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.TeleportInstance;

public class MarketGuard extends TeleportInstance {

	public MarketGuard(Npc npc){
		super(npc);
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		switch (getNpc().getNameIdNumber()) {
		case 18390:
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "grtzguard"));
			break;
		case 18391:
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gltzguard"));
			break;
		case 18392:
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "sktzguard"));
			break;
		case 18393:
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ortzguard"));
			break;
		}
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
	    switch (getNpc().getNameIdNumber()) {
	        case 18390:
	            if (action.equalsIgnoreCase("teleportURL")) {
	            	pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "grtzguard1"));
	            } else if(action.equalsIgnoreCase("teleport giran-trade-zone-giran")) {
	            	pc.toPotal(33438, 32798, 4);
	            }
	            break;

	        case 18391:
	            if (action.equalsIgnoreCase("teleportURL")) {
	            	pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gltzguard1"));
	            } else if(action.equalsIgnoreCase("teleport gludin-trade-zone-gludio")) {
	            	pc.toPotal(32611, 32739, 4);
	            }
	            break;

	        case 18392:
	            if (action.equalsIgnoreCase("teleportURL")) {
	            	pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "sktzguard1"));
	            } else if(action.equalsIgnoreCase("teleport silver-trade-zone-silver")) {
	            	pc.toPotal(33077, 33388, 4);
	            }
	            break;

	        case 18393:
	            if (action.equalsIgnoreCase("teleportURL")) { //오렌
	            	pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ortzguard1"));
	            } else if(action.equalsIgnoreCase("teleport oren-trade-zone-oren")) {
	            	pc.toPotal(34062, 32278, 4);
	            }
	            break;
	    }
	}
}
