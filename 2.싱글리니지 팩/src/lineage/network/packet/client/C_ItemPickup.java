package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.util.Util;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.AbsoluteBarrier;

public class C_ItemPickup extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_ItemPickup(data, length);
		else
			((C_ItemPickup)bp).clone(data, length);
		return bp;
	}
	
	public C_ItemPickup(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		//쿠베라 앱솔상태에서 못줍게 추가
		if(pc==null || pc.getInventory()==null || !isRead(12) || pc.isDead() || pc.isWorldDelete() || pc.isBuffAbsoluteBarrier())
			return this;

		int x = readH();
		int y = readH();
		int object_id = readD();
		long count = readD();
		
		object o = pc.findInsideList(object_id);
		if(o!=null && (pc.getGm()>0 || !pc.isTransparent())){
			if(o.getX()==x && o.getY()==y && Util.isDistance(pc, o, 2)) {
				synchronized (o.sync_pickup) {
					if(o.isWorldDelete() == false) {
						// 앱솔루트 배리어 제거
						BuffController.remove(pc, AbsoluteBarrier.class);
						
						pc.getInventory().toPickup(o, count);
					}
				}
			}
		}
		
		return this;
	}
}
