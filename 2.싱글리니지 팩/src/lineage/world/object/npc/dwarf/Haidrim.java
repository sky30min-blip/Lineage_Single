package lineage.world.object.npc.dwarf;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.instance.DwarfInstance;
import lineage.world.object.instance.PcInstance;

public class Haidrim extends DwarfInstance {
	
	public Haidrim(Npc npc){
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(isLevel(pc.getLevel())){
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "haidrim"));
		}else{
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "haidrimop"));
		}
	}
}
