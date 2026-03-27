package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.FriendController;
import lineage.world.object.instance.PcInstance;

public class C_FriendAdd extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_FriendAdd(data, length);
		else
			((C_FriendAdd)bp).clone(data, length);
		return bp;
	}
	
	public C_FriendAdd(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		FriendController.append(pc, readS());
		
		return this;
	}

}
