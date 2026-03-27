package lineage.world.object.npc.background;

import java.util.ArrayList;
import java.util.List;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.instance.BackgroundInstance;
import lineage.world.object.instance.PcInstance;

public class Lawful extends BackgroundInstance {

	private List<String> list_html;

	public Lawful() {
		list_html = new ArrayList<String>();
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {

		if (getGfx() == 780) {
		// 라우풀신전
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lawfultp1", null, list_html));
    	}
  }

}