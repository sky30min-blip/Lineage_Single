package lineage.world.object.npc.shop;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.ShopInstance;

public class Squalid extends ShopInstance {
	
	public Squalid(Npc npc){
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(pc.getClassType() == Lineage.LINEAGE_CLASS_DARKELF) {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "scwaty1"));
		} else {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "scwaty2"));
		}
	}

}
