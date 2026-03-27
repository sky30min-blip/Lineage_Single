package lineage.powerball;

import lineage.bean.database.Npc;
import lineage.network.packet.ClientBasePacket;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.NpcInstance;

/**
 * 파워볼 맵 전광판용 NPC (일반볼 5개 + 파워볼 1개 = 6개).
 * 맵에 배치되어 이름(숫자)만 표시하며, 클릭 시 아무 반응 없음.
 */
public class PowerBallDisplayBall extends NpcInstance {

	public PowerBallDisplayBall(Npc npc) {
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		// 전광판용: 클릭해도 대화/상점 열지 않음
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		// 전광판용: bypass 등 클릭 시에도 반응 없음
	}
}
