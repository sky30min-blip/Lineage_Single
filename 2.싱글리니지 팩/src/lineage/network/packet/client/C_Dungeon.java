package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.object.instance.PcInstance;

public class C_Dungeon extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_Dungeon(data, length);
		else
			((C_Dungeon)bp).clone(data, length);
		return bp;
	}
	
	public C_Dungeon(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(!isRead(6) || pc==null || pc.isWorldDelete())
			return this;

		int map = readH();
		int x = readH();
		int y = readH();
		if(map>=0 && map<=15)
			pc.toTeleport(x, y, map, false);
		
		return this;
	}

}
