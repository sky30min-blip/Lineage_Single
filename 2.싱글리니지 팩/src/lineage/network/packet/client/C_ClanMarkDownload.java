package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.ClanController;
import lineage.world.object.instance.PcInstance;

public class C_ClanMarkDownload extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_ClanMarkDownload(data, length);
		else
			((C_ClanMarkDownload)bp).clone(data, length);
		return bp;
	}
	
	public C_ClanMarkDownload(byte[] data, int length){
		clone(data, length);
	}

	@Override
	public BasePacket init(PcInstance pc){
		// 버그방지
		if(!isRead(4) || pc==null || pc.isWorldDelete())
			return this;
		
		ClanController.toMarkDownload(pc, readD());
		return this;
	}
}
