package lineage.world.object.npc.craft;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.lineage.Craft;
import lineage.database.ItemDatabase;
import lineage.database.NpcSpawnlistDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.npc.guard.ElvenGuard;

public class FairyQueen extends ElvenGuard {

	static synchronized public object clone(object o, Npc n){
		if(o == null)
			o = NpcSpawnlistDatabase.newObject(n, new FairyQueen(n));
		else
			o = NpcSpawnlistDatabase.newObject(n, o);
		return o;
	}
	
	public FairyQueen(Npc npc){
		super(npc);
		
		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add( npc.getNameId() );
		
		// 제작 처리 초기화.
		Item i = ItemDatabase.find("오리하루콘");
		if(i != null){
			craft_list.put("request oriharukon", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("미스릴"), 10) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("미스릴");
		if(i != null){
			i.getListCraft().put("request lump of pure mithril", 20);
			craft_list.put("request lump of pure mithril", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("미스릴 원석"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("페어리의 날개");
		if(i != null){
			craft_list.put("request ala of fairy", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("미스릴 실"), 5) );
			l.add( new Craft(ItemDatabase.find("페어리 더스트"), 40) );
			list.put(i, l);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		super.toTalk(pc, cbp);
		
		if(pc.getClassType()==Lineage.LINEAGE_CLASS_ELF || pc.getGm()>0){
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "fairyqe1"));
		}else{
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "fairyqm1"));
		}
	}
	
}
