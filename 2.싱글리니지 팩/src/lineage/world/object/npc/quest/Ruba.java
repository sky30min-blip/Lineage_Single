package lineage.world.object.npc.quest;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.lineage.Craft;
import lineage.bean.lineage.Quest;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.controller.CharacterController;
import lineage.world.controller.CraftController;
import lineage.world.controller.QuestController;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.QuestInstance;

public class Ruba extends QuestInstance {

	public Ruba(Npc n) {
		super(n);
		// 관리목록에 등록. toTimer가 호출되도록 하기 위해.
		CharacterController.toWorldJoin(this);
		// 20초 단위로 멘트 표현.
		ment_show_sec = 20;
		// 예언서에 나타난 그분은 언제쯤 오시려나...
		list_ment.add("$1735");
		// 만일 지하 던전의 봉인이 풀리기라도 한다면...
		list_ment.add("$1736");

		Item i = ItemDatabase.find("섬의 증표");
		if (i != null) {
			craft_list.put(Lineage.QUEST_RUBA, i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("검은 열쇠"), 1));
			list.put(i, l);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		Quest q = QuestController.find(pc, Lineage.QUEST_RUBA);
		if (q == null)
			q = QuestController.newQuest(pc, this, Lineage.QUEST_RUBA);
		if (q.getQuestStep() == 0) {
			switch (pc.getClassType()) {
			case Lineage.LINEAGE_CLASS_KNIGHT:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ruba4"));
				break;
			case Lineage.LINEAGE_CLASS_ROYAL:
			case Lineage.LINEAGE_CLASS_WIZARD:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ruba"));
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ruba5"));
				break;
			default:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ruba6"));
				break;
			}
		} else
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ruba6"));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (action.equalsIgnoreCase("request amulet of island")) {
			Item craft = craft_list.get(Lineage.QUEST_RUBA);
			Quest q = QuestController.find(pc, Lineage.QUEST_RUBA);
			if (craft != null && q != null && q.getQuestStep() == 0) {
				List<Craft> l = list.get(craft);
				// 재료 확인.
				if (CraftController.isCraft(pc, l, true)) {
					// 재료 제거
					CraftController.toCraft(pc, l);
					// 아이템 지급.
					CraftController.toCraft(this, pc, craft, 1, true);
					// 퀘스트 스탭 변경.
					q.setQuestStep(1);
				}
			}
		}
	}
}
