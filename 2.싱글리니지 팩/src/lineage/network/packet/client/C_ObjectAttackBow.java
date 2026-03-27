package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.object.instance.PcInstance;

public class C_ObjectAttackBow extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_ObjectAttackBow(data, length);
		else
			((C_ObjectAttackBow)bp).clone(data, length);
		return bp;
	}
	
	public C_ObjectAttackBow(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(pc==null || pc.isDead() || !isRead(8) || pc.isWorldDelete())
			return this;
		
		int obj_id = readD();
		int x = readH();
		int y = readH();

		if (pc.getGm() > 0 || !pc.isTransparent())
			pc.toAttack(pc.findInsideList(obj_id), x, y, true, 0, 0, false);
		return this;
	}
}
