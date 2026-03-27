package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.ClanController;
import lineage.world.object.instance.PcInstance;

public class C_ClanWar extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_ClanWar(data, length);
		else
			((C_ClanWar)bp).clone(data, length);
		return bp;
	}
	
	public C_ClanWar(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그방지
		if(pc==null || pc.isWorldDelete() || !isRead(1))
			return this;
		
		int type = readC();
		String name = readS().trim();
		
		switch(type){
			case 0:	// 선포
				ClanController.toWar(pc, name);
				break;
			case 2:	// 항복
				ClanController.toWarSubmission(pc, name);
				break;
		}
		
		return this;
	}

}
