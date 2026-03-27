package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.ChattingController;
import lineage.world.object.instance.PcInstance;

public class C_ObjectChattingWhisper extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_ObjectChattingWhisper(data, length);
		else
			((C_ObjectChattingWhisper)bp).clone(data, length);
		return bp;
	}
	
	public C_ObjectChattingWhisper(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(pc==null || pc.isWorldDelete())
			return this;
		
		String name = readS();
		String msg = readS();

		ChattingController.toWhisper(pc, name, msg);
		return this;
	}
	
}
