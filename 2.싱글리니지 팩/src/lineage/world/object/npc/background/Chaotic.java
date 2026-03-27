package lineage.world.object.npc.background;

import java.util.ArrayList;
import java.util.List;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.instance.BackgroundInstance;
import lineage.world.object.instance.PcInstance;

public class Chaotic extends BackgroundInstance {

	private List<String> list_html;

	public Chaotic() {
		list_html = new ArrayList<String>();
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {

		if (getGfx() == 155) {
		// 카오틱신전
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "chaotictp1", null, list_html));
    	}
  }

}