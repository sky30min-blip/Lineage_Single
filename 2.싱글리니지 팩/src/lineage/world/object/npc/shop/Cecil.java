package lineage.world.object.npc.shop;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.controller.CharacterController;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.QuestInstance;

public class Cecil extends QuestInstance {

	public Cecil(Npc npc) {
		super(npc);

		// 관리목록에 등록. toTimer가 호출되도록 하기 위해.
		CharacterController.toWorldJoin(this);
		// 20초 단위로 멘트 표현.
		ment_show_sec = 20;
		list_ment.add("개 경주장은 문을 닫게 되었습니다.");
		list_ment.add("그동안 이용해 주셔서 감사합니다.");
		list_ment.add("아직 남아있는 티켓은 모두 구매하고 있습니다.");
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maeno11"));
	}
}
