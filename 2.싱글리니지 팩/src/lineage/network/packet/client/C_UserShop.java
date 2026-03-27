package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.UserShopController;
import lineage.world.object.instance.PcInstance;

public class C_UserShop extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_UserShop(data, length);
		else
			((C_UserShop)bp).clone(data, length);
		return bp;
	}
	
	public C_UserShop(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(pc==null || pc.isDead() || pc.isWorldDelete())
			return this;
		
		if(readC() == 0)
			UserShopController.toStart(pc, this);
		else
			UserShopController.toStop(pc);
		return this;
	}

}
