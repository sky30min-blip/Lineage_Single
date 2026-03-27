package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.FriendController;
import lineage.world.object.instance.PcInstance;

public class C_FriendList extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_FriendList(data, length);
		else
			((C_FriendList)bp).clone(data, length);
		return bp;
	}
	
	public C_FriendList(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그방지
		if(pc==null || pc.isWorldDelete())
			return this;
		
		FriendController.toList(pc);
		
		return this;
	}

}
