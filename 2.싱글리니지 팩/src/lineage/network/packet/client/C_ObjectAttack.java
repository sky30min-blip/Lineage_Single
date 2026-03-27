package lineage.network.packet.client;

import lineage.bean.lineage.Kingdom;
import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.KingdomController;
import lineage.world.controller.SpotController;
import lineage.world.object.instance.PcInstance;

public class C_ObjectAttack extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_ObjectAttack(data, length);
		else
			((C_ObjectAttack)bp).clone(data, length);

		return bp;
	}
	
	public C_ObjectAttack(byte[] data, int length){
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

		Kingdom k = KingdomController.find(4);
		if (k != null && k.getCrown() != null && k.getCrown().getObjectId() == obj_id) {
			k.getCrown().toDamage(pc, 0, Lineage.ATTACK_TYPE_WEAPON);
			return this;
		}
		
		if (SpotController.spot != null && SpotController.spot.getCrown().getObjectId() == obj_id) {
			SpotController.spot.getCrown().toDamage(pc, 0, Lineage.ATTACK_TYPE_WEAPON);
			return this;
		}
		
		if (pc.getGm() > 0 || !pc.isTransparent())
			pc.toAttack(pc.findInsideList(obj_id), x, y, false, 0, 0, false);
		
		return this;
	}
}
