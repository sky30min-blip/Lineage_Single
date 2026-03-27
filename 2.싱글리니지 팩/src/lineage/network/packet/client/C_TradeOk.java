package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.TradeController;
import lineage.world.object.instance.PcInstance;

public class C_TradeOk extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_TradeOk(data, length);
		else
			((C_TradeOk)bp).clone(data, length);
		return bp;
	}
	
	public C_TradeOk(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(pc==null || pc.isWorldDelete())
			return this;
		
		TradeController.toTradeOk(pc);
		
		return this;
	}
}
