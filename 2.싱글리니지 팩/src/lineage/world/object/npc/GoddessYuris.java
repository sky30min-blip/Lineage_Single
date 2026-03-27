package lineage.world.object.npc;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class GoddessYuris extends object {

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "yuris1"));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		int count = 0;
		if (action.equalsIgnoreCase("0"))
			count = 1;
		else if (action.equalsIgnoreCase("1"))
			count = 3;
		else if (action.equalsIgnoreCase("2"))
			count = 5;
		else if (action.equalsIgnoreCase("3"))
			count = 10;
		
		ItemInstance jeryo = pc.getInventory().findDbNameId(5840);
		if (count>0 && jeryo!=null && jeryo.getCount()>=count) {
			pc.getInventory().count(jeryo, jeryo.getCount()-count, true);
			pc.setLawful(pc.getLawful() + 3000 * count);

			pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 9009), true);
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "yuris2"));
		} else {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "yuris3"));
		}
	}

}
