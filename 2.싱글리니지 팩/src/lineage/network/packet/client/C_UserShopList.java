package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.UserShopController;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class C_UserShopList extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_UserShopList(data, length);
		else
			((C_UserShopList)bp).clone(data, length);
		return bp;
	}
	
	public C_UserShopList(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(pc==null || pc.isDead() || pc.isWorldDelete())
			return this;
		
		boolean buy = readC() == 0;
		object o = pc.findInsideList(readD());
		if(o!=null && o instanceof PcInstance)
			UserShopController.toList(pc, (PcInstance)o, buy);
		return this;
	}

}
