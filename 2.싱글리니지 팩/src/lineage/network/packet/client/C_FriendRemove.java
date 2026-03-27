package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.FriendController;
import lineage.world.object.instance.PcInstance;

public class C_FriendRemove extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_FriendRemove(data, length);
		else
			((C_FriendRemove)bp).clone(data, length);
		return bp;
	}
	
	public C_FriendRemove(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그방지
		if(pc==null || pc.isWorldDelete())
			return this;
		
		FriendController.remove(pc, readS());
		
		return this;
	}

}
