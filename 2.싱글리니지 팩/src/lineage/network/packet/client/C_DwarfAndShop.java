package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.npc.자동판매;

public class C_DwarfAndShop extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_DwarfAndShop(data, length);
		else
			((C_DwarfAndShop)bp).clone(data, length);
		return bp;
	}
	
	public C_DwarfAndShop(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그방지용
		if(pc==null || pc.isWorldDelete() || !isRead(4) || pc.getInventory()==null)
			return this;
		
		object o = pc.findInsideList(readD());
		
		
		if (pc.isAutoSellAdding) {
			자동판매.addShopItem(pc,this);
		} 

		
		if(o!=null && (pc.getGm()>0 || !pc.isTransparent()))
			o.toDwarfAndShop(pc, this);
		else
			//f1상점
			if(o == null && pc.getTempShop() != null)
				pc.getTempShop().toDwarfAndShop(pc, this);
		return this;
	}
}
