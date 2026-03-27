package lineage.world.object.npc.craft;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.lineage.Craft;
import lineage.database.ItemDatabase;
import lineage.database.NpcSpawnlistDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_HyperText;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_MessageYesNo;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.controller.CraftController;
import lineage.world.object.object;
import lineage.world.object.instance.CraftInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.npc.guard.PatrolGuard;

public class Nerupa extends CraftInstance {

	public Nerupa(Npc npc) {
		super(npc);

		Item i = ItemDatabase.find("페어리 더스트");
		if (i != null) {
	        i.getListCraft().put("request fairydust", 20);
			craft_list.put("request fairydust", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("정령의 돌"), 1));
			list.put(i, l);
		}
		
		i = ItemDatabase.find("화살");
		if (i != null) {
			i.getListCraft().put("request arrow", 10);
			craft_list.put("request arrow", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("엔트의 줄기"), 1));
			list.put(i, l);
		}
		
		i = ItemDatabase.find("미스릴 화살");
		if (i != null) {
			i.getListCraft().put("request mithril arrow", 100);
			craft_list.put("request mithril arrow", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("엔트의 줄기"), 1));
			l.add(new Craft(ItemDatabase.find("미스릴"), 1));
			list.put(i, l);
		}
	}

	public class CreateItem {
		public String itemName;
		public boolean isCheckBless;
		public int bless;
		public boolean isCheckEnchant;
		public int enchant;
		public int count;
		public int yitem = 0;

		/**
		 * @param itemName
		 *            : 재료 아이템 이름
		 * @param isCheckBless
		 *            : 재료 축여부 체크
		 * @param bless
		 *            : 축복(0~2)
		 * @param isCheckEnchant
		 *            : 재료 인첸트 체크
		 * @param enchant
		 *            : 인첸트
		 * @param count
		 *            : 수량
		 */
		public CreateItem(String itemName, boolean isCheckBless, int bless, boolean isCheckEnchant, int enchant, int count) {
			this.itemName = itemName;
			this.isCheckBless = isCheckBless;
			this.bless = bless;
			this.isCheckEnchant = isCheckEnchant;
			this.enchant = enchant;
			this.count = count;
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(pc.getClassType()==Lineage.LINEAGE_CLASS_ELF || pc.getGm()>0){
			if(pc.getLawful()<Lineage.NEUTRAL && pc.getGm()==0){
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "nerupaCE1"));
			}else{
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "nerupaE1"));
			}
		}else{
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "nerupaM1"));
		}
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (pc.getInventory() != null) {
			List<CreateItem> createList = new ArrayList<CreateItem>();
			List<CreateItem> createList2 = new ArrayList<CreateItem>();
			List<ItemInstance> itemList = new ArrayList<ItemInstance>();
			
			Item craft = craft_list.get(action);
			if (craft != null) {
				// 페어리 더스트
				if (action.equalsIgnoreCase("request fairydust")) {
					// 재료 확인.
					if(CraftController.isCraft(pc, list.get(craft), true)){
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if(Lineage.server_version <= 144)
							toNerupa(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "request", action, 0, 1, 1, max, temp_request_list));
					}
				}
				// 화살
				if (action.equalsIgnoreCase("request arrow")) {
					// 재료 확인.
					if(CraftController.isCraft(pc, list.get(craft), true)){
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if(Lineage.server_version <= 144)
							toNerupa(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "request", action, 0, 1, 1, max, temp_request_list));
					}
				}
				// 미스릴화살
				if (action.equalsIgnoreCase("request mithril arrow")) {
					// 재료 확인.
					if(CraftController.isCraft(pc, list.get(craft), true)){
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if(Lineage.server_version <= 144)
							toNerupa(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "request", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			// 활
			if (action.equalsIgnoreCase("request bow")) {
				int rnd = Util.random(0, 100);
				
				createList.add(new CreateItem("엔트의 줄기", false, 1, false, 0, 1));
				createList.add(new CreateItem("실", false, 1, false, 0, 5));			
				checkItem(pc, createList, itemList);
				int success_probability = 20;
				
				if (rnd < success_probability) {
					createItem(pc, createList, createList2, itemList, "활", 0, 0, 1);
				} else {
					createItem(pc, createList, createList2, itemList, "활", 1, 0, 1);
				}
			}
			// 요정족 활
			if (action.equalsIgnoreCase("request elven bow")) {
				int rnd = Util.random(0, 100);
				
				createList.add(new CreateItem("엔트의 줄기", false, 1, false, 0, 10));
				createList.add(new CreateItem("미스릴", false, 1, false, 0, 20));		
				createList.add(new CreateItem("실", false, 1, false, 0, 2));	
				createList.add(new CreateItem("아라크네의 허물", false, 1, false, 0, 2));	
				checkItem(pc, createList, itemList);
				int success_probability = 20;
				
				if (rnd < success_probability) {
					createItem(pc, createList, createList2, itemList, "요정족 활", 0, 0, 1);
				} else {
					createItem(pc, createList, createList2, itemList, "요정족 활", 1, 0, 1);
				}
			}
			// 크로스 보우
			if (action.equalsIgnoreCase("request crossbow")) {
				int rnd = Util.random(0, 100);
				
				createList.add(new CreateItem("페어리의 날개", false, 1, false, 0, 8));
				createList.add(new CreateItem("오리하루콘 판금", false, 1, false, 0, 3));
				createList.add(new CreateItem("미스릴 실", false, 1, false, 0, 20));
				createList.add(new CreateItem("아라크네의 거미줄", false, 1, false, 0, 30));
				checkItem(pc, createList, itemList);
				int success_probability = 20;
				
				if (rnd < success_probability) {
					createItem(pc, createList, createList2, itemList, "크로스 보우", 0, 0, 1);
				} else {
					createItem(pc, createList, createList2, itemList, "크로스 보우", 1, 0, 1);
				}
			}
			// 장궁
			if (action.equalsIgnoreCase("request yumi")) {
				int rnd = Util.random(0, 100);
				
				createList.add(new CreateItem("오리하루콘 도금 뿔", false, 1, false, 0, 1));
				createList.add(new CreateItem("오리하루콘 판금", false, 1, false, 0, 6));
				createList.add(new CreateItem("미스릴 실", false, 1, false, 0, 40));
				createList.add(new CreateItem("고급 에메랄드", false, 1, false, 0, 2));
				createList.add(new CreateItem("고급 다이아몬드", false, 1, false, 0, 1));
				createList.add(new CreateItem("아라크네의 허물", false, 1, false, 0, 5));
				checkItem(pc, createList, itemList);
				int success_probability = 20;
				
				if (rnd < success_probability) {
					createItem(pc, createList, createList2, itemList, "장궁", 0, 0, 1);
				} else {
					createItem(pc, createList, createList2, itemList, "장궁", 1, 0, 1);
				}
			}
			// 요정족 단검
			if (action.equalsIgnoreCase("request elven dagger")) {
				int rnd = Util.random(0, 100);
				
				createList.add(new CreateItem("엔트의 줄기", false, 1, false, 0, 5));
				createList.add(new CreateItem("미스릴", false, 1, false, 0, 20));		
				checkItem(pc, createList, itemList);
				int success_probability = 20;
				
				if (rnd < success_probability) {
					createItem(pc, createList, createList2, itemList, "요정족 단검", 0, 0, 1);
				} else {
					createItem(pc, createList, createList2, itemList, "요정족 단검", 1, 0, 1);
				}
			}
			// 메일 브레이커
			if (action.equalsIgnoreCase("request mail breaker")) {
				int rnd = Util.random(0, 100);
				
				createList.add(new CreateItem("미스릴 도금 뿔", false, 1, false, 0, 1));
				createList.add(new CreateItem("엔트의 줄기", false, 1, false, 0, 10));
				createList.add(new CreateItem("단검신", false, 1, false, 0, 1));
				createList.add(new CreateItem("다이아몬드", false, 1, false, 0, 1));
				createList.add(new CreateItem("아라크네의 거미줄", false, 1, false, 0, 50));
				checkItem(pc, createList, itemList);
				int success_probability = 20;
				
				if (rnd < success_probability) {
					createItem(pc, createList, createList2, itemList, "메일 브레이커", 0, 0, 1);
				} else {
					createItem(pc, createList, createList2, itemList, "메일 브레이커", 1, 0, 1);
				}
			}
			// 요정족 검
			if (action.equalsIgnoreCase("request elven short sword")) {
				int rnd = Util.random(0, 100);
				
				createList.add(new CreateItem("장검신", false, 1, false, 0, 1));
				createList.add(new CreateItem("엔트의 줄기", false, 1, false, 0, 5));
				createList.add(new CreateItem("미스릴", false, 1, false, 0, 150));
				createList.add(new CreateItem("아라크네의 거미줄", false, 1, false, 0, 150));
				checkItem(pc, createList, itemList);
				int success_probability = 20;
				
				if (rnd < success_probability) {
					createItem(pc, createList, createList2, itemList, "요정족 검", 0, 0, 1);
				} else {
					createItem(pc, createList, createList2, itemList, "요정족 검", 1, 0, 1);
				}
			}
			// 레이피어
			if (action.equalsIgnoreCase("request rapier")) {
				int rnd = Util.random(0, 100);
				
				createList.add(new CreateItem("오리하루콘 검신", false, 1, false, 0, 1));
				createList.add(new CreateItem("페어리의 날개", false, 1, false, 0, 2));
				createList.add(new CreateItem("오리하루콘", false, 1, false, 0, 50));
				createList.add(new CreateItem("고급 루비", false, 1, false, 0, 1));
				createList.add(new CreateItem("아라크네의 거미줄", false, 1, false, 0, 25));
				checkItem(pc, createList, itemList);
				int success_probability = 20;
				
				if (rnd < success_probability) {
					createItem(pc, createList, createList2, itemList, "레이피어", 0, 0, 1);
				} else {
					createItem(pc, createList, createList2, itemList, "레이피어", 1, 0, 1);
				}
			}
			// 몽둥이
			if (action.equalsIgnoreCase("request club")) {
				int rnd = Util.random(0, 100);
				
				createList.add(new CreateItem("엔트의 줄기", false, 1, false, 0, 10));
				createList.add(new CreateItem("아라크네의 거미줄", false, 1, false, 0, 5));
				checkItem(pc, createList, itemList);
				int success_probability = 20;
				
				if (rnd < success_probability) {
					createItem(pc, createList, createList2, itemList, "몽둥이", 0, 0, 1);
				} else {
					createItem(pc, createList, createList2, itemList, "몽둥이", 1, 0, 1);
				}
			}
			// 전투 도끼
			if (action.equalsIgnoreCase("request battle axe")) {
				int rnd = Util.random(0, 100);
				
				createList.add(new CreateItem("단검신", false, 1, false, 0, 1));
				createList.add(new CreateItem("아라크네의 거미줄", false, 1, false, 0, 5));
				createList.add(new CreateItem("엔트의 줄기", false, 1, false, 0, 10));
				createList.add(new CreateItem("미스릴", false, 1, false, 0, 60));
				checkItem(pc, createList, itemList);
				int success_probability = 20;
				
				if (rnd < success_probability) {
					createItem(pc, createList, createList2, itemList, "전투 도끼", 0, 0, 1);
				} else {
					createItem(pc, createList, createList2, itemList, "전투 도끼", 1, 0, 1);
				}
			}
			// 귀사름
			if (action.equalsIgnoreCase("request guisarme")) {
				int rnd = Util.random(0, 100);
				
				createList.add(new CreateItem("단검신", false, 1, false, 0, 1));
				createList.add(new CreateItem("아라크네의 거미줄", false, 1, false, 0, 10));
				createList.add(new CreateItem("엔트의 줄기", false, 1, false, 0, 10));
				createList.add(new CreateItem("미스릴", false, 1, false, 0, 90));
				checkItem(pc, createList, itemList);
				int success_probability = 20;
				
				if (rnd < success_probability) {
					createItem(pc, createList, createList2, itemList, "귀사름", 0, 0, 1);
				} else {
					createItem(pc, createList, createList2, itemList, "귀사름", 1, 0, 1);
				}
			}
			// 요정족 창
			if (action.equalsIgnoreCase("request elven spear")) {
				int rnd = Util.random(0, 100);
				
				createList.add(new CreateItem("미스릴 도금 뿔", false, 1, false, 0, 1));
				createList.add(new CreateItem("아라크네의 거미줄", false, 1, false, 0, 30));
				createList.add(new CreateItem("엔트의 줄기", false, 1, false, 0, 10));
				checkItem(pc, createList, itemList);
				int success_probability = 20;
				
				if (rnd < success_probability) {
					createItem(pc, createList, createList2, itemList, "요정족 창", 0, 0, 1);
				} else {
					createItem(pc, createList, createList2, itemList, "요정족 창", 1, 0, 1);
				}
			}
			// 포챠드
			if (action.equalsIgnoreCase("request fauchard")) {
				int rnd = Util.random(0, 100);
				
				createList.add(new CreateItem("오리하루콘 도금 뿔", false, 1, false, 0, 1));
				createList.add(new CreateItem("아라크네의 거미줄", false, 1, false, 0, 50));
				createList.add(new CreateItem("오리하루콘", false, 1, false, 0, 60));
				createList.add(new CreateItem("고급 루비", false, 1, false, 0, 1));
				checkItem(pc, createList, itemList);
				int success_probability = 20;
				
				if (rnd < success_probability) {
					createItem(pc, createList, createList2, itemList, "포챠드", 0, 0, 1);
				} else {
					createItem(pc, createList, createList2, itemList, "포챠드", 1, 0, 1);
				}
			}
			// 나무 줄기 옷
			if (action.equalsIgnoreCase("request wooden jacket")) {
				int rnd = Util.random(0, 100);
				
				createList.add(new CreateItem("엔트의 줄기", false, 1, false, 0, 10));
				createList.add(new CreateItem("실", false, 1, false, 0, 6));
				checkItem(pc, createList, itemList);
				int success_probability = 20;
				
				if (rnd < success_probability) {
					createItem(pc, createList, createList2, itemList, "나무 줄기 옷", 0, 0, 1);
				} else {
					createItem(pc, createList, createList2, itemList, "나무 줄기 옷", 1, 0, 1);
				}
			}
			// 나무 갑옷
			if (action.equalsIgnoreCase("request wooden armor")) {
				int rnd = Util.random(0, 100);
				
				createList.add(new CreateItem("엔트의 껍질", false, 1, false, 0, 2));
				createList.add(new CreateItem("판의 갈기털", false, 1, false, 0, 5));
				checkItem(pc, createList, itemList);
				int success_probability = 20;
				
				if (rnd < success_probability) {
					createItem(pc, createList, createList2, itemList, "나무 갑옷", 0, 0, 1);
				} else {
					createItem(pc, createList, createList2, itemList, "나무 갑옷", 1, 0, 1);
				}
			}
			// 요정족 흉갑
			if (action.equalsIgnoreCase("request elven breast plate")) {
				int rnd = Util.random(0, 100);
				
				createList.add(new CreateItem("아라크네의 허물", false, 1, false, 0, 2));
				createList.add(new CreateItem("실", false, 1, false, 0, 10));
				checkItem(pc, createList, itemList);
				int success_probability = 20;
				
				if (rnd < success_probability) {
					createItem(pc, createList, createList2, itemList, "요정족 흉갑", 0, 0, 1);
				} else {
					createItem(pc, createList, createList2, itemList, "요정족 흉갑", 1, 0, 1);
				}
			}
			// 요정족 사슬 갑옷
			if (action.equalsIgnoreCase("request elven chain mail")) {
				int rnd = Util.random(0, 100);
				
				createList.add(new CreateItem("미스릴 판금", false, 1, false, 0, 4));
				createList.add(new CreateItem("미스릴 실", false, 1, false, 0, 10));
				checkItem(pc, createList, itemList);
				int success_probability = 20;
				
				if (rnd < success_probability) {
					createItem(pc, createList, createList2, itemList, "요정족 사슬 갑옷", 0, 0, 1);
				} else {
					createItem(pc, createList, createList2, itemList, "요정족 사슬 갑옷", 1, 0, 1);
				}
			}
			// 요정족 판금 갑옷
			if (action.equalsIgnoreCase("request elven plate mail")) {
				int rnd = Util.random(0, 100);
				
				createList.add(new CreateItem("오리하루콘 판금", false, 1, false, 0, 8));
				createList.add(new CreateItem("미스릴 실", false, 1, false, 0, 20));
				createList.add(new CreateItem("최고급 다이아몬드", false, 1, false, 0, 1));
				checkItem(pc, createList, itemList);
				int success_probability = 20;
				
				if (rnd < success_probability) {
					createItem(pc, createList, createList2, itemList, "요정족 판금 갑옷", 0, 0, 1);
				} else {
					createItem(pc, createList, createList2, itemList, "요정족 판금 갑옷", 1, 0, 1);
				}
			}
			// 나무 방패
			if (action.equalsIgnoreCase("request wooden shield")) {
				int rnd = Util.random(0, 100);
				
				createList.add(new CreateItem("엔트의 껍질", false, 1, false, 0, 1));
				createList.add(new CreateItem("아라크네의 거미줄", false, 1, false, 0, 5));
				createList.add(new CreateItem("엔트의 줄기", false, 1, false, 0, 5));
				checkItem(pc, createList, itemList);
				int success_probability = 20;
				
				if (rnd < success_probability) {
					createItem(pc, createList, createList2, itemList, "나무 방패", 0, 0, 1);
				} else {
					createItem(pc, createList, createList2, itemList, "나무 방패", 1, 0, 1);
				}
			}
			// 요정족 방패
			if (action.equalsIgnoreCase("request elven shield")) {
				int rnd = Util.random(0, 100);
				
				createList.add(new CreateItem("미스릴 판금", false, 1, false, 0, 2));
				createList.add(new CreateItem("아라크네의 거미줄", false, 1, false, 0, 5));
				createList.add(new CreateItem("나무 방패", false, 1, false, 0, 1));
				checkItem(pc, createList, itemList);
				int success_probability = 20;
				
				if (rnd < success_probability) {
					createItem(pc, createList, createList2, itemList, "요정족 방패", 0, 0, 1);
				} else {
					createItem(pc, createList, createList2, itemList, "요정족 방패", 1, 0, 1);
				}
			}
			// 요정족 투구
			if (action.equalsIgnoreCase("request elven leather helm")) {
				int rnd = Util.random(0, 100);
				
				createList.add(new CreateItem("페어리의 날개", false, 1, false, 0, 1));
				createList.add(new CreateItem("엔트의 껍질", false, 1, false, 0, 2));
				createList.add(new CreateItem("판의 갈기털", false, 1, false, 0, 10));
				createList.add(new CreateItem("아라크네의 거미줄", false, 1, false, 0, 20));
				checkItem(pc, createList, itemList);
				int success_probability = 20;
				
				if (rnd < success_probability) {
					createItem(pc, createList, createList2, itemList, "요정족 투구", 0, 0, 1);
				} else {
					createItem(pc, createList, createList2, itemList, "요정족 투구", 1, 0, 1);
				}
			}
			// 엘름의 축복
			if (action.equalsIgnoreCase("request bless of elm")) {
				int rnd = Util.random(0, 100);
				
				createList.add(new CreateItem("요정족 투구", false, 1, false, 0, 1));
				createList.add(new CreateItem("고급 다이아몬드", false, 1, false, 0, 1));
				createList.add(new CreateItem("고급 에메랄드", false, 1, false, 0, 1));
				createList.add(new CreateItem("고급 사파이어", false, 1, false, 0, 1));
				createList.add(new CreateItem("마력의 돌", false, 1, false, 0, 5));
				createList.add(new CreateItem("오리하루콘 판금", false, 1, false, 0, 3));
				createList.add(new CreateItem("미스릴 실", false, 1, false, 0, 150));
				checkItem(pc, createList, itemList);
				int success_probability = 20;
				
				if (rnd < success_probability) {
					createItem(pc, createList, createList2, itemList, "엘름의 축복", 0, 0, 1);
				} else {
					createItem(pc, createList, createList2, itemList, "엘름의 축복", 1, 0, 1);
				}
			}
			// 활 골무
			if (action.equalsIgnoreCase("request bracer")) {
				int rnd = Util.random(0, 100);
				
				createList.add(new CreateItem("미스릴 실", false, 1, false, 0, 20));
				createList.add(new CreateItem("엔트의 껍질", false, 1, false, 0, 3));
				checkItem(pc, createList, itemList);
				int success_probability = 20;
				
				if (rnd < success_probability) {
					createItem(pc, createList, createList2, itemList, "활 골무", 0, 0, 1);
				} else {
					createItem(pc, createList, createList2, itemList, "활 골무", 1, 0, 1);
				}
			}
			// 파워 글로브
			if (action.equalsIgnoreCase("request power gloves")) {
				int rnd = Util.random(0, 100);
				
				createList.add(new CreateItem("아라크네의 허물", false, 1, false, 0, 5));
				createList.add(new CreateItem("미스릴 실", false, 1, false, 0, 20));
				createList.add(new CreateItem("오우거의 피", false, 1, false, 0, 1));
				createList.add(new CreateItem("고급 다이아몬드", false, 1, false, 0, 1));
				checkItem(pc, createList, itemList);
				int success_probability = 20;
				
				if (rnd < success_probability) {
					createItem(pc, createList, createList2, itemList, "파워 글로브", 0, 0, 1);
				} else {
					createItem(pc, createList, createList2, itemList, "파워 글로브", 1, 0, 1);
				}
			}
			// 요정족 망토
			if (action.equalsIgnoreCase("request elven cloak")) {
				int rnd = Util.random(0, 100);
				
				createList.add(new CreateItem("마력의 돌", false, 1, false, 0, 2));
				createList.add(new CreateItem("페어리 더스트", false, 1, false, 0, 120));
				createList.add(new CreateItem("미스릴 실", false, 1, false, 0, 10));
				checkItem(pc, createList, itemList);
				int success_probability = 20;
				
				if (rnd < success_probability) {
					createItem(pc, createList, createList2, itemList, "요정족 망토", 0, 0, 1);
				} else {
					createItem(pc, createList, createList2, itemList, "요정족 망토", 1, 0, 1);
				}
			}
			// 짧은 장화
			if (action.equalsIgnoreCase("request low boots")) {
				int rnd = Util.random(0, 100);
				
				createList.add(new CreateItem("엔트의 껍질", false, 1, false, 0, 2));
				createList.add(new CreateItem("실", false, 1, false, 0, 4));
				checkItem(pc, createList, itemList);
				int success_probability = 20;
				
				if (rnd < success_probability) {
					createItem(pc, createList, createList2, itemList, "짧은 장화", 0, 0, 1);
				} else {
					createItem(pc, createList, createList2, itemList, "짧은 장화", 1, 0, 1);
				}
			}
			// 부츠
			if (action.equalsIgnoreCase("request boots")) {
				int rnd = Util.random(0, 100);
				
				createList.add(new CreateItem("아라크네의 허물", false, 1, false, 0, 2));
				createList.add(new CreateItem("실", false, 1, false, 0, 10));
				checkItem(pc, createList, itemList);
				int success_probability = 20;
				
				if (rnd < success_probability) {
					createItem(pc, createList, createList2, itemList, "부츠", 0, 0, 1);
				} else {
					createItem(pc, createList, createList2, itemList, "부츠", 1, 0, 1);
				}
			}
		}
	}

	public void checkItem(PcInstance pc, List<CreateItem> createList, List<ItemInstance> itemList) {
		if (createList != null && itemList != null) {
			if (itemList.size() > 0)
				itemList.clear();

			for (CreateItem list : createList) {
				for (ItemInstance i : pc.getInventory().getList()) {
					if (i.getItem() != null && i.getItem().getName().equalsIgnoreCase(list.itemName) && i.getCount() >= list.count && !i.isEquipped()) {
						// 축여부 체크일 경우
						if (list.isCheckBless) {
							// 인첸트 체크일 경우
							if (list.isCheckEnchant) {
								if (i.getBless() == list.bless && i.getEnLevel() == list.enchant) {
									itemList.add(i);
									break;
								}
							} else {
								if (i.getBless() == list.bless) {
									itemList.add(i);
									break;
								}
							}
						} else {
							// 인첸트 체크일 경우
							if (list.isCheckEnchant) {
								if (i.getEnLevel() == list.enchant) {
									itemList.add(i);
									break;
								}
							} else {
								itemList.add(i);
								break;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 제작처리 마지막 부분.
	 *  : 중복코드 방지용
	 * @param pc
	 * @param action
	 * @param count
	 */
	private void toNerupa(PcInstance pc, String action, long count){
		Item craft = craft_list.get(action);
		
		if(craft != null){
			int max = CraftController.getMax(pc, list.get(craft));
			if(count>0 && max>0 && count<=max){
				// 재료 제거
				for(int i=0 ; i<count ; ++i)
					CraftController.toCraft(pc, list.get(craft));
				// 제작 아이템 지급.
				int jegop = craft.getListCraft().get(action)==null ? 0 : craft.getListCraft().get(action);
				if(jegop == 0)
					CraftController.toCraft(this, pc, craft, count, true);
				else
					CraftController.toCraft(this, pc, craft, count*jegop, true);
			}
		}
	}

	public void createItem(PcInstance pc, List<CreateItem> createList, List<CreateItem> createList2, List<ItemInstance> itemList, String createItemName, int bless, int enchant, int count) {
		if ((createList.size() > 0 && itemList.size() > 0 && createList.size() == itemList.size()) || (createList2.size() > 0 && itemList.size() > 0 && createList2.size() == itemList.size())) {

			Item i = ItemDatabase.find(createItemName);

			if (i != null) {
				ItemInstance temp = pc.getInventory().find(i.getName(), bless, i.isPiles());

				if (temp != null && (temp.getBless() != bless || temp.getEnLevel() != enchant))
					temp = null;

				if (temp == null) {
					// 겹칠수 있는 아이템이 존재하지 않을경우.
					if (i.isPiles()) {
						temp = ItemDatabase.newInstance(i);
						temp.setObjectId(ServerDatabase.nextItemObjId());
						temp.setBless(bless);
						temp.setEnLevel(enchant);
						temp.setCount(count);
						temp.setDefinite(false);
						pc.getInventory().append(temp, true);
					} else {
						for (int idx = 0; idx < count; idx++) {
							temp = ItemDatabase.newInstance(i);
							temp.setObjectId(ServerDatabase.nextItemObjId());
							temp.setBless(bless);
							temp.setEnLevel(enchant);
							temp.setDefinite(false);
							pc.getInventory().append(temp, true);
						}
					}
				} else {
					// 겹치는 아이템이 존재할 경우.
					pc.getInventory().count(temp, temp.getCount() + count, true);
				}

				if (createList2.size() == 0) {
					for (CreateItem list : createList) {
						for (ItemInstance item : itemList) {
							if (item != null && item.getItem() != null && list.itemName.equalsIgnoreCase(item.getItem().getName()))
								pc.getInventory().count(item, item.getCount() - list.count, true);
						}
					}
				} else {
					for (CreateItem list : createList2) {
						for (ItemInstance item : itemList) {
							if (item != null && item.getItem() != null && list.itemName.equalsIgnoreCase(item.getItem().getName()))
								pc.getInventory().count(item, item.getCount() - list.count, true);
						}
					}
				}
				ChattingController.toChatting(pc, String.format("%s을 제작하였습니다.", createItemName), Lineage.CHATTING_MODE_MESSAGE);
				// 창 제거
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));
			}
		} else {
			String msg = "";

			if (createList2.size() > 0) {
				int idx = 0;

				for (CreateItem list : createList) {
					idx++;

					if (list.enchant > 0 && list.count > 1)
						msg += String.format("+%d %s(%,d)", list.enchant, list.itemName, list.count);
					else if (list.enchant > 0 && list.count == 1)
						msg += String.format("+%d %s", list.enchant, list.itemName);
					else if (list.enchant == 0 && list.count > 1)
						msg += String.format("%s(%,d)", list.itemName, list.count);
					else if (list.enchant == 0 && list.count == 1)
						msg += String.format("%s", list.itemName);

					if (idx < createList.size())
						msg += ", ";
				}

				idx = 0;
				msg += " 또는 ";

				for (CreateItem list : createList2) {
					idx++;

					if (list.enchant > 0 && list.count > 1)
						msg += String.format("+%d %s(%,d)", list.enchant, list.itemName, list.count);
					else if (list.enchant > 0 && list.count == 1)
						msg += String.format("+%d %s", list.enchant, list.itemName);
					else if (list.enchant == 0 && list.count > 1)
						msg += String.format("%s(%,d)", list.itemName, list.count);
					else if (list.enchant == 0 && list.count == 1)
						msg += String.format("%s", list.itemName);

					if (idx < createList2.size())
						msg += ", ";
				}
			} else {
				int idx = 0;

				for (CreateItem list : createList) {
					idx++;

					if (list.enchant > 0 && list.count > 1)
						msg += String.format("+%d %s(%,d)", list.enchant, list.itemName, list.count);
					else if (list.enchant > 0 && list.count == 1)
						msg += String.format("+%d %s", list.enchant, list.itemName);
					else if (list.enchant == 0 && list.count > 1)
						msg += String.format("%s(%,d)", list.itemName, list.count);
					else if (list.enchant == 0 && list.count == 1)
						msg += String.format("%s", list.itemName);

					if (idx < createList.size())
						msg += ", ";
				}
			}
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 337, msg));
			// 창 제거
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));
		}
	}
}