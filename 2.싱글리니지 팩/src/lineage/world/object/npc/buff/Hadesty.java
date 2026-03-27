package lineage.world.object.npc.buff;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectHeading;
import lineage.util.Util;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class Hadesty extends object {

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		int h = Util.calcheading(this, pc.getX(), pc.getY());
		if(heading != h){
			setHeading( h );
			toSender(S_ObjectHeading.clone(BasePacketPooling.getPool(S_ObjectHeading.class), this), false);
		}
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "hadesty1"));
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("fullheal")){
			pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 831), true);
			pc.setNowHp(pc.getTotalHp());
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "hadesty2"));
		}
	}

}
