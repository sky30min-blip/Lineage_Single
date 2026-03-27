package lineage.world.object.npc.event;

import lineage.bean.database.Npc;
import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.LuckyController;
import lineage.world.object.instance.EventInstance;
import lineage.world.object.instance.PcInstance;

public class Keplisha extends EventInstance {

	public Keplisha(Npc npc) {
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		LuckyController.toLucky(this, pc);
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		LuckyController.toLuckyFinal(this, pc, action);
	}

}
