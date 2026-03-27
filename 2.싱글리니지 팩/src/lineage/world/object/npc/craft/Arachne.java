package lineage.world.object.npc.craft;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.lineage.Craft;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.CraftController;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.npc.guard.ElvenGuard;

public class Arachne extends ElvenGuard {

	public Arachne(Npc npc) {
		super(npc);

		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add(npc.getNameId());

		// 제작 처리 초기화.
		Item i = ItemDatabase.find("아라크네의 거미줄");
		if (i != null) {
			craft_list.put("1", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("엔트의 줄기"), 2));
			list.put(i, l);
		}

		i = ItemDatabase.find("아라크네의 허물");
		if (i != null) {
			craft_list.put("request ecdysis of arachne", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("엔트의 껍질"), 3));
			list.put(i, l);
		}

		i = ItemDatabase.find("실");
		if (i != null) {
			craft_list.put("request thread", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("판의 갈기털"), 1));
			list.put(i, l);
		}

		i = ItemDatabase.find("미스릴 실");
		if (i != null) {
			craft_list.put("request mithril thread", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("미스릴"), 5));
			l.add(new Craft(ItemDatabase.find("실"), 1));
			list.put(i, l);
		}

	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		super.toTalk(pc, cbp);

		if (pc.getClassType() == Lineage.LINEAGE_CLASS_ELF || pc.getGm() > 0) {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "arachnee1"));
		} else {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "arachnem1"));
		}
	}

	@Override
	protected void toGatherUp(PcInstance pc) {
		if (Util.random(0, 100) < 30) {
			Item i = craft_list.get("1");
			List<Craft> l = list.get(i);
			// 엔트줄기 2개 -> 아라크네의 거미줄 1개
			if (CraftController.isCraft(this, l, false)) {
				// 제작할 수 있는 최대갯수 추출.
				int count = CraftController.getMax(this, l);
				if (count > 0) {
					// 재료 제거
					for (int j = 0; j < count; ++j)
						CraftController.toCraft(this, l);
					// 지급.
					CraftController.toCraft(this, pc, i, count, true);
				}
			}
		}
	}

}
