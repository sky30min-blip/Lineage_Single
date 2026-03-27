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

public class Sarsha extends CraftInstance {
	
	public Sarsha(Npc npc){
		super(npc);
		
		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add( npc.getNameId() );
		
		// 제작 처리 초기화.
		Item i = ItemDatabase.find("얼음 여왕의 지팡이");
		if(i != null){
			craft_list.put("request enchanted wand of ice queen", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("마력을 잃은 얼음 여왕의 지팡이"), 1) );
			l.add( new Craft(ItemDatabase.find("마력의 돌"), 100) );
			l.add( new Craft(ItemDatabase.find("빈 주문서 (레벨 1)"), 2) );
			l.add( new Craft(ItemDatabase.find("빈 주문서 (레벨 2)"), 2) );
			l.add( new Craft(ItemDatabase.find("빈 주문서 (레벨 3)"), 2) );
			l.add( new Craft(ItemDatabase.find("빈 주문서 (레벨 4)"), 2) );
			l.add( new Craft(ItemDatabase.find("빈 주문서 (레벨 5)"), 2) );
			list.put(i, l);
		}
		
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "sarsha1"));
	}

}
