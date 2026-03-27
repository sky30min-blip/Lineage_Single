package lineage.world.object.npc.craft;

import lineage.bean.database.Npc;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_ObjectAction;
import lineage.share.Lineage;
import lineage.share.System;
import lineage.util.Util;
import lineage.world.object.instance.CraftInstance;
import lineage.world.object.instance.PcInstance;

public class Anton extends CraftInstance {

	private long actionTime;
	private int lastAction;

	public Anton(Npc npc) {
		super(npc);
	}

	@Override
	public void toAi(long time) {
		if (actionTime + (600 * (Util.random(10, 30))) < System.currentTimeMillis()) {
			while (true) {
				int tempGfxMode = Lineage.Anton[Util.random(0, Lineage.Anton.length - 1)];
				if (SpriteFrameDatabase.findGfxMode(getGfx(), tempGfxMode) && lastAction != tempGfxMode) {
					lastAction = 0;
					actionTime = System.currentTimeMillis();
					toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this, tempGfxMode), true);
					break;
				}
			}
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "anton1"));
	}
}