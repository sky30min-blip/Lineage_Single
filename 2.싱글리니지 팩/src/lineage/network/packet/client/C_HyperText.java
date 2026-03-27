package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class C_HyperText extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_HyperText(data, length);
		else
			((C_HyperText)bp).clone(data, length);
		return bp;
	}
	
	public C_HyperText(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그방지
		if(!isRead(4) || pc==null || pc.isWorldDelete())
			return this;
		
		object o = pc.findInsideList(readD());
		if(o!=null && (pc.getGm()>0 || !pc.isTransparent()))
			o.toHyperText(pc, this);
		return this;
	}

}
