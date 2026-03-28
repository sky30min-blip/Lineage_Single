package lineage.world.object.npc;

import java.util.List;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Html;
import lineage.world.controller.AutoHuntHtmParams;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

/**
 * 자동사냥 메인 UI — {@code autohunt.htm} + 치환 리스트(자동물약 NPC와 동일 패턴).
 */
public class AutoHuntMenuNpc extends object {

	public void showMainHtml(PcInstance pc) {
		if (pc == null)
			return;
		List<String> list = AutoHuntHtmParams.buildMainList(pc);
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "autohunt", null, list));
	}
}
