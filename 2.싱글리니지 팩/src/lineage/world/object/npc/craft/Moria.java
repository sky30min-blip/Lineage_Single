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

public class Moria extends CraftInstance {
	
	public Moria(Npc npc){
		super(npc);
		
		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add( npc.getNameId() );
		
		// 제작 처리 초기화.
		Item i = ItemDatabase.find("마법사 옷");
		if(i != null){
			craft_list.put("request magician dress", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("최고급 사파이어"), 1) );
			l.add( new Craft(ItemDatabase.find("마력의 돌"), 25) );
			l.add( new Craft(ItemDatabase.find("하얀 옷감"), 2) );
			l.add( new Craft(ItemDatabase.find("파란 옷감"), 4) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("마법사 모자");
		if(i != null){
			craft_list.put("request magician cap", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("고급 에메랄드"), 2) );
			l.add( new Craft(ItemDatabase.find("마력의 돌"), 20) );
			l.add( new Craft(ItemDatabase.find("하얀 옷감"), 1) );
			l.add( new Craft(ItemDatabase.find("붉은 옷감"), 1) );
			l.add( new Craft(ItemDatabase.find("파란 옷감"), 1) );
			list.put(i, l);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "moria1"));
	}
	
}
