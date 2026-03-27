package lineage.world.object.npc.teleporter;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.TeleportInstance;

public class FieldOfHonor extends TeleportInstance {

	public FieldOfHonor(Npc npc){
		super(npc);
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(getMap()==68){
			if(getHeading()==6){
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "agin"));
			}else{
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "agout"));
			}
		}else{
			if(getHeading()==4){
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "agin"));
			}else{
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "agout"));
			}
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(getMap()==68){
			if(getHeading()==6){
				pc.toPotal(32827, 32776, getMap());
			}else{
				pc.toPotal(32835, 32781, getMap());
			}
		}else{
			if(getHeading()==4){
				pc.toPotal(32721, 32777, getMap());
			}else{
				pc.toPotal(32709, 32775, getMap());
			}
		}
	}

}
