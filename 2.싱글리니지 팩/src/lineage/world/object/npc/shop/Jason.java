package lineage.world.object.npc.shop;

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
import lineage.world.object.instance.ShopInstance;

public class Jason extends CraftInstance {

	private long actionTime;
	private int lastAction;

	public Jason(Npc npc) {
		super(npc);
	}

	@Override
	public void toAi(long time) {
		if (actionTime + (600 * (Util.random(10, 30))) < System.currentTimeMillis()) {
			while (true) {
				int tempGfxMode = Lineage.Jason[Util.random(0, Lineage.Jason.length - 1)];
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
		if (pc.getLawful() < Lineage.NEUTRAL) {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "jason2"));
		} else {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "jason1"));
		}
	}
}
