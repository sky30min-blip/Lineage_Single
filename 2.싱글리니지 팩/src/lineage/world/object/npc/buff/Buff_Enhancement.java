package lineage.world.object.npc.buff;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lineage.bean.database.Skill;
import lineage.database.ItemDatabase;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.CraftController;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class Buff_Enhancement extends object {

	// 사용자들이 마법주문서 제작에 갯수를 요청할때 그 값을 저장해놓고 활용하기 위해.
	private Map<Long, Integer> memory;

	public Buff_Enhancement() {
		memory = new ConcurrentHashMap<Long, Integer>();
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "bs_01"));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		//
		int skill_lv = 0;
		int skill_num = 0;
		int aden = 10000;
		//
		switch (action) {
		case "a":
			// 강화 마법을 받는다
			if (pc.getInventory().isAden(aden, true)) {
				//어벤
				//lineage.world.object.magic.AdvanceSpirit.onBuff(pc, SkillDatabase.find(9, 2));
				//어스스킨
				lineage.world.object.magic.EarthSkin.onBuff(pc, SkillDatabase.find(600, 0));

				ItemInstance weapon = pc.getInventory().getSlot(Lineage.SLOT_WEAPON);
				if (weapon != null) {
					if (weapon.getItem().getType2().equalsIgnoreCase("bow")
							|| weapon.getItem().getType2().equalsIgnoreCase("singlebow")) {
						//스톰샷
						lineage.world.object.magic.StormShot.onBuff(pc, SkillDatabase.find(21, 5));
					} else {
						//버닝웨폰
						lineage.world.object.magic.BurningWeapon.onBuff(pc, SkillDatabase.find(20, 1));
					}
				}
			} else {
				// \f1아데나가 충분치 않습니다.
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "bs_adena"));
			}
			break;
		case "b": //
			break;
		case "z":
			// 흑사의 기운을 받는다
			break;
		case "1":
		case "2":
		case "3":
		case "4":
		case "5":
			// 1~5
			// : 마법 주문서 제작
			// : 1개, 5개, 10개, 100개, 500개
			int i = 0;
			if (action.equalsIgnoreCase("1"))
				i = 1;
			else if (action.equalsIgnoreCase("2"))
				i = 5;
			else if (action.equalsIgnoreCase("3"))
				i = 10;
			else if (action.equalsIgnoreCase("4"))
				i = 100;
			else if (action.equalsIgnoreCase("5"))
				i = 500;
			List<String> htmlData = new ArrayList<String>();
			htmlData.add(String.valueOf(50 * i));
			htmlData.add(String.valueOf(100 * i));
			htmlData.add(String.valueOf(100 * i));
			htmlData.add(String.valueOf(200 * i));
			htmlData.add(String.valueOf(200 * i));
			htmlData.add(String.valueOf(i));
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "bs_m4", null, htmlData));
			memory.put(pc.getObjectId(), i);
			break;

		case "A": // 힐 (1단계)
			if (skill_lv == 0) {
				skill_lv = 1;
				skill_num = 0;
			}
		case "B": // 쉴드 (1단계)
			if (skill_lv == 0) {
				skill_lv = 1;
				skill_num = 2;
			}
		case "C": // 에너지볼트 (1단계)
			if (skill_lv == 0) {
				skill_lv = 1;
				skill_num = 3;
			}
		case "D": // 홀리웨폰 (1단계)
			if (skill_lv == 0) {
				skill_lv = 1;
				skill_num = 7;
			}
		case "E": // 디크리즈웨이트 (2단계)
			if (skill_lv == 0) {
				skill_lv = 2;
				skill_num = 5;
			}
		case "F": // 디텍션 (2단계)
			if (skill_lv == 0) {
				skill_lv = 2;
				skill_num = 4;
			}
		case "G": // 인첸트웨폰 (2단계)
			if (skill_lv == 0) {
				skill_lv = 2;
				skill_num = 3;
			}
		case "H": // 큐어포이즌 (2단계)
			if (skill_lv == 0) {
				skill_lv = 2;
				skill_num = 0;
			}
		case "I": // 라이트닝 (3단계)
			if (skill_lv == 0) {
				skill_lv = 3;
				skill_num = 0;
			}
		case "J": // 블레스트아머 (3단계)
			if (skill_lv == 0) {
				skill_lv = 3;
				skill_num = 4;
			}
		case "K": // 익스트라힐 (3단계)
			if (skill_lv == 0) {
				skill_lv = 3;
				skill_num = 2;
			}
		case "L": // 턴언데드 (3단계)
			if (skill_lv == 0) {
				skill_lv = 3;
				skill_num = 1;
			}
		case "M": // 메디테이션 (4단계)
			if (skill_lv == 0) {
				skill_lv = 4;
				skill_num = 7;
			}
		case "N": // 파이어볼 (4단계)
			if (skill_lv == 0) {
				skill_lv = 4;
				skill_num = 0;
			}
		case "O": // 인챈트 덱스터리티 (4단계)
			if (skill_lv == 0) {
				skill_lv = 4;
				skill_num = 1;
			}
		case "P": // 카운터매직 (4단계)
			if (skill_lv == 0) {
				skill_lv = 4;
				skill_num = 6;
			}
		case "Q": // 그레이터힐 (5단계)
			if (skill_lv == 0) {
				skill_lv = 5;
				skill_num = 2;
			}
		case "R": // 리무브커스 (5단계)
			if (skill_lv == 0) {
				skill_lv = 5;
				skill_num = 4;
			}
		case "S": // 마나드레인 (5단계)
			if (skill_lv == 0) {
				skill_lv = 5;
				skill_num = 6;
			}
		case "T": // 콘오브콜드 (5단계)
			if (skill_lv == 0) {
				skill_lv = 5;
				skill_num = 5;
			}
			
			// 요청했던 갯수 춫출.
			Integer count = memory.get(pc.getObjectId());
			if (count == null || count <= 0)
				count = 1;
			// 스킬존재 하는지 확인.
			Skill s = SkillDatabase.find(skill_lv, skill_num);
			if (s != null) {
				// 빈주문서 존재하는지 확인.
				ItemInstance none_scroll = null;
				switch (skill_lv) {
				case 1:
					none_scroll = pc.getInventory().find(ItemDatabase.find(1486));
				case 2:
					if (none_scroll == null)
						none_scroll = pc.getInventory().find(ItemDatabase.find(1892));
				case 3:
					if (none_scroll == null)
						none_scroll = pc.getInventory().find(ItemDatabase.find(1893));
				case 4:
					if (none_scroll == null)
						none_scroll = pc.getInventory().find(ItemDatabase.find(1894));
				case 5:
					if (none_scroll == null)
						none_scroll = pc.getInventory().find(ItemDatabase.find(1895));
					break;
				}
				if (none_scroll == null || none_scroll.getCount() < count) {
					ChattingController.toChatting(pc, String.format("빈 주문서 (레벨 %d)가 부족합니다.", skill_lv),
							Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
				// 스킬레벨에 따라 가격이 다름.
				aden = 50;
				if (skill_lv == 2 || skill_lv == 3)
					aden = 100;
				else if (skill_lv == 4 || skill_lv == 5)
					aden = 200;

				// 마돌이 들어가는 주문서라면
				if (마돌재료(skill_lv, skill_num)) {
					ItemInstance item = pc.getInventory().find("마력의 돌");
					if (item == null || item.getCount() < count) {
						ChattingController.toChatting(pc, "마력의 돌이 부족합니다", Lineage.CHATTING_MODE_MESSAGE);
						return;
					}
					// 아덴이 존재한다면.
					if (pc.getInventory().isAden(count * aden, true)) {
						pc.getInventory().count(none_scroll, none_scroll.getCount() - count, true);
						// 주문서 지급.
						pc.getInventory().count(item, item.getCount() - count, true);
						CraftController.toCraft(this, pc, ItemDatabase.find("item", String.format("spellscroll_%d", s.getUid())), count, true);
					} else {
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "bs_adena"));
					}
				} else {

					// 아덴이 존재한다면.
					if (pc.getInventory().isAden(count * aden, true)) {
						pc.getInventory().count(none_scroll, none_scroll.getCount() - count, true);
						// 주문서 지급.
						CraftController.toCraft(this, pc, ItemDatabase.find("item", String.format("spellscroll_%d", s.getUid())), count, true);
					} else {
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "bs_adena"));
					}
				}
			}
			break;
		default:
			ChattingController.toChatting(pc, "강화 마법사 : " + action, Lineage.CHATTING_MODE_MESSAGE);
			break;
		}
	}

	private boolean 마돌재료(int skill_lv, int skill_num) {
		if (skill_lv == 4 && skill_num == 4)
			return true;
		if (skill_lv == 4 && skill_num == 6)
			return true;

		return false;
	}

}
