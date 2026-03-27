package lineage.world.object.npc.teleporter;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.TeleportInstance;

public class Coco extends TeleportInstance {

	public Coco(Npc npc){
		super(npc);
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		switch(pc.getClassType()){
			case Lineage.LINEAGE_CLASS_ROYAL:
			case Lineage.LINEAGE_CLASS_WIZARD:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "coco1"));
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "coco2"));
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "coco3"));
				break;
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("teleportURL")){
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "coco4"));
		}else if(action.equalsIgnoreCase("teleport dungeon-in")){
			pc.toPotal(32870, 32869, 86);
		}
	}

}
