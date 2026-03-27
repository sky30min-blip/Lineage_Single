package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.World;
import lineage.world.object.instance.PcInstance;

public class C_Potal extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_Potal(data, length);
		else
			((C_Potal)bp).clone(data, length);
		return bp;
	}
	
	public C_Potal(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(pc==null || pc.isWorldDelete())
			return this;
		
		if( !pc.isTransparent() )
			pc.toTeleport(pc.getHomeX(), pc.getHomeY(), pc.getHomeMap(), World.get_map(pc.getX(), pc.getY(), pc.getMap())!=127);
		else
			pc.toTeleport(pc.getHomeX(), pc.getHomeY(), pc.getHomeMap(), false);
		
		return this;
	}
}
