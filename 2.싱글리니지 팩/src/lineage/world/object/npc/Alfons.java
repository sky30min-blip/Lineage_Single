package lineage.world.object.npc;

import java.util.ArrayList;
import java.util.List;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.controller.WantedController;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class Alfons extends object {

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		List<String> list = new ArrayList<String>();

		list.add(String.format("%s", pc.getName()));
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "alfons1", null, list));
	}
}