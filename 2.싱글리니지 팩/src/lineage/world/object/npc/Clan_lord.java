package lineage.world.object.npc;

import java.util.ArrayList;
import java.util.List;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.GiranClanLordController;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class Clan_lord extends object {

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		List<String> list = new ArrayList<String>();
		
		list.add(Lineage.is_new_clan_pvp ? "가능" : "불가능");
		list.add(Lineage.is_new_clan_attack_boss ? "가능" : "불가능");
		list.add(Lineage.is_new_clan_oman_top ? "가능" : "불가능");
		
		if (Lineage.is_new_clan_auto_out) {
			list.add(String.format("신규 혈맹은 %d레벨 이상 자동탈퇴 됩니다.", Lineage.new_clan_max_level));
			
			if (!pc.isWorldDelete())
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "giranLoad1", null, list));
		} else {
			if (!pc.isWorldDelete())
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "giranLoad2", null, list));
		}

	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if (action.equalsIgnoreCase("join new clan")) {
			if (pc.getClanId() > 0) {
				//89 \f1이미 혈맹에 가입했습니다.
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 89));
			} else {
				if (pc.getLevel() < Lineage.new_clan_max_level)
					GiranClanLordController.toAsk(pc);
				else
					ChattingController.toChatting(pc, String.format("신규 혈맹은 %d레벨 이하 가입 가능합니다.", Lineage.new_clan_max_level - 1), Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}
}
