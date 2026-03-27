package lineage.world.object.npc.event;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.util.Util;
import lineage.world.controller.BuffController;
import lineage.world.object.instance.EventInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.ShapeChange;

public class FishingBoy extends EventInstance {

	public FishingBoy(Npc npc) {
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		if (getMap() == 4)
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "fk_in_1"));
		else
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "fk_out_1"));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (action.startsWith("a"))
			in_fishing(pc, false);
		else
			out_fishing(pc);
	}

	/**
	 * 낚시터 입장 
	 */
	private void in_fishing(PcInstance pc, boolean type) {
		if (pc.getLevel() >= 15) {
			if (pc.getInventory().isAden(1000, true)) {
				
				BuffController.remove(pc, ShapeChange.class);
				BuffController.removeAll(pc);
				
				int i = Util.random(0, 3);
				switch (i) {
				case 0:
					pc.toTeleport(32812, 32809, 5124, true);
					break;
				case 1:
					pc.toTeleport(32793, 32813, 5124, true);
					break;
				case 2:
					pc.toTeleport(32786, 32797, 5124, true);
					break;
				case 3:
					pc.toTeleport(32803, 32785, 5124, true);
					break;
				}
			} else {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "fk_in_ad"));
			}
		} else {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "fk_in_lv"));
		}
	}

	private void out_fishing(PcInstance pc) {
		pc.toTeleport(32607, 32773, 4, true);
	}
}
