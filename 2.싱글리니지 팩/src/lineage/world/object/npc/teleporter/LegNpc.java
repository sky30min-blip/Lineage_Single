package lineage.world.object.npc.teleporter;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.TeleportInstance;

public class LegNpc extends TeleportInstance {

	public LegNpc(Npc npc) {
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		switch (getNpc().getNameIdNumber()) {
		case 834:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "daniel1"));
			break;
		case 833:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "Paul1"));
			break;
		}
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
	    switch (getNpc().getNameIdNumber()) {
	        case 834:
	            if (action.equalsIgnoreCase("teleportURL")) {
	            	pc.toPotal(32653, 32543, 4);
	            	pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));
	            }
	            break;

	        case 833:
	            if (action.equalsIgnoreCase("teleportURL")) {
					pc.toPotal(32657, 32521, 4);
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));
	            }
	            break;
	    }
	}
}
