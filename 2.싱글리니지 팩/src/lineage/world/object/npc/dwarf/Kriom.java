package lineage.world.object.npc.dwarf;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Npc;
import lineage.bean.lineage.Kingdom;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.controller.KingdomController;
import lineage.world.object.instance.DwarfInstance;
import lineage.world.object.instance.PcInstance;

public class Kriom extends DwarfInstance {
	
	private List<String> list_html;
	
	public Kriom(Npc npc){
		super(npc);
		kingdom = KingdomController.find(Lineage.KINGDOM_ABYSS);
		list_html = new ArrayList<String>();
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		Kingdom k = KingdomController.find(pc);
		if(k==null || k.getUid()!=kingdom.getUid()){
			list_html.clear();
			list_html.add(kingdom.getClanName()==null ? "" : kingdom.getClanName());
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "krioml", null, list_html));
		}else{
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "kriom"));
		}
	}
}
