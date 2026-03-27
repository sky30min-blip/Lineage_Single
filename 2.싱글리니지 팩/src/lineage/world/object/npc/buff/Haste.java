package lineage.world.object.npc.buff;

import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class Haste extends object {

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "freehaste"));
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("haste")){
			pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 755), true);
			// 헤이스트 시전.
			lineage.world.object.magic.Haste.onBuff(pc, SkillDatabase.find(6, 2));
		}
	}
}
