package lineage.world.object.npc;

import lineage.bean.database.marketPrice;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.World;
import lineage.world.controller.ChattingController;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class PriceCheck extends object {
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		int index = Integer.valueOf(action);

		if (!pc.isDead() && !pc.isLock() && !pc.isWorldDelete()) {
			if (!World.isSafetyZone(pc.getX(), pc.getY(), pc.getMap())) {
				ChattingController.toChatting(pc, "\\fR세이프존에서 이용가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			if (pc.marketPrice != null && pc.marketPrice.get(index) != null) {
				marketPrice mp = pc.marketPrice.get(index);
				
				if (mp.getShopNpc() != null && mp.getX() > 0 && mp.getY() > 0) {					
					if (World.isSafetyZone(mp.getX(), mp.getY(), mp.getMap())) {
						pc.toTeleport(mp.getX(), mp.getY(), mp.getMap(), false);
					} else {
						ChattingController.toChatting(pc, "\\fR세이프티존에서만 이동 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
					}
					
					object o = pc.findInsideList(mp.getObjId());
					if (o != null)
						o.toTalk(pc, null);
				}
			}
		} else {
			ChattingController.toChatting(pc, "\\fR이동할 수 없는 상태입니다.", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
}
