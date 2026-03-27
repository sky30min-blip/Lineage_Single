package lineage.world.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lineage.bean.database.Item;
import lineage.bean.lineage.Craft;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.npc.background.MagicFirewood;

public class CookController {

	static private Map<Item, List<Craft>> craft_list; // 지급아이템에 연결된 재료목록.
	static private Map<String, Item> request_list; // 요청문장에 연결된 지급 아이템.

	static public void init() {
		//
		craft_list = new HashMap<Item, List<Craft>>();
		request_list = new HashMap<String, Item>();
		// 괴물눈 스테이크
		List<Craft> l = new ArrayList<Craft>();
		l.add(new Craft(ItemDatabase.find("괴물 눈 고기"), 1));
		craft_list.put(ItemDatabase.find("괴물눈 스테이크"), l);
		craft_list.put(ItemDatabase.find("환상의 괴물눈 스테이크"), l);
		request_list.put("request cook 0", ItemDatabase.find("괴물눈 스테이크"));
		request_list.put("request cook 011", ItemDatabase.find("환상의 괴물눈 스테이크"));
		// 곰고기 구이
		l = new ArrayList<Craft>();
		l.add(new Craft(ItemDatabase.find("곰 고기"), 1));
		craft_list.put(ItemDatabase.find("곰고기 구이"), l);
		craft_list.put(ItemDatabase.find("환상의 곰고기 구이"), l);
		request_list.put("request cook 1", ItemDatabase.find("곰고기 구이"));
		request_list.put("request cook 111", ItemDatabase.find("환상의 곰고기 구이"));
		// 씨호떡
		l = new ArrayList<Craft>();
		l.add(new Craft(ItemDatabase.find("해바라기씨"), 1));
		l.add(new Craft(ItemDatabase.find("꿀"), 1));
		craft_list.put(ItemDatabase.find("씨호떡"), l);
		craft_list.put(ItemDatabase.find("환상의 씨호떡"), l);
		request_list.put("request cook 2", ItemDatabase.find("씨호떡"));
		request_list.put("request cook 211", ItemDatabase.find("환상의 씨호떡"));
		// 개미다리 치즈구이
		l = new ArrayList<Craft>();
		l.add(new Craft(ItemDatabase.find("개미다리"), 1));
		l.add(new Craft(ItemDatabase.find("치즈"), 1));
		craft_list.put(ItemDatabase.find("개미다리 치즈구이"), l);
		craft_list.put(ItemDatabase.find("환상의 개미다리 치즈구이"), l);
		request_list.put("request cook 3", ItemDatabase.find("개미다리 치즈구이"));
		request_list.put("request cook 311", ItemDatabase.find("환상의 개미다리 치즈구이"));
		// 과일 샐러드
		l = new ArrayList<Craft>();
		l.add(new Craft(ItemDatabase.find("사과"), 1));
		l.add(new Craft(ItemDatabase.find("바나나"), 1));
		l.add(new Craft(ItemDatabase.find("오렌지"), 1));
		craft_list.put(ItemDatabase.find("과일 샐러드"), l);
		craft_list.put(ItemDatabase.find("환상의 과일 샐러드"), l);
		request_list.put("request cook 4", ItemDatabase.find("과일 샐러드"));
		request_list.put("request cook 411", ItemDatabase.find("환상의 과일 샐러드"));
		// 과일 탕수육
		l = new ArrayList<Craft>();
		l.add(new Craft(ItemDatabase.find("고기"), 1));
		l.add(new Craft(ItemDatabase.find("레몬"), 1));
		l.add(new Craft(ItemDatabase.find("당근"), 1));
		craft_list.put(ItemDatabase.find("과일 탕수육"), l);
		craft_list.put(ItemDatabase.find("환상의 과일 탕수육"), l);
		request_list.put("request cook 5", ItemDatabase.find("과일 탕수육"));
		request_list.put("request cook 511", ItemDatabase.find("환상의 과일 탕수육"));
		// 멧돼지 꼬치 구이
		l = new ArrayList<Craft>();
		l.add(new Craft(ItemDatabase.find("멧돼지 고기"), 1));
		craft_list.put(ItemDatabase.find("멧돼지 꼬치 구이"), l);
		craft_list.put(ItemDatabase.find("환상의 멧돼지 꼬치 구이"), l);
		request_list.put("request cook 6", ItemDatabase.find("멧돼지 꼬치 구이"));
		request_list.put("request cook 611", ItemDatabase.find("환상의 멧돼지 꼬치 구이"));
		// 버섯 스프
		l = new ArrayList<Craft>();
		l.add(new Craft(ItemDatabase.find("버섯포자의 즙"), 1));
		l.add(new Craft(ItemDatabase.find("당근"), 1));
		craft_list.put(ItemDatabase.find("버섯 스프"), l);
		craft_list.put(ItemDatabase.find("환상의 버섯 스프"), l);
		request_list.put("request cook 7", ItemDatabase.find("버섯 스프"));
		request_list.put("request cook 711", ItemDatabase.find("환상의 버섯 스프"));

		// 케비어 카나페
		l = new ArrayList<Craft>();
		l.add(new Craft(ItemDatabase.find("상어 알"), 1));
		l.add(new Craft(ItemDatabase.find("모듬 양념 소스"), 1));
		craft_list.put(ItemDatabase.find("캐비어 카나페"), l);
		craft_list.put(ItemDatabase.find("환상의 캐비어 카나페"), l);
		request_list.put("request cook 8", ItemDatabase.find("캐비어 카나페"));
		request_list.put("request cook 811", ItemDatabase.find("환상의 캐비어 카나페"));
		// 악어 스테이크
		l = new ArrayList<Craft>();
		l.add(new Craft(ItemDatabase.find("악어 고기"), 1));
		l.add(new Craft(ItemDatabase.find("모듬 양념 소스"), 1));
		craft_list.put(ItemDatabase.find("악어 스테이크"), l);
		craft_list.put(ItemDatabase.find("환상의 악어 스테이크"), l);
		request_list.put("request cook 9", ItemDatabase.find("악어 스테이크"));
		request_list.put("request cook 911", ItemDatabase.find("환상의 악어 스테이크"));
		// 터틀 드레곤 과자
		l = new ArrayList<Craft>();
		l.add(new Craft(ItemDatabase.find("터틀드래곤의 알"), 1));
		l.add(new Craft(ItemDatabase.find("꿀"), 1));
		l.add(new Craft(ItemDatabase.find("모듬 양념 소스"), 1));
		craft_list.put(ItemDatabase.find("터틀드래곤 과자"), l);
		craft_list.put(ItemDatabase.find("환상의 터틀드래곤 과자"), l);
		request_list.put("request cook 10", ItemDatabase.find("터틀드래곤 과자"));
		request_list.put("request cook 1011", ItemDatabase.find("환상의 터틀드래곤 과자"));
		// 키위 패롯 구이
		l = new ArrayList<Craft>();
		l.add(new Craft(ItemDatabase.find("키위 패롯 고기"), 1));
		l.add(new Craft(ItemDatabase.find("모듬 양념 소스"), 1));
		craft_list.put(ItemDatabase.find("키위 패롯 구이"), l);
		craft_list.put(ItemDatabase.find("환상의 키위 패롯 구이"), l);
		request_list.put("request cook 11", ItemDatabase.find("키위 패롯 구이"));
		request_list.put("request cook 1111", ItemDatabase.find("환상의 키위 패롯 구이"));
		// 스콜피온 구이
		l = new ArrayList<Craft>();
		l.add(new Craft(ItemDatabase.find("스콜피온 살"), 1));
		l.add(new Craft(ItemDatabase.find("모듬 양념 소스"), 1));
		craft_list.put(ItemDatabase.find("스콜피온 구이"), l);
		craft_list.put(ItemDatabase.find("환상의 스콜피온 구이"), l);
		request_list.put("request cook 12", ItemDatabase.find("스콜피온 구이"));
		request_list.put("request cook 1211", ItemDatabase.find("환상의 스콜피온 구이"));
		// 일렉카둠 스튜
		l = new ArrayList<Craft>();
		l.add(new Craft(ItemDatabase.find("일렉카둠 살"), 1));
		l.add(new Craft(ItemDatabase.find("모듬 양념 소스"), 1));
		craft_list.put(ItemDatabase.find("일렉카둠 스튜"), l);
		craft_list.put(ItemDatabase.find("환상의 일렉카둠 스튜"), l);
		request_list.put("request cook 13", ItemDatabase.find("일렉카둠 스튜"));
		request_list.put("request cook 1311", ItemDatabase.find("환상의 일렉카둠 스튜"));
		// 거미다리 꼬치 구이
		l = new ArrayList<Craft>();
		l.add(new Craft(ItemDatabase.find("거미 다리 살"), 1));
		l.add(new Craft(ItemDatabase.find("모듬 양념 소스"), 1));
		craft_list.put(ItemDatabase.find("거미다리 꼬치 구이"), l);
		craft_list.put(ItemDatabase.find("환상의 거미다리 꼬치 구이"), l);
		request_list.put("request cook 14", ItemDatabase.find("거미다리 꼬치 구이"));
		request_list.put("request cook 1411", ItemDatabase.find("환상의 거미다리 꼬치 구이"));
		// 크랩살 스프
		l = new ArrayList<Craft>();
		l.add(new Craft(ItemDatabase.find("크랩살"), 1));
		l.add(new Craft(ItemDatabase.find("버섯포자의 즙"), 1));
		l.add(new Craft(ItemDatabase.find("모듬 양념 소스"), 1));
		craft_list.put(ItemDatabase.find("크랩살 스프"), l);
		craft_list.put(ItemDatabase.find("환상의 크랩살 스프"), l);
		request_list.put("request cook 15", ItemDatabase.find("크랩살 스프"));
		request_list.put("request cook 1511", ItemDatabase.find("환상의 크랩살 스프"));
		// 크러스트 시안 집게발 구이
		l = new ArrayList<Craft>();
		l.add(new Craft(ItemDatabase.find("크러스트 시안 집게발"), 1));
		l.add(new Craft(ItemDatabase.find("모듬 양념 소스"), 1));
		l.add(new Craft(ItemDatabase.find("허브"), 1));
		craft_list.put(ItemDatabase.find("크러스트 시안 집게발 구이"), l);
		craft_list.put(ItemDatabase.find("환상의 크러스트 시안 집게발 구이"), l);
		request_list.put("request cook 16", ItemDatabase.find("크러스트 시안 집게발 구이"));
		request_list.put("request cook 1611", ItemDatabase.find("환상의 크러스트 시안 집게발 구이"));
		// 그리폰 구이
		l = new ArrayList<Craft>();
		l.add(new Craft(ItemDatabase.find("그리폰 고기"), 1));
		l.add(new Craft(ItemDatabase.find("모듬 양념 소스"), 1));
		l.add(new Craft(ItemDatabase.find("허브"), 1));
		craft_list.put(ItemDatabase.find("그리폰 구이"), l);
		craft_list.put(ItemDatabase.find("환상의 그리폰 구이"), l);
		request_list.put("request cook 17", ItemDatabase.find("그리폰 구이"));
		request_list.put("request cook 1711", ItemDatabase.find("환상의 그리폰 구이"));
		// 코카트리스 스테이크
		l = new ArrayList<Craft>();
		l.add(new Craft(ItemDatabase.find("코카트리스 꼬리"), 1));
		l.add(new Craft(ItemDatabase.find("모듬 양념 소스"), 1));
		l.add(new Craft(ItemDatabase.find("허브"), 1));
		craft_list.put(ItemDatabase.find("코카트리스 스테이크"), l);
		craft_list.put(ItemDatabase.find("환상의 코카트리스 스테이크"), l);
		request_list.put("request cook 18", ItemDatabase.find("코카트리스 스테이크"));
		request_list.put("request cook 1811", ItemDatabase.find("환상의 코카트리스 스테이크"));
		// 대왕거북 구이
		l = new ArrayList<Craft>();
		l.add(new Craft(ItemDatabase.find("대왕거북 살"), 1));
		l.add(new Craft(ItemDatabase.find("모듬 양념 소스"), 1));
		l.add(new Craft(ItemDatabase.find("허브"), 1));
		craft_list.put(ItemDatabase.find("대왕거북 구이"), l);
		craft_list.put(ItemDatabase.find("환상의 대왕거북 구이"), l);
		request_list.put("request cook 19", ItemDatabase.find("대왕거북 구이"));
		request_list.put("request cook 1911", ItemDatabase.find("환상의 대왕거북 구이"));
		// 레서 드래곤 날개 꼬치
		l = new ArrayList<Craft>();
		l.add(new Craft(ItemDatabase.find("레서 드래곤 날개"), 1));
		l.add(new Craft(ItemDatabase.find("모듬 양념 소스"), 1));
		l.add(new Craft(ItemDatabase.find("허브"), 1));
		craft_list.put(ItemDatabase.find("레서 드래곤 날개 꼬치"), l);
		craft_list.put(ItemDatabase.find("환상의 레서 드래곤 날개 꼬치"), l);
		request_list.put("request cook 20", ItemDatabase.find("레서 드래곤 날개 꼬치"));
		request_list.put("request cook 2011", ItemDatabase.find("환상의 레서 드래곤 날개 꼬치"));
		// 드레이크 구이
		l = new ArrayList<Craft>();
		l.add(new Craft(ItemDatabase.find("드레이크 고기"), 1));
		l.add(new Craft(ItemDatabase.find("모듬 양념 소스"), 1));
		l.add(new Craft(ItemDatabase.find("허브"), 1));
		craft_list.put(ItemDatabase.find("드레이크 구이"), l);
		craft_list.put(ItemDatabase.find("환상의 드레이크 구이"), l);
		request_list.put("request cook 21", ItemDatabase.find("드레이크 구이"));
		request_list.put("request cook 2111", ItemDatabase.find("환상의 드레이크 구이"));
		// 심해어 스튜
		l = new ArrayList<Craft>();
		l.add(new Craft(ItemDatabase.find("심해어 살"), 1));
		l.add(new Craft(ItemDatabase.find("모듬 양념 소스"), 1));
		l.add(new Craft(ItemDatabase.find("허브"), 1));
		craft_list.put(ItemDatabase.find("심해어 스튜"), l);
		craft_list.put(ItemDatabase.find("환상의 심해어 스튜"), l);
		request_list.put("request cook 22", ItemDatabase.find("심해어 스튜"));
		request_list.put("request cook 2211", ItemDatabase.find("환상의 심해어 스튜"));
		// 바실리스크 알 스프
		l = new ArrayList<Craft>();
		l.add(new Craft(ItemDatabase.find("바실리스크 알"), 1));
		l.add(new Craft(ItemDatabase.find("버섯포자의 즙"), 1));
		l.add(new Craft(ItemDatabase.find("모듬 양념 소스"), 1));
		l.add(new Craft(ItemDatabase.find("허브"), 1));
		craft_list.put(ItemDatabase.find("바실리스크 알 스프"), l);
		craft_list.put(ItemDatabase.find("환상의 바실리스크 알 스프"), l);
		request_list.put("request cook 23", ItemDatabase.find("바실리스크 알 스프"));
		request_list.put("request cook 2311", ItemDatabase.find("환상의 바실리스크 알 스프"));
	}

	static public void close() {
		//
	}

	/**
	 * 요리 요청시 호출됨.
	 * 
	 * @param cha
	 * @param action
	 */
	static public void toCook(Character cha, String action) {
		// 근처에 장작이 있는지 확인.
		boolean find = false;
		for (object o : cha.getInsideList(true)) {
			if (o instanceof MagicFirewood && Util.isDistance(cha, o, 1))
				find = true;
		}
		if (!find) {
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 1160));
			return;
		}
		// 환상 아이템으로 변경 확률.
		int success = 10;
		ItemInstance item = cha.getInventory().find("요리사의 모자");
		if (item != null && item.isEquipped())
			success *= 5;
		if (Util.random(0, 100) < success)
			action += "11";
		//
		Item i = request_list.get(action);
		if (i == null)
			return;
		// 재료 확인.
		if (!CraftController.isCraft(cha, craft_list.get(i), true))
			return;
		// 재료 제거.
		CraftController.toCraft(cha, craft_list.get(i));
		// 지급.
		// 실패확률 추가
		if (Util.random(0, 100) < 30) {
			ChattingController.toChatting(cha, "요리가 실패하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
			cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, 6394), true);
			return;
		}
		CraftController.toCraft(cha, i, 1, true);
	}

}
