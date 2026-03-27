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

public class Eveurol extends CraftInstance {

	public Eveurol(Npc npc){
		super(npc);
		
		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add( npc.getNameId() );
		
		// 제작 처리 초기화.
		Item i = ItemDatabase.find("에바의 축복");
		if(i != null){
			craft_list.put("request bless of eva", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("아데나"), 300) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("아데나");
		if(i != null){
			i.getListCraft().put("request adena", 1000);
			craft_list.put("request adena", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("인어의 비늘"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("에바의 방패");
		if(i != null){
			craft_list.put("request shield of eva", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("사각 방패"), 1) );
			l.add( new Craft(ItemDatabase.find("인어의 비늘"), 100) );
			l.add( new Craft(ItemDatabase.find("수룡 비늘"), 10) );
			list.put(i, l);
		}
		
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "eveurol1"));
	}
	
}
