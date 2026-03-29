package lineage.world.controller;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectHeading;
import lineage.util.Util;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.object;

/**
 * npc.face_player_on_talk=1 일 때만 PC 쪽으로 heading (GM 강제).<br>
 * 0(기본)이면 이 클래스는 아무 것도 하지 않고, 예전처럼 각 NPC 클래스의 기존 로직을 따른다.
 */
public final class NpcTalkHeading {

	public static void apply(object o, PcInstance pc) {
		if (o == null || pc == null || o == pc || !o.isFacePlayerOnTalk())
			return;
		int h = Util.calcheading(o, pc.getX(), pc.getY());
		if (o.getHeading() != h) {
			o.setHeading(h);
			o.toSender(S_ObjectHeading.clone(BasePacketPooling.getPool(S_ObjectHeading.class), o), false);
		}
	}

	/**
	 * DB 플래그가 꺼져 있을 때만, 예전 상점/제작 NPC 등에서 쓰던 방식으로 PC 방향으로 돌린다.
	 * (플래그가 켜져 있으면 apply()에서 이미 처리하므로 여기서는 생략.)
	 */
	public static void legacyFacePlayerWhenDbOff(object o, PcInstance pc) {
		if (o == null || pc == null || o == pc || o.isFacePlayerOnTalk())
			return;
		o.setHeading(Util.calcheading(o, pc.getX(), pc.getY()));
		o.toSender(S_ObjectHeading.clone(BasePacketPooling.getPool(S_ObjectHeading.class), o), false);
	}
}
