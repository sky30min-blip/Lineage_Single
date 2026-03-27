package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.TradeController;
import lineage.world.object.instance.PcInstance;

public class C_TradeItem extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_TradeItem(data, length);
		else
			((C_TradeItem)bp).clone(data, length);
		return bp;
	}
	
	public C_TradeItem(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(pc==null || pc.isDead() || pc.isWorldDelete() || !isRead(8))
			return this;
		
		int inv_id = readD();
		long count = readD();

		TradeController.toTradeAddItem(pc, pc.getInventory().value(inv_id), count);
		
		return this;
	}
}
