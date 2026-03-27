package lineage.world.object.npc;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class Siris extends object {

	public Siris(){
		//
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(pc.getClassType() == Lineage.LINEAGE_CLASS_KNIGHT)
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "sirissnw"));
		else
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "siriss"));
	}
	
}
