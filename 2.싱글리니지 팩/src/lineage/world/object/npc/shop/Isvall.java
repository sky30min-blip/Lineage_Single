package lineage.world.object.npc.shop;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_ShopBuy;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.ShopInstance;

public class Isvall extends ShopInstance {
	
	public Isvall(Npc npc){
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(pc.getGm()>0){
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "isvall4"));
		}else{
			if(pc.getLevel()<40){
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "isvall2"));
			}else if(pc.getLevel()<45){
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "isvall3"));
			}else{
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "isvall4"));
			}
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (action.equalsIgnoreCase("buy")) {
			pc.toSender(S_ShopBuy.clone(BasePacketPooling.getPool(S_ShopBuy.class), this));
		} else if (pc.getInventory().isAden(20000, true)) {
	        if(action.equalsIgnoreCase("isvallT")) {
	            pc.toTeleport(32936, 33051, 70, true);
	            pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, null));
	        }
	    } else {
	        pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "isvall9"));
	    }
	}
}