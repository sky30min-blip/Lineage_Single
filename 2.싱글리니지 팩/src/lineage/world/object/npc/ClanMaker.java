package lineage.world.object.npc;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class ClanMaker extends object {

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(pc.getClanId() != 0){
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "bpledge1"));
		}else{
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "bpledge2"));
		}
	}
}
