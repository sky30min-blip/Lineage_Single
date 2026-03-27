package lineage.world.object.npc.teleporter;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.TeleportInstance;

public class Luck extends TeleportInstance {

	public Luck(Npc npc) {
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "luck1"));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
	    if (action.equalsIgnoreCase("teleportURL")) {
	        pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "luck2"));
	    } else if (action.equalsIgnoreCase("teleport escape-forgotten-island")) {
	        if (pc.getInventory().isAden(7, true)) {
	        	 pc.toPotal(33599, 33252, 4);
	        } else {
	            pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "luck9"));
	        }
	    }
	}
}