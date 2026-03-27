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
import lineage.network.packet.server.S_HyperText;
import lineage.share.Lineage;
import lineage.world.controller.CraftController;
import lineage.world.controller.QuestController;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.QuestInstance;

public class Talass extends QuestInstance {

	public Talass(Npc npc) {
		super(npc);

		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add(npc.getNameId());

		// 제작 처리 초기화.
		Item i = ItemDatabase.find("사이하의 활");
		if (i != null) {
			craft_list.put("request bow of sayha", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("장궁"), 1));
			l.add(new Craft(ItemDatabase.find("풍룡 비늘"), 15));
			l.add(new Craft(ItemDatabase.find("그리폰의 깃털"), 30));
			l.add(new Craft(ItemDatabase.find("바람의 눈물"), 50));
			list.put(i, l);
		}
		i = ItemDatabase.find("타라스의 부츠");
		if (i != null) {
			craft_list.put("request boots of talass", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("데몬의 부츠"), 1));
			l.add(new Craft(ItemDatabase.find("마물의 기운"), 100));
			l.add(new Craft(ItemDatabase.find("아데나"), 100000));
			list.put(i, l);
		}
		i = ItemDatabase.find("타라스의 장갑");
		if (i != null) {
			craft_list.put("request gloves of talass", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("데몬의 장갑"), 1));
			l.add(new Craft(ItemDatabase.find("마물의 기운"), 150));
			l.add(new Craft(ItemDatabase.find("아데나"), 100000));
			list.put(i, l);
		}

		i = ItemDatabase.find("수정 지팡이");
		if (i != null) {
			craft_list.put("request crystal staff", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("신비한 지팡이"), 1));
			l.add(new Craft(ItemDatabase.find("언데드의 뼈"), 1));
			list.put(i, l);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		switch (pc.getClassType()) {
		case Lineage.LINEAGE_CLASS_WIZARD:
			Quest q = QuestController.find(pc, Lineage.QUEST_WIZARD_LV30);
			if (q == null) {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "talass"));
			} else {
				switch (q.getQuestStep()) {
				case 4:
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "talassE1"));
					break;
				case 5:
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "talassE2"));
					break;
				default:
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "talass"));
					break;
				}
			}
			break;
		default:
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "talass"));
			break;
		}
	}

	/**
	 * 제작처리 마지막 부분. : 중복코드 방지용
	 * 
	 * @param pc
	 * @param action
	 * @param count
	 */
	private void toLeather(PcInstance pc, String action, long count) {
		Item craft = craft_list.get(action);

		if (craft != null) {
			int max = CraftController.getMax(pc, list.get(craft));
			if (count > 0 && max > 0 && count <= max) {
				// 재료 제거
				for (int i = 0; i < count; ++i)
					CraftController.toCraft(pc, list.get(craft));
				// 제작 아이템 지급.
				int jegop = craft.getListCraft().get(action) == null ? 0 : craft.getListCraft().get(action);
				if (jegop == 0)
					CraftController.toCraft(this, pc, craft, count, true);
				else
					CraftController.toCraft(this, pc, craft, count * jegop, true);
			}
		}
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
	    if (pc.getInventory() != null) {
	        Item craft = craft_list.get(action);
	        // 재료가 부족 할 시 창 닫기
	        pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));

	        if (craft != null) {
	            processCraft(pc, action, craft);
	        }

	        if (action.equalsIgnoreCase("quest 16 talassE2")) {
	            // 언데드의 뼈
	            Quest q = QuestController.find(pc, Lineage.QUEST_WIZARD_LV30);
	            if (q == null || q.getQuestStep() != 4) {
	                toTalk(pc, null);
	            } else {
	                q.setQuestStep(5);
	                toTalk(pc, null);
	            }
	        } else if (action.equalsIgnoreCase("request crystal staff")) {
	            // 언데드의 뼈조각을 건네 준다.
	            Quest q = QuestController.find(pc, Lineage.QUEST_WIZARD_LV30);
	            if (craft != null && q != null && q.getQuestStep() == 5) {
	                List<Craft> l = list.get(craft);
	                // 재료 확인.
	                if (CraftController.isCraft(pc, l, true)) {
	                    // 재료 제거
	                    CraftController.toCraft(pc, l);
	                    // 아이템 지급.
	                    CraftController.toCraft(this, pc, craft, 1, true);
	                    // 퀘스트 스탭 변경.
	                    q.setQuestStep(6);
	                    // 안내창 띄우기.
	                    toTalk(pc, null);
	                }
	            }
	        } else {
	        }
	    }
	}

	private void processCraft(PcInstance pc, String action, Item craft) {
	    // 재료 확인.
	    if (CraftController.isCraft(pc, list.get(craft), true)) {
	        // 제작 가능한 최대값 추출.
	        int max = CraftController.getMax(pc, list.get(craft));
	        if (Lineage.server_version <= 144)
	            toLeather(pc, action, max);
	        else
	            // 패킷 처리.
	            pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "talass9", action, 0, 1, 1, max, temp_request_list));
	    }
	}
}