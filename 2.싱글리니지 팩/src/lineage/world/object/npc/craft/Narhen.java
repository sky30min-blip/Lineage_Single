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
import lineage.world.object.instance.CraftInstance;
import lineage.world.object.instance.PcInstance;

public class Narhen extends CraftInstance {
	
	public Narhen(Npc npc){
		super(npc);
		
		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add( npc.getNameId() );
		
		// 제작 처리 초기화.
		Item i = ItemDatabase.find("마법의 플룻");
		if(i != null){
			craft_list.put("request magic flute", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("오리하루콘"), 10) );
			l.add( new Craft(ItemDatabase.find("엔트의 껍질"), 1) );
			list.put(i, l);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(pc.getClassType()==Lineage.LINEAGE_CLASS_ELF){
			if(pc.getLawful()<Lineage.NEUTRAL){
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "narhenCE"));
			}else{
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "narhenE1"));
			}
		}else{
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "narhenM1"));
		}
		
	}

}
