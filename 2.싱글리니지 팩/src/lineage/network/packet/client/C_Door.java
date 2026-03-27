package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class C_Door extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_Door(data, length);
		else
			((C_Door)bp).clone(data, length);
		return bp;
	}
	
	public C_Door(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그방지
		if(!isRead(8) || pc==null || pc.isWorldDelete())
			return this;
		
		readD();
		object o = pc.findInsideList(readD());
		if(o!=null && (pc.getGm()>0 || !pc.isTransparent()))
			o.toClick(pc, this);
		return this;
	}
}
