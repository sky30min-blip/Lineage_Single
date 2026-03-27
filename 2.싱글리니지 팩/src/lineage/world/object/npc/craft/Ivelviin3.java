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
import lineage.world.object.instance.CraftInstance;
import lineage.world.object.instance.PcInstance;

public class Ivelviin3 extends CraftInstance {

	public Ivelviin3(Npc npc) {
		super(npc);

		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add(npc.getNameId());
		// 여기보시면 마갑주라는게없죠? 그래서 반응이 안되는거에요
		// 여기서 추가해주면
		//
		// 제작 처리 초기화.
		Item i = ItemDatabase.find("스냅퍼 용사의 반지");
		if (i != null) {
			craft_list.put("스냅퍼용사의반지0", i);

			List<Craft> l = new ArrayList<Craft>();
			
			l.add(new Craft(ItemDatabase.find("스냅퍼 용사의 반지"), 0, 1, 1));
			l.add(new Craft(ItemDatabase.find("스냅퍼 용사의 반지"), 0, 1, 1));
			l.add(new Craft(ItemDatabase.find("코인"), 0, 1, 5));
			list.put(i, l);
		}
		
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "kuberam16"));
	}

}
