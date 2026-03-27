package lineage.world.object.npc;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.object.instance.PcInstance;

public class Morien extends TalkMovingNpc {

	public Morien(Npc npc){
		super(npc, null);
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		super.toTalk(pc, cbp);
		
		switch(pc.getClassType()){
			case Lineage.LINEAGE_CLASS_ELF:
				if(pc.getLawful()<Lineage.NEUTRAL)
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "morienCE1"));
				else
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "morienE1"));
				break;
			case Lineage.LINEAGE_CLASS_DARKELF:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "morienM2"));
				break;
			default:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "morienM1"));
				break;
		}
	}
}
