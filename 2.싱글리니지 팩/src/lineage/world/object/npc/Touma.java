package lineage.world.object.npc;

import java.util.ArrayList;
import java.util.List;

import lineage.share.System;
import lineage.util.Util;
import lineage.bean.database.Npc;
import lineage.bean.lineage.Quest;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_ObjectAction;
import lineage.share.Lineage;
import lineage.world.controller.QuestController;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.QuestInstance;

public class Touma extends QuestInstance {
	
	private long actionTime;

	public Touma(Npc npc){
		super(npc);
	}
	
	@Override
	public void toAi(long time) {
		if (actionTime + (3 * (Util.random(2, 3))) < System.currentTimeMillis()) {
			while (true) {
				int tempGfxMode = Lineage.Touma[Util.random(1, Lineage.Touma.length - 1)];
				if (SpriteFrameDatabase.findGfxMode(getGfx(), tempGfxMode)) {
					actionTime = (int) System.currentTimeMillis();
					toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this, tempGfxMode), true);
					break;
				}
			}
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "touma1"));
	}
}
