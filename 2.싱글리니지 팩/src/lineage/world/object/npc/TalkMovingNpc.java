package lineage.world.object.npc;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Npc;
import lineage.database.ItemDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.NpcInstance;
import lineage.world.object.instance.PcInstance;


public class TalkMovingNpc extends NpcInstance {

	private String talk;
	private List<String> list_pcname;

	public TalkMovingNpc(Npc npc, String talk) {
		super(npc);
		this.talk = talk;
		list_pcname = new ArrayList<String>();
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		super.toTalk(pc, cbp);

		if (Lineage.event_christmas && Util.random(0, 100) <= 1) {
			if (!list_pcname.contains(pc.getName())) {
				// 빨간양말 지급.
				pc.toGiveItem(this, ItemDatabase.newInstance(ItemDatabase.find("빨간 양말")), 1);
				// 패킷 처리.
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "christmas"));
				// 관리목록에 등록. 재지급 방지용
				list_pcname.add(pc.getName());
				return;
			}
		}

		if (talk != null)
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, talk));
	}

}
