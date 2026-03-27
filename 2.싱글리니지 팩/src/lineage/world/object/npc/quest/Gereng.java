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
import lineage.world.controller.CraftController;
import lineage.world.controller.QuestController;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.QuestInstance;

public class Gereng extends QuestInstance {

	public Gereng(Npc n) {
		super(n);

		craft_list.put("request bone piece of undead", null);
		List<Craft> l = new ArrayList<Craft>();
		l.add(new Craft(ItemDatabase.find("언데드의 뼈"), 1));
		list.put(null, l);

		Item i = ItemDatabase.find("신비한 지팡이");
		if (i != null) {
			craft_list.put("request mystery staff", i);

			l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("신비한 수정구"), 1));
			list.put(i, l);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		switch (pc.getClassType()) {
		case 0x00:
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gerengp1"));
			break;
		case 0x01:
			if (pc.getLevel() >= 50) {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gerengEv1"));
			} else {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gerengk1"));
			}
			break;
		case 0x02:
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gerenge1"));
			break;
		case 0x03:
			if (Lineage.server_version < 200) {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gerengw1"));
			} else {
				if (pc.getLevel() < 30) {
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gerengTe1"));
				} else {
					Quest q = QuestController.find(pc, Lineage.QUEST_WIZARD_LV30);
					if (q == null)
						q = QuestController.newQuest(pc, this, Lineage.QUEST_WIZARD_LV30);
					switch (q.getQuestStep()) {
					case 0:
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gerengT1"));
						break;
					case 1:
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gerengT2"));
						break;
					case 2:
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gerengT3"));
						break;
					case 3:
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gerengT4"));
						break;
					default:
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gerengw1"));
						break;
					}
				}
			}
			break;
		case 0x04:
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gerengp1"));
			break;
		}
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		Quest q = QuestController.find(pc, Lineage.QUEST_WIZARD_LV30);
		if (q != null) {
			Item craft = craft_list.get(action);
			if (action.equalsIgnoreCase("quest 12 gerengT2")) {
				// 게렝을 제안을 받아 들인다
				q.setQuestStep(1);
				toTalk(pc, null);
			} else if (action.equalsIgnoreCase("quest 14 gerengT4") && q.getQuestStep() == 2) {
				// 마법사에게 필요한 것을 요구한다
				q.setQuestStep(3);
				toTalk(pc, null);
			} else if (action.equalsIgnoreCase("request bone piece of undead")) {
				// 언데드의 뼈를 건네 준다.
				if (q != null && q.getQuestStep() == 1) {
					List<Craft> l = list.get(craft);
					// 재료 확인.
					if (CraftController.isCraft(pc, l, true)) {
						// 퀘스트 스탭 변경.
						q.setQuestStep(2);
						// 안내창 띄우기.
						toTalk(pc, null);
					}
				}
			} else if (action.equalsIgnoreCase("request mystery staff")) {
				// 신비한 수정구를 건네 준다
				if (craft != null && q != null && q.getQuestStep() == 3) {
					List<Craft> l = list.get(craft);
					// 재료 확인.
					if (CraftController.isCraft(pc, l, true)) {
						// 재료 제거
						CraftController.toCraft(pc, l);
						// 아이템 지급.
						CraftController.toCraft(this, pc, craft, 1, true);
						// 퀘스트 스탭 변경.
						q.setQuestStep(4);
						// 안내창 띄우기.
						toTalk(pc, null);
					}
				}
			}
		}
	}
}
