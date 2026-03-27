/*
교환원하는 재료가없을때
rrafons6
*/

package lineage.world.object.npc.craft;

import lineage.bean.database.Npc;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.CraftController;
import lineage.world.object.instance.CraftInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class Rafons extends CraftInstance {

	public Rafons(Npc npc) {
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		if (pc.getInventory().find(ItemDatabase.find("요리책 : 3단계")) == null)
		    pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "rrafons"));
		else {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "rrafons9"));
		}
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
//		System.out.println("aciotn : " + action);
		ItemInstance book1 = pc.getInventory().find(ItemDatabase.find("요리책 : 1단계"));
		ItemInstance book2 = pc.getInventory().find(ItemDatabase.find("요리책 : 2단계"));
		ItemInstance book3 = pc.getInventory().find(ItemDatabase.find("요리책 : 3단계"));

		if (book3 != null) {
			return;
		}
		switch (action.charAt(0)) {
		case 'A': // 1만아데나
			if (book2 != null || book3 != null) {
				pc.toSender(new S_Html(this, "rrafons3"));
				break;
			}
			if (book1 == null) {
				if (pc.getInventory().isAden(10000, true)) {
					CraftController.toCraft(this, pc, ItemDatabase.find("요리책 : 1단계"), 1, true);
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "rrafons1"));
				} else {
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "rrafons2"));
				}
			} else {
				pc.toSender(new S_Html(this, "rrafons1"));
				ChattingController.toChatting(pc, "이미 요리책 : 1단계를 가지고 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
			break;
		case 'B': // 2단게
			if (book2 == null) {
				if ( book1 == null) {
					pc.toSender(new S_Html(this, "rrafons5"));
					return;
				}
				if (pc.getInventory().isAden(10000, true) && book1 != null) {
					CraftController.toCraft(this, pc, ItemDatabase.find("요리책 : 2단계"), 1, true);
					pc.getInventory().remove(book1, true);
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "rrafons1"));
				} else {
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "rrafons2"));
				}
			} else {
				pc.toSender(new S_Html(this, "rrafons1"));
				ChattingController.toChatting(pc, "이미 요리책 : 2단계를 가지고 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
			break;
		case 'q': // 3단계
			if ( book2 == null) {
				pc.toSender(new S_Html(this, "rrafons11"));
				return;
			}
			if (pc.getInventory().isAden(10000, true) && book2 != null) {
				CraftController.toCraft(this, pc, ItemDatabase.find("요리책 : 3단계"), 1, true);
				pc.getInventory().remove(book2, true);
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "rrafons1"));
			} else {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "rrafons2"));
			}
			break;
		default:
			break;
		}
	}
}
