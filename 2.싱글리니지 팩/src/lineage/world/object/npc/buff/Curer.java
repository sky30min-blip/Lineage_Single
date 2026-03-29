package lineage.world.object.npc.buff;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.world.controller.NpcTalkHeading;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class Curer extends object {

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		super.toTalk(pc, cbp);
		NpcTalkHeading.legacyFacePlayerWhenDbOff(this, pc);
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "noved"));
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		NpcTalkHeading.apply(this, pc);
		if(action.equalsIgnoreCase("fullheal")){
			pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 831), true);
			pc.setNowHp(pc.getTotalHp());
		}
	}

}
