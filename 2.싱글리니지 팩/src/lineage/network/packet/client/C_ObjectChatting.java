package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.ChattingController;
import lineage.world.object.instance.PcInstance;

public class C_ObjectChatting extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_ObjectChatting(data, length);
		else
			((C_ObjectChatting)bp).clone(data, length);
		return bp;
	}
	
	public C_ObjectChatting(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(pc==null || pc.isWorldDelete())
			return this;
		
		int mode = readC();
		String msg = readS();
		
		if(pc.getGm()>0 || !pc.isTransparent())
			ChattingController.toChatting(pc, msg, mode);
		
		return this;
	}
}
