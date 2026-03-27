package lineage.world.object.npc.dwarf;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.instance.DwarfInstance;
import lineage.world.object.instance.PcInstance;

public class Rayearth extends DwarfInstance {
	
	public Rayearth(Npc npc){
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(isLevel(pc.getLevel())){
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "rayearth1"));
		}else{
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "rayearth2"));
		}
	}

}
