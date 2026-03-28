package lineage.world.controller;

import java.util.ArrayList;
import java.util.List;

import lineage.share.Lineage;
import lineage.world.object.instance.PcInstance;

/**
 * 클라이언트 {@code autohunt.htm} 치환용 문자열 목록. 플레이스홀더는 {@code %0} ~ {@code %33} (파워볼·기타 NPC와 동일 규칙).
 */
public final class AutoHuntHtmParams {

	private AutoHuntHtmParams() {
	}

	private static String timeLine(PcInstance pc) {
		if (!Lineage.is_auto_hunt_time)
			return "[남은 시간: 무제한]";
		long time = Lineage.is_auto_hunt_time_account ? pc.auto_hunt_account_time : pc.auto_hunt_time;
		if (time > 0) {
			if (time / 3600 > 0)
				return String.format("[남은 시간: %d시간 %d분 %d초]", time / 3600, time % 3600 / 60, time % 3600 % 60);
			if (time % 3600 / 60 > 0)
				return String.format("[남은 시간: %d분 %d초]", time % 3600 / 60, time % 3600 % 60);
			return String.format("[남은 시간: %d초]", time % 3600 % 60);
		}
		return "[남은 시간: 없음]";
	}

	private static void padHpList(List<String> list, List<Integer> src) {
		for (int i = 0; i < 7; i++) {
			if (i < src.size())
				list.add(src.get(i) + "%");
			else
				list.add(" ");
		}
	}

	/** 메인 자동사냥 설정 — {@code autohunt.htm} 의 %0 … %33 순서와 일치 */
	public static List<String> buildMainList(PcInstance pc) {
		List<String> list = new ArrayList<>(40);
		list.add(timeLine(pc));
		list.add(pc.isAutoHunt ? "ON" : "OFF");
		list.add(pc.auto_return_home_hp < 1 ? "설정 X" : pc.auto_return_home_hp + "%");
		padHpList(list, Lineage.auto_hunt_home_hp_list);
		list.add(pc.is_auto_buff ? "ON" : "OFF");
		list.add(pc.isAutoPotion ? "ON" : "OFF");
		list.add(pc.is_auto_potion_buy ? "ON" : "OFF");
		list.add(pc.autoPotionPercent < 1 ? "설정 X" : pc.autoPotionPercent + "% 이하");
		padHpList(list, Lineage.auto_hunt_potion_hp_list);
		list.add(pc.autoPotionName == null || pc.autoPotionName.length() < 1 ? "설정 X" : pc.autoPotionName);
		list.add(pc.is_auto_poly_select ? "랭변" : "일반");
		list.add(pc.is_auto_rank_poly ? "ON" : "OFF");
		list.add(pc.is_auto_rank_poly_buy ? "ON" : "OFF");
		list.add(pc.is_auto_poly ? "ON" : "OFF");
		list.add(pc.is_auto_poly_buy ? "ON" : "OFF");
		String qp = pc.getQuickPolymorph();
		list.add(qp == null || qp.length() < 1 ? "설정 X" : qp);
		list.add(pc.is_auto_teleport ? "ON" : "OFF");
		list.add(pc.is_auto_bravery ? "ON" : "OFF");
		list.add(pc.is_auto_bravery_buy ? "ON" : "OFF");
		list.add(pc.is_auto_haste ? "ON" : "OFF");
		list.add(pc.is_auto_haste_buy ? "ON" : "OFF");
		list.add(pc.is_auto_arrow_buy ? "ON" : "OFF");
		return list;
	}
}
