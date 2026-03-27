package lineage.world.object.npc;

import java.util.ArrayList;
import java.util.List;

import all_night.Npc_promotion;
import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.CharacterController;
import lineage.world.controller.ChattingController;
import lineage.world.object.instance.NpcInstance;
import lineage.world.object.instance.PcInstance;

public class Promot_npc extends NpcInstance {
	long mentTime;
	
	public Promot_npc(Npc npc) {
		super(npc);
		CharacterController.toWorldJoin(this);
	}
	
	@Override
	public void close() {
		mentTime = 0;
		super.close();
		CharacterController.toWorldOut(this);
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		List<String> list = new ArrayList<String>();
		
		if (Npc_promotion.title1.length() > 2 && Npc_promotion.content1.length() > 2) {
			list.add(Npc_promotion.title1);
			list.add(Npc_promotion.content1);
		}
		
		if (Npc_promotion.title2.length() > 2 && Npc_promotion.content2.length() > 2) {
			list.add(Npc_promotion.title2);
			list.add(Npc_promotion.content2);
		}
		
		if (Npc_promotion.title3.length() > 2 && Npc_promotion.content3.length() > 2) {
			list.add(Npc_promotion.title3);
			list.add(Npc_promotion.content3);
		}
		
		if (Npc_promotion.title4.length() > 2 && Npc_promotion.content4.length() > 2) {
			list.add(Npc_promotion.title4);
			list.add(Npc_promotion.content4);
		}
		
		if (Npc_promotion.title5.length() > 2 && Npc_promotion.content5.length() > 2) {
			list.add(Npc_promotion.title5);
			list.add(Npc_promotion.content5);
		}
		
		if (Npc_promotion.title6.length() > 2 && Npc_promotion.content6.length() > 2) {
			list.add(Npc_promotion.title6);
			list.add(Npc_promotion.content6);
		}
		
		if (Npc_promotion.title7.length() > 2 && Npc_promotion.content7.length() > 2) {
			list.add(Npc_promotion.title7);
			list.add(Npc_promotion.content7);
		}
		
		if (Npc_promotion.title8.length() > 2 && Npc_promotion.content8.length() > 2) {
			list.add(Npc_promotion.title8);
			list.add(Npc_promotion.content8);
		}
		
		if (Npc_promotion.title9.length() > 2 && Npc_promotion.content9.length() > 2) {
			list.add(Npc_promotion.title9);
			list.add(Npc_promotion.content9);
		}
		
		if (Npc_promotion.title10.length() > 2 && Npc_promotion.content10.length() > 2) {
			list.add(Npc_promotion.title10);
			list.add(Npc_promotion.content10);
		}
		
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "npcpromote", null, list));
	}
	
	@Override
	public void toTimer(long time) {
		boolean isMent = false;

		for (int i = 0; i < Npc_promotion.ment.length; i++) {
			if (Npc_promotion.ment[i].length() > 4) {
				isMent = true;
				break;
			}
		}
		
		if (mentTime < time && isMent) {
			mentTime = time + Npc_promotion.ment_delay;
			
			while (true) {
				int random = Util.random(0, Npc_promotion.ment.length - 1);
				
				if (Npc_promotion.ment[random].length() > 4) {
					ChattingController.toChatting(this, Npc_promotion.ment[random], Lineage.CHATTING_MODE_NORMAL);
					break;
				}
			}			
		}
	}
		
}
